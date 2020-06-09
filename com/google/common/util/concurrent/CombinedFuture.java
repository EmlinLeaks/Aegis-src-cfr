/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableCollection;
import com.google.common.util.concurrent.AggregateFuture;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.CombinedFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

@GwtCompatible
final class CombinedFuture<V>
extends AggregateFuture<Object, V> {
    CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, AsyncCallable<V> callable) {
        this.init((AggregateFuture.RunningState)((Object)new CombinedFutureRunningState((CombinedFuture)this, futures, (boolean)allMustSucceed, (CombinedFutureInterruptibleTask)((Object)new AsyncCallableInterruptibleTask((CombinedFuture)this, callable, (Executor)listenerExecutor)))));
    }

    CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, Callable<V> callable) {
        this.init((AggregateFuture.RunningState)((Object)new CombinedFutureRunningState((CombinedFuture)this, futures, (boolean)allMustSucceed, (CombinedFutureInterruptibleTask)((Object)new CallableInterruptibleTask((CombinedFuture)this, callable, (Executor)listenerExecutor)))));
    }
}

