/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.util.concurrent.ListenerCallQueue;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
@GwtIncompatible
public final class ServiceManager {
    private static final Logger logger = Logger.getLogger((String)ServiceManager.class.getName());
    private static final ListenerCallQueue.Callback<Listener> HEALTHY_CALLBACK = new ListenerCallQueue.Callback<Listener>((String)"healthy()"){

        void call(Listener listener) {
            listener.healthy();
        }
    };
    private static final ListenerCallQueue.Callback<Listener> STOPPED_CALLBACK = new ListenerCallQueue.Callback<Listener>((String)"stopped()"){

        void call(Listener listener) {
            listener.stopped();
        }
    };
    private final ServiceManagerState state;
    private final ImmutableList<Service> services;

    public ServiceManager(Iterable<? extends Service> services) {
        ImmutableList<Service> copy = ImmutableList.copyOf(services);
        if (copy.isEmpty()) {
            logger.log((Level)Level.WARNING, (String)"ServiceManager configured with no services.  Is your application configured properly?", (Throwable)new EmptyServiceManagerWarning(null));
            copy = ImmutableList.of(new NoOpService(null));
        }
        this.state = new ServiceManagerState(copy);
        this.services = copy;
        WeakReference<ServiceManagerState> stateReference = new WeakReference<ServiceManagerState>(this.state);
        Iterator i$ = copy.iterator();
        do {
            if (!i$.hasNext()) {
                this.state.markReady();
                return;
            }
            Service service = (Service)i$.next();
            service.addListener((Service.Listener)new ServiceListener((Service)service, stateReference), (Executor)MoreExecutors.directExecutor());
            Preconditions.checkArgument((boolean)(service.state() == Service.State.NEW), (String)"Can only manage NEW services, %s", (Object)service);
        } while (true);
    }

    public void addListener(Listener listener, Executor executor) {
        this.state.addListener((Listener)listener, (Executor)executor);
    }

    public void addListener(Listener listener) {
        this.state.addListener((Listener)listener, (Executor)MoreExecutors.directExecutor());
    }

    @CanIgnoreReturnValue
    public ServiceManager startAsync() {
        for (Service service : this.services) {
            Service.State state = service.state();
            Preconditions.checkState((boolean)(state == Service.State.NEW), (String)"Service %s is %s, cannot start it.", (Object)service, (Object)((Object)state));
        }
        Iterator i$ = this.services.iterator();
        while (i$.hasNext()) {
            Service service;
            service = (Service)i$.next();
            try {
                this.state.tryStartTiming((Service)service);
                service.startAsync();
            }
            catch (IllegalStateException e) {
                logger.log((Level)Level.WARNING, (String)("Unable to start Service " + service), (Throwable)e);
            }
        }
        return this;
    }

    public void awaitHealthy() {
        this.state.awaitHealthy();
    }

    public void awaitHealthy(long timeout, TimeUnit unit) throws TimeoutException {
        this.state.awaitHealthy((long)timeout, (TimeUnit)unit);
    }

    @CanIgnoreReturnValue
    public ServiceManager stopAsync() {
        Iterator i$ = this.services.iterator();
        while (i$.hasNext()) {
            Service service = (Service)i$.next();
            service.stopAsync();
        }
        return this;
    }

    public void awaitStopped() {
        this.state.awaitStopped();
    }

    public void awaitStopped(long timeout, TimeUnit unit) throws TimeoutException {
        this.state.awaitStopped((long)timeout, (TimeUnit)unit);
    }

    public boolean isHealthy() {
        Service service;
        Iterator i$ = this.services.iterator();
        do {
            if (!i$.hasNext()) return true;
        } while ((service = (Service)i$.next()).isRunning());
        return false;
    }

    public ImmutableMultimap<Service.State, Service> servicesByState() {
        return this.state.servicesByState();
    }

    public ImmutableMap<Service, Long> startupTimes() {
        return this.state.startupTimes();
    }

    public String toString() {
        return MoreObjects.toStringHelper(ServiceManager.class).add((String)"services", Collections2.filter(this.services, Predicates.not(Predicates.instanceOf(NoOpService.class)))).toString();
    }

    static /* synthetic */ Logger access$200() {
        return logger;
    }

    static /* synthetic */ ListenerCallQueue.Callback access$300() {
        return STOPPED_CALLBACK;
    }

    static /* synthetic */ ListenerCallQueue.Callback access$400() {
        return HEALTHY_CALLBACK;
    }
}

