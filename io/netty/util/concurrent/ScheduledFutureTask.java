/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseTask;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

final class ScheduledFutureTask<V>
extends PromiseTask<V>
implements ScheduledFuture<V>,
PriorityQueueNode {
    private static final long START_TIME = System.nanoTime();
    private long id;
    private long deadlineNanos;
    private final long periodNanos;
    private int queueIndex = -1;

    static long nanoTime() {
        return System.nanoTime() - START_TIME;
    }

    static long deadlineNanos(long delay) {
        long deadlineNanos = ScheduledFutureTask.nanoTime() + delay;
        if (deadlineNanos < 0L) {
            return Long.MAX_VALUE;
        }
        long l = deadlineNanos;
        return l;
    }

    static long initialNanoTime() {
        return START_TIME;
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Runnable runnable, long nanoTime) {
        super((EventExecutor)executor, (Runnable)runnable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0L;
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Runnable runnable, long nanoTime, long period) {
        super((EventExecutor)executor, (Runnable)runnable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = ScheduledFutureTask.validatePeriod((long)period);
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Callable<V> callable, long nanoTime, long period) {
        super((EventExecutor)executor, callable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = ScheduledFutureTask.validatePeriod((long)period);
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Callable<V> callable, long nanoTime) {
        super((EventExecutor)executor, callable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0L;
    }

    private static long validatePeriod(long period) {
        if (period != 0L) return period;
        throw new IllegalArgumentException((String)"period: 0 (expected: != 0)");
    }

    ScheduledFutureTask<V> setId(long id) {
        this.id = id;
        return this;
    }

    @Override
    protected EventExecutor executor() {
        return super.executor();
    }

    public long deadlineNanos() {
        return this.deadlineNanos;
    }

    public long delayNanos() {
        return ScheduledFutureTask.deadlineToDelayNanos((long)this.deadlineNanos());
    }

    static long deadlineToDelayNanos(long deadlineNanos) {
        return Math.max((long)0L, (long)(deadlineNanos - ScheduledFutureTask.nanoTime()));
    }

    public long delayNanos(long currentTimeNanos) {
        return Math.max((long)0L, (long)(this.deadlineNanos() - (currentTimeNanos - START_TIME)));
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert((long)this.delayNanos(), (TimeUnit)TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this == o) {
            return 0;
        }
        ScheduledFutureTask that = (ScheduledFutureTask)o;
        long d = this.deadlineNanos() - that.deadlineNanos();
        if (d < 0L) {
            return -1;
        }
        if (d > 0L) {
            return 1;
        }
        if (this.id < that.id) {
            return -1;
        }
        if ($assertionsDisabled) return 1;
        if (this.id != that.id) return 1;
        throw new AssertionError();
    }

    @Override
    public void run() {
        assert (this.executor().inEventLoop());
        try {
            if (this.periodNanos == 0L) {
                if (!this.setUncancellableInternal()) return;
                V result = this.runTask();
                this.setSuccessInternal(result);
                return;
            }
            if (this.isCancelled()) return;
            this.runTask();
            if (this.executor().isShutdown()) return;
            this.deadlineNanos = this.periodNanos > 0L ? (this.deadlineNanos += this.periodNanos) : ScheduledFutureTask.nanoTime() - this.periodNanos;
            if (this.isCancelled()) return;
            PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = ((AbstractScheduledEventExecutor)this.executor()).scheduledTaskQueue;
            assert (scheduledTaskQueue != null);
            scheduledTaskQueue.add(this);
            return;
        }
        catch (Throwable cause) {
            this.setFailureInternal((Throwable)cause);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean canceled = super.cancel((boolean)mayInterruptIfRunning);
        if (!canceled) return canceled;
        ((AbstractScheduledEventExecutor)this.executor()).removeScheduled(this);
        return canceled;
    }

    boolean cancelWithoutRemove(boolean mayInterruptIfRunning) {
        return super.cancel((boolean)mayInterruptIfRunning);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.setCharAt((int)(buf.length() - 1), (char)',');
        return buf.append((String)" deadline: ").append((long)this.deadlineNanos).append((String)", period: ").append((long)this.periodNanos).append((char)')');
    }

    @Override
    public int priorityQueueIndex(DefaultPriorityQueue<?> queue) {
        return this.queueIndex;
    }

    @Override
    public void priorityQueueIndex(DefaultPriorityQueue<?> queue, int i) {
        this.queueIndex = i;
    }
}

