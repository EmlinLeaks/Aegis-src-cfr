/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.common.collect.ReverseNaturalOrdering;
import java.io.Serializable;

@GwtCompatible(serializable=true)
final class NaturalOrdering
extends Ordering<Comparable>
implements Serializable {
    static final NaturalOrdering INSTANCE = new NaturalOrdering();
    private transient Ordering<Comparable> nullsFirst;
    private transient Ordering<Comparable> nullsLast;
    private static final long serialVersionUID = 0L;

    @Override
    public int compare(Comparable left, Comparable right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        return left.compareTo(right);
    }

    @Override
    public <S extends Comparable> Ordering<S> nullsFirst() {
        Ordering<Comparable<Object>> result = this.nullsFirst;
        if (result != null) return result;
        this.nullsFirst = super.nullsFirst();
        return this.nullsFirst;
    }

    @Override
    public <S extends Comparable> Ordering<S> nullsLast() {
        Ordering<Comparable<Object>> result = this.nullsLast;
        if (result != null) return result;
        this.nullsLast = super.nullsLast();
        return this.nullsLast;
    }

    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return ReverseNaturalOrdering.INSTANCE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public String toString() {
        return "Ordering.natural()";
    }

    private NaturalOrdering() {
    }
}

