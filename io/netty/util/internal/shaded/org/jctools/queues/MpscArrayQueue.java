/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueL3Pad;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public class MpscArrayQueue<E>
extends MpscArrayQueueL3Pad<E> {
    public MpscArrayQueue(int capacity) {
        super((int)capacity);
    }

    public boolean offerIfBelowThreshold(E e, int threshold) {
        long pIndex;
        if (null == e) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long capacity = mask + 1L;
        long producerLimit = this.lvProducerLimit();
        do {
            long size;
            long available;
            if ((size = capacity - (available = producerLimit - (pIndex = this.lvProducerIndex()))) < (long)threshold) continue;
            long cIndex = this.lvConsumerIndex();
            size = pIndex - cIndex;
            if (size >= (long)threshold) {
                return false;
            }
            producerLimit = cIndex + capacity;
            this.soProducerLimit((long)producerLimit);
        } while (!this.casProducerIndex((long)pIndex, (long)(pIndex + 1L)));
        long offset = MpscArrayQueue.calcElementOffset((long)pIndex, (long)mask);
        UnsafeRefArrayAccess.soElement(this.buffer, (long)offset, e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        long pIndex;
        if (null == e) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long producerLimit = this.lvProducerLimit();
        do {
            if ((pIndex = this.lvProducerIndex()) < producerLimit) continue;
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + mask + 1L;
            if (pIndex >= producerLimit) {
                return false;
            }
            this.soProducerLimit((long)producerLimit);
        } while (!this.casProducerIndex((long)pIndex, (long)(pIndex + 1L)));
        long offset = MpscArrayQueue.calcElementOffset((long)pIndex, (long)mask);
        UnsafeRefArrayAccess.soElement(this.buffer, (long)offset, e);
        return true;
    }

    public final int failFastOffer(E e) {
        long producerLimit;
        if (null == e) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long capacity = mask + 1L;
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
        long offset = MpscArrayQueue.calcElementOffset((long)pIndex, (long)mask);
        UnsafeRefArrayAccess.soElement(this.buffer, (long)offset, e);
        return 0;
    }

    @Override
    public E poll() {
        Object[] buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        long offset = this.calcElementOffset((long)cIndex);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        if (null == e) {
            if (cIndex == this.lvProducerIndex()) return (E)null;
            while ((e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset)) == null) {
            }
        }
        UnsafeRefArrayAccess.spElement(buffer, (long)offset, null);
        this.soConsumerIndex((long)(cIndex + 1L));
        return (E)e;
    }

    @Override
    public E peek() {
        Object[] buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        long offset = this.calcElementOffset((long)cIndex);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        if (null != e) return (E)e;
        if (cIndex == this.lvProducerIndex()) return (E)null;
        while ((e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset)) == null) {
        }
        return (E)((E)e);
    }

    @Override
    public boolean relaxedOffer(E e) {
        return this.offer(e);
    }

    @Override
    public E relaxedPoll() {
        Object[] buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        long offset = this.calcElementOffset((long)cIndex);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
        if (null == e) {
            return (E)null;
        }
        UnsafeRefArrayAccess.spElement(buffer, (long)offset, null);
        this.soConsumerIndex((long)(cIndex + 1L));
        return (E)e;
    }

    @Override
    public E relaxedPeek() {
        Object[] buffer = this.buffer;
        long mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        return (E)UnsafeRefArrayAccess.lvElement(buffer, (long)MpscArrayQueue.calcElementOffset((long)cIndex, (long)mask));
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
        Object[] buffer = this.buffer;
        long mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        int i = 0;
        while (i < limit) {
            long index = cIndex + (long)i;
            long offset = MpscArrayQueue.calcElementOffset((long)index, (long)mask);
            Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
            if (null == e) {
                return i;
            }
            UnsafeRefArrayAccess.spElement(buffer, (long)offset, null);
            this.soConsumerIndex((long)(index + 1L));
            c.accept(e);
            ++i;
        }
        return limit;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
        long pIndex;
        long available;
        long mask = this.mask;
        long capacity = mask + 1L;
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
        Object[] buffer = this.buffer;
        int i = 0;
        while (i < actualLimit) {
            long offset = MpscArrayQueue.calcElementOffset((long)(pIndex + (long)i), (long)mask);
            UnsafeRefArrayAccess.soElement(buffer, (long)offset, s.get());
            ++i;
        }
        return actualLimit;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        Object[] buffer = this.buffer;
        long mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        int counter = 0;
        block0 : while (exit.keepRunning()) {
            int i = 0;
            do {
                if (i >= 4096) continue block0;
                long offset = MpscArrayQueue.calcElementOffset((long)cIndex, (long)mask);
                Object e = UnsafeRefArrayAccess.lvElement(buffer, (long)offset);
                if (null == e) {
                    counter = w.idle((int)counter);
                } else {
                    counter = 0;
                    UnsafeRefArrayAccess.spElement(buffer, (long)offset, null);
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
}

