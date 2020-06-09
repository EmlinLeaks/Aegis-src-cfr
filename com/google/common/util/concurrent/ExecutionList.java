/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.GuardedBy
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ExecutionList;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@GwtIncompatible
public final class ExecutionList {
    private static final Logger log = Logger.getLogger((String)ExecutionList.class.getName());
    @GuardedBy(value="this")
    private RunnableExecutorPair runnables;
    @GuardedBy(value="this")
    private boolean executed;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(Runnable runnable, Executor executor) {
        Preconditions.checkNotNull(runnable, (Object)"Runnable was null.");
        Preconditions.checkNotNull(executor, (Object)"Executor was null.");
        ExecutionList executionList = this;
        // MONITORENTER : executionList
        if (!this.executed) {
            this.runnables = new RunnableExecutorPair((Runnable)runnable, (Executor)executor, (RunnableExecutorPair)this.runnables);
            // MONITOREXIT : executionList
            return;
        }
        // MONITOREXIT : executionList
        ExecutionList.executeListener((Runnable)runnable, (Executor)executor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute() {
        ExecutionList executionList = this;
        // MONITORENTER : executionList
        if (this.executed) {
            // MONITOREXIT : executionList
            return;
        }
        this.executed = true;
        RunnableExecutorPair list = this.runnables;
        this.runnables = null;
        // MONITOREXIT : executionList
        RunnableExecutorPair reversedList = null;
        do {
            if (list == null) {
                while (reversedList != null) {
                    ExecutionList.executeListener((Runnable)reversedList.runnable, (Executor)reversedList.executor);
                    reversedList = reversedList.next;
                }
                return;
            }
            RunnableExecutorPair tmp = list;
            list = list.next;
            tmp.next = reversedList;
            reversedList = tmp;
        } while (true);
    }

    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute((Runnable)runnable);
            return;
        }
        catch (RuntimeException e) {
            log.log((Level)Level.SEVERE, (String)("RuntimeException while executing runnable " + runnable + " with executor " + executor), (Throwable)e);
        }
    }
}

