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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingFuture<V>
extends ForwardingObject
implements Future<V> {
    protected ForwardingFuture() {
    }

    @Override
    protected abstract Future<? extends V> delegate();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.delegate().cancel((boolean)mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.delegate().isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.delegate().isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return (V)this.delegate().get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (V)this.delegate().get((long)timeout, (TimeUnit)unit);
    }
}

