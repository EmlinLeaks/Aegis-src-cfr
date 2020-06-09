/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingObject;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingExecutorService
extends ForwardingObject
implements ExecutorService {
    protected ForwardingExecutorService() {
    }

    @Override
    protected abstract ExecutorService delegate();

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().awaitTermination((long)timeout, (TimeUnit)unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate().invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().invokeAll(tasks, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return (T)this.delegate().invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (T)this.delegate().invokeAny(tasks, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public boolean isShutdown() {
        return this.delegate().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.delegate().isTerminated();
    }

    @Override
    public void shutdown() {
        this.delegate().shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.delegate().shutdownNow();
    }

    @Override
    public void execute(Runnable command) {
        this.delegate().execute((Runnable)command);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.delegate().submit(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.delegate().submit((Runnable)task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.delegate().submit((Runnable)task, result);
    }
}

