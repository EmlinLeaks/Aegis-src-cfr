/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class GlobalTrafficShapingHandler
extends AbstractTrafficShapingHandler {
    private final ConcurrentMap<Integer, PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
    private final AtomicLong queuesSize = new AtomicLong();
    long maxGlobalWriteSize = 419430400L;

    void createGlobalTrafficCounter(ScheduledExecutorService executor) {
        if (executor == null) {
            throw new NullPointerException((String)"executor");
        }
        TrafficCounter tc = new TrafficCounter((AbstractTrafficShapingHandler)this, (ScheduledExecutorService)executor, (String)"GlobalTC", (long)this.checkInterval);
        this.setTrafficCounter((TrafficCounter)tc);
        tc.start();
    }

    @Override
    protected int userDefinedWritabilityIndex() {
        return 2;
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super((long)writeLimit, (long)readLimit, (long)checkInterval, (long)maxTime);
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval) {
        super((long)writeLimit, (long)readLimit, (long)checkInterval);
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit) {
        super((long)writeLimit, (long)readLimit);
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
        super((long)checkInterval);
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public GlobalTrafficShapingHandler(EventExecutor executor) {
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public long getMaxGlobalWriteSize() {
        return this.maxGlobalWriteSize;
    }

    public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
        this.maxGlobalWriteSize = maxGlobalWriteSize;
    }

    public long queuesSize() {
        return this.queuesSize.get();
    }

    public final void release() {
        this.trafficCounter.stop();
    }

    private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Integer key = Integer.valueOf((int)channel.hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
        if (perChannel != null) return perChannel;
        perChannel = new PerChannel(null);
        perChannel.messagesQueue = new ArrayDeque<E>();
        perChannel.queueSize = 0L;
        perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
        this.channelQueues.put((Integer)key, (PerChannel)perChannel);
        return perChannel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.getOrSetPerChannel((ChannelHandlerContext)ctx);
        super.handlerAdded((ChannelHandlerContext)ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Integer key = Integer.valueOf((int)channel.hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.remove((Object)key);
        if (perChannel != null) {
            PerChannel perChannel2 = perChannel;
            // MONITORENTER : perChannel2
            if (channel.isActive()) {
                for (ToSend toSend : perChannel.messagesQueue) {
                    long size = this.calculateSize((Object)toSend.toSend);
                    this.trafficCounter.bytesRealWriteFlowControl((long)size);
                    perChannel.queueSize -= size;
                    this.queuesSize.addAndGet((long)(-size));
                    ctx.write((Object)toSend.toSend, (ChannelPromise)toSend.promise);
                }
            } else {
                this.queuesSize.addAndGet((long)(-perChannel.queueSize));
                for (ToSend toSend : perChannel.messagesQueue) {
                    if (!(toSend.toSend instanceof ByteBuf)) continue;
                    ((ByteBuf)toSend.toSend).release();
                }
            }
            perChannel.messagesQueue.clear();
            // MONITOREXIT : perChannel2
        }
        this.releaseWriteSuspended((ChannelHandlerContext)ctx);
        this.releaseReadSuspended((ChannelHandlerContext)ctx);
        super.handlerRemoved((ChannelHandlerContext)ctx);
    }

    @Override
    long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        Integer key = Integer.valueOf((int)ctx.channel().hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
        if (perChannel == null) return wait;
        if (wait <= this.maxTime) return wait;
        if (now + wait - perChannel.lastReadTimestamp <= this.maxTime) return wait;
        return this.maxTime;
    }

    @Override
    void informReadOperation(ChannelHandlerContext ctx, long now) {
        Integer key = Integer.valueOf((int)ctx.channel().hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
        if (perChannel == null) return;
        perChannel.lastReadTimestamp = now;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void submitWrite(ChannelHandlerContext ctx, Object msg, long size, long writedelay, long now, ChannelPromise promise) {
        Channel channel = ctx.channel();
        Integer key = Integer.valueOf((int)channel.hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
        if (perChannel == null) {
            perChannel = this.getOrSetPerChannel((ChannelHandlerContext)ctx);
        }
        long delay = writedelay;
        boolean globalSizeExceeded = false;
        PerChannel perChannel2 = perChannel;
        // MONITORENTER : perChannel2
        if (writedelay == 0L && perChannel.messagesQueue.isEmpty()) {
            this.trafficCounter.bytesRealWriteFlowControl((long)size);
            ctx.write((Object)msg, (ChannelPromise)promise);
            perChannel.lastWriteTimestamp = now;
            // MONITOREXIT : perChannel2
            return;
        }
        if (delay > this.maxTime && now + delay - perChannel.lastWriteTimestamp > this.maxTime) {
            delay = this.maxTime;
        }
        ToSend newToSend = new ToSend((long)(delay + now), (Object)msg, (long)size, (ChannelPromise)promise, null);
        perChannel.messagesQueue.addLast((ToSend)newToSend);
        perChannel.queueSize += size;
        this.queuesSize.addAndGet((long)size);
        this.checkWriteSuspend((ChannelHandlerContext)ctx, (long)delay, (long)perChannel.queueSize);
        if (this.queuesSize.get() > this.maxGlobalWriteSize) {
            globalSizeExceeded = true;
        }
        // MONITOREXIT : perChannel2
        if (globalSizeExceeded) {
            this.setUserDefinedWritability((ChannelHandlerContext)ctx, (boolean)false);
        }
        long futureNow = newToSend.relativeTimeAction;
        PerChannel forSchedule = perChannel;
        ctx.executor().schedule((Runnable)new Runnable((GlobalTrafficShapingHandler)this, (ChannelHandlerContext)ctx, (PerChannel)forSchedule, (long)futureNow){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ PerChannel val$forSchedule;
            final /* synthetic */ long val$futureNow;
            final /* synthetic */ GlobalTrafficShapingHandler this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
                this.val$forSchedule = perChannel;
                this.val$futureNow = l;
            }

            public void run() {
                GlobalTrafficShapingHandler.access$200((GlobalTrafficShapingHandler)this.this$0, (ChannelHandlerContext)this.val$ctx, (PerChannel)this.val$forSchedule, (long)this.val$futureNow);
            }
        }, (long)delay, (TimeUnit)TimeUnit.MILLISECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendAllValid(ChannelHandlerContext ctx, PerChannel perChannel, long now) {
        PerChannel perChannel2 = perChannel;
        // MONITORENTER : perChannel2
        ToSend newToSend = perChannel.messagesQueue.pollFirst();
        while (newToSend != null) {
            if (newToSend.relativeTimeAction <= now) {
                long size = newToSend.size;
                this.trafficCounter.bytesRealWriteFlowControl((long)size);
                perChannel.queueSize -= size;
                this.queuesSize.addAndGet((long)(-size));
                ctx.write((Object)newToSend.toSend, (ChannelPromise)newToSend.promise);
                perChannel.lastWriteTimestamp = now;
                newToSend = perChannel.messagesQueue.pollFirst();
                continue;
            }
            perChannel.messagesQueue.addFirst((ToSend)newToSend);
            break;
        }
        if (perChannel.messagesQueue.isEmpty()) {
            this.releaseWriteSuspended((ChannelHandlerContext)ctx);
        }
        // MONITOREXIT : perChannel2
        ctx.flush();
    }

    static /* synthetic */ void access$200(GlobalTrafficShapingHandler x0, ChannelHandlerContext x1, PerChannel x2, long x3) {
        x0.sendAllValid((ChannelHandlerContext)x1, (PerChannel)x2, (long)x3);
    }
}

