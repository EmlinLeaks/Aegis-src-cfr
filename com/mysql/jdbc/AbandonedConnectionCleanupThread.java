/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.NetworkResources;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbandonedConnectionCleanupThread
implements Runnable {
    private static final Map<ConnectionFinalizerPhantomReference, ConnectionFinalizerPhantomReference> connectionFinalizerPhantomRefs;
    private static final ReferenceQueue<MySQLConnection> referenceQueue;
    private static final ExecutorService cleanupThreadExcecutorService;
    static Thread threadRef;
    private static Lock threadRefLock;

    private AbandonedConnectionCleanupThread() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        do {
            try {
                do {
                    this.checkThreadContextClassLoader();
                    Reference<MySQLConnection> reference = referenceQueue.remove((long)5000L);
                    if (reference == null) continue;
                    AbandonedConnectionCleanupThread.finalizeResource((ConnectionFinalizerPhantomReference)((ConnectionFinalizerPhantomReference)reference));
                } while (true);
            }
            catch (InterruptedException e) {
                threadRefLock.lock();
                try {
                    threadRef = null;
                    do {
                        Reference<MySQLConnection> reference;
                        if ((reference = referenceQueue.poll()) == null) {
                            connectionFinalizerPhantomRefs.clear();
                            Object var4_5 = null;
                            threadRefLock.unlock();
                            return;
                        }
                        AbandonedConnectionCleanupThread.finalizeResource((ConnectionFinalizerPhantomReference)((ConnectionFinalizerPhantomReference)reference));
                    } while (true);
                }
                catch (Throwable throwable) {
                    Object var4_6 = null;
                    threadRefLock.unlock();
                    throw throwable;
                }
            }
            catch (Exception ex) {
                continue;
            }
            break;
        } while (true);
    }

    private void checkThreadContextClassLoader() {
        try {
            threadRef.getContextClassLoader().getResource((String)"");
            return;
        }
        catch (Throwable e) {
            AbandonedConnectionCleanupThread.uncheckedShutdown();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean consistentClassLoaders() {
        threadRefLock.lock();
        try {
            if (threadRef == null) {
                boolean bl = false;
                Object var4_2 = null;
                threadRefLock.unlock();
                return bl;
            }
            ClassLoader callerCtxClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader threadCtxClassLoader = threadRef.getContextClassLoader();
            boolean bl = callerCtxClassLoader != null && threadCtxClassLoader != null && callerCtxClassLoader == threadCtxClassLoader;
            Object var4_3 = null;
            threadRefLock.unlock();
            return bl;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            threadRefLock.unlock();
            throw throwable;
        }
    }

    private static void shutdown(boolean checked) {
        if (checked && !AbandonedConnectionCleanupThread.consistentClassLoaders()) {
            return;
        }
        cleanupThreadExcecutorService.shutdownNow();
    }

    public static void checkedShutdown() {
        AbandonedConnectionCleanupThread.shutdown((boolean)true);
    }

    public static void uncheckedShutdown() {
        AbandonedConnectionCleanupThread.shutdown((boolean)false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isAlive() {
        threadRefLock.lock();
        try {
            boolean bl = threadRef != null && threadRef.isAlive();
            Object var2_1 = null;
            threadRefLock.unlock();
            return bl;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            threadRefLock.unlock();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static void trackConnection(MySQLConnection conn, NetworkResources io) {
        threadRefLock.lock();
        try {
            if (AbandonedConnectionCleanupThread.isAlive()) {
                ConnectionFinalizerPhantomReference reference = new ConnectionFinalizerPhantomReference((MySQLConnection)conn, (NetworkResources)io, referenceQueue);
                connectionFinalizerPhantomRefs.put((ConnectionFinalizerPhantomReference)reference, (ConnectionFinalizerPhantomReference)reference);
            }
            Object var4_3 = null;
            threadRefLock.unlock();
            return;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            threadRefLock.unlock();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void finalizeResource(ConnectionFinalizerPhantomReference reference) {
        try {
            reference.finalizeResources();
            reference.clear();
            Object var2_1 = null;
            connectionFinalizerPhantomRefs.remove((Object)reference);
            return;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            connectionFinalizerPhantomRefs.remove((Object)reference);
            throw throwable;
        }
    }

    public static Thread getThread() {
        return threadRef;
    }

    static {
        referenceQueue = new ReferenceQueue<T>();
        threadRef = null;
        threadRefLock = new ReentrantLock();
        connectionFinalizerPhantomRefs = new ConcurrentHashMap<ConnectionFinalizerPhantomReference, ConnectionFinalizerPhantomReference>();
        cleanupThreadExcecutorService = Executors.newSingleThreadExecutor((ThreadFactory)new ThreadFactory(){

            public Thread newThread(Runnable r) {
                Thread t = new Thread((Runnable)r, (String)"mysql-cj-abandoned-connection-cleanup");
                t.setDaemon((boolean)true);
                ClassLoader classLoader = AbandonedConnectionCleanupThread.class.getClassLoader();
                if (classLoader == null) {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                t.setContextClassLoader((ClassLoader)classLoader);
                threadRef = t;
                return threadRef;
            }
        });
        cleanupThreadExcecutorService.execute((Runnable)new AbandonedConnectionCleanupThread());
    }
}

