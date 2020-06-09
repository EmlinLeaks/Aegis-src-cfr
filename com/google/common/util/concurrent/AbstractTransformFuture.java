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
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.AbstractTransformFuture;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.errorprone.annotations.ForOverride;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractTransformFuture<I, O, F, T>
extends AbstractFuture.TrustedFuture<O>
implements Runnable {
    @Nullable
    ListenableFuture<? extends I> inputFuture;
    @Nullable
    F function;

    static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function) {
        AsyncTransformFuture<? super I, ? extends O> output = new AsyncTransformFuture<I, O>(input, function);
        input.addListener(output, (Executor)MoreExecutors.directExecutor());
        return output;
    }

    static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor) {
        Preconditions.checkNotNull(executor);
        AsyncTransformFuture<? super I, ? extends O> output = new AsyncTransformFuture<I, O>(input, function);
        input.addListener(output, (Executor)MoreExecutors.rejectionPropagatingExecutor((Executor)executor, output));
        return output;
    }

    static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(function);
        TransformFuture<? super I, ? extends O> output = new TransformFuture<I, O>(input, function);
        input.addListener(output, (Executor)MoreExecutors.directExecutor());
        return output;
    }

    static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor) {
        Preconditions.checkNotNull(function);
        TransformFuture<? super I, ? extends O> output = new TransformFuture<I, O>(input, function);
        input.addListener(output, (Executor)MoreExecutors.rejectionPropagatingExecutor((Executor)executor, output));
        return output;
    }

    AbstractTransformFuture(ListenableFuture<? extends I> inputFuture, F function) {
        this.inputFuture = Preconditions.checkNotNull(inputFuture);
        this.function = Preconditions.checkNotNull(function);
    }

    @Override
    public final void run() {
        T transformResult;
        I sourceResult;
        ListenableFuture<? extends I> localInputFuture = this.inputFuture;
        F localFunction = this.function;
        if (this.isCancelled() | localInputFuture == null | localFunction == null) {
            return;
        }
        this.inputFuture = null;
        this.function = null;
        try {
            sourceResult = Futures.getDone(localInputFuture);
        }
        catch (CancellationException e) {
            this.cancel((boolean)false);
            return;
        }
        catch (ExecutionException e) {
            this.setException((Throwable)e.getCause());
            return;
        }
        catch (RuntimeException e) {
            this.setException((Throwable)e);
            return;
        }
        catch (Error e) {
            this.setException((Throwable)e);
            return;
        }
        try {
            transformResult = this.doTransform(localFunction, sourceResult);
        }
        catch (UndeclaredThrowableException e) {
            this.setException((Throwable)e.getCause());
            return;
        }
        catch (Throwable t) {
            this.setException((Throwable)t);
            return;
        }
        this.setResult(transformResult);
    }

    @Nullable
    @ForOverride
    abstract T doTransform(F var1, @Nullable I var2) throws Exception;

    @ForOverride
    abstract void setResult(@Nullable T var1);

    @Override
    protected final void afterDone() {
        this.maybePropagateCancellation(this.inputFuture);
        this.inputFuture = null;
        this.function = null;
    }
}

