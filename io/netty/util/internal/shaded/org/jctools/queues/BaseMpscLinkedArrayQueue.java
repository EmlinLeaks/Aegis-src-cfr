/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueColdProducerFields;
import io.netty.util.internal.shaded.org.jctools.queues.CircularArrayOffsetCalculator;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.util.Iterator;

public abstract class BaseMpscLinkedArrayQueue<E>
extends BaseMpscLinkedArrayQueueColdProducerFields<E>
implements MessagePassingQueue<E>,
QueueProgressIndicators {
    private static final Object JUMP = new Object();
    private static final int CONTINUE_TO_P_INDEX_CAS = 0;
    private static final int RETRY = 1;
    private static final int QUEUE_FULL = 2;
    private static final int QUEUE_RESIZE = 3;

    public BaseMpscLinkedArrayQueue(int initialCapacity) {
        RangeUtil.checkGreaterThanOrEqual((int)initialCapacity, (int)2, (String)"initialCapacity");
        int p2capacity = Pow2.roundToPowerOfTwo((int)initialCapacity);
        long mask = (long)(p2capacity - 1 << 1);
        E[] buffer = CircularArrayOffsetCalculator.allocate((int)(p2capacity + 1));
        this.producerBuffer = buffer;
        this.producerMask = mask;
        this.consumerBuffer = buffer;
        this.consumerMask = mask;
        this.soProducerLimit((long)mask);
    }

    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size() {
        long before;
        long currentProducerIndex;
        long after = this.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = this.lvProducerIndex();
        } while (before != (after = this.lvConsumerIndex()));
        long size = currentProducerIndex - after >> 1;
        if (size <= Integer.MAX_VALUE) return (int)size;
        return Integer.MAX_VALUE;
    }

    @Override
    public final boolean isEmpty() {
        if (this.lvConsumerIndex() != this.lvProducerIndex()) return false;
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    @Override
    public boolean offer(E e) {
        long pIndex;
        long mask;
        Object[] buffer;
        if (null == e) {
            throw new NullPointerException();
        }
        block6 : do {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            if (producerLimit <= pIndex) {
                int result = this.offerSlowPath((long)mask, (long)pIndex, (long)producerLimit);
                switch (result) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        continue block6;
                    }
                    case 2: {
                        return false;
                    }
                    case 3: {
                        this.resize((long)mask, buffer, (long)pIndex, e);
                        return true;
                    }
                }
            }
            if (this.casProducerIndex((long)pIndex, (long)(pIndex + 2L))) break;
        } while (true);
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)pIndex, (long)mask);
        UnsafeRefArrayAccess.soElement(buffer, (long)offset, e);
        return true;
    }

    @Override
    public E poll() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        if (e == null) {
            if (index == this.lvProducerIndex()) return (E)null;
            while ((e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset)) == null) {
            }
        }
        if (e == JUMP) {
            Object[] nextBuffer = this.getNextBuffer(buffer, (long)mask);
            return (E)this.newBufferPoll(nextBuffer, (long)index);
        }
        UnsafeRefArrayAccess.soElement(buffer, (long)offset, null);
        this.soConsumerIndex((long)(index + 2L));
        return (E)e;
    }

    @Override
    public E peek() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        if (e == null && index != this.lvProducerIndex()) {
            while ((e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset)) == null) {
            }
        }
        if (e != JUMP) return (E)e;
        return (E)this.newBufferPeek(this.getNextBuffer(buffer, (long)mask), (long)index);
    }

    private int offerSlowPath(long mask, long pIndex, long producerLimit) {
        long bufferCapacity;
        long cIndex = this.lvConsumerIndex();
        if (cIndex + (bufferCapacity = this.getCurrentBufferCapacity((long)mask)) > pIndex) {
            if (this.casProducerLimit((long)producerLimit, (long)(cIndex + bufferCapacity))) return 0;
            return 1;
        }
        if (this.availableInQueue((long)pIndex, (long)cIndex) <= 0L) {
            return 2;
        }
        if (!this.casProducerIndex((long)pIndex, (long)(pIndex + 1L))) return 1;
        return 3;
    }

    protected abstract long availableInQueue(long var1, long var3);

    private E[] getNextBuffer(E[] buffer, long mask) {
        long offset = this.nextArrayOffset((long)mask);
        Object[] nextBuffer = (Object[])UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        UnsafeRefArrayAccess.soElement(buffer, (long)offset, null);
        return nextBuffer;
    }

    private long nextArrayOffset(long mask) {
        return LinkedArrayQueueUtil.modifiedCalcElementOffset((long)(mask + 2L), (long)Long.MAX_VALUE);
    }

    private E newBufferPoll(E[] nextBuffer, long index) {
        long offset = this.newBufferAndOffset(nextBuffer, (long)index);
        E n = UnsafeRefArrayAccess.lvElement(nextBuffer, (long)offset);
        if (n == null) {
            throw new IllegalStateException((String)"new buffer must have at least one element");
        }
        UnsafeRefArrayAccess.soElement(nextBuffer, (long)offset, null);
        this.soConsumerIndex((long)(index + 2L));
        return (E)n;
    }

    private E newBufferPeek(E[] nextBuffer, long index) {
        long offset = this.newBufferAndOffset(nextBuffer, (long)index);
        E n = UnsafeRefArrayAccess.lvElement(nextBuffer, (long)offset);
        if (null != n) return (E)n;
        throw new IllegalStateException((String)"new buffer must have at least one element");
    }

    private long newBufferAndOffset(E[] nextBuffer, long index) {
        this.consumerBuffer = nextBuffer;
        this.consumerMask = (long)(LinkedArrayQueueUtil.length((Object[])nextBuffer) - 2 << 1);
        return LinkedArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)this.consumerMask);
    }

    @Override
    public long currentProducerIndex() {
        return this.lvProducerIndex() / 2L;
    }

    @Override
    public long currentConsumerIndex() {
        return this.lvConsumerIndex() / 2L;
    }

    @Override
    public abstract int capacity();

    @Override
    public boolean relaxedOffer(E e) {
        return this.offer(e);
    }

    @Override
    public E relaxedPoll() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        if (e == null) {
            return (E)null;
        }
        if (e == JUMP) {
            Object[] nextBuffer = this.getNextBuffer(buffer, (long)mask);
            return (E)this.newBufferPoll(nextBuffer, (long)index);
        }
        UnsafeRefArrayAccess.soElement(buffer, (long)offset, null);
        this.soConsumerIndex((long)(index + 2L));
        return (E)e;
    }

    @Override
    public E relaxedPeek() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        if (e != JUMP) return (E)e;
        return (E)this.newBufferPeek(this.getNextBuffer(buffer, (long)mask), (long)index);
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s) {
        int filled;
        long result = 0L;
        int capacity = this.capacity();
        do {
            if ((filled = this.fill(s, (int)PortableJvmInfo.RECOMENDED_OFFER_BATCH)) != 0) continue;
            return (int)result;
        } while ((result += (long)filled) <= (long)capacity);
        return (int)result;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int batchSize) {
        Object[] buffer;
        long pIndex;
        long batchIndex;
        long mask;
        block5 : do {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            batchIndex = Math.min((long)producerLimit, (long)(pIndex + (long)(2 * batchSize)));
            if (pIndex >= producerLimit || producerLimit < batchIndex) {
                int result = this.offerSlowPath((long)mask, (long)pIndex, (long)producerLimit);
                switch (result) {
                    case 0: 
                    case 1: {
                        continue block5;
                    }
                    case 2: {
                        return 0;
                    }
                    case 3: {
                        this.resize((long)mask, buffer, (long)pIndex, s.get());
                        return 1;
                    }
                }
            }
            if (this.casProducerIndex((long)pIndex, (long)batchIndex)) break;
        } while (true);
        int claimedSlots = (int)((batchIndex - pIndex) / 2L);
        int i = 0;
        while (i < claimedSlots) {
            long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)(pIndex + (long)(2 * i)), (long)mask);
            UnsafeRefArrayAccess.soElement(buffer, (long)offset, s.get());
            ++i;
        }
        return claimedSlots;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        block0 : do lbl-1000: // 3 sources:
        {
            if (exit.keepRunning() == false) return;
            if (this.fill(s, (int)PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0) ** GOTO lbl-1000
            idleCounter = 0;
            do {
                if (!exit.keepRunning() || this.fill(s, (int)PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0) continue block0;
                idleCounter = w.idle((int)idleCounter);
            } while (true);
            break;
        } while (true);
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        return this.drain(c, (int)this.capacity());
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
        int i = 0;
        while (i < limit) {
            E m = this.relaxedPoll();
            if (m == null) return i;
            c.accept(m);
            ++i;
        }
        return i;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            E e = this.relaxedPoll();
            if (e == null) {
                idleCounter = w.idle((int)idleCounter);
                continue;
            }
            idleCounter = 0;
            c.accept(e);
        }
    }

    private void resize(long oldMask, E[] oldBuffer, long pIndex, E e) {
        int newBufferLength = this.getNextBufferSize(oldBuffer);
        E[] newBuffer = CircularArrayOffsetCalculator.allocate((int)newBufferLength);
        this.producerBuffer = newBuffer;
        int newMask = newBufferLength - 2 << 1;
        this.producerMask = (long)newMask;
        long offsetInOld = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)pIndex, (long)oldMask);
        long offsetInNew = LinkedArrayQueueUtil.modifiedCalcElementOffset((long)pIndex, (long)((long)newMask));
        UnsafeRefArrayAccess.soElement(newBuffer, (long)offsetInNew, e);
        UnsafeRefArrayAccess.soElement(oldBuffer, (long)this.nextArrayOffset((long)oldMask), newBuffer);
        long cIndex = this.lvConsumerIndex();
        long availableInQueue = this.availableInQueue((long)pIndex, (long)cIndex);
        RangeUtil.checkPositive((long)availableInQueue, (String)"availableInQueue");
        this.soProducerLimit((long)(pIndex + Math.min((long)((long)newMask), (long)availableInQueue)));
        this.soProducerIndex((long)(pIndex + 2L));
        UnsafeRefArrayAccess.soElement(oldBuffer, (long)offsetInOld, JUMP);
    }

    protected abstract int getNextBufferSize(E[] var1);

    protected abstract long getCurrentBufferCapacity(long var1);
}

