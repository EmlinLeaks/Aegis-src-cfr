/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.HashedWheelTimer;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

public class HashedWheelTimer
implements Timer {
    static final InternalLogger logger = InternalLoggerFactory.getInstance(HashedWheelTimer.class);
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();
    private static final AtomicBoolean WARNED_TOO_MANY_INSTANCES = new AtomicBoolean();
    private static final int INSTANCE_COUNT_LIMIT = 64;
    private static final long MILLISECOND_NANOS = TimeUnit.MILLISECONDS.toNanos((long)1L);
    private static final ResourceLeakDetector<HashedWheelTimer> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(HashedWheelTimer.class, (int)1);
    private static final AtomicIntegerFieldUpdater<HashedWheelTimer> WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimer.class, (String)"workerState");
    private final ResourceLeakTracker<HashedWheelTimer> leak;
    private final Worker worker = new Worker((HashedWheelTimer)this, null);
    private final Thread workerThread;
    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    private volatile int workerState;
    private final long tickDuration;
    private final HashedWheelBucket[] wheel;
    private final int mask;
    private final CountDownLatch startTimeInitialized = new CountDownLatch((int)1);
    private final Queue<HashedWheelTimeout> timeouts = PlatformDependent.newMpscQueue();
    private final Queue<HashedWheelTimeout> cancelledTimeouts = PlatformDependent.newMpscQueue();
    private final AtomicLong pendingTimeouts = new AtomicLong((long)0L);
    private final long maxPendingTimeouts;
    private volatile long startTime;

    public HashedWheelTimer() {
        this((ThreadFactory)Executors.defaultThreadFactory());
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit) {
        this((ThreadFactory)Executors.defaultThreadFactory(), (long)tickDuration, (TimeUnit)unit);
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this((ThreadFactory)Executors.defaultThreadFactory(), (long)tickDuration, (TimeUnit)unit, (int)ticksPerWheel);
    }

    public HashedWheelTimer(ThreadFactory threadFactory) {
        this((ThreadFactory)threadFactory, (long)100L, (TimeUnit)TimeUnit.MILLISECONDS);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
        this((ThreadFactory)threadFactory, (long)tickDuration, (TimeUnit)unit, (int)512);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this((ThreadFactory)threadFactory, (long)tickDuration, (TimeUnit)unit, (int)ticksPerWheel, (boolean)true);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection) {
        this((ThreadFactory)threadFactory, (long)tickDuration, (TimeUnit)unit, (int)ticksPerWheel, (boolean)leakDetection, (long)-1L);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection, long maxPendingTimeouts) {
        if (threadFactory == null) {
            throw new NullPointerException((String)"threadFactory");
        }
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        if (tickDuration <= 0L) {
            throw new IllegalArgumentException((String)("tickDuration must be greater than 0: " + tickDuration));
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException((String)("ticksPerWheel must be greater than 0: " + ticksPerWheel));
        }
        this.wheel = HashedWheelTimer.createWheel((int)ticksPerWheel);
        this.mask = this.wheel.length - 1;
        long duration = unit.toNanos((long)tickDuration);
        if (duration >= Long.MAX_VALUE / (long)this.wheel.length) {
            throw new IllegalArgumentException((String)String.format((String)"tickDuration: %d (expected: 0 < tickDuration in nanos < %d", (Object[])new Object[]{Long.valueOf((long)tickDuration), Long.valueOf((long)(Long.MAX_VALUE / (long)this.wheel.length))}));
        }
        if (duration < MILLISECOND_NANOS) {
            logger.warn((String)"Configured tickDuration {} smaller then {}, using 1ms.", (Object)Long.valueOf((long)tickDuration), (Object)Long.valueOf((long)MILLISECOND_NANOS));
            this.tickDuration = MILLISECOND_NANOS;
        } else {
            this.tickDuration = duration;
        }
        this.workerThread = threadFactory.newThread((Runnable)this.worker);
        this.leak = leakDetection || !this.workerThread.isDaemon() ? leakDetector.track((HashedWheelTimer)this) : null;
        this.maxPendingTimeouts = maxPendingTimeouts;
        if (INSTANCE_COUNTER.incrementAndGet() <= 64) return;
        if (!WARNED_TOO_MANY_INSTANCES.compareAndSet((boolean)false, (boolean)true)) return;
        HashedWheelTimer.reportTooManyInstances();
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
            return;
        }
        finally {
            if (WORKER_STATE_UPDATER.getAndSet((HashedWheelTimer)this, (int)2) != 2) {
                INSTANCE_COUNTER.decrementAndGet();
            }
        }
    }

    private static HashedWheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException((String)("ticksPerWheel must be greater than 0: " + ticksPerWheel));
        }
        if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException((String)("ticksPerWheel may not be greater than 2^30: " + ticksPerWheel));
        }
        ticksPerWheel = HashedWheelTimer.normalizeTicksPerWheel((int)ticksPerWheel);
        HashedWheelBucket[] wheel = new HashedWheelBucket[ticksPerWheel];
        int i = 0;
        while (i < wheel.length) {
            wheel[i] = new HashedWheelBucket(null);
            ++i;
        }
        return wheel;
    }

    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = 1;
        while (normalizedTicksPerWheel < ticksPerWheel) {
            normalizedTicksPerWheel <<= 1;
        }
        return normalizedTicksPerWheel;
    }

    public void start() {
        switch (WORKER_STATE_UPDATER.get((HashedWheelTimer)this)) {
            case 0: {
                if (!WORKER_STATE_UPDATER.compareAndSet((HashedWheelTimer)this, (int)0, (int)1)) break;
                this.workerThread.start();
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                throw new IllegalStateException((String)"cannot be started once stopped");
            }
            default: {
                throw new Error((String)"Invalid WorkerState");
            }
        }
        while (this.startTime == 0L) {
            try {
                this.startTimeInitialized.await();
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    @Override
    public Set<Timeout> stop() {
        if (Thread.currentThread() == this.workerThread) {
            throw new IllegalStateException((String)(HashedWheelTimer.class.getSimpleName() + ".stop() cannot be called from " + TimerTask.class.getSimpleName()));
        }
        if (!WORKER_STATE_UPDATER.compareAndSet((HashedWheelTimer)this, (int)1, (int)2)) {
            if (WORKER_STATE_UPDATER.getAndSet((HashedWheelTimer)this, (int)2) == 2) return Collections.emptySet();
            INSTANCE_COUNTER.decrementAndGet();
            if (this.leak == null) return Collections.emptySet();
            boolean closed = this.leak.close((HashedWheelTimer)this);
            if ($assertionsDisabled) return Collections.emptySet();
            if (closed) return Collections.emptySet();
            throw new AssertionError();
        }
        try {
            boolean interrupted = false;
            while (this.workerThread.isAlive()) {
                this.workerThread.interrupt();
                try {
                    this.workerThread.join((long)100L);
                }
                catch (InterruptedException ignored) {
                    interrupted = true;
                }
            }
            if (!interrupted) return this.worker.unprocessedTimeouts();
            Thread.currentThread().interrupt();
            return this.worker.unprocessedTimeouts();
        }
        finally {
            INSTANCE_COUNTER.decrementAndGet();
            if (this.leak != null) {
                boolean closed = this.leak.close((HashedWheelTimer)this);
                assert (closed);
            }
        }
    }

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new NullPointerException((String)"task");
        }
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        long pendingTimeoutsCount = this.pendingTimeouts.incrementAndGet();
        if (this.maxPendingTimeouts > 0L && pendingTimeoutsCount > this.maxPendingTimeouts) {
            this.pendingTimeouts.decrementAndGet();
            throw new RejectedExecutionException((String)("Number of pending timeouts (" + pendingTimeoutsCount + ") is greater than or equal to maximum allowed pending timeouts (" + this.maxPendingTimeouts + ")"));
        }
        this.start();
        long deadline = System.nanoTime() + unit.toNanos((long)delay) - this.startTime;
        if (delay > 0L && deadline < 0L) {
            deadline = Long.MAX_VALUE;
        }
        HashedWheelTimeout timeout = new HashedWheelTimeout((HashedWheelTimer)this, (TimerTask)task, (long)deadline);
        this.timeouts.add((HashedWheelTimeout)timeout);
        return timeout;
    }

    public long pendingTimeouts() {
        return this.pendingTimeouts.get();
    }

    private static void reportTooManyInstances() {
        if (!logger.isErrorEnabled()) return;
        String resourceType = StringUtil.simpleClassName(HashedWheelTimer.class);
        logger.error((String)("You are creating too many " + resourceType + " instances. " + resourceType + " is a shared resource that must be reused across the JVM,so that only a few instances are created."));
    }

    static /* synthetic */ long access$202(HashedWheelTimer x0, long x1) {
        x0.startTime = x1;
        return x0.startTime;
    }

    static /* synthetic */ long access$200(HashedWheelTimer x0) {
        return x0.startTime;
    }

    static /* synthetic */ CountDownLatch access$300(HashedWheelTimer x0) {
        return x0.startTimeInitialized;
    }

    static /* synthetic */ int access$400(HashedWheelTimer x0) {
        return x0.mask;
    }

    static /* synthetic */ HashedWheelBucket[] access$500(HashedWheelTimer x0) {
        return x0.wheel;
    }

    static /* synthetic */ AtomicIntegerFieldUpdater access$600() {
        return WORKER_STATE_UPDATER;
    }

    static /* synthetic */ Queue access$700(HashedWheelTimer x0) {
        return x0.timeouts;
    }

    static /* synthetic */ long access$900(HashedWheelTimer x0) {
        return x0.tickDuration;
    }

    static /* synthetic */ Queue access$1000(HashedWheelTimer x0) {
        return x0.cancelledTimeouts;
    }

    static /* synthetic */ AtomicLong access$1100(HashedWheelTimer x0) {
        return x0.pendingTimeouts;
    }
}

