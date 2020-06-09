/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.math.LongMath;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.SmoothRateLimiter;
import java.util.concurrent.TimeUnit;

@GwtIncompatible
abstract class SmoothRateLimiter
extends RateLimiter {
    double storedPermits;
    double maxPermits;
    double stableIntervalMicros;
    private long nextFreeTicketMicros = 0L;

    private SmoothRateLimiter(RateLimiter.SleepingStopwatch stopwatch) {
        super((RateLimiter.SleepingStopwatch)stopwatch);
    }

    @Override
    final void doSetRate(double permitsPerSecond, long nowMicros) {
        double stableIntervalMicros;
        this.resync((long)nowMicros);
        this.stableIntervalMicros = stableIntervalMicros = (double)TimeUnit.SECONDS.toMicros((long)1L) / permitsPerSecond;
        this.doSetRate((double)permitsPerSecond, (double)stableIntervalMicros);
    }

    abstract void doSetRate(double var1, double var3);

    @Override
    final double doGetRate() {
        return (double)TimeUnit.SECONDS.toMicros((long)1L) / this.stableIntervalMicros;
    }

    @Override
    final long queryEarliestAvailable(long nowMicros) {
        return this.nextFreeTicketMicros;
    }

    @Override
    final long reserveEarliestAvailable(int requiredPermits, long nowMicros) {
        this.resync((long)nowMicros);
        long returnValue = this.nextFreeTicketMicros;
        double storedPermitsToSpend = Math.min((double)((double)requiredPermits), (double)this.storedPermits);
        double freshPermits = (double)requiredPermits - storedPermitsToSpend;
        long waitMicros = this.storedPermitsToWaitTime((double)this.storedPermits, (double)storedPermitsToSpend) + (long)(freshPermits * this.stableIntervalMicros);
        this.nextFreeTicketMicros = LongMath.saturatedAdd((long)this.nextFreeTicketMicros, (long)waitMicros);
        this.storedPermits -= storedPermitsToSpend;
        return returnValue;
    }

    abstract long storedPermitsToWaitTime(double var1, double var3);

    abstract double coolDownIntervalMicros();

    void resync(long nowMicros) {
        if (nowMicros <= this.nextFreeTicketMicros) return;
        double newPermits = (double)(nowMicros - this.nextFreeTicketMicros) / this.coolDownIntervalMicros();
        this.storedPermits = Math.min((double)this.maxPermits, (double)(this.storedPermits + newPermits));
        this.nextFreeTicketMicros = nowMicros;
    }
}

