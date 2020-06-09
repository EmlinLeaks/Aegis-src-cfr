/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.SucceededFuture;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class UnorderedThreadPoolEventExecutor
extends ScheduledThreadPoolExecutor
implements EventExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnorderedThreadPoolEventExecutor.class);
    private final Promise<?> terminationFuture = GlobalEventExecutor.INSTANCE.newPromise();
    private final Set<EventExecutor> executorSet = Collections.singleton(this);

    public UnorderedThreadPoolEventExecutor(int corePoolSize) {
        this((int)corePoolSize, (ThreadFactory)new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class));
    }

    public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super((int)corePoolSize, (ThreadFactory)threadFactory);
    }

    public UnorderedThreadPoolEventExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        this((int)corePoolSize, (ThreadFactory)new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class), (RejectedExecutionHandler)handler);
    }

    public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super((int)corePoolSize, (ThreadFactory)threadFactory, (RejectedExecutionHandler)handler);
    }

    @Override
    public EventExecutor next() {
        return this;
    }

    @Override
    public EventExecutorGroup parent() {
        return this;
    }

    @Override
    public boolean inEventLoop() {
        return false;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return false;
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new DefaultPromise<V>((EventExecutor)this);
    }

    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new DefaultProgressivePromise<V>((EventExecutor)this);
    }

    @Override
    public <V> Future<V> newSucceededFuture(V result) {
        return new SucceededFuture<V>((EventExecutor)this, result);
    }

    @Override
    public <V> Future<V> newFailedFuture(Throwable cause) {
        return new FailedFuture<V>((EventExecutor)this, (Throwable)cause);
    }

    @Override
    public boolean isShuttingDown() {
        return this.isShutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks = super.shutdownNow();
        this.terminationFuture.trySuccess(null);
        return tasks;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        this.terminationFuture.trySuccess(null);
    }

    @Override
    public Future<?> shutdownGracefully() {
        return this.shutdownGracefully((long)2L, (long)15L, (TimeUnit)TimeUnit.SECONDS);
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        this.shutdown();
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return this.executorSet.iterator();
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        RunnableScheduledFutureTask<V> runnableScheduledFutureTask;
        if (runnable instanceof NonNotifyRunnable) {
            runnableScheduledFutureTask = task;
            return runnableScheduledFutureTask;
        }
        runnableScheduledFutureTask = new RunnableScheduledFutureTask<V>((EventExecutor)this, (Runnable)runnable, task);
        return runnableScheduledFutureTask;
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        return new RunnableScheduledFutureTask<V>((EventExecutor)this, callable, task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return (ScheduledFuture)super.schedule((Runnable)command, (long)delay, (TimeUnit)unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return (ScheduledFuture)super.schedule(callable, (long)delay, (TimeUnit)unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return (ScheduledFuture)super.scheduleAtFixedRate((Runnable)command, (long)initialDelay, (long)period, (TimeUnit)unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return (ScheduledFuture)super.scheduleWithFixedDelay((Runnable)command, (long)initialDelay, (long)delay, (TimeUnit)unit);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return (Future)super.submit((Runnable)task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return (Future)super.submit((Runnable)task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return (Future)super.submit(task);
    }

    @Override
    public void execute(Runnable command) {
        super.schedule((Runnable)new NonNotifyRunnable((Runnable)command), (long)0L, (TimeUnit)TimeUnit.NANOSECONDS);
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }
}

