/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledEventExecutor
extends AbstractEventExecutor {
    private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>(){

        public int compare(ScheduledFutureTask<?> o1, ScheduledFutureTask<?> o2) {
            return o1.compareTo(o2);
        }
    };
    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
    long nextTaskId;

    protected AbstractScheduledEventExecutor() {
    }

    protected AbstractScheduledEventExecutor(EventExecutorGroup parent) {
        super((EventExecutorGroup)parent);
    }

    protected static long nanoTime() {
        return ScheduledFutureTask.nanoTime();
    }

    protected static long deadlineToDelayNanos(long deadlineNanos) {
        return ScheduledFutureTask.deadlineToDelayNanos((long)deadlineNanos);
    }

    protected static long initialNanoTime() {
        return ScheduledFutureTask.initialNanoTime();
    }

    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
        if (this.scheduledTaskQueue != null) return this.scheduledTaskQueue;
        this.scheduledTaskQueue = new DefaultPriorityQueue<ScheduledFutureTask<?>>(SCHEDULED_FUTURE_TASK_COMPARATOR, (int)11);
        return this.scheduledTaskQueue;
    }

    private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> queue) {
        if (queue == null) return true;
        if (queue.isEmpty()) return true;
        return false;
    }

    protected void cancelScheduledTasks() {
        ScheduledFutureTask[] scheduledTasks;
        assert (this.inEventLoop());
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (AbstractScheduledEventExecutor.isNullOrEmpty(scheduledTaskQueue)) {
            return;
        }
        ScheduledFutureTask[] arrscheduledFutureTask = scheduledTasks = scheduledTaskQueue.toArray(new ScheduledFutureTask[0]);
        int n = arrscheduledFutureTask.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                scheduledTaskQueue.clearIgnoringIndexes();
                return;
            }
            ScheduledFutureTask task = arrscheduledFutureTask[n2];
            task.cancelWithoutRemove((boolean)false);
            ++n2;
        } while (true);
    }

    protected final Runnable pollScheduledTask() {
        return this.pollScheduledTask((long)AbstractScheduledEventExecutor.nanoTime());
    }

    protected final Runnable pollScheduledTask(long nanoTime) {
        assert (this.inEventLoop());
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        ScheduledFutureTask scheduledTask = scheduledTaskQueue == null ? null : (ScheduledFutureTask)scheduledTaskQueue.peek();
        if (scheduledTask == null) return null;
        if (scheduledTask.deadlineNanos() - nanoTime > 0L) {
            return null;
        }
        scheduledTaskQueue.remove();
        return scheduledTask;
    }

    protected final long nextScheduledTaskNano() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null) return -1L;
        long l = Math.max((long)0L, (long)(scheduledTask.deadlineNanos() - AbstractScheduledEventExecutor.nanoTime()));
        return l;
    }

    protected final long nextScheduledTaskDeadlineNanos() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null) return -1L;
        long l = scheduledTask.deadlineNanos();
        return l;
    }

    final ScheduledFutureTask<?> peekScheduledTask() {
        PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (scheduledTaskQueue == null) return null;
        ScheduledFutureTask scheduledFutureTask = (ScheduledFutureTask)scheduledTaskQueue.peek();
        return scheduledFutureTask;
    }

    protected final boolean hasScheduledTasks() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null) return false;
        if (scheduledTask.deadlineNanos() > AbstractScheduledEventExecutor.nanoTime()) return false;
        return true;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, (String)"command");
        ObjectUtil.checkNotNull(unit, (String)"unit");
        if (delay < 0L) {
            delay = 0L;
        }
        this.validateScheduled0((long)delay, (TimeUnit)unit);
        return this.schedule(new ScheduledFutureTask<V>((AbstractScheduledEventExecutor)this, (Runnable)command, (long)ScheduledFutureTask.deadlineNanos((long)unit.toNanos((long)delay))));
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(callable, (String)"callable");
        ObjectUtil.checkNotNull(unit, (String)"unit");
        if (delay < 0L) {
            delay = 0L;
        }
        this.validateScheduled0((long)delay, (TimeUnit)unit);
        return this.schedule(new ScheduledFutureTask<V>((AbstractScheduledEventExecutor)this, callable, (long)ScheduledFutureTask.deadlineNanos((long)unit.toNanos((long)delay))));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, (String)"command");
        ObjectUtil.checkNotNull(unit, (String)"unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException((String)String.format((String)"initialDelay: %d (expected: >= 0)", (Object[])new Object[]{Long.valueOf((long)initialDelay)}));
        }
        if (period <= 0L) {
            throw new IllegalArgumentException((String)String.format((String)"period: %d (expected: > 0)", (Object[])new Object[]{Long.valueOf((long)period)}));
        }
        this.validateScheduled0((long)initialDelay, (TimeUnit)unit);
        this.validateScheduled0((long)period, (TimeUnit)unit);
        return this.schedule(new ScheduledFutureTask<V>((AbstractScheduledEventExecutor)this, (Runnable)command, (long)ScheduledFutureTask.deadlineNanos((long)unit.toNanos((long)initialDelay)), (long)unit.toNanos((long)period)));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, (String)"command");
        ObjectUtil.checkNotNull(unit, (String)"unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException((String)String.format((String)"initialDelay: %d (expected: >= 0)", (Object[])new Object[]{Long.valueOf((long)initialDelay)}));
        }
        if (delay <= 0L) {
            throw new IllegalArgumentException((String)String.format((String)"delay: %d (expected: > 0)", (Object[])new Object[]{Long.valueOf((long)delay)}));
        }
        this.validateScheduled0((long)initialDelay, (TimeUnit)unit);
        this.validateScheduled0((long)delay, (TimeUnit)unit);
        return this.schedule(new ScheduledFutureTask<V>((AbstractScheduledEventExecutor)this, (Runnable)command, (long)ScheduledFutureTask.deadlineNanos((long)unit.toNanos((long)initialDelay)), (long)(-unit.toNanos((long)delay))));
    }

    private void validateScheduled0(long amount, TimeUnit unit) {
        this.validateScheduled((long)amount, (TimeUnit)unit);
    }

    @Deprecated
    protected void validateScheduled(long amount, TimeUnit unit) {
    }

    private <V> ScheduledFuture<V> schedule(ScheduledFutureTask<V> task) {
        if (this.inEventLoop()) {
            this.scheduledTaskQueue().add(task.setId((long)this.nextTaskId++));
            return task;
        }
        this.executeScheduledRunnable((Runnable)new Runnable((AbstractScheduledEventExecutor)this, task){
            final /* synthetic */ ScheduledFutureTask val$task;
            final /* synthetic */ AbstractScheduledEventExecutor this$0;
            {
                this.this$0 = this$0;
                this.val$task = scheduledFutureTask;
            }

            public void run() {
                this.this$0.scheduledTaskQueue().add(this.val$task.setId((long)this.this$0.nextTaskId++));
            }
        }, (boolean)true, (long)task.deadlineNanos());
        return task;
    }

    final void removeScheduled(ScheduledFutureTask<?> task) {
        if (this.inEventLoop()) {
            this.scheduledTaskQueue().removeTyped(task);
            return;
        }
        this.executeScheduledRunnable((Runnable)new Runnable((AbstractScheduledEventExecutor)this, task){
            final /* synthetic */ ScheduledFutureTask val$task;
            final /* synthetic */ AbstractScheduledEventExecutor this$0;
            {
                this.this$0 = this$0;
                this.val$task = scheduledFutureTask;
            }

            public void run() {
                this.this$0.scheduledTaskQueue().removeTyped(this.val$task);
            }
        }, (boolean)false, (long)task.deadlineNanos());
    }

    void executeScheduledRunnable(Runnable runnable, boolean isAddition, long deadlineNanos) {
        this.execute((Runnable)runnable);
    }
}

