/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.AtomicReferenceArrayQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;

final class LinkedAtomicArrayQueueUtil {
    private LinkedAtomicArrayQueueUtil() {
    }

    public static <E> E lvElement(AtomicReferenceArray<E> buffer, int offset) {
        return (E)AtomicReferenceArrayQueue.lvElement(buffer, (int)offset);
    }

    public static <E> E lpElement(AtomicReferenceArray<E> buffer, int offset) {
        return (E)AtomicReferenceArrayQueue.lpElement(buffer, (int)offset);
    }

    public static <E> void spElement(AtomicReferenceArray<E> buffer, int offset, E value) {
        AtomicReferenceArrayQueue.spElement(buffer, (int)offset, value);
    }

    public static <E> void svElement(AtomicReferenceArray<E> buffer, int offset, E value) {
        AtomicReferenceArrayQueue.svElement(buffer, (int)offset, value);
    }

    static <E> void soElement(AtomicReferenceArray buffer, int offset, Object value) {
        buffer.lazySet((int)offset, value);
    }

    static int calcElementOffset(long index, long mask) {
        return (int)(index & mask);
    }

    static <E> AtomicReferenceArray<E> allocate(int capacity) {
        return new AtomicReferenceArray<E>((int)capacity);
    }

    static int length(AtomicReferenceArray<?> buf) {
        return buf.length();
    }

    static int modifiedCalcElementOffset(long index, long mask) {
        return (int)(index & mask) >> 1;
    }

    static int nextArrayOffset(AtomicReferenceArray<?> curr) {
        return LinkedAtomicArrayQueueUtil.length(curr) - 1;
    }
}

