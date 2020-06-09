/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.util.concurrent.InterruptibleTask;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;

@GwtCompatible(emulated=true)
abstract class InterruptibleTask
implements Runnable {
    private volatile Thread runner;
    private volatile boolean doneInterrupting;
    private static final AtomicHelper ATOMIC_HELPER;
    private static final Logger log;

    InterruptibleTask() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void run() {
        if (!ATOMIC_HELPER.compareAndSetRunner((InterruptibleTask)this, null, (Thread)Thread.currentThread())) {
            return;
        }
        try {
            this.runInterruptibly();
            return;
        }
        finally {
            if (this.wasInterrupted()) {
                while (!this.doneInterrupting) {
                    Thread.yield();
                }
            }
        }
    }

    abstract void runInterruptibly();

    abstract boolean wasInterrupted();

    final void interruptTask() {
        Thread currentRunner = this.runner;
        if (currentRunner != null) {
            currentRunner.interrupt();
        }
        this.doneInterrupting = true;
    }

    static /* synthetic */ Thread access$200(InterruptibleTask x0) {
        return x0.runner;
    }

    static /* synthetic */ Thread access$202(InterruptibleTask x0, Thread x1) {
        x0.runner = x1;
        return x0.runner;
    }

    static {
        AtomicHelper helper;
        log = Logger.getLogger((String)InterruptibleTask.class.getName());
        try {
            helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(InterruptibleTask.class, Thread.class, (String)"runner"));
        }
        catch (Throwable reflectionFailure) {
            log.log((Level)Level.SEVERE, (String)"SafeAtomicHelper is broken!", (Throwable)reflectionFailure);
            helper = new SynchronizedAtomicHelper(null);
        }
        ATOMIC_HELPER = helper;
    }
}

