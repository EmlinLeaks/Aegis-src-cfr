/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.ArrayDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChannelTrafficShapingHandler
extends AbstractTrafficShapingHandler {
    private final ArrayDeque<ToSend> messagesQueue = new ArrayDeque<E>();
    private long queueSize;

    public ChannelTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super((long)writeLimit, (long)readLimit, (long)checkInterval, (long)maxTime);
    }

    public ChannelTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval) {
        super((long)writeLimit, (long)readLimit, (long)checkInterval);
    }

    public ChannelTrafficShapingHandler(long writeLimit, long readLimit) {
        super((long)writeLimit, (long)readLimit);
    }

    public ChannelTrafficShapingHandler(long checkInterval) {
        super((long)checkInterval);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        TrafficCounter trafficCounter = new TrafficCounter((AbstractTrafficShapingHandler)this, (ScheduledExecutorService)ctx.executor(), (String)("ChannelTC" + ctx.channel().hashCode()), (long)this.checkInterval);
        this.setTrafficCounter((TrafficCounter)trafficCounter);
        trafficCounter.start();
        super.handlerAdded((ChannelHandlerContext)ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.trafficCounter.stop();
        ChannelTrafficShapingHandler channelTrafficShapingHandler = this;
        // MONITORENTER : channelTrafficShapingHandler
        if (ctx.channel().isActive()) {
            for (ToSend toSend : this.messagesQueue) {
                long size = this.calculateSize((Object)toSend.toSend);
                this.trafficCounter.bytesRealWriteFlowControl((long)size);
                this.queueSize -= size;
                ctx.write((Object)toSend.toSend, (ChannelPromise)toSend.promise);
            }
        } else {
            for (ToSend toSend : this.messagesQueue) {
                if (!(toSend.toSend instanceof ByteBuf)) continue;
                ((ByteBuf)toSend.toSend).release();
            }
        }
        this.messagesQueue.clear();
        // MONITOREXIT : channelTrafficShapingHandler
        this.releaseWriteSuspended((ChannelHandlerContext)ctx);
        this.releaseReadSuspended((ChannelHandlerContext)ctx);
        super.handlerRemoved((ChannelHandlerContext)ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void submitWrite(ChannelHandlerContext ctx, Object msg, long size, long delay, long now, ChannelPromise promise) {
        ChannelTrafficShapingHandler channelTrafficShapingHandler = this;
        // MONITORENTER : channelTrafficShapingHandler
        if (delay == 0L && this.messagesQueue.isEmpty()) {
            this.trafficCounter.bytesRealWriteFlowControl((long)size);
            ctx.write((Object)msg, (ChannelPromise)promise);
            // MONITOREXIT : channelTrafficShapingHandler
            return;
        }
        ToSend newToSend = new ToSend((long)(delay + now), (Object)msg, (ChannelPromise)promise, null);
        this.messagesQueue.addLast((ToSend)newToSend);
        this.queueSize += size;
        this.checkWriteSuspend((ChannelHandlerContext)ctx, (long)delay, (long)this.queueSize);
        // MONITOREXIT : channelTrafficShapingHandler
        long futureNow = newToSend.relativeTimeAction;
        ctx.executor().schedule((Runnable)new Runnable((ChannelTrafficShapingHandler)this, (ChannelHandlerContext)ctx, (long)futureNow){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ long val$futureNow;
            final /* synthetic */ ChannelTrafficShapingHandler this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
                this.val$futureNow = l;
            }

            public void run() {
                ChannelTrafficShapingHandler.access$100((ChannelTrafficShapingHandler)this.this$0, (ChannelHandlerContext)this.val$ctx, (long)this.val$futureNow);
            }
        }, (long)delay, (TimeUnit)TimeUnit.MILLISECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendAllValid(ChannelHandlerContext ctx, long now) {
        ChannelTrafficShapingHandler channelTrafficShapingHandler = this;
        // MONITORENTER : channelTrafficShapingHandler
        ToSend newToSend = this.messagesQueue.pollFirst();
        while (newToSend != null) {
            if (newToSend.relativeTimeAction <= now) {
                long size = this.calculateSize((Object)newToSend.toSend);
                this.trafficCounter.bytesRealWriteFlowControl((long)size);
                this.queueSize -= size;
                ctx.write((Object)newToSend.toSend, (ChannelPromise)newToSend.promise);
                newToSend = this.messagesQueue.pollFirst();
                continue;
            }
            this.messagesQueue.addFirst((ToSend)newToSend);
            break;
        }
        if (this.messagesQueue.isEmpty()) {
            this.releaseWriteSuspended((ChannelHandlerContext)ctx);
        }
        // MONITOREXIT : channelTrafficShapingHandler
        ctx.flush();
    }

    public long queueSize() {
        return this.queueSize;
    }

    static /* synthetic */ void access$100(ChannelTrafficShapingHandler x0, ChannelHandlerContext x1, long x2) {
        x0.sendAllValid((ChannelHandlerContext)x1, (long)x2);
    }
}

