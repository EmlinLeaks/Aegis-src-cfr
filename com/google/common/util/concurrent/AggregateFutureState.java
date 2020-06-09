/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AggregateFutureState;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;

@GwtCompatible(emulated=true)
abstract class AggregateFutureState {
    private volatile Set<Throwable> seenExceptions = null;
    private volatile int remaining;
    private static final AtomicHelper ATOMIC_HELPER;
    private static final Logger log;

    AggregateFutureState(int remainingFutures) {
        this.remaining = remainingFutures;
    }

    final Set<Throwable> getOrInitSeenExceptions() {
        Set<Throwable> seenExceptionsLocal = this.seenExceptions;
        if (seenExceptionsLocal != null) return seenExceptionsLocal;
        seenExceptionsLocal = Sets.newConcurrentHashSet();
        this.addInitialException(seenExceptionsLocal);
        ATOMIC_HELPER.compareAndSetSeenExceptions((AggregateFutureState)this, null, seenExceptionsLocal);
        return this.seenExceptions;
    }

    abstract void addInitialException(Set<Throwable> var1);

    final int decrementRemainingAndGet() {
        return ATOMIC_HELPER.decrementAndGetRemainingCount((AggregateFutureState)this);
    }

    static /* synthetic */ Set access$200(AggregateFutureState x0) {
        return x0.seenExceptions;
    }

    static /* synthetic */ Set access$202(AggregateFutureState x0, Set x1) {
        x0.seenExceptions = x1;
        return x0.seenExceptions;
    }

    static /* synthetic */ int access$310(AggregateFutureState x0) {
        return x0.remaining--;
    }

    static /* synthetic */ int access$300(AggregateFutureState x0) {
        return x0.remaining;
    }

    static {
        AtomicHelper helper;
        log = Logger.getLogger((String)AggregateFutureState.class.getName());
        try {
            helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, (String)"seenExceptions"), AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, (String)"remaining"));
        }
        catch (Throwable reflectionFailure) {
            log.log((Level)Level.SEVERE, (String)"SafeAtomicHelper is broken!", (Throwable)reflectionFailure);
            helper = new SynchronizedAtomicHelper(null);
        }
        ATOMIC_HELPER = helper;
    }
}

