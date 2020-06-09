/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.ssl.ApplicationProtocolAccessor;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

public class SslHandler
extends ByteToMessageDecoder
implements ChannelOutboundHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
    private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile((String)"^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
    private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile((String)"^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", (int)2);
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private volatile ChannelHandlerContext ctx;
    private final SSLEngine engine;
    private final SslEngineType engineType;
    private final Executor delegatedTaskExecutor;
    private final boolean jdkCompatibilityMode;
    private final ByteBuffer[] singleBuffer = new ByteBuffer[1];
    private final boolean startTls;
    private boolean sentFirstMessage;
    private boolean flushedBeforeHandshake;
    private boolean readDuringHandshake;
    private boolean handshakeStarted;
    private SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
    private Promise<Channel> handshakePromise = new LazyChannelPromise((SslHandler)this, null);
    private final LazyChannelPromise sslClosePromise = new LazyChannelPromise((SslHandler)this, null);
    private boolean needsFlush;
    private boolean outboundClosed;
    private boolean closeNotify;
    private boolean processTask;
    private int packetLength;
    private boolean firedChannelRead;
    private volatile long handshakeTimeoutMillis = 10000L;
    private volatile long closeNotifyFlushTimeoutMillis = 3000L;
    private volatile long closeNotifyReadTimeoutMillis;
    volatile int wrapDataSize = 16384;

    public SslHandler(SSLEngine engine) {
        this((SSLEngine)engine, (boolean)false);
    }

    public SslHandler(SSLEngine engine, boolean startTls) {
        this((SSLEngine)engine, (boolean)startTls, (Executor)ImmediateExecutor.INSTANCE);
    }

    public SslHandler(SSLEngine engine, Executor delegatedTaskExecutor) {
        this((SSLEngine)engine, (boolean)false, (Executor)delegatedTaskExecutor);
    }

    public SslHandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor) {
        if (engine == null) {
            throw new NullPointerException((String)"engine");
        }
        if (delegatedTaskExecutor == null) {
            throw new NullPointerException((String)"delegatedTaskExecutor");
        }
        this.engine = engine;
        this.engineType = SslEngineType.forEngine((SSLEngine)engine);
        this.delegatedTaskExecutor = delegatedTaskExecutor;
        this.startTls = startTls;
        this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode((SSLEngine)engine);
        this.setCumulator((ByteToMessageDecoder.Cumulator)this.engineType.cumulator);
    }

    public long getHandshakeTimeoutMillis() {
        return this.handshakeTimeoutMillis;
    }

    public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        this.setHandshakeTimeoutMillis((long)unit.toMillis((long)handshakeTimeout));
    }

    public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis) {
        if (handshakeTimeoutMillis < 0L) {
            throw new IllegalArgumentException((String)("handshakeTimeoutMillis: " + handshakeTimeoutMillis + " (expected: >= 0)"));
        }
        this.handshakeTimeoutMillis = handshakeTimeoutMillis;
    }

    public final void setWrapDataSize(int wrapDataSize) {
        this.wrapDataSize = wrapDataSize;
    }

    @Deprecated
    public long getCloseNotifyTimeoutMillis() {
        return this.getCloseNotifyFlushTimeoutMillis();
    }

    @Deprecated
    public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) {
        this.setCloseNotifyFlushTimeout((long)closeNotifyTimeout, (TimeUnit)unit);
    }

    @Deprecated
    public void setCloseNotifyTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
        this.setCloseNotifyFlushTimeoutMillis((long)closeNotifyFlushTimeoutMillis);
    }

    public final long getCloseNotifyFlushTimeoutMillis() {
        return this.closeNotifyFlushTimeoutMillis;
    }

    public final void setCloseNotifyFlushTimeout(long closeNotifyFlushTimeout, TimeUnit unit) {
        this.setCloseNotifyFlushTimeoutMillis((long)unit.toMillis((long)closeNotifyFlushTimeout));
    }

    public final void setCloseNotifyFlushTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
        if (closeNotifyFlushTimeoutMillis < 0L) {
            throw new IllegalArgumentException((String)("closeNotifyFlushTimeoutMillis: " + closeNotifyFlushTimeoutMillis + " (expected: >= 0)"));
        }
        this.closeNotifyFlushTimeoutMillis = closeNotifyFlushTimeoutMillis;
    }

    public final long getCloseNotifyReadTimeoutMillis() {
        return this.closeNotifyReadTimeoutMillis;
    }

    public final void setCloseNotifyReadTimeout(long closeNotifyReadTimeout, TimeUnit unit) {
        this.setCloseNotifyReadTimeoutMillis((long)unit.toMillis((long)closeNotifyReadTimeout));
    }

    public final void setCloseNotifyReadTimeoutMillis(long closeNotifyReadTimeoutMillis) {
        if (closeNotifyReadTimeoutMillis < 0L) {
            throw new IllegalArgumentException((String)("closeNotifyReadTimeoutMillis: " + closeNotifyReadTimeoutMillis + " (expected: >= 0)"));
        }
        this.closeNotifyReadTimeoutMillis = closeNotifyReadTimeoutMillis;
    }

    public SSLEngine engine() {
        return this.engine;
    }

    public String applicationProtocol() {
        SSLEngine engine = this.engine();
        if (engine instanceof ApplicationProtocolAccessor) return ((ApplicationProtocolAccessor)((Object)engine)).getNegotiatedApplicationProtocol();
        return null;
    }

    public Future<Channel> handshakeFuture() {
        return this.handshakePromise;
    }

    @Deprecated
    public ChannelFuture close() {
        return this.closeOutbound();
    }

    @Deprecated
    public ChannelFuture close(ChannelPromise promise) {
        return this.closeOutbound((ChannelPromise)promise);
    }

    public ChannelFuture closeOutbound() {
        return this.closeOutbound((ChannelPromise)this.ctx.newPromise());
    }

    public ChannelFuture closeOutbound(ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx.executor().inEventLoop()) {
            this.closeOutbound0((ChannelPromise)promise);
            return promise;
        }
        ctx.executor().execute((Runnable)new Runnable((SslHandler)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ SslHandler this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                SslHandler.access$500((SslHandler)this.this$0, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    private void closeOutbound0(ChannelPromise promise) {
        this.outboundClosed = true;
        this.engine.closeOutbound();
        try {
            this.flush((ChannelHandlerContext)this.ctx, (ChannelPromise)promise);
            return;
        }
        catch (Exception e) {
            if (promise.tryFailure((Throwable)e)) return;
            logger.warn((String)"{} flush() raised a masked exception.", (Object)this.ctx.channel(), (Object)e);
        }
    }

    public Future<Channel> sslCloseFuture() {
        return this.sslClosePromise;
    }

    @Override
    public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        if (!this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, (Throwable)new ChannelException((String)"Pending write on removal of SslHandler"));
        }
        this.pendingUnencryptedWrites = null;
        if (!(this.engine instanceof ReferenceCounted)) return;
        ((ReferenceCounted)((Object)this.engine)).release();
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind((SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister((ChannelPromise)promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel((ChannelHandlerContext)ctx, (ChannelPromise)promise, (boolean)true);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel((ChannelHandlerContext)ctx, (ChannelPromise)promise, (boolean)false);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (!this.handshakePromise.isDone()) {
            this.readDuringHandshake = true;
        }
        ctx.read();
    }

    private static IllegalStateException newPendingWritesNullException() {
        return new IllegalStateException((String)"pendingUnencryptedWrites is null, handlerRemoved0 called?");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException((Object)msg, ByteBuf.class);
            ReferenceCountUtil.safeRelease((Object)msg);
            promise.setFailure((Throwable)exception);
            return;
        }
        if (this.pendingUnencryptedWrites == null) {
            ReferenceCountUtil.safeRelease((Object)msg);
            promise.setFailure((Throwable)SslHandler.newPendingWritesNullException());
            return;
        }
        this.pendingUnencryptedWrites.add((ByteBuf)((ByteBuf)msg), (ChannelPromise)promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.startTls && !this.sentFirstMessage) {
            this.sentFirstMessage = true;
            this.pendingUnencryptedWrites.writeAndRemoveAll((ChannelHandlerContext)ctx);
            this.forceFlush((ChannelHandlerContext)ctx);
            this.startHandshakeProcessing();
            return;
        }
        if (this.processTask) {
            return;
        }
        try {
            this.wrapAndFlush((ChannelHandlerContext)ctx);
            return;
        }
        catch (Throwable cause) {
            this.setHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)cause);
            PlatformDependent.throwException((Throwable)cause);
        }
    }

    private void wrapAndFlush(ChannelHandlerContext ctx) throws SSLException {
        if (this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.add((ByteBuf)Unpooled.EMPTY_BUFFER, (ChannelPromise)ctx.newPromise());
        }
        if (!this.handshakePromise.isDone()) {
            this.flushedBeforeHandshake = true;
        }
        try {
            this.wrap((ChannelHandlerContext)ctx, (boolean)false);
            return;
        }
        finally {
            this.forceFlush((ChannelHandlerContext)ctx);
        }
    }

    /*
     * Exception decompiling
     */
    private void wrap(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 8[CASE]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    private void finishWrap(ChannelHandlerContext ctx, ByteBuf out, ChannelPromise promise, boolean inUnwrap, boolean needUnwrap) {
        if (out == null) {
            out = Unpooled.EMPTY_BUFFER;
        } else if (!out.isReadable()) {
            out.release();
            out = Unpooled.EMPTY_BUFFER;
        }
        if (promise != null) {
            ctx.write((Object)out, (ChannelPromise)promise);
        } else {
            ctx.write((Object)out);
        }
        if (inUnwrap) {
            this.needsFlush = true;
        }
        if (!needUnwrap) return;
        this.readIfNeeded((ChannelHandlerContext)ctx);
    }

    /*
     * Exception decompiling
     */
    private boolean wrapNonAppData(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Invalid source, tried to remove [0] lbl64 : GotoStatement: goto lbl4;\u000a\u000afrom [] lbl3 : TryStatement: try { 0[TRYBLOCK]\u000a\u000abut was not a source.
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.removeSource(Op03SimpleStatement.java:313)
        // org.benf.cfr.reader.bytecode.analysis.parse.utils.finalhelp.FinalAnalyzer$2.call(FinalAnalyzer.java:259)
        // org.benf.cfr.reader.bytecode.analysis.parse.utils.finalhelp.FinalAnalyzer$2.call(FinalAnalyzer.java:247)
        // org.benf.cfr.reader.util.graph.GraphVisitorDFS.process(GraphVisitorDFS.java:68)
        // org.benf.cfr.reader.bytecode.analysis.parse.utils.finalhelp.FinalAnalyzer.identifyFinally(FinalAnalyzer.java:267)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.FinallyRewriter.identifyFinally(FinallyRewriter.java:40)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:414)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    private SSLEngineResult wrap(ByteBufAllocator alloc, SSLEngine engine, ByteBuf in, ByteBuf out) throws SSLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:478)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.rebuildSwitches(SwitchReplacer.java:328)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:466)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ClosedChannelException exception = new ClosedChannelException();
        this.setHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)exception, (boolean)(!this.outboundClosed), (boolean)this.handshakeStarted, (boolean)false);
        this.notifyClosePromise((Throwable)exception);
        super.channelInactive((ChannelHandlerContext)ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!this.ignoreException((Throwable)cause)) {
            ctx.fireExceptionCaught((Throwable)cause);
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug((String)"{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", (Object)ctx.channel(), (Object)cause);
        }
        if (!ctx.channel().isActive()) return;
        ctx.close();
    }

    private boolean ignoreException(Throwable t) {
        StackTraceElement[] elements;
        if (t instanceof SSLException) return false;
        if (!(t instanceof IOException)) return false;
        if (!this.sslClosePromise.isDone()) return false;
        String message = t.getMessage();
        if (message != null && IGNORABLE_ERROR_MESSAGE.matcher((CharSequence)message).matches()) {
            return true;
        }
        StackTraceElement[] arrstackTraceElement = elements = t.getStackTrace();
        int n = arrstackTraceElement.length;
        int n2 = 0;
        while (n2 < n) {
            StackTraceElement element = arrstackTraceElement[n2];
            String classname = element.getClassName();
            String methodname = element.getMethodName();
            if (!classname.startsWith((String)"io.netty.") && "read".equals((Object)methodname)) {
                if (IGNORABLE_CLASS_IN_STACK.matcher((CharSequence)classname).matches()) {
                    return true;
                }
                try {
                    Class<?> clazz = PlatformDependent.getClassLoader(this.getClass()).loadClass((String)classname);
                    if (SocketChannel.class.isAssignableFrom(clazz)) return true;
                    if (DatagramChannel.class.isAssignableFrom(clazz)) {
                        return true;
                    }
                    if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals((Object)clazz.getSuperclass().getName())) {
                        return true;
                    }
                }
                catch (Throwable cause) {
                    logger.debug((String)"Unexpected exception while loading class {} classname {}", (Object[])new Object[]{this.getClass(), classname, cause});
                }
            }
            ++n2;
        }
        return false;
    }

    public static boolean isEncrypted(ByteBuf buffer) {
        if (buffer.readableBytes() < 5) {
            throw new IllegalArgumentException((String)"buffer must have at least 5 readable bytes");
        }
        if (SslUtils.getEncryptedPacketLength((ByteBuf)buffer, (int)buffer.readerIndex()) == -2) return false;
        return true;
    }

    private void decodeJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) throws NotSslRecordException {
        int packetLength = this.packetLength;
        if (packetLength > 0) {
            if (in.readableBytes() < packetLength) {
                return;
            }
        } else {
            int readableBytes = in.readableBytes();
            if (readableBytes < 5) {
                return;
            }
            packetLength = SslUtils.getEncryptedPacketLength((ByteBuf)in, (int)in.readerIndex());
            if (packetLength == -2) {
                NotSslRecordException e = new NotSslRecordException((String)("not an SSL/TLS record: " + ByteBufUtil.hexDump((ByteBuf)in)));
                in.skipBytes((int)in.readableBytes());
                this.setHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)e);
                throw e;
            }
            assert (packetLength > 0);
            if (packetLength > readableBytes) {
                this.packetLength = packetLength;
                return;
            }
        }
        this.packetLength = 0;
        try {
            int bytesConsumed = this.unwrap((ChannelHandlerContext)ctx, (ByteBuf)in, (int)in.readerIndex(), (int)packetLength);
            assert (bytesConsumed == packetLength || this.engine.isInboundDone()) : "we feed the SSLEngine a packets worth of data: " + packetLength + " but it only consumed: " + bytesConsumed;
            in.skipBytes((int)bytesConsumed);
            return;
        }
        catch (Throwable cause) {
            this.handleUnwrapThrowable((ChannelHandlerContext)ctx, (Throwable)cause);
        }
    }

    private void decodeNonJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) {
        try {
            in.skipBytes((int)this.unwrap((ChannelHandlerContext)ctx, (ByteBuf)in, (int)in.readerIndex(), (int)in.readableBytes()));
            return;
        }
        catch (Throwable cause) {
            this.handleUnwrapThrowable((ChannelHandlerContext)ctx, (Throwable)cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleUnwrapThrowable(ChannelHandlerContext ctx, Throwable cause) {
        try {
            if (this.handshakePromise.tryFailure((Throwable)cause)) {
                ctx.fireUserEventTriggered((Object)new SslHandshakeCompletionEvent((Throwable)cause));
            }
            this.wrapAndFlush((ChannelHandlerContext)ctx);
        }
        catch (SSLException ex) {
            logger.debug((String)"SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", (Throwable)ex);
        }
        finally {
            this.setHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)cause, (boolean)true, (boolean)false, (boolean)true);
        }
        PlatformDependent.throwException((Throwable)cause);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws SSLException {
        if (this.processTask) {
            return;
        }
        if (this.jdkCompatibilityMode) {
            this.decodeJdkCompatible((ChannelHandlerContext)ctx, (ByteBuf)in);
            return;
        }
        this.decodeNonJdkCompatible((ChannelHandlerContext)ctx, (ByteBuf)in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.channelReadComplete0((ChannelHandlerContext)ctx);
    }

    private void channelReadComplete0(ChannelHandlerContext ctx) {
        this.discardSomeReadBytes();
        this.flushIfNeeded((ChannelHandlerContext)ctx);
        this.readIfNeeded((ChannelHandlerContext)ctx);
        this.firedChannelRead = false;
        ctx.fireChannelReadComplete();
    }

    private void readIfNeeded(ChannelHandlerContext ctx) {
        if (ctx.channel().config().isAutoRead()) return;
        if (this.firedChannelRead) {
            if (this.handshakePromise.isDone()) return;
        }
        ctx.read();
    }

    private void flushIfNeeded(ChannelHandlerContext ctx) {
        if (!this.needsFlush) return;
        this.forceFlush((ChannelHandlerContext)ctx);
    }

    private void unwrapNonAppData(ChannelHandlerContext ctx) throws SSLException {
        this.unwrap((ChannelHandlerContext)ctx, (ByteBuf)Unpooled.EMPTY_BUFFER, (int)0, (int)0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private int unwrap(ChannelHandlerContext ctx, ByteBuf packet, int offset, int length) throws SSLException {
        originalLength = length;
        wrapLater = false;
        notifyClosure = false;
        overflowReadableBytes = -1;
        decodeOut = this.allocate((ChannelHandlerContext)ctx, (int)length);
        try {
            block14 : while (!ctx.isRemoved()) {
                result = this.engineType.unwrap((SslHandler)this, (ByteBuf)packet, (int)offset, (int)length, (ByteBuf)decodeOut);
                status = result.getStatus();
                handshakeStatus = result.getHandshakeStatus();
                produced = result.bytesProduced();
                consumed = result.bytesConsumed();
                offset += consumed;
                length -= consumed;
                switch (9.$SwitchMap$javax$net$ssl$SSLEngineResult$Status[status.ordinal()]) {
                    case 1: {
                        readableBytes = decodeOut.readableBytes();
                        previousOverflowReadableBytes = overflowReadableBytes;
                        overflowReadableBytes = readableBytes;
                        bufferSize = this.engine.getSession().getApplicationBufferSize() - readableBytes;
                        if (readableBytes > 0) {
                            this.firedChannelRead = true;
                            ctx.fireChannelRead((Object)decodeOut);
                            decodeOut = null;
                            if (bufferSize <= 0) {
                                bufferSize = this.engine.getSession().getApplicationBufferSize();
                            }
                        } else {
                            decodeOut.release();
                            decodeOut = null;
                        }
                        if (readableBytes == 0 && previousOverflowReadableBytes == 0) {
                            throw new IllegalStateException((String)("Two consecutive overflows but no content was consumed. " + SSLSession.class.getSimpleName() + " getApplicationBufferSize: " + this.engine.getSession().getApplicationBufferSize() + " maybe too small."));
                        }
                        decodeOut = this.allocate((ChannelHandlerContext)ctx, (int)this.engineType.calculatePendingData((SslHandler)this, (int)bufferSize));
                        continue block14;
                    }
                    case 2: {
                        notifyClosure = true;
                        overflowReadableBytes = -1;
                        ** break;
                    }
                }
                overflowReadableBytes = -1;
lbl41: // 2 sources:
                switch (9.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[handshakeStatus.ordinal()]) {
                    case 5: {
                        break;
                    }
                    case 4: {
                        if (!this.wrapNonAppData((ChannelHandlerContext)ctx, (boolean)true) || length != 0) break;
                        break block14;
                    }
                    case 1: {
                        if (this.runDelegatedTasks((boolean)true)) break;
                        wrapLater = false;
                        break block14;
                    }
                    case 2: {
                        this.setHandshakeSuccess();
                        wrapLater = true;
                        break;
                    }
                    case 3: {
                        if (this.setHandshakeSuccessIfStillHandshaking()) {
                            wrapLater = true;
                            continue block14;
                        }
                        if (length != 0) break;
                        break block14;
                    }
                    default: {
                        throw new IllegalStateException((String)("unknown handshake status: " + (Object)handshakeStatus));
                    }
                }
                if (status != SSLEngineResult.Status.BUFFER_UNDERFLOW && (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK || consumed != 0 || produced != 0)) continue;
                if (handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP) break;
                this.readIfNeeded((ChannelHandlerContext)ctx);
                break;
            }
            if (this.flushedBeforeHandshake && this.handshakePromise.isDone()) {
                this.flushedBeforeHandshake = false;
                wrapLater = true;
            }
            if (wrapLater) {
                this.wrap((ChannelHandlerContext)ctx, (boolean)true);
            }
            if (notifyClosure == false) return originalLength - length;
            this.notifyClosePromise(null);
            return originalLength - length;
        }
        finally {
            if (decodeOut != null) {
                if (decodeOut.isReadable()) {
                    this.firedChannelRead = true;
                    ctx.fireChannelRead((Object)decodeOut);
                } else {
                    decodeOut.release();
                }
            }
        }
    }

    private static ByteBuffer toByteBuffer(ByteBuf out, int index, int len) {
        ByteBuffer byteBuffer;
        if (out.nioBufferCount() == 1) {
            byteBuffer = out.internalNioBuffer((int)index, (int)len);
            return byteBuffer;
        }
        byteBuffer = out.nioBuffer((int)index, (int)len);
        return byteBuffer;
    }

    private static boolean inEventLoop(Executor executor) {
        if (!(executor instanceof EventExecutor)) return false;
        if (!((EventExecutor)executor).inEventLoop()) return false;
        return true;
    }

    private static void runAllDelegatedTasks(SSLEngine engine) {
        Runnable task;
        while ((task = engine.getDelegatedTask()) != null) {
            task.run();
        }
        return;
    }

    private boolean runDelegatedTasks(boolean inUnwrap) {
        if (this.delegatedTaskExecutor != ImmediateExecutor.INSTANCE && !SslHandler.inEventLoop((Executor)this.delegatedTaskExecutor)) {
            this.executeDelegatedTasks((boolean)inUnwrap);
            return false;
        }
        SslHandler.runAllDelegatedTasks((SSLEngine)this.engine);
        return true;
    }

    private void executeDelegatedTasks(boolean inUnwrap) {
        this.processTask = true;
        try {
            this.delegatedTaskExecutor.execute((Runnable)new SslTasksRunner((SslHandler)this, (boolean)inUnwrap));
            return;
        }
        catch (RejectedExecutionException e) {
            this.processTask = false;
            throw e;
        }
    }

    private boolean setHandshakeSuccessIfStillHandshaking() {
        if (this.handshakePromise.isDone()) return false;
        this.setHandshakeSuccess();
        return true;
    }

    private void setHandshakeSuccess() {
        this.handshakePromise.trySuccess((Channel)this.ctx.channel());
        if (logger.isDebugEnabled()) {
            logger.debug((String)"{} HANDSHAKEN: {}", (Object)this.ctx.channel(), (Object)this.engine.getSession().getCipherSuite());
        }
        this.ctx.fireUserEventTriggered((Object)SslHandshakeCompletionEvent.SUCCESS);
        if (!this.readDuringHandshake) return;
        if (this.ctx.channel().config().isAutoRead()) return;
        this.readDuringHandshake = false;
        this.ctx.read();
    }

    private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause) {
        this.setHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)cause, (boolean)true, (boolean)true, (boolean)false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean closeInbound, boolean notify, boolean alwaysFlushAndClose) {
        try {
            block7 : {
                this.outboundClosed = true;
                this.engine.closeOutbound();
                if (closeInbound) {
                    try {
                        this.engine.closeInbound();
                    }
                    catch (SSLException e) {
                        String msg;
                        if (!logger.isDebugEnabled() || (msg = e.getMessage()) != null && (msg.contains((CharSequence)"possible truncation attack") || msg.contains((CharSequence)"closing inbound before receiving peer's close_notify"))) break block7;
                        logger.debug((String)"{} SSLEngine.closeInbound() raised an exception.", (Object)ctx.channel(), (Object)e);
                    }
                }
            }
            if (!this.handshakePromise.tryFailure((Throwable)cause)) {
                if (!alwaysFlushAndClose) return;
            }
            SslUtils.handleHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)cause, (boolean)notify);
            return;
        }
        finally {
            this.releaseAndFailAll((ChannelHandlerContext)ctx, (Throwable)cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setHandshakeFailureTransportFailure(ChannelHandlerContext ctx, Throwable cause) {
        try {
            SSLException transportFailure = new SSLException((String)"failure when writing TLS control frames", (Throwable)cause);
            this.releaseAndFailAll((ChannelHandlerContext)ctx, (Throwable)transportFailure);
            if (!this.handshakePromise.tryFailure((Throwable)transportFailure)) return;
            ctx.fireUserEventTriggered((Object)new SslHandshakeCompletionEvent((Throwable)transportFailure));
            return;
        }
        finally {
            ctx.close();
        }
    }

    private void releaseAndFailAll(ChannelHandlerContext ctx, Throwable cause) {
        if (this.pendingUnencryptedWrites == null) return;
        this.pendingUnencryptedWrites.releaseAndFailAll((ChannelOutboundInvoker)ctx, (Throwable)cause);
    }

    private void notifyClosePromise(Throwable cause) {
        if (cause == null) {
            if (!this.sslClosePromise.trySuccess(this.ctx.channel())) return;
            this.ctx.fireUserEventTriggered((Object)SslCloseCompletionEvent.SUCCESS);
            return;
        }
        if (!this.sslClosePromise.tryFailure((Throwable)cause)) return;
        this.ctx.fireUserEventTriggered((Object)new SslCloseCompletionEvent((Throwable)cause));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void closeOutboundAndChannel(ChannelHandlerContext ctx, ChannelPromise promise, boolean disconnect) throws Exception {
        block5 : {
            this.outboundClosed = true;
            this.engine.closeOutbound();
            if (!ctx.channel().isActive()) {
                if (disconnect) {
                    ctx.disconnect((ChannelPromise)promise);
                    return;
                }
                ctx.close((ChannelPromise)promise);
                return;
            }
            ChannelPromise closeNotifyPromise = ctx.newPromise();
            try {
                this.flush((ChannelHandlerContext)ctx, (ChannelPromise)closeNotifyPromise);
                if (this.closeNotify) break block5;
                this.closeNotify = true;
            }
            catch (Throwable throwable) {
                if (!this.closeNotify) {
                    this.closeNotify = true;
                    this.safeClose((ChannelHandlerContext)ctx, (ChannelFuture)closeNotifyPromise, (ChannelPromise)ctx.newPromise().addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelPromiseNotifier((boolean)false, (ChannelPromise[])new ChannelPromise[]{promise})));
                    throw throwable;
                }
                this.sslClosePromise.addListener((GenericFutureListener)new FutureListener<Channel>((SslHandler)this, (ChannelPromise)promise){
                    final /* synthetic */ ChannelPromise val$promise;
                    final /* synthetic */ SslHandler this$0;
                    {
                        this.this$0 = this$0;
                        this.val$promise = channelPromise;
                    }

                    public void operationComplete(Future<Channel> future) {
                        this.val$promise.setSuccess();
                    }
                });
                throw throwable;
            }
            this.safeClose((ChannelHandlerContext)ctx, (ChannelFuture)closeNotifyPromise, (ChannelPromise)ctx.newPromise().addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelPromiseNotifier((boolean)false, (ChannelPromise[])new ChannelPromise[]{promise})));
            return;
        }
        this.sslClosePromise.addListener((GenericFutureListener)new /* invalid duplicate definition of identical inner class */);
    }

    private void flush(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.pendingUnencryptedWrites != null) {
            this.pendingUnencryptedWrites.add((ByteBuf)Unpooled.EMPTY_BUFFER, (ChannelPromise)promise);
        } else {
            promise.setFailure((Throwable)SslHandler.newPendingWritesNullException());
        }
        this.flush((ChannelHandlerContext)ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.pendingUnencryptedWrites = new SslHandlerCoalescingBufferQueue((SslHandler)this, (Channel)ctx.channel(), (int)16);
        if (!ctx.channel().isActive()) return;
        this.startHandshakeProcessing();
    }

    private void startHandshakeProcessing() {
        if (this.handshakeStarted) return;
        this.handshakeStarted = true;
        if (this.engine.getUseClientMode()) {
            this.handshake();
        }
        this.applyHandshakeTimeout();
    }

    public Future<Channel> renegotiate() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx != null) return this.renegotiate(ctx.executor().newPromise());
        throw new IllegalStateException();
    }

    public Future<Channel> renegotiate(Promise<Channel> promise) {
        if (promise == null) {
            throw new NullPointerException((String)"promise");
        }
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException();
        }
        EventExecutor executor = ctx.executor();
        if (!executor.inEventLoop()) {
            executor.execute((Runnable)new Runnable((SslHandler)this, promise){
                final /* synthetic */ Promise val$promise;
                final /* synthetic */ SslHandler this$0;
                {
                    this.this$0 = this$0;
                    this.val$promise = promise;
                }

                public void run() {
                    SslHandler.access$2200((SslHandler)this.this$0, (Promise)this.val$promise);
                }
            });
            return promise;
        }
        this.renegotiateOnEventLoop(promise);
        return promise;
    }

    private void renegotiateOnEventLoop(Promise<Channel> newHandshakePromise) {
        Promise<Channel> oldHandshakePromise = this.handshakePromise;
        if (!oldHandshakePromise.isDone()) {
            oldHandshakePromise.addListener(new PromiseNotifier<V, F>(newHandshakePromise));
            return;
        }
        this.handshakePromise = newHandshakePromise;
        this.handshake();
        this.applyHandshakeTimeout();
    }

    private void handshake() {
        if (this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            return;
        }
        if (this.handshakePromise.isDone()) {
            return;
        }
        ChannelHandlerContext ctx = this.ctx;
        try {
            this.engine.beginHandshake();
            this.wrapNonAppData((ChannelHandlerContext)ctx, (boolean)false);
            return;
        }
        catch (Throwable e) {
            this.setHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)e);
            return;
        }
        finally {
            this.forceFlush((ChannelHandlerContext)ctx);
        }
    }

    private void applyHandshakeTimeout() {
        Promise<Channel> localHandshakePromise = this.handshakePromise;
        long handshakeTimeoutMillis = this.handshakeTimeoutMillis;
        if (handshakeTimeoutMillis <= 0L) return;
        if (localHandshakePromise.isDone()) {
            return;
        }
        io.netty.util.concurrent.ScheduledFuture<?> timeoutFuture = this.ctx.executor().schedule((Runnable)new Runnable((SslHandler)this, localHandshakePromise){
            final /* synthetic */ Promise val$localHandshakePromise;
            final /* synthetic */ SslHandler this$0;
            {
                this.this$0 = this$0;
                this.val$localHandshakePromise = promise;
            }

            public void run() {
                if (this.val$localHandshakePromise.isDone()) {
                    return;
                }
                SSLException exception = new SSLException((String)"handshake timed out");
                try {
                    if (!this.val$localHandshakePromise.tryFailure((Throwable)exception)) return;
                    SslUtils.handleHandshakeFailure((ChannelHandlerContext)SslHandler.access$700((SslHandler)this.this$0), (Throwable)exception, (boolean)true);
                    return;
                }
                finally {
                    SslHandler.access$2300((SslHandler)this.this$0, (ChannelHandlerContext)SslHandler.access$700((SslHandler)this.this$0), (Throwable)exception);
                }
            }
        }, (long)handshakeTimeoutMillis, (TimeUnit)TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<Future<Channel>>)new FutureListener<Channel>((SslHandler)this, timeoutFuture){
            final /* synthetic */ ScheduledFuture val$timeoutFuture;
            final /* synthetic */ SslHandler this$0;
            {
                this.this$0 = this$0;
                this.val$timeoutFuture = scheduledFuture;
            }

            public void operationComplete(Future<Channel> f) throws Exception {
                this.val$timeoutFuture.cancel((boolean)false);
            }
        });
    }

    private void forceFlush(ChannelHandlerContext ctx) {
        this.needsFlush = false;
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.startTls) {
            this.startHandshakeProcessing();
        }
        ctx.fireChannelActive();
    }

    private void safeClose(ChannelHandlerContext ctx, ChannelFuture flushFuture, ChannelPromise promise) {
        long closeNotifyTimeout;
        if (!ctx.channel().isActive()) {
            ctx.close((ChannelPromise)promise);
            return;
        }
        io.netty.util.concurrent.ScheduledFuture<?> timeoutFuture = !flushFuture.isDone() ? ((closeNotifyTimeout = this.closeNotifyFlushTimeoutMillis) > 0L ? ctx.executor().schedule((Runnable)new Runnable((SslHandler)this, (ChannelFuture)flushFuture, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelFuture val$flushFuture;
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ SslHandler this$0;
            {
                this.this$0 = this$0;
                this.val$flushFuture = channelFuture;
                this.val$ctx = channelHandlerContext;
                this.val$promise = channelPromise;
            }

            public void run() {
                if (this.val$flushFuture.isDone()) return;
                SslHandler.access$2400().warn((String)"{} Last write attempt timed out; force-closing the connection.", (Object)this.val$ctx.channel());
                SslHandler.access$2500((ChannelFuture)this.val$ctx.close((ChannelPromise)this.val$ctx.newPromise()), (ChannelPromise)this.val$promise);
            }
        }, (long)closeNotifyTimeout, (TimeUnit)TimeUnit.MILLISECONDS) : null) : null;
        flushFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((SslHandler)this, timeoutFuture, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ScheduledFuture val$timeoutFuture;
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ SslHandler this$0;
            {
                this.this$0 = this$0;
                this.val$timeoutFuture = scheduledFuture;
                this.val$ctx = channelHandlerContext;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture f) throws Exception {
                long closeNotifyReadTimeout;
                if (this.val$timeoutFuture != null) {
                    this.val$timeoutFuture.cancel((boolean)false);
                }
                if ((closeNotifyReadTimeout = SslHandler.access$2600((SslHandler)this.this$0)) <= 0L) {
                    SslHandler.access$2500((ChannelFuture)this.val$ctx.close((ChannelPromise)this.val$ctx.newPromise()), (ChannelPromise)this.val$promise);
                    return;
                }
                io.netty.util.concurrent.ScheduledFuture<?> closeNotifyReadTimeoutFuture = !SslHandler.access$2700((SslHandler)this.this$0).isDone() ? this.val$ctx.executor().schedule((Runnable)new Runnable(this, (long)closeNotifyReadTimeout){
                    final /* synthetic */ long val$closeNotifyReadTimeout;
                    final /* synthetic */ 8 this$1;
                    {
                        this.this$1 = this$1;
                        this.val$closeNotifyReadTimeout = l;
                    }

                    public void run() {
                        if (SslHandler.access$2700((SslHandler)this.this$1.this$0).isDone()) return;
                        SslHandler.access$2400().debug((String)"{} did not receive close_notify in {}ms; force-closing the connection.", (Object)this.this$1.val$ctx.channel(), (Object)java.lang.Long.valueOf((long)this.val$closeNotifyReadTimeout));
                        SslHandler.access$2500((ChannelFuture)this.this$1.val$ctx.close((ChannelPromise)this.this$1.val$ctx.newPromise()), (ChannelPromise)this.this$1.val$promise);
                    }
                }, (long)closeNotifyReadTimeout, (TimeUnit)TimeUnit.MILLISECONDS) : null;
                SslHandler.access$2700((SslHandler)this.this$0).addListener((GenericFutureListener)new FutureListener<Channel>(this, closeNotifyReadTimeoutFuture){
                    final /* synthetic */ ScheduledFuture val$closeNotifyReadTimeoutFuture;
                    final /* synthetic */ 8 this$1;
                    {
                        this.this$1 = this$1;
                        this.val$closeNotifyReadTimeoutFuture = scheduledFuture;
                    }

                    public void operationComplete(Future<Channel> future) throws Exception {
                        if (this.val$closeNotifyReadTimeoutFuture != null) {
                            this.val$closeNotifyReadTimeoutFuture.cancel((boolean)false);
                        }
                        SslHandler.access$2500((ChannelFuture)this.this$1.val$ctx.close((ChannelPromise)this.this$1.val$ctx.newPromise()), (ChannelPromise)this.this$1.val$promise);
                    }
                });
            }
        });
    }

    private static void addCloseListener(ChannelFuture future, ChannelPromise promise) {
        future.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelPromiseNotifier((boolean)false, (ChannelPromise[])new ChannelPromise[]{promise}));
    }

    private ByteBuf allocate(ChannelHandlerContext ctx, int capacity) {
        ByteBufAllocator alloc = ctx.alloc();
        if (!this.engineType.wantsDirectBuffer) return alloc.buffer((int)capacity);
        return alloc.directBuffer((int)capacity);
    }

    private ByteBuf allocateOutNetBuf(ChannelHandlerContext ctx, int pendingBytes, int numComponents) {
        return this.engineType.allocateWrapBuffer((SslHandler)this, (ByteBufAllocator)ctx.alloc(), (int)pendingBytes, (int)numComponents);
    }

    private static boolean attemptCopyToCumulation(ByteBuf cumulation, ByteBuf next, int wrapDataSize) {
        int inReadableBytes = next.readableBytes();
        int cumulationCapacity = cumulation.capacity();
        if (wrapDataSize - cumulation.readableBytes() < inReadableBytes) return false;
        if (!cumulation.isWritable((int)inReadableBytes) || cumulationCapacity < wrapDataSize) {
            if (cumulationCapacity >= wrapDataSize) return false;
            if (!ByteBufUtil.ensureWritableSuccess((int)cumulation.ensureWritable((int)inReadableBytes, (boolean)false))) return false;
        }
        cumulation.writeBytes((ByteBuf)next);
        next.release();
        return true;
    }

    static /* synthetic */ SSLEngine access$100(SslHandler x0) {
        return x0.engine;
    }

    static /* synthetic */ ByteBuffer[] access$200(SslHandler x0) {
        return x0.singleBuffer;
    }

    static /* synthetic */ ByteBuffer access$300(ByteBuf x0, int x1, int x2) {
        return SslHandler.toByteBuffer((ByteBuf)x0, (int)x1, (int)x2);
    }

    static /* synthetic */ void access$500(SslHandler x0, ChannelPromise x1) {
        x0.closeOutbound0((ChannelPromise)x1);
    }

    static /* synthetic */ void access$600(SslHandler x0, ChannelHandlerContext x1, Throwable x2) {
        x0.setHandshakeFailureTransportFailure((ChannelHandlerContext)x1, (Throwable)x2);
    }

    static /* synthetic */ ChannelHandlerContext access$700(SslHandler x0) {
        return x0.ctx;
    }

    static /* synthetic */ void access$800(SslHandler x0, ChannelHandlerContext x1, Throwable x2) {
        x0.handleUnwrapThrowable((ChannelHandlerContext)x1, (Throwable)x2);
    }

    static /* synthetic */ void access$900(SslHandler x0, ChannelHandlerContext x1, Throwable x2) {
        x0.setHandshakeFailure((ChannelHandlerContext)x1, (Throwable)x2);
    }

    static /* synthetic */ void access$1000(SslHandler x0, ChannelHandlerContext x1) {
        x0.forceFlush((ChannelHandlerContext)x1);
    }

    static /* synthetic */ void access$1100(SslHandler x0, ChannelHandlerContext x1) {
        x0.channelReadComplete0((ChannelHandlerContext)x1);
    }

    static /* synthetic */ boolean access$1202(SslHandler x0, boolean x1) {
        x0.processTask = x1;
        return x0.processTask;
    }

    static /* synthetic */ void access$1300(SslHandler x0, boolean x1) {
        x0.executeDelegatedTasks((boolean)x1);
    }

    static /* synthetic */ void access$1400(SslHandler x0) {
        x0.setHandshakeSuccess();
    }

    static /* synthetic */ boolean access$1500(SslHandler x0) {
        return x0.setHandshakeSuccessIfStillHandshaking();
    }

    static /* synthetic */ void access$1600(SslHandler x0, ChannelHandlerContext x1, boolean x2) throws SSLException {
        x0.wrap((ChannelHandlerContext)x1, (boolean)x2);
    }

    static /* synthetic */ void access$1700(SslHandler x0, ChannelHandlerContext x1) throws SSLException {
        x0.unwrapNonAppData((ChannelHandlerContext)x1);
    }

    static /* synthetic */ boolean access$1800(SslHandler x0, ChannelHandlerContext x1, boolean x2) throws SSLException {
        return x0.wrapNonAppData((ChannelHandlerContext)x1, (boolean)x2);
    }

    static /* synthetic */ void access$1900(SSLEngine x0) {
        SslHandler.runAllDelegatedTasks((SSLEngine)x0);
    }

    static /* synthetic */ void access$2200(SslHandler x0, Promise x1) {
        x0.renegotiateOnEventLoop((Promise<Channel>)x1);
    }

    static /* synthetic */ void access$2300(SslHandler x0, ChannelHandlerContext x1, Throwable x2) {
        x0.releaseAndFailAll((ChannelHandlerContext)x1, (Throwable)x2);
    }

    static /* synthetic */ InternalLogger access$2400() {
        return logger;
    }

    static /* synthetic */ void access$2500(ChannelFuture x0, ChannelPromise x1) {
        SslHandler.addCloseListener((ChannelFuture)x0, (ChannelPromise)x1);
    }

    static /* synthetic */ long access$2600(SslHandler x0) {
        return x0.closeNotifyReadTimeoutMillis;
    }

    static /* synthetic */ LazyChannelPromise access$2700(SslHandler x0) {
        return x0.sslClosePromise;
    }

    static /* synthetic */ boolean access$2800(ByteBuf x0, ByteBuf x1, int x2) {
        return SslHandler.attemptCopyToCumulation((ByteBuf)x0, (ByteBuf)x1, (int)x2);
    }

    static /* synthetic */ SslEngineType access$2900(SslHandler x0) {
        return x0.engineType;
    }
}

