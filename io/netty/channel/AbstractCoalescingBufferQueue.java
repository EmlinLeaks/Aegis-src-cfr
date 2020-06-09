/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DelegatingChannelPromiseNotifier;
import io.netty.channel.PendingBytesTracker;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Collection;

public abstract class AbstractCoalescingBufferQueue {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractCoalescingBufferQueue.class);
    private final ArrayDeque<Object> bufAndListenerPairs;
    private final PendingBytesTracker tracker;
    private int readableBytes;

    protected AbstractCoalescingBufferQueue(Channel channel, int initSize) {
        this.bufAndListenerPairs = new ArrayDeque<E>((int)initSize);
        this.tracker = channel == null ? null : PendingBytesTracker.newTracker((Channel)channel);
    }

    public final void addFirst(ByteBuf buf, ChannelPromise promise) {
        this.addFirst((ByteBuf)buf, (ChannelFutureListener)AbstractCoalescingBufferQueue.toChannelFutureListener((ChannelPromise)promise));
    }

    private void addFirst(ByteBuf buf, ChannelFutureListener listener) {
        if (listener != null) {
            this.bufAndListenerPairs.addFirst((Object)listener);
        }
        this.bufAndListenerPairs.addFirst((Object)buf);
        this.incrementReadableBytes((int)buf.readableBytes());
    }

    public final void add(ByteBuf buf) {
        this.add((ByteBuf)buf, (ChannelFutureListener)((ChannelFutureListener)null));
    }

    public final void add(ByteBuf buf, ChannelPromise promise) {
        this.add((ByteBuf)buf, (ChannelFutureListener)AbstractCoalescingBufferQueue.toChannelFutureListener((ChannelPromise)promise));
    }

    public final void add(ByteBuf buf, ChannelFutureListener listener) {
        this.bufAndListenerPairs.add((Object)buf);
        if (listener != null) {
            this.bufAndListenerPairs.add((Object)listener);
        }
        this.incrementReadableBytes((int)buf.readableBytes());
    }

    public final ByteBuf removeFirst(ChannelPromise aggregatePromise) {
        Object entry = this.bufAndListenerPairs.poll();
        if (entry == null) {
            return null;
        }
        assert (entry instanceof ByteBuf);
        ByteBuf result = (ByteBuf)entry;
        this.decrementReadableBytes((int)result.readableBytes());
        entry = this.bufAndListenerPairs.peek();
        if (!(entry instanceof ChannelFutureListener)) return result;
        aggregatePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)((ChannelFutureListener)entry));
        this.bufAndListenerPairs.poll();
        return result;
    }

    public final ByteBuf remove(ByteBufAllocator alloc, int bytes, ChannelPromise aggregatePromise) {
        ObjectUtil.checkPositiveOrZero((int)bytes, (String)"bytes");
        ObjectUtil.checkNotNull(aggregatePromise, (String)"aggregatePromise");
        if (this.bufAndListenerPairs.isEmpty()) {
            return this.removeEmptyValue();
        }
        bytes = Math.min((int)bytes, (int)this.readableBytes);
        ByteBuf toReturn = null;
        ByteBuf entryBuffer = null;
        int originalBytes = bytes;
        try {
            Object entry;
            while ((entry = this.bufAndListenerPairs.poll()) != null) {
                if (entry instanceof ChannelFutureListener) {
                    aggregatePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)((ChannelFutureListener)entry));
                    continue;
                }
                entryBuffer = (ByteBuf)entry;
                if (entryBuffer.readableBytes() > bytes) {
                    this.bufAndListenerPairs.addFirst((Object)entryBuffer);
                    if (bytes > 0) {
                        entryBuffer = entryBuffer.readRetainedSlice((int)bytes);
                        toReturn = toReturn == null ? this.composeFirst((ByteBufAllocator)alloc, (ByteBuf)entryBuffer) : this.compose((ByteBufAllocator)alloc, (ByteBuf)toReturn, (ByteBuf)entryBuffer);
                        bytes = 0;
                    }
                    break;
                }
                bytes -= entryBuffer.readableBytes();
                toReturn = toReturn == null ? this.composeFirst((ByteBufAllocator)alloc, (ByteBuf)entryBuffer) : this.compose((ByteBufAllocator)alloc, toReturn, (ByteBuf)entryBuffer);
                entryBuffer = null;
            }
        }
        catch (Throwable cause) {
            ReferenceCountUtil.safeRelease(entryBuffer);
            ReferenceCountUtil.safeRelease(toReturn);
            aggregatePromise.setFailure((Throwable)cause);
            PlatformDependent.throwException((Throwable)cause);
        }
        this.decrementReadableBytes((int)(originalBytes - bytes));
        return toReturn;
    }

    public final int readableBytes() {
        return this.readableBytes;
    }

    public final boolean isEmpty() {
        return this.bufAndListenerPairs.isEmpty();
    }

    public final void releaseAndFailAll(ChannelOutboundInvoker invoker, Throwable cause) {
        this.releaseAndCompleteAll((ChannelFuture)invoker.newFailedFuture((Throwable)cause));
    }

    public final void copyTo(AbstractCoalescingBufferQueue dest) {
        dest.bufAndListenerPairs.addAll(this.bufAndListenerPairs);
        dest.incrementReadableBytes((int)this.readableBytes);
    }

    public final void writeAndRemoveAll(ChannelHandlerContext ctx) {
        this.decrementReadableBytes((int)this.readableBytes);
        Throwable pending = null;
        ByteBuf previousBuf = null;
        do {
            Object entry = this.bufAndListenerPairs.poll();
            try {
                if (entry == null) {
                    if (previousBuf == null) break;
                    ctx.write(previousBuf, (ChannelPromise)ctx.voidPromise());
                    break;
                }
                if (entry instanceof ByteBuf) {
                    if (previousBuf != null) {
                        ctx.write((Object)previousBuf, (ChannelPromise)ctx.voidPromise());
                    }
                    previousBuf = (ByteBuf)entry;
                    continue;
                }
                if (entry instanceof ChannelPromise) {
                    ctx.write((Object)previousBuf, (ChannelPromise)((ChannelPromise)entry));
                    previousBuf = null;
                    continue;
                }
                ctx.write((Object)previousBuf).addListener((GenericFutureListener<? extends Future<? super Void>>)((ChannelFutureListener)entry));
                previousBuf = null;
            }
            catch (Throwable t) {
                if (pending == null) {
                    pending = t;
                    continue;
                }
                logger.info((String)"Throwable being suppressed because Throwable {} is already pending", (Object)pending, (Object)t);
            }
        } while (true);
        if (pending == null) return;
        throw new IllegalStateException((Throwable)pending);
    }

    protected abstract ByteBuf compose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);

    protected final ByteBuf composeIntoComposite(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
        CompositeByteBuf composite = alloc.compositeBuffer((int)(this.size() + 2));
        try {
            composite.addComponent((boolean)true, (ByteBuf)cumulation);
            composite.addComponent((boolean)true, (ByteBuf)next);
            return composite;
        }
        catch (Throwable cause) {
            composite.release();
            ReferenceCountUtil.safeRelease((Object)next);
            PlatformDependent.throwException((Throwable)cause);
        }
        return composite;
    }

    protected final ByteBuf copyAndCompose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
        ByteBuf newCumulation = alloc.ioBuffer((int)(cumulation.readableBytes() + next.readableBytes()));
        try {
            newCumulation.writeBytes((ByteBuf)cumulation).writeBytes((ByteBuf)next);
        }
        catch (Throwable cause) {
            newCumulation.release();
            ReferenceCountUtil.safeRelease((Object)next);
            PlatformDependent.throwException((Throwable)cause);
        }
        cumulation.release();
        next.release();
        return newCumulation;
    }

    protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
        return first;
    }

    protected abstract ByteBuf removeEmptyValue();

    protected final int size() {
        return this.bufAndListenerPairs.size();
    }

    private void releaseAndCompleteAll(ChannelFuture future) {
        this.decrementReadableBytes((int)this.readableBytes);
        Throwable pending = null;
        do {
            Object entry;
            if ((entry = this.bufAndListenerPairs.poll()) == null) {
                if (pending == null) return;
                throw new IllegalStateException(pending);
            }
            try {
                if (entry instanceof ByteBuf) {
                    ReferenceCountUtil.safeRelease((Object)entry);
                    continue;
                }
                ((ChannelFutureListener)entry).operationComplete(future);
            }
            catch (Throwable t) {
                if (pending == null) {
                    pending = t;
                    continue;
                }
                logger.info((String)"Throwable being suppressed because Throwable {} is already pending", (Object)pending, (Object)t);
                continue;
            }
            break;
        } while (true);
    }

    private void incrementReadableBytes(int increment) {
        int nextReadableBytes = this.readableBytes + increment;
        if (nextReadableBytes < this.readableBytes) {
            throw new IllegalStateException((String)("buffer queue length overflow: " + this.readableBytes + " + " + increment));
        }
        this.readableBytes = nextReadableBytes;
        if (this.tracker == null) return;
        this.tracker.incrementPendingOutboundBytes((long)((long)increment));
    }

    private void decrementReadableBytes(int decrement) {
        this.readableBytes -= decrement;
        assert (this.readableBytes >= 0);
        if (this.tracker == null) return;
        this.tracker.decrementPendingOutboundBytes((long)((long)decrement));
    }

    private static ChannelFutureListener toChannelFutureListener(ChannelPromise promise) {
        if (promise.isVoid()) {
            return null;
        }
        DelegatingChannelPromiseNotifier delegatingChannelPromiseNotifier = new DelegatingChannelPromiseNotifier((ChannelPromise)promise);
        return delegatingChannelPromiseNotifier;
    }
}

