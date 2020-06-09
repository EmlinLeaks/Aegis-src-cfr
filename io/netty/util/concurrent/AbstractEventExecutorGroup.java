/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractEventExecutorGroup
implements EventExecutorGroup {
    @Override
    public Future<?> submit(Runnable task) {
        return this.next().submit((Runnable)task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.next().submit((Runnable)task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.next().submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.next().schedule((Runnable)command, (long)delay, (TimeUnit)unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.next().schedule(callable, (long)delay, (TimeUnit)unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.next().scheduleAtFixedRate((Runnable)command, (long)initialDelay, (long)period, (TimeUnit)unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.next().scheduleWithFixedDelay((Runnable)command, (long)initialDelay, (long)delay, (TimeUnit)unit);
    }

    @Override
    public Future<?> shutdownGracefully() {
        return this.shutdownGracefully((long)2L, (long)15L, (TimeUnit)TimeUnit.SECONDS);
    }

    @Deprecated
    @Override
    public abstract void shutdown();

    @Deprecated
    @Override
    public List<Runnable> shutdownNow() {
        this.shutdown();
        return Collections.emptyList();
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.next().invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.next().invokeAll(tasks, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return (T)this.next().invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (T)this.next().invokeAny(tasks, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public void execute(Runnable command) {
        this.next().execute((Runnable)command);
    }
}

