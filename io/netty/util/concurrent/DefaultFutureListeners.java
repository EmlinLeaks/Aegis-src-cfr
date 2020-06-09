/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import java.util.Arrays;

final class DefaultFutureListeners {
    private GenericFutureListener<? extends Future<?>>[] listeners = new GenericFutureListener[2];
    private int size;
    private int progressiveSize;

    DefaultFutureListeners(GenericFutureListener<? extends Future<?>> first, GenericFutureListener<? extends Future<?>> second) {
        this.listeners[0] = first;
        this.listeners[1] = second;
        this.size = 2;
        if (first instanceof GenericProgressiveFutureListener) {
            ++this.progressiveSize;
        }
        if (!(second instanceof GenericProgressiveFutureListener)) return;
        ++this.progressiveSize;
    }

    public void add(GenericFutureListener<? extends Future<?>> l) {
        int size = this.size;
        GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
        if (size == listeners.length) {
            this.listeners = listeners = Arrays.copyOf(listeners, (int)(size << 1));
        }
        listeners[size] = l;
        this.size = size + 1;
        if (!(l instanceof GenericProgressiveFutureListener)) return;
        ++this.progressiveSize;
    }

    public void remove(GenericFutureListener<? extends Future<?>> l) {
        GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
        int size = this.size;
        int i = 0;
        while (i < size) {
            if (listeners[i] == l) {
                int listenersToMove = size - i - 1;
                if (listenersToMove > 0) {
                    System.arraycopy(listeners, (int)(i + 1), listeners, (int)i, (int)listenersToMove);
                }
                listeners[--size] = null;
                this.size = size;
                if (!(l instanceof GenericProgressiveFutureListener)) return;
                --this.progressiveSize;
                return;
            }
            ++i;
        }
    }

    public GenericFutureListener<? extends Future<?>>[] listeners() {
        return this.listeners;
    }

    public int size() {
        return this.size;
    }

    public int progressiveSize() {
        return this.progressiveSize;
    }
}

