/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.Iterators;
import com.google.common.collect.ObjectArrays;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingCollection<E>
extends ForwardingObject
implements Collection<E> {
    protected ForwardingCollection() {
    }

    @Override
    protected abstract Collection<E> delegate();

    @Override
    public Iterator<E> iterator() {
        return this.delegate().iterator();
    }

    @Override
    public int size() {
        return this.delegate().size();
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeAll(Collection<?> collection) {
        return this.delegate().removeAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        return this.delegate().contains((Object)object);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean add(E element) {
        return this.delegate().add(element);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean remove(Object object) {
        return this.delegate().remove((Object)object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.delegate().containsAll(collection);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return this.delegate().addAll(collection);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean retainAll(Collection<?> collection) {
        return this.delegate().retainAll(collection);
    }

    @Override
    public void clear() {
        this.delegate().clear();
    }

    @Override
    public Object[] toArray() {
        return this.delegate().toArray();
    }

    @CanIgnoreReturnValue
    @Override
    public <T> T[] toArray(T[] array) {
        return this.delegate().toArray(array);
    }

    protected boolean standardContains(@Nullable Object object) {
        return Iterators.contains(this.iterator(), (Object)object);
    }

    protected boolean standardContainsAll(Collection<?> collection) {
        return Collections2.containsAllImpl(this, collection);
    }

    protected boolean standardAddAll(Collection<? extends E> collection) {
        return Iterators.addAll(this, collection.iterator());
    }

    protected boolean standardRemove(@Nullable Object object) {
        Iterator<E> iterator = this.iterator();
        do {
            if (!iterator.hasNext()) return false;
        } while (!Objects.equal(iterator.next(), (Object)object));
        iterator.remove();
        return true;
    }

    protected boolean standardRemoveAll(Collection<?> collection) {
        return Iterators.removeAll(this.iterator(), collection);
    }

    protected boolean standardRetainAll(Collection<?> collection) {
        return Iterators.retainAll(this.iterator(), collection);
    }

    protected void standardClear() {
        Iterators.clear(this.iterator());
    }

    protected boolean standardIsEmpty() {
        if (this.iterator().hasNext()) return false;
        return true;
    }

    protected String standardToString() {
        return Collections2.toStringImpl(this);
    }

    protected Object[] standardToArray() {
        Object[] newArray = new Object[this.size()];
        return this.toArray(newArray);
    }

    protected <T> T[] standardToArray(T[] array) {
        return ObjectArrays.toArrayImpl(this, array);
    }
}

