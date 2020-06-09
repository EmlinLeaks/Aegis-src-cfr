/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.concurrent.ThreadProperties;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class SingleThreadEventExecutor
extends AbstractScheduledEventExecutor
implements OrderedEventExecutor {
    static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max((int)16, (int)SystemPropertyUtil.getInt((String)"io.netty.eventexecutor.maxPendingTasks", (int)Integer.MAX_VALUE));
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;
    private static final Runnable WAKEUP_TASK = new Runnable(){

        public void run() {
        }
    };
    private static final Runnable NOOP_TASK = new Runnable(){

        public void run() {
        }
    };
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, (String)"state");
    private static final AtomicReferenceFieldUpdater<SingleThreadEventExecutor, ThreadProperties> PROPERTIES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(SingleThreadEventExecutor.class, ThreadProperties.class, (String)"threadProperties");
    private final Queue<Runnable> taskQueue;
    private volatile Thread thread;
    private volatile ThreadProperties threadProperties;
    private final Executor executor;
    private volatile boolean interrupted;
    private final CountDownLatch threadLock = new CountDownLatch((int)1);
    private final Set<Runnable> shutdownHooks = new LinkedHashSet<Runnable>();
    private final boolean addTaskWakesUp;
    private final int maxPendingTasks;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private long lastExecutionTime;
    private volatile int state = 1;
    private volatile long gracefulShutdownQuietPeriod;
    private volatile long gracefulShutdownTimeout;
    private long gracefulShutdownStartTime;
    private final Promise<?> terminationFuture = new DefaultPromise<?>((EventExecutor)GlobalEventExecutor.INSTANCE);
    private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos((long)1L);

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        this((EventExecutorGroup)parent, (Executor)new ThreadPerTaskExecutor((ThreadFactory)threadFactory), (boolean)addTaskWakesUp);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        this((EventExecutorGroup)parent, (Executor)new ThreadPerTaskExecutor((ThreadFactory)threadFactory), (boolean)addTaskWakesUp, (int)maxPendingTasks, (RejectedExecutionHandler)rejectedHandler);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp) {
        this((EventExecutorGroup)parent, (Executor)executor, (boolean)addTaskWakesUp, (int)DEFAULT_MAX_PENDING_EXECUTOR_TASKS, (RejectedExecutionHandler)RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        super((EventExecutorGroup)parent);
        this.addTaskWakesUp = addTaskWakesUp;
        this.maxPendingTasks = Math.max((int)16, (int)maxPendingTasks);
        this.executor = ThreadExecutorMap.apply((Executor)executor, (EventExecutor)this);
        this.taskQueue = this.newTaskQueue((int)this.maxPendingTasks);
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, (String)"rejectedHandler");
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, RejectedExecutionHandler rejectedHandler) {
        super((EventExecutorGroup)parent);
        this.addTaskWakesUp = addTaskWakesUp;
        this.maxPendingTasks = DEFAULT_MAX_PENDING_EXECUTOR_TASKS;
        this.executor = ThreadExecutorMap.apply((Executor)executor, (EventExecutor)this);
        this.taskQueue = ObjectUtil.checkNotNull(taskQueue, (String)"taskQueue");
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, (String)"rejectedHandler");
    }

    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        return true;
    }

    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        return true;
    }

    @Deprecated
    protected Queue<Runnable> newTaskQueue() {
        return this.newTaskQueue((int)this.maxPendingTasks);
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<Runnable>((int)maxPendingTasks);
    }

    protected void interruptThread() {
        Thread currentThread = this.thread;
        if (currentThread == null) {
            this.interrupted = true;
            return;
        }
        currentThread.interrupt();
    }

    protected Runnable pollTask() {
        if ($assertionsDisabled) return SingleThreadEventExecutor.pollTaskFrom(this.taskQueue);
        if (this.inEventLoop()) return SingleThreadEventExecutor.pollTaskFrom(this.taskQueue);
        throw new AssertionError();
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        Runnable task;
        while ((task = taskQueue.poll()) == WAKEUP_TASK) {
        }
        return task;
    }

    protected Runnable takeTask() {
        Runnable task;
        assert (this.inEventLoop());
        if (!(this.taskQueue instanceof BlockingQueue)) {
            throw new UnsupportedOperationException();
        }
        BlockingQueue taskQueue = (BlockingQueue)this.taskQueue;
        do {
            ScheduledFutureTask<?> scheduledTask;
            if ((scheduledTask = this.peekScheduledTask()) == null) {
                Runnable task2 = null;
                try {
                    task2 = (Runnable)taskQueue.take();
                    if (task2 != WAKEUP_TASK) return task2;
                    return null;
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return task2;
            }
            long delayNanos = scheduledTask.delayNanos();
            task = null;
            if (delayNanos > 0L) {
                try {
                    task = (Runnable)taskQueue.poll((long)delayNanos, (TimeUnit)TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e) {
                    return null;
                }
            }
            if (task != null) continue;
            this.fetchFromScheduledTaskQueue();
            task = (Runnable)taskQueue.poll();
        } while (task == null);
        return task;
    }

    private boolean fetchFromScheduledTaskQueue() {
        Runnable scheduledTask;
        if (this.scheduledTaskQueue == null) return true;
        if (this.scheduledTaskQueue.isEmpty()) {
            return true;
        }
        long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        do {
            if ((scheduledTask = this.pollScheduledTask((long)nanoTime)) != null) continue;
            return true;
        } while (this.taskQueue.offer((Runnable)scheduledTask));
        this.scheduledTaskQueue.add((ScheduledFutureTask)scheduledTask);
        return false;
    }

    private boolean executeExpiredScheduledTasks() {
        if (this.scheduledTaskQueue == null) return false;
        if (this.scheduledTaskQueue.isEmpty()) {
            return false;
        }
        long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        Runnable scheduledTask = this.pollScheduledTask((long)nanoTime);
        if (scheduledTask == null) {
            return false;
        }
        do {
            SingleThreadEventExecutor.safeExecute((Runnable)scheduledTask);
        } while ((scheduledTask = this.pollScheduledTask((long)nanoTime)) != null);
        return true;
    }

    protected Runnable peekTask() {
        if ($assertionsDisabled) return this.taskQueue.peek();
        if (this.inEventLoop()) return this.taskQueue.peek();
        throw new AssertionError();
    }

    protected boolean hasTasks() {
        assert (this.inEventLoop());
        if (this.taskQueue.isEmpty()) return false;
        return true;
    }

    public int pendingTasks() {
        return this.taskQueue.size();
    }

    protected void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        if (this.offerTask((Runnable)task)) return;
        this.reject((Runnable)task);
    }

    final boolean offerTask(Runnable task) {
        if (!this.isShutdown()) return this.taskQueue.offer((Runnable)task);
        SingleThreadEventExecutor.reject();
        return this.taskQueue.offer((Runnable)task);
    }

    protected boolean removeTask(Runnable task) {
        if (task != null) return this.taskQueue.remove((Object)task);
        throw new NullPointerException((String)"task");
    }

    protected boolean runAllTasks() {
        boolean fetchedAll;
        assert (this.inEventLoop());
        boolean ranAtLeastOne = false;
        do {
            fetchedAll = this.fetchFromScheduledTaskQueue();
            if (!this.runAllTasksFrom(this.taskQueue)) continue;
            ranAtLeastOne = true;
        } while (!fetchedAll);
        if (ranAtLeastOne) {
            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        this.afterRunningAllTasks();
        return ranAtLeastOne;
    }

    protected final boolean runScheduledAndExecutorTasks(int maxDrainAttempts) {
        boolean ranAtLeastOneTask;
        assert (this.inEventLoop());
        int drainAttempt = 0;
        while ((ranAtLeastOneTask = this.runExistingTasksFrom(this.taskQueue) | this.executeExpiredScheduledTasks()) && ++drainAttempt < maxDrainAttempts) {
        }
        if (drainAttempt > 0) {
            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        this.afterRunningAllTasks();
        if (drainAttempt <= 0) return false;
        return true;
    }

    protected final boolean runAllTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = SingleThreadEventExecutor.pollTaskFrom(taskQueue);
        if (task == null) {
            return false;
        }
        do {
            SingleThreadEventExecutor.safeExecute((Runnable)task);
        } while ((task = SingleThreadEventExecutor.pollTaskFrom(taskQueue)) != null);
        return true;
    }

    private boolean runExistingTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = SingleThreadEventExecutor.pollTaskFrom(taskQueue);
        if (task == null) {
            return false;
        }
        int remaining = Math.min((int)this.maxPendingTasks, (int)taskQueue.size());
        SingleThreadEventExecutor.safeExecute((Runnable)task);
        while (remaining-- > 0) {
            task = taskQueue.poll();
            if (task == null) return true;
            SingleThreadEventExecutor.safeExecute((Runnable)task);
        }
        return true;
    }

    protected boolean runAllTasks(long timeoutNanos) {
        long lastExecutionTime;
        block2 : {
            this.fetchFromScheduledTaskQueue();
            Runnable task = this.pollTask();
            if (task == null) {
                this.afterRunningAllTasks();
                return false;
            }
            long deadline = ScheduledFutureTask.nanoTime() + timeoutNanos;
            long runTasks = 0L;
            do {
                SingleThreadEventExecutor.safeExecute((Runnable)task);
                if ((++runTasks & 63L) == 0L && (lastExecutionTime = ScheduledFutureTask.nanoTime()) >= deadline) break block2;
            } while ((task = this.pollTask()) != null);
            lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        this.afterRunningAllTasks();
        this.lastExecutionTime = lastExecutionTime;
        return true;
    }

    protected void afterRunningAllTasks() {
    }

    protected long delayNanos(long currentTimeNanos) {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask != null) return scheduledTask.delayNanos((long)currentTimeNanos);
        return SCHEDULE_PURGE_INTERVAL;
    }

    protected long deadlineNanos() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask != null) return scheduledTask.deadlineNanos();
        return SingleThreadEventExecutor.nanoTime() + SCHEDULE_PURGE_INTERVAL;
    }

    protected void updateLastExecutionTime() {
        this.lastExecutionTime = ScheduledFutureTask.nanoTime();
    }

    protected abstract void run();

    protected void cleanup() {
    }

    protected void wakeup(boolean inEventLoop) {
        if (inEventLoop) return;
        this.taskQueue.offer((Runnable)WAKEUP_TASK);
    }

    @Override
    final void executeScheduledRunnable(Runnable runnable, boolean isAddition, long deadlineNanos) {
        if (isAddition && this.beforeScheduledTaskSubmitted((long)deadlineNanos)) {
            super.executeScheduledRunnable((Runnable)runnable, (boolean)isAddition, (long)deadlineNanos);
            return;
        }
        super.executeScheduledRunnable((Runnable)new NonWakeupRunnable((SingleThreadEventExecutor)this, (Runnable)runnable){
            final /* synthetic */ Runnable val$runnable;
            final /* synthetic */ SingleThreadEventExecutor this$0;
            {
                this.this$0 = this$0;
                this.val$runnable = runnable;
            }

            public void run() {
                this.val$runnable.run();
            }
        }, (boolean)isAddition, (long)deadlineNanos);
        if (!isAddition) return;
        if (!this.afterScheduledTaskSubmitted((long)deadlineNanos)) return;
        this.wakeup((boolean)false);
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        if (thread != this.thread) return false;
        return true;
    }

    public void addShutdownHook(Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.add((Runnable)task);
            return;
        }
        this.execute((Runnable)new Runnable((SingleThreadEventExecutor)this, (Runnable)task){
            final /* synthetic */ Runnable val$task;
            final /* synthetic */ SingleThreadEventExecutor this$0;
            {
                this.this$0 = this$0;
                this.val$task = runnable;
            }

            public void run() {
                SingleThreadEventExecutor.access$000((SingleThreadEventExecutor)this.this$0).add(this.val$task);
            }
        });
    }

    public void removeShutdownHook(Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.remove((Object)task);
            return;
        }
        this.execute((Runnable)new Runnable((SingleThreadEventExecutor)this, (Runnable)task){
            final /* synthetic */ Runnable val$task;
            final /* synthetic */ SingleThreadEventExecutor this$0;
            {
                this.this$0 = this$0;
                this.val$task = runnable;
            }

            public void run() {
                SingleThreadEventExecutor.access$000((SingleThreadEventExecutor)this.this$0).remove((Object)this.val$task);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean runShutdownHooks() {
        boolean ran = false;
        block5 : do {
            if (this.shutdownHooks.isEmpty()) {
                if (!ran) return ran;
                this.lastExecutionTime = ScheduledFutureTask.nanoTime();
                return ran;
            }
            ArrayList<Runnable> copy = new ArrayList<Runnable>(this.shutdownHooks);
            this.shutdownHooks.clear();
            Iterator<E> iterator = copy.iterator();
            do {
                if (!iterator.hasNext()) continue block5;
                Runnable task = (Runnable)iterator.next();
                try {
                    task.run();
                    continue;
                }
                catch (Throwable t) {
                    logger.warn((String)"Shutdown hook raised an exception.", (Throwable)t);
                    continue;
                }
                finally {
                    ran = true;
                    continue;
                }
                break;
            } while (true);
            break;
        } while (true);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        if (quietPeriod < 0L) {
            throw new IllegalArgumentException((String)("quietPeriod: " + quietPeriod + " (expected >= 0)"));
        }
        if (timeout < quietPeriod) {
            throw new IllegalArgumentException((String)("timeout: " + timeout + " (expected >= quietPeriod (" + quietPeriod + "))"));
        }
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        if (this.isShuttingDown()) {
            return this.terminationFuture();
        }
        inEventLoop = this.inEventLoop();
        do {
            if (this.isShuttingDown()) {
                return this.terminationFuture();
            }
            wakeup = true;
            oldState = this.state;
            if (inEventLoop) {
                newState = 3;
                continue;
            }
            switch (oldState) {
                case 1: 
                case 2: {
                    newState = 3;
                    ** break;
                }
            }
            newState = oldState;
            wakeup = false;
lbl24: // 3 sources:
        } while (!SingleThreadEventExecutor.STATE_UPDATER.compareAndSet((SingleThreadEventExecutor)this, (int)oldState, (int)newState));
        this.gracefulShutdownQuietPeriod = unit.toNanos((long)quietPeriod);
        this.gracefulShutdownTimeout = unit.toNanos((long)timeout);
        if (this.ensureThreadStarted((int)oldState)) {
            return this.terminationFuture;
        }
        if (wakeup == false) return this.terminationFuture();
        this.taskQueue.offer((Runnable)SingleThreadEventExecutor.WAKEUP_TASK);
        if (this.addTaskWakesUp != false) return this.terminationFuture();
        this.wakeup((boolean)inEventLoop);
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    /*
     * Unable to fully structure code
     */
    @Deprecated
    @Override
    public void shutdown() {
        if (this.isShutdown()) {
            return;
        }
        inEventLoop = this.inEventLoop();
        do {
            if (this.isShuttingDown()) {
                return;
            }
            wakeup = true;
            oldState = this.state;
            if (inEventLoop) {
                newState = 4;
                continue;
            }
            switch (oldState) {
                case 1: 
                case 2: 
                case 3: {
                    newState = 4;
                    ** break;
                }
            }
            newState = oldState;
            wakeup = false;
lbl18: // 3 sources:
        } while (!SingleThreadEventExecutor.STATE_UPDATER.compareAndSet((SingleThreadEventExecutor)this, (int)oldState, (int)newState));
        if (this.ensureThreadStarted((int)oldState)) {
            return;
        }
        if (wakeup == false) return;
        this.taskQueue.offer((Runnable)SingleThreadEventExecutor.WAKEUP_TASK);
        if (this.addTaskWakesUp != false) return;
        this.wakeup((boolean)inEventLoop);
    }

    @Override
    public boolean isShuttingDown() {
        if (this.state < 3) return false;
        return true;
    }

    @Override
    public boolean isShutdown() {
        if (this.state < 4) return false;
        return true;
    }

    @Override
    public boolean isTerminated() {
        if (this.state != 5) return false;
        return true;
    }

    protected boolean confirmShutdown() {
        if (!this.isShuttingDown()) {
            return false;
        }
        if (!this.inEventLoop()) {
            throw new IllegalStateException((String)"must be invoked from an event loop");
        }
        this.cancelScheduledTasks();
        if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = ScheduledFutureTask.nanoTime();
        }
        if (this.runAllTasks() || this.runShutdownHooks()) {
            if (this.isShutdown()) {
                return true;
            }
            if (this.gracefulShutdownQuietPeriod == 0L) {
                return true;
            }
            this.taskQueue.offer((Runnable)WAKEUP_TASK);
            return false;
        }
        long nanoTime = ScheduledFutureTask.nanoTime();
        if (this.isShutdown()) return true;
        if (nanoTime - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout) {
            return true;
        }
        if (nanoTime - this.lastExecutionTime > this.gracefulShutdownQuietPeriod) return true;
        this.taskQueue.offer((Runnable)WAKEUP_TASK);
        try {
            Thread.sleep((long)100L);
            return false;
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        if (this.inEventLoop()) {
            throw new IllegalStateException((String)"cannot await termination of the current thread");
        }
        this.threadLock.await((long)timeout, (TimeUnit)unit);
        return this.isTerminated();
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        boolean inEventLoop = this.inEventLoop();
        this.addTask((Runnable)task);
        if (!inEventLoop) {
            this.startThread();
            if (this.isShutdown()) {
                boolean reject = false;
                try {
                    if (this.removeTask((Runnable)task)) {
                        reject = true;
                    }
                }
                catch (UnsupportedOperationException unsupportedOperationException) {
                    // empty catch block
                }
                if (reject) {
                    SingleThreadEventExecutor.reject();
                }
            }
        }
        if (this.addTaskWakesUp) return;
        if (!this.wakesUpForTask((Runnable)task)) return;
        this.wakeup((boolean)inEventLoop);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        this.throwIfInEventLoop((String)"invokeAny");
        return (T)super.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.throwIfInEventLoop((String)"invokeAny");
        return (T)super.invokeAny(tasks, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        this.throwIfInEventLoop((String)"invokeAll");
        return super.invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        this.throwIfInEventLoop((String)"invokeAll");
        return super.invokeAll(tasks, (long)timeout, (TimeUnit)unit);
    }

    private void throwIfInEventLoop(String method) {
        if (!this.inEventLoop()) return;
        throw new RejectedExecutionException((String)("Calling " + method + " from within the EventLoop is not allowed"));
    }

    public final ThreadProperties threadProperties() {
        ThreadProperties threadProperties = this.threadProperties;
        if (threadProperties != null) return threadProperties;
        Thread thread = this.thread;
        if (thread == null) {
            assert (!this.inEventLoop());
            this.submit((Runnable)NOOP_TASK).syncUninterruptibly();
            thread = this.thread;
            assert (thread != null);
        }
        if (PROPERTIES_UPDATER.compareAndSet((SingleThreadEventExecutor)this, null, (ThreadProperties)(threadProperties = new DefaultThreadProperties((Thread)thread)))) return threadProperties;
        return this.threadProperties;
    }

    protected boolean wakesUpForTask(Runnable task) {
        if (task instanceof NonWakeupRunnable) return false;
        return true;
    }

    protected static void reject() {
        throw new RejectedExecutionException((String)"event executor terminated");
    }

    protected final void reject(Runnable task) {
        this.rejectedExecutionHandler.rejected((Runnable)task, (SingleThreadEventExecutor)this);
    }

    private void startThread() {
        if (this.state != 1) return;
        if (!STATE_UPDATER.compareAndSet((SingleThreadEventExecutor)this, (int)1, (int)2)) return;
        boolean success = false;
        try {
            this.doStartThread();
            success = true;
            return;
        }
        finally {
            if (!success) {
                STATE_UPDATER.compareAndSet((SingleThreadEventExecutor)this, (int)2, (int)1);
            }
        }
    }

    private boolean ensureThreadStarted(int oldState) {
        if (oldState != 1) return false;
        try {
            this.doStartThread();
            return false;
        }
        catch (Throwable cause) {
            STATE_UPDATER.set((SingleThreadEventExecutor)this, (int)5);
            this.terminationFuture.tryFailure((Throwable)cause);
            if (cause instanceof Exception) return true;
            PlatformDependent.throwException((Throwable)cause);
            return true;
        }
    }

    private void doStartThread() {
        assert (this.thread == null);
        this.executor.execute((Runnable)new Runnable((SingleThreadEventExecutor)this){
            final /* synthetic */ SingleThreadEventExecutor this$0;
            {
                this.this$0 = this$0;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                SingleThreadEventExecutor.access$102((SingleThreadEventExecutor)this.this$0, (Thread)Thread.currentThread());
                if (SingleThreadEventExecutor.access$200((SingleThreadEventExecutor)this.this$0)) {
                    SingleThreadEventExecutor.access$100((SingleThreadEventExecutor)this.this$0).interrupt();
                }
                boolean success = false;
                this.this$0.updateLastExecutionTime();
                try {
                    this.this$0.run();
                    success = true;
                }
                catch (Throwable t) {
                    SingleThreadEventExecutor.access$300().warn((String)"Unexpected exception from an event executor: ", (Throwable)t);
                }
                finally {
                    int oldState;
                    while ((oldState = SingleThreadEventExecutor.access$400((SingleThreadEventExecutor)this.this$0)) < 3 && !SingleThreadEventExecutor.access$500().compareAndSet(this.this$0, (int)oldState, (int)3)) {
                    }
                    if (success && SingleThreadEventExecutor.access$600((SingleThreadEventExecutor)this.this$0) == 0L && SingleThreadEventExecutor.access$300().isErrorEnabled()) {
                        SingleThreadEventExecutor.access$300().error((String)("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates."));
                    }
                    while (!this.this$0.confirmShutdown()) {
                    }
                }
                try {
                    this.this$0.cleanup();
                    return;
                }
                finally {
                    io.netty.util.concurrent.FastThreadLocal.removeAll();
                    SingleThreadEventExecutor.access$500().set(this.this$0, (int)5);
                    SingleThreadEventExecutor.access$700((SingleThreadEventExecutor)this.this$0).countDown();
                    if (SingleThreadEventExecutor.access$300().isWarnEnabled() && !SingleThreadEventExecutor.access$800((SingleThreadEventExecutor)this.this$0).isEmpty()) {
                        SingleThreadEventExecutor.access$300().warn((String)("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.access$800((SingleThreadEventExecutor)this.this$0).size() + ')'));
                    }
                    SingleThreadEventExecutor.access$900((SingleThreadEventExecutor)this.this$0).setSuccess(null);
                }
            }
        });
    }

    static /* synthetic */ Set access$000(SingleThreadEventExecutor x0) {
        return x0.shutdownHooks;
    }

    static /* synthetic */ Thread access$102(SingleThreadEventExecutor x0, Thread x1) {
        x0.thread = x1;
        return x0.thread;
    }

    static /* synthetic */ boolean access$200(SingleThreadEventExecutor x0) {
        return x0.interrupted;
    }

    static /* synthetic */ Thread access$100(SingleThreadEventExecutor x0) {
        return x0.thread;
    }

    static /* synthetic */ InternalLogger access$300() {
        return logger;
    }

    static /* synthetic */ int access$400(SingleThreadEventExecutor x0) {
        return x0.state;
    }

    static /* synthetic */ AtomicIntegerFieldUpdater access$500() {
        return STATE_UPDATER;
    }

    static /* synthetic */ long access$600(SingleThreadEventExecutor x0) {
        return x0.gracefulShutdownStartTime;
    }

    static /* synthetic */ CountDownLatch access$700(SingleThreadEventExecutor x0) {
        return x0.threadLock;
    }

    static /* synthetic */ Queue access$800(SingleThreadEventExecutor x0) {
        return x0.taskQueue;
    }

    static /* synthetic */ Promise access$900(SingleThreadEventExecutor x0) {
        return x0.terminationFuture;
    }
}

