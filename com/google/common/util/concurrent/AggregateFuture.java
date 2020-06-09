/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableCollection;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.AggregateFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

@GwtCompatible
abstract class AggregateFuture<InputT, OutputT>
extends AbstractFuture.TrustedFuture<OutputT> {
    private static final Logger logger = Logger.getLogger((String)AggregateFuture.class.getName());
    private AggregateFuture<InputT, OutputT> runningState;

    AggregateFuture() {
    }

    @Override
    protected final void afterDone() {
        super.afterDone();
        AggregateFuture<InputT, OutputT> localRunningState = this.runningState;
        if (localRunningState == null) return;
        this.runningState = null;
        ImmutableCollection futures = localRunningState.futures;
        boolean wasInterrupted = this.wasInterrupted();
        if (this.wasInterrupted()) {
            ((RunningState)((Object)localRunningState)).interruptTask();
        }
        if (!(this.isCancelled() & futures != null)) return;
        Iterator i$ = futures.iterator();
        while (i$.hasNext()) {
            ListenableFuture future = (ListenableFuture)i$.next();
            future.cancel((boolean)wasInterrupted);
        }
    }

    final void init(AggregateFuture<InputT, OutputT> runningState) {
        this.runningState = runningState;
        runningState.init();
    }

    private static boolean addCausalChain(Set<Throwable> seen, Throwable t) {
        while (t != null) {
            boolean firstTimeSeen = seen.add((Throwable)t);
            if (!firstTimeSeen) {
                return false;
            }
            t = t.getCause();
        }
        return true;
    }

    static /* synthetic */ boolean access$400(Set x0, Throwable x1) {
        return AggregateFuture.addCausalChain((Set<Throwable>)x0, (Throwable)x1);
    }

    static /* synthetic */ Logger access$500() {
        return logger;
    }

    static /* synthetic */ RunningState access$602(AggregateFuture x0, RunningState x1) {
        x0.runningState = x1;
        return x0.runningState;
    }
}

