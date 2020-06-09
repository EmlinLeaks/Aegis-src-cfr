/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.local;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractServerChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalChannelRegistry;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;

public class LocalServerChannel
extends AbstractServerChannel {
    private final ChannelConfig config = new DefaultChannelConfig((Channel)this);
    private final Queue<Object> inboundBuffer = new ArrayDeque<Object>();
    private final Runnable shutdownHook = new Runnable((LocalServerChannel)this){
        final /* synthetic */ LocalServerChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            this.this$0.unsafe().close((io.netty.channel.ChannelPromise)this.this$0.unsafe().voidPromise());
        }
    };
    private volatile int state;
    private volatile LocalAddress localAddress;
    private volatile boolean acceptInProgress;

    public LocalServerChannel() {
        this.config().setAllocator((ByteBufAllocator)new PreferHeapByteBufAllocator((ByteBufAllocator)this.config.getAllocator()));
    }

    @Override
    public ChannelConfig config() {
        return this.config;
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
        if (this.state >= 2) return false;
        return true;
    }

    @Override
    public boolean isActive() {
        if (this.state != 1) return false;
        return true;
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
    protected void doRegister() throws Exception {
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).addShutdownHook((Runnable)this.shutdownHook);
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.localAddress = LocalChannelRegistry.register((Channel)this, (LocalAddress)this.localAddress, (SocketAddress)localAddress);
        this.state = 1;
    }

    @Override
    protected void doClose() throws Exception {
        if (this.state > 1) return;
        if (this.localAddress != null) {
            LocalChannelRegistry.unregister((LocalAddress)this.localAddress);
            this.localAddress = null;
        }
        this.state = 2;
    }

    @Override
    protected void doDeregister() throws Exception {
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).removeShutdownHook((Runnable)this.shutdownHook);
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (this.acceptInProgress) {
            return;
        }
        Queue<Object> inboundBuffer = this.inboundBuffer;
        if (inboundBuffer.isEmpty()) {
            this.acceptInProgress = true;
            return;
        }
        this.readInbound();
    }

    LocalChannel serve(LocalChannel peer) {
        LocalChannel child = this.newLocalChannel((LocalChannel)peer);
        if (this.eventLoop().inEventLoop()) {
            this.serve0((LocalChannel)child);
            return child;
        }
        this.eventLoop().execute((Runnable)new Runnable((LocalServerChannel)this, (LocalChannel)child){
            final /* synthetic */ LocalChannel val$child;
            final /* synthetic */ LocalServerChannel this$0;
            {
                this.this$0 = this$0;
                this.val$child = localChannel;
            }

            public void run() {
                LocalServerChannel.access$000((LocalServerChannel)this.this$0, (LocalChannel)this.val$child);
            }
        });
        return child;
    }

    private void readInbound() {
        Object m;
        RecvByteBufAllocator.Handle handle = this.unsafe().recvBufAllocHandle();
        handle.reset((ChannelConfig)this.config());
        ChannelPipeline pipeline = this.pipeline();
        while ((m = this.inboundBuffer.poll()) != null) {
            pipeline.fireChannelRead((Object)m);
            if (handle.continueReading()) continue;
        }
        pipeline.fireChannelReadComplete();
    }

    protected LocalChannel newLocalChannel(LocalChannel peer) {
        return new LocalChannel((LocalServerChannel)this, (LocalChannel)peer);
    }

    private void serve0(LocalChannel child) {
        this.inboundBuffer.add((Object)child);
        if (!this.acceptInProgress) return;
        this.acceptInProgress = false;
        this.readInbound();
    }

    static /* synthetic */ void access$000(LocalServerChannel x0, LocalChannel x1) {
        x0.serve0((LocalChannel)x1);
    }
}

