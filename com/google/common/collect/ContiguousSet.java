/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Cut;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.EmptyContiguousSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RegularContiguousSet;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
public abstract class ContiguousSet<C extends Comparable>
extends ImmutableSortedSet<C> {
    final DiscreteDomain<C> domain;

    public static <C extends Comparable> ContiguousSet<C> create(Range<C> range, DiscreteDomain<C> domain) {
        boolean empty;
        ContiguousSet contiguousSet;
        Preconditions.checkNotNull(range);
        Preconditions.checkNotNull(domain);
        Range<C> effectiveRange = range;
        try {
            if (!range.hasLowerBound()) {
                effectiveRange = effectiveRange.intersection(Range.atLeast(domain.minValue()));
            }
            if (!range.hasUpperBound()) {
                effectiveRange = effectiveRange.intersection(Range.atMost(domain.maxValue()));
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException((Throwable)e);
        }
        boolean bl = empty = effectiveRange.isEmpty() || Range.compareOrThrow(range.lowerBound.leastValueAbove(domain), range.upperBound.greatestValueBelow(domain)) > 0;
        if (empty) {
            contiguousSet = new EmptyContiguousSet<C>(domain);
            return contiguousSet;
        }
        contiguousSet = new RegularContiguousSet<C>(effectiveRange, domain);
        return contiguousSet;
    }

    ContiguousSet(DiscreteDomain<C> domain) {
        super(Ordering.<C>natural());
        this.domain = domain;
    }

    @Override
    public ContiguousSet<C> headSet(C toElement) {
        return this.headSetImpl((Comparable)Preconditions.checkNotNull(toElement), (boolean)false);
    }

    @GwtIncompatible
    @Override
    public ContiguousSet<C> headSet(C toElement, boolean inclusive) {
        return this.headSetImpl((Comparable)Preconditions.checkNotNull(toElement), (boolean)inclusive);
    }

    @Override
    public ContiguousSet<C> subSet(C fromElement, C toElement) {
        Preconditions.checkNotNull(fromElement);
        Preconditions.checkNotNull(toElement);
        Preconditions.checkArgument((boolean)(this.comparator().compare(fromElement, toElement) <= 0));
        return this.subSetImpl(fromElement, (boolean)true, toElement, (boolean)false);
    }

    @GwtIncompatible
    @Override
    public ContiguousSet<C> subSet(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive) {
        Preconditions.checkNotNull(fromElement);
        Preconditions.checkNotNull(toElement);
        Preconditions.checkArgument((boolean)(this.comparator().compare(fromElement, toElement) <= 0));
        return this.subSetImpl(fromElement, (boolean)fromInclusive, toElement, (boolean)toInclusive);
    }

    @Override
    public ContiguousSet<C> tailSet(C fromElement) {
        return this.tailSetImpl((Comparable)Preconditions.checkNotNull(fromElement), (boolean)true);
    }

    @GwtIncompatible
    @Override
    public ContiguousSet<C> tailSet(C fromElement, boolean inclusive) {
        return this.tailSetImpl((Comparable)Preconditions.checkNotNull(fromElement), (boolean)inclusive);
    }

    @Override
    abstract ContiguousSet<C> headSetImpl(C var1, boolean var2);

    @Override
    abstract ContiguousSet<C> subSetImpl(C var1, boolean var2, C var3, boolean var4);

    @Override
    abstract ContiguousSet<C> tailSetImpl(C var1, boolean var2);

    public abstract ContiguousSet<C> intersection(ContiguousSet<C> var1);

    public abstract Range<C> range();

    public abstract Range<C> range(BoundType var1, BoundType var2);

    @Override
    public String toString() {
        return this.range().toString();
    }

    @Deprecated
    public static <E> ImmutableSortedSet.Builder<E> builder() {
        throw new UnsupportedOperationException();
    }
}

