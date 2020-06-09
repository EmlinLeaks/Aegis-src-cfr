/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueMidPad;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class MpscArrayQueueProducerLimitField<E>
extends MpscArrayQueueMidPad<E> {
    private static final long P_LIMIT_OFFSET;
    private volatile long producerLimit;

    public MpscArrayQueueProducerLimitField(int capacity) {
        super((int)capacity);
        this.producerLimit = (long)capacity;
    }

    protected final long lvProducerLimit() {
        return this.producerLimit;
    }

    protected final void soProducerLimit(long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong((Object)this, (long)P_LIMIT_OFFSET, (long)newValue);
    }

    static {
        try {
            P_LIMIT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset((Field)MpscArrayQueueProducerLimitField.class.getDeclaredField((String)"producerLimit"));
            return;
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

