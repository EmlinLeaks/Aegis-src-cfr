/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.traffic;

import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficCounter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(TrafficCounter.class);
    private final AtomicLong currentWrittenBytes = new AtomicLong();
    private final AtomicLong currentReadBytes = new AtomicLong();
    private long writingTime;
    private long readingTime;
    private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
    private final AtomicLong cumulativeReadBytes = new AtomicLong();
    private long lastCumulativeTime;
    private long lastWriteThroughput;
    private long lastReadThroughput;
    final AtomicLong lastTime = new AtomicLong();
    private volatile long lastWrittenBytes;
    private volatile long lastReadBytes;
    private volatile long lastWritingTime;
    private volatile long lastReadingTime;
    private final AtomicLong realWrittenBytes = new AtomicLong();
    private long realWriteThroughput;
    final AtomicLong checkInterval = new AtomicLong((long)1000L);
    final String name;
    final AbstractTrafficShapingHandler trafficShapingHandler;
    final ScheduledExecutorService executor;
    Runnable monitor;
    volatile ScheduledFuture<?> scheduledFuture;
    volatile boolean monitorActive;

    public static long milliSecondFromNano() {
        return System.nanoTime() / 1000000L;
    }

    public synchronized void start() {
        if (this.monitorActive) {
            return;
        }
        this.lastTime.set((long)TrafficCounter.milliSecondFromNano());
        long localCheckInterval = this.checkInterval.get();
        if (localCheckInterval <= 0L) return;
        if (this.executor == null) return;
        this.monitorActive = true;
        this.monitor = new TrafficMonitoringTask((TrafficCounter)this, null);
        this.scheduledFuture = this.executor.scheduleAtFixedRate((Runnable)this.monitor, (long)0L, (long)localCheckInterval, (TimeUnit)TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        if (!this.monitorActive) {
            return;
        }
        this.monitorActive = false;
        this.resetAccounting((long)TrafficCounter.milliSecondFromNano());
        if (this.trafficShapingHandler != null) {
            this.trafficShapingHandler.doAccounting((TrafficCounter)this);
        }
        if (this.scheduledFuture == null) return;
        this.scheduledFuture.cancel((boolean)true);
    }

    synchronized void resetAccounting(long newLastTime) {
        long interval = newLastTime - this.lastTime.getAndSet((long)newLastTime);
        if (interval == 0L) {
            return;
        }
        if (logger.isDebugEnabled() && interval > this.checkInterval() << 1) {
            logger.debug((String)("Acct schedule not ok: " + interval + " > 2*" + this.checkInterval() + " from " + this.name));
        }
        this.lastReadBytes = this.currentReadBytes.getAndSet((long)0L);
        this.lastWrittenBytes = this.currentWrittenBytes.getAndSet((long)0L);
        this.lastReadThroughput = this.lastReadBytes * 1000L / interval;
        this.lastWriteThroughput = this.lastWrittenBytes * 1000L / interval;
        this.realWriteThroughput = this.realWrittenBytes.getAndSet((long)0L) * 1000L / interval;
        this.lastWritingTime = Math.max((long)this.lastWritingTime, (long)this.writingTime);
        this.lastReadingTime = Math.max((long)this.lastReadingTime, (long)this.readingTime);
    }

    public TrafficCounter(ScheduledExecutorService executor, String name, long checkInterval) {
        if (name == null) {
            throw new NullPointerException((String)"name");
        }
        this.trafficShapingHandler = null;
        this.executor = executor;
        this.name = name;
        this.init((long)checkInterval);
    }

    public TrafficCounter(AbstractTrafficShapingHandler trafficShapingHandler, ScheduledExecutorService executor, String name, long checkInterval) {
        if (trafficShapingHandler == null) {
            throw new IllegalArgumentException((String)"trafficShapingHandler");
        }
        if (name == null) {
            throw new NullPointerException((String)"name");
        }
        this.trafficShapingHandler = trafficShapingHandler;
        this.executor = executor;
        this.name = name;
        this.init((long)checkInterval);
    }

    private void init(long checkInterval) {
        this.lastCumulativeTime = System.currentTimeMillis();
        this.readingTime = this.writingTime = TrafficCounter.milliSecondFromNano();
        this.lastWritingTime = this.writingTime;
        this.lastReadingTime = this.writingTime;
        this.configure((long)checkInterval);
    }

    public void configure(long newCheckInterval) {
        long newInterval = newCheckInterval / 10L * 10L;
        if (this.checkInterval.getAndSet((long)newInterval) == newInterval) return;
        if (newInterval <= 0L) {
            this.stop();
            this.lastTime.set((long)TrafficCounter.milliSecondFromNano());
            return;
        }
        this.stop();
        this.start();
    }

    void bytesRecvFlowControl(long recv) {
        this.currentReadBytes.addAndGet((long)recv);
        this.cumulativeReadBytes.addAndGet((long)recv);
    }

    void bytesWriteFlowControl(long write) {
        this.currentWrittenBytes.addAndGet((long)write);
        this.cumulativeWrittenBytes.addAndGet((long)write);
    }

    void bytesRealWriteFlowControl(long write) {
        this.realWrittenBytes.addAndGet((long)write);
    }

    public long checkInterval() {
        return this.checkInterval.get();
    }

    public long lastReadThroughput() {
        return this.lastReadThroughput;
    }

    public long lastWriteThroughput() {
        return this.lastWriteThroughput;
    }

    public long lastReadBytes() {
        return this.lastReadBytes;
    }

    public long lastWrittenBytes() {
        return this.lastWrittenBytes;
    }

    public long currentReadBytes() {
        return this.currentReadBytes.get();
    }

    public long currentWrittenBytes() {
        return this.currentWrittenBytes.get();
    }

    public long lastTime() {
        return this.lastTime.get();
    }

    public long cumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }

    public long cumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }

    public long lastCumulativeTime() {
        return this.lastCumulativeTime;
    }

    public AtomicLong getRealWrittenBytes() {
        return this.realWrittenBytes;
    }

    public long getRealWriteThroughput() {
        return this.realWriteThroughput;
    }

    public void resetCumulativeTime() {
        this.lastCumulativeTime = System.currentTimeMillis();
        this.cumulativeReadBytes.set((long)0L);
        this.cumulativeWrittenBytes.set((long)0L);
    }

    public String name() {
        return this.name;
    }

    @Deprecated
    public long readTimeToWait(long size, long limitTraffic, long maxTime) {
        return this.readTimeToWait((long)size, (long)limitTraffic, (long)maxTime, (long)TrafficCounter.milliSecondFromNano());
    }

    public long readTimeToWait(long size, long limitTraffic, long maxTime, long now) {
        this.bytesRecvFlowControl((long)size);
        if (size == 0L) return 0L;
        if (limitTraffic == 0L) {
            return 0L;
        }
        long lastTimeCheck = this.lastTime.get();
        long sum = this.currentReadBytes.get();
        long localReadingTime = this.readingTime;
        long lastRB = this.lastReadBytes;
        long interval = now - lastTimeCheck;
        long pastDelay = Math.max((long)(this.lastReadingTime - lastTimeCheck), (long)0L);
        if (interval > 10L) {
            long time = sum * 1000L / limitTraffic - interval + pastDelay;
            if (time <= 10L) {
                this.readingTime = Math.max((long)localReadingTime, (long)now);
                return 0L;
            }
            if (logger.isDebugEnabled()) {
                logger.debug((String)("Time: " + time + ':' + sum + ':' + interval + ':' + pastDelay));
            }
            if (time > maxTime && now + time - localReadingTime > maxTime) {
                time = maxTime;
            }
            this.readingTime = Math.max((long)localReadingTime, (long)(now + time));
            return time;
        }
        long lastsum = sum + lastRB;
        long lastinterval = interval + this.checkInterval.get();
        long time = lastsum * 1000L / limitTraffic - lastinterval + pastDelay;
        if (time <= 10L) {
            this.readingTime = Math.max((long)localReadingTime, (long)now);
            return 0L;
        }
        if (logger.isDebugEnabled()) {
            logger.debug((String)("Time: " + time + ':' + lastsum + ':' + lastinterval + ':' + pastDelay));
        }
        if (time > maxTime && now + time - localReadingTime > maxTime) {
            time = maxTime;
        }
        this.readingTime = Math.max((long)localReadingTime, (long)(now + time));
        return time;
    }

    @Deprecated
    public long writeTimeToWait(long size, long limitTraffic, long maxTime) {
        return this.writeTimeToWait((long)size, (long)limitTraffic, (long)maxTime, (long)TrafficCounter.milliSecondFromNano());
    }

    public long writeTimeToWait(long size, long limitTraffic, long maxTime, long now) {
        this.bytesWriteFlowControl((long)size);
        if (size == 0L) return 0L;
        if (limitTraffic == 0L) {
            return 0L;
        }
        long lastTimeCheck = this.lastTime.get();
        long sum = this.currentWrittenBytes.get();
        long lastWB = this.lastWrittenBytes;
        long localWritingTime = this.writingTime;
        long pastDelay = Math.max((long)(this.lastWritingTime - lastTimeCheck), (long)0L);
        long interval = now - lastTimeCheck;
        if (interval > 10L) {
            long time = sum * 1000L / limitTraffic - interval + pastDelay;
            if (time <= 10L) {
                this.writingTime = Math.max((long)localWritingTime, (long)now);
                return 0L;
            }
            if (logger.isDebugEnabled()) {
                logger.debug((String)("Time: " + time + ':' + sum + ':' + interval + ':' + pastDelay));
            }
            if (time > maxTime && now + time - localWritingTime > maxTime) {
                time = maxTime;
            }
            this.writingTime = Math.max((long)localWritingTime, (long)(now + time));
            return time;
        }
        long lastsum = sum + lastWB;
        long lastinterval = interval + this.checkInterval.get();
        long time = lastsum * 1000L / limitTraffic - lastinterval + pastDelay;
        if (time <= 10L) {
            this.writingTime = Math.max((long)localWritingTime, (long)now);
            return 0L;
        }
        if (logger.isDebugEnabled()) {
            logger.debug((String)("Time: " + time + ':' + lastsum + ':' + lastinterval + ':' + pastDelay));
        }
        if (time > maxTime && now + time - localWritingTime > maxTime) {
            time = maxTime;
        }
        this.writingTime = Math.max((long)localWritingTime, (long)(now + time));
        return time;
    }

    public String toString() {
        return new StringBuilder((int)165).append((String)"Monitor ").append((String)this.name).append((String)" Current Speed Read: ").append((long)(this.lastReadThroughput >> 10)).append((String)" KB/s, ").append((String)"Asked Write: ").append((long)(this.lastWriteThroughput >> 10)).append((String)" KB/s, ").append((String)"Real Write: ").append((long)(this.realWriteThroughput >> 10)).append((String)" KB/s, ").append((String)"Current Read: ").append((long)(this.currentReadBytes.get() >> 10)).append((String)" KB, ").append((String)"Current asked Write: ").append((long)(this.currentWrittenBytes.get() >> 10)).append((String)" KB, ").append((String)"Current real Write: ").append((long)(this.realWrittenBytes.get() >> 10)).append((String)" KB").toString();
    }
}

