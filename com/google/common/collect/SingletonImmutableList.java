/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;

@GwtCompatible(serializable=true, emulated=true)
final class SingletonImmutableList<E>
extends ImmutableList<E> {
    final transient E element;

    SingletonImmutableList(E element) {
        this.element = Preconditions.checkNotNull(element);
    }

    @Override
    public E get(int index) {
        Preconditions.checkElementIndex((int)index, (int)1);
        return (E)this.element;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ImmutableList<E> subList(int fromIndex, int toIndex) {
        SingletonImmutableList singletonImmutableList;
        Preconditions.checkPositionIndexes((int)fromIndex, (int)toIndex, (int)1);
        if (fromIndex == toIndex) {
            singletonImmutableList = ImmutableList.of();
            return singletonImmutableList;
        }
        singletonImmutableList = this;
        return singletonImmutableList;
    }

    @Override
    public String toString() {
        return '[' + this.element.toString() + ']';
    }

    @Override
    boolean isPartialView() {
        return false;
    }
}

