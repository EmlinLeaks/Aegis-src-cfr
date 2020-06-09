/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

final class LinkedQueueNode<E> {
    private static final long NEXT_OFFSET;
    private E value;
    private volatile LinkedQueueNode<E> next;

    LinkedQueueNode() {
        this(null);
    }

    LinkedQueueNode(E val) {
        this.spValue(val);
    }

    public E getAndNullValue() {
        E temp = this.lpValue();
        this.spValue(null);
        return (E)temp;
    }

    public E lpValue() {
        return (E)this.value;
    }

    public void spValue(E newValue) {
        this.value = newValue;
    }

    public void soNext(LinkedQueueNode<E> n) {
        UnsafeAccess.UNSAFE.putOrderedObject((Object)this, (long)NEXT_OFFSET, n);
    }

    public LinkedQueueNode<E> lvNext() {
        return this.next;
    }

    static {
        try {
            NEXT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset((Field)LinkedQueueNode.class.getDeclaredField((String)"next"));
            return;
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException((Throwable)e);
        }
    }
}

