/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.BaseMpscLinkedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;

abstract class MpscChunkedAtomicArrayQueueColdProducerFields<E>
extends BaseMpscLinkedAtomicArrayQueue<E> {
    protected final long maxQueueCapacity;

    public MpscChunkedAtomicArrayQueueColdProducerFields(int initialCapacity, int maxCapacity) {
        super((int)initialCapacity);
        RangeUtil.checkGreaterThanOrEqual((int)maxCapacity, (int)4, (String)"maxCapacity");
        RangeUtil.checkLessThan((int)Pow2.roundToPowerOfTwo((int)initialCapacity), (int)Pow2.roundToPowerOfTwo((int)maxCapacity), (String)"initialCapacity");
        this.maxQueueCapacity = (long)Pow2.roundToPowerOfTwo((int)maxCapacity) << 1;
    }
}

