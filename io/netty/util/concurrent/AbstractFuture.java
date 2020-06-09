/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFuture<V>
implements Future<V> {
    @Override
    public V get() throws InterruptedException, ExecutionException {
        this.await();
        Throwable cause = this.cause();
        if (cause == null) {
            return (V)this.getNow();
        }
        if (!(cause instanceof CancellationException)) throw new ExecutionException((Throwable)cause);
        throw (CancellationException)cause;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!this.await((long)timeout, (TimeUnit)unit)) throw new TimeoutException();
        Throwable cause = this.cause();
        if (cause == null) {
            return (V)this.getNow();
        }
        if (!(cause instanceof CancellationException)) throw new ExecutionException((Throwable)cause);
        throw (CancellationException)cause;
    }
}

