/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractRangeSet;
import com.google.common.collect.Cut;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedLists;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;

@Beta
@GwtIncompatible
public final class ImmutableRangeSet<C extends Comparable>
extends AbstractRangeSet<C>
implements Serializable {
    private static final ImmutableRangeSet<Comparable<?>> EMPTY = new ImmutableRangeSet<C>(ImmutableList.<Range<C>>of());
    private static final ImmutableRangeSet<Comparable<?>> ALL = new ImmutableRangeSet<C>(ImmutableList.of(Range.<C>all()));
    private final transient ImmutableList<Range<C>> ranges;
    @LazyInit
    private transient ImmutableRangeSet<C> complement;

    public static <C extends Comparable> ImmutableRangeSet<C> of() {
        return EMPTY;
    }

    static <C extends Comparable> ImmutableRangeSet<C> all() {
        return ALL;
    }

    public static <C extends Comparable> ImmutableRangeSet<C> of(Range<C> range) {
        Preconditions.checkNotNull(range);
        if (range.isEmpty()) {
            return ImmutableRangeSet.of();
        }
        if (!range.equals(Range.<C>all())) return new ImmutableRangeSet<C>(ImmutableList.of(range));
        return ImmutableRangeSet.all();
    }

    public static <C extends Comparable> ImmutableRangeSet<C> copyOf(RangeSet<C> rangeSet) {
        Preconditions.checkNotNull(rangeSet);
        if (rangeSet.isEmpty()) {
            return ImmutableRangeSet.of();
        }
        if (rangeSet.encloses(Range.<C>all())) {
            return ImmutableRangeSet.all();
        }
        if (!(rangeSet instanceof ImmutableRangeSet)) return new ImmutableRangeSet<C>(ImmutableList.copyOf(rangeSet.asRanges()));
        ImmutableRangeSet immutableRangeSet = (ImmutableRangeSet)rangeSet;
        if (immutableRangeSet.isPartialView()) return new ImmutableRangeSet<C>(ImmutableList.copyOf(rangeSet.asRanges()));
        return immutableRangeSet;
    }

    ImmutableRangeSet(ImmutableList<Range<C>> ranges) {
        this.ranges = ranges;
    }

    private ImmutableRangeSet(ImmutableList<Range<C>> ranges, ImmutableRangeSet<C> complement) {
        this.ranges = ranges;
        this.complement = complement;
    }

    @Override
    public boolean intersects(Range<C> otherRange) {
        int ceilingIndex = SortedLists.binarySearch(this.ranges, Range.<C>lowerBoundFn(), otherRange.lowerBound, Ordering.<C>natural(), (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.ANY_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        if (ceilingIndex < this.ranges.size() && ((Range)this.ranges.get((int)ceilingIndex)).isConnected(otherRange) && !((Range)this.ranges.get((int)ceilingIndex)).intersection(otherRange).isEmpty()) {
            return true;
        }
        if (ceilingIndex <= 0) return false;
        if (!((Range)this.ranges.get((int)(ceilingIndex - 1))).isConnected(otherRange)) return false;
        if (((Range)this.ranges.get((int)(ceilingIndex - 1))).intersection(otherRange).isEmpty()) return false;
        return true;
    }

    @Override
    public boolean encloses(Range<C> otherRange) {
        int index = SortedLists.binarySearch(this.ranges, Range.<C>lowerBoundFn(), otherRange.lowerBound, Ordering.<C>natural(), (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.ANY_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (index == -1) return false;
        if (!((Range)this.ranges.get((int)index)).encloses(otherRange)) return false;
        return true;
    }

    @Override
    public Range<C> rangeContaining(C value) {
        int index = SortedLists.binarySearch(this.ranges, Range.<C>lowerBoundFn(), Cut.belowValue(value), Ordering.<C>natural(), (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.ANY_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (index == -1) return null;
        Range range = (Range)this.ranges.get((int)index);
        if (!range.contains(value)) return null;
        Range range2 = range;
        return range2;
    }

    @Override
    public Range<C> span() {
        if (!this.ranges.isEmpty()) return Range.create(((Range)this.ranges.get((int)0)).lowerBound, ((Range)this.ranges.get((int)(this.ranges.size() - 1))).upperBound);
        throw new NoSuchElementException();
    }

    @Override
    public boolean isEmpty() {
        return this.ranges.isEmpty();
    }

    @Deprecated
    @Override
    public void add(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void addAll(RangeSet<C> other) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void remove(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void removeAll(RangeSet<C> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableSet<Range<C>> asRanges() {
        if (!this.ranges.isEmpty()) return new RegularImmutableSortedSet<Range<C>>(this.ranges, Range.RANGE_LEX_ORDERING);
        return ImmutableSet.of();
    }

    @Override
    public ImmutableSet<Range<C>> asDescendingSetOfRanges() {
        if (!this.ranges.isEmpty()) return new RegularImmutableSortedSet<Range<C>>(this.ranges.reverse(), Range.RANGE_LEX_ORDERING.reverse());
        return ImmutableSet.of();
    }

    @Override
    public ImmutableRangeSet<C> complement() {
        ImmutableRangeSet<C> result = this.complement;
        if (result != null) {
            return result;
        }
        if (this.ranges.isEmpty()) {
            this.complement = ImmutableRangeSet.all();
            return this.complement;
        }
        if (this.ranges.size() == 1 && ((Range)this.ranges.get((int)0)).equals(Range.<C>all())) {
            this.complement = ImmutableRangeSet.of();
            return this.complement;
        }
        ComplementRanges complementRanges = new ComplementRanges((ImmutableRangeSet)this);
        this.complement = new ImmutableRangeSet<C>(complementRanges, this);
        return this.complement;
    }

    private ImmutableList<Range<C>> intersectRanges(Range<C> range) {
        if (this.ranges.isEmpty()) return ImmutableList.of();
        if (range.isEmpty()) {
            return ImmutableList.of();
        }
        if (range.encloses(this.span())) {
            return this.ranges;
        }
        int fromIndex = range.hasLowerBound() ? SortedLists.binarySearch(this.ranges, Range.<C>upperBoundFn(), range.lowerBound, (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.FIRST_AFTER, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER) : 0;
        int toIndex = range.hasUpperBound() ? SortedLists.binarySearch(this.ranges, Range.<C>lowerBoundFn(), range.upperBound, (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.FIRST_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER) : this.ranges.size();
        int length = toIndex - fromIndex;
        if (length != 0) return new ImmutableList<Range<C>>((ImmutableRangeSet)this, (int)length, (int)fromIndex, range){
            final /* synthetic */ int val$length;
            final /* synthetic */ int val$fromIndex;
            final /* synthetic */ Range val$range;
            final /* synthetic */ ImmutableRangeSet this$0;
            {
                this.this$0 = immutableRangeSet;
                this.val$length = n;
                this.val$fromIndex = n2;
                this.val$range = range;
            }

            public int size() {
                return this.val$length;
            }

            public Range<C> get(int index) {
                Preconditions.checkElementIndex((int)index, (int)this.val$length);
                if (index == 0) return ((Range)ImmutableRangeSet.access$000((ImmutableRangeSet)this.this$0).get((int)(index + this.val$fromIndex))).intersection(this.val$range);
                if (index != this.val$length - 1) return (Range)ImmutableRangeSet.access$000((ImmutableRangeSet)this.this$0).get((int)(index + this.val$fromIndex));
                return ((Range)ImmutableRangeSet.access$000((ImmutableRangeSet)this.this$0).get((int)(index + this.val$fromIndex))).intersection(this.val$range);
            }

            boolean isPartialView() {
                return true;
            }
        };
        return ImmutableList.of();
    }

    @Override
    public ImmutableRangeSet<C> subRangeSet(Range<C> range) {
        if (this.isEmpty()) return ImmutableRangeSet.of();
        Range<C> span = this.span();
        if (range.encloses(span)) {
            return this;
        }
        if (!range.isConnected(span)) return ImmutableRangeSet.of();
        return new ImmutableRangeSet<C>(this.intersectRanges(range));
    }

    public ImmutableSortedSet<C> asSet(DiscreteDomain<C> domain) {
        Preconditions.checkNotNull(domain);
        if (this.isEmpty()) {
            return ImmutableSortedSet.of();
        }
        Range<C> span = this.span().canonical(domain);
        if (!span.hasLowerBound()) {
            throw new IllegalArgumentException((String)"Neither the DiscreteDomain nor this range set are bounded below");
        }
        if (span.hasUpperBound()) return new AsSet((ImmutableRangeSet)this, domain);
        try {
            domain.maxValue();
            return new AsSet((ImmutableRangeSet)this, domain);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException((String)"Neither the DiscreteDomain nor this range set are bounded above");
        }
    }

    boolean isPartialView() {
        return this.ranges.isPartialView();
    }

    public static <C extends Comparable<?>> Builder<C> builder() {
        return new Builder<C>();
    }

    Object writeReplace() {
        return new SerializedForm<C>(this.ranges);
    }

    static /* synthetic */ ImmutableList access$000(ImmutableRangeSet x0) {
        return x0.ranges;
    }
}

