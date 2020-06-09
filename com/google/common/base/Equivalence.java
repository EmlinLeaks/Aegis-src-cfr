/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.FunctionalEquivalence;
import com.google.common.base.PairwiseEquivalence;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Equivalence<T> {
    protected Equivalence() {
    }

    public final boolean equivalent(@Nullable T a, @Nullable T b) {
        if (a == b) {
            return true;
        }
        if (a == null) return false;
        if (b != null) return this.doEquivalent(a, b);
        return false;
    }

    protected abstract boolean doEquivalent(T var1, T var2);

    public final int hash(@Nullable T t) {
        if (t != null) return this.doHash(t);
        return 0;
    }

    protected abstract int doHash(T var1);

    public final <F> Equivalence<F> onResultOf(Function<F, ? extends T> function) {
        return new FunctionalEquivalence<F, T>(function, this);
    }

    public final <S extends T> Wrapper<S> wrap(@Nullable S reference) {
        return new Wrapper<T>((Equivalence)this, reference, null);
    }

    @GwtCompatible(serializable=true)
    public final <S extends T> Equivalence<Iterable<S>> pairwise() {
        return new PairwiseEquivalence<T>(this);
    }

    public final Predicate<T> equivalentTo(@Nullable T target) {
        return new EquivalentToPredicate<T>(this, target);
    }

    public static Equivalence<Object> equals() {
        return Equals.INSTANCE;
    }

    public static Equivalence<Object> identity() {
        return Identity.INSTANCE;
    }
}

