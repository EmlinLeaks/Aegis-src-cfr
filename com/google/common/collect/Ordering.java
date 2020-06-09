/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.AllEqualOrdering;
import com.google.common.collect.ByFunctionOrdering;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ComparatorOrdering;
import com.google.common.collect.CompoundOrdering;
import com.google.common.collect.ExplicitOrdering;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LexicographicalOrdering;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.NaturalOrdering;
import com.google.common.collect.NullsFirstOrdering;
import com.google.common.collect.NullsLastOrdering;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Ordering;
import com.google.common.collect.ReverseOrdering;
import com.google.common.collect.TopKSelector;
import com.google.common.collect.UsingToStringOrdering;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Ordering<T>
implements Comparator<T> {
    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;

    @GwtCompatible(serializable=true)
    public static <C extends Comparable> Ordering<C> natural() {
        return NaturalOrdering.INSTANCE;
    }

    @GwtCompatible(serializable=true)
    public static <T> Ordering<T> from(Comparator<T> comparator) {
        Ordering ordering;
        if (comparator instanceof Ordering) {
            ordering = (Ordering)comparator;
            return ordering;
        }
        ordering = new ComparatorOrdering<T>(comparator);
        return ordering;
    }

    @Deprecated
    @GwtCompatible(serializable=true)
    public static <T> Ordering<T> from(Ordering<T> ordering) {
        return Preconditions.checkNotNull(ordering);
    }

    @GwtCompatible(serializable=true)
    public static <T> Ordering<T> explicit(List<T> valuesInOrder) {
        return new ExplicitOrdering<T>(valuesInOrder);
    }

    @GwtCompatible(serializable=true)
    public static <T> Ordering<T> explicit(T leastValue, T ... remainingValuesInOrder) {
        return Ordering.explicit(Lists.asList(leastValue, remainingValuesInOrder));
    }

    @GwtCompatible(serializable=true)
    public static Ordering<Object> allEqual() {
        return AllEqualOrdering.INSTANCE;
    }

    @GwtCompatible(serializable=true)
    public static Ordering<Object> usingToString() {
        return UsingToStringOrdering.INSTANCE;
    }

    public static Ordering<Object> arbitrary() {
        return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
    }

    protected Ordering() {
    }

    @GwtCompatible(serializable=true)
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering<T>(this);
    }

    @GwtCompatible(serializable=true)
    public <S extends T> Ordering<S> nullsFirst() {
        return new NullsFirstOrdering<T>(this);
    }

    @GwtCompatible(serializable=true)
    public <S extends T> Ordering<S> nullsLast() {
        return new NullsLastOrdering<T>(this);
    }

    @GwtCompatible(serializable=true)
    public <F> Ordering<F> onResultOf(Function<F, ? extends T> function) {
        return new ByFunctionOrdering<F, T>(function, this);
    }

    <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
        return this.onResultOf(Maps.<K>keyFunction());
    }

    @GwtCompatible(serializable=true)
    public <U extends T> Ordering<U> compound(Comparator<? super U> secondaryComparator) {
        return new CompoundOrdering<U>(this, Preconditions.checkNotNull(secondaryComparator));
    }

    @GwtCompatible(serializable=true)
    public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> comparators) {
        return new CompoundOrdering<T>(comparators);
    }

    @GwtCompatible(serializable=true)
    public <S extends T> Ordering<Iterable<S>> lexicographical() {
        return new LexicographicalOrdering<T>(this);
    }

    @CanIgnoreReturnValue
    @Override
    public abstract int compare(@Nullable T var1, @Nullable T var2);

    @CanIgnoreReturnValue
    public <E extends T> E min(Iterator<E> iterator) {
        E minSoFar = iterator.next();
        while (iterator.hasNext()) {
            minSoFar = this.min(minSoFar, iterator.next());
        }
        return (E)minSoFar;
    }

    @CanIgnoreReturnValue
    public <E extends T> E min(Iterable<E> iterable) {
        return (E)this.min(iterable.iterator());
    }

    @CanIgnoreReturnValue
    public <E extends T> E min(@Nullable E a, @Nullable E b) {
        E e;
        if (this.compare(a, b) <= 0) {
            e = a;
            return (E)((E)e);
        }
        e = b;
        return (E)e;
    }

    @CanIgnoreReturnValue
    public <E extends T> E min(@Nullable E a, @Nullable E b, @Nullable E c, E ... rest) {
        E minSoFar = this.min(this.min(a, b), c);
        E[] arr$ = rest;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            E r = arr$[i$];
            minSoFar = this.min(minSoFar, r);
            ++i$;
        }
        return (E)minSoFar;
    }

    @CanIgnoreReturnValue
    public <E extends T> E max(Iterator<E> iterator) {
        E maxSoFar = iterator.next();
        while (iterator.hasNext()) {
            maxSoFar = this.max(maxSoFar, iterator.next());
        }
        return (E)maxSoFar;
    }

    @CanIgnoreReturnValue
    public <E extends T> E max(Iterable<E> iterable) {
        return (E)this.max(iterable.iterator());
    }

    @CanIgnoreReturnValue
    public <E extends T> E max(@Nullable E a, @Nullable E b) {
        E e;
        if (this.compare(a, b) >= 0) {
            e = a;
            return (E)((E)e);
        }
        e = b;
        return (E)e;
    }

    @CanIgnoreReturnValue
    public <E extends T> E max(@Nullable E a, @Nullable E b, @Nullable E c, E ... rest) {
        E maxSoFar = this.max(this.max(a, b), c);
        E[] arr$ = rest;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            E r = arr$[i$];
            maxSoFar = this.max(maxSoFar, r);
            ++i$;
        }
        return (E)maxSoFar;
    }

    public <E extends T> List<E> leastOf(Iterable<E> iterable, int k) {
        if (!(iterable instanceof Collection)) return this.leastOf(iterable.iterator(), (int)k);
        Collection collection = (Collection)iterable;
        if ((long)collection.size() > 2L * (long)k) return this.leastOf(iterable.iterator(), (int)k);
        Object[] array = collection.toArray();
        Arrays.sort(array, this);
        if (array.length <= k) return Collections.unmodifiableList(Arrays.asList(array));
        array = ObjectArrays.arraysCopyOf(array, (int)k);
        return Collections.unmodifiableList(Arrays.asList(array));
    }

    public <E extends T> List<E> leastOf(Iterator<E> iterator, int k) {
        Preconditions.checkNotNull(iterator);
        CollectPreconditions.checkNonnegative((int)k, (String)"k");
        if (k == 0) return ImmutableList.of();
        if (!iterator.hasNext()) {
            return ImmutableList.of();
        }
        if (k < 1073741823) {
            TopKSelector<E> selector = TopKSelector.least((int)k, this);
            selector.offerAll(iterator);
            return selector.topK();
        }
        ArrayList<E> list = Lists.newArrayList(iterator);
        Collections.sort(list, this);
        if (list.size() > k) {
            list.subList((int)k, (int)list.size()).clear();
        }
        list.trimToSize();
        return Collections.unmodifiableList(list);
    }

    public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k) {
        return this.reverse().leastOf(iterable, (int)k);
    }

    public <E extends T> List<E> greatestOf(Iterator<E> iterator, int k) {
        return this.reverse().leastOf(iterator, (int)k);
    }

    @CanIgnoreReturnValue
    public <E extends T> List<E> sortedCopy(Iterable<E> elements) {
        Object[] array = Iterables.toArray(elements);
        Arrays.sort(array, this);
        return Lists.newArrayList(Arrays.asList(array));
    }

    @CanIgnoreReturnValue
    public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> elements) {
        Object[] array;
        Object[] arr$ = array = Iterables.toArray(elements);
        int len$ = arr$.length;
        int i$ = 0;
        do {
            if (i$ >= len$) {
                Arrays.sort(array, this);
                return ImmutableList.asImmutableList((Object[])array);
            }
            Object e = arr$[i$];
            Preconditions.checkNotNull(e);
            ++i$;
        } while (true);
    }

    public boolean isOrdered(Iterable<? extends T> iterable) {
        Iterator<T> it = iterable.iterator();
        if (!it.hasNext()) return true;
        T prev = it.next();
        while (it.hasNext()) {
            T next = it.next();
            if (this.compare(prev, next) > 0) {
                return false;
            }
            prev = next;
        }
        return true;
    }

    public boolean isStrictlyOrdered(Iterable<? extends T> iterable) {
        Iterator<T> it = iterable.iterator();
        if (!it.hasNext()) return true;
        T prev = it.next();
        while (it.hasNext()) {
            T next = it.next();
            if (this.compare(prev, next) >= 0) {
                return false;
            }
            prev = next;
        }
        return true;
    }

    @Deprecated
    public int binarySearch(List<? extends T> sortedList, @Nullable T key) {
        return Collections.binarySearch(sortedList, key, this);
    }
}

