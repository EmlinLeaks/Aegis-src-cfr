/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.Weak
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableListIterator;
import com.google.j2objc.annotations.Weak;
import java.util.ListIterator;

@GwtCompatible(emulated=true)
class RegularImmutableAsList<E>
extends ImmutableAsList<E> {
    @Weak
    private final ImmutableCollection<E> delegate;
    private final ImmutableList<? extends E> delegateList;

    RegularImmutableAsList(ImmutableCollection<E> delegate, ImmutableList<? extends E> delegateList) {
        this.delegate = delegate;
        this.delegateList = delegateList;
    }

    RegularImmutableAsList(ImmutableCollection<E> delegate, Object[] array) {
        this(delegate, ImmutableList.asImmutableList((Object[])array));
    }

    @Override
    ImmutableCollection<E> delegateCollection() {
        return this.delegate;
    }

    ImmutableList<? extends E> delegateList() {
        return this.delegateList;
    }

    @Override
    public UnmodifiableListIterator<E> listIterator(int index) {
        return this.delegateList.listIterator((int)index);
    }

    @GwtIncompatible
    @Override
    int copyIntoArray(Object[] dst, int offset) {
        return this.delegateList.copyIntoArray((Object[])dst, (int)offset);
    }

    @Override
    public E get(int index) {
        return (E)this.delegateList.get((int)index);
    }
}

