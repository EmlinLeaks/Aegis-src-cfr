/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.timeout;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WriteTimeoutHandler
extends ChannelOutboundHandlerAdapter {
    private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos((long)1L);
    private final long timeoutNanos;
    private WriteTimeoutTask lastTask;
    private boolean closed;

    public WriteTimeoutHandler(int timeoutSeconds) {
        this((long)((long)timeoutSeconds), (TimeUnit)TimeUnit.SECONDS);
    }

    public WriteTimeoutHandler(long timeout, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        if (timeout <= 0L) {
            this.timeoutNanos = 0L;
            return;
        }
        this.timeoutNanos = Math.max((long)unit.toNanos((long)timeout), (long)MIN_TIMEOUT_NANOS);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.timeoutNanos > 0L) {
            promise = promise.unvoid();
            this.scheduleTimeout((ChannelHandlerContext)ctx, (ChannelPromise)promise);
        }
        ctx.write((Object)msg, (ChannelPromise)promise);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        WriteTimeoutTask task = this.lastTask;
        this.lastTask = null;
        while (task != null) {
            task.scheduledFuture.cancel((boolean)false);
            WriteTimeoutTask prev = task.prev;
            task.prev = null;
            task.next = null;
            task = prev;
        }
    }

    private void scheduleTimeout(ChannelHandlerContext ctx, ChannelPromise promise) {
        WriteTimeoutTask task = new WriteTimeoutTask((WriteTimeoutHandler)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise);
        task.scheduledFuture = ctx.executor().schedule((Runnable)task, (long)this.timeoutNanos, (TimeUnit)TimeUnit.NANOSECONDS);
        if (task.scheduledFuture.isDone()) return;
        this.addWriteTimeoutTask((WriteTimeoutTask)task);
        promise.addListener((GenericFutureListener<? extends Future<? super Void>>)task);
    }

    private void addWriteTimeoutTask(WriteTimeoutTask task) {
        if (this.lastTask != null) {
            this.lastTask.next = task;
            task.prev = this.lastTask;
        }
        this.lastTask = task;
    }

    private void removeWriteTimeoutTask(WriteTimeoutTask task) {
        if (task == this.lastTask) {
            assert (task.next == null);
            this.lastTask = this.lastTask.prev;
            if (this.lastTask != null) {
                this.lastTask.next = null;
            }
        } else {
            if (task.prev == null && task.next == null) {
                return;
            }
            if (task.prev == null) {
                task.next.prev = null;
            } else {
                task.prev.next = task.next;
                task.next.prev = task.prev;
            }
        }
        task.prev = null;
        task.next = null;
    }

    protected void writeTimedOut(ChannelHandlerContext ctx) throws Exception {
        if (this.closed) return;
        ctx.fireExceptionCaught((Throwable)WriteTimeoutException.INSTANCE);
        ctx.close();
        this.closed = true;
    }

    static /* synthetic */ void access$000(WriteTimeoutHandler x0, WriteTimeoutTask x1) {
        x0.removeWriteTimeoutTask((WriteTimeoutTask)x1);
    }
}

