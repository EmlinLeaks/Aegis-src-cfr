/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class SingleThreadEventLoop
extends SingleThreadEventExecutor
implements EventLoop {
    protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max((int)16, (int)SystemPropertyUtil.getInt((String)"io.netty.eventLoop.maxPendingTasks", (int)Integer.MAX_VALUE));
    private final Queue<Runnable> tailTasks;

    protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        this((EventLoopGroup)parent, (ThreadFactory)threadFactory, (boolean)addTaskWakesUp, (int)DEFAULT_MAX_PENDING_TASKS, (RejectedExecutionHandler)RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp) {
        this((EventLoopGroup)parent, (Executor)executor, (boolean)addTaskWakesUp, (int)DEFAULT_MAX_PENDING_TASKS, (RejectedExecutionHandler)RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, (ThreadFactory)threadFactory, (boolean)addTaskWakesUp, (int)maxPendingTasks, (RejectedExecutionHandler)rejectedExecutionHandler);
        this.tailTasks = this.newTaskQueue((int)maxPendingTasks);
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, (Executor)executor, (boolean)addTaskWakesUp, (int)maxPendingTasks, (RejectedExecutionHandler)rejectedExecutionHandler);
        this.tailTasks = this.newTaskQueue((int)maxPendingTasks);
    }

    protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, Queue<Runnable> tailTaskQueue, RejectedExecutionHandler rejectedExecutionHandler) {
        super((EventExecutorGroup)parent, (Executor)executor, (boolean)addTaskWakesUp, taskQueue, (RejectedExecutionHandler)rejectedExecutionHandler);
        this.tailTasks = ObjectUtil.checkNotNull(tailTaskQueue, (String)"tailTaskQueue");
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup)super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop)super.next();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.register((ChannelPromise)new DefaultChannelPromise((Channel)channel, (EventExecutor)this));
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        ObjectUtil.checkNotNull(promise, (String)"promise");
        promise.channel().unsafe().register((EventLoop)this, (ChannelPromise)promise);
        return promise;
    }

    @Deprecated
    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        if (channel == null) {
            throw new NullPointerException((String)"channel");
        }
        if (promise == null) {
            throw new NullPointerException((String)"promise");
        }
        channel.unsafe().register((EventLoop)this, (ChannelPromise)promise);
        return promise;
    }

    public final void executeAfterEventLoopIteration(Runnable task) {
        ObjectUtil.checkNotNull(task, (String)"task");
        if (this.isShutdown()) {
            SingleThreadEventLoop.reject();
        }
        if (!this.tailTasks.offer((Runnable)task)) {
            this.reject((Runnable)task);
        }
        if (!this.wakesUpForTask((Runnable)task)) return;
        this.wakeup((boolean)this.inEventLoop());
    }

    final boolean removeAfterEventLoopIterationTask(Runnable task) {
        return this.tailTasks.remove((Object)ObjectUtil.checkNotNull(task, (String)"task"));
    }

    @Override
    protected void afterRunningAllTasks() {
        this.runAllTasksFrom(this.tailTasks);
    }

    @Override
    protected boolean hasTasks() {
        if (super.hasTasks()) return true;
        if (!this.tailTasks.isEmpty()) return true;
        return false;
    }

    @Override
    public int pendingTasks() {
        return super.pendingTasks() + this.tailTasks.size();
    }

    public int registeredChannels() {
        return -1;
    }
}

