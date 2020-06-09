/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.GlobalChannelTrafficCounter;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class GlobalChannelTrafficShapingHandler
extends AbstractTrafficShapingHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalChannelTrafficShapingHandler.class);
    final ConcurrentMap<Integer, PerChannel> channelQueues = PlatformDependent.newConcurrentHashMap();
    private final AtomicLong queuesSize = new AtomicLong();
    private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
    private final AtomicLong cumulativeReadBytes = new AtomicLong();
    volatile long maxGlobalWriteSize = 419430400L;
    private volatile long writeChannelLimit;
    private volatile long readChannelLimit;
    private static final float DEFAULT_DEVIATION = 0.1f;
    private static final float MAX_DEVIATION = 0.4f;
    private static final float DEFAULT_SLOWDOWN = 0.4f;
    private static final float DEFAULT_ACCELERATION = -0.1f;
    private volatile float maxDeviation;
    private volatile float accelerationFactor;
    private volatile float slowDownFactor;
    private volatile boolean readDeviationActive;
    private volatile boolean writeDeviationActive;

    void createGlobalTrafficCounter(ScheduledExecutorService executor) {
        this.setMaxDeviation((float)0.1f, (float)0.4f, (float)-0.1f);
        if (executor == null) {
            throw new IllegalArgumentException((String)"Executor must not be null");
        }
        GlobalChannelTrafficCounter tc = new GlobalChannelTrafficCounter((GlobalChannelTrafficShapingHandler)this, (ScheduledExecutorService)executor, (String)"GlobalChannelTC", (long)this.checkInterval);
        this.setTrafficCounter((TrafficCounter)tc);
        ((TrafficCounter)tc).start();
    }

    @Override
    protected int userDefinedWritabilityIndex() {
        return 3;
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval, long maxTime) {
        super((long)writeGlobalLimit, (long)readGlobalLimit, (long)checkInterval, (long)maxTime);
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit, long checkInterval) {
        super((long)writeGlobalLimit, (long)readGlobalLimit, (long)checkInterval);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long writeGlobalLimit, long readGlobalLimit, long writeChannelLimit, long readChannelLimit) {
        super((long)writeGlobalLimit, (long)readGlobalLimit);
        this.writeChannelLimit = writeChannelLimit;
        this.readChannelLimit = readChannelLimit;
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
        super((long)checkInterval);
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public GlobalChannelTrafficShapingHandler(ScheduledExecutorService executor) {
        this.createGlobalTrafficCounter((ScheduledExecutorService)executor);
    }

    public float maxDeviation() {
        return this.maxDeviation;
    }

    public float accelerationFactor() {
        return this.accelerationFactor;
    }

    public float slowDownFactor() {
        return this.slowDownFactor;
    }

    public void setMaxDeviation(float maxDeviation, float slowDownFactor, float accelerationFactor) {
        if (maxDeviation > 0.4f) {
            throw new IllegalArgumentException((String)"maxDeviation must be <= 0.4");
        }
        if (slowDownFactor < 0.0f) {
            throw new IllegalArgumentException((String)"slowDownFactor must be >= 0");
        }
        if (accelerationFactor > 0.0f) {
            throw new IllegalArgumentException((String)"accelerationFactor must be <= 0");
        }
        this.maxDeviation = maxDeviation;
        this.accelerationFactor = 1.0f + accelerationFactor;
        this.slowDownFactor = 1.0f + slowDownFactor;
    }

    private void computeDeviationCumulativeBytes() {
        long maxWrittenBytes = 0L;
        long maxReadBytes = 0L;
        long minWrittenBytes = Long.MAX_VALUE;
        long minReadBytes = Long.MAX_VALUE;
        for (PerChannel perChannel : this.channelQueues.values()) {
            long value = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
            if (maxWrittenBytes < value) {
                maxWrittenBytes = value;
            }
            if (minWrittenBytes > value) {
                minWrittenBytes = value;
            }
            if (maxReadBytes < (value = perChannel.channelTrafficCounter.cumulativeReadBytes())) {
                maxReadBytes = value;
            }
            if (minReadBytes <= value) continue;
            minReadBytes = value;
        }
        boolean multiple = this.channelQueues.size() > 1;
        this.readDeviationActive = multiple && minReadBytes < maxReadBytes / 2L;
        this.writeDeviationActive = multiple && minWrittenBytes < maxWrittenBytes / 2L;
        this.cumulativeWrittenBytes.set((long)maxWrittenBytes);
        this.cumulativeReadBytes.set((long)maxReadBytes);
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        this.computeDeviationCumulativeBytes();
        super.doAccounting((TrafficCounter)counter);
    }

    private long computeBalancedWait(float maxLocal, float maxGlobal, long wait) {
        if (maxGlobal == 0.0f) {
            return wait;
        }
        float ratio = maxLocal / maxGlobal;
        if (!(ratio > this.maxDeviation)) {
            ratio = this.accelerationFactor;
            return (long)((float)wait * ratio);
        }
        if (ratio < 1.0f - this.maxDeviation) {
            return wait;
        }
        ratio = this.slowDownFactor;
        if (wait >= 10L) return (long)((float)wait * ratio);
        wait = 10L;
        return (long)((float)wait * ratio);
    }

    public long getMaxGlobalWriteSize() {
        return this.maxGlobalWriteSize;
    }

    public void setMaxGlobalWriteSize(long maxGlobalWriteSize) {
        if (maxGlobalWriteSize <= 0L) {
            throw new IllegalArgumentException((String)"maxGlobalWriteSize must be positive");
        }
        this.maxGlobalWriteSize = maxGlobalWriteSize;
    }

    public long queuesSize() {
        return this.queuesSize.get();
    }

    public void configureChannel(long newWriteLimit, long newReadLimit) {
        this.writeChannelLimit = newWriteLimit;
        this.readChannelLimit = newReadLimit;
        long now = TrafficCounter.milliSecondFromNano();
        Iterator<V> iterator = this.channelQueues.values().iterator();
        while (iterator.hasNext()) {
            PerChannel perChannel = (PerChannel)iterator.next();
            perChannel.channelTrafficCounter.resetAccounting((long)now);
        }
    }

    public long getWriteChannelLimit() {
        return this.writeChannelLimit;
    }

    public void setWriteChannelLimit(long writeLimit) {
        this.writeChannelLimit = writeLimit;
        long now = TrafficCounter.milliSecondFromNano();
        Iterator<V> iterator = this.channelQueues.values().iterator();
        while (iterator.hasNext()) {
            PerChannel perChannel = (PerChannel)iterator.next();
            perChannel.channelTrafficCounter.resetAccounting((long)now);
        }
    }

    public long getReadChannelLimit() {
        return this.readChannelLimit;
    }

    public void setReadChannelLimit(long readLimit) {
        this.readChannelLimit = readLimit;
        long now = TrafficCounter.milliSecondFromNano();
        Iterator<V> iterator = this.channelQueues.values().iterator();
        while (iterator.hasNext()) {
            PerChannel perChannel = (PerChannel)iterator.next();
            perChannel.channelTrafficCounter.resetAccounting((long)now);
        }
    }

    public final void release() {
        this.trafficCounter.stop();
    }

    private PerChannel getOrSetPerChannel(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Integer key = Integer.valueOf((int)channel.hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
        if (perChannel != null) return perChannel;
        perChannel = new PerChannel();
        perChannel.messagesQueue = new ArrayDeque<E>();
        perChannel.channelTrafficCounter = new TrafficCounter((AbstractTrafficShapingHandler)this, null, (String)("ChannelTC" + ctx.channel().hashCode()), (long)this.checkInterval);
        perChannel.queueSize = 0L;
        perChannel.lastWriteTimestamp = perChannel.lastReadTimestamp = TrafficCounter.milliSecondFromNano();
        this.channelQueues.put((Integer)key, (PerChannel)perChannel);
        return perChannel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.getOrSetPerChannel((ChannelHandlerContext)ctx);
        this.trafficCounter.resetCumulativeTime();
        super.handlerAdded((ChannelHandlerContext)ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.trafficCounter.resetCumulativeTime();
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
                    perChannel.channelTrafficCounter.bytesRealWriteFlowControl((long)size);
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long size = this.calculateSize((Object)msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L) {
            long waitGlobal = this.trafficCounter.readTimeToWait((long)size, (long)this.getReadLimit(), (long)this.maxTime, (long)now);
            Integer key = Integer.valueOf((int)ctx.channel().hashCode());
            PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
            long wait = 0L;
            if (perChannel != null) {
                wait = perChannel.channelTrafficCounter.readTimeToWait((long)size, (long)this.readChannelLimit, (long)this.maxTime, (long)now);
                if (this.readDeviationActive) {
                    long maxLocalRead = perChannel.channelTrafficCounter.cumulativeReadBytes();
                    long maxGlobalRead = this.cumulativeReadBytes.get();
                    if (maxLocalRead <= 0L) {
                        maxLocalRead = 0L;
                    }
                    if (maxGlobalRead < maxLocalRead) {
                        maxGlobalRead = maxLocalRead;
                    }
                    wait = this.computeBalancedWait((float)((float)maxLocalRead), (float)((float)maxGlobalRead), (long)wait);
                }
            }
            if (wait < waitGlobal) {
                wait = waitGlobal;
            }
            if ((wait = this.checkWaitReadTime((ChannelHandlerContext)ctx, (long)wait, (long)now)) >= 10L) {
                Channel channel = ctx.channel();
                ChannelConfig config = channel.config();
                if (logger.isDebugEnabled()) {
                    logger.debug((String)("Read Suspend: " + wait + ':' + config.isAutoRead() + ':' + GlobalChannelTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx)));
                }
                if (config.isAutoRead() && GlobalChannelTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx)) {
                    config.setAutoRead((boolean)false);
                    channel.attr(READ_SUSPENDED).set(Boolean.valueOf((boolean)true));
                    Attribute<Runnable> attr = channel.attr(REOPEN_TASK);
                    Runnable reopenTask = (Runnable)attr.get();
                    if (reopenTask == null) {
                        reopenTask = new AbstractTrafficShapingHandler.ReopenReadTimerTask((ChannelHandlerContext)ctx);
                        attr.set(reopenTask);
                    }
                    ctx.executor().schedule((Runnable)reopenTask, (long)wait, (TimeUnit)TimeUnit.MILLISECONDS);
                    if (logger.isDebugEnabled()) {
                        logger.debug((String)("Suspend final status => " + config.isAutoRead() + ':' + GlobalChannelTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx) + " will reopened at: " + wait));
                    }
                }
            }
        }
        this.informReadOperation((ChannelHandlerContext)ctx, (long)now);
        ctx.fireChannelRead((Object)msg);
    }

    @Override
    protected long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        Integer key = Integer.valueOf((int)ctx.channel().hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
        if (perChannel == null) return wait;
        if (wait <= this.maxTime) return wait;
        if (now + wait - perChannel.lastReadTimestamp <= this.maxTime) return wait;
        return this.maxTime;
    }

    @Override
    protected void informReadOperation(ChannelHandlerContext ctx, long now) {
        Integer key = Integer.valueOf((int)ctx.channel().hashCode());
        PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
        if (perChannel == null) return;
        perChannel.lastReadTimestamp = now;
    }

    protected long maximumCumulativeWrittenBytes() {
        return this.cumulativeWrittenBytes.get();
    }

    protected long maximumCumulativeReadBytes() {
        return this.cumulativeReadBytes.get();
    }

    public Collection<TrafficCounter> channelTrafficCounters() {
        return new AbstractCollection<TrafficCounter>((GlobalChannelTrafficShapingHandler)this){
            final /* synthetic */ GlobalChannelTrafficShapingHandler this$0;
            {
                this.this$0 = this$0;
            }

            public Iterator<TrafficCounter> iterator() {
                return new Iterator<TrafficCounter>(this){
                    final Iterator<PerChannel> iter;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.iter = this.this$1.this$0.channelQueues.values().iterator();
                    }

                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }

                    public TrafficCounter next() {
                        return this.iter.next().channelTrafficCounter;
                    }

                    public void remove() {
                        throw new java.lang.UnsupportedOperationException();
                    }
                };
            }

            public int size() {
                return this.this$0.channelQueues.size();
            }
        };
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        long size = this.calculateSize((Object)msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L) {
            long waitGlobal = this.trafficCounter.writeTimeToWait((long)size, (long)this.getWriteLimit(), (long)this.maxTime, (long)now);
            Integer key = Integer.valueOf((int)ctx.channel().hashCode());
            PerChannel perChannel = (PerChannel)this.channelQueues.get((Object)key);
            long wait = 0L;
            if (perChannel != null) {
                wait = perChannel.channelTrafficCounter.writeTimeToWait((long)size, (long)this.writeChannelLimit, (long)this.maxTime, (long)now);
                if (this.writeDeviationActive) {
                    long maxLocalWrite = perChannel.channelTrafficCounter.cumulativeWrittenBytes();
                    long maxGlobalWrite = this.cumulativeWrittenBytes.get();
                    if (maxLocalWrite <= 0L) {
                        maxLocalWrite = 0L;
                    }
                    if (maxGlobalWrite < maxLocalWrite) {
                        maxGlobalWrite = maxLocalWrite;
                    }
                    wait = this.computeBalancedWait((float)((float)maxLocalWrite), (float)((float)maxGlobalWrite), (long)wait);
                }
            }
            if (wait < waitGlobal) {
                wait = waitGlobal;
            }
            if (wait >= 10L) {
                if (logger.isDebugEnabled()) {
                    logger.debug((String)("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':' + GlobalChannelTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx)));
                }
                this.submitWrite((ChannelHandlerContext)ctx, (Object)msg, (long)size, (long)wait, (long)now, (ChannelPromise)promise);
                return;
            }
        }
        this.submitWrite((ChannelHandlerContext)ctx, (Object)msg, (long)size, (long)0L, (long)now, (ChannelPromise)promise);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void submitWrite(ChannelHandlerContext ctx, Object msg, long size, long writedelay, long now, ChannelPromise promise) {
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
            perChannel.channelTrafficCounter.bytesRealWriteFlowControl((long)size);
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
        ctx.executor().schedule((Runnable)new Runnable((GlobalChannelTrafficShapingHandler)this, (ChannelHandlerContext)ctx, (PerChannel)forSchedule, (long)futureNow){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ PerChannel val$forSchedule;
            final /* synthetic */ long val$futureNow;
            final /* synthetic */ GlobalChannelTrafficShapingHandler this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
                this.val$forSchedule = perChannel;
                this.val$futureNow = l;
            }

            public void run() {
                GlobalChannelTrafficShapingHandler.access$100((GlobalChannelTrafficShapingHandler)this.this$0, (ChannelHandlerContext)this.val$ctx, (PerChannel)this.val$forSchedule, (long)this.val$futureNow);
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
                perChannel.channelTrafficCounter.bytesRealWriteFlowControl((long)size);
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

    @Override
    public String toString() {
        return new StringBuilder((int)340).append((String)super.toString()).append((String)" Write Channel Limit: ").append((long)this.writeChannelLimit).append((String)" Read Channel Limit: ").append((long)this.readChannelLimit).toString();
    }

    static /* synthetic */ void access$100(GlobalChannelTrafficShapingHandler x0, ChannelHandlerContext x1, PerChannel x2, long x3) {
        x0.sendAllValid((ChannelHandlerContext)x1, (PerChannel)x2, (long)x3);
    }
}

