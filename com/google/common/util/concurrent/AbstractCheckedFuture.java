/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public abstract class AbstractCheckedFuture<V, X extends Exception>
extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V>
implements CheckedFuture<V, X> {
    protected AbstractCheckedFuture(ListenableFuture<V> delegate) {
        super(delegate);
    }

    protected abstract X mapException(Exception var1);

    @CanIgnoreReturnValue
    @Override
    public V checkedGet() throws Exception {
        try {
            return (V)this.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw this.mapException((Exception)e);
        }
        catch (CancellationException e) {
            throw this.mapException((Exception)e);
        }
        catch (ExecutionException e) {
            throw this.mapException((Exception)e);
        }
    }

    @CanIgnoreReturnValue
    @Override
    public V checkedGet(long timeout, TimeUnit unit) throws TimeoutException, Exception {
        try {
            return (V)this.get((long)timeout, (TimeUnit)unit);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw this.mapException((Exception)e);
        }
        catch (CancellationException e) {
            throw this.mapException((Exception)e);
        }
        catch (ExecutionException e) {
            throw this.mapException((Exception)e);
        }
    }
}

