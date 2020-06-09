/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.AbstractSniHandler;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.SniCompletionEvent;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

public abstract class AbstractSniHandler<T>
extends ByteToMessageDecoder
implements ChannelOutboundHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractSniHandler.class);
    private boolean handshakeFailed;
    private boolean suppressRead;
    private boolean readPending;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        block14 : {
            if (this.suppressRead) return;
            if (this.handshakeFailed) return;
            try {
                int readerIndex = in.readerIndex();
                int readableBytes = in.readableBytes();
                if (readableBytes < 5) {
                    return;
                }
                short command = in.getUnsignedByte((int)readerIndex);
                block2 : switch (command) {
                    case 20: 
                    case 21: {
                        int len = SslUtils.getEncryptedPacketLength((ByteBuf)in, (int)readerIndex);
                        if (len == -2) {
                            this.handshakeFailed = true;
                            NotSslRecordException e = new NotSslRecordException((String)("not an SSL/TLS record: " + ByteBufUtil.hexDump((ByteBuf)in)));
                            in.skipBytes((int)in.readableBytes());
                            ctx.fireUserEventTriggered((Object)new SniCompletionEvent((Throwable)e));
                            SslUtils.handleHandshakeFailure((ChannelHandlerContext)ctx, (Throwable)e, (boolean)true);
                            throw e;
                        }
                        if (len != -1) break;
                        return;
                    }
                    case 22: {
                        int extensionsLimit;
                        int extensionsLength;
                        short majorVersion = in.getUnsignedByte((int)(readerIndex + 1));
                        if (majorVersion != 3) break;
                        int packetLength = in.getUnsignedShort((int)(readerIndex + 3)) + 5;
                        if (readableBytes < packetLength) {
                            return;
                        }
                        int endOffset = readerIndex + packetLength;
                        int offset = readerIndex + 43;
                        if (endOffset - offset < 6) break;
                        short sessionIdLength = in.getUnsignedByte((int)offset);
                        int cipherSuitesLength = in.getUnsignedShort((int)(offset += sessionIdLength + 1));
                        short compressionMethodLength = in.getUnsignedByte((int)(offset += cipherSuitesLength + 2));
                        offset += compressionMethodLength + 1;
                        if ((extensionsLimit = (offset += 2) + (extensionsLength = in.getUnsignedShort((int)offset))) > endOffset) break;
                        while (extensionsLimit - offset >= 4) {
                            int extensionLength;
                            int extensionType = in.getUnsignedShort((int)offset);
                            offset += 2;
                            if (extensionsLimit - (offset += 2) < (extensionLength = in.getUnsignedShort((int)offset))) break block2;
                            if (extensionType == 0) {
                                int serverNameLength;
                                if (extensionsLimit - (offset += 2) < 3) break block2;
                                short serverNameType = in.getUnsignedByte((int)offset);
                                ++offset;
                                if (serverNameType != 0 || extensionsLimit - (offset += 2) < (serverNameLength = in.getUnsignedShort((int)offset))) break block2;
                                String hostname = in.toString((int)offset, (int)serverNameLength, (Charset)CharsetUtil.US_ASCII);
                                try {
                                    this.select((ChannelHandlerContext)ctx, (String)hostname.toLowerCase((Locale)Locale.US));
                                    return;
                                }
                                catch (Throwable t) {
                                    PlatformDependent.throwException((Throwable)t);
                                }
                                return;
                            }
                            offset += extensionLength;
                        }
                        break;
                    }
                }
            }
            catch (NotSslRecordException e) {
                throw e;
            }
            catch (Exception e) {
                if (!logger.isDebugEnabled()) break block14;
                logger.debug((String)("Unexpected client hello packet: " + ByteBufUtil.hexDump((ByteBuf)in)), (Throwable)e);
            }
        }
        this.select((ChannelHandlerContext)ctx, null);
    }

    private void select(ChannelHandlerContext ctx, String hostname) throws Exception {
        Future<T> future = this.lookup((ChannelHandlerContext)ctx, (String)hostname);
        if (future.isDone()) {
            this.fireSniCompletionEvent((ChannelHandlerContext)ctx, (String)hostname, future);
            this.onLookupComplete((ChannelHandlerContext)ctx, (String)hostname, future);
            return;
        }
        this.suppressRead = true;
        future.addListener(new FutureListener<T>((AbstractSniHandler)this, (ChannelHandlerContext)ctx, (String)hostname){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ String val$hostname;
            final /* synthetic */ AbstractSniHandler this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
                this.val$hostname = string;
            }

            public void operationComplete(Future<T> future) throws Exception {
                try {
                    AbstractSniHandler.access$002((AbstractSniHandler)this.this$0, (boolean)false);
                    try {
                        AbstractSniHandler.access$100((AbstractSniHandler)this.this$0, (ChannelHandlerContext)this.val$ctx, (String)this.val$hostname, future);
                        this.this$0.onLookupComplete((ChannelHandlerContext)this.val$ctx, (String)this.val$hostname, future);
                        return;
                    }
                    catch (io.netty.handler.codec.DecoderException err) {
                        this.val$ctx.fireExceptionCaught((Throwable)err);
                        return;
                    }
                    catch (Exception cause) {
                        this.val$ctx.fireExceptionCaught((Throwable)new io.netty.handler.codec.DecoderException((Throwable)cause));
                        return;
                    }
                    catch (Throwable cause) {
                        this.val$ctx.fireExceptionCaught((Throwable)cause);
                        return;
                    }
                }
                finally {
                    if (AbstractSniHandler.access$200((AbstractSniHandler)this.this$0)) {
                        AbstractSniHandler.access$202((AbstractSniHandler)this.this$0, (boolean)false);
                        this.val$ctx.read();
                    }
                }
            }
        });
    }

    private void fireSniCompletionEvent(ChannelHandlerContext ctx, String hostname, Future<T> future) {
        Throwable cause = future.cause();
        if (cause == null) {
            ctx.fireUserEventTriggered((Object)new SniCompletionEvent((String)hostname));
            return;
        }
        ctx.fireUserEventTriggered((Object)new SniCompletionEvent((String)hostname, (Throwable)cause));
    }

    protected abstract Future<T> lookup(ChannelHandlerContext var1, String var2) throws Exception;

    protected abstract void onLookupComplete(ChannelHandlerContext var1, String var2, Future<T> var3) throws Exception;

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (this.suppressRead) {
            this.readPending = true;
            return;
        }
        ctx.read();
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
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect((ChannelPromise)promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close((ChannelPromise)promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister((ChannelPromise)promise);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    static /* synthetic */ boolean access$002(AbstractSniHandler x0, boolean x1) {
        x0.suppressRead = x1;
        return x0.suppressRead;
    }

    static /* synthetic */ void access$100(AbstractSniHandler x0, ChannelHandlerContext x1, String x2, Future x3) {
        x0.fireSniCompletionEvent((ChannelHandlerContext)x1, (String)x2, x3);
    }

    static /* synthetic */ boolean access$200(AbstractSniHandler x0) {
        return x0.readPending;
    }

    static /* synthetic */ boolean access$202(AbstractSniHandler x0, boolean x1) {
        x0.readPending = x1;
        return x0.readPending;
    }
}

