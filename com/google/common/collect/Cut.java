/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.Cut;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.primitives.Booleans;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
abstract class Cut<C extends Comparable>
implements Comparable<Cut<C>>,
Serializable {
    final C endpoint;
    private static final long serialVersionUID = 0L;

    Cut(@Nullable C endpoint) {
        this.endpoint = endpoint;
    }

    abstract boolean isLessThan(C var1);

    abstract BoundType typeAsLowerBound();

    abstract BoundType typeAsUpperBound();

    abstract Cut<C> withLowerBoundType(BoundType var1, DiscreteDomain<C> var2);

    abstract Cut<C> withUpperBoundType(BoundType var1, DiscreteDomain<C> var2);

    abstract void describeAsLowerBound(StringBuilder var1);

    abstract void describeAsUpperBound(StringBuilder var1);

    abstract C leastValueAbove(DiscreteDomain<C> var1);

    abstract C greatestValueBelow(DiscreteDomain<C> var1);

    Cut<C> canonical(DiscreteDomain<C> domain) {
        return this;
    }

    @Override
    public int compareTo(Cut<C> that) {
        if (that == Cut.belowAll()) {
            return 1;
        }
        if (that == Cut.aboveAll()) {
            return -1;
        }
        int result = Range.compareOrThrow(this.endpoint, that.endpoint);
        if (result == 0) return Booleans.compare((boolean)(this instanceof AboveValue), (boolean)(that instanceof AboveValue));
        return result;
    }

    C endpoint() {
        return (C)this.endpoint;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Cut)) return false;
        Cut that = (Cut)obj;
        try {
            int compareResult = this.compareTo(that);
            if (compareResult != 0) return false;
            return true;
        }
        catch (ClassCastException ignored) {
            // empty catch block
        }
        return false;
    }

    static <C extends Comparable> Cut<C> belowAll() {
        return BelowAll.INSTANCE;
    }

    static <C extends Comparable> Cut<C> aboveAll() {
        return AboveAll.INSTANCE;
    }

    static <C extends Comparable> Cut<C> belowValue(C endpoint) {
        return new BelowValue<C>(endpoint);
    }

    static <C extends Comparable> Cut<C> aboveValue(C endpoint) {
        return new AboveValue<C>(endpoint);
    }
}

