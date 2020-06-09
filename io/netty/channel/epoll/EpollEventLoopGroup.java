/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class EpollEventLoopGroup
extends MultithreadEventLoopGroup {
    public EpollEventLoopGroup() {
        this((int)0);
    }

    public EpollEventLoopGroup(int nThreads) {
        this((int)nThreads, (ThreadFactory)((ThreadFactory)null));
    }

    public EpollEventLoopGroup(int nThreads, SelectStrategyFactory selectStrategyFactory) {
        this((int)nThreads, (ThreadFactory)((ThreadFactory)null), (SelectStrategyFactory)selectStrategyFactory);
    }

    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        this((int)nThreads, (ThreadFactory)threadFactory, (int)0);
    }

    public EpollEventLoopGroup(int nThreads, Executor executor) {
        this((int)nThreads, (Executor)executor, (SelectStrategyFactory)DefaultSelectStrategyFactory.INSTANCE);
    }

    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectStrategyFactory selectStrategyFactory) {
        this((int)nThreads, (ThreadFactory)threadFactory, (int)0, (SelectStrategyFactory)selectStrategyFactory);
    }

    @Deprecated
    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce) {
        this((int)nThreads, (ThreadFactory)threadFactory, (int)maxEventsAtOnce, (SelectStrategyFactory)DefaultSelectStrategyFactory.INSTANCE);
    }

    @Deprecated
    public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce, SelectStrategyFactory selectStrategyFactory) {
        super((int)nThreads, (ThreadFactory)threadFactory, (Object[])new Object[]{Integer.valueOf((int)maxEventsAtOnce), selectStrategyFactory, RejectedExecutionHandlers.reject()});
        Epoll.ensureAvailability();
    }

    public EpollEventLoopGroup(int nThreads, Executor executor, SelectStrategyFactory selectStrategyFactory) {
        super((int)nThreads, (Executor)executor, (Object[])new Object[]{Integer.valueOf((int)0), selectStrategyFactory, RejectedExecutionHandlers.reject()});
        Epoll.ensureAvailability();
    }

    public EpollEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectStrategyFactory selectStrategyFactory) {
        super((int)nThreads, (Executor)executor, (EventExecutorChooserFactory)chooserFactory, (Object[])new Object[]{Integer.valueOf((int)0), selectStrategyFactory, RejectedExecutionHandlers.reject()});
        Epoll.ensureAvailability();
    }

    public EpollEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectStrategyFactory selectStrategyFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super((int)nThreads, (Executor)executor, (EventExecutorChooserFactory)chooserFactory, (Object[])new Object[]{Integer.valueOf((int)0), selectStrategyFactory, rejectedExecutionHandler});
        Epoll.ensureAvailability();
    }

    public EpollEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectStrategyFactory selectStrategyFactory, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory queueFactory) {
        super((int)nThreads, (Executor)executor, (EventExecutorChooserFactory)chooserFactory, (Object[])new Object[]{Integer.valueOf((int)0), selectStrategyFactory, rejectedExecutionHandler, queueFactory});
        Epoll.ensureAvailability();
    }

    @Deprecated
    public void setIoRatio(int ioRatio) {
        if (ioRatio <= 0) throw new IllegalArgumentException((String)("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)"));
        if (ioRatio <= 100) return;
        throw new IllegalArgumentException((String)("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)"));
    }

    @Override
    protected EventLoop newChild(Executor executor, Object ... args) throws Exception {
        EventLoopTaskQueueFactory queueFactory = args.length == 4 ? (EventLoopTaskQueueFactory)args[3] : null;
        return new EpollEventLoop((EventLoopGroup)this, (Executor)executor, (int)((Integer)args[0]).intValue(), (SelectStrategy)((SelectStrategyFactory)args[1]).newSelectStrategy(), (RejectedExecutionHandler)((RejectedExecutionHandler)args[2]), (EventLoopTaskQueueFactory)queueFactory);
    }
}

