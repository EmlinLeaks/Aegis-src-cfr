/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

@GwtCompatible
class ConsumingQueueIterator<T>
extends AbstractIterator<T> {
    private final Queue<T> queue;

    ConsumingQueueIterator(T ... elements) {
        this.queue = new ArrayDeque<T>((int)elements.length);
        Collections.addAll(this.queue, elements);
    }

    ConsumingQueueIterator(Queue<T> queue) {
        this.queue = Preconditions.checkNotNull(queue);
    }

    @Override
    public T computeNext() {
        T t;
        if (this.queue.isEmpty()) {
            t = this.endOfData();
            return (T)((T)t);
        }
        t = this.queue.remove();
        return (T)t;
    }
}

