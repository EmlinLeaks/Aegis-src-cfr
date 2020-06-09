/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.GuardedBy
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SerializingExecutor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@GwtIncompatible
final class SerializingExecutor
implements Executor {
    private static final Logger log = Logger.getLogger((String)SerializingExecutor.class.getName());
    private final Executor executor;
    @GuardedBy(value="internalLock")
    private final Deque<Runnable> queue = new ArrayDeque<Runnable>();
    @GuardedBy(value="internalLock")
    private boolean isWorkerRunning = false;
    @GuardedBy(value="internalLock")
    private int suspensions = 0;
    private final Object internalLock = new Object();

    public SerializingExecutor(Executor executor) {
        this.executor = Preconditions.checkNotNull(executor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Runnable task) {
        Object object = this.internalLock;
        // MONITORENTER : object
        this.queue.add((Runnable)task);
        // MONITOREXIT : object
        this.startQueueWorker();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeFirst(Runnable task) {
        Object object = this.internalLock;
        // MONITORENTER : object
        this.queue.addFirst((Runnable)task);
        // MONITOREXIT : object
        this.startQueueWorker();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void suspend() {
        Object object = this.internalLock;
        // MONITORENTER : object
        ++this.suspensions;
        // MONITOREXIT : object
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resume() {
        Object object = this.internalLock;
        // MONITORENTER : object
        Preconditions.checkState((boolean)(this.suspensions > 0));
        --this.suspensions;
        // MONITOREXIT : object
        this.startQueueWorker();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startQueueWorker() {
        Object object = this.internalLock;
        // MONITORENTER : object
        if (this.queue.peek() == null) {
            // MONITOREXIT : object
            return;
        }
        if (this.suspensions > 0) {
            // MONITOREXIT : object
            return;
        }
        if (this.isWorkerRunning) {
            // MONITOREXIT : object
            return;
        }
        this.isWorkerRunning = true;
        // MONITOREXIT : object
        boolean executionRejected = true;
        try {
            this.executor.execute((Runnable)new QueueWorker((SerializingExecutor)this, null));
            executionRejected = false;
            return;
        }
        finally {
            if (executionRejected) {
                Object object2 = this.internalLock;
                // MONITORENTER : object2
                this.isWorkerRunning = false;
                // MONITOREXIT : object2
            }
        }
    }

    static /* synthetic */ Object access$100(SerializingExecutor x0) {
        return x0.internalLock;
    }

    static /* synthetic */ boolean access$202(SerializingExecutor x0, boolean x1) {
        x0.isWorkerRunning = x1;
        return x0.isWorkerRunning;
    }

    static /* synthetic */ int access$300(SerializingExecutor x0) {
        return x0.suspensions;
    }

    static /* synthetic */ Deque access$400(SerializingExecutor x0) {
        return x0.queue;
    }

    static /* synthetic */ Logger access$500() {
        return log;
    }
}

