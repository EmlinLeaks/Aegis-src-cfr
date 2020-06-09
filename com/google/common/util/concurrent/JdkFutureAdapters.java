/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Beta
@GwtIncompatible
public final class JdkFutureAdapters {
    public static <V> ListenableFuture<V> listenInPoolThread(Future<V> future) {
        if (!(future instanceof ListenableFuture)) return new ListenableFutureAdapter<V>(future);
        return (ListenableFuture)future;
    }

    public static <V> ListenableFuture<V> listenInPoolThread(Future<V> future, Executor executor) {
        Preconditions.checkNotNull(executor);
        if (!(future instanceof ListenableFuture)) return new ListenableFutureAdapter<V>(future, (Executor)executor);
        return (ListenableFuture)future;
    }

    private JdkFutureAdapters() {
    }
}

