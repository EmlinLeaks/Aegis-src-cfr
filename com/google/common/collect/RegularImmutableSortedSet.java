/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedAsList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.SortedIterables;
import com.google.common.collect.SortedLists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
final class RegularImmutableSortedSet<E>
extends ImmutableSortedSet<E> {
    static final RegularImmutableSortedSet<Comparable> NATURAL_EMPTY_SET = new RegularImmutableSortedSet<E>(ImmutableList.<E>of(), Ordering.<C>natural());
    private final transient ImmutableList<E> elements;

    RegularImmutableSortedSet(ImmutableList<E> elements, Comparator<? super E> comparator) {
        super(comparator);
        this.elements = elements;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.elements.iterator();
    }

    @GwtIncompatible
    @Override
    public UnmodifiableIterator<E> descendingIterator() {
        return this.elements.reverse().iterator();
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        try {
            if (o == null) return false;
            if (this.unsafeBinarySearch((Object)o) < 0) return false;
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> targets) {
        if (targets instanceof Multiset) {
            targets = ((Multiset)targets).elementSet();
        }
        if (!SortedIterables.hasSameComparator(this.comparator(), targets)) return super.containsAll(targets);
        if (targets.size() <= 1) {
            return super.containsAll(targets);
        }
        PeekingIterator<T> thisIterator = Iterators.peekingIterator(this.iterator());
        Iterator<?> thatIterator = targets.iterator();
        ? target = thatIterator.next();
        try {
            while (thisIterator.hasNext()) {
                int cmp = this.unsafeCompare(thisIterator.peek(), target);
                if (cmp < 0) {
                    thisIterator.next();
                    continue;
                }
                if (cmp == 0) {
                    if (!thatIterator.hasNext()) {
                        return true;
                    }
                    target = thatIterator.next();
                    continue;
                }
                if (cmp > 0) return false;
            }
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    private int unsafeBinarySearch(Object key) throws ClassCastException {
        return Collections.binarySearch(this.elements, key, this.unsafeComparator());
    }

    @Override
    boolean isPartialView() {
        return this.elements.isPartialView();
    }

    @Override
    int copyIntoArray(Object[] dst, int offset) {
        return this.elements.copyIntoArray((Object[])dst, (int)offset);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Set)) {
            return false;
        }
        Set that = (Set)object;
        if (this.size() != that.size()) {
            return false;
        }
        if (this.isEmpty()) {
            return true;
        }
        if (!SortedIterables.hasSameComparator(this.comparator, that)) return this.containsAll(that);
        Iterator<E> otherIterator = that.iterator();
        try {
            E element;
            E otherElement;
            Iterator iterator = this.iterator();
            do {
                if (!iterator.hasNext()) return true;
                element = iterator.next();
                otherElement = otherIterator.next();
                if (otherElement == null) return false;
            } while (this.unsafeCompare(element, otherElement) == 0);
            return false;
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public E first() {
        if (!this.isEmpty()) return (E)this.elements.get((int)0);
        throw new NoSuchElementException();
    }

    @Override
    public E last() {
        if (!this.isEmpty()) return (E)this.elements.get((int)(this.size() - 1));
        throw new NoSuchElementException();
    }

    @Override
    public E lower(E element) {
        E e;
        int index = this.headIndex(element, (boolean)false) - 1;
        if (index == -1) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.elements.get((int)index);
        return (E)e;
    }

    @Override
    public E floor(E element) {
        E e;
        int index = this.headIndex(element, (boolean)true) - 1;
        if (index == -1) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.elements.get((int)index);
        return (E)e;
    }

    @Override
    public E ceiling(E element) {
        E e;
        int index = this.tailIndex(element, (boolean)true);
        if (index == this.size()) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.elements.get((int)index);
        return (E)e;
    }

    @Override
    public E higher(E element) {
        E e;
        int index = this.tailIndex(element, (boolean)false);
        if (index == this.size()) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.elements.get((int)index);
        return (E)e;
    }

    @Override
    ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive) {
        return this.getSubSet((int)0, (int)this.headIndex(toElement, (boolean)inclusive));
    }

    int headIndex(E toElement, boolean inclusive) {
        SortedLists.KeyPresentBehavior keyPresentBehavior;
        if (inclusive) {
            keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_AFTER;
            return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(toElement), this.comparator(), (SortedLists.KeyPresentBehavior)keyPresentBehavior, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        }
        keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_PRESENT;
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(toElement), this.comparator(), (SortedLists.KeyPresentBehavior)keyPresentBehavior, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    @Override
    ImmutableSortedSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return this.tailSetImpl(fromElement, (boolean)fromInclusive).headSetImpl(toElement, (boolean)toInclusive);
    }

    @Override
    ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive) {
        return this.getSubSet((int)this.tailIndex(fromElement, (boolean)inclusive), (int)this.size());
    }

    int tailIndex(E fromElement, boolean inclusive) {
        SortedLists.KeyPresentBehavior keyPresentBehavior;
        if (inclusive) {
            keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_PRESENT;
            return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(fromElement), this.comparator(), (SortedLists.KeyPresentBehavior)keyPresentBehavior, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        }
        keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_AFTER;
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(fromElement), this.comparator(), (SortedLists.KeyPresentBehavior)keyPresentBehavior, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    Comparator<Object> unsafeComparator() {
        return this.comparator;
    }

    RegularImmutableSortedSet<E> getSubSet(int newFromIndex, int newToIndex) {
        if (newFromIndex == 0 && newToIndex == this.size()) {
            return this;
        }
        if (newFromIndex >= newToIndex) return RegularImmutableSortedSet.emptySet(this.comparator);
        return new RegularImmutableSortedSet<E>(this.elements.subList((int)newFromIndex, (int)newToIndex), this.comparator);
    }

    @Override
    int indexOf(@Nullable Object target) {
        int position;
        if (target == null) {
            return -1;
        }
        try {
            position = SortedLists.binarySearch(this.elements, target, this.unsafeComparator(), (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.ANY_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
        }
        catch (ClassCastException e) {
            return -1;
        }
        if (position < 0) return -1;
        int n = position;
        return n;
    }

    @Override
    ImmutableList<E> createAsList() {
        ImmutableSortedAsList<E> immutableSortedAsList;
        if (this.size() <= 1) {
            immutableSortedAsList = this.elements;
            return immutableSortedAsList;
        }
        immutableSortedAsList = new ImmutableSortedAsList<E>(this, this.elements);
        return immutableSortedAsList;
    }

    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        RegularImmutableSortedSet<E> regularImmutableSortedSet;
        Ordering<S> reversedOrder = Ordering.from(this.comparator).reverse();
        if (this.isEmpty()) {
            regularImmutableSortedSet = RegularImmutableSortedSet.emptySet(reversedOrder);
            return regularImmutableSortedSet;
        }
        regularImmutableSortedSet = new RegularImmutableSortedSet<E>(this.elements.reverse(), reversedOrder);
        return regularImmutableSortedSet;
    }
}

