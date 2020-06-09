/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueuePad3;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

abstract class BaseMpscLinkedArrayQueueColdProducerFields<E>
extends BaseMpscLinkedArrayQueuePad3<E> {
    private static final long P_LIMIT_OFFSET;
    private volatile long producerLimit;
    protected long producerMask;
    protected E[] producerBuffer;

    BaseMpscLinkedArrayQueueColdProducerFields() {
    }

    final long lvProducerLimit() {
        return this.producerLimit;
    }

    final boolean casProducerLimit(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong((Object)this, (long)P_LIMIT_OFFSET, (long)expect, (long)newValue);
    }

    final void soProducerLimit(long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong((Object)this, (long)P_LIMIT_OFFSET, (long)newValue);
    }

    static {
        try {
            Field iField = BaseMpscLinkedArrayQueueColdProducerFields.class.getDeclaredField((String)"producerLimit");
            P_LIMIT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset((Field)iField);
            return;
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

