/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.ThreadDeathWatcher;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public final class ThreadDeathWatcher {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadDeathWatcher.class);
    static final ThreadFactory threadFactory;
    private static final Queue<Entry> pendingEntries;
    private static final Watcher watcher;
    private static final AtomicBoolean started;
    private static volatile Thread watcherThread;

    public static void watch(Thread thread, Runnable task) {
        if (thread == null) {
            throw new NullPointerException((String)"thread");
        }
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        if (!thread.isAlive()) {
            throw new IllegalArgumentException((String)"thread must be alive.");
        }
        ThreadDeathWatcher.schedule((Thread)thread, (Runnable)task, (boolean)true);
    }

    public static void unwatch(Thread thread, Runnable task) {
        if (thread == null) {
            throw new NullPointerException((String)"thread");
        }
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        ThreadDeathWatcher.schedule((Thread)thread, (Runnable)task, (boolean)false);
    }

    private static void schedule(Thread thread, Runnable task, boolean isWatch) {
        pendingEntries.add((Entry)new Entry((Thread)thread, (Runnable)task, (boolean)isWatch));
        if (!started.compareAndSet((boolean)false, (boolean)true)) return;
        Thread watcherThread = threadFactory.newThread((Runnable)watcher);
        AccessController.doPrivileged(new PrivilegedAction<Void>((Thread)watcherThread){
            final /* synthetic */ Thread val$watcherThread;
            {
                this.val$watcherThread = thread;
            }

            public Void run() {
                this.val$watcherThread.setContextClassLoader(null);
                return null;
            }
        });
        watcherThread.start();
        ThreadDeathWatcher.watcherThread = watcherThread;
    }

    public static boolean awaitInactivity(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        Thread watcherThread = ThreadDeathWatcher.watcherThread;
        if (watcherThread == null) return true;
        watcherThread.join((long)unit.toMillis((long)timeout));
        if (watcherThread.isAlive()) return false;
        return true;
    }

    private ThreadDeathWatcher() {
    }

    static /* synthetic */ Queue access$100() {
        return pendingEntries;
    }

    static /* synthetic */ AtomicBoolean access$200() {
        return started;
    }

    static /* synthetic */ InternalLogger access$300() {
        return logger;
    }

    static {
        pendingEntries = new ConcurrentLinkedQueue<Entry>();
        watcher = new Watcher(null);
        started = new AtomicBoolean();
        String poolName = "threadDeathWatcher";
        String serviceThreadPrefix = SystemPropertyUtil.get((String)"io.netty.serviceThreadPrefix");
        if (!StringUtil.isNullOrEmpty((String)serviceThreadPrefix)) {
            poolName = serviceThreadPrefix + poolName;
        }
        threadFactory = new DefaultThreadFactory((String)poolName, (boolean)true, (int)1, null);
    }
}

