/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableListIterator;
import java.util.NoSuchElementException;

@GwtCompatible
abstract class AbstractIndexedListIterator<E>
extends UnmodifiableListIterator<E> {
    private final int size;
    private int position;

    protected abstract E get(int var1);

    protected AbstractIndexedListIterator(int size) {
        this((int)size, (int)0);
    }

    protected AbstractIndexedListIterator(int size, int position) {
        Preconditions.checkPositionIndex((int)position, (int)size);
        this.size = size;
        this.position = position;
    }

    @Override
    public final boolean hasNext() {
        if (this.position >= this.size) return false;
        return true;
    }

    @Override
    public final E next() {
        if (this.hasNext()) return (E)this.get((int)this.position++);
        throw new NoSuchElementException();
    }

    @Override
    public final int nextIndex() {
        return this.position;
    }

    @Override
    public final boolean hasPrevious() {
        if (this.position <= 0) return false;
        return true;
    }

    @Override
    public final E previous() {
        if (this.hasPrevious()) return (E)this.get((int)(--this.position));
        throw new NoSuchElementException();
    }

    @Override
    public final int previousIndex() {
        return this.position - 1;
    }
}

