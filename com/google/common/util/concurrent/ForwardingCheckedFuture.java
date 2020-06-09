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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public abstract class ForwardingCheckedFuture<V, X extends Exception>
extends ForwardingListenableFuture<V>
implements CheckedFuture<V, X> {
    @CanIgnoreReturnValue
    @Override
    public V checkedGet() throws Exception {
        return (V)this.delegate().checkedGet();
    }

    @CanIgnoreReturnValue
    @Override
    public V checkedGet(long timeout, TimeUnit unit) throws TimeoutException, Exception {
        return (V)this.delegate().checkedGet((long)timeout, (TimeUnit)unit);
    }

    @Override
    protected abstract CheckedFuture<V, X> delegate();
}

