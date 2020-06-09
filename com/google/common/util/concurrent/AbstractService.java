/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.concurrent.GuardedBy
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.ListenerCallQueue;
import com.google.common.util.concurrent.Monitor;
import com.google.common.util.concurrent.Service;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.concurrent.GuardedBy;

@Beta
@GwtIncompatible
public abstract class AbstractService
implements Service {
    private static final ListenerCallQueue.Callback<Service.Listener> STARTING_CALLBACK = new ListenerCallQueue.Callback<Service.Listener>((String)"starting()"){

        void call(Service.Listener listener) {
            listener.starting();
        }
    };
    private static final ListenerCallQueue.Callback<Service.Listener> RUNNING_CALLBACK = new ListenerCallQueue.Callback<Service.Listener>((String)"running()"){

        void call(Service.Listener listener) {
            listener.running();
        }
    };
    private static final ListenerCallQueue.Callback<Service.Listener> STOPPING_FROM_STARTING_CALLBACK = AbstractService.stoppingCallback((Service.State)Service.State.STARTING);
    private static final ListenerCallQueue.Callback<Service.Listener> STOPPING_FROM_RUNNING_CALLBACK = AbstractService.stoppingCallback((Service.State)Service.State.RUNNING);
    private static final ListenerCallQueue.Callback<Service.Listener> TERMINATED_FROM_NEW_CALLBACK = AbstractService.terminatedCallback((Service.State)Service.State.NEW);
    private static final ListenerCallQueue.Callback<Service.Listener> TERMINATED_FROM_RUNNING_CALLBACK = AbstractService.terminatedCallback((Service.State)Service.State.RUNNING);
    private static final ListenerCallQueue.Callback<Service.Listener> TERMINATED_FROM_STOPPING_CALLBACK = AbstractService.terminatedCallback((Service.State)Service.State.STOPPING);
    private final Monitor monitor = new Monitor();
    private final Monitor.Guard isStartable = new IsStartableGuard((AbstractService)this);
    private final Monitor.Guard isStoppable = new IsStoppableGuard((AbstractService)this);
    private final Monitor.Guard hasReachedRunning = new HasReachedRunningGuard((AbstractService)this);
    private final Monitor.Guard isStopped = new IsStoppedGuard((AbstractService)this);
    @GuardedBy(value="monitor")
    private final List<ListenerCallQueue<Service.Listener>> listeners = Collections.synchronizedList(new ArrayList<E>());
    @GuardedBy(value="monitor")
    private volatile StateSnapshot snapshot = new StateSnapshot((Service.State)Service.State.NEW);

    private static ListenerCallQueue.Callback<Service.Listener> terminatedCallback(Service.State from) {
        return new ListenerCallQueue.Callback<Service.Listener>((String)("terminated({from = " + (Object)((Object)from) + "})"), (Service.State)from){
            final /* synthetic */ Service.State val$from;
            {
                this.val$from = state;
                super((String)x0);
            }

            void call(Service.Listener listener) {
                listener.terminated((Service.State)this.val$from);
            }
        };
    }

    private static ListenerCallQueue.Callback<Service.Listener> stoppingCallback(Service.State from) {
        return new ListenerCallQueue.Callback<Service.Listener>((String)("stopping({from = " + (Object)((Object)from) + "})"), (Service.State)from){
            final /* synthetic */ Service.State val$from;
            {
                this.val$from = state;
                super((String)x0);
            }

            void call(Service.Listener listener) {
                listener.stopping((Service.State)this.val$from);
            }
        };
    }

    protected AbstractService() {
    }

    protected abstract void doStart();

    protected abstract void doStop();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @CanIgnoreReturnValue
    @Override
    public final Service startAsync() {
        if (!this.monitor.enterIf((Monitor.Guard)this.isStartable)) throw new IllegalStateException((String)("Service " + this + " has already been started"));
        try {
            this.snapshot = new StateSnapshot((Service.State)Service.State.STARTING);
            this.starting();
            this.doStart();
            return this;
        }
        catch (Throwable startupFailure) {
            this.notifyFailed((Throwable)startupFailure);
            return this;
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @CanIgnoreReturnValue
    @Override
    public final Service stopAsync() {
        if (!this.monitor.enterIf((Monitor.Guard)this.isStoppable)) return this;
        try {
            Service.State previous = this.state();
            switch (6.$SwitchMap$com$google$common$util$concurrent$Service$State[previous.ordinal()]) {
                case 1: {
                    this.snapshot = new StateSnapshot((Service.State)Service.State.TERMINATED);
                    this.terminated((Service.State)Service.State.NEW);
                    return this;
                }
                case 2: {
                    this.snapshot = new StateSnapshot((Service.State)Service.State.STARTING, (boolean)true, null);
                    this.stopping((Service.State)Service.State.STARTING);
                    return this;
                }
                case 3: {
                    this.snapshot = new StateSnapshot((Service.State)Service.State.STOPPING);
                    this.stopping((Service.State)Service.State.RUNNING);
                    this.doStop();
                    return this;
                }
                case 4: 
                case 5: 
                case 6: {
                    throw new AssertionError((Object)("isStoppable is incorrectly implemented, saw: " + (Object)((Object)previous)));
                }
            }
            throw new AssertionError((Object)("Unexpected state: " + (Object)((Object)previous)));
        }
        catch (Throwable shutdownFailure) {
            this.notifyFailed((Throwable)shutdownFailure);
            return this;
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void awaitRunning() {
        this.monitor.enterWhenUninterruptibly((Monitor.Guard)this.hasReachedRunning);
        try {
            this.checkCurrentState((Service.State)Service.State.RUNNING);
            return;
        }
        finally {
            this.monitor.leave();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
        if (!this.monitor.enterWhenUninterruptibly((Monitor.Guard)this.hasReachedRunning, (long)timeout, (TimeUnit)unit)) throw new TimeoutException((String)("Timed out waiting for " + this + " to reach the RUNNING state."));
        try {
            this.checkCurrentState((Service.State)Service.State.RUNNING);
            return;
        }
        finally {
            this.monitor.leave();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void awaitTerminated() {
        this.monitor.enterWhenUninterruptibly((Monitor.Guard)this.isStopped);
        try {
            this.checkCurrentState((Service.State)Service.State.TERMINATED);
            return;
        }
        finally {
            this.monitor.leave();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
        if (!this.monitor.enterWhenUninterruptibly((Monitor.Guard)this.isStopped, (long)timeout, (TimeUnit)unit)) throw new TimeoutException((String)("Timed out waiting for " + this + " to reach a terminal state. " + "Current state: " + (Object)((Object)this.state())));
        try {
            this.checkCurrentState((Service.State)Service.State.TERMINATED);
            return;
        }
        finally {
            this.monitor.leave();
        }
    }

    @GuardedBy(value="monitor")
    private void checkCurrentState(Service.State expected) {
        Service.State actual = this.state();
        if (actual == expected) return;
        if (actual != Service.State.FAILED) throw new IllegalStateException((String)("Expected the service " + this + " to be " + (Object)((Object)expected) + ", but was " + (Object)((Object)actual)));
        throw new IllegalStateException((String)("Expected the service " + this + " to be " + (Object)((Object)expected) + ", but the service has FAILED"), (Throwable)this.failureCause());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void notifyStarted() {
        this.monitor.enter();
        try {
            if (this.snapshot.state != Service.State.STARTING) {
                IllegalStateException failure = new IllegalStateException((String)("Cannot notifyStarted() when the service is " + (Object)((Object)this.snapshot.state)));
                this.notifyFailed((Throwable)failure);
                throw failure;
            }
            if (this.snapshot.shutdownWhenStartupFinishes) {
                this.snapshot = new StateSnapshot((Service.State)Service.State.STOPPING);
                this.doStop();
                return;
            }
            this.snapshot = new StateSnapshot((Service.State)Service.State.RUNNING);
            this.running();
            return;
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void notifyStopped() {
        this.monitor.enter();
        try {
            Service.State previous = this.snapshot.state;
            if (previous != Service.State.STOPPING && previous != Service.State.RUNNING) {
                IllegalStateException failure = new IllegalStateException((String)("Cannot notifyStopped() when the service is " + (Object)((Object)previous)));
                this.notifyFailed((Throwable)failure);
                throw failure;
            }
            this.snapshot = new StateSnapshot((Service.State)Service.State.TERMINATED);
            this.terminated((Service.State)previous);
            return;
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void notifyFailed(Throwable cause) {
        Preconditions.checkNotNull(cause);
        this.monitor.enter();
        try {
            Service.State previous = this.state();
            switch (6.$SwitchMap$com$google$common$util$concurrent$Service$State[previous.ordinal()]) {
                case 1: 
                case 5: {
                    throw new IllegalStateException((String)("Failed while in state:" + (Object)((Object)previous)), (Throwable)cause);
                }
                case 2: 
                case 3: 
                case 4: {
                    this.snapshot = new StateSnapshot((Service.State)Service.State.FAILED, (boolean)false, (Throwable)cause);
                    this.failed((Service.State)previous, (Throwable)cause);
                    return;
                }
                case 6: {
                    return;
                }
            }
            throw new AssertionError((Object)("Unexpected state: " + (Object)((Object)previous)));
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }

    @Override
    public final boolean isRunning() {
        if (this.state() != Service.State.RUNNING) return false;
        return true;
    }

    @Override
    public final Service.State state() {
        return this.snapshot.externalState();
    }

    @Override
    public final Throwable failureCause() {
        return this.snapshot.failureCause();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void addListener(Service.Listener listener, Executor executor) {
        Preconditions.checkNotNull(listener, (Object)"listener");
        Preconditions.checkNotNull(executor, (Object)"executor");
        this.monitor.enter();
        try {
            if (this.state().isTerminal()) return;
            this.listeners.add(new ListenerCallQueue<Service.Listener>(listener, (Executor)executor));
            return;
        }
        finally {
            this.monitor.leave();
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [" + (Object)((Object)this.state()) + "]";
    }

    private void executeListeners() {
        if (this.monitor.isOccupiedByCurrentThread()) return;
        int i = 0;
        while (i < this.listeners.size()) {
            this.listeners.get((int)i).execute();
            ++i;
        }
    }

    @GuardedBy(value="monitor")
    private void starting() {
        STARTING_CALLBACK.enqueueOn(this.listeners);
    }

    @GuardedBy(value="monitor")
    private void running() {
        RUNNING_CALLBACK.enqueueOn(this.listeners);
    }

    @GuardedBy(value="monitor")
    private void stopping(Service.State from) {
        if (from == Service.State.STARTING) {
            STOPPING_FROM_STARTING_CALLBACK.enqueueOn(this.listeners);
            return;
        }
        if (from != Service.State.RUNNING) throw new AssertionError();
        STOPPING_FROM_RUNNING_CALLBACK.enqueueOn(this.listeners);
    }

    @GuardedBy(value="monitor")
    private void terminated(Service.State from) {
        switch (from) {
            case NEW: {
                TERMINATED_FROM_NEW_CALLBACK.enqueueOn(this.listeners);
                return;
            }
            case RUNNING: {
                TERMINATED_FROM_RUNNING_CALLBACK.enqueueOn(this.listeners);
                return;
            }
            case STOPPING: {
                TERMINATED_FROM_STOPPING_CALLBACK.enqueueOn(this.listeners);
                return;
            }
        }
        throw new AssertionError();
    }

    @GuardedBy(value="monitor")
    private void failed(Service.State from, Throwable cause) {
        new ListenerCallQueue.Callback<Service.Listener>((AbstractService)this, (String)("failed({from = " + (Object)((Object)from) + ", cause = " + cause + "})"), (Service.State)from, (Throwable)cause){
            final /* synthetic */ Service.State val$from;
            final /* synthetic */ Throwable val$cause;
            final /* synthetic */ AbstractService this$0;
            {
                this.this$0 = abstractService;
                this.val$from = state;
                this.val$cause = throwable;
                super((String)x0);
            }

            void call(Service.Listener listener) {
                listener.failed((Service.State)this.val$from, (Throwable)this.val$cause);
            }
        }.enqueueOn(this.listeners);
    }

    static /* synthetic */ Monitor access$000(AbstractService x0) {
        return x0.monitor;
    }
}

