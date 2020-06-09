/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;

abstract class MpscChunkedArrayQueueColdProducerFields<E>
extends BaseMpscLinkedArrayQueue<E> {
    protected final long maxQueueCapacity;

    public MpscChunkedArrayQueueColdProducerFields(int initialCapacity, int maxCapacity) {
        super((int)initialCapacity);
        RangeUtil.checkGreaterThanOrEqual((int)maxCapacity, (int)4, (String)"maxCapacity");
        RangeUtil.checkLessThan((int)Pow2.roundToPowerOfTwo((int)initialCapacity), (int)Pow2.roundToPowerOfTwo((int)maxCapacity), (String)"initialCapacity");
        this.maxQueueCapacity = (long)Pow2.roundToPowerOfTwo((int)maxCapacity) << 1;
    }
}

