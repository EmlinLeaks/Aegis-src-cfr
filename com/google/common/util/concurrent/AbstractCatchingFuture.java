/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.ForOverride
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractCatchingFuture;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Platform;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractCatchingFuture<V, X extends Throwable, F, T>
extends AbstractFuture.TrustedFuture<V>
implements Runnable {
    @Nullable
    ListenableFuture<? extends V> inputFuture;
    @Nullable
    Class<X> exceptionType;
    @Nullable
    F fallback;

    static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback) {
        CatchingFuture<? extends V, ? super X> future = new CatchingFuture<V, X>(input, exceptionType, fallback);
        input.addListener(future, (Executor)MoreExecutors.directExecutor());
        return future;
    }

    static <V, X extends Throwable> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback, Executor executor) {
        CatchingFuture<? extends V, ? super X> future = new CatchingFuture<V, X>(input, exceptionType, fallback);
        input.addListener(future, (Executor)MoreExecutors.rejectionPropagatingExecutor((Executor)executor, future));
        return future;
    }

    static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback) {
        AsyncCatchingFuture<? extends V, ? super X> future = new AsyncCatchingFuture<V, X>(input, exceptionType, fallback);
        input.addListener(future, (Executor)MoreExecutors.directExecutor());
        return future;
    }

    static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback, Executor executor) {
        AsyncCatchingFuture<? extends V, ? super X> future = new AsyncCatchingFuture<V, X>(input, exceptionType, fallback);
        input.addListener(future, (Executor)MoreExecutors.rejectionPropagatingExecutor((Executor)executor, future));
        return future;
    }

    AbstractCatchingFuture(ListenableFuture<? extends V> inputFuture, Class<X> exceptionType, F fallback) {
        this.inputFuture = Preconditions.checkNotNull(inputFuture);
        this.exceptionType = Preconditions.checkNotNull(exceptionType);
        this.fallback = Preconditions.checkNotNull(fallback);
    }

    @Override
    public final void run() {
        T fallbackResult;
        F localFallback;
        Class<X> localExceptionType;
        ListenableFuture<? extends V> localInputFuture = this.inputFuture;
        if (localInputFuture == null | (localExceptionType = this.exceptionType) == null | (localFallback = this.fallback) == null | this.isCancelled()) {
            return;
        }
        this.inputFuture = null;
        this.exceptionType = null;
        this.fallback = null;
        V sourceResult = null;
        Throwable throwable = null;
        try {
            sourceResult = (V)Futures.getDone(localInputFuture);
        }
        catch (ExecutionException e) {
            throwable = Preconditions.checkNotNull(e.getCause());
        }
        catch (Throwable e) {
            throwable = e;
        }
        if (throwable == null) {
            this.set(sourceResult);
            return;
        }
        if (!Platform.isInstanceOfThrowableClass((Throwable)throwable, localExceptionType)) {
            this.setException((Throwable)throwable);
            return;
        }
        Throwable castThrowable = throwable;
        try {
            fallbackResult = this.doFallback(localFallback, castThrowable);
        }
        catch (Throwable t) {
            this.setException((Throwable)t);
            return;
        }
        this.setResult(fallbackResult);
    }

    @Nullable
    @ForOverride
    abstract T doFallback(F var1, X var2) throws Exception;

    @ForOverride
    abstract void setResult(@Nullable T var1);

    @Override
    protected final void afterDone() {
        this.maybePropagateCancellation(this.inputFuture);
        this.inputFuture = null;
        this.exceptionType = null;
        this.fallback = null;
    }
}

