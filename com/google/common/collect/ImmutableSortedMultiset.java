/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.DescendingImmutableSortedMultiset;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedMultisetFauxverideShim;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.RegularImmutableSortedMultiset;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

@GwtIncompatible
public abstract class ImmutableSortedMultiset<E>
extends ImmutableSortedMultisetFauxverideShim<E>
implements SortedMultiset<E> {
    @LazyInit
    transient ImmutableSortedMultiset<E> descendingMultiset;

    public static <E> ImmutableSortedMultiset<E> of() {
        return RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E element) {
        RegularImmutableSortedSet elementSet = (RegularImmutableSortedSet)ImmutableSortedSet.of(element);
        long[] cumulativeCounts = new long[]{0L, 1L};
        return new RegularImmutableSortedMultiset<E>(elementSet, (long[])cumulativeCounts, (int)0, (int)1);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2) {
        return ImmutableSortedMultiset.copyOf(Ordering.<C>natural(), Arrays.asList(e1, e2));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3) {
        return ImmutableSortedMultiset.copyOf(Ordering.<C>natural(), Arrays.asList(e1, e2, e3));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4) {
        return ImmutableSortedMultiset.copyOf(Ordering.<C>natural(), Arrays.asList(e1, e2, e3, e4));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5) {
        return ImmutableSortedMultiset.copyOf(Ordering.<C>natural(), Arrays.asList(e1, e2, e3, e4, e5));
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E ... remaining) {
        int size = remaining.length + 6;
        ArrayList<E> all = Lists.newArrayListWithCapacity((int)size);
        Collections.addAll(all, e1, e2, e3, e4, e5, e6);
        Collections.addAll(all, remaining);
        return ImmutableSortedMultiset.copyOf(Ordering.<C>natural(), all);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] elements) {
        return ImmutableSortedMultiset.copyOf(Ordering.<C>natural(), Arrays.asList(elements));
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> elements) {
        Ordering<C> naturalOrder = Ordering.natural();
        return ImmutableSortedMultiset.copyOf(naturalOrder, elements);
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> elements) {
        Ordering<C> naturalOrder = Ordering.natural();
        return ImmutableSortedMultiset.copyOf(naturalOrder, elements);
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements) {
        Preconditions.checkNotNull(comparator);
        return ((Builder)new Builder<E>(comparator).addAll(elements)).build();
    }

    public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements) {
        ImmutableSortedMultiset multiset;
        if (elements instanceof ImmutableSortedMultiset && comparator.equals((multiset = (ImmutableSortedMultiset)elements).comparator())) {
            if (!multiset.isPartialView()) return multiset;
            return ImmutableSortedMultiset.copyOfSortedEntries(comparator, ((ImmutableSet)multiset.entrySet()).asList());
        }
        elements = Lists.newArrayList(elements);
        TreeMultiset<E> sortedCopy = TreeMultiset.create(Preconditions.checkNotNull(comparator));
        Iterables.addAll(sortedCopy, elements);
        return ImmutableSortedMultiset.copyOfSortedEntries(comparator, sortedCopy.entrySet());
    }

    public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> sortedMultiset) {
        return ImmutableSortedMultiset.copyOfSortedEntries(sortedMultiset.comparator(), Lists.newArrayList(sortedMultiset.entrySet()));
    }

    private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(Comparator<? super E> comparator, Collection<Multiset.Entry<E>> entries) {
        if (entries.isEmpty()) {
            return ImmutableSortedMultiset.emptyMultiset(comparator);
        }
        ImmutableList.Builder<E> elementsBuilder = new ImmutableList.Builder<E>((int)entries.size());
        long[] cumulativeCounts = new long[entries.size() + 1];
        int i = 0;
        Iterator<Multiset.Entry<E>> i$ = entries.iterator();
        while (i$.hasNext()) {
            Multiset.Entry<E> entry = i$.next();
            elementsBuilder.add(entry.getElement());
            cumulativeCounts[i + 1] = cumulativeCounts[i] + (long)entry.getCount();
            ++i;
        }
        return new RegularImmutableSortedMultiset<E>(new RegularImmutableSortedSet<E>(elementsBuilder.build(), comparator), (long[])cumulativeCounts, (int)0, (int)entries.size());
    }

    static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> comparator) {
        if (!Ordering.natural().equals(comparator)) return new RegularImmutableSortedMultiset<E>(comparator);
        return RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
    }

    ImmutableSortedMultiset() {
    }

    @Override
    public final Comparator<? super E> comparator() {
        return ((ImmutableSortedSet)this.elementSet()).comparator();
    }

    @Override
    public abstract ImmutableSortedSet<E> elementSet();

    @Override
    public ImmutableSortedMultiset<E> descendingMultiset() {
        ImmutableSortedMultiset<E> result = this.descendingMultiset;
        if (result != null) return result;
        this.descendingMultiset = this.isEmpty() ? ImmutableSortedMultiset.emptyMultiset(Ordering.from(this.comparator()).reverse()) : new DescendingImmutableSortedMultiset<E>(this);
        return this.descendingMultiset;
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final Multiset.Entry<E> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final Multiset.Entry<E> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract ImmutableSortedMultiset<E> headMultiset(E var1, BoundType var2);

    @Override
    public ImmutableSortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType) {
        Preconditions.checkArgument((boolean)(this.comparator().compare(lowerBound, upperBound) <= 0), (String)"Expected lowerBound <= upperBound but %s > %s", lowerBound, upperBound);
        return ((ImmutableSortedMultiset)this.tailMultiset(lowerBound, (BoundType)lowerBoundType)).headMultiset(upperBound, (BoundType)upperBoundType);
    }

    @Override
    public abstract ImmutableSortedMultiset<E> tailMultiset(E var1, BoundType var2);

    public static <E> Builder<E> orderedBy(Comparator<E> comparator) {
        return new Builder<E>(comparator);
    }

    public static <E extends Comparable<?>> Builder<E> reverseOrder() {
        return new Builder<S>(Ordering.natural().reverse());
    }

    public static <E extends Comparable<?>> Builder<E> naturalOrder() {
        return new Builder<C>(Ordering.<C>natural());
    }

    @Override
    Object writeReplace() {
        return new SerializedForm<E>(this);
    }
}

