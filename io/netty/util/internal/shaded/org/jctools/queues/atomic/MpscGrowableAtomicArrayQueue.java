/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedAtomicArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscChunkedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscGrowableAtomicArrayQueue<E>
extends MpscChunkedAtomicArrayQueue<E> {
    public MpscGrowableAtomicArrayQueue(int maxCapacity) {
        super((int)Math.max((int)2, (int)Pow2.roundToPowerOfTwo((int)(maxCapacity / 8))), (int)maxCapacity);
    }

    public MpscGrowableAtomicArrayQueue(int initialCapacity, int maxCapacity) {
        super((int)initialCapacity, (int)maxCapacity);
    }

    @Override
    protected int getNextBufferSize(AtomicReferenceArray<E> buffer) {
        long maxSize = this.maxQueueCapacity / 2L;
        RangeUtil.checkLessThanOrEqual((int)LinkedAtomicArrayQueueUtil.length(buffer), (long)maxSize, (String)"buffer.length");
        int newSize = 2 * (LinkedAtomicArrayQueueUtil.length(buffer) - 1);
        return newSize + 1;
    }

    @Override
    protected long getCurrentBufferCapacity(long mask) {
        long l;
        if (mask + 2L == this.maxQueueCapacity) {
            l = this.maxQueueCapacity;
            return l;
        }
        l = mask;
        return l;
    }
}

