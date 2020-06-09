/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.flush;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.handler.flush.FlushConsolidationHandler;
import java.util.concurrent.Future;

public class FlushConsolidationHandler
extends ChannelDuplexHandler {
    private final int explicitFlushAfterFlushes;
    private final boolean consolidateWhenNoReadInProgress;
    private final Runnable flushTask;
    private int flushPendingCount;
    private boolean readInProgress;
    private ChannelHandlerContext ctx;
    private Future<?> nextScheduledFlush;
    public static final int DEFAULT_EXPLICIT_FLUSH_AFTER_FLUSHES = 256;

    public FlushConsolidationHandler() {
        this((int)256, (boolean)false);
    }

    public FlushConsolidationHandler(int explicitFlushAfterFlushes) {
        this((int)explicitFlushAfterFlushes, (boolean)false);
    }

    public FlushConsolidationHandler(int explicitFlushAfterFlushes, boolean consolidateWhenNoReadInProgress) {
        if (explicitFlushAfterFlushes <= 0) {
            throw new IllegalArgumentException((String)("explicitFlushAfterFlushes: " + explicitFlushAfterFlushes + " (expected: > 0)"));
        }
        this.explicitFlushAfterFlushes = explicitFlushAfterFlushes;
        this.consolidateWhenNoReadInProgress = consolidateWhenNoReadInProgress;
        this.flushTask = consolidateWhenNoReadInProgress ? new Runnable((FlushConsolidationHandler)this){
            final /* synthetic */ FlushConsolidationHandler this$0;
            {
                this.this$0 = this$0;
            }

            public void run() {
                if (FlushConsolidationHandler.access$000((FlushConsolidationHandler)this.this$0) <= 0) return;
                if (FlushConsolidationHandler.access$100((FlushConsolidationHandler)this.this$0)) return;
                FlushConsolidationHandler.access$002((FlushConsolidationHandler)this.this$0, (int)0);
                FlushConsolidationHandler.access$200((FlushConsolidationHandler)this.this$0).flush();
                FlushConsolidationHandler.access$302((FlushConsolidationHandler)this.this$0, null);
            }
        } : null;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.readInProgress) {
            if (++this.flushPendingCount != this.explicitFlushAfterFlushes) return;
            this.flushNow((ChannelHandlerContext)ctx);
            return;
        }
        if (!this.consolidateWhenNoReadInProgress) {
            this.flushNow((ChannelHandlerContext)ctx);
            return;
        }
        if (++this.flushPendingCount == this.explicitFlushAfterFlushes) {
            this.flushNow((ChannelHandlerContext)ctx);
            return;
        }
        this.scheduleFlush((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.resetReadAndFlushIfNeeded((ChannelHandlerContext)ctx);
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.readInProgress = true;
        ctx.fireChannelRead((Object)msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.resetReadAndFlushIfNeeded((ChannelHandlerContext)ctx);
        ctx.fireExceptionCaught((Throwable)cause);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.resetReadAndFlushIfNeeded((ChannelHandlerContext)ctx);
        ctx.disconnect((ChannelPromise)promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.resetReadAndFlushIfNeeded((ChannelHandlerContext)ctx);
        ctx.close((ChannelPromise)promise);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (!ctx.channel().isWritable()) {
            this.flushIfNeeded((ChannelHandlerContext)ctx);
        }
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.flushIfNeeded((ChannelHandlerContext)ctx);
    }

    private void resetReadAndFlushIfNeeded(ChannelHandlerContext ctx) {
        this.readInProgress = false;
        this.flushIfNeeded((ChannelHandlerContext)ctx);
    }

    private void flushIfNeeded(ChannelHandlerContext ctx) {
        if (this.flushPendingCount <= 0) return;
        this.flushNow((ChannelHandlerContext)ctx);
    }

    private void flushNow(ChannelHandlerContext ctx) {
        this.cancelScheduledFlush();
        this.flushPendingCount = 0;
        ctx.flush();
    }

    private void scheduleFlush(ChannelHandlerContext ctx) {
        if (this.nextScheduledFlush != null) return;
        this.nextScheduledFlush = ctx.channel().eventLoop().submit((Runnable)this.flushTask);
    }

    private void cancelScheduledFlush() {
        if (this.nextScheduledFlush == null) return;
        this.nextScheduledFlush.cancel((boolean)false);
        this.nextScheduledFlush = null;
    }

    static /* synthetic */ int access$000(FlushConsolidationHandler x0) {
        return x0.flushPendingCount;
    }

    static /* synthetic */ boolean access$100(FlushConsolidationHandler x0) {
        return x0.readInProgress;
    }

    static /* synthetic */ int access$002(FlushConsolidationHandler x0, int x1) {
        x0.flushPendingCount = x1;
        return x0.flushPendingCount;
    }

    static /* synthetic */ ChannelHandlerContext access$200(FlushConsolidationHandler x0) {
        return x0.ctx;
    }

    static /* synthetic */ Future access$302(FlushConsolidationHandler x0, Future x1) {
        x0.nextScheduledFlush = x1;
        return x0.nextScheduledFlush;
    }
}

