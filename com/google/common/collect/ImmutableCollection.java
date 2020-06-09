/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.RegularImmutableAsList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public abstract class ImmutableCollection<E>
extends AbstractCollection<E>
implements Serializable {
    ImmutableCollection() {
    }

    @Override
    public abstract UnmodifiableIterator<E> iterator();

    @Override
    public final Object[] toArray() {
        int size = this.size();
        if (size == 0) {
            return ObjectArrays.EMPTY_ARRAY;
        }
        Object[] result = new Object[size];
        this.copyIntoArray((Object[])result, (int)0);
        return result;
    }

    @CanIgnoreReturnValue
    @Override
    public final <T> T[] toArray(T[] other) {
        Preconditions.checkNotNull(other);
        int size = this.size();
        if (other.length < size) {
            other = ObjectArrays.newArray(other, (int)size);
        } else if (other.length > size) {
            other[size] = null;
        }
        this.copyIntoArray((Object[])other, (int)0);
        return other;
    }

    @Override
    public abstract boolean contains(@Nullable Object var1);

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean addAll(Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean removeAll(Collection<?> oldElements) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean retainAll(Collection<?> elementsToKeep) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    public ImmutableList<E> asList() {
        switch (this.size()) {
            case 0: {
                return ImmutableList.of();
            }
            case 1: {
                return ImmutableList.of(this.iterator().next());
            }
        }
        return new RegularImmutableAsList<E>(this, (Object[])this.toArray());
    }

    abstract boolean isPartialView();

    @CanIgnoreReturnValue
    int copyIntoArray(Object[] dst, int offset) {
        Iterator i$ = this.iterator();
        while (i$.hasNext()) {
            E e = i$.next();
            dst[offset++] = e;
        }
        return offset;
    }

    Object writeReplace() {
        return new ImmutableList.SerializedForm((Object[])this.toArray());
    }
}

