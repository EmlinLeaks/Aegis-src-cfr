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
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMultiset<E>
extends ForwardingCollection<E>
implements Multiset<E> {
    protected ForwardingMultiset() {
    }

    @Override
    protected abstract Multiset<E> delegate();

    @Override
    public int count(Object element) {
        return this.delegate().count((Object)element);
    }

    @CanIgnoreReturnValue
    @Override
    public int add(E element, int occurrences) {
        return this.delegate().add(element, (int)occurrences);
    }

    @CanIgnoreReturnValue
    @Override
    public int remove(Object element, int occurrences) {
        return this.delegate().remove((Object)element, (int)occurrences);
    }

    @Override
    public Set<E> elementSet() {
        return this.delegate().elementSet();
    }

    @Override
    public Set<Multiset.Entry<E>> entrySet() {
        return this.delegate().entrySet();
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

    @CanIgnoreReturnValue
    @Override
    public int setCount(E element, int count) {
        return this.delegate().setCount(element, (int)count);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean setCount(E element, int oldCount, int newCount) {
        return this.delegate().setCount(element, (int)oldCount, (int)newCount);
    }

    @Override
    protected boolean standardContains(@Nullable Object object) {
        if (this.count((Object)object) <= 0) return false;
        return true;
    }

    @Override
    protected void standardClear() {
        Iterators.clear(this.entrySet().iterator());
    }

    @Beta
    protected int standardCount(@Nullable Object object) {
        Multiset.Entry<E> entry;
        Iterator<Multiset.Entry<E>> i$ = this.entrySet().iterator();
        do {
            if (!i$.hasNext()) return 0;
        } while (!Objects.equal((entry = i$.next()).getElement(), (Object)object));
        return entry.getCount();
    }

    protected boolean standardAdd(E element) {
        this.add(element, (int)1);
        return true;
    }

    @Beta
    @Override
    protected boolean standardAddAll(Collection<? extends E> elementsToAdd) {
        return Multisets.addAllImpl(this, elementsToAdd);
    }

    @Override
    protected boolean standardRemove(Object element) {
        if (this.remove((Object)element, (int)1) <= 0) return false;
        return true;
    }

    @Override
    protected boolean standardRemoveAll(Collection<?> elementsToRemove) {
        return Multisets.removeAllImpl(this, elementsToRemove);
    }

    @Override
    protected boolean standardRetainAll(Collection<?> elementsToRetain) {
        return Multisets.retainAllImpl(this, elementsToRetain);
    }

    protected int standardSetCount(E element, int count) {
        return Multisets.setCountImpl(this, element, (int)count);
    }

    protected boolean standardSetCount(E element, int oldCount, int newCount) {
        return Multisets.setCountImpl(this, element, (int)oldCount, (int)newCount);
    }

    protected Iterator<E> standardIterator() {
        return Multisets.iteratorImpl(this);
    }

    protected int standardSize() {
        return Multisets.sizeImpl(this);
    }

    protected boolean standardEquals(@Nullable Object object) {
        return Multisets.equalsImpl(this, (Object)object);
    }

    protected int standardHashCode() {
        return this.entrySet().hashCode();
    }

    @Override
    protected String standardToString() {
        return this.entrySet().toString();
    }
}

