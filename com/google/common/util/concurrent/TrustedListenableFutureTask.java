/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.InterruptibleTask;
import com.google.common.util.concurrent.TrustedListenableFutureTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import javax.annotation.Nullable;

@GwtCompatible
class TrustedListenableFutureTask<V>
extends AbstractFuture.TrustedFuture<V>
implements RunnableFuture<V> {
    private TrustedListenableFutureTask<V> task;

    static <V> TrustedListenableFutureTask<V> create(Callable<V> callable) {
        return new TrustedListenableFutureTask<V>(callable);
    }

    static <V> TrustedListenableFutureTask<V> create(Runnable runnable, @Nullable V result) {
        return new TrustedListenableFutureTask<V>(Executors.callable((Runnable)runnable, result));
    }

    TrustedListenableFutureTask(Callable<V> callable) {
        this.task = new TrustedFutureInterruptibleTask((TrustedListenableFutureTask)this, callable);
    }

    @Override
    public void run() {
        TrustedListenableFutureTask<V> localTask = this.task;
        if (localTask == null) return;
        ((InterruptibleTask)((Object)localTask)).run();
    }

    @Override
    protected void afterDone() {
        TrustedListenableFutureTask<V> localTask;
        super.afterDone();
        if (this.wasInterrupted() && (localTask = this.task) != null) {
            ((InterruptibleTask)((Object)localTask)).interruptTask();
        }
        this.task = null;
    }

    public String toString() {
        return Object.super.toString() + " (delegate = " + this.task + ")";
    }
}

