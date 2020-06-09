/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueL1Pad;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class MpscArrayQueueProducerIndexField<E>
extends MpscArrayQueueL1Pad<E> {
    private static final long P_INDEX_OFFSET;
    private volatile long producerIndex;

    public MpscArrayQueueProducerIndexField(int capacity) {
        super((int)capacity);
    }

    @Override
    public final long lvProducerIndex() {
        return this.producerIndex;
    }

    protected final boolean casProducerIndex(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong((Object)this, (long)P_INDEX_OFFSET, (long)expect, (long)newValue);
    }

    static {
        try {
            P_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset((Field)MpscArrayQueueProducerIndexField.class.getDeclaredField((String)"producerIndex"));
            return;
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

