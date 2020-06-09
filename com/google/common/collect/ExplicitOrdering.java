/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
final class ExplicitOrdering<T>
extends Ordering<T>
implements Serializable {
    final ImmutableMap<T, Integer> rankMap;
    private static final long serialVersionUID = 0L;

    ExplicitOrdering(List<T> valuesInOrder) {
        this(Maps.indexMap(valuesInOrder));
    }

    ExplicitOrdering(ImmutableMap<T, Integer> rankMap) {
        this.rankMap = rankMap;
    }

    @Override
    public int compare(T left, T right) {
        return this.rank(left) - this.rank(right);
    }

    private int rank(T value) {
        Integer rank = this.rankMap.get(value);
        if (rank != null) return rank.intValue();
        throw new Ordering.IncomparableValueException(value);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof ExplicitOrdering)) return false;
        ExplicitOrdering that = (ExplicitOrdering)object;
        return this.rankMap.equals(that.rankMap);
    }

    public int hashCode() {
        return this.rankMap.hashCode();
    }

    public String toString() {
        return "Ordering.explicit(" + this.rankMap.keySet() + ")";
    }
}

