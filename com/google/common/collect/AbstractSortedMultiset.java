/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.AbstractSortedMultiset;
import com.google.common.collect.BoundType;
import com.google.common.collect.GwtTransient;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.SortedMultisets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
abstract class AbstractSortedMultiset<E>
extends AbstractMultiset<E>
implements SortedMultiset<E> {
    @GwtTransient
    final Comparator<? super E> comparator;
    private transient SortedMultiset<E> descendingMultiset;

    AbstractSortedMultiset() {
        this(Ordering.natural());
    }

    AbstractSortedMultiset(Comparator<? super E> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator);
    }

    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet)super.elementSet();
    }

    @Override
    NavigableSet<E> createElementSet() {
        return new SortedMultisets.NavigableElementSet<E>(this);
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.entryIterator();
        if (!entryIterator.hasNext()) return null;
        Multiset.Entry<E> entry = entryIterator.next();
        return entry;
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.descendingEntryIterator();
        if (!entryIterator.hasNext()) return null;
        Multiset.Entry<E> entry = entryIterator.next();
        return entry;
    }

    @Override
    public Multiset.Entry<E> pollFirstEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.entryIterator();
        if (!entryIterator.hasNext()) return null;
        Multiset.Entry<E> result = entryIterator.next();
        result = Multisets.immutableEntry(result.getElement(), (int)result.getCount());
        entryIterator.remove();
        return result;
    }

    @Override
    public Multiset.Entry<E> pollLastEntry() {
        Iterator<Multiset.Entry<E>> entryIterator = this.descendingEntryIterator();
        if (!entryIterator.hasNext()) return null;
        Multiset.Entry<E> result = entryIterator.next();
        result = Multisets.immutableEntry(result.getElement(), (int)result.getCount());
        entryIterator.remove();
        return result;
    }

    @Override
    public SortedMultiset<E> subMultiset(@Nullable E fromElement, BoundType fromBoundType, @Nullable E toElement, BoundType toBoundType) {
        Preconditions.checkNotNull(fromBoundType);
        Preconditions.checkNotNull(toBoundType);
        return this.tailMultiset(fromElement, (BoundType)fromBoundType).headMultiset(toElement, (BoundType)toBoundType);
    }

    abstract Iterator<Multiset.Entry<E>> descendingEntryIterator();

    Iterator<E> descendingIterator() {
        return Multisets.iteratorImpl(this.descendingMultiset());
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        SortedMultiset<E> sortedMultiset;
        SortedMultiset<E> result = this.descendingMultiset;
        if (result == null) {
            sortedMultiset = this.descendingMultiset = this.createDescendingMultiset();
            return sortedMultiset;
        }
        sortedMultiset = result;
        return sortedMultiset;
    }

    SortedMultiset<E> createDescendingMultiset() {
        class DescendingMultisetImpl
        extends com.google.common.collect.DescendingMultiset<E> {
            final /* synthetic */ AbstractSortedMultiset this$0;

            DescendingMultisetImpl(AbstractSortedMultiset abstractSortedMultiset) {
                this.this$0 = abstractSortedMultiset;
            }

            SortedMultiset<E> forwardMultiset() {
                return this.this$0;
            }

            Iterator<Multiset.Entry<E>> entryIterator() {
                return this.this$0.descendingEntryIterator();
            }

            public Iterator<E> iterator() {
                return this.this$0.descendingIterator();
            }
        }
        return new DescendingMultisetImpl((AbstractSortedMultiset)this);
    }
}

