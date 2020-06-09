/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Cut;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedLists;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public class ImmutableRangeMap<K extends Comparable<?>, V>
implements RangeMap<K, V>,
Serializable {
    private static final ImmutableRangeMap<Comparable<?>, Object> EMPTY = new ImmutableRangeMap<K, E>(ImmutableList.<Range<K>>of(), ImmutableList.<E>of());
    private final transient ImmutableList<Range<K>> ranges;
    private final transient ImmutableList<V> values;
    private static final long serialVersionUID = 0L;

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of() {
        return EMPTY;
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of(Range<K> range, V value) {
        return new ImmutableRangeMap<K, V>(ImmutableList.of(range), ImmutableList.of(value));
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> copyOf(RangeMap<K, ? extends V> rangeMap) {
        if (rangeMap instanceof ImmutableRangeMap) {
            return (ImmutableRangeMap)rangeMap;
        }
        Map<Range<K>, V> map = rangeMap.asMapOfRanges();
        ImmutableList.Builder<E> rangesBuilder = new ImmutableList.Builder<E>((int)map.size());
        ImmutableList.Builder<E> valuesBuilder = new ImmutableList.Builder<E>((int)map.size());
        Iterator<Map.Entry<Range<K>, V>> i$ = map.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<Range<K>, V> entry = i$.next();
            rangesBuilder.add(entry.getKey());
            valuesBuilder.add(entry.getValue());
        }
        return new ImmutableRangeMap<K, V>(rangesBuilder.build(), valuesBuilder.build());
    }

    public static <K extends Comparable<?>, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }

    ImmutableRangeMap(ImmutableList<Range<K>> ranges, ImmutableList<V> values) {
        this.ranges = ranges;
        this.values = values;
    }

    @Nullable
    @Override
    public V get(K key) {
        V v;
        int index = SortedLists.binarySearch(this.ranges, Range.<C>lowerBoundFn(), Cut.belowValue(key), (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.ANY_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (index == -1) {
            return (V)null;
        }
        Range range = (Range)this.ranges.get((int)index);
        if (range.contains(key)) {
            v = (V)this.values.get((int)index);
            return (V)((V)v);
        }
        v = null;
        return (V)v;
    }

    @Nullable
    @Override
    public Map.Entry<Range<K>, V> getEntry(K key) {
        int index = SortedLists.binarySearch(this.ranges, Range.<C>lowerBoundFn(), Cut.belowValue(key), (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.ANY_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (index == -1) {
            return null;
        }
        Range range = (Range)this.ranges.get((int)index);
        if (!range.contains(key)) return null;
        Map.Entry<Range, E> entry = Maps.immutableEntry(range, this.values.get((int)index));
        return entry;
    }

    @Override
    public Range<K> span() {
        if (this.ranges.isEmpty()) {
            throw new NoSuchElementException();
        }
        Range firstRange = (Range)this.ranges.get((int)0);
        Range lastRange = (Range)this.ranges.get((int)(this.ranges.size() - 1));
        return Range.create(firstRange.lowerBound, lastRange.upperBound);
    }

    @Deprecated
    @Override
    public void put(Range<K> range, V value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void putAll(RangeMap<K, V> rangeMap) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void remove(Range<K> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableMap<Range<K>, V> asMapOfRanges() {
        if (this.ranges.isEmpty()) {
            return ImmutableMap.of();
        }
        RegularImmutableSortedSet<Range<?>> rangeSet = new RegularImmutableSortedSet<Range<?>>(this.ranges, Range.RANGE_LEX_ORDERING);
        return new ImmutableSortedMap<Range<?>, V>(rangeSet, this.values);
    }

    @Override
    public ImmutableMap<Range<K>, V> asDescendingMapOfRanges() {
        if (this.ranges.isEmpty()) {
            return ImmutableMap.of();
        }
        RegularImmutableSortedSet<Range<K>> rangeSet = new RegularImmutableSortedSet<Range<K>>(this.ranges.reverse(), Range.RANGE_LEX_ORDERING.reverse());
        return new ImmutableSortedMap<Range<K>, V>(rangeSet, this.values.reverse());
    }

    @Override
    public ImmutableRangeMap<K, V> subRangeMap(Range<K> range) {
        int upperIndex;
        if (Preconditions.checkNotNull(range).isEmpty()) {
            return ImmutableRangeMap.of();
        }
        if (this.ranges.isEmpty()) return this;
        if (range.encloses(this.span())) {
            return this;
        }
        int lowerIndex = SortedLists.binarySearch(this.ranges, Range.<C>upperBoundFn(), range.lowerBound, (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.FIRST_AFTER, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        if (lowerIndex >= (upperIndex = SortedLists.binarySearch(this.ranges, Range.<C>lowerBoundFn(), range.upperBound, (SortedLists.KeyPresentBehavior)SortedLists.KeyPresentBehavior.ANY_PRESENT, (SortedLists.KeyAbsentBehavior)SortedLists.KeyAbsentBehavior.NEXT_HIGHER))) {
            return ImmutableRangeMap.of();
        }
        int off = lowerIndex;
        int len = upperIndex - lowerIndex;
        ImmutableList<Range<K>> subRanges = new ImmutableList<Range<K>>((ImmutableRangeMap)this, (int)len, (int)off, range){
            final /* synthetic */ int val$len;
            final /* synthetic */ int val$off;
            final /* synthetic */ Range val$range;
            final /* synthetic */ ImmutableRangeMap this$0;
            {
                this.this$0 = immutableRangeMap;
                this.val$len = n;
                this.val$off = n2;
                this.val$range = range;
            }

            public int size() {
                return this.val$len;
            }

            public Range<K> get(int index) {
                Preconditions.checkElementIndex((int)index, (int)this.val$len);
                if (index == 0) return ((Range)ImmutableRangeMap.access$000((ImmutableRangeMap)this.this$0).get((int)(index + this.val$off))).intersection(this.val$range);
                if (index != this.val$len - 1) return (Range)ImmutableRangeMap.access$000((ImmutableRangeMap)this.this$0).get((int)(index + this.val$off));
                return ((Range)ImmutableRangeMap.access$000((ImmutableRangeMap)this.this$0).get((int)(index + this.val$off))).intersection(this.val$range);
            }

            boolean isPartialView() {
                return true;
            }
        };
        ImmutableRangeMap outer = this;
        return new ImmutableRangeMap<K, V>((ImmutableRangeMap)this, (ImmutableList)subRanges, (ImmutableList)this.values.subList((int)lowerIndex, (int)upperIndex), range, (ImmutableRangeMap)outer){
            final /* synthetic */ Range val$range;
            final /* synthetic */ ImmutableRangeMap val$outer;
            final /* synthetic */ ImmutableRangeMap this$0;
            {
                this.this$0 = immutableRangeMap;
                this.val$range = range;
                this.val$outer = immutableRangeMap2;
                super(x0, x1);
            }

            public ImmutableRangeMap<K, V> subRangeMap(Range<K> subRange) {
                if (!this.val$range.isConnected(subRange)) return ImmutableRangeMap.of();
                return this.val$outer.subRangeMap(subRange.intersection(this.val$range));
            }
        };
    }

    @Override
    public int hashCode() {
        return ((ImmutableMap)this.asMapOfRanges()).hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof RangeMap)) return false;
        RangeMap rangeMap = (RangeMap)o;
        return ((ImmutableMap)this.asMapOfRanges()).equals(rangeMap.asMapOfRanges());
    }

    @Override
    public String toString() {
        return ((ImmutableMap)this.asMapOfRanges()).toString();
    }

    Object writeReplace() {
        return new SerializedForm<K, V>(this.asMapOfRanges());
    }

    static /* synthetic */ ImmutableList access$000(ImmutableRangeMap x0) {
        return x0.ranges;
    }
}

