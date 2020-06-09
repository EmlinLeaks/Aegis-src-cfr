/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class ChannelOutboundBuffer {
    static final int CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD = SystemPropertyUtil.getInt((String)"io.netty.transport.outboundBufferEntrySizeOverhead", (int)96);
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
    private static final FastThreadLocal<ByteBuffer[]> NIO_BUFFERS = new FastThreadLocal<ByteBuffer[]>(){

        protected ByteBuffer[] initialValue() throws Exception {
            return new ByteBuffer[1024];
        }
    };
    private final Channel channel;
    private Entry flushedEntry;
    private Entry unflushedEntry;
    private Entry tailEntry;
    private int flushed;
    private int nioBufferCount;
    private long nioBufferSize;
    private boolean inFail;
    private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, (String)"totalPendingSize");
    private volatile long totalPendingSize;
    private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, (String)"unwritable");
    private volatile int unwritable;
    private volatile Runnable fireChannelWritabilityChangedTask;

    ChannelOutboundBuffer(AbstractChannel channel) {
        this.channel = channel;
    }

    public void addMessage(Object msg, int size, ChannelPromise promise) {
        Entry entry = Entry.newInstance((Object)msg, (int)size, (long)ChannelOutboundBuffer.total((Object)msg), (ChannelPromise)promise);
        if (this.tailEntry == null) {
            this.flushedEntry = null;
        } else {
            Entry tail = this.tailEntry;
            tail.next = entry;
        }
        this.tailEntry = entry;
        if (this.unflushedEntry == null) {
            this.unflushedEntry = entry;
        }
        this.incrementPendingOutboundBytes((long)((long)entry.pendingSize), (boolean)false);
    }

    public void addFlush() {
        Entry entry = this.unflushedEntry;
        if (entry == null) return;
        if (this.flushedEntry == null) {
            this.flushedEntry = entry;
        }
        do {
            ++this.flushed;
            if (entry.promise.setUncancellable()) continue;
            int pending = entry.cancel();
            this.decrementPendingOutboundBytes((long)((long)pending), (boolean)false, (boolean)true);
        } while ((entry = entry.next) != null);
        this.unflushedEntry = null;
    }

    void incrementPendingOutboundBytes(long size) {
        this.incrementPendingOutboundBytes((long)size, (boolean)true);
    }

    private void incrementPendingOutboundBytes(long size, boolean invokeLater) {
        if (size == 0L) {
            return;
        }
        long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet((ChannelOutboundBuffer)this, (long)size);
        if (newWriteBufferSize <= (long)this.channel.config().getWriteBufferHighWaterMark()) return;
        this.setUnwritable((boolean)invokeLater);
    }

    void decrementPendingOutboundBytes(long size) {
        this.decrementPendingOutboundBytes((long)size, (boolean)true, (boolean)true);
    }

    private void decrementPendingOutboundBytes(long size, boolean invokeLater, boolean notifyWritability) {
        if (size == 0L) {
            return;
        }
        long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet((ChannelOutboundBuffer)this, (long)(-size));
        if (!notifyWritability) return;
        if (newWriteBufferSize >= (long)this.channel.config().getWriteBufferLowWaterMark()) return;
        this.setWritable((boolean)invokeLater);
    }

    private static long total(Object msg) {
        if (msg instanceof ByteBuf) {
            return (long)((ByteBuf)msg).readableBytes();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion)msg).count();
        }
        if (!(msg instanceof ByteBufHolder)) return -1L;
        return (long)((ByteBufHolder)msg).content().readableBytes();
    }

    public Object current() {
        Entry entry = this.flushedEntry;
        if (entry != null) return entry.msg;
        return null;
    }

    public long currentProgress() {
        Entry entry = this.flushedEntry;
        if (entry != null) return entry.progress;
        return 0L;
    }

    public void progress(long amount) {
        long progress;
        Entry e = this.flushedEntry;
        assert (e != null);
        ChannelPromise p = e.promise;
        e.progress = progress = e.progress + amount;
        if (!(p instanceof ChannelProgressivePromise)) return;
        ((ChannelProgressivePromise)p).tryProgress((long)progress, (long)e.total);
    }

    public boolean remove() {
        Entry e = this.flushedEntry;
        if (e == null) {
            this.clearNioBuffers();
            return false;
        }
        Object msg = e.msg;
        ChannelPromise promise = e.promise;
        int size = e.pendingSize;
        this.removeEntry((Entry)e);
        if (!e.cancelled) {
            ReferenceCountUtil.safeRelease((Object)msg);
            ChannelOutboundBuffer.safeSuccess((ChannelPromise)promise);
            this.decrementPendingOutboundBytes((long)((long)size), (boolean)false, (boolean)true);
        }
        e.recycle();
        return true;
    }

    public boolean remove(Throwable cause) {
        return this.remove0((Throwable)cause, (boolean)true);
    }

    private boolean remove0(Throwable cause, boolean notifyWritability) {
        Entry e = this.flushedEntry;
        if (e == null) {
            this.clearNioBuffers();
            return false;
        }
        Object msg = e.msg;
        ChannelPromise promise = e.promise;
        int size = e.pendingSize;
        this.removeEntry((Entry)e);
        if (!e.cancelled) {
            ReferenceCountUtil.safeRelease((Object)msg);
            ChannelOutboundBuffer.safeFail((ChannelPromise)promise, (Throwable)cause);
            this.decrementPendingOutboundBytes((long)((long)size), (boolean)false, (boolean)notifyWritability);
        }
        e.recycle();
        return true;
    }

    private void removeEntry(Entry e) {
        if (--this.flushed == 0) {
            this.flushedEntry = null;
            if (e != this.tailEntry) return;
            this.tailEntry = null;
            this.unflushedEntry = null;
            return;
        }
        this.flushedEntry = e.next;
    }

    public void removeBytes(long writtenBytes) {
        block5 : {
            int readerIndex;
            ByteBuf buf;
            do {
                Object msg;
                if (!((msg = this.current()) instanceof ByteBuf)) {
                    assert (writtenBytes == 0L);
                    break block5;
                }
                buf = (ByteBuf)msg;
                readerIndex = buf.readerIndex();
                int readableBytes = buf.writerIndex() - readerIndex;
                if ((long)readableBytes > writtenBytes) break;
                if (writtenBytes != 0L) {
                    this.progress((long)((long)readableBytes));
                    writtenBytes -= (long)readableBytes;
                }
                this.remove();
            } while (true);
            if (writtenBytes != 0L) {
                buf.readerIndex((int)(readerIndex + (int)writtenBytes));
                this.progress((long)writtenBytes);
            }
        }
        this.clearNioBuffers();
    }

    private void clearNioBuffers() {
        int count = this.nioBufferCount;
        if (count <= 0) return;
        this.nioBufferCount = 0;
        Arrays.fill((Object[])((Object[])NIO_BUFFERS.get()), (int)0, (int)count, null);
    }

    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers((int)Integer.MAX_VALUE, (long)Integer.MAX_VALUE);
    }

    public ByteBuffer[] nioBuffers(int maxCount, long maxBytes) {
        assert (maxCount > 0);
        assert (maxBytes > 0L);
        long nioBufferSize = 0L;
        int nioBufferCount = 0;
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
        ByteBuffer[] nioBuffers = NIO_BUFFERS.get((InternalThreadLocalMap)threadLocalMap);
        Entry entry = this.flushedEntry;
        while (this.isFlushedEntry((Entry)entry) && entry.msg instanceof ByteBuf) {
            if (!entry.cancelled) {
                ByteBuf buf = (ByteBuf)entry.msg;
                int readerIndex = buf.readerIndex();
                int readableBytes = buf.writerIndex() - readerIndex;
                if (readableBytes > 0) {
                    int neededSpace;
                    if (maxBytes - (long)readableBytes < nioBufferSize && nioBufferCount != 0) break;
                    nioBufferSize += (long)readableBytes;
                    int count = entry.count;
                    if (count == -1) {
                        entry.count = count = buf.nioBufferCount();
                    }
                    if ((neededSpace = Math.min((int)maxCount, (int)(nioBufferCount + count))) > nioBuffers.length) {
                        nioBuffers = ChannelOutboundBuffer.expandNioBufferArray((ByteBuffer[])nioBuffers, (int)neededSpace, (int)nioBufferCount);
                        NIO_BUFFERS.set((InternalThreadLocalMap)threadLocalMap, (ByteBuffer[])nioBuffers);
                    }
                    if (count == 1) {
                        ByteBuffer nioBuf = entry.buf;
                        if (nioBuf == null) {
                            entry.buf = nioBuf = buf.internalNioBuffer((int)readerIndex, (int)readableBytes);
                        }
                        nioBuffers[nioBufferCount++] = nioBuf;
                    } else {
                        nioBufferCount = ChannelOutboundBuffer.nioBuffers((Entry)entry, (ByteBuf)buf, (ByteBuffer[])nioBuffers, (int)nioBufferCount, (int)maxCount);
                    }
                    if (nioBufferCount == maxCount) break;
                }
            }
            entry = entry.next;
        }
        this.nioBufferCount = nioBufferCount;
        this.nioBufferSize = nioBufferSize;
        return nioBuffers;
    }

    private static int nioBuffers(Entry entry, ByteBuf buf, ByteBuffer[] nioBuffers, int nioBufferCount, int maxCount) {
        ByteBuffer[] nioBufs = entry.bufs;
        if (nioBufs == null) {
            entry.bufs = nioBufs = buf.nioBuffers();
        }
        int i = 0;
        while (i < nioBufs.length) {
            if (nioBufferCount >= maxCount) return nioBufferCount;
            ByteBuffer nioBuf = nioBufs[i];
            if (nioBuf == null) {
                return nioBufferCount;
            }
            if (nioBuf.hasRemaining()) {
                nioBuffers[nioBufferCount++] = nioBuf;
            }
            ++i;
        }
        return nioBufferCount;
    }

    private static ByteBuffer[] expandNioBufferArray(ByteBuffer[] array, int neededSpace, int size) {
        int newCapacity = array.length;
        do {
            if ((newCapacity <<= 1) >= 0) continue;
            throw new IllegalStateException();
        } while (neededSpace > newCapacity);
        ByteBuffer[] newArray = new ByteBuffer[newCapacity];
        System.arraycopy((Object)array, (int)0, (Object)newArray, (int)0, (int)size);
        return newArray;
    }

    public int nioBufferCount() {
        return this.nioBufferCount;
    }

    public long nioBufferSize() {
        return this.nioBufferSize;
    }

    public boolean isWritable() {
        if (this.unwritable != 0) return false;
        return true;
    }

    public boolean getUserDefinedWritability(int index) {
        if ((this.unwritable & ChannelOutboundBuffer.writabilityMask((int)index)) != 0) return false;
        return true;
    }

    public void setUserDefinedWritability(int index, boolean writable) {
        if (writable) {
            this.setUserDefinedWritability((int)index);
            return;
        }
        this.clearUserDefinedWritability((int)index);
    }

    private void setUserDefinedWritability(int index) {
        int newValue;
        int oldValue;
        int mask = ~ChannelOutboundBuffer.writabilityMask((int)index);
        while (!UNWRITABLE_UPDATER.compareAndSet((ChannelOutboundBuffer)this, (int)(oldValue = this.unwritable), (int)(newValue = oldValue & mask))) {
        }
        if (oldValue == 0) return;
        if (newValue != 0) return;
        this.fireChannelWritabilityChanged((boolean)true);
    }

    private void clearUserDefinedWritability(int index) {
        int newValue;
        int oldValue;
        int mask = ChannelOutboundBuffer.writabilityMask((int)index);
        while (!UNWRITABLE_UPDATER.compareAndSet((ChannelOutboundBuffer)this, (int)(oldValue = this.unwritable), (int)(newValue = oldValue | mask))) {
        }
        if (oldValue != 0) return;
        if (newValue == 0) return;
        this.fireChannelWritabilityChanged((boolean)true);
    }

    private static int writabilityMask(int index) {
        if (index < 1) throw new IllegalArgumentException((String)("index: " + index + " (expected: 1~31)"));
        if (index <= 31) return 1 << index;
        throw new IllegalArgumentException((String)("index: " + index + " (expected: 1~31)"));
    }

    private void setWritable(boolean invokeLater) {
        int oldValue;
        int newValue;
        while (!UNWRITABLE_UPDATER.compareAndSet((ChannelOutboundBuffer)this, (int)(oldValue = this.unwritable), (int)(newValue = oldValue & -2))) {
        }
        if (oldValue == 0) return;
        if (newValue != 0) return;
        this.fireChannelWritabilityChanged((boolean)invokeLater);
    }

    private void setUnwritable(boolean invokeLater) {
        int oldValue;
        int newValue;
        while (!UNWRITABLE_UPDATER.compareAndSet((ChannelOutboundBuffer)this, (int)(oldValue = this.unwritable), (int)(newValue = oldValue | 1))) {
        }
        if (oldValue != 0) return;
        if (newValue == 0) return;
        this.fireChannelWritabilityChanged((boolean)invokeLater);
    }

    private void fireChannelWritabilityChanged(boolean invokeLater) {
        ChannelPipeline pipeline = this.channel.pipeline();
        if (!invokeLater) {
            pipeline.fireChannelWritabilityChanged();
            return;
        }
        Runnable task = this.fireChannelWritabilityChangedTask;
        if (task == null) {
            this.fireChannelWritabilityChangedTask = task = new Runnable((ChannelOutboundBuffer)this, (ChannelPipeline)pipeline){
                final /* synthetic */ ChannelPipeline val$pipeline;
                final /* synthetic */ ChannelOutboundBuffer this$0;
                {
                    this.this$0 = this$0;
                    this.val$pipeline = channelPipeline;
                }

                public void run() {
                    this.val$pipeline.fireChannelWritabilityChanged();
                }
            };
        }
        this.channel.eventLoop().execute((Runnable)task);
    }

    public int size() {
        return this.flushed;
    }

    public boolean isEmpty() {
        if (this.flushed != 0) return false;
        return true;
    }

    void failFlushed(Throwable cause, boolean notify) {
        if (this.inFail) {
            return;
        }
        try {
            this.inFail = true;
            while (this.remove0((Throwable)cause, (boolean)notify)) {
            }
            return;
        }
        finally {
            this.inFail = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close(Throwable cause, boolean allowChannelOpen) {
        if (this.inFail) {
            this.channel.eventLoop().execute((Runnable)new Runnable((ChannelOutboundBuffer)this, (Throwable)cause, (boolean)allowChannelOpen){
                final /* synthetic */ Throwable val$cause;
                final /* synthetic */ boolean val$allowChannelOpen;
                final /* synthetic */ ChannelOutboundBuffer this$0;
                {
                    this.this$0 = this$0;
                    this.val$cause = throwable;
                    this.val$allowChannelOpen = bl;
                }

                public void run() {
                    this.this$0.close((Throwable)this.val$cause, (boolean)this.val$allowChannelOpen);
                }
            });
            return;
        }
        this.inFail = true;
        if (!allowChannelOpen && this.channel.isOpen()) {
            throw new IllegalStateException((String)"close() must be invoked after the channel is closed.");
        }
        if (!this.isEmpty()) {
            throw new IllegalStateException((String)"close() must be invoked after all flushed writes are handled.");
        }
        try {
            for (Entry e = this.unflushedEntry; e != null; e = e.recycleAndGetNext()) {
                int size = e.pendingSize;
                TOTAL_PENDING_SIZE_UPDATER.addAndGet((ChannelOutboundBuffer)this, (long)((long)(-size)));
                if (e.cancelled) continue;
                ReferenceCountUtil.safeRelease((Object)e.msg);
                ChannelOutboundBuffer.safeFail((ChannelPromise)e.promise, (Throwable)cause);
            }
        }
        finally {
            this.inFail = false;
        }
        this.clearNioBuffers();
    }

    void close(ClosedChannelException cause) {
        this.close((Throwable)cause, (boolean)false);
    }

    private static void safeSuccess(ChannelPromise promise) {
        PromiseNotificationUtil.trySuccess(promise, null, (InternalLogger)(promise instanceof VoidChannelPromise ? null : logger));
    }

    private static void safeFail(ChannelPromise promise, Throwable cause) {
        PromiseNotificationUtil.tryFailure(promise, (Throwable)cause, (InternalLogger)(promise instanceof VoidChannelPromise ? null : logger));
    }

    @Deprecated
    public void recycle() {
    }

    public long totalPendingWriteBytes() {
        return this.totalPendingSize;
    }

    public long bytesBeforeUnwritable() {
        long bytes = (long)this.channel.config().getWriteBufferHighWaterMark() - this.totalPendingSize;
        if (bytes <= 0L) return 0L;
        if (!this.isWritable()) return 0L;
        long l = bytes;
        return l;
    }

    public long bytesBeforeWritable() {
        long bytes = this.totalPendingSize - (long)this.channel.config().getWriteBufferLowWaterMark();
        if (bytes <= 0L) return 0L;
        if (this.isWritable()) {
            return 0L;
        }
        long l = bytes;
        return l;
    }

    public void forEachFlushedMessage(MessageProcessor processor) throws Exception {
        if (processor == null) {
            throw new NullPointerException((String)"processor");
        }
        Entry entry = this.flushedEntry;
        if (entry == null) {
            return;
        }
        do {
            if (entry.cancelled || processor.processMessage((Object)entry.msg)) continue;
            return;
        } while (this.isFlushedEntry((Entry)(entry = entry.next)));
    }

    private boolean isFlushedEntry(Entry e) {
        if (e == null) return false;
        if (e == this.unflushedEntry) return false;
        return true;
    }
}

