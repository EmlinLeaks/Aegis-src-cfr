/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.TimeoutFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@GwtIncompatible
final class TimeoutFuture<V>
extends AbstractFuture.TrustedFuture<V> {
    @Nullable
    private ListenableFuture<V> delegateRef;
    @Nullable
    private Future<?> timer;

    static <V> ListenableFuture<V> create(ListenableFuture<V> delegate, long time, TimeUnit unit, ScheduledExecutorService scheduledExecutor) {
        TimeoutFuture<V> result = new TimeoutFuture<V>(delegate);
        Fire<V> fire = new Fire<V>(result);
        result.timer = scheduledExecutor.schedule(fire, (long)time, (TimeUnit)unit);
        delegate.addListener(fire, (Executor)MoreExecutors.directExecutor());
        return result;
    }

    private TimeoutFuture(ListenableFuture<V> delegate) {
        this.delegateRef = Preconditions.checkNotNull(delegate);
    }

    @Override
    protected void afterDone() {
        this.maybePropagateCancellation(this.delegateRef);
        Future<?> localTimer = this.timer;
        if (localTimer != null) {
            localTimer.cancel((boolean)false);
        }
        this.delegateRef = null;
        this.timer = null;
    }

    static /* synthetic */ ListenableFuture access$000(TimeoutFuture x0) {
        return x0.delegateRef;
    }
}

