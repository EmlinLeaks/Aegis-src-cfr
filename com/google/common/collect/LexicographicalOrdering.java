/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
final class LexicographicalOrdering<T>
extends Ordering<Iterable<T>>
implements Serializable {
    final Comparator<? super T> elementOrder;
    private static final long serialVersionUID = 0L;

    LexicographicalOrdering(Comparator<? super T> elementOrder) {
        this.elementOrder = elementOrder;
    }

    @Override
    public int compare(Iterable<T> leftIterable, Iterable<T> rightIterable) {
        int result;
        Iterator<T> left = leftIterable.iterator();
        Iterator<T> right = rightIterable.iterator();
        do {
            if (!left.hasNext()) {
                if (!right.hasNext()) return 0;
                return -1;
            }
            if (right.hasNext()) continue;
            return 1;
        } while ((result = this.elementOrder.compare(left.next(), right.next())) == 0);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof LexicographicalOrdering)) return false;
        LexicographicalOrdering that = (LexicographicalOrdering)object;
        return this.elementOrder.equals(that.elementOrder);
    }

    public int hashCode() {
        return this.elementOrder.hashCode() ^ 2075626741;
    }

    public String toString() {
        return this.elementOrder + ".lexicographical()";
    }
}

