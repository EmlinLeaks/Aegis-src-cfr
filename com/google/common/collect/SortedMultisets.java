/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Multiset;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class SortedMultisets {
    private SortedMultisets() {
    }

    private static <E> E getElementOrThrow(Multiset.Entry<E> entry) {
        if (entry != null) return (E)entry.getElement();
        throw new NoSuchElementException();
    }

    private static <E> E getElementOrNull(@Nullable Multiset.Entry<E> entry) {
        E e;
        if (entry == null) {
            e = null;
            return (E)((E)e);
        }
        e = (E)entry.getElement();
        return (E)e;
    }

    static /* synthetic */ Object access$000(Multiset.Entry x0) {
        return SortedMultisets.getElementOrThrow(x0);
    }

    static /* synthetic */ Object access$100(Multiset.Entry x0) {
        return SortedMultisets.getElementOrNull(x0);
    }
}

