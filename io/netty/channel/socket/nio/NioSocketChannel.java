/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioSocketChannel
extends AbstractNioByteChannel
implements SocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSocketChannel.class);
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private final SocketChannelConfig config;

    private static java.nio.channels.SocketChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openSocketChannel();
        }
        catch (IOException e) {
            throw new ChannelException((String)"Failed to open a socket.", (Throwable)e);
        }
    }

    public NioSocketChannel() {
        this((SelectorProvider)DEFAULT_SELECTOR_PROVIDER);
    }

    public NioSocketChannel(SelectorProvider provider) {
        this((java.nio.channels.SocketChannel)NioSocketChannel.newSocket((SelectorProvider)provider));
    }

    public NioSocketChannel(java.nio.channels.SocketChannel socket) {
        this(null, (java.nio.channels.SocketChannel)socket);
    }

    public NioSocketChannel(Channel parent, java.nio.channels.SocketChannel socket) {
        super((Channel)parent, (SelectableChannel)socket);
        this.config = new NioSocketChannelConfig((NioSocketChannel)this, (NioSocketChannel)this, (Socket)socket.socket(), null);
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    public SocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected java.nio.channels.SocketChannel javaChannel() {
        return (java.nio.channels.SocketChannel)super.javaChannel();
    }

    @Override
    public boolean isActive() {
        java.nio.channels.SocketChannel ch = this.javaChannel();
        if (!ch.isOpen()) return false;
        if (!ch.isConnected()) return false;
        return true;
    }

    @Override
    public boolean isOutputShutdown() {
        if (this.javaChannel().socket().isOutputShutdown()) return true;
        if (!this.isActive()) return true;
        return false;
    }

    @Override
    public boolean isInputShutdown() {
        if (this.javaChannel().socket().isInputShutdown()) return true;
        if (!this.isActive()) return true;
        return false;
    }

    @Override
    public boolean isShutdown() {
        Socket socket = this.javaChannel().socket();
        if (socket.isInputShutdown()) {
            if (socket.isOutputShutdown()) return true;
        }
        if (!this.isActive()) return true;
        return false;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    @Override
    protected final void doShutdownOutput() throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            this.javaChannel().shutdownOutput();
            return;
        }
        this.javaChannel().socket().shutdownOutput();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput((ChannelPromise)this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(ChannelPromise promise) {
        NioEventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            ((AbstractChannel.AbstractUnsafe)((Object)this.unsafe())).shutdownOutput((ChannelPromise)promise);
            return promise;
        }
        loop.execute((Runnable)new Runnable((NioSocketChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ NioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                ((AbstractChannel.AbstractUnsafe)((Object)this.this$0.unsafe())).shutdownOutput((ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    @Override
    public ChannelFuture shutdownInput() {
        return this.shutdownInput((ChannelPromise)this.newPromise());
    }

    @Override
    protected boolean isInputShutdown0() {
        return this.isInputShutdown();
    }

    @Override
    public ChannelFuture shutdownInput(ChannelPromise promise) {
        NioEventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            this.shutdownInput0((ChannelPromise)promise);
            return promise;
        }
        loop.execute((Runnable)new Runnable((NioSocketChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ NioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                NioSocketChannel.access$100((NioSocketChannel)this.this$0, (ChannelPromise)this.val$promise);
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
        shutdownOutputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((NioSocketChannel)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ NioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
                NioSocketChannel.access$200((NioSocketChannel)this.this$0, (ChannelFuture)shutdownOutputFuture, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    private void shutdownOutputDone(ChannelFuture shutdownOutputFuture, ChannelPromise promise) {
        ChannelFuture shutdownInputFuture = this.shutdownInput();
        if (shutdownInputFuture.isDone()) {
            NioSocketChannel.shutdownDone((ChannelFuture)shutdownOutputFuture, (ChannelFuture)shutdownInputFuture, (ChannelPromise)promise);
            return;
        }
        shutdownInputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((NioSocketChannel)this, (ChannelFuture)shutdownOutputFuture, (ChannelPromise)promise){
            final /* synthetic */ ChannelFuture val$shutdownOutputFuture;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ NioSocketChannel this$0;
            {
                this.this$0 = this$0;
                this.val$shutdownOutputFuture = channelFuture;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
                NioSocketChannel.access$300((ChannelFuture)this.val$shutdownOutputFuture, (ChannelFuture)shutdownInputFuture, (ChannelPromise)this.val$promise);
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

    private void shutdownInput0(ChannelPromise promise) {
        try {
            this.shutdownInput0();
            promise.setSuccess();
            return;
        }
        catch (Throwable t) {
            promise.setFailure((Throwable)t);
        }
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    private void shutdownInput0() throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            this.javaChannel().shutdownInput();
            return;
        }
        this.javaChannel().socket().shutdownInput();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.doBind0((SocketAddress)localAddress);
    }

    private void doBind0(SocketAddress localAddress) throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            SocketUtils.bind((java.nio.channels.SocketChannel)this.javaChannel(), (SocketAddress)localAddress);
            return;
        }
        SocketUtils.bind((Socket)this.javaChannel().socket(), (SocketAddress)localAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.doBind0((SocketAddress)localAddress);
        }
        boolean success = false;
        try {
            boolean connected = SocketUtils.connect((java.nio.channels.SocketChannel)this.javaChannel(), (SocketAddress)remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps((int)8);
            }
            success = true;
            boolean bl = connected;
            return bl;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (this.javaChannel().finishConnect()) return;
        throw new Error();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.javaChannel().close();
    }

    @Override
    protected int doReadBytes(ByteBuf byteBuf) throws Exception {
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        allocHandle.attemptedBytesRead((int)byteBuf.writableBytes());
        return byteBuf.writeBytes((ScatteringByteChannel)this.javaChannel(), (int)allocHandle.attemptedBytesRead());
    }

    @Override
    protected int doWriteBytes(ByteBuf buf) throws Exception {
        int expectedWrittenBytes = buf.readableBytes();
        return buf.readBytes((GatheringByteChannel)this.javaChannel(), (int)expectedWrittenBytes);
    }

    @Override
    protected long doWriteFileRegion(FileRegion region) throws Exception {
        long position = region.transferred();
        return region.transferTo((WritableByteChannel)this.javaChannel(), (long)position);
    }

    private void adjustMaxBytesPerGatheringWrite(int attempted, int written, int oldMaxBytesPerGatheringWrite) {
        if (attempted == written) {
            if (attempted << 1 <= oldMaxBytesPerGatheringWrite) return;
            ((NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite((int)(attempted << 1));
            return;
        }
        if (attempted <= 4096) return;
        if (written >= attempted >>> 1) return;
        ((NioSocketChannelConfig)this.config).setMaxBytesPerGatheringWrite((int)(attempted >>> 1));
    }

    /*
     * Unable to fully structure code
     */
    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        ch = this.javaChannel();
        writeSpinCount = this.config().getWriteSpinCount();
        do {
            if (in.isEmpty()) {
                this.clearOpWrite();
                return;
            }
            maxBytesPerGatheringWrite = ((NioSocketChannelConfig)this.config).getMaxBytesPerGatheringWrite();
            nioBuffers = in.nioBuffers((int)1024, (long)((long)maxBytesPerGatheringWrite));
            nioBufferCnt = in.nioBufferCount();
            switch (nioBufferCnt) {
                case 0: {
                    writeSpinCount -= this.doWrite0((ChannelOutboundBuffer)in);
                    ** break;
                }
                case 1: {
                    buffer = nioBuffers[0];
                    attemptedBytes = buffer.remaining();
                    localWrittenBytes = ch.write((ByteBuffer)buffer);
                    if (localWrittenBytes <= 0) {
                        this.incompleteWrite((boolean)true);
                        return;
                    }
                    this.adjustMaxBytesPerGatheringWrite((int)attemptedBytes, (int)localWrittenBytes, (int)maxBytesPerGatheringWrite);
                    in.removeBytes((long)((long)localWrittenBytes));
                    --writeSpinCount;
                    ** break;
                }
            }
            attemptedBytes = in.nioBufferSize();
            localWrittenBytes = ch.write((ByteBuffer[])nioBuffers, (int)0, (int)nioBufferCnt);
            if (localWrittenBytes <= 0L) {
                this.incompleteWrite((boolean)true);
                return;
            }
            this.adjustMaxBytesPerGatheringWrite((int)((int)attemptedBytes), (int)((int)localWrittenBytes), (int)maxBytesPerGatheringWrite);
            in.removeBytes((long)localWrittenBytes);
            --writeSpinCount;
            ** break;
lbl34: // 3 sources:
        } while (writeSpinCount > 0);
        this.incompleteWrite((boolean)(writeSpinCount < 0));
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioSocketChannelUnsafe((NioSocketChannel)this, null);
    }

    static /* synthetic */ void access$100(NioSocketChannel x0, ChannelPromise x1) {
        x0.shutdownInput0((ChannelPromise)x1);
    }

    static /* synthetic */ void access$200(NioSocketChannel x0, ChannelFuture x1, ChannelPromise x2) {
        x0.shutdownOutputDone((ChannelFuture)x1, (ChannelPromise)x2);
    }

    static /* synthetic */ void access$300(ChannelFuture x0, ChannelFuture x1, ChannelPromise x2) {
        NioSocketChannel.shutdownDone((ChannelFuture)x0, (ChannelFuture)x1, (ChannelPromise)x2);
    }

    static /* synthetic */ void access$500(NioSocketChannel x0) throws Exception {
        x0.doDeregister();
    }

    static /* synthetic */ void access$600(NioSocketChannel x0) {
        x0.clearReadPending();
    }
}

