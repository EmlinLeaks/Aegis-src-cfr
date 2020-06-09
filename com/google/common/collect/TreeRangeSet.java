/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractRangeSet;
import com.google.common.collect.Cut;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public class TreeRangeSet<C extends Comparable<?>>
extends AbstractRangeSet<C>
implements Serializable {
    @VisibleForTesting
    final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
    private transient Set<Range<C>> asRanges;
    private transient Set<Range<C>> asDescendingSetOfRanges;
    private transient RangeSet<C> complement;

    public static <C extends Comparable<?>> TreeRangeSet<C> create() {
        return new TreeRangeSet<C>(new TreeMap<Cut<C>, Range<C>>());
    }

    public static <C extends Comparable<?>> TreeRangeSet<C> create(RangeSet<C> rangeSet) {
        TreeRangeSet<C> result = TreeRangeSet.create();
        result.addAll(rangeSet);
        return result;
    }

    private TreeRangeSet(NavigableMap<Cut<C>, Range<C>> rangesByLowerCut) {
        this.rangesByLowerBound = rangesByLowerCut;
    }

    @Override
    public Set<Range<C>> asRanges() {
        AsRanges asRanges;
        AsRanges result = this.asRanges;
        if (result == null) {
            asRanges = this.asRanges = new AsRanges((TreeRangeSet)this, this.rangesByLowerBound.values());
            return asRanges;
        }
        asRanges = result;
        return asRanges;
    }

    @Override
    public Set<Range<C>> asDescendingSetOfRanges() {
        AsRanges asRanges;
        AsRanges result = this.asDescendingSetOfRanges;
        if (result == null) {
            asRanges = this.asDescendingSetOfRanges = new AsRanges((TreeRangeSet)this, this.rangesByLowerBound.descendingMap().values());
            return asRanges;
        }
        asRanges = result;
        return asRanges;
    }

    @Nullable
    @Override
    public Range<C> rangeContaining(C value) {
        Preconditions.checkNotNull(value);
        Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(Cut.belowValue(value));
        if (floorEntry == null) return null;
        if (!floorEntry.getValue().contains(value)) return null;
        return floorEntry.getValue();
    }

    @Override
    public boolean intersects(Range<C> range) {
        Preconditions.checkNotNull(range);
        Map.Entry<Cut<C>, Range<C>> ceilingEntry = this.rangesByLowerBound.ceilingEntry(range.lowerBound);
        if (ceilingEntry != null && ceilingEntry.getValue().isConnected(range) && !ceilingEntry.getValue().intersection(range).isEmpty()) {
            return true;
        }
        Map.Entry<Cut<C>, Range<C>> priorEntry = this.rangesByLowerBound.lowerEntry(range.lowerBound);
        if (priorEntry == null) return false;
        if (!priorEntry.getValue().isConnected(range)) return false;
        if (priorEntry.getValue().intersection(range).isEmpty()) return false;
        return true;
    }

    @Override
    public boolean encloses(Range<C> range) {
        Preconditions.checkNotNull(range);
        Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
        if (floorEntry == null) return false;
        if (!floorEntry.getValue().encloses(range)) return false;
        return true;
    }

    @Nullable
    private Range<C> rangeEnclosing(Range<C> range) {
        Preconditions.checkNotNull(range);
        Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
        if (floorEntry == null) return null;
        if (!floorEntry.getValue().encloses(range)) return null;
        Range<C> range2 = floorEntry.getValue();
        return range2;
    }

    @Override
    public Range<C> span() {
        Map.Entry<Cut<C>, Range<C>> firstEntry = this.rangesByLowerBound.firstEntry();
        Map.Entry<Cut<C>, Range<C>> lastEntry = this.rangesByLowerBound.lastEntry();
        if (firstEntry != null) return Range.create(firstEntry.getValue().lowerBound, lastEntry.getValue().upperBound);
        throw new NoSuchElementException();
    }

    @Override
    public void add(Range<C> rangeToAdd) {
        Map.Entry<Cut<C>, Range<C>> entryBelowUB;
        Preconditions.checkNotNull(rangeToAdd);
        if (rangeToAdd.isEmpty()) {
            return;
        }
        Cut<C> lbToAdd = rangeToAdd.lowerBound;
        Cut<C> ubToAdd = rangeToAdd.upperBound;
        Map.Entry<Cut<C>, Range<C>> entryBelowLB = this.rangesByLowerBound.lowerEntry(lbToAdd);
        if (entryBelowLB != null) {
            Range<C> rangeBelowLB = entryBelowLB.getValue();
            if (rangeBelowLB.upperBound.compareTo(lbToAdd) >= 0) {
                if (rangeBelowLB.upperBound.compareTo(ubToAdd) >= 0) {
                    ubToAdd = rangeBelowLB.upperBound;
                }
                lbToAdd = rangeBelowLB.lowerBound;
            }
        }
        if ((entryBelowUB = this.rangesByLowerBound.floorEntry(ubToAdd)) != null) {
            Range<C> rangeBelowUB = entryBelowUB.getValue();
            if (rangeBelowUB.upperBound.compareTo(ubToAdd) >= 0) {
                ubToAdd = rangeBelowUB.upperBound;
            }
        }
        this.rangesByLowerBound.subMap(lbToAdd, ubToAdd).clear();
        this.replaceRangeWithSameLowerBound(Range.create(lbToAdd, ubToAdd));
    }

    @Override
    public void remove(Range<C> rangeToRemove) {
        Map.Entry<Cut<C>, Range<C>> entryBelowUB;
        Preconditions.checkNotNull(rangeToRemove);
        if (rangeToRemove.isEmpty()) {
            return;
        }
        Map.Entry<Cut<C>, Range<C>> entryBelowLB = this.rangesByLowerBound.lowerEntry(rangeToRemove.lowerBound);
        if (entryBelowLB != null) {
            Range<C> rangeBelowLB = entryBelowLB.getValue();
            if (rangeBelowLB.upperBound.compareTo(rangeToRemove.lowerBound) >= 0) {
                if (rangeToRemove.hasUpperBound() && rangeBelowLB.upperBound.compareTo(rangeToRemove.upperBound) >= 0) {
                    this.replaceRangeWithSameLowerBound(Range.create(rangeToRemove.upperBound, rangeBelowLB.upperBound));
                }
                this.replaceRangeWithSameLowerBound(Range.create(rangeBelowLB.lowerBound, rangeToRemove.lowerBound));
            }
        }
        if ((entryBelowUB = this.rangesByLowerBound.floorEntry(rangeToRemove.upperBound)) != null) {
            Range<C> rangeBelowUB = entryBelowUB.getValue();
            if (rangeToRemove.hasUpperBound() && rangeBelowUB.upperBound.compareTo(rangeToRemove.upperBound) >= 0) {
                this.replaceRangeWithSameLowerBound(Range.create(rangeToRemove.upperBound, rangeBelowUB.upperBound));
            }
        }
        this.rangesByLowerBound.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
    }

    private void replaceRangeWithSameLowerBound(Range<C> range) {
        if (range.isEmpty()) {
            this.rangesByLowerBound.remove(range.lowerBound);
            return;
        }
        this.rangesByLowerBound.put(range.lowerBound, range);
    }

    @Override
    public RangeSet<C> complement() {
        Complement complement;
        Complement result = this.complement;
        if (result == null) {
            complement = this.complement = new Complement((TreeRangeSet)this);
            return complement;
        }
        complement = result;
        return complement;
    }

    @Override
    public RangeSet<C> subRangeSet(Range<C> view) {
        TreeRangeSet treeRangeSet;
        if (view.equals(Range.<C>all())) {
            treeRangeSet = this;
            return treeRangeSet;
        }
        treeRangeSet = new SubRangeSet((TreeRangeSet)this, view);
        return treeRangeSet;
    }

    static /* synthetic */ Range access$600(TreeRangeSet x0, Range x1) {
        return x0.rangeEnclosing(x1);
    }
}

