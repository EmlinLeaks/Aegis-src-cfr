/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.Callables;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Callables {
    private Callables() {
    }

    public static <T> Callable<T> returning(@Nullable T value) {
        return new Callable<T>(value){
            final /* synthetic */ Object val$value;
            {
                this.val$value = object;
            }

            public T call() {
                return (T)this.val$value;
            }
        };
    }

    @Beta
    @GwtIncompatible
    public static <T> AsyncCallable<T> asAsyncCallable(Callable<T> callable, ListeningExecutorService listeningExecutorService) {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull(listeningExecutorService);
        return new AsyncCallable<T>((ListeningExecutorService)listeningExecutorService, callable){
            final /* synthetic */ ListeningExecutorService val$listeningExecutorService;
            final /* synthetic */ Callable val$callable;
            {
                this.val$listeningExecutorService = listeningExecutorService;
                this.val$callable = callable;
            }

            public com.google.common.util.concurrent.ListenableFuture<T> call() throws java.lang.Exception {
                return this.val$listeningExecutorService.submit(this.val$callable);
            }
        };
    }

    @GwtIncompatible
    static <T> Callable<T> threadRenaming(Callable<T> callable, Supplier<String> nameSupplier) {
        Preconditions.checkNotNull(nameSupplier);
        Preconditions.checkNotNull(callable);
        return new Callable<T>(nameSupplier, callable){
            final /* synthetic */ Supplier val$nameSupplier;
            final /* synthetic */ Callable val$callable;
            {
                this.val$nameSupplier = supplier;
                this.val$callable = callable;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public T call() throws java.lang.Exception {
                Thread currentThread = Thread.currentThread();
                String oldName = currentThread.getName();
                boolean restoreName = Callables.access$000((String)((String)this.val$nameSupplier.get()), (Thread)currentThread);
                try {
                    V v = this.val$callable.call();
                    return (T)((T)v);
                }
                finally {
                    if (restoreName) {
                        boolean unused = Callables.access$000((String)oldName, (Thread)currentThread);
                    }
                }
            }
        };
    }

    @GwtIncompatible
    static Runnable threadRenaming(Runnable task, Supplier<String> nameSupplier) {
        Preconditions.checkNotNull(nameSupplier);
        Preconditions.checkNotNull(task);
        return new Runnable(nameSupplier, (Runnable)task){
            final /* synthetic */ Supplier val$nameSupplier;
            final /* synthetic */ Runnable val$task;
            {
                this.val$nameSupplier = supplier;
                this.val$task = runnable;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                Thread currentThread = Thread.currentThread();
                String oldName = currentThread.getName();
                boolean restoreName = Callables.access$000((String)((String)this.val$nameSupplier.get()), (Thread)currentThread);
                try {
                    this.val$task.run();
                    return;
                }
                finally {
                    if (restoreName) {
                        boolean unused = Callables.access$000((String)oldName, (Thread)currentThread);
                    }
                }
            }
        };
    }

    @GwtIncompatible
    private static boolean trySetName(String threadName, Thread currentThread) {
        try {
            currentThread.setName((String)threadName);
            return true;
        }
        catch (SecurityException e) {
            return false;
        }
    }

    static /* synthetic */ boolean access$000(String x0, Thread x1) {
        return Callables.trySetName((String)x0, (Thread)x1);
    }
}

