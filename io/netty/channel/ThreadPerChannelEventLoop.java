/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import io.netty.channel.ThreadPerChannelEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;

@Deprecated
public class ThreadPerChannelEventLoop
extends SingleThreadEventLoop {
    private final ThreadPerChannelEventLoopGroup parent;
    private Channel ch;

    public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup parent) {
        super((EventLoopGroup)parent, (Executor)parent.executor, (boolean)true);
        this.parent = parent;
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return super.register((ChannelPromise)promise).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((ThreadPerChannelEventLoop)this){
            final /* synthetic */ ThreadPerChannelEventLoop this$0;
            {
                this.this$0 = this$0;
            }

            public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                if (future.isSuccess()) {
                    ThreadPerChannelEventLoop.access$002((ThreadPerChannelEventLoop)this.this$0, (Channel)future.channel());
                    return;
                }
                this.this$0.deregister();
            }
        });
    }

    @Deprecated
    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return super.register((Channel)channel, (ChannelPromise)promise).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((ThreadPerChannelEventLoop)this){
            final /* synthetic */ ThreadPerChannelEventLoop this$0;
            {
                this.this$0 = this$0;
            }

            public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                if (future.isSuccess()) {
                    ThreadPerChannelEventLoop.access$002((ThreadPerChannelEventLoop)this.this$0, (Channel)future.channel());
                    return;
                }
                this.this$0.deregister();
            }
        });
    }

    @Override
    protected void run() {
        do {
            Runnable task;
            if ((task = this.takeTask()) != null) {
                task.run();
                this.updateLastExecutionTime();
            }
            Channel ch = this.ch;
            if (this.isShuttingDown()) {
                if (ch != null) {
                    ch.unsafe().close((ChannelPromise)ch.unsafe().voidPromise());
                }
                if (!this.confirmShutdown()) continue;
                return;
            }
            if (ch == null || ch.isRegistered()) continue;
            this.runAllTasks();
            this.deregister();
        } while (true);
    }

    protected void deregister() {
        this.ch = null;
        this.parent.activeChildren.remove((Object)this);
        this.parent.idleChildren.add((EventLoop)this);
    }

    @Override
    public int registeredChannels() {
        return 1;
    }

    static /* synthetic */ Channel access$002(ThreadPerChannelEventLoop x0, Channel x1) {
        x0.ch = x1;
        return x0.ch;
    }
}

