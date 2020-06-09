/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultiset<E>
extends AbstractCollection<E>
implements Multiset<E> {
    private transient Set<E> elementSet;
    private transient Set<Multiset.Entry<E>> entrySet;

    AbstractMultiset() {
    }

    @Override
    public int size() {
        return Multisets.sizeImpl(this);
    }

    @Override
    public boolean isEmpty() {
        return this.entrySet().isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object element) {
        if (this.count((Object)element) <= 0) return false;
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return Multisets.iteratorImpl(this);
    }

    @Override
    public int count(@Nullable Object element) {
        Multiset.Entry<E> entry;
        Iterator<Multiset.Entry<E>> i$ = this.entrySet().iterator();
        do {
            if (!i$.hasNext()) return 0;
        } while (!Objects.equal((entry = i$.next()).getElement(), (Object)element));
        return entry.getCount();
    }

    @CanIgnoreReturnValue
    @Override
    public boolean add(@Nullable E element) {
        this.add(element, (int)1);
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public int add(@Nullable E element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @CanIgnoreReturnValue
    @Override
    public boolean remove(@Nullable Object element) {
        if (this.remove((Object)element, (int)1) <= 0) return false;
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public int remove(@Nullable Object element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @CanIgnoreReturnValue
    @Override
    public int setCount(@Nullable E element, int count) {
        return Multisets.setCountImpl(this, element, (int)count);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean setCount(@Nullable E element, int oldCount, int newCount) {
        return Multisets.setCountImpl(this, element, (int)oldCount, (int)newCount);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean addAll(Collection<? extends E> elementsToAdd) {
        return Multisets.addAllImpl(this, elementsToAdd);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeAll(Collection<?> elementsToRemove) {
        return Multisets.removeAllImpl(this, elementsToRemove);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean retainAll(Collection<?> elementsToRetain) {
        return Multisets.retainAllImpl(this, elementsToRetain);
    }

    @Override
    public void clear() {
        Iterators.clear(this.entryIterator());
    }

    @Override
    public Set<E> elementSet() {
        Set<E> result = this.elementSet;
        if (result != null) return result;
        this.elementSet = result = this.createElementSet();
        return result;
    }

    Set<E> createElementSet() {
        return new ElementSet((AbstractMultiset)this);
    }

    abstract Iterator<Multiset.Entry<E>> entryIterator();

    abstract int distinctElements();

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        Set<Multiset.Entry<Multiset.Entry<E>>> result = this.entrySet;
        if (result != null) return result;
        this.entrySet = result = this.createEntrySet();
        return result;
    }

    Set<Multiset.Entry<E>> createEntrySet() {
        return new EntrySet((AbstractMultiset)this);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return Multisets.equalsImpl(this, (Object)object);
    }

    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }

    @Override
    public String toString() {
        return this.entrySet().toString();
    }
}

