/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueuePad1;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class BaseMpscLinkedArrayQueueProducerFields<E>
extends BaseMpscLinkedArrayQueuePad1<E> {
    private static final long P_INDEX_OFFSET;
    protected long producerIndex;

    BaseMpscLinkedArrayQueueProducerFields() {
    }

    @Override
    public final long lvProducerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile((Object)this, (long)P_INDEX_OFFSET);
    }

    final void soProducerIndex(long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong((Object)this, (long)P_INDEX_OFFSET, (long)newValue);
    }

    final boolean casProducerIndex(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong((Object)this, (long)P_INDEX_OFFSET, (long)expect, (long)newValue);
    }

    static {
        try {
            Field iField = BaseMpscLinkedArrayQueueProducerFields.class.getDeclaredField((String)"producerIndex");
            P_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset((Field)iField);
            return;
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

