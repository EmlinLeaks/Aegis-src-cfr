/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.RegularImmutableAsList;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedIterable;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class ImmutableSortedAsList<E>
extends RegularImmutableAsList<E>
implements SortedIterable<E> {
    ImmutableSortedAsList(ImmutableSortedSet<E> backingSet, ImmutableList<E> backingList) {
        super(backingSet, backingList);
    }

    @Override
    ImmutableSortedSet<E> delegateCollection() {
        return (ImmutableSortedSet)super.delegateCollection();
    }

    @Override
    public Comparator<? super E> comparator() {
        return ((ImmutableSortedSet)this.delegateCollection()).comparator();
    }

    @GwtIncompatible
    @Override
    public int indexOf(@Nullable Object target) {
        int index = ((ImmutableSortedSet)this.delegateCollection()).indexOf((Object)target);
        if (index < 0) return -1;
        if (!this.get((int)index).equals((Object)target)) return -1;
        int n = index;
        return n;
    }

    @GwtIncompatible
    @Override
    public int lastIndexOf(@Nullable Object target) {
        return this.indexOf((Object)target);
    }

    @Override
    public boolean contains(Object target) {
        if (this.indexOf((Object)target) < 0) return false;
        return true;
    }

    @GwtIncompatible
    @Override
    ImmutableList<E> subListUnchecked(int fromIndex, int toIndex) {
        ImmutableList<E> parentSubList = super.subListUnchecked((int)fromIndex, (int)toIndex);
        return new RegularImmutableSortedSet<E>(parentSubList, this.comparator()).asList();
    }
}

