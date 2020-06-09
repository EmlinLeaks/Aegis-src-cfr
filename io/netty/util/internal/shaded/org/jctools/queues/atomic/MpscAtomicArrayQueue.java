/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueueL3Pad;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscAtomicArrayQueue<E>
extends MpscAtomicArrayQueueL3Pad<E> {
    public MpscAtomicArrayQueue(int capacity) {
        super((int)capacity);
    }

    public boolean offerIfBelowThreshold(E e, int threshold) {
        long pIndex;
        if (null == e) {
            throw new NullPointerException();
        }
        int mask = this.mask;
        long capacity = (long)(mask + 1);
        long producerLimit = this.lvProducerLimit();
        do {
            long available;
            long size;
            if ((size = capacity - (available = producerLimit - (pIndex = this.lvProducerIndex()))) < (long)threshold) continue;
            long cIndex = this.lvConsumerIndex();
            size = pIndex - cIndex;
            if (size >= (long)threshold) {
                return false;
            }
            producerLimit = cIndex + capacity;
            this.soProducerLimit((long)producerLimit);
        } while (!this.casProducerIndex((long)pIndex, (long)(pIndex + 1L)));
        int offset = this.calcElementOffset((long)pIndex, (int)mask);
        MpscAtomicArrayQueue.soElement(this.buffer, (int)offset, e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        long pIndex;
        if (null == e) {
            throw new NullPointerException();
        }
        int mask = this.mask;
        long producerLimit = this.lvProducerLimit();
        do {
            if ((pIndex = this.lvProducerIndex()) < producerLimit) continue;
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + (long)mask + 1L;
            if (pIndex >= producerLimit) {
                return false;
            }
            this.soProducerLimit((long)producerLimit);
        } while (!this.casProducerIndex((long)pIndex, (long)(pIndex + 1L)));
        int offset = this.calcElementOffset((long)pIndex, (int)mask);
        MpscAtomicArrayQueue.soElement(this.buffer, (int)offset, e);
        return true;
    }

    public final int failFastOffer(E e) {
        long producerLimit;
        if (null == e) {
            throw new NullPointerException();
        }
        int mask = this.mask;
        long capacity = (long)(mask + 1);
        long pIndex = this.lvProducerIndex();
        if (pIndex >= (producerLimit = this.lvProducerLimit())) {
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            if (pIndex >= producerLimit) {
                return 1;
            }
            this.soProducerLimit((long)producerLimit);
        }
        if (!this.casProducerIndex((long)pIndex, (long)(pIndex + 1L))) {
            return -1;
        }
        int offset = this.calcElementOffset((long)pIndex, (int)mask);
        MpscAtomicArrayQueue.soElement(this.buffer, (int)offset, e);
        return 0;
    }

    @Override
    public E poll() {
        AtomicReferenceArray buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        int offset = this.calcElementOffset((long)cIndex);
        E e = MpscAtomicArrayQueue.lvElement(buffer, (int)offset);
        if (null == e) {
            if (cIndex == this.lvProducerIndex()) return (E)null;
            while ((e = MpscAtomicArrayQueue.lvElement(buffer, (int)offset)) == null) {
            }
        }
        MpscAtomicArrayQueue.spElement(buffer, (int)offset, null);
        this.soConsumerIndex((long)(cIndex + 1L));
        return (E)e;
    }

    @Override
    public E peek() {
        AtomicReferenceArray buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        int offset = this.calcElementOffset((long)cIndex);
        E e = MpscAtomicArrayQueue.lvElement(buffer, (int)offset);
        if (null != e) return (E)e;
        if (cIndex == this.lvProducerIndex()) return (E)null;
        while ((e = MpscAtomicArrayQueue.lvElement(buffer, (int)offset)) == null) {
        }
        return (E)((E)e);
    }

    @Override
    public boolean relaxedOffer(E e) {
        return this.offer(e);
    }

    @Override
    public E relaxedPoll() {
        AtomicReferenceArray buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        int offset = this.calcElementOffset((long)cIndex);
        E e = MpscAtomicArrayQueue.lvElement(buffer, (int)offset);
        if (null == e) {
            return (E)null;
        }
        MpscAtomicArrayQueue.spElement(buffer, (int)offset, null);
        this.soConsumerIndex((long)(cIndex + 1L));
        return (E)e;
    }

    @Override
    public E relaxedPeek() {
        AtomicReferenceArray buffer = this.buffer;
        int mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        return (E)MpscAtomicArrayQueue.lvElement(buffer, (int)this.calcElementOffset((long)cIndex, (int)mask));
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        return this.drain(c, (int)this.capacity());
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
    public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
        AtomicReferenceArray buffer = this.buffer;
        int mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        int i = 0;
        while (i < limit) {
            long index = cIndex + (long)i;
            int offset = this.calcElementOffset((long)index, (int)mask);
            E e = MpscAtomicArrayQueue.lvElement(buffer, (int)offset);
            if (null == e) {
                return i;
            }
            MpscAtomicArrayQueue.spElement(buffer, (int)offset, null);
            this.soConsumerIndex((long)(index + 1L));
            c.accept(e);
            ++i;
        }
        return limit;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
        long available;
        long pIndex;
        int mask = this.mask;
        long capacity = (long)(mask + 1);
        long producerLimit = this.lvProducerLimit();
        int actualLimit = 0;
        do {
            if ((available = producerLimit - (pIndex = this.lvProducerIndex())) > 0L) continue;
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            available = producerLimit - pIndex;
            if (available <= 0L) {
                return 0;
            }
            this.soProducerLimit((long)producerLimit);
        } while (!this.casProducerIndex((long)pIndex, (long)(pIndex + (long)(actualLimit = Math.min((int)((int)available), (int)limit)))));
        AtomicReferenceArray buffer = this.buffer;
        int i = 0;
        while (i < actualLimit) {
            int offset = this.calcElementOffset((long)(pIndex + (long)i), (int)mask);
            MpscAtomicArrayQueue.soElement(buffer, (int)offset, s.get());
            ++i;
        }
        return actualLimit;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        AtomicReferenceArray buffer = this.buffer;
        int mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        int counter = 0;
        block0 : while (exit.keepRunning()) {
            int i = 0;
            do {
                if (i >= 4096) continue block0;
                int offset = this.calcElementOffset((long)cIndex, (int)mask);
                E e = MpscAtomicArrayQueue.lvElement(buffer, (int)offset);
                if (null == e) {
                    counter = w.idle((int)counter);
                } else {
                    counter = 0;
                    MpscAtomicArrayQueue.spElement(buffer, (int)offset, null);
                    this.soConsumerIndex((long)(++cIndex));
                    c.accept(e);
                }
                ++i;
            } while (true);
            break;
        }
        return;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            if (this.fill(s, (int)PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0) {
                idleCounter = w.idle((int)idleCounter);
                continue;
            }
            idleCounter = 0;
        }
    }

    @Deprecated
    public int weakOffer(E e) {
        return this.failFastOffer(e);
    }
}

