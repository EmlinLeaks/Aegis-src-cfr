/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoop
extends SingleThreadEventLoop {
    public DefaultEventLoop() {
        this((EventLoopGroup)((EventLoopGroup)null));
    }

    public DefaultEventLoop(ThreadFactory threadFactory) {
        this(null, (ThreadFactory)threadFactory);
    }

    public DefaultEventLoop(Executor executor) {
        this(null, (Executor)executor);
    }

    public DefaultEventLoop(EventLoopGroup parent) {
        this((EventLoopGroup)parent, (ThreadFactory)new DefaultThreadFactory(DefaultEventLoop.class));
    }

    public DefaultEventLoop(EventLoopGroup parent, ThreadFactory threadFactory) {
        super((EventLoopGroup)parent, (ThreadFactory)threadFactory, (boolean)true);
    }

    public DefaultEventLoop(EventLoopGroup parent, Executor executor) {
        super((EventLoopGroup)parent, (Executor)executor, (boolean)true);
    }

    @Override
    protected void run() {
        do {
            Runnable task;
            if ((task = this.takeTask()) == null) continue;
            task.run();
            this.updateLastExecutionTime();
        } while (!this.confirmShutdown());
    }
}

