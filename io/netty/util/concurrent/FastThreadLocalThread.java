/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.FastThreadLocalRunnable;
import io.netty.util.internal.InternalThreadLocalMap;

public class FastThreadLocalThread
extends Thread {
    private final boolean cleanupFastThreadLocals;
    private InternalThreadLocalMap threadLocalMap;

    public FastThreadLocalThread() {
        this.cleanupFastThreadLocals = false;
    }

    public FastThreadLocalThread(Runnable target) {
        super((Runnable)FastThreadLocalRunnable.wrap((Runnable)target));
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(ThreadGroup group, Runnable target) {
        super((ThreadGroup)group, (Runnable)FastThreadLocalRunnable.wrap((Runnable)target));
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(String name) {
        super((String)name);
        this.cleanupFastThreadLocals = false;
    }

    public FastThreadLocalThread(ThreadGroup group, String name) {
        super((ThreadGroup)group, (String)name);
        this.cleanupFastThreadLocals = false;
    }

    public FastThreadLocalThread(Runnable target, String name) {
        super((Runnable)FastThreadLocalRunnable.wrap((Runnable)target), (String)name);
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(ThreadGroup group, Runnable target, String name) {
        super((ThreadGroup)group, (Runnable)FastThreadLocalRunnable.wrap((Runnable)target), (String)name);
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super((ThreadGroup)group, (Runnable)FastThreadLocalRunnable.wrap((Runnable)target), (String)name, (long)stackSize);
        this.cleanupFastThreadLocals = true;
    }

    public final InternalThreadLocalMap threadLocalMap() {
        return this.threadLocalMap;
    }

    public final void setThreadLocalMap(InternalThreadLocalMap threadLocalMap) {
        this.threadLocalMap = threadLocalMap;
    }

    public boolean willCleanupFastThreadLocals() {
        return this.cleanupFastThreadLocals;
    }

    public static boolean willCleanupFastThreadLocals(Thread thread) {
        if (!(thread instanceof FastThreadLocalThread)) return false;
        if (!((FastThreadLocalThread)thread).willCleanupFastThreadLocals()) return false;
        return true;
    }
}

