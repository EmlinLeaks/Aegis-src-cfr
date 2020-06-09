/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.local;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalChannelRegistry;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class LocalChannel
extends AbstractChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(LocalChannel.class);
    private static final AtomicReferenceFieldUpdater<LocalChannel, Future> FINISH_READ_FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(LocalChannel.class, Future.class, (String)"finishReadFuture");
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)false);
    private static final int MAX_READER_STACK_DEPTH = 8;
    private final ChannelConfig config = new DefaultChannelConfig((Channel)this);
    final Queue<Object> inboundBuffer = PlatformDependent.newSpscQueue();
    private final Runnable readTask = new Runnable((LocalChannel)this){
        final /* synthetic */ LocalChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            if (this.this$0.inboundBuffer.isEmpty()) return;
            LocalChannel.access$000((LocalChannel)this.this$0);
        }
    };
    private final Runnable shutdownHook = new Runnable((LocalChannel)this){
        final /* synthetic */ LocalChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            this.this$0.unsafe().close((ChannelPromise)this.this$0.unsafe().voidPromise());
        }
    };
    private volatile State state;
    private volatile LocalChannel peer;
    private volatile LocalAddress localAddress;
    private volatile LocalAddress remoteAddress;
    private volatile ChannelPromise connectPromise;
    private volatile boolean readInProgress;
    private volatile boolean writeInProgress;
    private volatile Future<?> finishReadFuture;

    public LocalChannel() {
        super(null);
        this.config().setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator((ByteBufAllocator)this.config.getAllocator()));
    }

    protected LocalChannel(LocalServerChannel parent, LocalChannel peer) {
        super((Channel)parent);
        this.config().setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator((ByteBufAllocator)this.config.getAllocator()));
        this.peer = peer;
        this.localAddress = parent.localAddress();
        this.remoteAddress = peer.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public LocalServerChannel parent() {
        return (LocalServerChannel)super.parent();
    }

    @Override
    public LocalAddress localAddress() {
        return (LocalAddress)super.localAddress();
    }

    @Override
    public LocalAddress remoteAddress() {
        return (LocalAddress)super.remoteAddress();
    }

    @Override
    public boolean isOpen() {
        if (this.state == State.CLOSED) return false;
        return true;
    }

    @Override
    public boolean isActive() {
        if (this.state != State.CONNECTED) return false;
        return true;
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new LocalUnsafe((LocalChannel)this, null);
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof SingleThreadEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.localAddress;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.remoteAddress;
    }

    @Override
    protected void doRegister() throws Exception {
        if (this.peer != null && this.parent() != null) {
            LocalChannel peer = this.peer;
            this.state = State.CONNECTED;
            peer.remoteAddress = this.parent() == null ? null : this.parent().localAddress();
            peer.state = State.CONNECTED;
            peer.eventLoop().execute((Runnable)new Runnable((LocalChannel)this, (LocalChannel)peer){
                final /* synthetic */ LocalChannel val$peer;
                final /* synthetic */ LocalChannel this$0;
                {
                    this.this$0 = this$0;
                    this.val$peer = localChannel;
                }

                public void run() {
                    ChannelPromise promise = LocalChannel.access$200((LocalChannel)this.val$peer);
                    if (promise == null) return;
                    if (!promise.trySuccess()) return;
                    this.val$peer.pipeline().fireChannelActive();
                }
            });
        }
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).addShutdownHook((Runnable)this.shutdownHook);
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.localAddress = LocalChannelRegistry.register((Channel)this, (LocalAddress)this.localAddress, (SocketAddress)localAddress);
        this.state = State.BOUND;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        LocalChannel peer = this.peer;
        State oldState = this.state;
        try {
            if (oldState != State.CLOSED) {
                ChannelPromise promise;
                if (this.localAddress != null) {
                    if (this.parent() == null) {
                        LocalChannelRegistry.unregister((LocalAddress)this.localAddress);
                    }
                    this.localAddress = null;
                }
                this.state = State.CLOSED;
                if (this.writeInProgress && peer != null) {
                    this.finishPeerRead((LocalChannel)peer);
                }
                if ((promise = this.connectPromise) != null) {
                    promise.tryFailure((Throwable)new ClosedChannelException());
                    this.connectPromise = null;
                }
            }
            if (peer == null) return;
            this.peer = null;
            EventLoop peerEventLoop = peer.eventLoop();
            boolean peerIsActive = peer.isActive();
            try {
                peerEventLoop.execute((Runnable)new Runnable((LocalChannel)this, (LocalChannel)peer, (boolean)peerIsActive){
                    final /* synthetic */ LocalChannel val$peer;
                    final /* synthetic */ boolean val$peerIsActive;
                    final /* synthetic */ LocalChannel this$0;
                    {
                        this.this$0 = this$0;
                        this.val$peer = localChannel;
                        this.val$peerIsActive = bl;
                    }

                    public void run() {
                        LocalChannel.access$300((LocalChannel)this.val$peer, (boolean)this.val$peerIsActive);
                    }
                });
                return;
            }
            catch (Throwable cause) {
                logger.warn((String)"Releasing Inbound Queues for channels {}-{} because exception occurred!", (Object[])new Object[]{this, peer, cause});
                if (peerEventLoop.inEventLoop()) {
                    peer.releaseInboundBuffers();
                } else {
                    peer.close();
                }
                PlatformDependent.throwException((Throwable)cause);
                return;
            }
        }
        finally {
            if (oldState != null && oldState != State.CLOSED) {
                this.releaseInboundBuffers();
            }
        }
    }

    private void tryClose(boolean isActive) {
        if (isActive) {
            this.unsafe().close((ChannelPromise)this.unsafe().voidPromise());
            return;
        }
        this.releaseInboundBuffers();
    }

    @Override
    protected void doDeregister() throws Exception {
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).removeShutdownHook((Runnable)this.shutdownHook);
    }

    private void readInbound() {
        Object received;
        RecvByteBufAllocator.Handle handle = this.unsafe().recvBufAllocHandle();
        handle.reset((ChannelConfig)this.config());
        ChannelPipeline pipeline = this.pipeline();
        while ((received = this.inboundBuffer.poll()) != null) {
            pipeline.fireChannelRead((Object)received);
            if (handle.continueReading()) continue;
        }
        pipeline.fireChannelReadComplete();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doBeginRead() throws Exception {
        if (this.readInProgress) {
            return;
        }
        Queue<Object> inboundBuffer = this.inboundBuffer;
        if (inboundBuffer.isEmpty()) {
            this.readInProgress = true;
            return;
        }
        InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
        Integer stackDepth = Integer.valueOf((int)threadLocals.localChannelReaderStackDepth());
        if (stackDepth.intValue() < 8) {
            threadLocals.setLocalChannelReaderStackDepth((int)(stackDepth.intValue() + 1));
            try {
                this.readInbound();
                return;
            }
            finally {
                threadLocals.setLocalChannelReaderStackDepth((int)stackDepth.intValue());
            }
        }
        try {
            this.eventLoop().execute((Runnable)this.readTask);
            return;
        }
        catch (Throwable cause) {
            logger.warn((String)"Closing Local channels {}-{} because exception occurred!", (Object[])new Object[]{this, this.peer, cause});
            this.close();
            this.peer.close();
            PlatformDependent.throwException((Throwable)cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        switch (this.state) {
            case OPEN: 
            case BOUND: {
                throw new NotYetConnectedException();
            }
            case CLOSED: {
                throw new ClosedChannelException();
            }
        }
        LocalChannel peer = this.peer;
        this.writeInProgress = true;
        try {
            Object msg;
            ClosedChannelException exception = null;
            while ((msg = in.current()) != null) {
                try {
                    if (peer.state == State.CONNECTED) {
                        peer.inboundBuffer.add((Object)ReferenceCountUtil.retain(msg));
                        in.remove();
                        continue;
                    }
                    if (exception == null) {
                        exception = new ClosedChannelException();
                    }
                    in.remove(exception);
                }
                catch (Throwable cause) {
                    in.remove((Throwable)cause);
                }
            }
        }
        finally {
            this.writeInProgress = false;
        }
        this.finishPeerRead((LocalChannel)peer);
    }

    private void finishPeerRead(LocalChannel peer) {
        if (peer.eventLoop() == this.eventLoop() && !peer.writeInProgress) {
            this.finishPeerRead0((LocalChannel)peer);
            return;
        }
        this.runFinishPeerReadTask((LocalChannel)peer);
    }

    private void runFinishPeerReadTask(LocalChannel peer) {
        Runnable finishPeerReadTask = new Runnable((LocalChannel)this, (LocalChannel)peer){
            final /* synthetic */ LocalChannel val$peer;
            final /* synthetic */ LocalChannel this$0;
            {
                this.this$0 = this$0;
                this.val$peer = localChannel;
            }

            public void run() {
                LocalChannel.access$400((LocalChannel)this.this$0, (LocalChannel)this.val$peer);
            }
        };
        try {
            if (peer.writeInProgress) {
                peer.finishReadFuture = peer.eventLoop().submit((Runnable)finishPeerReadTask);
                return;
            }
            peer.eventLoop().execute((Runnable)finishPeerReadTask);
            return;
        }
        catch (Throwable cause) {
            logger.warn((String)"Closing Local channels {}-{} because exception occurred!", (Object[])new Object[]{this, peer, cause});
            this.close();
            peer.close();
            PlatformDependent.throwException((Throwable)cause);
        }
    }

    private void releaseInboundBuffers() {
        Object msg;
        assert (this.eventLoop() == null || this.eventLoop().inEventLoop());
        this.readInProgress = false;
        Queue<Object> inboundBuffer = this.inboundBuffer;
        while ((msg = inboundBuffer.poll()) != null) {
            ReferenceCountUtil.release((Object)msg);
        }
    }

    private void finishPeerRead0(LocalChannel peer) {
        Future<?> peerFinishReadFuture = peer.finishReadFuture;
        if (peerFinishReadFuture != null) {
            if (!peerFinishReadFuture.isDone()) {
                this.runFinishPeerReadTask((LocalChannel)peer);
                return;
            }
            FINISH_READ_FUTURE_UPDATER.compareAndSet((LocalChannel)peer, peerFinishReadFuture, null);
        }
        if (!peer.readInProgress) return;
        if (peer.inboundBuffer.isEmpty()) return;
        peer.readInProgress = false;
        peer.readInbound();
    }

    static /* synthetic */ void access$000(LocalChannel x0) {
        x0.readInbound();
    }

    static /* synthetic */ ChannelPromise access$200(LocalChannel x0) {
        return x0.connectPromise;
    }

    static /* synthetic */ void access$300(LocalChannel x0, boolean x1) {
        x0.tryClose((boolean)x1);
    }

    static /* synthetic */ void access$400(LocalChannel x0, LocalChannel x1) {
        x0.finishPeerRead0((LocalChannel)x1);
    }

    static /* synthetic */ State access$500(LocalChannel x0) {
        return x0.state;
    }

    static /* synthetic */ ChannelPromise access$202(LocalChannel x0, ChannelPromise x1) {
        x0.connectPromise = x1;
        return x0.connectPromise;
    }

    static /* synthetic */ LocalChannel access$602(LocalChannel x0, LocalChannel x1) {
        x0.peer = x1;
        return x0.peer;
    }
}

