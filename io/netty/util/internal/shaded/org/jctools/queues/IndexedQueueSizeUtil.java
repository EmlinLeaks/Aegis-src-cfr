/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.IndexedQueueSizeUtil;

public final class IndexedQueueSizeUtil {
    public static int size(IndexedQueue iq) {
        long before;
        long currentProducerIndex;
        long after = iq.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = iq.lvProducerIndex();
        } while (before != (after = iq.lvConsumerIndex()));
        long size = currentProducerIndex - after;
        if (size <= Integer.MAX_VALUE) return (int)size;
        return Integer.MAX_VALUE;
    }

    public static boolean isEmpty(IndexedQueue iq) {
        if (iq.lvConsumerIndex() != iq.lvProducerIndex()) return false;
        return true;
    }
}

