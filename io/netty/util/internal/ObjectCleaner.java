/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.ConcurrentSet;
import io.netty.util.internal.ObjectCleaner;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ObjectCleaner {
    private static final int REFERENCE_QUEUE_POLL_TIMEOUT_MS = Math.max((int)500, (int)SystemPropertyUtil.getInt((String)"io.netty.util.internal.ObjectCleaner.refQueuePollTimeout", (int)10000));
    static final String CLEANER_THREAD_NAME = ObjectCleaner.class.getSimpleName() + "Thread";
    private static final Set<AutomaticCleanerReference> LIVE_SET = new ConcurrentSet<AutomaticCleanerReference>();
    private static final ReferenceQueue<Object> REFERENCE_QUEUE = new ReferenceQueue<T>();
    private static final AtomicBoolean CLEANER_RUNNING = new AtomicBoolean((boolean)false);
    private static final Runnable CLEANER_TASK = new Runnable(){

        public void run() {
            boolean interrupted = false;
            do {
                if (!ObjectCleaner.access$000().isEmpty()) {
                    AutomaticCleanerReference reference;
                    try {
                        reference = (AutomaticCleanerReference)ObjectCleaner.access$200().remove((long)((long)ObjectCleaner.access$100()));
                    }
                    catch (java.lang.InterruptedException ex) {
                        interrupted = true;
                        continue;
                    }
                    if (reference == null) continue;
                    try {
                        reference.cleanup();
                    }
                    catch (java.lang.Throwable ex) {
                        // empty catch block
                    }
                    ObjectCleaner.access$000().remove((Object)reference);
                    continue;
                }
                ObjectCleaner.access$300().set((boolean)false);
                if (ObjectCleaner.access$000().isEmpty() || !ObjectCleaner.access$300().compareAndSet((boolean)false, (boolean)true)) break;
            } while (true);
            if (!interrupted) return;
            Thread.currentThread().interrupt();
        }
    };

    public static void register(Object object, Runnable cleanupTask) {
        AutomaticCleanerReference reference = new AutomaticCleanerReference((Object)object, (Runnable)ObjectUtil.checkNotNull(cleanupTask, (String)"cleanupTask"));
        LIVE_SET.add((AutomaticCleanerReference)reference);
        if (!CLEANER_RUNNING.compareAndSet((boolean)false, (boolean)true)) return;
        FastThreadLocalThread cleanupThread = new FastThreadLocalThread((Runnable)CLEANER_TASK);
        cleanupThread.setPriority((int)1);
        AccessController.doPrivileged(new PrivilegedAction<Void>((Thread)cleanupThread){
            final /* synthetic */ Thread val$cleanupThread;
            {
                this.val$cleanupThread = thread;
            }

            public Void run() {
                this.val$cleanupThread.setContextClassLoader(null);
                return null;
            }
        });
        cleanupThread.setName((String)CLEANER_THREAD_NAME);
        cleanupThread.setDaemon((boolean)true);
        cleanupThread.start();
    }

    public static int getLiveSetCount() {
        return LIVE_SET.size();
    }

    private ObjectCleaner() {
    }

    static /* synthetic */ Set access$000() {
        return LIVE_SET;
    }

    static /* synthetic */ int access$100() {
        return REFERENCE_QUEUE_POLL_TIMEOUT_MS;
    }

    static /* synthetic */ ReferenceQueue access$200() {
        return REFERENCE_QUEUE;
    }

    static /* synthetic */ AtomicBoolean access$300() {
        return CLEANER_RUNNING;
    }
}

