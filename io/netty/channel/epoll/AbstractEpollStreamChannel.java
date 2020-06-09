/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollStreamChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.util.Queue;
import java.util.concurrent.Executor;

public abstract class AbstractEpollStreamChannel
extends AbstractEpollChannel
implements DuplexChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)false, (int)16);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEpollStreamChannel.class);
    private final Runnable flushTask = new Runnable((AbstractEpollStreamChannel)this){
        final /* synthetic */ AbstractEpollStreamChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            ((AbstractEpollChannel.AbstractEpollUnsafe)this.this$0.unsafe()).flush0();
        }
    };
    private volatile Queue<SpliceInTask> spliceQueue;
    private FileDescriptor pipeIn;
    private FileDescriptor pipeOut;
    private WritableByteChannel byteChannel;

    protected AbstractEpollStreamChannel(Channel parent, int fd) {
        this((Channel)parent, (LinuxSocket)new LinuxSocket((int)fd));
    }

    protected AbstractEpollStreamChannel(int fd) {
        this((LinuxSocket)new LinuxSocket((int)fd));
    }

    AbstractEpollStreamChannel(LinuxSocket fd) {
        this((LinuxSocket)fd, (boolean)AbstractEpollStreamChannel.isSoErrorZero((Socket)fd));
    }

    AbstractEpollStreamChannel(Channel parent, LinuxSocket fd) {
        super((Channel)parent, (LinuxSocket)fd, (boolean)true);
        this.flags |= Native.EPOLLRDHUP;
    }

    AbstractEpollStreamChannel(Channel parent, LinuxSocket fd, SocketAddress remote) {
        super((Channel)parent, (LinuxSocket)fd, (SocketAddress)remote);
        this.flags |= Native.EPOLLRDHUP;
    }

    protected AbstractEpollStreamChannel(LinuxSocket fd, boolean active) {
        super(null, (LinuxSocket)fd, (boolean)active);
        this.flags |= Native.EPOLLRDHUP;
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollStreamUnsafe((AbstractEpollStreamChannel)this);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    public final ChannelFuture spliceTo(AbstractEpollStreamChannel ch, int len) {
        return this.spliceTo((AbstractEpollStreamChannel)ch, (int)len, (ChannelPromise)this.newPromise());
    }

    public final ChannelFuture spliceTo(AbstractEpollStreamChannel ch, int len, ChannelPromise promise) {
        if (ch.eventLoop() != this.eventLoop()) {
            throw new IllegalArgumentException((String)"EventLoops are not the same.");
        }
        ObjectUtil.checkPositiveOrZero((int)len, (String)"len");
        if (ch.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) throw new IllegalStateException((String)("spliceTo() supported only when using " + (Object)((Object)((Object)EpollMode.LEVEL_TRIGGERED))));
        if (this.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException((String)("spliceTo() supported only when using " + (Object)((Object)((Object)EpollMode.LEVEL_TRIGGERED))));
        }
        ObjectUtil.checkNotNull(promise, (String)"promise");
        if (!this.isOpen()) {
            promise.tryFailure((Throwable)new ClosedChannelException());
            return promise;
        }
        this.addToSpliceQueue((SpliceInTask)new SpliceInChannelTask((AbstractEpollStreamChannel)this, (AbstractEpollStreamChannel)ch, (int)len, (ChannelPromise)promise));
        this.failSpliceIfClosed((ChannelPromise)promise);
        return promise;
    }

    public final ChannelFuture spliceTo(FileDescriptor ch, int offset, int len) {
        return this.spliceTo((FileDescriptor)ch, (int)offset, (int)len, (ChannelPromise)this.newPromise());
    }

    public final ChannelFuture spliceTo(FileDescriptor ch, int offset, int len, ChannelPromise promise) {
        ObjectUtil.checkPositiveOrZero((int)len, (String)"len");
        ObjectUtil.checkPositiveOrZero((int)offset, (String)"offset");
        if (this.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException((String)("spliceTo() supported only when using " + (Object)((Object)EpollMode.LEVEL_TRIGGERED)));
        }
        ObjectUtil.checkNotNull(promise, (String)"promise");
        if (!this.isOpen()) {
            promise.tryFailure((Throwable)new ClosedChannelException());
            return promise;
        }
        this.addToSpliceQueue((SpliceInTask)new SpliceFdTask((AbstractEpollStreamChannel)this, (FileDescriptor)ch, (int)offset, (int)len, (ChannelPromise)promise));
        this.failSpliceIfClosed((ChannelPromise)promise);
        return promise;
    }

    private void failSpliceIfClosed(ChannelPromise promise) {
        if (this.isOpen()) return;
        if (!promise.tryFailure((Throwable)new ClosedChannelException())) return;
        this.eventLoop().execute((Runnable)new Runnable((AbstractEpollStreamChannel)this){
            final /* synthetic */ AbstractEpollStreamChannel this$0;
            {
                this.this$0 = this$0;
            }

            public void run() {
                AbstractEpollStreamChannel.access$000((AbstractEpollStreamChannel)this.this$0);
            }
        });
    }

    private int writeBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            in.remove();
            return 0;
        }
        if (buf.hasMemoryAddress()) return this.doWriteBytes((ChannelOutboundBuffer)in, (ByteBuf)buf);
        if (buf.nioBufferCount() == 1) {
            return this.doWriteBytes((ChannelOutboundBuffer)in, (ByteBuf)buf);
        }
        ByteBuffer[] nioBuffers = buf.nioBuffers();
        return this.writeBytesMultiple((ChannelOutboundBuffer)in, (ByteBuffer[])nioBuffers, (int)nioBuffers.length, (long)((long)readableBytes), (long)this.config().getMaxBytesPerGatheringWrite());
    }

    private void adjustMaxBytesPerGatheringWrite(long attempted, long written, long oldMaxBytesPerGatheringWrite) {
        if (attempted == written) {
            if (attempted << 1 <= oldMaxBytesPerGatheringWrite) return;
            this.config().setMaxBytesPerGatheringWrite((long)(attempted << 1));
            return;
        }
        if (attempted <= 4096L) return;
        if (written >= attempted >>> 1) return;
        this.config().setMaxBytesPerGatheringWrite((long)(attempted >>> 1));
    }

    private int writeBytesMultiple(ChannelOutboundBuffer in, IovArray array) throws IOException {
        long expectedWrittenBytes = array.size();
        assert (expectedWrittenBytes != 0L);
        int cnt = array.count();
        assert (cnt != 0);
        long localWrittenBytes = this.socket.writevAddresses((long)array.memoryAddress((int)0), (int)cnt);
        if (localWrittenBytes <= 0L) return Integer.MAX_VALUE;
        this.adjustMaxBytesPerGatheringWrite((long)expectedWrittenBytes, (long)localWrittenBytes, (long)array.maxBytes());
        in.removeBytes((long)localWrittenBytes);
        return 1;
    }

    private int writeBytesMultiple(ChannelOutboundBuffer in, ByteBuffer[] nioBuffers, int nioBufferCnt, long expectedWrittenBytes, long maxBytesPerGatheringWrite) throws IOException {
        long localWrittenBytes;
        assert (expectedWrittenBytes != 0L);
        if (expectedWrittenBytes > maxBytesPerGatheringWrite) {
            expectedWrittenBytes = maxBytesPerGatheringWrite;
        }
        if ((localWrittenBytes = this.socket.writev((ByteBuffer[])nioBuffers, (int)0, (int)nioBufferCnt, (long)expectedWrittenBytes)) <= 0L) return Integer.MAX_VALUE;
        this.adjustMaxBytesPerGatheringWrite((long)expectedWrittenBytes, (long)localWrittenBytes, (long)maxBytesPerGatheringWrite);
        in.removeBytes((long)localWrittenBytes);
        return 1;
    }

    private int writeDefaultFileRegion(ChannelOutboundBuffer in, DefaultFileRegion region) throws Exception {
        long regionCount;
        long offset = region.transferred();
        if (offset >= (regionCount = region.count())) {
            in.remove();
            return 0;
        }
        long flushedAmount = this.socket.sendFile((DefaultFileRegion)region, (long)region.position(), (long)offset, (long)(regionCount - offset));
        if (flushedAmount > 0L) {
            in.progress((long)flushedAmount);
            if (region.transferred() < regionCount) return 1;
            in.remove();
            return 1;
        }
        if (flushedAmount != 0L) return Integer.MAX_VALUE;
        this.validateFileRegion((DefaultFileRegion)region, (long)offset);
        return Integer.MAX_VALUE;
    }

    private int writeFileRegion(ChannelOutboundBuffer in, FileRegion region) throws Exception {
        long flushedAmount;
        if (region.transferred() >= region.count()) {
            in.remove();
            return 0;
        }
        if (this.byteChannel == null) {
            this.byteChannel = new EpollSocketWritableByteChannel((AbstractEpollStreamChannel)this);
        }
        if ((flushedAmount = region.transferTo((WritableByteChannel)this.byteChannel, (long)region.transferred())) <= 0L) return Integer.MAX_VALUE;
        in.progress((long)flushedAmount);
        if (region.transferred() < region.count()) return 1;
        in.remove();
        return 1;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        int writeSpinCount = this.config().getWriteSpinCount();
        do {
            int msgCount;
            if ((msgCount = in.size()) > 1 && in.current() instanceof ByteBuf) {
                writeSpinCount -= this.doWriteMultiple((ChannelOutboundBuffer)in);
                continue;
            }
            if (msgCount == 0) {
                this.clearFlag((int)Native.EPOLLOUT);
                return;
            }
            writeSpinCount -= this.doWriteSingle((ChannelOutboundBuffer)in);
        } while (writeSpinCount > 0);
        if (writeSpinCount == 0) {
            this.clearFlag((int)Native.EPOLLOUT);
            this.eventLoop().execute((Runnable)this.flushTask);
            return;
        }
        this.setFlag((int)Native.EPOLLOUT);
    }

    protected int doWriteSingle(ChannelOutboundBuffer in) throws Exception {
        Object msg = in.current();
        if (msg instanceof ByteBuf) {
            return this.writeBytes((ChannelOutboundBuffer)in, (ByteBuf)((ByteBuf)msg));
        }
        if (msg instanceof DefaultFileRegion) {
            return this.writeDefaultFileRegion((ChannelOutboundBuffer)in, (DefaultFileRegion)((DefaultFileRegion)msg));
        }
        if (msg instanceof FileRegion) {
            return this.writeFileRegion((ChannelOutboundBuffer)in, (FileRegion)((FileRegion)msg));
        }
        if (!(msg instanceof SpliceOutTask)) throw new Error();
        if (!((SpliceOutTask)msg).spliceOut()) {
            return Integer.MAX_VALUE;
        }
        in.remove();
        return 1;
    }

    private int doWriteMultiple(ChannelOutboundBuffer in) throws Exception {
        long maxBytesPerGatheringWrite = this.config().getMaxBytesPerGatheringWrite();
        IovArray array = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
        array.maxBytes((long)maxBytesPerGatheringWrite);
        in.forEachFlushedMessage((ChannelOutboundBuffer.MessageProcessor)array);
        if (array.count() >= 1) {
            return this.writeBytesMultiple((ChannelOutboundBuffer)in, (IovArray)array);
        }
        in.removeBytes((long)0L);
        return 0;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        ByteBuf byteBuf;
        if (!(msg instanceof ByteBuf)) {
            if (msg instanceof FileRegion) return msg;
            if (!(msg instanceof SpliceOutTask)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
            return msg;
        }
        ByteBuf buf = (ByteBuf)msg;
        if (UnixChannelUtil.isBufferCopyNeededForWrite((ByteBuf)buf)) {
            byteBuf = this.newDirectBuffer((ByteBuf)buf);
            return byteBuf;
        }
        byteBuf = buf;
        return byteBuf;
    }

    @Override
    protected final void doShutdownOutput() throws Exception {
        this.socket.shutdown((boolean)false, (boolean)true);
    }

    private void shutdownInput0(ChannelPromise promise) {
        try {
            this.socket.shutdown((boolean)true, (boolean)false);
            promise.setSuccess();
            return;
        }
        catch (Throwable cause) {
            promise.setFailure((Throwable)cause);
        }
    }

    @Override
    public boolean isOutputShutdown() {
        return this.socket.isOutputShutdown();
    }

    @Override
    public boolean isInputShutdown() {
        return this.socket.isInputShutdown();
    }

    @Override
    public boolean isShutdown() {
        return this.socket.isShutdown();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(ChannelPromise promise) {
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            ((AbstractChannel.AbstractUnsafe)this.unsafe()).shutdownOutput((ChannelPromise)promise);
            return promise;
        }
        loop.execute((Runnable)new Runnable((AbstractEpollStreamChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractEpollStreamChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                ((AbstractChannel.AbstractUnsafe)this.this$0.unsafe()).shutdownOutput((ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    @Override
    public ChannelFuture shutdownInput() {
        return this.shutdownInput((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture shutdownInput(ChannelPromise promise) {
        Executor closeExecutor = ((EpollStreamUnsafe)this.unsafe()).prepareToClose();
        if (closeExecutor != null) {
            closeExecutor.execute((Runnable)new Runnable((AbstractEpollStreamChannel)this, (ChannelPromise)promise){
                final /* synthetic */ ChannelPromise val$promise;
                final /* synthetic */ AbstractEpollStreamChannel this$0;
                {
                    this.this$0 = this$0;
                    this.val$promise = channelPromise;
                }

                public void run() {
                    AbstractEpollStreamChannel.access$100((AbstractEpollStreamChannel)this.this$0, (ChannelPromise)this.val$promise);
                }
            });
            return promise;
        }
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            this.shutdownInput0((ChannelPromise)promise);
            return promise;
        }
        loop.execute((Runnable)new Runnable((AbstractEpollStreamChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractEpollStreamChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                AbstractEpollStreamChannel.access$100((AbstractEpollStreamChannel)this.this$0, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    @Override
    public ChannelFuture shutdown() {
        return this.shutdown((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture shutdown(ChannelPromise promise) {
        ChannelFuture shutdownOutputFuture = this.shutdownOutput();
        if (shutdownOutputFuture.isDone()) {
            this.shutdownOutputDone((ChannelFuture)shutdownOutputFuture, (ChannelPromise)promise);
            return promise;
        }
        shutdownOutputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((AbstractEpollStreamChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractEpollStreamChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
                AbstractEpollStreamChannel.access$200((AbstractEpollStreamChannel)this.this$0, (ChannelFuture)shutdownOutputFuture, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    private void shutdownOutputDone(ChannelFuture shutdownOutputFuture, ChannelPromise promise) {
        ChannelFuture shutdownInputFuture = this.shutdownInput();
        if (shutdownInputFuture.isDone()) {
            AbstractEpollStreamChannel.shutdownDone((ChannelFuture)shutdownOutputFuture, (ChannelFuture)shutdownInputFuture, (ChannelPromise)promise);
            return;
        }
        shutdownInputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((AbstractEpollStreamChannel)this, (ChannelFuture)shutdownOutputFuture, (ChannelPromise)promise){
            final /* synthetic */ ChannelFuture val$shutdownOutputFuture;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ AbstractEpollStreamChannel this$0;
            {
                this.this$0 = this$0;
                this.val$shutdownOutputFuture = channelFuture;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
                AbstractEpollStreamChannel.access$300((ChannelFuture)this.val$shutdownOutputFuture, (ChannelFuture)shutdownInputFuture, (ChannelPromise)this.val$promise);
            }
        });
    }

    private static void shutdownDone(ChannelFuture shutdownOutputFuture, ChannelFuture shutdownInputFuture, ChannelPromise promise) {
        Throwable shutdownOutputCause = shutdownOutputFuture.cause();
        Throwable shutdownInputCause = shutdownInputFuture.cause();
        if (shutdownOutputCause != null) {
            if (shutdownInputCause != null) {
                logger.debug((String)"Exception suppressed because a previous exception occurred.", (Throwable)shutdownInputCause);
            }
            promise.setFailure((Throwable)shutdownOutputCause);
            return;
        }
        if (shutdownInputCause != null) {
            promise.setFailure((Throwable)shutdownInputCause);
            return;
        }
        promise.setSuccess();
    }

    @Override
    protected void doClose() throws Exception {
        try {
            super.doClose();
            return;
        }
        finally {
            AbstractEpollStreamChannel.safeClosePipe((FileDescriptor)this.pipeIn);
            AbstractEpollStreamChannel.safeClosePipe((FileDescriptor)this.pipeOut);
            this.clearSpliceQueue();
        }
    }

    private void clearSpliceQueue() {
        Queue<SpliceInTask> sQueue = this.spliceQueue;
        if (sQueue == null) {
            return;
        }
        ClosedChannelException exception = null;
        SpliceInTask task;
        while ((task = sQueue.poll()) != null) {
            if (exception == null) {
                exception = new ClosedChannelException();
            }
            task.promise.tryFailure((Throwable)exception);
        }
        return;
    }

    private static void safeClosePipe(FileDescriptor fd) {
        if (fd == null) return;
        try {
            fd.close();
            return;
        }
        catch (IOException e) {
            logger.warn((String)"Error while closing a pipe", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addToSpliceQueue(SpliceInTask task) {
        Queue<SpliceInTask> sQueue = this.spliceQueue;
        if (sQueue == null) {
            AbstractEpollStreamChannel abstractEpollStreamChannel = this;
            // MONITORENTER : abstractEpollStreamChannel
            sQueue = this.spliceQueue;
            if (sQueue == null) {
                this.spliceQueue = sQueue = PlatformDependent.newMpscQueue();
            }
            // MONITOREXIT : abstractEpollStreamChannel
        }
        sQueue.add((SpliceInTask)task);
    }

    static /* synthetic */ void access$000(AbstractEpollStreamChannel x0) {
        x0.clearSpliceQueue();
    }

    static /* synthetic */ void access$100(AbstractEpollStreamChannel x0, ChannelPromise x1) {
        x0.shutdownInput0((ChannelPromise)x1);
    }

    static /* synthetic */ void access$200(AbstractEpollStreamChannel x0, ChannelFuture x1, ChannelPromise x2) {
        x0.shutdownOutputDone((ChannelFuture)x1, (ChannelPromise)x2);
    }

    static /* synthetic */ void access$300(ChannelFuture x0, ChannelFuture x1, ChannelPromise x2) {
        AbstractEpollStreamChannel.shutdownDone((ChannelFuture)x0, (ChannelFuture)x1, (ChannelPromise)x2);
    }

    static /* synthetic */ Queue access$400(AbstractEpollStreamChannel x0) {
        return x0.spliceQueue;
    }

    static /* synthetic */ FileDescriptor access$500(AbstractEpollStreamChannel x0) {
        return x0.pipeOut;
    }

    static /* synthetic */ FileDescriptor access$602(AbstractEpollStreamChannel x0, FileDescriptor x1) {
        x0.pipeIn = x1;
        return x0.pipeIn;
    }

    static /* synthetic */ FileDescriptor access$502(AbstractEpollStreamChannel x0, FileDescriptor x1) {
        x0.pipeOut = x1;
        return x0.pipeOut;
    }

    static /* synthetic */ FileDescriptor access$600(AbstractEpollStreamChannel x0) {
        return x0.pipeIn;
    }

    static /* synthetic */ void access$700(FileDescriptor x0) {
        AbstractEpollStreamChannel.safeClosePipe((FileDescriptor)x0);
    }
}

