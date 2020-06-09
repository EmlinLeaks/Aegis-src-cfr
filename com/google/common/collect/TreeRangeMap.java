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
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class TreeRangeMap<K extends Comparable, V>
implements RangeMap<K, V> {
    private final NavigableMap<Cut<K>, RangeMapEntry<K, V>> entriesByLowerBound = Maps.newTreeMap();
    private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap(){

        @Nullable
        public Object get(Comparable key) {
            return null;
        }

        @Nullable
        public Map.Entry<Range, Object> getEntry(Comparable key) {
            return null;
        }

        public Range span() {
            throw new NoSuchElementException();
        }

        public void put(Range range, Object value) {
            Preconditions.checkNotNull(range);
            throw new java.lang.IllegalArgumentException((String)("Cannot insert range " + range + " into an empty subRangeMap"));
        }

        public void putAll(RangeMap rangeMap) {
            if (rangeMap.asMapOfRanges().isEmpty()) return;
            throw new java.lang.IllegalArgumentException((String)"Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
        }

        public void clear() {
        }

        public void remove(Range range) {
            Preconditions.checkNotNull(range);
        }

        public Map<Range, Object> asMapOfRanges() {
            return java.util.Collections.emptyMap();
        }

        public Map<Range, Object> asDescendingMapOfRanges() {
            return java.util.Collections.emptyMap();
        }

        public RangeMap subRangeMap(Range range) {
            Preconditions.checkNotNull(range);
            return this;
        }
    };

    public static <K extends Comparable, V> TreeRangeMap<K, V> create() {
        return new TreeRangeMap<K, V>();
    }

    private TreeRangeMap() {
    }

    @Nullable
    @Override
    public V get(K key) {
        V v;
        Map.Entry<Range<K>, V> entry = this.getEntry(key);
        if (entry == null) {
            v = null;
            return (V)((V)v);
        }
        v = (V)entry.getValue();
        return (V)v;
    }

    @Nullable
    @Override
    public Map.Entry<Range<K>, V> getEntry(K key) {
        Map.Entry<Cut<K>, RangeMapEntry<K, V>> mapEntry = this.entriesByLowerBound.floorEntry(Cut.belowValue(key));
        if (mapEntry == null) return null;
        if (!mapEntry.getValue().contains(key)) return null;
        return (Map.Entry)mapEntry.getValue();
    }

    @Override
    public void put(Range<K> range, V value) {
        if (range.isEmpty()) return;
        Preconditions.checkNotNull(value);
        this.remove(range);
        this.entriesByLowerBound.put(range.lowerBound, new RangeMapEntry<K, V>(range, value));
    }

    @Override
    public void putAll(RangeMap<K, V> rangeMap) {
        Iterator<Map.Entry<Range<K>, V>> i$ = rangeMap.asMapOfRanges().entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<Range<K>, V> entry = i$.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.entriesByLowerBound.clear();
    }

    @Override
    public Range<K> span() {
        Map.Entry<Cut<K>, RangeMapEntry<K, V>> firstEntry = this.entriesByLowerBound.firstEntry();
        Map.Entry<Cut<K>, RangeMapEntry<K, V>> lastEntry = this.entriesByLowerBound.lastEntry();
        if (firstEntry != null) return Range.create(((Range)firstEntry.getValue().getKey()).lowerBound, ((Range)lastEntry.getValue().getKey()).upperBound);
        throw new NoSuchElementException();
    }

    private void putRangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, V value) {
        this.entriesByLowerBound.put(lowerBound, new RangeMapEntry<K, V>(lowerBound, upperBound, value));
    }

    @Override
    public void remove(Range<K> rangeToRemove) {
        Map.Entry<Cut<C>, RangeMapEntry<K, V>> mapEntryAboveToTruncate;
        RangeMapEntry<K, V> rangeMapEntry;
        RangeMapEntry<K, V> rangeMapEntry2;
        if (rangeToRemove.isEmpty()) {
            return;
        }
        Map.Entry<Cut<C>, RangeMapEntry<K, V>> mapEntryBelowToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.lowerBound);
        if (mapEntryBelowToTruncate != null && (rangeMapEntry2 = mapEntryBelowToTruncate.getValue()).getUpperBound().compareTo(rangeToRemove.lowerBound) > 0) {
            if (rangeMapEntry2.getUpperBound().compareTo(rangeToRemove.upperBound) > 0) {
                this.putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry2.getUpperBound(), mapEntryBelowToTruncate.getValue().getValue());
            }
            this.putRangeMapEntry(rangeMapEntry2.getLowerBound(), rangeToRemove.lowerBound, mapEntryBelowToTruncate.getValue().getValue());
        }
        if ((mapEntryAboveToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.upperBound)) != null && (rangeMapEntry = mapEntryAboveToTruncate.getValue()).getUpperBound().compareTo(rangeToRemove.upperBound) > 0) {
            this.putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry.getUpperBound(), mapEntryAboveToTruncate.getValue().getValue());
            this.entriesByLowerBound.remove(rangeToRemove.lowerBound);
        }
        this.entriesByLowerBound.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
    }

    @Override
    public Map<Range<K>, V> asMapOfRanges() {
        return new AsMapOfRanges((TreeRangeMap)this, this.entriesByLowerBound.values());
    }

    @Override
    public Map<Range<K>, V> asDescendingMapOfRanges() {
        return new AsMapOfRanges((TreeRangeMap)this, this.entriesByLowerBound.descendingMap().values());
    }

    @Override
    public RangeMap<K, V> subRangeMap(Range<K> subRange) {
        if (!subRange.equals(Range.<C>all())) return new SubRangeMap((TreeRangeMap)this, subRange);
        return this;
    }

    private RangeMap<K, V> emptySubRangeMap() {
        return EMPTY_SUB_RANGE_MAP;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof RangeMap)) return false;
        RangeMap rangeMap = (RangeMap)o;
        return this.asMapOfRanges().equals(rangeMap.asMapOfRanges());
    }

    @Override
    public int hashCode() {
        return this.asMapOfRanges().hashCode();
    }

    @Override
    public String toString() {
        return this.entriesByLowerBound.values().toString();
    }

    static /* synthetic */ NavigableMap access$000(TreeRangeMap x0) {
        return x0.entriesByLowerBound;
    }

    static /* synthetic */ RangeMap access$100(TreeRangeMap x0) {
        return x0.emptySubRangeMap();
    }
}

