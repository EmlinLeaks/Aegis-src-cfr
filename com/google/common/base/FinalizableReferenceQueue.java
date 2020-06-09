/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.FinalizableReference;
import com.google.common.base.FinalizableReferenceQueue;
import java.io.Closeable;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

@GwtIncompatible
public class FinalizableReferenceQueue
implements Closeable {
    private static final Logger logger = Logger.getLogger((String)FinalizableReferenceQueue.class.getName());
    private static final String FINALIZER_CLASS_NAME = "com.google.common.base.internal.Finalizer";
    private static final Method startFinalizer;
    final ReferenceQueue<Object> queue = new ReferenceQueue<T>();
    final PhantomReference<Object> frqRef = new PhantomReference<Object>(this, this.queue);
    final boolean threadStarted;

    public FinalizableReferenceQueue() {
        boolean threadStarted = false;
        try {
            startFinalizer.invoke(null, (Object[])new Object[]{FinalizableReference.class, this.queue, this.frqRef});
            threadStarted = true;
        }
        catch (IllegalAccessException impossible) {
            throw new AssertionError((Object)impossible);
        }
        catch (Throwable t) {
            logger.log((Level)Level.INFO, (String)"Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.", (Throwable)t);
        }
        this.threadStarted = threadStarted;
    }

    @Override
    public void close() {
        this.frqRef.enqueue();
        this.cleanUp();
    }

    void cleanUp() {
        Reference<Object> reference;
        if (this.threadStarted) {
            return;
        }
        while ((reference = this.queue.poll()) != null) {
            reference.clear();
            try {
                ((FinalizableReference)((Object)reference)).finalizeReferent();
            }
            catch (Throwable t) {
                logger.log((Level)Level.SEVERE, (String)"Error cleaning up after reference.", (Throwable)t);
            }
        }
    }

    private static Class<?> loadFinalizer(FinalizerLoader ... loaders) {
        FinalizerLoader[] arr$ = loaders;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            FinalizerLoader loader = arr$[i$];
            Class<?> finalizer = loader.loadFinalizer();
            if (finalizer != null) {
                return finalizer;
            }
            ++i$;
        }
        throw new AssertionError();
    }

    static Method getStartFinalizer(Class<?> finalizer) {
        try {
            return finalizer.getMethod((String)"startFinalizer", Class.class, ReferenceQueue.class, PhantomReference.class);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
    }

    static /* synthetic */ Logger access$000() {
        return logger;
    }

    static {
        Class<?> finalizer = FinalizableReferenceQueue.loadFinalizer((FinalizerLoader[])new FinalizerLoader[]{new SystemLoader(), new DecoupledLoader(), new DirectLoader()});
        startFinalizer = FinalizableReferenceQueue.getStartFinalizer(finalizer);
    }
}

