/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueL2Pad;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class MpscArrayQueueConsumerIndexField<E>
extends MpscArrayQueueL2Pad<E> {
    private static final long C_INDEX_OFFSET;
    protected long consumerIndex;

    public MpscArrayQueueConsumerIndexField(int capacity) {
        super((int)capacity);
    }

    protected final long lpConsumerIndex() {
        return this.consumerIndex;
    }

    @Override
    public final long lvConsumerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile((Object)this, (long)C_INDEX_OFFSET);
    }

    protected void soConsumerIndex(long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong((Object)this, (long)C_INDEX_OFFSET, (long)newValue);
    }

    static {
        try {
            C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset((Field)MpscArrayQueueConsumerIndexField.class.getDeclaredField((String)"consumerIndex"));
            return;
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

