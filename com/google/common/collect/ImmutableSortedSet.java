/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.DescendingImmutableSortedSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSetFauxverideShim;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Ordering;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedIterable;
import com.google.common.collect.SortedIterables;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableSortedSet<E>
extends ImmutableSortedSetFauxverideShim<E>
implements NavigableSet<E>,
SortedIterable<E> {
    final transient Comparator<? super E> comparator;
    @LazyInit
    @GwtIncompatible
    transient ImmutableSortedSet<E> descendingSet;

    static <E> RegularImmutableSortedSet<E> emptySet(Comparator<? super E> comparator) {
        if (!Ordering.natural().equals(comparator)) return new RegularImmutableSortedSet<E>(ImmutableList.<E>of(), comparator);
        return RegularImmutableSortedSet.NATURAL_EMPTY_SET;
    }

    public static <E> ImmutableSortedSet<E> of() {
        return RegularImmutableSortedSet.NATURAL_EMPTY_SET;
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E element) {
        return new RegularImmutableSortedSet<E>(ImmutableList.of(element), Ordering.<C>natural());
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2) {
        return ImmutableSortedSet.construct(Ordering.<C>natural(), (int)2, e1, e2);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3) {
        return ImmutableSortedSet.construct(Ordering.<C>natural(), (int)3, e1, e2, e3);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4) {
        return ImmutableSortedSet.construct(Ordering.<C>natural(), (int)4, e1, e2, e3, e4);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5) {
        return ImmutableSortedSet.construct(Ordering.<C>natural(), (int)5, e1, e2, e3, e4, e5);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E ... remaining) {
        Comparable[] contents = new Comparable[6 + remaining.length];
        contents[0] = e1;
        contents[1] = e2;
        contents[2] = e3;
        contents[3] = e4;
        contents[4] = e5;
        contents[5] = e6;
        System.arraycopy(remaining, (int)0, (Object)contents, (int)6, (int)remaining.length);
        return ImmutableSortedSet.construct(Ordering.<C>natural(), (int)contents.length, contents);
    }

    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] elements) {
        return ImmutableSortedSet.construct(Ordering.<C>natural(), (int)elements.length, (Object[])elements.clone());
    }

    public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> elements) {
        Ordering<C> naturalOrder = Ordering.natural();
        return ImmutableSortedSet.copyOf(naturalOrder, elements);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> elements) {
        Ordering<C> naturalOrder = Ordering.natural();
        return ImmutableSortedSet.copyOf(naturalOrder, elements);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> elements) {
        Ordering<C> naturalOrder = Ordering.natural();
        return ImmutableSortedSet.copyOf(naturalOrder, elements);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements) {
        return ((Builder)new Builder<E>(comparator).addAll(elements)).build();
    }

    public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements) {
        ImmutableSortedSet original;
        Preconditions.checkNotNull(comparator);
        boolean hasSameComparator = SortedIterables.hasSameComparator(comparator, elements);
        if (hasSameComparator && elements instanceof ImmutableSortedSet && !(original = (ImmutableSortedSet)elements).isPartialView()) {
            return original;
        }
        Object[] array = Iterables.toArray(elements);
        return ImmutableSortedSet.construct(comparator, (int)array.length, array);
    }

    public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Collection<? extends E> elements) {
        return ImmutableSortedSet.copyOf(comparator, elements);
    }

    public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> sortedSet) {
        Comparator<E> comparator = SortedIterables.comparator(sortedSet);
        ImmutableList<E> list = ImmutableList.copyOf(sortedSet);
        if (!list.isEmpty()) return new RegularImmutableSortedSet<E>(list, comparator);
        return ImmutableSortedSet.emptySet(comparator);
    }

    static <E> ImmutableSortedSet<E> construct(Comparator<? super E> comparator, int n, E ... contents) {
        if (n == 0) {
            return ImmutableSortedSet.emptySet(comparator);
        }
        ObjectArrays.checkElementsNotNull((Object[])contents, (int)n);
        Arrays.sort(contents, (int)0, (int)n, comparator);
        int uniques = 1;
        int i = 1;
        do {
            if (i >= n) {
                Arrays.fill((Object[])contents, (int)uniques, (int)n, null);
                return new RegularImmutableSortedSet<E>(ImmutableList.asImmutableList((Object[])contents, (int)uniques), comparator);
            }
            E cur = contents[i];
            E prev = contents[uniques - 1];
            if (comparator.compare(cur, prev) != 0) {
                contents[uniques++] = cur;
            }
            ++i;
        } while (true);
    }

    public static <E> Builder<E> orderedBy(Comparator<E> comparator) {
        return new Builder<E>(comparator);
    }

    public static <E extends Comparable<?>> Builder<E> reverseOrder() {
        return new Builder<S>(Ordering.natural().reverse());
    }

    public static <E extends Comparable<?>> Builder<E> naturalOrder() {
        return new Builder<C>(Ordering.<C>natural());
    }

    int unsafeCompare(Object a, Object b) {
        return ImmutableSortedSet.unsafeCompare(this.comparator, (Object)a, (Object)b);
    }

    static int unsafeCompare(Comparator<?> comparator, Object a, Object b) {
        Comparator<?> unsafeComparator = comparator;
        return unsafeComparator.compare(a, b);
    }

    ImmutableSortedSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    @Override
    public abstract UnmodifiableIterator<E> iterator();

    @Override
    public ImmutableSortedSet<E> headSet(E toElement) {
        return this.headSet(toElement, (boolean)false);
    }

    @GwtIncompatible
    @Override
    public ImmutableSortedSet<E> headSet(E toElement, boolean inclusive) {
        return this.headSetImpl(Preconditions.checkNotNull(toElement), (boolean)inclusive);
    }

    @Override
    public ImmutableSortedSet<E> subSet(E fromElement, E toElement) {
        return this.subSet(fromElement, (boolean)true, toElement, (boolean)false);
    }

    @GwtIncompatible
    @Override
    public ImmutableSortedSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        Preconditions.checkNotNull(fromElement);
        Preconditions.checkNotNull(toElement);
        Preconditions.checkArgument((boolean)(this.comparator.compare(fromElement, toElement) <= 0));
        return this.subSetImpl(fromElement, (boolean)fromInclusive, toElement, (boolean)toInclusive);
    }

    @Override
    public ImmutableSortedSet<E> tailSet(E fromElement) {
        return this.tailSet(fromElement, (boolean)true);
    }

    @GwtIncompatible
    @Override
    public ImmutableSortedSet<E> tailSet(E fromElement, boolean inclusive) {
        return this.tailSetImpl(Preconditions.checkNotNull(fromElement), (boolean)inclusive);
    }

    abstract ImmutableSortedSet<E> headSetImpl(E var1, boolean var2);

    abstract ImmutableSortedSet<E> subSetImpl(E var1, boolean var2, E var3, boolean var4);

    abstract ImmutableSortedSet<E> tailSetImpl(E var1, boolean var2);

    @GwtIncompatible
    @Override
    public E lower(E e) {
        return (E)Iterators.getNext(((ImmutableSortedSet)this.headSet(e, (boolean)false)).descendingIterator(), null);
    }

    @GwtIncompatible
    @Override
    public E floor(E e) {
        return (E)Iterators.getNext(((ImmutableSortedSet)this.headSet(e, (boolean)true)).descendingIterator(), null);
    }

    @GwtIncompatible
    @Override
    public E ceiling(E e) {
        return (E)Iterables.getFirst(this.tailSet(e, (boolean)true), null);
    }

    @GwtIncompatible
    @Override
    public E higher(E e) {
        return (E)Iterables.getFirst(this.tailSet(e, (boolean)false), null);
    }

    @Override
    public E first() {
        return (E)this.iterator().next();
    }

    @Override
    public E last() {
        return (E)this.descendingIterator().next();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @GwtIncompatible
    @Override
    public final E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @GwtIncompatible
    @Override
    public final E pollLast() {
        throw new UnsupportedOperationException();
    }

    @GwtIncompatible
    @Override
    public ImmutableSortedSet<E> descendingSet() {
        ImmutableSortedSet<E> result = this.descendingSet;
        if (result != null) return result;
        result = this.descendingSet = this.createDescendingSet();
        result.descendingSet = this;
        return result;
    }

    @GwtIncompatible
    ImmutableSortedSet<E> createDescendingSet() {
        return new DescendingImmutableSortedSet<E>(this);
    }

    @GwtIncompatible
    @Override
    public abstract UnmodifiableIterator<E> descendingIterator();

    abstract int indexOf(@Nullable Object var1);

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException((String)"Use SerializedForm");
    }

    @Override
    Object writeReplace() {
        return new SerializedForm<E>(this.comparator, (Object[])this.toArray());
    }
}

