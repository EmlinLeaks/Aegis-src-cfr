/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.BaseMpscLinkedAtomicArrayQueueColdProducerFields;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedAtomicArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class BaseMpscLinkedAtomicArrayQueue<E>
extends BaseMpscLinkedAtomicArrayQueueColdProducerFields<E>
implements MessagePassingQueue<E>,
QueueProgressIndicators {
    private static final Object JUMP = new Object();

    public BaseMpscLinkedAtomicArrayQueue(int initialCapacity) {
        AtomicReferenceArray<E> buffer;
        RangeUtil.checkGreaterThanOrEqual((int)initialCapacity, (int)2, (String)"initialCapacity");
        int p2capacity = Pow2.roundToPowerOfTwo((int)initialCapacity);
        long mask = (long)(p2capacity - 1 << 1);
        this.producerBuffer = buffer = LinkedAtomicArrayQueueUtil.allocate((int)(p2capacity + 1));
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
        AtomicReferenceArray buffer;
        long mask;
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
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)pIndex, (long)mask);
        LinkedAtomicArrayQueueUtil.soElement((AtomicReferenceArray)buffer, (int)offset, e);
        return true;
    }

    @Override
    public E poll() {
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        E e = LinkedAtomicArrayQueueUtil.lvElement(buffer, (int)offset);
        if (e == null) {
            if (index == this.lvProducerIndex()) return (E)null;
            while ((e = LinkedAtomicArrayQueueUtil.lvElement(buffer, (int)offset)) == null) {
            }
        }
        if (e == JUMP) {
            AtomicReferenceArray<E> nextBuffer = this.getNextBuffer(buffer, (long)mask);
            return (E)this.newBufferPoll(nextBuffer, (long)index);
        }
        LinkedAtomicArrayQueueUtil.soElement((AtomicReferenceArray)buffer, (int)offset, null);
        this.soConsumerIndex((long)(index + 2L));
        return (E)e;
    }

    @Override
    public E peek() {
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        E e = LinkedAtomicArrayQueueUtil.lvElement(buffer, (int)offset);
        if (e == null && index != this.lvProducerIndex()) {
            while ((e = LinkedAtomicArrayQueueUtil.lvElement(buffer, (int)offset)) == null) {
            }
        }
        if (e != JUMP) return (E)e;
        return (E)this.newBufferPeek(this.getNextBuffer(buffer, (long)mask), (long)index);
    }

    private int offerSlowPath(long mask, long pIndex, long producerLimit) {
        long cIndex = this.lvConsumerIndex();
        long bufferCapacity = this.getCurrentBufferCapacity((long)mask);
        int result = 0;
        if (cIndex + bufferCapacity > pIndex) {
            if (this.casProducerLimit((long)producerLimit, (long)(cIndex + bufferCapacity))) return result;
            return 1;
        }
        if (this.availableInQueue((long)pIndex, (long)cIndex) <= 0L) {
            return 2;
        }
        if (!this.casProducerIndex((long)pIndex, (long)(pIndex + 1L))) return 1;
        return 3;
    }

    protected abstract long availableInQueue(long var1, long var3);

    private AtomicReferenceArray<E> getNextBuffer(AtomicReferenceArray<E> buffer, long mask) {
        int offset = this.nextArrayOffset((long)mask);
        AtomicReferenceArray nextBuffer = (AtomicReferenceArray)LinkedAtomicArrayQueueUtil.lvElement(buffer, (int)offset);
        LinkedAtomicArrayQueueUtil.soElement(buffer, (int)offset, null);
        return nextBuffer;
    }

    private int nextArrayOffset(long mask) {
        return LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)(mask + 2L), (long)Long.MAX_VALUE);
    }

    private E newBufferPoll(AtomicReferenceArray<E> nextBuffer, long index) {
        int offset = this.newBufferAndOffset(nextBuffer, (long)index);
        E n = LinkedAtomicArrayQueueUtil.lvElement(nextBuffer, (int)offset);
        if (n == null) {
            throw new IllegalStateException((String)"new buffer must have at least one element");
        }
        LinkedAtomicArrayQueueUtil.soElement(nextBuffer, (int)offset, null);
        this.soConsumerIndex((long)(index + 2L));
        return (E)n;
    }

    private E newBufferPeek(AtomicReferenceArray<E> nextBuffer, long index) {
        int offset = this.newBufferAndOffset(nextBuffer, (long)index);
        E n = LinkedAtomicArrayQueueUtil.lvElement(nextBuffer, (int)offset);
        if (null != n) return (E)n;
        throw new IllegalStateException((String)"new buffer must have at least one element");
    }

    private int newBufferAndOffset(AtomicReferenceArray<E> nextBuffer, long index) {
        this.consumerBuffer = nextBuffer;
        this.consumerMask = (long)(LinkedAtomicArrayQueueUtil.length(nextBuffer) - 2 << 1);
        return LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)this.consumerMask);
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
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        E e = LinkedAtomicArrayQueueUtil.lvElement(buffer, (int)offset);
        if (e == null) {
            return (E)null;
        }
        if (e == JUMP) {
            AtomicReferenceArray<E> nextBuffer = this.getNextBuffer(buffer, (long)mask);
            return (E)this.newBufferPoll(nextBuffer, (long)index);
        }
        LinkedAtomicArrayQueueUtil.soElement((AtomicReferenceArray)buffer, (int)offset, null);
        this.soConsumerIndex((long)(index + 2L));
        return (E)e;
    }

    @Override
    public E relaxedPeek() {
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)index, (long)mask);
        E e = LinkedAtomicArrayQueueUtil.lvElement(buffer, (int)offset);
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
        long pIndex;
        AtomicReferenceArray buffer;
        long batchIndex;
        long mask;
        block5 : do {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            batchIndex = Math.min((long)producerLimit, (long)(pIndex + (long)(2 * batchSize)));
            if (pIndex == producerLimit || producerLimit < batchIndex) {
                int result = this.offerSlowPath((long)mask, (long)pIndex, (long)producerLimit);
                switch (result) {
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
        i = 0;
        while (i < claimedSlots) {
            int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)(pIndex + (long)(2 * i)), (long)mask);
            LinkedAtomicArrayQueueUtil.soElement((AtomicReferenceArray)buffer, (int)offset, s.get());
            ++i;
        }
        return claimedSlots;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        block0 : while (exit.keepRunning()) {
            while (this.fill(s, (int)PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0 && exit.keepRunning()) {
            }
            int idleCounter = 0;
            do {
                if (!exit.keepRunning() || this.fill(s, (int)PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0) continue block0;
                idleCounter = w.idle((int)idleCounter);
            } while (true);
            break;
        }
        return;
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

    private void resize(long oldMask, AtomicReferenceArray<E> oldBuffer, long pIndex, E e) {
        AtomicReferenceArray<E> newBuffer;
        int newBufferLength = this.getNextBufferSize(oldBuffer);
        this.producerBuffer = newBuffer = LinkedAtomicArrayQueueUtil.allocate((int)newBufferLength);
        int newMask = newBufferLength - 2 << 1;
        this.producerMask = (long)newMask;
        int offsetInOld = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)pIndex, (long)oldMask);
        int offsetInNew = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset((long)pIndex, (long)((long)newMask));
        LinkedAtomicArrayQueueUtil.soElement(newBuffer, (int)offsetInNew, e);
        LinkedAtomicArrayQueueUtil.soElement(oldBuffer, (int)this.nextArrayOffset((long)oldMask), newBuffer);
        long cIndex = this.lvConsumerIndex();
        long availableInQueue = this.availableInQueue((long)pIndex, (long)cIndex);
        RangeUtil.checkPositive((long)availableInQueue, (String)"availableInQueue");
        this.soProducerLimit((long)(pIndex + Math.min((long)((long)newMask), (long)availableInQueue)));
        this.soProducerIndex((long)(pIndex + 2L));
        LinkedAtomicArrayQueueUtil.soElement(oldBuffer, (int)offsetInOld, (Object)JUMP);
    }

    protected abstract int getNextBufferSize(AtomicReferenceArray<E> var1);

    protected abstract long getCurrentBufferCapacity(long var1);
}

