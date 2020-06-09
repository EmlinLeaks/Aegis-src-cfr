/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GlobalEventExecutor
extends AbstractScheduledEventExecutor
implements OrderedEventExecutor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalEventExecutor.class);
    private static final long SCHEDULE_QUIET_PERIOD_INTERVAL = TimeUnit.SECONDS.toNanos((long)1L);
    public static final GlobalEventExecutor INSTANCE = new GlobalEventExecutor();
    final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
    final ScheduledFutureTask<Void> quietPeriodTask = new ScheduledFutureTask<java.lang.Object>((AbstractScheduledEventExecutor)this, Executors.callable((Runnable)new Runnable((GlobalEventExecutor)this){
        final /* synthetic */ GlobalEventExecutor this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
        }
    }, null), (long)ScheduledFutureTask.deadlineNanos((long)SCHEDULE_QUIET_PERIOD_INTERVAL), (long)(-SCHEDULE_QUIET_PERIOD_INTERVAL));
    final ThreadFactory threadFactory;
    private final TaskRunner taskRunner = new TaskRunner((GlobalEventExecutor)this);
    private final AtomicBoolean started = new AtomicBoolean();
    volatile Thread thread;
    private final Future<?> terminationFuture = new FailedFuture<?>((EventExecutor)this, (Throwable)new UnsupportedOperationException());

    private GlobalEventExecutor() {
        this.scheduledTaskQueue().add(this.quietPeriodTask);
        this.threadFactory = ThreadExecutorMap.apply((ThreadFactory)new DefaultThreadFactory((String)DefaultThreadFactory.toPoolName(this.getClass()), (boolean)false, (int)5, null), (EventExecutor)this);
    }

    Runnable takeTask() {
        Runnable task;
        BlockingQueue<Runnable> taskQueue = this.taskQueue;
        do {
            ScheduledFutureTask<?> scheduledTask;
            if ((scheduledTask = this.peekScheduledTask()) == null) {
                Runnable task2 = null;
                try {
                    return taskQueue.take();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return task2;
            }
            long delayNanos = scheduledTask.delayNanos();
            if (delayNanos > 0L) {
                try {
                    task = taskQueue.poll((long)delayNanos, (TimeUnit)TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e) {
                    return null;
                }
            } else {
                task = (Runnable)taskQueue.poll();
            }
            if (task != null) continue;
            this.fetchFromScheduledTaskQueue();
            task = (Runnable)taskQueue.poll();
        } while (task == null);
        return task;
    }

    private void fetchFromScheduledTaskQueue() {
        long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        Runnable scheduledTask = this.pollScheduledTask((long)nanoTime);
        while (scheduledTask != null) {
            this.taskQueue.add((Runnable)scheduledTask);
            scheduledTask = this.pollScheduledTask((long)nanoTime);
        }
    }

    public int pendingTasks() {
        return this.taskQueue.size();
    }

    private void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        this.taskQueue.add((Runnable)task);
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        if (thread != this.thread) return false;
        return true;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Deprecated
    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    public boolean awaitInactivity(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        Thread thread = this.thread;
        if (thread == null) {
            throw new IllegalStateException((String)"thread was not started");
        }
        thread.join((long)unit.toMillis((long)timeout));
        if (thread.isAlive()) return false;
        return true;
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        this.addTask((Runnable)task);
        if (this.inEventLoop()) return;
        this.startThread();
    }

    private void startThread() {
        if (!this.started.compareAndSet((boolean)false, (boolean)true)) return;
        Thread t = this.threadFactory.newThread((Runnable)this.taskRunner);
        AccessController.doPrivileged(new PrivilegedAction<Void>((GlobalEventExecutor)this, (Thread)t){
            final /* synthetic */ Thread val$t;
            final /* synthetic */ GlobalEventExecutor this$0;
            {
                this.this$0 = this$0;
                this.val$t = thread;
            }

            public Void run() {
                this.val$t.setContextClassLoader(null);
                return null;
            }
        });
        this.thread = t;
        t.start();
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }

    static /* synthetic */ AtomicBoolean access$100(GlobalEventExecutor x0) {
        return x0.started;
    }
}

