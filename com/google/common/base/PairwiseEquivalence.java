/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
final class PairwiseEquivalence<T>
extends Equivalence<Iterable<T>>
implements Serializable {
    final Equivalence<? super T> elementEquivalence;
    private static final long serialVersionUID = 1L;

    PairwiseEquivalence(Equivalence<? super T> elementEquivalence) {
        this.elementEquivalence = Preconditions.checkNotNull(elementEquivalence);
    }

    @Override
    protected boolean doEquivalent(Iterable<T> iterableA, Iterable<T> iterableB) {
        Iterator<T> iteratorA = iterableA.iterator();
        Iterator<T> iteratorB = iterableB.iterator();
        while (iteratorA.hasNext() && iteratorB.hasNext()) {
            if (this.elementEquivalence.equivalent(iteratorA.next(), iteratorB.next())) continue;
            return false;
        }
        if (iteratorA.hasNext()) return false;
        if (iteratorB.hasNext()) return false;
        return true;
    }

    @Override
    protected int doHash(Iterable<T> iterable) {
        int hash = 78721;
        Iterator<T> i$ = iterable.iterator();
        while (i$.hasNext()) {
            T element = i$.next();
            hash = hash * 24943 + this.elementEquivalence.hash(element);
        }
        return hash;
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof PairwiseEquivalence)) return false;
        PairwiseEquivalence that = (PairwiseEquivalence)object;
        return this.elementEquivalence.equals(that.elementEquivalence);
    }

    public int hashCode() {
        return this.elementEquivalence.hashCode() ^ 1185147655;
    }

    public String toString() {
        return this.elementEquivalence + ".pairwise()";
    }
}

