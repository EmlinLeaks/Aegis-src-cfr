/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventExecutorGroup
extends MultithreadEventExecutorGroup {
    public DefaultEventExecutorGroup(int nThreads) {
        this((int)nThreads, null);
    }

    public DefaultEventExecutorGroup(int nThreads, ThreadFactory threadFactory) {
        this((int)nThreads, (ThreadFactory)threadFactory, (int)SingleThreadEventExecutor.DEFAULT_MAX_PENDING_EXECUTOR_TASKS, (RejectedExecutionHandler)RejectedExecutionHandlers.reject());
    }

    public DefaultEventExecutorGroup(int nThreads, ThreadFactory threadFactory, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        super((int)nThreads, (ThreadFactory)threadFactory, (Object[])new Object[]{Integer.valueOf((int)maxPendingTasks), rejectedHandler});
    }

    @Override
    protected EventExecutor newChild(Executor executor, Object ... args) throws Exception {
        return new DefaultEventExecutor((EventExecutorGroup)this, (Executor)executor, (int)((Integer)args[0]).intValue(), (RejectedExecutionHandler)((RejectedExecutionHandler)args[1]));
    }
}

