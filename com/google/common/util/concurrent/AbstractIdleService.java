/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public abstract class AbstractIdleService
implements Service {
    private final Supplier<String> threadNameSupplier = new ThreadNameSupplier((AbstractIdleService)this, null);
    private final Service delegate = new DelegateService((AbstractIdleService)this, null);

    protected AbstractIdleService() {
    }

    protected abstract void startUp() throws Exception;

    protected abstract void shutDown() throws Exception;

    protected Executor executor() {
        return new Executor((AbstractIdleService)this){
            final /* synthetic */ AbstractIdleService this$0;
            {
                this.this$0 = abstractIdleService;
            }

            public void execute(java.lang.Runnable command) {
                com.google.common.util.concurrent.MoreExecutors.newThread((String)((String)AbstractIdleService.access$200((AbstractIdleService)this.this$0).get()), (java.lang.Runnable)command).start();
            }
        };
    }

    public String toString() {
        return this.serviceName() + " [" + (Object)((Object)this.state()) + "]";
    }

    @Override
    public final boolean isRunning() {
        return this.delegate.isRunning();
    }

    @Override
    public final Service.State state() {
        return this.delegate.state();
    }

    @Override
    public final void addListener(Service.Listener listener, Executor executor) {
        this.delegate.addListener((Service.Listener)listener, (Executor)executor);
    }

    @Override
    public final Throwable failureCause() {
        return this.delegate.failureCause();
    }

    @CanIgnoreReturnValue
    @Override
    public final Service startAsync() {
        this.delegate.startAsync();
        return this;
    }

    @CanIgnoreReturnValue
    @Override
    public final Service stopAsync() {
        this.delegate.stopAsync();
        return this;
    }

    @Override
    public final void awaitRunning() {
        this.delegate.awaitRunning();
    }

    @Override
    public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
        this.delegate.awaitRunning((long)timeout, (TimeUnit)unit);
    }

    @Override
    public final void awaitTerminated() {
        this.delegate.awaitTerminated();
    }

    @Override
    public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
        this.delegate.awaitTerminated((long)timeout, (TimeUnit)unit);
    }

    protected String serviceName() {
        return this.getClass().getSimpleName();
    }

    static /* synthetic */ Supplier access$200(AbstractIdleService x0) {
        return x0.threadNameSupplier;
    }
}

