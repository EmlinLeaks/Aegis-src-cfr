/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Cut;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public final class Range<C extends Comparable>
implements Predicate<C>,
Serializable {
    private static final Function<Range, Cut> LOWER_BOUND_FN = new Function<Range, Cut>(){

        public Cut apply(Range range) {
            return range.lowerBound;
        }
    };
    private static final Function<Range, Cut> UPPER_BOUND_FN = new Function<Range, Cut>(){

        public Cut apply(Range range) {
            return range.upperBound;
        }
    };
    static final Ordering<Range<?>> RANGE_LEX_ORDERING = new RangeLexOrdering(null);
    private static final Range<Comparable> ALL = new Range<C>(Cut.<C>belowAll(), Cut.<C>aboveAll());
    final Cut<C> lowerBound;
    final Cut<C> upperBound;
    private static final long serialVersionUID = 0L;

    static <C extends Comparable<?>> Function<Range<C>, Cut<C>> lowerBoundFn() {
        return LOWER_BOUND_FN;
    }

    static <C extends Comparable<?>> Function<Range<C>, Cut<C>> upperBoundFn() {
        return UPPER_BOUND_FN;
    }

    static <C extends Comparable<?>> Range<C> create(Cut<C> lowerBound, Cut<C> upperBound) {
        return new Range<C>(lowerBound, upperBound);
    }

    public static <C extends Comparable<?>> Range<C> open(C lower, C upper) {
        return Range.create(Cut.aboveValue(lower), Cut.belowValue(upper));
    }

    public static <C extends Comparable<?>> Range<C> closed(C lower, C upper) {
        return Range.create(Cut.belowValue(lower), Cut.aboveValue(upper));
    }

    public static <C extends Comparable<?>> Range<C> closedOpen(C lower, C upper) {
        return Range.create(Cut.belowValue(lower), Cut.belowValue(upper));
    }

    public static <C extends Comparable<?>> Range<C> openClosed(C lower, C upper) {
        return Range.create(Cut.aboveValue(lower), Cut.aboveValue(upper));
    }

    public static <C extends Comparable<?>> Range<C> range(C lower, BoundType lowerType, C upper, BoundType upperType) {
        Preconditions.checkNotNull(lowerType);
        Preconditions.checkNotNull(upperType);
        Cut<C> lowerBound = lowerType == BoundType.OPEN ? Cut.aboveValue(lower) : Cut.belowValue(lower);
        Cut<C> upperBound = upperType == BoundType.OPEN ? Cut.belowValue(upper) : Cut.aboveValue(upper);
        return Range.create(lowerBound, upperBound);
    }

    public static <C extends Comparable<?>> Range<C> lessThan(C endpoint) {
        return Range.create(Cut.<C>belowAll(), Cut.belowValue(endpoint));
    }

    public static <C extends Comparable<?>> Range<C> atMost(C endpoint) {
        return Range.create(Cut.<C>belowAll(), Cut.aboveValue(endpoint));
    }

    public static <C extends Comparable<?>> Range<C> upTo(C endpoint, BoundType boundType) {
        switch (3.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
            case 1: {
                return Range.lessThan(endpoint);
            }
            case 2: {
                return Range.atMost(endpoint);
            }
        }
        throw new AssertionError();
    }

    public static <C extends Comparable<?>> Range<C> greaterThan(C endpoint) {
        return Range.create(Cut.aboveValue(endpoint), Cut.<C>aboveAll());
    }

    public static <C extends Comparable<?>> Range<C> atLeast(C endpoint) {
        return Range.create(Cut.belowValue(endpoint), Cut.<C>aboveAll());
    }

    public static <C extends Comparable<?>> Range<C> downTo(C endpoint, BoundType boundType) {
        switch (3.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
            case 1: {
                return Range.greaterThan(endpoint);
            }
            case 2: {
                return Range.atLeast(endpoint);
            }
        }
        throw new AssertionError();
    }

    public static <C extends Comparable<?>> Range<C> all() {
        return ALL;
    }

    public static <C extends Comparable<?>> Range<C> singleton(C value) {
        return Range.closed(value, value);
    }

    public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> values) {
        Comparable min;
        Preconditions.checkNotNull(values);
        if (values instanceof ContiguousSet) {
            return ((ContiguousSet)values).range();
        }
        Iterator<C> valueIterator = values.iterator();
        Comparable max = min = (Comparable)Preconditions.checkNotNull(valueIterator.next());
        while (valueIterator.hasNext()) {
            Comparable value = (Comparable)Preconditions.checkNotNull(valueIterator.next());
            min = Ordering.natural().min(min, value);
            max = Ordering.natural().max(max, value);
        }
        return Range.closed(min, max);
    }

    private Range(Cut<C> lowerBound, Cut<C> upperBound) {
        this.lowerBound = Preconditions.checkNotNull(lowerBound);
        this.upperBound = Preconditions.checkNotNull(upperBound);
        if (lowerBound.compareTo(upperBound) > 0) throw new IllegalArgumentException((String)("Invalid range: " + Range.toString(lowerBound, upperBound)));
        if (lowerBound == Cut.aboveAll()) throw new IllegalArgumentException((String)("Invalid range: " + Range.toString(lowerBound, upperBound)));
        if (upperBound != Cut.belowAll()) return;
        throw new IllegalArgumentException((String)("Invalid range: " + Range.toString(lowerBound, upperBound)));
    }

    public boolean hasLowerBound() {
        if (this.lowerBound == Cut.belowAll()) return false;
        return true;
    }

    public C lowerEndpoint() {
        return (C)this.lowerBound.endpoint();
    }

    public BoundType lowerBoundType() {
        return this.lowerBound.typeAsLowerBound();
    }

    public boolean hasUpperBound() {
        if (this.upperBound == Cut.aboveAll()) return false;
        return true;
    }

    public C upperEndpoint() {
        return (C)this.upperBound.endpoint();
    }

    public BoundType upperBoundType() {
        return this.upperBound.typeAsUpperBound();
    }

    public boolean isEmpty() {
        return this.lowerBound.equals(this.upperBound);
    }

    public boolean contains(C value) {
        Preconditions.checkNotNull(value);
        if (!this.lowerBound.isLessThan(value)) return false;
        if (this.upperBound.isLessThan(value)) return false;
        return true;
    }

    @Deprecated
    @Override
    public boolean apply(C input) {
        return this.contains(input);
    }

    public boolean containsAll(Iterable<? extends C> values) {
        Comparable value;
        if (Iterables.isEmpty(values)) {
            return true;
        }
        if (values instanceof SortedSet) {
            SortedSet<C> set = Range.cast(values);
            Comparator<? extends C> comparator = set.comparator();
            if (Ordering.natural().equals(comparator) || comparator == null) {
                if (!this.contains((Comparable)set.first())) return false;
                if (!this.contains((Comparable)set.last())) return false;
                return true;
            }
        }
        Iterator<C> i$ = values.iterator();
        do {
            if (!i$.hasNext()) return true;
        } while (this.contains(value = (Comparable)i$.next()));
        return false;
    }

    public boolean encloses(Range<C> other) {
        if (this.lowerBound.compareTo(other.lowerBound) > 0) return false;
        if (this.upperBound.compareTo(other.upperBound) < 0) return false;
        return true;
    }

    public boolean isConnected(Range<C> other) {
        if (this.lowerBound.compareTo(other.upperBound) > 0) return false;
        if (other.lowerBound.compareTo(this.upperBound) > 0) return false;
        return true;
    }

    public Range<C> intersection(Range<C> connectedRange) {
        int lowerCmp = this.lowerBound.compareTo(connectedRange.lowerBound);
        int upperCmp = this.upperBound.compareTo(connectedRange.upperBound);
        if (lowerCmp >= 0 && upperCmp <= 0) {
            return this;
        }
        if (lowerCmp <= 0 && upperCmp >= 0) {
            return connectedRange;
        }
        Cut<C> newLower = lowerCmp >= 0 ? this.lowerBound : connectedRange.lowerBound;
        Cut<C> newUpper = upperCmp <= 0 ? this.upperBound : connectedRange.upperBound;
        return Range.create(newLower, newUpper);
    }

    public Range<C> span(Range<C> other) {
        int lowerCmp = this.lowerBound.compareTo(other.lowerBound);
        int upperCmp = this.upperBound.compareTo(other.upperBound);
        if (lowerCmp <= 0 && upperCmp >= 0) {
            return this;
        }
        if (lowerCmp >= 0 && upperCmp <= 0) {
            return other;
        }
        Cut<C> newLower = lowerCmp <= 0 ? this.lowerBound : other.lowerBound;
        Cut<C> newUpper = upperCmp >= 0 ? this.upperBound : other.upperBound;
        return Range.create(newLower, newUpper);
    }

    public Range<C> canonical(DiscreteDomain<C> domain) {
        Range range;
        Preconditions.checkNotNull(domain);
        Cut<C> lower = this.lowerBound.canonical(domain);
        Cut<C> upper = this.upperBound.canonical(domain);
        if (lower == this.lowerBound && upper == this.upperBound) {
            range = this;
            return range;
        }
        range = Range.create(lower, upper);
        return range;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Range)) return false;
        Range other = (Range)object;
        if (!this.lowerBound.equals(other.lowerBound)) return false;
        if (!this.upperBound.equals(other.upperBound)) return false;
        return true;
    }

    public int hashCode() {
        return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
    }

    public String toString() {
        return Range.toString(this.lowerBound, this.upperBound);
    }

    private static String toString(Cut<?> lowerBound, Cut<?> upperBound) {
        StringBuilder sb = new StringBuilder((int)16);
        lowerBound.describeAsLowerBound((StringBuilder)sb);
        sb.append((String)"..");
        upperBound.describeAsUpperBound((StringBuilder)sb);
        return sb.toString();
    }

    private static <T> SortedSet<T> cast(Iterable<T> iterable) {
        return (SortedSet)iterable;
    }

    Object readResolve() {
        if (!this.equals(ALL)) return this;
        return Range.all();
    }

    static int compareOrThrow(Comparable left, Comparable right) {
        return left.compareTo(right);
    }
}

