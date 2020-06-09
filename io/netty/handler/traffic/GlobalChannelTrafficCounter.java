/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.traffic;

import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.GlobalChannelTrafficCounter;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class GlobalChannelTrafficCounter
extends TrafficCounter {
    public GlobalChannelTrafficCounter(GlobalChannelTrafficShapingHandler trafficShapingHandler, ScheduledExecutorService executor, String name, long checkInterval) {
        super((AbstractTrafficShapingHandler)trafficShapingHandler, (ScheduledExecutorService)executor, (String)name, (long)checkInterval);
        if (executor != null) return;
        throw new IllegalArgumentException((String)"Executor must not be null");
    }

    @Override
    public synchronized void start() {
        if (this.monitorActive) {
            return;
        }
        this.lastTime.set((long)GlobalChannelTrafficCounter.milliSecondFromNano());
        long localCheckInterval = this.checkInterval.get();
        if (localCheckInterval <= 0L) return;
        this.monitorActive = true;
        this.monitor = new MixedTrafficMonitoringTask((GlobalChannelTrafficShapingHandler)((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler), (TrafficCounter)this);
        this.scheduledFuture = this.executor.scheduleAtFixedRate((Runnable)this.monitor, (long)0L, (long)localCheckInterval, (TimeUnit)TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void stop() {
        if (!this.monitorActive) {
            return;
        }
        this.monitorActive = false;
        this.resetAccounting((long)GlobalChannelTrafficCounter.milliSecondFromNano());
        this.trafficShapingHandler.doAccounting((TrafficCounter)this);
        if (this.scheduledFuture == null) return;
        this.scheduledFuture.cancel((boolean)true);
    }

    @Override
    public void resetCumulativeTime() {
        Iterator<V> iterator = ((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler).channelQueues.values().iterator();
        do {
            if (!iterator.hasNext()) {
                super.resetCumulativeTime();
                return;
            }
            GlobalChannelTrafficShapingHandler.PerChannel perChannel = (GlobalChannelTrafficShapingHandler.PerChannel)iterator.next();
            perChannel.channelTrafficCounter.resetCumulativeTime();
        } while (true);
    }
}

