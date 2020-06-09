/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoopGroup
extends MultithreadEventLoopGroup {
    public DefaultEventLoopGroup() {
        this((int)0);
    }

    public DefaultEventLoopGroup(int nThreads) {
        this((int)nThreads, (ThreadFactory)((ThreadFactory)null));
    }

    public DefaultEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        super((int)nThreads, (ThreadFactory)threadFactory, (Object[])new Object[0]);
    }

    public DefaultEventLoopGroup(int nThreads, Executor executor) {
        super((int)nThreads, (Executor)executor, (Object[])new Object[0]);
    }

    @Override
    protected EventLoop newChild(Executor executor, Object ... args) throws Exception {
        return new DefaultEventLoop((EventLoopGroup)this, (Executor)executor);
    }
}

