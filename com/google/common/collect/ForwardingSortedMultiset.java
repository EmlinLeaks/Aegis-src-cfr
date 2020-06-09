/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SortedMultiset;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

@Beta
@GwtCompatible(emulated=true)
public abstract class ForwardingSortedMultiset<E>
extends ForwardingMultiset<E>
implements SortedMultiset<E> {
    protected ForwardingSortedMultiset() {
    }

    @Override
    protected abstract SortedMultiset<E> delegate();

    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet)super.elementSet();
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        return this.delegate().descendingMultiset();
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return this.delegate().firstEntry();
    }

    protected Multiset.Entry<E> standardFirstEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.entrySet().iterator();
        if (!entryIterator.hasNext()) {
            return null;
        }
        Multiset.Entry<E> entry = entryIterator.next();
        return Multisets.immutableEntry(entry.getElement(), (int)entry.getCount());
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return this.delegate().lastEntry();
    }

    protected Multiset.Entry<E> standardLastEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.descendingMultiset().entrySet().iterator();
        if (!entryIterator.hasNext()) {
            return null;
        }
        Multiset.Entry<E> entry = entryIterator.next();
        return Multisets.immutableEntry(entry.getElement(), (int)entry.getCount());
    }

    @Override
    public Multiset.Entry<E> pollFirstEntry() {
        return this.delegate().pollFirstEntry();
    }

    protected Multiset.Entry<E> standardPollFirstEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.entrySet().iterator();
        if (!entryIterator.hasNext()) {
            return null;
        }
        Multiset.Entry<E> entry = entryIterator.next();
        entry = Multisets.immutableEntry(entry.getElement(), (int)entry.getCount());
        entryIterator.remove();
        return entry;
    }

    @Override
    public Multiset.Entry<E> pollLastEntry() {
        return this.delegate().pollLastEntry();
    }

    protected Multiset.Entry<E> standardPollLastEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.descendingMultiset().entrySet().iterator();
        if (!entryIterator.hasNext()) {
            return null;
        }
        Multiset.Entry<E> entry = entryIterator.next();
        entry = Multisets.immutableEntry(entry.getElement(), (int)entry.getCount());
        entryIterator.remove();
        return entry;
    }

    @Override
    public SortedMultiset<E> headMultiset(E upperBound, BoundType boundType) {
        return this.delegate().headMultiset(upperBound, (BoundType)boundType);
    }

    @Override
    public SortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType) {
        return this.delegate().subMultiset(lowerBound, (BoundType)lowerBoundType, upperBound, (BoundType)upperBoundType);
    }

    protected SortedMultiset<E> standardSubMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType) {
        return this.tailMultiset(lowerBound, (BoundType)lowerBoundType).headMultiset(upperBound, (BoundType)upperBoundType);
    }

    @Override
    public SortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType) {
        return this.delegate().tailMultiset(lowerBound, (BoundType)boundType);
    }
}

