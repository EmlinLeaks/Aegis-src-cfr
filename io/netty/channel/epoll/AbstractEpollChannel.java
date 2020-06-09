/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollDomainSocketChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.ScheduledFuture;

abstract class AbstractEpollChannel
extends AbstractChannel
implements UnixChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)false);
    final LinuxSocket socket;
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;
    private volatile SocketAddress local;
    private volatile SocketAddress remote;
    protected int flags = Native.EPOLLET | Native.EPOLLIN;
    protected int activeFlags;
    boolean inputClosedSeenErrorOnRead;
    boolean epollInReadyRunnablePending;
    protected volatile boolean active;
    private Runnable clearEpollInTask;

    AbstractEpollChannel(LinuxSocket fd) {
        this(null, (LinuxSocket)fd, (boolean)false);
    }

    AbstractEpollChannel(Channel parent, LinuxSocket fd, boolean active) {
        super((Channel)parent);
        this.socket = ObjectUtil.checkNotNull(fd, (String)"fd");
        this.active = active;
        if (!active) return;
        this.local = fd.localAddress();
        this.remote = fd.remoteAddress();
    }

    AbstractEpollChannel(Channel parent, LinuxSocket fd, SocketAddress remote) {
        super((Channel)parent);
        this.socket = ObjectUtil.checkNotNull(fd, (String)"fd");
        this.active = true;
        this.remote = remote;
        this.local = fd.localAddress();
    }

    static boolean isSoErrorZero(Socket fd) {
        try {
            if (fd.getSoError() != 0) return false;
            return true;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    void setFlag(int flag) {
        if (this.isFlagSet((int)flag)) return;
        this.flags |= flag;
        this.updatePendingFlagsSet();
    }

    void clearFlag(int flag) {
        if (!this.isFlagSet((int)flag)) return;
        this.flags &= ~flag;
        this.updatePendingFlagsSet();
    }

    private void updatePendingFlagsSet() {
        if (!this.isRegistered()) return;
        ((EpollEventLoop)this.eventLoop()).updatePendingFlagsSet((AbstractEpollChannel)this);
    }

    boolean isFlagSet(int flag) {
        if ((this.flags & flag) == 0) return false;
        return true;
    }

    @Override
    public final FileDescriptor fd() {
        return this.socket;
    }

    @Override
    public abstract EpollChannelConfig config();

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        this.active = false;
        this.inputClosedSeenErrorOnRead = true;
        try {
            ScheduledFuture<?> future;
            ChannelPromise promise = this.connectPromise;
            if (promise != null) {
                promise.tryFailure((Throwable)new ClosedChannelException());
                this.connectPromise = null;
            }
            if ((future = this.connectTimeoutFuture) != null) {
                future.cancel((boolean)false);
                this.connectTimeoutFuture = null;
            }
            if (!this.isRegistered()) return;
            EventLoop loop = this.eventLoop();
            if (loop.inEventLoop()) {
                this.doDeregister();
                return;
            }
            loop.execute((Runnable)new Runnable((AbstractEpollChannel)this){
                final /* synthetic */ AbstractEpollChannel this$0;
                {
                    this.this$0 = this$0;
                }

                public void run() {
                    try {
                        this.this$0.doDeregister();
                        return;
                    }
                    catch (Throwable cause) {
                        this.this$0.pipeline().fireExceptionCaught((Throwable)cause);
                    }
                }
            });
            return;
        }
        finally {
            this.socket.close();
        }
    }

    void resetCachedAddresses() {
        this.local = this.socket.localAddress();
        this.remote = this.socket.remoteAddress();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }

    @Override
    public boolean isOpen() {
        return this.socket.isOpen();
    }

    @Override
    protected void doDeregister() throws Exception {
        ((EpollEventLoop)this.eventLoop()).remove((AbstractEpollChannel)this);
    }

    @Override
    protected final void doBeginRead() throws Exception {
        AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
        unsafe.readPending = true;
        this.setFlag((int)Native.EPOLLIN);
        if (!unsafe.maybeMoreDataToRead) return;
        unsafe.executeEpollInReadyRunnable((ChannelConfig)this.config());
    }

    final boolean shouldBreakEpollInReady(ChannelConfig config) {
        if (!this.socket.isInputShutdown()) return false;
        if (this.inputClosedSeenErrorOnRead) return true;
        if (AbstractEpollChannel.isAllowHalfClosure((ChannelConfig)config)) return false;
        return true;
    }

    private static boolean isAllowHalfClosure(ChannelConfig config) {
        if (config instanceof EpollDomainSocketChannelConfig) {
            return ((EpollDomainSocketChannelConfig)config).isAllowHalfClosure();
        }
        if (!(config instanceof SocketChannelConfig)) return false;
        if (!((SocketChannelConfig)config).isAllowHalfClosure()) return false;
        return true;
    }

    final void clearEpollIn() {
        EventLoop loop = this.isRegistered() ? this.eventLoop() : null;
        AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
        if (loop == null || loop.inEventLoop()) {
            unsafe.clearEpollIn0();
            return;
        }
        Runnable clearFlagTask = this.clearEpollInTask;
        if (clearFlagTask == null) {
            this.clearEpollInTask = clearFlagTask = new Runnable((AbstractEpollChannel)this, (AbstractEpollUnsafe)unsafe){
                final /* synthetic */ AbstractEpollUnsafe val$unsafe;
                final /* synthetic */ AbstractEpollChannel this$0;
                {
                    this.this$0 = this$0;
                    this.val$unsafe = abstractEpollUnsafe;
                }

                public void run() {
                    if (this.val$unsafe.readPending) return;
                    if (this.this$0.config().isAutoRead()) return;
                    this.val$unsafe.clearEpollIn0();
                }
            };
        }
        loop.execute((Runnable)clearFlagTask);
    }

    void modifyEvents() throws IOException {
        if (!this.isOpen()) return;
        if (!this.isRegistered()) return;
        ((EpollEventLoop)this.eventLoop()).modify((AbstractEpollChannel)this);
    }

    @Override
    protected void doRegister() throws Exception {
        this.epollInReadyRunnablePending = false;
        ((EpollEventLoop)this.eventLoop()).add((AbstractEpollChannel)this);
    }

    @Override
    protected abstract AbstractEpollUnsafe newUnsafe();

    protected final ByteBuf newDirectBuffer(ByteBuf buf) {
        return this.newDirectBuffer((Object)buf, (ByteBuf)buf);
    }

    protected final ByteBuf newDirectBuffer(Object holder, ByteBuf buf) {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.release((Object)holder);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            return AbstractEpollChannel.newDirectBuffer0((Object)holder, (ByteBuf)buf, (ByteBufAllocator)alloc, (int)readableBytes);
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf == null) {
            return AbstractEpollChannel.newDirectBuffer0((Object)holder, (ByteBuf)buf, (ByteBufAllocator)alloc, (int)readableBytes);
        }
        directBuf.writeBytes((ByteBuf)buf, (int)buf.readerIndex(), (int)readableBytes);
        ReferenceCountUtil.safeRelease((Object)holder);
        return directBuf;
    }

    private static ByteBuf newDirectBuffer0(Object holder, ByteBuf buf, ByteBufAllocator alloc, int capacity) {
        ByteBuf directBuf = alloc.directBuffer((int)capacity);
        directBuf.writeBytes((ByteBuf)buf, (int)buf.readerIndex(), (int)capacity);
        ReferenceCountUtil.safeRelease((Object)holder);
        return directBuf;
    }

    protected static void checkResolvable(InetSocketAddress addr) {
        if (!addr.isUnresolved()) return;
        throw new UnresolvedAddressException();
    }

    protected final int doReadBytes(ByteBuf byteBuf) throws Exception {
        int localReadAmount;
        int writerIndex = byteBuf.writerIndex();
        this.unsafe().recvBufAllocHandle().attemptedBytesRead((int)byteBuf.writableBytes());
        if (byteBuf.hasMemoryAddress()) {
            localReadAmount = this.socket.readAddress((long)byteBuf.memoryAddress(), (int)writerIndex, (int)byteBuf.capacity());
        } else {
            ByteBuffer buf = byteBuf.internalNioBuffer((int)writerIndex, (int)byteBuf.writableBytes());
            localReadAmount = this.socket.read((ByteBuffer)buf, (int)buf.position(), (int)buf.limit());
        }
        if (localReadAmount <= 0) return localReadAmount;
        byteBuf.writerIndex((int)(writerIndex + localReadAmount));
        return localReadAmount;
    }

    protected final int doWriteBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
        if (buf.hasMemoryAddress()) {
            int localFlushedAmount = this.socket.writeAddress((long)buf.memoryAddress(), (int)buf.readerIndex(), (int)buf.writerIndex());
            if (localFlushedAmount <= 0) return Integer.MAX_VALUE;
            in.removeBytes((long)((long)localFlushedAmount));
            return 1;
        }
        ByteBuffer nioBuf = buf.nioBufferCount() == 1 ? buf.internalNioBuffer((int)buf.readerIndex(), (int)buf.readableBytes()) : buf.nioBuffer();
        int localFlushedAmount = this.socket.write((ByteBuffer)nioBuf, (int)nioBuf.position(), (int)nioBuf.limit());
        if (localFlushedAmount <= 0) return Integer.MAX_VALUE;
        nioBuf.position((int)(nioBuf.position() + localFlushedAmount));
        in.removeBytes((long)((long)localFlushedAmount));
        return 1;
    }

    @Override
    protected void doBind(SocketAddress local) throws Exception {
        if (local instanceof InetSocketAddress) {
            AbstractEpollChannel.checkResolvable((InetSocketAddress)((InetSocketAddress)local));
        }
        this.socket.bind((SocketAddress)local);
        this.local = this.socket.localAddress();
    }

    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        boolean connected;
        InetSocketAddress remoteSocketAddr;
        if (localAddress instanceof InetSocketAddress) {
            AbstractEpollChannel.checkResolvable((InetSocketAddress)((InetSocketAddress)localAddress));
        }
        InetSocketAddress inetSocketAddress = remoteSocketAddr = remoteAddress instanceof InetSocketAddress ? (InetSocketAddress)remoteAddress : null;
        if (remoteSocketAddr != null) {
            AbstractEpollChannel.checkResolvable((InetSocketAddress)remoteSocketAddr);
        }
        if (this.remote != null) {
            throw new AlreadyConnectedException();
        }
        if (localAddress != null) {
            this.socket.bind((SocketAddress)localAddress);
        }
        if (connected = this.doConnect0((SocketAddress)remoteAddress)) {
            this.remote = remoteSocketAddr == null ? remoteAddress : UnixChannelUtil.computeRemoteAddr((InetSocketAddress)remoteSocketAddr, (InetSocketAddress)this.socket.remoteAddress());
        }
        this.local = this.socket.localAddress();
        return connected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean doConnect0(SocketAddress remote) throws Exception {
        boolean success = false;
        try {
            boolean connected = this.socket.connect((SocketAddress)remote);
            if (!connected) {
                this.setFlag((int)Native.EPOLLOUT);
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
    protected SocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.remote;
    }

    static /* synthetic */ boolean access$000(ChannelConfig x0) {
        return AbstractEpollChannel.isAllowHalfClosure((ChannelConfig)x0);
    }

    static /* synthetic */ ChannelPromise access$100(AbstractEpollChannel x0) {
        return x0.connectPromise;
    }

    static /* synthetic */ ChannelPromise access$102(AbstractEpollChannel x0, ChannelPromise x1) {
        x0.connectPromise = x1;
        return x0.connectPromise;
    }

    static /* synthetic */ SocketAddress access$202(AbstractEpollChannel x0, SocketAddress x1) {
        x0.requestedRemoteAddress = x1;
        return x0.requestedRemoteAddress;
    }

    static /* synthetic */ ScheduledFuture access$302(AbstractEpollChannel x0, ScheduledFuture x1) {
        x0.connectTimeoutFuture = x1;
        return x0.connectTimeoutFuture;
    }

    static /* synthetic */ ScheduledFuture access$300(AbstractEpollChannel x0) {
        return x0.connectTimeoutFuture;
    }

    static /* synthetic */ SocketAddress access$200(AbstractEpollChannel x0) {
        return x0.requestedRemoteAddress;
    }

    static /* synthetic */ SocketAddress access$402(AbstractEpollChannel x0, SocketAddress x1) {
        x0.remote = x1;
        return x0.remote;
    }
}

