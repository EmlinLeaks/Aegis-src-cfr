/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.WrappingExecutorService;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CanIgnoreReturnValue
@GwtIncompatible
abstract class WrappingExecutorService
implements ExecutorService {
    private final ExecutorService delegate;

    protected WrappingExecutorService(ExecutorService delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    protected abstract <T> Callable<T> wrapTask(Callable<T> var1);

    protected Runnable wrapTask(Runnable command) {
        Callable<Object> wrapped = this.wrapTask(Executors.callable((Runnable)command, null));
        return new Runnable((WrappingExecutorService)this, wrapped){
            final /* synthetic */ Callable val$wrapped;
            final /* synthetic */ WrappingExecutorService this$0;
            {
                this.this$0 = wrappingExecutorService;
                this.val$wrapped = callable;
            }

            public void run() {
                try {
                    this.val$wrapped.call();
                    return;
                }
                catch (java.lang.Exception e) {
                    com.google.common.base.Throwables.throwIfUnchecked((java.lang.Throwable)e);
                    throw new java.lang.RuntimeException((java.lang.Throwable)e);
                }
            }
        };
    }

    private final <T> ImmutableList<Callable<T>> wrapTasks(Collection<? extends Callable<T>> tasks) {
        ImmutableList.Builder<E> builder = ImmutableList.builder();
        Iterator<Callable<T>> i$ = tasks.iterator();
        while (i$.hasNext()) {
            Callable<T> task = i$.next();
            builder.add(this.wrapTask(task));
        }
        return builder.build();
    }

    @Override
    public final void execute(Runnable command) {
        this.delegate.execute((Runnable)this.wrapTask((Runnable)command));
    }

    @Override
    public final <T> Future<T> submit(Callable<T> task) {
        return this.delegate.submit(this.wrapTask(Preconditions.checkNotNull(task)));
    }

    @Override
    public final Future<?> submit(Runnable task) {
        return this.delegate.submit((Runnable)this.wrapTask((Runnable)task));
    }

    @Override
    public final <T> Future<T> submit(Runnable task, T result) {
        return this.delegate.submit((Runnable)this.wrapTask((Runnable)task), result);
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(this.wrapTasks(tasks));
    }

    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(this.wrapTasks(tasks), (long)timeout, (TimeUnit)unit);
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return (T)this.delegate.invokeAny(this.wrapTasks(tasks));
    }

    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (T)this.delegate.invokeAny(this.wrapTasks(tasks), (long)timeout, (TimeUnit)unit);
    }

    @Override
    public final void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    public final List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    @Override
    public final boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    @Override
    public final boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    @Override
    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination((long)timeout, (TimeUnit)unit);
    }
}

