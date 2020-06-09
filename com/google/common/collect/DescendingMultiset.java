/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.DescendingMultiset;
import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.SortedMultisets;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
abstract class DescendingMultiset<E>
extends ForwardingMultiset<E>
implements SortedMultiset<E> {
    private transient Comparator<? super E> comparator;
    private transient NavigableSet<E> elementSet;
    private transient Set<Multiset.Entry<E>> entrySet;

    DescendingMultiset() {
    }

    abstract SortedMultiset<E> forwardMultiset();

    @Override
    public Comparator<? super E> comparator() {
        Comparator<? super E> result = this.comparator;
        if (result != null) return result;
        this.comparator = Ordering.from(this.forwardMultiset().comparator()).reverse();
        return this.comparator;
    }

    @Override
    public NavigableSet<E> elementSet() {
        NavigableSet<E> result = this.elementSet;
        if (result != null) return result;
        this.elementSet = new SortedMultisets.NavigableElementSet<E>(this);
        return this.elementSet;
    }

    @Override
    public Multiset.Entry<E> pollFirstEntry() {
        return this.forwardMultiset().pollLastEntry();
    }

    @Override
    public Multiset.Entry<E> pollLastEntry() {
        return this.forwardMultiset().pollFirstEntry();
    }

    @Override
    public SortedMultiset<E> headMultiset(E toElement, BoundType boundType) {
        return this.forwardMultiset().tailMultiset(toElement, (BoundType)boundType).descendingMultiset();
    }

    @Override
    public SortedMultiset<E> subMultiset(E fromElement, BoundType fromBoundType, E toElement, BoundType toBoundType) {
        return this.forwardMultiset().subMultiset(toElement, (BoundType)toBoundType, fromElement, (BoundType)fromBoundType).descendingMultiset();
    }

    @Override
    public SortedMultiset<E> tailMultiset(E fromElement, BoundType boundType) {
        return this.forwardMultiset().headMultiset(fromElement, (BoundType)boundType).descendingMultiset();
    }

    @Override
    protected Multiset<E> delegate() {
        return this.forwardMultiset();
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        return this.forwardMultiset();
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return this.forwardMultiset().lastEntry();
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return this.forwardMultiset().firstEntry();
    }

    abstract Iterator<Multiset.Entry<E>> entryIterator();

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        Set<Multiset.Entry<E>> set;
        Set<Multiset.Entry<E>> result = this.entrySet;
        if (result == null) {
            set = this.entrySet = this.createEntrySet();
            return set;
        }
        set = result;
        return set;
    }

    Set<Multiset.Entry<E>> createEntrySet() {
        class EntrySetImpl
        extends com.google.common.collect.Multisets$EntrySet<E> {
            final /* synthetic */ DescendingMultiset this$0;

            EntrySetImpl(DescendingMultiset descendingMultiset) {
                this.this$0 = descendingMultiset;
            }

            Multiset<E> multiset() {
                return this.this$0;
            }

            public Iterator<Multiset.Entry<E>> iterator() {
                return this.this$0.entryIterator();
            }

            public int size() {
                return this.this$0.forwardMultiset().entrySet().size();
            }
        }
        return new EntrySetImpl((DescendingMultiset)this);
    }

    @Override
    public Iterator<E> iterator() {
        return Multisets.iteratorImpl(this);
    }

    @Override
    public Object[] toArray() {
        return this.standardToArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return this.standardToArray(array);
    }

    @Override
    public String toString() {
        return this.entrySet().toString();
    }
}

