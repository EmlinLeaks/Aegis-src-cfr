/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@Beta
@GwtIncompatible
public abstract class AbstractScheduledService
implements Service {
    private static final Logger logger = Logger.getLogger((String)AbstractScheduledService.class.getName());
    private final AbstractService delegate = new ServiceDelegate((AbstractScheduledService)this, null);

    protected AbstractScheduledService() {
    }

    protected abstract void runOneIteration() throws Exception;

    protected void startUp() throws Exception {
    }

    protected void shutDown() throws Exception {
    }

    protected abstract Scheduler scheduler();

    protected ScheduledExecutorService executor() {
        class ThreadFactoryImpl
        implements ThreadFactory {
            final /* synthetic */ AbstractScheduledService this$0;

            ThreadFactoryImpl(AbstractScheduledService abstractScheduledService) {
                this.this$0 = abstractScheduledService;
            }

            public java.lang.Thread newThread(java.lang.Runnable runnable) {
                return MoreExecutors.newThread((String)this.this$0.serviceName(), (java.lang.Runnable)runnable);
            }
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((ThreadFactory)new ThreadFactoryImpl((AbstractScheduledService)this));
        this.addListener((Service.Listener)new Service.Listener((AbstractScheduledService)this, (ScheduledExecutorService)executor){
            final /* synthetic */ ScheduledExecutorService val$executor;
            final /* synthetic */ AbstractScheduledService this$0;
            {
                this.this$0 = abstractScheduledService;
                this.val$executor = scheduledExecutorService;
            }

            public void terminated(Service.State from) {
                this.val$executor.shutdown();
            }

            public void failed(Service.State from, Throwable failure) {
                this.val$executor.shutdown();
            }
        }, (Executor)MoreExecutors.directExecutor());
        return executor;
    }

    protected String serviceName() {
        return this.getClass().getSimpleName();
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

    static /* synthetic */ Logger access$400() {
        return logger;
    }

    static /* synthetic */ AbstractService access$500(AbstractScheduledService x0) {
        return x0.delegate;
    }
}

