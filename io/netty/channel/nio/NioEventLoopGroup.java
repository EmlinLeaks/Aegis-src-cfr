/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.nio;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class NioEventLoopGroup
extends MultithreadEventLoopGroup {
    public NioEventLoopGroup() {
        this((int)0);
    }

    public NioEventLoopGroup(int nThreads) {
        this((int)nThreads, (Executor)((Executor)null));
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        this((int)nThreads, (ThreadFactory)threadFactory, (SelectorProvider)SelectorProvider.provider());
    }

    public NioEventLoopGroup(int nThreads, Executor executor) {
        this((int)nThreads, (Executor)executor, (SelectorProvider)SelectorProvider.provider());
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectorProvider selectorProvider) {
        this((int)nThreads, (ThreadFactory)threadFactory, (SelectorProvider)selectorProvider, (SelectStrategyFactory)DefaultSelectStrategyFactory.INSTANCE);
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
        super((int)nThreads, (ThreadFactory)threadFactory, (Object[])new Object[]{selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject()});
    }

    public NioEventLoopGroup(int nThreads, Executor executor, SelectorProvider selectorProvider) {
        this((int)nThreads, (Executor)executor, (SelectorProvider)selectorProvider, (SelectStrategyFactory)DefaultSelectStrategyFactory.INSTANCE);
    }

    public NioEventLoopGroup(int nThreads, Executor executor, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
        super((int)nThreads, (Executor)executor, (Object[])new Object[]{selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject()});
    }

    public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
        super((int)nThreads, (Executor)executor, (EventExecutorChooserFactory)chooserFactory, (Object[])new Object[]{selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject()});
    }

    public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super((int)nThreads, (Executor)executor, (EventExecutorChooserFactory)chooserFactory, (Object[])new Object[]{selectorProvider, selectStrategyFactory, rejectedExecutionHandler});
    }

    public NioEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory taskQueueFactory) {
        super((int)nThreads, (Executor)executor, (EventExecutorChooserFactory)chooserFactory, (Object[])new Object[]{selectorProvider, selectStrategyFactory, rejectedExecutionHandler, taskQueueFactory});
    }

    public void setIoRatio(int ioRatio) {
        Iterator<EventExecutor> iterator = this.iterator();
        while (iterator.hasNext()) {
            EventExecutor e = iterator.next();
            ((NioEventLoop)e).setIoRatio((int)ioRatio);
        }
    }

    public void rebuildSelectors() {
        Iterator<EventExecutor> iterator = this.iterator();
        while (iterator.hasNext()) {
            EventExecutor e = iterator.next();
            ((NioEventLoop)e).rebuildSelector();
        }
    }

    @Override
    protected EventLoop newChild(Executor executor, Object ... args) throws Exception {
        EventLoopTaskQueueFactory queueFactory = args.length == 4 ? (EventLoopTaskQueueFactory)args[3] : null;
        return new NioEventLoop((NioEventLoopGroup)this, (Executor)executor, (SelectorProvider)((SelectorProvider)args[0]), (SelectStrategy)((SelectStrategyFactory)args[1]).newSelectStrategy(), (RejectedExecutionHandler)((RejectedExecutionHandler)args[2]), (EventLoopTaskQueueFactory)queueFactory);
    }
}

