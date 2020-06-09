/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.group;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class CombinedIterator<E>
implements Iterator<E> {
    private final Iterator<E> i1;
    private final Iterator<E> i2;
    private Iterator<E> currentIterator;

    CombinedIterator(Iterator<E> i1, Iterator<E> i2) {
        if (i1 == null) {
            throw new NullPointerException((String)"i1");
        }
        if (i2 == null) {
            throw new NullPointerException((String)"i2");
        }
        this.i1 = i1;
        this.i2 = i2;
        this.currentIterator = i1;
    }

    @Override
    public boolean hasNext() {
        while (!this.currentIterator.hasNext()) {
            if (this.currentIterator != this.i1) return false;
            this.currentIterator = this.i2;
        }
        return true;
    }

    @Override
    public E next() {
        do {
            try {
                return (E)this.currentIterator.next();
            }
            catch (NoSuchElementException e) {
                if (this.currentIterator != this.i1) throw e;
                this.currentIterator = this.i2;
                continue;
            }
            break;
        } while (true);
    }

    @Override
    public void remove() {
        this.currentIterator.remove();
    }
}

