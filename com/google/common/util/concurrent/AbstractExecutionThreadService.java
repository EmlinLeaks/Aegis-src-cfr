/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@Beta
@GwtIncompatible
public abstract class AbstractExecutionThreadService
implements Service {
    private static final Logger logger = Logger.getLogger((String)AbstractExecutionThreadService.class.getName());
    private final Service delegate = new AbstractService((AbstractExecutionThreadService)this){
        final /* synthetic */ AbstractExecutionThreadService this$0;
        {
            this.this$0 = abstractExecutionThreadService;
        }

        protected final void doStart() {
            Executor executor = com.google.common.util.concurrent.MoreExecutors.renamingDecorator((Executor)this.this$0.executor(), (com.google.common.base.Supplier<String>)new com.google.common.base.Supplier<String>(this){
                final /* synthetic */ 1 this$1;
                {
                    this.this$1 = var1_1;
                }

                public String get() {
                    return this.this$1.this$0.serviceName();
                }
            });
            executor.execute((java.lang.Runnable)new java.lang.Runnable(this){
                final /* synthetic */ 1 this$1;
                {
                    this.this$1 = var1_1;
                }

                public void run() {
                    try {
                        this.this$1.this$0.startUp();
                        this.this$1.notifyStarted();
                        if (this.this$1.isRunning()) {
                            try {
                                this.this$1.this$0.run();
                            }
                            catch (Throwable t) {
                                try {
                                    this.this$1.this$0.shutDown();
                                }
                                catch (Exception ignored) {
                                    AbstractExecutionThreadService.access$000().log((java.util.logging.Level)java.util.logging.Level.WARNING, (String)"Error while attempting to shut down the service after failure.", (Throwable)ignored);
                                }
                                this.this$1.notifyFailed((Throwable)t);
                                return;
                            }
                        }
                        this.this$1.this$0.shutDown();
                        this.this$1.notifyStopped();
                        return;
                    }
                    catch (Throwable t) {
                        this.this$1.notifyFailed((Throwable)t);
                    }
                }
            });
        }

        protected void doStop() {
            this.this$0.triggerShutdown();
        }

        public String toString() {
            return this.this$0.toString();
        }
    };

    protected AbstractExecutionThreadService() {
    }

    protected void startUp() throws Exception {
    }

    protected abstract void run() throws Exception;

    protected void shutDown() throws Exception {
    }

    protected void triggerShutdown() {
    }

    protected Executor executor() {
        return new Executor((AbstractExecutionThreadService)this){
            final /* synthetic */ AbstractExecutionThreadService this$0;
            {
                this.this$0 = abstractExecutionThreadService;
            }

            public void execute(java.lang.Runnable command) {
                com.google.common.util.concurrent.MoreExecutors.newThread((String)this.this$0.serviceName(), (java.lang.Runnable)command).start();
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

    static /* synthetic */ Logger access$000() {
        return logger;
    }
}

