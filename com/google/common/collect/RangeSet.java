/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Range;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public interface RangeSet<C extends Comparable> {
    public boolean contains(C var1);

    public Range<C> rangeContaining(C var1);

    public boolean intersects(Range<C> var1);

    public boolean encloses(Range<C> var1);

    public boolean enclosesAll(RangeSet<C> var1);

    public boolean isEmpty();

    public Range<C> span();

    public Set<Range<C>> asRanges();

    public Set<Range<C>> asDescendingSetOfRanges();

    public RangeSet<C> complement();

    public RangeSet<C> subRangeSet(Range<C> var1);

    public void add(Range<C> var1);

    public void remove(Range<C> var1);

    public void clear();

    public void addAll(RangeSet<C> var1);

    public void removeAll(RangeSet<C> var1);

    public boolean equals(@Nullable Object var1);

    public int hashCode();

    public String toString();
}

