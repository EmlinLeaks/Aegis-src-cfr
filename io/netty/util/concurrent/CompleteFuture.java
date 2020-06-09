/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.TimeUnit;

public abstract class CompleteFuture<V>
extends AbstractFuture<V> {
    private final EventExecutor executor;

    protected CompleteFuture(EventExecutor executor) {
        this.executor = executor;
    }

    protected EventExecutor executor() {
        return this.executor;
    }

    @Override
    public Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        if (listener == null) {
            throw new NullPointerException((String)"listener");
        }
        DefaultPromise.notifyListener((EventExecutor)this.executor(), this, listener);
        return this;
    }

    @Override
    public Future<V> addListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        if (listeners == null) {
            throw new NullPointerException((String)"listeners");
        }
        GenericFutureListener<? extends Future<? super V>>[] arrgenericFutureListener = listeners;
        int n = arrgenericFutureListener.length;
        int n2 = 0;
        while (n2 < n) {
            GenericFutureListener<Future<V>> l = arrgenericFutureListener[n2];
            if (l == null) {
                return this;
            }
            DefaultPromise.notifyListener((EventExecutor)this.executor(), this, l);
            ++n2;
        }
        return this;
    }

    @Override
    public Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        return this;
    }

    @Override
    public Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        return this;
    }

    @Override
    public Future<V> await() throws InterruptedException {
        if (!Thread.interrupted()) return this;
        throw new InterruptedException();
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (!Thread.interrupted()) return true;
        throw new InterruptedException();
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        return this;
    }

    @Override
    public Future<V> syncUninterruptibly() {
        return this;
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        if (!Thread.interrupted()) return true;
        throw new InterruptedException();
    }

    @Override
    public Future<V> awaitUninterruptibly() {
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return true;
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return true;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
}

