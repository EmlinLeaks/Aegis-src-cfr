/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.TimeUnit;

@GwtCompatible
public final class Stopwatch {
    private final Ticker ticker;
    private boolean isRunning;
    private long elapsedNanos;
    private long startTick;

    public static Stopwatch createUnstarted() {
        return new Stopwatch();
    }

    public static Stopwatch createUnstarted(Ticker ticker) {
        return new Stopwatch((Ticker)ticker);
    }

    public static Stopwatch createStarted() {
        return new Stopwatch().start();
    }

    public static Stopwatch createStarted(Ticker ticker) {
        return new Stopwatch((Ticker)ticker).start();
    }

    Stopwatch() {
        this.ticker = Ticker.systemTicker();
    }

    Stopwatch(Ticker ticker) {
        this.ticker = Preconditions.checkNotNull(ticker, (Object)"ticker");
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @CanIgnoreReturnValue
    public Stopwatch start() {
        Preconditions.checkState((boolean)(!this.isRunning), (Object)"This stopwatch is already running.");
        this.isRunning = true;
        this.startTick = this.ticker.read();
        return this;
    }

    @CanIgnoreReturnValue
    public Stopwatch stop() {
        long tick = this.ticker.read();
        Preconditions.checkState((boolean)this.isRunning, (Object)"This stopwatch is already stopped.");
        this.isRunning = false;
        this.elapsedNanos += tick - this.startTick;
        return this;
    }

    @CanIgnoreReturnValue
    public Stopwatch reset() {
        this.elapsedNanos = 0L;
        this.isRunning = false;
        return this;
    }

    private long elapsedNanos() {
        long l;
        if (this.isRunning) {
            l = this.ticker.read() - this.startTick + this.elapsedNanos;
            return l;
        }
        l = this.elapsedNanos;
        return l;
    }

    public long elapsed(TimeUnit desiredUnit) {
        return desiredUnit.convert((long)this.elapsedNanos(), (TimeUnit)TimeUnit.NANOSECONDS);
    }

    public String toString() {
        long nanos = this.elapsedNanos();
        TimeUnit unit = Stopwatch.chooseUnit((long)nanos);
        double value = (double)nanos / (double)TimeUnit.NANOSECONDS.convert((long)1L, (TimeUnit)unit);
        return Platform.formatCompact4Digits((double)value) + " " + Stopwatch.abbreviate((TimeUnit)unit);
    }

    private static TimeUnit chooseUnit(long nanos) {
        if (TimeUnit.DAYS.convert((long)nanos, (TimeUnit)TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.DAYS;
        }
        if (TimeUnit.HOURS.convert((long)nanos, (TimeUnit)TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.HOURS;
        }
        if (TimeUnit.MINUTES.convert((long)nanos, (TimeUnit)TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.MINUTES;
        }
        if (TimeUnit.SECONDS.convert((long)nanos, (TimeUnit)TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.SECONDS;
        }
        if (TimeUnit.MILLISECONDS.convert((long)nanos, (TimeUnit)TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.MILLISECONDS;
        }
        if (TimeUnit.MICROSECONDS.convert((long)nanos, (TimeUnit)TimeUnit.NANOSECONDS) <= 0L) return TimeUnit.NANOSECONDS;
        return TimeUnit.MICROSECONDS;
    }

    private static String abbreviate(TimeUnit unit) {
        switch (1.$SwitchMap$java$util$concurrent$TimeUnit[unit.ordinal()]) {
            case 1: {
                return "ns";
            }
            case 2: {
                return "\u03bcs";
            }
            case 3: {
                return "ms";
            }
            case 4: {
                return "s";
            }
            case 5: {
                return "min";
            }
            case 6: {
                return "h";
            }
            case 7: {
                return "d";
            }
        }
        throw new AssertionError();
    }
}

