/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.WrappingExecutorService;
import com.google.common.util.concurrent.WrappingScheduledExecutorService;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@GwtCompatible(emulated=true)
public final class MoreExecutors {
    private MoreExecutors() {
    }

    @Beta
    @GwtIncompatible
    public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
        return new Application().getExitingExecutorService((ThreadPoolExecutor)executor, (long)terminationTimeout, (TimeUnit)timeUnit);
    }

    @Beta
    @GwtIncompatible
    public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
        return new Application().getExitingScheduledExecutorService((ScheduledThreadPoolExecutor)executor, (long)terminationTimeout, (TimeUnit)timeUnit);
    }

    @Beta
    @GwtIncompatible
    public static void addDelayedShutdownHook(ExecutorService service, long terminationTimeout, TimeUnit timeUnit) {
        new Application().addDelayedShutdownHook((ExecutorService)service, (long)terminationTimeout, (TimeUnit)timeUnit);
    }

    @Beta
    @GwtIncompatible
    public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor) {
        return new Application().getExitingExecutorService((ThreadPoolExecutor)executor);
    }

    @Beta
    @GwtIncompatible
    public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor) {
        return new Application().getExitingScheduledExecutorService((ScheduledThreadPoolExecutor)executor);
    }

    @GwtIncompatible
    private static void useDaemonThreadFactory(ThreadPoolExecutor executor) {
        executor.setThreadFactory((ThreadFactory)new ThreadFactoryBuilder().setDaemon((boolean)true).setThreadFactory((ThreadFactory)executor.getThreadFactory()).build());
    }

    @Deprecated
    @GwtIncompatible
    public static ListeningExecutorService sameThreadExecutor() {
        return new DirectExecutorService(null);
    }

    @GwtIncompatible
    public static ListeningExecutorService newDirectExecutorService() {
        return new DirectExecutorService(null);
    }

    public static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }

    @GwtIncompatible
    public static ListeningExecutorService listeningDecorator(ExecutorService delegate) {
        ListeningExecutorService listeningExecutorService;
        if (delegate instanceof ListeningExecutorService) {
            listeningExecutorService = (ListeningExecutorService)delegate;
            return listeningExecutorService;
        }
        if (delegate instanceof ScheduledExecutorService) {
            listeningExecutorService = new ScheduledListeningDecorator((ScheduledExecutorService)((ScheduledExecutorService)delegate));
            return listeningExecutorService;
        }
        listeningExecutorService = new ListeningDecorator((ExecutorService)delegate);
        return listeningExecutorService;
    }

    @GwtIncompatible
    public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService delegate) {
        ListeningScheduledExecutorService listeningScheduledExecutorService;
        if (delegate instanceof ListeningScheduledExecutorService) {
            listeningScheduledExecutorService = (ListeningScheduledExecutorService)delegate;
            return listeningScheduledExecutorService;
        }
        listeningScheduledExecutorService = new ScheduledListeningDecorator((ScheduledExecutorService)delegate);
        return listeningScheduledExecutorService;
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @GwtIncompatible
    static <T> T invokeAnyImpl(ListeningExecutorService executorService, Collection<? extends Callable<T>> tasks, boolean timed, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Preconditions.checkNotNull(executorService);
        Preconditions.checkNotNull(unit);
        ntasks = tasks.size();
        Preconditions.checkArgument((boolean)(ntasks > 0));
        futures = Lists.newArrayListWithCapacity((int)ntasks);
        futureQueue = Queues.newLinkedBlockingQueue();
        timeoutNanos = unit.toNanos((long)timeout);
        ee = null;
        lastTime = timed != false ? System.nanoTime() : 0L;
        it = tasks.iterator();
        futures.add(MoreExecutors.submitAndAddQueueListener((ListeningExecutorService)executorService, it.next(), futureQueue));
        --ntasks;
        active = 1;
        do lbl-1000: // 4 sources:
        {
            if ((f = (Future)futureQueue.poll()) == null) {
                if (ntasks > 0) {
                    --ntasks;
                    futures.add(MoreExecutors.submitAndAddQueueListener((ListeningExecutorService)executorService, it.next(), futureQueue));
                    ++active;
                } else {
                    if (active == 0) {
                        if (ee != null) throw ee;
                        ee = new ExecutionException(null);
                        throw ee;
                    }
                    if (timed) {
                        f = (Future)futureQueue.poll((long)timeoutNanos, (TimeUnit)TimeUnit.NANOSECONDS);
                        if (f == null) {
                            throw new TimeoutException();
                        }
                        now = System.nanoTime();
                        timeoutNanos -= now - lastTime;
                        lastTime = now;
                    } else {
                        f = (Future)futureQueue.take();
                    }
                }
            }
            if (f == null) continue;
            --active;
            try {
                now = f.get();
                return (T)((T)now);
            }
            catch (ExecutionException eex) {
                ee = eex;
            }
            catch (RuntimeException rex) {
                ee = new ExecutionException((Throwable)rex);
                continue;
            }
            break;
        } while (true);
        ** GOTO lbl-1000
        finally {
            i$ = futures.iterator();
            do {
                if (!i$.hasNext()) {
                }
                f = (Future)i$.next();
                f.cancel((boolean)true);
            } while (true);
        }
    }

    @GwtIncompatible
    private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService executorService, Callable<T> task, BlockingQueue<Future<T>> queue) {
        ListenableFuture<T> future = executorService.submit(task);
        future.addListener((Runnable)new Runnable(queue, future){
            final /* synthetic */ BlockingQueue val$queue;
            final /* synthetic */ ListenableFuture val$future;
            {
                this.val$queue = blockingQueue;
                this.val$future = listenableFuture;
            }

            public void run() {
                this.val$queue.add(this.val$future);
            }
        }, (Executor)MoreExecutors.directExecutor());
        return future;
    }

    @Beta
    @GwtIncompatible
    public static ThreadFactory platformThreadFactory() {
        if (!MoreExecutors.isAppEngine()) {
            return Executors.defaultThreadFactory();
        }
        try {
            return (ThreadFactory)Class.forName((String)"com.google.appengine.api.ThreadManager").getMethod((String)"currentRequestThreadFactory", new Class[0]).invoke(null, (Object[])new Object[0]);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException((String)"Couldn't invoke ThreadManager.currentRequestThreadFactory", (Throwable)e);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException((String)"Couldn't invoke ThreadManager.currentRequestThreadFactory", (Throwable)e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException((String)"Couldn't invoke ThreadManager.currentRequestThreadFactory", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            throw Throwables.propagate((Throwable)e.getCause());
        }
    }

    @GwtIncompatible
    private static boolean isAppEngine() {
        if (System.getProperty((String)"com.google.appengine.runtime.environment") == null) {
            return false;
        }
        try {
            if (Class.forName((String)"com.google.apphosting.api.ApiProxy").getMethod((String)"getCurrentEnvironment", new Class[0]).invoke(null, (Object[])new Object[0]) == null) return false;
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            return false;
        }
        catch (IllegalAccessException e) {
            return false;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    @GwtIncompatible
    static Thread newThread(String name, Runnable runnable) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(runnable);
        Thread result = MoreExecutors.platformThreadFactory().newThread((Runnable)runnable);
        try {
            result.setName((String)name);
            return result;
        }
        catch (SecurityException e) {
            // empty catch block
        }
        return result;
    }

    @GwtIncompatible
    static Executor renamingDecorator(Executor executor, Supplier<String> nameSupplier) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(nameSupplier);
        if (!MoreExecutors.isAppEngine()) return new Executor((Executor)executor, nameSupplier){
            final /* synthetic */ Executor val$executor;
            final /* synthetic */ Supplier val$nameSupplier;
            {
                this.val$executor = executor;
                this.val$nameSupplier = supplier;
            }

            public void execute(Runnable command) {
                this.val$executor.execute((Runnable)com.google.common.util.concurrent.Callables.threadRenaming((Runnable)command, (Supplier<String>)this.val$nameSupplier));
            }
        };
        return executor;
    }

    @GwtIncompatible
    static ExecutorService renamingDecorator(ExecutorService service, Supplier<String> nameSupplier) {
        Preconditions.checkNotNull(service);
        Preconditions.checkNotNull(nameSupplier);
        if (!MoreExecutors.isAppEngine()) return new WrappingExecutorService((ExecutorService)service, nameSupplier){
            final /* synthetic */ Supplier val$nameSupplier;
            {
                this.val$nameSupplier = supplier;
                super((ExecutorService)x0);
            }

            protected <T> Callable<T> wrapTask(Callable<T> callable) {
                return com.google.common.util.concurrent.Callables.threadRenaming(callable, (Supplier<String>)this.val$nameSupplier);
            }

            protected Runnable wrapTask(Runnable command) {
                return com.google.common.util.concurrent.Callables.threadRenaming((Runnable)command, (Supplier<String>)this.val$nameSupplier);
            }
        };
        return service;
    }

    @GwtIncompatible
    static ScheduledExecutorService renamingDecorator(ScheduledExecutorService service, Supplier<String> nameSupplier) {
        Preconditions.checkNotNull(service);
        Preconditions.checkNotNull(nameSupplier);
        if (!MoreExecutors.isAppEngine()) return new WrappingScheduledExecutorService((ScheduledExecutorService)service, nameSupplier){
            final /* synthetic */ Supplier val$nameSupplier;
            {
                this.val$nameSupplier = supplier;
                super((ScheduledExecutorService)x0);
            }

            protected <T> Callable<T> wrapTask(Callable<T> callable) {
                return com.google.common.util.concurrent.Callables.threadRenaming(callable, (Supplier<String>)this.val$nameSupplier);
            }

            protected Runnable wrapTask(Runnable command) {
                return com.google.common.util.concurrent.Callables.threadRenaming((Runnable)command, (Supplier<String>)this.val$nameSupplier);
            }
        };
        return service;
    }

    @Beta
    @CanIgnoreReturnValue
    @GwtIncompatible
    public static boolean shutdownAndAwaitTermination(ExecutorService service, long timeout, TimeUnit unit) {
        long halfTimeoutNanos = unit.toNanos((long)timeout) / 2L;
        service.shutdown();
        try {
            if (service.awaitTermination((long)halfTimeoutNanos, (TimeUnit)TimeUnit.NANOSECONDS)) return service.isTerminated();
            service.shutdownNow();
            service.awaitTermination((long)halfTimeoutNanos, (TimeUnit)TimeUnit.NANOSECONDS);
            return service.isTerminated();
        }
        catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            service.shutdownNow();
        }
        return service.isTerminated();
    }

    static Executor rejectionPropagatingExecutor(Executor delegate, AbstractFuture<?> future) {
        Preconditions.checkNotNull(delegate);
        Preconditions.checkNotNull(future);
        if (delegate != MoreExecutors.directExecutor()) return new Executor((Executor)delegate, future){
            volatile boolean thrownFromDelegate;
            final /* synthetic */ Executor val$delegate;
            final /* synthetic */ AbstractFuture val$future;
            {
                this.val$delegate = executor;
                this.val$future = abstractFuture;
                this.thrownFromDelegate = true;
            }

            public void execute(Runnable command) {
                try {
                    this.val$delegate.execute((Runnable)new Runnable(this, (Runnable)command){
                        final /* synthetic */ Runnable val$command;
                        final /* synthetic */ 5 this$0;
                        {
                            this.this$0 = var1_1;
                            this.val$command = runnable;
                        }

                        public void run() {
                            this.this$0.thrownFromDelegate = false;
                            this.val$command.run();
                        }
                    });
                    return;
                }
                catch (java.util.concurrent.RejectedExecutionException e) {
                    if (!this.thrownFromDelegate) return;
                    this.val$future.setException((Throwable)e);
                }
            }
        };
        return delegate;
    }

    static /* synthetic */ void access$000(ThreadPoolExecutor x0) {
        MoreExecutors.useDaemonThreadFactory((ThreadPoolExecutor)x0);
    }
}

