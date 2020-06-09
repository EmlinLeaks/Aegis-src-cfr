/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTrafficShapingHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
    public static final long DEFAULT_CHECK_INTERVAL = 1000L;
    public static final long DEFAULT_MAX_TIME = 15000L;
    static final long DEFAULT_MAX_SIZE = 0x400000L;
    static final long MINIMAL_WAIT = 10L;
    protected TrafficCounter trafficCounter;
    private volatile long writeLimit;
    private volatile long readLimit;
    protected volatile long maxTime = 15000L;
    protected volatile long checkInterval = 1000L;
    static final AttributeKey<Boolean> READ_SUSPENDED = AttributeKey.valueOf((String)(AbstractTrafficShapingHandler.class.getName() + ".READ_SUSPENDED"));
    static final AttributeKey<Runnable> REOPEN_TASK = AttributeKey.valueOf((String)(AbstractTrafficShapingHandler.class.getName() + ".REOPEN_TASK"));
    volatile long maxWriteDelay = 4000L;
    volatile long maxWriteSize = 0x400000L;
    final int userDefinedWritabilityIndex;
    static final int CHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 1;
    static final int GLOBAL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 2;
    static final int GLOBALCHANNEL_DEFAULT_USER_DEFINED_WRITABILITY_INDEX = 3;

    void setTrafficCounter(TrafficCounter newTrafficCounter) {
        this.trafficCounter = newTrafficCounter;
    }

    protected int userDefinedWritabilityIndex() {
        return 1;
    }

    protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
        if (maxTime <= 0L) {
            throw new IllegalArgumentException((String)"maxTime must be positive");
        }
        this.userDefinedWritabilityIndex = this.userDefinedWritabilityIndex();
        this.writeLimit = writeLimit;
        this.readLimit = readLimit;
        this.checkInterval = checkInterval;
        this.maxTime = maxTime;
    }

    protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval) {
        this((long)writeLimit, (long)readLimit, (long)checkInterval, (long)15000L);
    }

    protected AbstractTrafficShapingHandler(long writeLimit, long readLimit) {
        this((long)writeLimit, (long)readLimit, (long)1000L, (long)15000L);
    }

    protected AbstractTrafficShapingHandler() {
        this((long)0L, (long)0L, (long)1000L, (long)15000L);
    }

    protected AbstractTrafficShapingHandler(long checkInterval) {
        this((long)0L, (long)0L, (long)checkInterval, (long)15000L);
    }

    public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval) {
        this.configure((long)newWriteLimit, (long)newReadLimit);
        this.configure((long)newCheckInterval);
    }

    public void configure(long newWriteLimit, long newReadLimit) {
        this.writeLimit = newWriteLimit;
        this.readLimit = newReadLimit;
        if (this.trafficCounter == null) return;
        this.trafficCounter.resetAccounting((long)TrafficCounter.milliSecondFromNano());
    }

    public void configure(long newCheckInterval) {
        this.checkInterval = newCheckInterval;
        if (this.trafficCounter == null) return;
        this.trafficCounter.configure((long)this.checkInterval);
    }

    public long getWriteLimit() {
        return this.writeLimit;
    }

    public void setWriteLimit(long writeLimit) {
        this.writeLimit = writeLimit;
        if (this.trafficCounter == null) return;
        this.trafficCounter.resetAccounting((long)TrafficCounter.milliSecondFromNano());
    }

    public long getReadLimit() {
        return this.readLimit;
    }

    public void setReadLimit(long readLimit) {
        this.readLimit = readLimit;
        if (this.trafficCounter == null) return;
        this.trafficCounter.resetAccounting((long)TrafficCounter.milliSecondFromNano());
    }

    public long getCheckInterval() {
        return this.checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
        if (this.trafficCounter == null) return;
        this.trafficCounter.configure((long)checkInterval);
    }

    public void setMaxTimeWait(long maxTime) {
        if (maxTime <= 0L) {
            throw new IllegalArgumentException((String)"maxTime must be positive");
        }
        this.maxTime = maxTime;
    }

    public long getMaxTimeWait() {
        return this.maxTime;
    }

    public long getMaxWriteDelay() {
        return this.maxWriteDelay;
    }

    public void setMaxWriteDelay(long maxWriteDelay) {
        if (maxWriteDelay <= 0L) {
            throw new IllegalArgumentException((String)"maxWriteDelay must be positive");
        }
        this.maxWriteDelay = maxWriteDelay;
    }

    public long getMaxWriteSize() {
        return this.maxWriteSize;
    }

    public void setMaxWriteSize(long maxWriteSize) {
        this.maxWriteSize = maxWriteSize;
    }

    protected void doAccounting(TrafficCounter counter) {
    }

    void releaseReadSuspended(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        channel.attr(READ_SUSPENDED).set((Boolean)Boolean.valueOf((boolean)false));
        channel.config().setAutoRead((boolean)true);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long size = this.calculateSize((Object)msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L) {
            long wait = this.trafficCounter.readTimeToWait((long)size, (long)this.readLimit, (long)this.maxTime, (long)now);
            if ((wait = this.checkWaitReadTime((ChannelHandlerContext)ctx, (long)wait, (long)now)) >= 10L) {
                Channel channel = ctx.channel();
                ChannelConfig config = channel.config();
                if (logger.isDebugEnabled()) {
                    logger.debug((String)("Read suspend: " + wait + ':' + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx)));
                }
                if (config.isAutoRead() && AbstractTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx)) {
                    config.setAutoRead((boolean)false);
                    channel.attr(READ_SUSPENDED).set((Boolean)Boolean.valueOf((boolean)true));
                    Attribute<Runnable> attr = channel.attr(REOPEN_TASK);
                    Runnable reopenTask = attr.get();
                    if (reopenTask == null) {
                        reopenTask = new ReopenReadTimerTask((ChannelHandlerContext)ctx);
                        attr.set((Runnable)reopenTask);
                    }
                    ctx.executor().schedule((Runnable)reopenTask, (long)wait, (TimeUnit)TimeUnit.MILLISECONDS);
                    if (logger.isDebugEnabled()) {
                        logger.debug((String)("Suspend final status => " + config.isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx) + " will reopened at: " + wait));
                    }
                }
            }
        }
        this.informReadOperation((ChannelHandlerContext)ctx, (long)now);
        ctx.fireChannelRead((Object)msg);
    }

    long checkWaitReadTime(ChannelHandlerContext ctx, long wait, long now) {
        return wait;
    }

    void informReadOperation(ChannelHandlerContext ctx, long now) {
    }

    protected static boolean isHandlerActive(ChannelHandlerContext ctx) {
        Boolean suspended = ctx.channel().attr(READ_SUSPENDED).get();
        if (suspended == null) return true;
        if (Boolean.FALSE.equals((Object)suspended)) return true;
        return false;
    }

    @Override
    public void read(ChannelHandlerContext ctx) {
        if (!AbstractTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx)) return;
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        long wait;
        long size = this.calculateSize((Object)msg);
        long now = TrafficCounter.milliSecondFromNano();
        if (size > 0L && (wait = this.trafficCounter.writeTimeToWait((long)size, (long)this.writeLimit, (long)this.maxTime, (long)now)) >= 10L) {
            if (logger.isDebugEnabled()) {
                logger.debug((String)("Write suspend: " + wait + ':' + ctx.channel().config().isAutoRead() + ':' + AbstractTrafficShapingHandler.isHandlerActive((ChannelHandlerContext)ctx)));
            }
            this.submitWrite((ChannelHandlerContext)ctx, (Object)msg, (long)size, (long)wait, (long)now, (ChannelPromise)promise);
            return;
        }
        this.submitWrite((ChannelHandlerContext)ctx, (Object)msg, (long)size, (long)0L, (long)now, (ChannelPromise)promise);
    }

    @Deprecated
    protected void submitWrite(ChannelHandlerContext ctx, Object msg, long delay, ChannelPromise promise) {
        this.submitWrite((ChannelHandlerContext)ctx, (Object)msg, (long)this.calculateSize((Object)msg), (long)delay, (long)TrafficCounter.milliSecondFromNano(), (ChannelPromise)promise);
    }

    abstract void submitWrite(ChannelHandlerContext var1, Object var2, long var3, long var5, long var7, ChannelPromise var9);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.setUserDefinedWritability((ChannelHandlerContext)ctx, (boolean)true);
        super.channelRegistered((ChannelHandlerContext)ctx);
    }

    void setUserDefinedWritability(ChannelHandlerContext ctx, boolean writable) {
        ChannelOutboundBuffer cob = ctx.channel().unsafe().outboundBuffer();
        if (cob == null) return;
        cob.setUserDefinedWritability((int)this.userDefinedWritabilityIndex, (boolean)writable);
    }

    void checkWriteSuspend(ChannelHandlerContext ctx, long delay, long queueSize) {
        if (queueSize <= this.maxWriteSize) {
            if (delay <= this.maxWriteDelay) return;
        }
        this.setUserDefinedWritability((ChannelHandlerContext)ctx, (boolean)false);
    }

    void releaseWriteSuspended(ChannelHandlerContext ctx) {
        this.setUserDefinedWritability((ChannelHandlerContext)ctx, (boolean)true);
    }

    public TrafficCounter trafficCounter() {
        return this.trafficCounter;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder((int)290).append((String)"TrafficShaping with Write Limit: ").append((long)this.writeLimit).append((String)" Read Limit: ").append((long)this.readLimit).append((String)" CheckInterval: ").append((long)this.checkInterval).append((String)" maxDelay: ").append((long)this.maxWriteDelay).append((String)" maxSize: ").append((long)this.maxWriteSize).append((String)" and Counter: ");
        if (this.trafficCounter != null) {
            builder.append((Object)this.trafficCounter);
            return builder.toString();
        }
        builder.append((String)"none");
        return builder.toString();
    }

    protected long calculateSize(Object msg) {
        if (msg instanceof ByteBuf) {
            return (long)((ByteBuf)msg).readableBytes();
        }
        if (!(msg instanceof ByteBufHolder)) return -1L;
        return (long)((ByteBufHolder)msg).content().readableBytes();
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }
}

