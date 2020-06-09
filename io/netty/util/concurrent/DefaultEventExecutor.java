/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class DefaultEventExecutor
extends SingleThreadEventExecutor {
    public DefaultEventExecutor() {
        this((EventExecutorGroup)((EventExecutorGroup)null));
    }

    public DefaultEventExecutor(ThreadFactory threadFactory) {
        this(null, (ThreadFactory)threadFactory);
    }

    public DefaultEventExecutor(Executor executor) {
        this(null, (Executor)executor);
    }

    public DefaultEventExecutor(EventExecutorGroup parent) {
        this((EventExecutorGroup)parent, (ThreadFactory)new DefaultThreadFactory(DefaultEventExecutor.class));
    }

    public DefaultEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory) {
        super((EventExecutorGroup)parent, (ThreadFactory)threadFactory, (boolean)true);
    }

    public DefaultEventExecutor(EventExecutorGroup parent, Executor executor) {
        super((EventExecutorGroup)parent, (Executor)executor, (boolean)true);
    }

    public DefaultEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, (ThreadFactory)threadFactory, (boolean)true, (int)maxPendingTasks, (RejectedExecutionHandler)rejectedExecutionHandler);
    }

    public DefaultEventExecutor(EventExecutorGroup parent, Executor executor, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, (Executor)executor, (boolean)true, (int)maxPendingTasks, (RejectedExecutionHandler)rejectedExecutionHandler);
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

