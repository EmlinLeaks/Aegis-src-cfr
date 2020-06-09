/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtIncompatible
abstract class AbstractRangeSet<C extends Comparable>
implements RangeSet<C> {
    AbstractRangeSet() {
    }

    @Override
    public boolean contains(C value) {
        if (this.rangeContaining(value) == null) return false;
        return true;
    }

    @Override
    public abstract Range<C> rangeContaining(C var1);

    @Override
    public boolean isEmpty() {
        return this.asRanges().isEmpty();
    }

    @Override
    public void add(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.remove(Range.<C>all());
    }

    @Override
    public boolean enclosesAll(RangeSet<C> other) {
        Range<C> range;
        Iterator<Range<C>> i$ = other.asRanges().iterator();
        do {
            if (!i$.hasNext()) return true;
        } while (this.encloses(range = i$.next()));
        return false;
    }

    @Override
    public void addAll(RangeSet<C> other) {
        Iterator<Range<C>> i$ = other.asRanges().iterator();
        while (i$.hasNext()) {
            Range<C> range = i$.next();
            this.add(range);
        }
    }

    @Override
    public void removeAll(RangeSet<C> other) {
        Iterator<Range<C>> i$ = other.asRanges().iterator();
        while (i$.hasNext()) {
            Range<C> range = i$.next();
            this.remove(range);
        }
    }

    @Override
    public boolean intersects(Range<C> otherRange) {
        if (this.subRangeSet(otherRange).isEmpty()) return false;
        return true;
    }

    @Override
    public abstract boolean encloses(Range<C> var1);

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RangeSet)) return false;
        RangeSet other = (RangeSet)obj;
        return this.asRanges().equals(other.asRanges());
    }

    @Override
    public final int hashCode() {
        return this.asRanges().hashCode();
    }

    @Override
    public final String toString() {
        return this.asRanges().toString();
    }
}

