/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.SmoothRateLimiter;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@Beta
@GwtIncompatible
public abstract class RateLimiter {
    private final SleepingStopwatch stopwatch;
    private volatile Object mutexDoNotUseDirectly;

    public static RateLimiter create(double permitsPerSecond) {
        return RateLimiter.create((SleepingStopwatch)SleepingStopwatch.createFromSystemTimer(), (double)permitsPerSecond);
    }

    @VisibleForTesting
    static RateLimiter create(SleepingStopwatch stopwatch, double permitsPerSecond) {
        SmoothRateLimiter.SmoothBursty rateLimiter = new SmoothRateLimiter.SmoothBursty((SleepingStopwatch)stopwatch, (double)1.0);
        rateLimiter.setRate((double)permitsPerSecond);
        return rateLimiter;
    }

    public static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
        Preconditions.checkArgument((boolean)(warmupPeriod >= 0L), (String)"warmupPeriod must not be negative: %s", (long)warmupPeriod);
        return RateLimiter.create((SleepingStopwatch)SleepingStopwatch.createFromSystemTimer(), (double)permitsPerSecond, (long)warmupPeriod, (TimeUnit)unit, (double)3.0);
    }

    @VisibleForTesting
    static RateLimiter create(SleepingStopwatch stopwatch, double permitsPerSecond, long warmupPeriod, TimeUnit unit, double coldFactor) {
        SmoothRateLimiter.SmoothWarmingUp rateLimiter = new SmoothRateLimiter.SmoothWarmingUp((SleepingStopwatch)stopwatch, (long)warmupPeriod, (TimeUnit)unit, (double)coldFactor);
        rateLimiter.setRate((double)permitsPerSecond);
        return rateLimiter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object mutex() {
        Object mutex = this.mutexDoNotUseDirectly;
        if (mutex != null) return mutex;
        RateLimiter rateLimiter = this;
        // MONITORENTER : rateLimiter
        mutex = this.mutexDoNotUseDirectly;
        if (mutex == null) {
            this.mutexDoNotUseDirectly = mutex = new Object();
        }
        // MONITOREXIT : rateLimiter
        return mutex;
    }

    RateLimiter(SleepingStopwatch stopwatch) {
        this.stopwatch = Preconditions.checkNotNull(stopwatch);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setRate(double permitsPerSecond) {
        Preconditions.checkArgument((boolean)(permitsPerSecond > 0.0 && !Double.isNaN((double)permitsPerSecond)), (Object)"rate must be positive");
        Object object = this.mutex();
        // MONITORENTER : object
        this.doSetRate((double)permitsPerSecond, (long)this.stopwatch.readMicros());
        // MONITOREXIT : object
        return;
    }

    abstract void doSetRate(double var1, long var3);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final double getRate() {
        Object object = this.mutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.doGetRate();
    }

    abstract double doGetRate();

    @CanIgnoreReturnValue
    public double acquire() {
        return this.acquire((int)1);
    }

    @CanIgnoreReturnValue
    public double acquire(int permits) {
        long microsToWait = this.reserve((int)permits);
        this.stopwatch.sleepMicrosUninterruptibly((long)microsToWait);
        return 1.0 * (double)microsToWait / (double)TimeUnit.SECONDS.toMicros((long)1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final long reserve(int permits) {
        RateLimiter.checkPermits((int)permits);
        Object object = this.mutex();
        // MONITORENTER : object
        // MONITOREXIT : object
        return this.reserveAndGetWaitLength((int)permits, (long)this.stopwatch.readMicros());
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) {
        return this.tryAcquire((int)1, (long)timeout, (TimeUnit)unit);
    }

    public boolean tryAcquire(int permits) {
        return this.tryAcquire((int)permits, (long)0L, (TimeUnit)TimeUnit.MICROSECONDS);
    }

    public boolean tryAcquire() {
        return this.tryAcquire((int)1, (long)0L, (TimeUnit)TimeUnit.MICROSECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
        long timeoutMicros = Math.max((long)unit.toMicros((long)timeout), (long)0L);
        RateLimiter.checkPermits((int)permits);
        Object object = this.mutex();
        // MONITORENTER : object
        long nowMicros = this.stopwatch.readMicros();
        if (!this.canAcquire((long)nowMicros, (long)timeoutMicros)) {
            // MONITOREXIT : object
            return false;
        }
        long microsToWait = this.reserveAndGetWaitLength((int)permits, (long)nowMicros);
        // MONITOREXIT : object
        this.stopwatch.sleepMicrosUninterruptibly((long)microsToWait);
        return true;
    }

    private boolean canAcquire(long nowMicros, long timeoutMicros) {
        if (this.queryEarliestAvailable((long)nowMicros) - timeoutMicros > nowMicros) return false;
        return true;
    }

    final long reserveAndGetWaitLength(int permits, long nowMicros) {
        long momentAvailable = this.reserveEarliestAvailable((int)permits, (long)nowMicros);
        return Math.max((long)(momentAvailable - nowMicros), (long)0L);
    }

    abstract long queryEarliestAvailable(long var1);

    abstract long reserveEarliestAvailable(int var1, long var2);

    public String toString() {
        return String.format((Locale)Locale.ROOT, (String)"RateLimiter[stableRate=%3.1fqps]", (Object[])new Object[]{Double.valueOf((double)this.getRate())});
    }

    private static void checkPermits(int permits) {
        Preconditions.checkArgument((boolean)(permits > 0), (String)"Requested permits (%s) must be positive", (int)permits);
    }
}

