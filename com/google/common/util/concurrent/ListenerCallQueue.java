/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.GuardedBy
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ListenerCallQueue;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@GwtIncompatible
final class ListenerCallQueue<L>
implements Runnable {
    private static final Logger logger = Logger.getLogger((String)ListenerCallQueue.class.getName());
    private final L listener;
    private final Executor executor;
    @GuardedBy(value="this")
    private final Queue<Callback<L>> waitQueue = Queues.newArrayDeque();
    @GuardedBy(value="this")
    private boolean isThreadScheduled;

    ListenerCallQueue(L listener, Executor executor) {
        this.listener = Preconditions.checkNotNull(listener);
        this.executor = Preconditions.checkNotNull(executor);
    }

    synchronized void add(Callback<L> callback) {
        this.waitQueue.add(callback);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void execute() {
        boolean scheduleTaskRunner = false;
        ListenerCallQueue listenerCallQueue = this;
        // MONITORENTER : listenerCallQueue
        if (!this.isThreadScheduled) {
            this.isThreadScheduled = true;
            scheduleTaskRunner = true;
        }
        // MONITOREXIT : listenerCallQueue
        if (!scheduleTaskRunner) return;
        try {
            this.executor.execute((Runnable)this);
            return;
        }
        catch (RuntimeException e) {
            ListenerCallQueue listenerCallQueue2 = this;
            // MONITORENTER : listenerCallQueue2
            this.isThreadScheduled = false;
            // MONITOREXIT : listenerCallQueue2
            logger.log((Level)Level.SEVERE, (String)("Exception while running callbacks for " + this.listener + " on " + this.executor), (Throwable)e);
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public void run() {
        stillRunning = true;
        do lbl-1000: // 3 sources:
        {
            var3_3 = this;
            // MONITORENTER : var3_3
            Preconditions.checkState((boolean)this.isThreadScheduled);
            nextToRun = this.waitQueue.poll();
            if (nextToRun == null) {
                this.isThreadScheduled = false;
                stillRunning = false;
                // MONITOREXIT : var3_3
                return;
            }
            // MONITOREXIT : var3_3
            try {
                nextToRun.call(this.listener);
            }
            catch (RuntimeException e) {
                ListenerCallQueue.logger.log((Level)Level.SEVERE, (String)("Exception while executing callback: " + this.listener + "." + Callback.access$000((Callback)nextToRun)), (Throwable)e);
                continue;
            }
            break;
        } while (true);
        ** GOTO lbl-1000
        finally {
            if (stillRunning) {
                nextToRun = this;
                // MONITORENTER : nextToRun
                this.isThreadScheduled = false;
                // MONITOREXIT : nextToRun
            }
        }
    }
}

