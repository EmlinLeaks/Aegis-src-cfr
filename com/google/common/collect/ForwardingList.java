/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingList<E>
extends ForwardingCollection<E>
implements List<E> {
    protected ForwardingList() {
    }

    @Override
    protected abstract List<E> delegate();

    @Override
    public void add(int index, E element) {
        this.delegate().add((int)index, element);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean addAll(int index, Collection<? extends E> elements) {
        return this.delegate().addAll((int)index, elements);
    }

    @Override
    public E get(int index) {
        return (E)this.delegate().get((int)index);
    }

    @Override
    public int indexOf(Object element) {
        return this.delegate().indexOf((Object)element);
    }

    @Override
    public int lastIndexOf(Object element) {
        return this.delegate().lastIndexOf((Object)element);
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.delegate().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return this.delegate().listIterator((int)index);
    }

    @CanIgnoreReturnValue
    @Override
    public E remove(int index) {
        return (E)this.delegate().remove((int)index);
    }

    @CanIgnoreReturnValue
    @Override
    public E set(int index, E element) {
        return (E)this.delegate().set((int)index, element);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return this.delegate().subList((int)fromIndex, (int)toIndex);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) return true;
        if (this.delegate().equals((Object)object)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }

    protected boolean standardAdd(E element) {
        this.add((int)this.size(), element);
        return true;
    }

    protected boolean standardAddAll(int index, Iterable<? extends E> elements) {
        return Lists.addAllImpl(this, (int)index, elements);
    }

    protected int standardIndexOf(@Nullable Object element) {
        return Lists.indexOfImpl(this, (Object)element);
    }

    protected int standardLastIndexOf(@Nullable Object element) {
        return Lists.lastIndexOfImpl(this, (Object)element);
    }

    protected Iterator<E> standardIterator() {
        return this.listIterator();
    }

    protected ListIterator<E> standardListIterator() {
        return this.listIterator((int)0);
    }

    @Beta
    protected ListIterator<E> standardListIterator(int start) {
        return Lists.listIteratorImpl(this, (int)start);
    }

    @Beta
    protected List<E> standardSubList(int fromIndex, int toIndex) {
        return Lists.subListImpl(this, (int)fromIndex, (int)toIndex);
    }

    @Beta
    protected boolean standardEquals(@Nullable Object object) {
        return Lists.equalsImpl(this, (Object)object);
    }

    @Beta
    protected int standardHashCode() {
        return Lists.hashCodeImpl(this);
    }
}

