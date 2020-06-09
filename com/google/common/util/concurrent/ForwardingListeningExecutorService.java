/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.ForwardingExecutorService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingListeningExecutorService
extends ForwardingExecutorService
implements ListeningExecutorService {
    protected ForwardingListeningExecutorService() {
    }

    @Override
    protected abstract ListeningExecutorService delegate();

    @Override
    public <T> ListenableFuture<T> submit(Callable<T> task) {
        return this.delegate().submit(task);
    }

    @Override
    public ListenableFuture<?> submit(Runnable task) {
        return this.delegate().submit((Runnable)task);
    }

    @Override
    public <T> ListenableFuture<T> submit(Runnable task, T result) {
        return this.delegate().submit((Runnable)task, result);
    }
}

