/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.PlatformDependent;

public final class FailedFuture<V>
extends CompleteFuture<V> {
    private final Throwable cause;

    public FailedFuture(EventExecutor executor, Throwable cause) {
        super((EventExecutor)executor);
        if (cause == null) {
            throw new NullPointerException((String)"cause");
        }
        this.cause = cause;
    }

    @Override
    public Throwable cause() {
        return this.cause;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Future<V> sync() {
        PlatformDependent.throwException((Throwable)this.cause);
        return this;
    }

    @Override
    public Future<V> syncUninterruptibly() {
        PlatformDependent.throwException((Throwable)this.cause);
        return this;
    }

    @Override
    public V getNow() {
        return (V)null;
    }
}

