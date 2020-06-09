/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.NonStickyEventExecutorGroup;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class NonStickyEventExecutorGroup
implements EventExecutorGroup {
    private final EventExecutorGroup group;
    private final int maxTaskExecutePerRun;

    public NonStickyEventExecutorGroup(EventExecutorGroup group) {
        this((EventExecutorGroup)group, (int)1024);
    }

    public NonStickyEventExecutorGroup(EventExecutorGroup group, int maxTaskExecutePerRun) {
        this.group = NonStickyEventExecutorGroup.verify((EventExecutorGroup)group);
        this.maxTaskExecutePerRun = ObjectUtil.checkPositive((int)maxTaskExecutePerRun, (String)"maxTaskExecutePerRun");
    }

    private static EventExecutorGroup verify(EventExecutorGroup group) {
        EventExecutor executor;
        Iterator<EventExecutor> executors = ObjectUtil.checkNotNull(group, (String)"group").iterator();
        do {
            if (!executors.hasNext()) return group;
        } while (!((executor = executors.next()) instanceof OrderedEventExecutor));
        throw new IllegalArgumentException((String)("EventExecutorGroup " + group + " contains OrderedEventExecutors: " + executor));
    }

    private NonStickyOrderedEventExecutor newExecutor(EventExecutor executor) {
        return new NonStickyOrderedEventExecutor((EventExecutor)executor, (int)this.maxTaskExecutePerRun);
    }

    @Override
    public boolean isShuttingDown() {
        return this.group.isShuttingDown();
    }

    @Override
    public Future<?> shutdownGracefully() {
        return this.group.shutdownGracefully();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return this.group.shutdownGracefully((long)quietPeriod, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public Future<?> terminationFuture() {
        return this.group.terminationFuture();
    }

    @Override
    public void shutdown() {
        this.group.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.group.shutdownNow();
    }

    @Override
    public EventExecutor next() {
        return this.newExecutor((EventExecutor)this.group.next());
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        Iterator<EventExecutor> itr = this.group.iterator();
        return new Iterator<EventExecutor>((NonStickyEventExecutorGroup)this, itr){
            final /* synthetic */ Iterator val$itr;
            final /* synthetic */ NonStickyEventExecutorGroup this$0;
            {
                this.this$0 = this$0;
                this.val$itr = iterator;
            }

            public boolean hasNext() {
                return this.val$itr.hasNext();
            }

            public EventExecutor next() {
                return NonStickyEventExecutorGroup.access$000((NonStickyEventExecutorGroup)this.this$0, (EventExecutor)((EventExecutor)this.val$itr.next()));
            }

            public void remove() {
                this.val$itr.remove();
            }
        };
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.group.submit((Runnable)task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.group.submit((Runnable)task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.group.submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.group.schedule((Runnable)command, (long)delay, (TimeUnit)unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.group.schedule(callable, (long)delay, (TimeUnit)unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.group.scheduleAtFixedRate((Runnable)command, (long)initialDelay, (long)period, (TimeUnit)unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.group.scheduleWithFixedDelay((Runnable)command, (long)initialDelay, (long)delay, (TimeUnit)unit);
    }

    @Override
    public boolean isShutdown() {
        return this.group.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.group.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.group.awaitTermination((long)timeout, (TimeUnit)unit);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.group.invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.group.invokeAll(tasks, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return (T)this.group.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (T)this.group.invokeAny(tasks, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public void execute(Runnable command) {
        this.group.execute((Runnable)command);
    }

    static /* synthetic */ NonStickyOrderedEventExecutor access$000(NonStickyEventExecutorGroup x0, EventExecutor x1) {
        return x0.newExecutor((EventExecutor)x1);
    }
}

