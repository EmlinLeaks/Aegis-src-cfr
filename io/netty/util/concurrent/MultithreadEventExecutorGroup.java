/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultEventExecutorChooserFactory;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultithreadEventExecutorGroup
extends AbstractEventExecutorGroup {
    private final EventExecutor[] children;
    private final Set<EventExecutor> readonlyChildren;
    private final AtomicInteger terminatedChildren = new AtomicInteger();
    private final Promise<?> terminationFuture = new DefaultPromise<?>((EventExecutor)GlobalEventExecutor.INSTANCE);
    private final EventExecutorChooserFactory.EventExecutorChooser chooser;

    protected MultithreadEventExecutorGroup(int nThreads, ThreadFactory threadFactory, Object ... args) {
        this((int)nThreads, (Executor)(threadFactory == null ? null : new ThreadPerTaskExecutor((ThreadFactory)threadFactory)), (Object[])args);
    }

    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, Object ... args) {
        this((int)nThreads, (Executor)executor, (EventExecutorChooserFactory)DefaultEventExecutorChooserFactory.INSTANCE, (Object[])args);
    }

    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, Object ... args) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException((String)String.format((String)"nThreads: %d (expected: > 0)", (Object[])new Object[]{Integer.valueOf((int)nThreads)}));
        }
        if (executor == null) {
            executor = new ThreadPerTaskExecutor((ThreadFactory)this.newDefaultThreadFactory());
        }
        this.children = new EventExecutor[nThreads];
        for (int i = 0; i < nThreads; ++i) {
            boolean success = false;
            try {
                this.children[i] = this.newChild((Executor)executor, (Object[])args);
                success = true;
                continue;
            }
            catch (Exception e) {
                throw new IllegalStateException((String)"failed to create a child event loop", (Throwable)e);
            }
            finally {
                if (!success) {
                    int j;
                    for (j = 0; j < i; ++j) {
                        this.children[j].shutdownGracefully();
                    }
                    for (j = 0; j < i; ++j) {
                        EventExecutor e = this.children[j];
                        try {
                            while (!e.isTerminated()) {
                                e.awaitTermination((long)Integer.MAX_VALUE, (TimeUnit)TimeUnit.SECONDS);
                            }
                            continue;
                        }
                        catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }
        this.chooser = chooserFactory.newChooser((EventExecutor[])this.children);
        FutureListener<Object> terminationListener = new FutureListener<Object>((MultithreadEventExecutorGroup)this){
            final /* synthetic */ MultithreadEventExecutorGroup this$0;
            {
                this.this$0 = this$0;
            }

            public void operationComplete(Future<Object> future) throws Exception {
                if (MultithreadEventExecutorGroup.access$000((MultithreadEventExecutorGroup)this.this$0).incrementAndGet() != MultithreadEventExecutorGroup.access$100((MultithreadEventExecutorGroup)this.this$0).length) return;
                MultithreadEventExecutorGroup.access$200((MultithreadEventExecutorGroup)this.this$0).setSuccess(null);
            }
        };
        EventExecutor[] success = this.children;
        int e = success.length;
        int e2 = 0;
        do {
            if (e2 >= e) {
                LinkedHashSet<E> childrenSet = new LinkedHashSet<E>((int)this.children.length);
                Collections.addAll(childrenSet, this.children);
                this.readonlyChildren = Collections.unmodifiableSet(childrenSet);
                return;
            }
            EventExecutor e3 = success[e2];
            e3.terminationFuture().addListener(terminationListener);
            ++e2;
        } while (true);
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(this.getClass());
    }

    @Override
    public EventExecutor next() {
        return this.chooser.next();
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return this.readonlyChildren.iterator();
    }

    public final int executorCount() {
        return this.children.length;
    }

    protected abstract EventExecutor newChild(Executor var1, Object ... var2) throws Exception;

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        EventExecutor[] arreventExecutor = this.children;
        int n = arreventExecutor.length;
        int n2 = 0;
        while (n2 < n) {
            EventExecutor l = arreventExecutor[n2];
            l.shutdownGracefully((long)quietPeriod, (long)timeout, (TimeUnit)unit);
            ++n2;
        }
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Deprecated
    @Override
    public void shutdown() {
        EventExecutor[] arreventExecutor = this.children;
        int n = arreventExecutor.length;
        int n2 = 0;
        while (n2 < n) {
            EventExecutor l = arreventExecutor[n2];
            l.shutdown();
            ++n2;
        }
    }

    @Override
    public boolean isShuttingDown() {
        EventExecutor[] arreventExecutor = this.children;
        int n = arreventExecutor.length;
        int n2 = 0;
        while (n2 < n) {
            EventExecutor l = arreventExecutor[n2];
            if (!l.isShuttingDown()) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    @Override
    public boolean isShutdown() {
        EventExecutor[] arreventExecutor = this.children;
        int n = arreventExecutor.length;
        int n2 = 0;
        while (n2 < n) {
            EventExecutor l = arreventExecutor[n2];
            if (!l.isShutdown()) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        EventExecutor[] arreventExecutor = this.children;
        int n = arreventExecutor.length;
        int n2 = 0;
        while (n2 < n) {
            EventExecutor l = arreventExecutor[n2];
            if (!l.isTerminated()) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos((long)timeout);
        EventExecutor[] arreventExecutor = this.children;
        int n = arreventExecutor.length;
        int n2 = 0;
        while (n2 < n) {
            long timeLeft;
            EventExecutor l = arreventExecutor[n2];
            do {
                if ((timeLeft = deadline - System.nanoTime()) > 0L) continue;
                return this.isTerminated();
            } while (!l.awaitTermination((long)timeLeft, (TimeUnit)TimeUnit.NANOSECONDS));
            ++n2;
        }
        return this.isTerminated();
    }

    static /* synthetic */ AtomicInteger access$000(MultithreadEventExecutorGroup x0) {
        return x0.terminatedChildren;
    }

    static /* synthetic */ EventExecutor[] access$100(MultithreadEventExecutorGroup x0) {
        return x0.children;
    }

    static /* synthetic */ Promise access$200(MultithreadEventExecutorGroup x0) {
        return x0.terminationFuture;
    }
}

