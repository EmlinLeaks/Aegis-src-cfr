/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.Service;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public interface Service {
    @CanIgnoreReturnValue
    public Service startAsync();

    public boolean isRunning();

    public State state();

    @CanIgnoreReturnValue
    public Service stopAsync();

    public void awaitRunning();

    public void awaitRunning(long var1, TimeUnit var3) throws TimeoutException;

    public void awaitTerminated();

    public void awaitTerminated(long var1, TimeUnit var3) throws TimeoutException;

    public Throwable failureCause();

    public void addListener(Listener var1, Executor var2);
}

