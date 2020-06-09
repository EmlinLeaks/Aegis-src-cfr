/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AbstractCatchingFuture;
import com.google.common.util.concurrent.AbstractTransformFuture;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.CollectionFuture;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FuturesGetChecked;
import com.google.common.util.concurrent.GwtFuturesCatchingSpecialization;
import com.google.common.util.concurrent.ImmediateFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Partially;
import com.google.common.util.concurrent.SerializingExecutor;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.TimeoutFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@Beta
@GwtCompatible(emulated=true)
public final class Futures
extends GwtFuturesCatchingSpecialization {
    private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction<ListenableFuture<Object>, Object>(){

        public ListenableFuture<Object> apply(ListenableFuture<Object> input) {
            return input;
        }
    };

    private Futures() {
    }

    @GwtIncompatible
    public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> future, Function<? super Exception, X> mapper) {
        return new MappingCheckedFuture<V, X>(Preconditions.checkNotNull(future), mapper);
    }

    public static <V> ListenableFuture<V> immediateFuture(@Nullable V value) {
        if (value != null) return new ImmediateFuture.ImmediateSuccessfulFuture<V>(value);
        return ImmediateFuture.ImmediateSuccessfulFuture.NULL;
    }

    @GwtIncompatible
    public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V value) {
        return new ImmediateFuture.ImmediateSuccessfulCheckedFuture<V, X>(value);
    }

    public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        return new ImmediateFuture.ImmediateFailedFuture<V>((Throwable)throwable);
    }

    public static <V> ListenableFuture<V> immediateCancelledFuture() {
        return new ImmediateFuture.ImmediateCancelledFuture<V>();
    }

    @GwtIncompatible
    public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X exception) {
        Preconditions.checkNotNull(exception);
        return new ImmediateFuture.ImmediateFailedCheckedFuture<V, X>(exception);
    }

    @Partially.GwtIncompatible(value="AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback) {
        return AbstractCatchingFuture.create(input, exceptionType, fallback);
    }

    @Partially.GwtIncompatible(value="AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback, Executor executor) {
        return AbstractCatchingFuture.create(input, exceptionType, fallback, (Executor)executor);
    }

    @CanIgnoreReturnValue
    @Partially.GwtIncompatible(value="AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback) {
        return AbstractCatchingFuture.create(input, exceptionType, fallback);
    }

    @CanIgnoreReturnValue
    @Partially.GwtIncompatible(value="AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback, Executor executor) {
        return AbstractCatchingFuture.create(input, exceptionType, fallback, (Executor)executor);
    }

    @GwtIncompatible
    public static <V> ListenableFuture<V> withTimeout(ListenableFuture<V> delegate, long time, TimeUnit unit, ScheduledExecutorService scheduledExecutor) {
        return TimeoutFuture.create(delegate, (long)time, (TimeUnit)unit, (ScheduledExecutorService)scheduledExecutor);
    }

    public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function) {
        return AbstractTransformFuture.create(input, function);
    }

    public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor) {
        return AbstractTransformFuture.create(input, function, (Executor)executor);
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function) {
        return AbstractTransformFuture.create(input, function);
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor) {
        return AbstractTransformFuture.create(input, function, (Executor)executor);
    }

    @GwtIncompatible
    public static <I, O> Future<O> lazyTransform(Future<I> input, Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(function);
        return new Future<O>(input, function){
            final /* synthetic */ Future val$input;
            final /* synthetic */ Function val$function;
            {
                this.val$input = future;
                this.val$function = function;
            }

            public boolean cancel(boolean mayInterruptIfRunning) {
                return this.val$input.cancel((boolean)mayInterruptIfRunning);
            }

            public boolean isCancelled() {
                return this.val$input.isCancelled();
            }

            public boolean isDone() {
                return this.val$input.isDone();
            }

            public O get() throws java.lang.InterruptedException, ExecutionException {
                return (O)this.applyTransformation(this.val$input.get());
            }

            public O get(long timeout, TimeUnit unit) throws java.lang.InterruptedException, ExecutionException, java.util.concurrent.TimeoutException {
                return (O)this.applyTransformation(this.val$input.get((long)timeout, (TimeUnit)unit));
            }

            private O applyTransformation(I input) throws ExecutionException {
                try {
                    return (O)this.val$function.apply(input);
                }
                catch (Throwable t) {
                    throw new ExecutionException((Throwable)t);
                }
            }
        };
    }

    public static <V> ListenableFuture<V> dereference(ListenableFuture<? extends ListenableFuture<? extends V>> nested) {
        return Futures.transformAsync(nested, DEREFERENCER);
    }

    @SafeVarargs
    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V> ... futures) {
        return new CollectionFuture.ListFuture<V>(ImmutableList.copyOf(futures), (boolean)true);
    }

    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> futures) {
        return new CollectionFuture.ListFuture<V>(ImmutableList.copyOf(futures), (boolean)true);
    }

    @SafeVarargs
    public static <V> FutureCombiner<V> whenAllComplete(ListenableFuture<? extends V> ... futures) {
        return new FutureCombiner<V>((boolean)false, ImmutableList.copyOf(futures), null);
    }

    public static <V> FutureCombiner<V> whenAllComplete(Iterable<? extends ListenableFuture<? extends V>> futures) {
        return new FutureCombiner<V>((boolean)false, ImmutableList.copyOf(futures), null);
    }

    @SafeVarargs
    public static <V> FutureCombiner<V> whenAllSucceed(ListenableFuture<? extends V> ... futures) {
        return new FutureCombiner<V>((boolean)true, ImmutableList.copyOf(futures), null);
    }

    public static <V> FutureCombiner<V> whenAllSucceed(Iterable<? extends ListenableFuture<? extends V>> futures) {
        return new FutureCombiner<V>((boolean)true, ImmutableList.copyOf(futures), null);
    }

    public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> future) {
        return new NonCancellationPropagatingFuture<V>(future);
    }

    @SafeVarargs
    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V> ... futures) {
        return new CollectionFuture.ListFuture<V>(ImmutableList.copyOf(futures), (boolean)false);
    }

    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> futures) {
        return new CollectionFuture.ListFuture<V>(ImmutableList.copyOf(futures), (boolean)false);
    }

    @Beta
    @GwtIncompatible
    public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> futures) {
        ConcurrentLinkedQueue<SettableFuture<V>> delegates = Queues.newConcurrentLinkedQueue();
        ImmutableList.Builder<E> listBuilder = ImmutableList.builder();
        SerializingExecutor executor = new SerializingExecutor((Executor)MoreExecutors.directExecutor());
        Iterator<ListenableFuture<T>> i$ = futures.iterator();
        while (i$.hasNext()) {
            ListenableFuture<T> future = i$.next();
            SettableFuture<V> delegate = SettableFuture.create();
            delegates.add(delegate);
            future.addListener((Runnable)new Runnable(delegates, future){
                final /* synthetic */ ConcurrentLinkedQueue val$delegates;
                final /* synthetic */ ListenableFuture val$future;
                {
                    this.val$delegates = concurrentLinkedQueue;
                    this.val$future = listenableFuture;
                }

                public void run() {
                    ((SettableFuture)this.val$delegates.remove()).setFuture(this.val$future);
                }
            }, (Executor)executor);
            listBuilder.add(delegate);
        }
        return listBuilder.build();
    }

    public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback) {
        Futures.addCallback(future, callback, (Executor)MoreExecutors.directExecutor());
    }

    public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback, Executor executor) {
        Preconditions.checkNotNull(callback);
        Runnable callbackListener = new Runnable(future, callback){
            final /* synthetic */ ListenableFuture val$future;
            final /* synthetic */ FutureCallback val$callback;
            {
                this.val$future = listenableFuture;
                this.val$callback = futureCallback;
            }

            public void run() {
                V value;
                try {
                    value = Futures.getDone(this.val$future);
                }
                catch (ExecutionException e) {
                    this.val$callback.onFailure((Throwable)e.getCause());
                    return;
                }
                catch (java.lang.RuntimeException e) {
                    this.val$callback.onFailure((Throwable)e);
                    return;
                }
                catch (Error e) {
                    this.val$callback.onFailure((Throwable)e);
                    return;
                }
                this.val$callback.onSuccess(value);
            }
        };
        future.addListener((Runnable)callbackListener, (Executor)executor);
    }

    @CanIgnoreReturnValue
    public static <V> V getDone(Future<V> future) throws ExecutionException {
        Preconditions.checkState((boolean)future.isDone(), (String)"Future was expected to be done: %s", future);
        return (V)Uninterruptibles.getUninterruptibly(future);
    }

    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws Exception {
        return (V)FuturesGetChecked.getChecked(future, exceptionClass);
    }

    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws Exception {
        return (V)FuturesGetChecked.getChecked(future, exceptionClass, (long)timeout, (TimeUnit)unit);
    }

    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <V> V getUnchecked(Future<V> future) {
        Preconditions.checkNotNull(future);
        try {
            return (V)Uninterruptibles.getUninterruptibly(future);
        }
        catch (ExecutionException e) {
            Futures.wrapAndThrowUnchecked((Throwable)e.getCause());
            throw new AssertionError();
        }
    }

    @GwtIncompatible
    private static void wrapAndThrowUnchecked(Throwable cause) {
        if (!(cause instanceof Error)) throw new UncheckedExecutionException((Throwable)cause);
        throw new ExecutionError((Error)((Error)cause));
    }
}

