/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingBytesTracker;
import io.netty.channel.PendingWriteQueue;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class PendingWriteQueue {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PendingWriteQueue.class);
    private static final int PENDING_WRITE_OVERHEAD = SystemPropertyUtil.getInt((String)"io.netty.transport.pendingWriteSizeOverhead", (int)64);
    private final ChannelHandlerContext ctx;
    private final PendingBytesTracker tracker;
    private PendingWrite head;
    private PendingWrite tail;
    private int size;
    private long bytes;

    public PendingWriteQueue(ChannelHandlerContext ctx) {
        this.tracker = PendingBytesTracker.newTracker((Channel)ctx.channel());
        this.ctx = ctx;
    }

    public boolean isEmpty() {
        assert (this.ctx.executor().inEventLoop());
        if (this.head != null) return false;
        return true;
    }

    public int size() {
        if ($assertionsDisabled) return this.size;
        if (this.ctx.executor().inEventLoop()) return this.size;
        throw new AssertionError();
    }

    public long bytes() {
        if ($assertionsDisabled) return this.bytes;
        if (this.ctx.executor().inEventLoop()) return this.bytes;
        throw new AssertionError();
    }

    private int size(Object msg) {
        int messageSize = this.tracker.size((Object)msg);
        if (messageSize >= 0) return messageSize + PENDING_WRITE_OVERHEAD;
        messageSize = 0;
        return messageSize + PENDING_WRITE_OVERHEAD;
    }

    public void add(Object msg, ChannelPromise promise) {
        assert (this.ctx.executor().inEventLoop());
        if (msg == null) {
            throw new NullPointerException((String)"msg");
        }
        if (promise == null) {
            throw new NullPointerException((String)"promise");
        }
        int messageSize = this.size((Object)msg);
        PendingWrite write = PendingWrite.newInstance((Object)msg, (int)messageSize, (ChannelPromise)promise);
        PendingWrite currentTail = this.tail;
        if (currentTail == null) {
            this.tail = this.head = write;
        } else {
            ((PendingWrite)currentTail).next = (PendingWrite)write;
            this.tail = write;
        }
        ++this.size;
        this.bytes += (long)messageSize;
        this.tracker.incrementPendingOutboundBytes((long)((PendingWrite)write).size);
    }

    public ChannelFuture removeAndWriteAll() {
        assert (this.ctx.executor().inEventLoop());
        if (this.isEmpty()) {
            return null;
        }
        ChannelPromise p = this.ctx.newPromise();
        PromiseCombiner combiner = new PromiseCombiner((EventExecutor)this.ctx.executor());
        try {
            PendingWrite write = this.head;
            while (write != null) {
                this.tail = null;
                this.head = null;
                this.size = 0;
                this.bytes = 0L;
                while (write != null) {
                    PendingWrite next = ((PendingWrite)write).next;
                    Object msg = ((PendingWrite)write).msg;
                    ChannelPromise promise = ((PendingWrite)write).promise;
                    this.recycle((PendingWrite)write, (boolean)false);
                    if (!(promise instanceof VoidChannelPromise)) {
                        combiner.add((Promise)promise);
                    }
                    this.ctx.write((Object)msg, (ChannelPromise)promise);
                    write = next;
                }
                write = this.head;
            }
            combiner.finish((Promise<Void>)p);
        }
        catch (Throwable cause) {
            p.setFailure((Throwable)cause);
        }
        this.assertEmpty();
        return p;
    }

    public void removeAndFailAll(Throwable cause) {
        assert (this.ctx.executor().inEventLoop());
        if (cause == null) {
            throw new NullPointerException((String)"cause");
        }
        PendingWrite write = this.head;
        do {
            if (write == null) {
                this.assertEmpty();
                return;
            }
            this.tail = null;
            this.head = null;
            this.size = 0;
            this.bytes = 0L;
            while (write != null) {
                PendingWrite next = ((PendingWrite)write).next;
                ReferenceCountUtil.safeRelease((Object)((PendingWrite)write).msg);
                ChannelPromise promise = ((PendingWrite)write).promise;
                this.recycle((PendingWrite)write, (boolean)false);
                PendingWriteQueue.safeFail((ChannelPromise)promise, (Throwable)cause);
                write = next;
            }
            write = this.head;
        } while (true);
    }

    public void removeAndFail(Throwable cause) {
        assert (this.ctx.executor().inEventLoop());
        if (cause == null) {
            throw new NullPointerException((String)"cause");
        }
        PendingWrite write = this.head;
        if (write == null) {
            return;
        }
        ReferenceCountUtil.safeRelease((Object)((PendingWrite)write).msg);
        ChannelPromise promise = ((PendingWrite)write).promise;
        PendingWriteQueue.safeFail((ChannelPromise)promise, (Throwable)cause);
        this.recycle((PendingWrite)write, (boolean)true);
    }

    private void assertEmpty() {
        if ($assertionsDisabled) return;
        if (this.tail != null) throw new AssertionError();
        if (this.head != null) throw new AssertionError();
        if (this.size == 0) return;
        throw new AssertionError();
    }

    public ChannelFuture removeAndWrite() {
        assert (this.ctx.executor().inEventLoop());
        PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        Object msg = ((PendingWrite)write).msg;
        ChannelPromise promise = ((PendingWrite)write).promise;
        this.recycle((PendingWrite)write, (boolean)true);
        return this.ctx.write((Object)msg, (ChannelPromise)promise);
    }

    public ChannelPromise remove() {
        assert (this.ctx.executor().inEventLoop());
        PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        ChannelPromise promise = ((PendingWrite)write).promise;
        ReferenceCountUtil.safeRelease((Object)((PendingWrite)write).msg);
        this.recycle((PendingWrite)write, (boolean)true);
        return promise;
    }

    public Object current() {
        assert (this.ctx.executor().inEventLoop());
        PendingWrite write = this.head;
        if (write != null) return ((PendingWrite)write).msg;
        return null;
    }

    private void recycle(PendingWrite write, boolean update) {
        PendingWrite next = ((PendingWrite)write).next;
        long writeSize = ((PendingWrite)write).size;
        if (update) {
            if (next == null) {
                this.tail = null;
                this.head = null;
                this.size = 0;
                this.bytes = 0L;
            } else {
                this.head = next;
                --this.size;
                this.bytes -= writeSize;
                if (!$assertionsDisabled) {
                    if (this.size <= 0) throw new AssertionError();
                    if (this.bytes < 0L) {
                        throw new AssertionError();
                    }
                }
            }
        }
        ((PendingWrite)write).recycle();
        this.tracker.decrementPendingOutboundBytes((long)writeSize);
    }

    private static void safeFail(ChannelPromise promise, Throwable cause) {
        if (promise instanceof VoidChannelPromise) return;
        if (promise.tryFailure((Throwable)cause)) return;
        logger.warn((String)"Failed to mark a promise as failure because it's done already: {}", (Object)promise, (Object)cause);
    }
}

