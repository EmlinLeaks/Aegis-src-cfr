/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedMultiset;
import com.google.common.primitives.Ints;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtIncompatible
final class RegularImmutableSortedMultiset<E>
extends ImmutableSortedMultiset<E> {
    private static final long[] ZERO_CUMULATIVE_COUNTS = new long[]{0L};
    static final ImmutableSortedMultiset<Comparable> NATURAL_EMPTY_MULTISET = new RegularImmutableSortedMultiset<C>(Ordering.<C>natural());
    private final transient RegularImmutableSortedSet<E> elementSet;
    private final transient long[] cumulativeCounts;
    private final transient int offset;
    private final transient int length;

    RegularImmutableSortedMultiset(Comparator<? super E> comparator) {
        this.elementSet = ImmutableSortedSet.emptySet(comparator);
        this.cumulativeCounts = ZERO_CUMULATIVE_COUNTS;
        this.offset = 0;
        this.length = 0;
    }

    RegularImmutableSortedMultiset(RegularImmutableSortedSet<E> elementSet, long[] cumulativeCounts, int offset, int length) {
        this.elementSet = elementSet;
        this.cumulativeCounts = cumulativeCounts;
        this.offset = offset;
        this.length = length;
    }

    private int getCount(int index) {
        return (int)(this.cumulativeCounts[this.offset + index + 1] - this.cumulativeCounts[this.offset + index]);
    }

    @Override
    Multiset.Entry<E> getEntry(int index) {
        return Multisets.immutableEntry(this.elementSet.asList().get((int)index), (int)this.getCount((int)index));
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        if (this.isEmpty()) {
            return null;
        }
        Multiset.Entry<E> entry = this.getEntry((int)0);
        return entry;
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        if (this.isEmpty()) {
            return null;
        }
        Multiset.Entry<E> entry = this.getEntry((int)(this.length - 1));
        return entry;
    }

    @Override
    public int count(@Nullable Object element) {
        int index = this.elementSet.indexOf((Object)element);
        if (index < 0) return 0;
        int n = this.getCount((int)index);
        return n;
    }

    @Override
    public int size() {
        long size = this.cumulativeCounts[this.offset + this.length] - this.cumulativeCounts[this.offset];
        return Ints.saturatedCast((long)size);
    }

    @Override
    public ImmutableSortedSet<E> elementSet() {
        return this.elementSet;
    }

    @Override
    public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType) {
        boolean bl;
        if (Preconditions.checkNotNull(boundType) == BoundType.CLOSED) {
            bl = true;
            return this.getSubMultiset((int)0, (int)this.elementSet.headIndex(upperBound, (boolean)bl));
        }
        bl = false;
        return this.getSubMultiset((int)0, (int)this.elementSet.headIndex(upperBound, (boolean)bl));
    }

    @Override
    public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType) {
        boolean bl;
        if (Preconditions.checkNotNull(boundType) == BoundType.CLOSED) {
            bl = true;
            return this.getSubMultiset((int)this.elementSet.tailIndex(lowerBound, (boolean)bl), (int)this.length);
        }
        bl = false;
        return this.getSubMultiset((int)this.elementSet.tailIndex(lowerBound, (boolean)bl), (int)this.length);
    }

    ImmutableSortedMultiset<E> getSubMultiset(int from, int to) {
        Preconditions.checkPositionIndexes((int)from, (int)to, (int)this.length);
        if (from == to) {
            return RegularImmutableSortedMultiset.emptyMultiset(this.comparator());
        }
        if (from == 0 && to == this.length) {
            return this;
        }
        RegularImmutableSortedSet<E> subElementSet = this.elementSet.getSubSet((int)from, (int)to);
        return new RegularImmutableSortedMultiset<E>(subElementSet, (long[])this.cumulativeCounts, (int)(this.offset + from), (int)(to - from));
    }

    @Override
    boolean isPartialView() {
        if (this.offset > 0) return true;
        if (this.length < this.cumulativeCounts.length - 1) return true;
        return false;
    }
}

