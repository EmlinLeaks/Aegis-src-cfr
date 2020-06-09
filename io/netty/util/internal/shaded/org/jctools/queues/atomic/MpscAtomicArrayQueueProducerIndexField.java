/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueueL1Pad;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueProducerIndexField<E>
extends MpscAtomicArrayQueueL1Pad<E> {
    private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueProducerIndexField> P_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(MpscAtomicArrayQueueProducerIndexField.class, (String)"producerIndex");
    private volatile long producerIndex;

    public MpscAtomicArrayQueueProducerIndexField(int capacity) {
        super((int)capacity);
    }

    @Override
    public final long lvProducerIndex() {
        return this.producerIndex;
    }

    protected final boolean casProducerIndex(long expect, long newValue) {
        return P_INDEX_UPDATER.compareAndSet((MpscAtomicArrayQueueProducerIndexField)this, (long)expect, (long)newValue);
    }
}

