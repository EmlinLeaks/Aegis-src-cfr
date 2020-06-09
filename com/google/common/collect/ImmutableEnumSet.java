/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableEnumSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

@GwtCompatible(serializable=true, emulated=true)
final class ImmutableEnumSet<E extends Enum<E>>
extends ImmutableSet<E> {
    private final transient EnumSet<E> delegate;
    @LazyInit
    private transient int hashCode;

    static ImmutableSet asImmutable(EnumSet set) {
        switch (set.size()) {
            case 0: {
                return ImmutableSet.of();
            }
            case 1: {
                return ImmutableSet.of(Iterables.getOnlyElement(set));
            }
        }
        return new ImmutableEnumSet<E>(set);
    }

    private ImmutableEnumSet(EnumSet<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.unmodifiableIterator(this.delegate.iterator());
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean contains(Object object) {
        return this.delegate.contains((Object)object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (!(collection instanceof ImmutableEnumSet)) return this.delegate.containsAll(collection);
        collection = ((ImmutableEnumSet)collection).delegate;
        return this.delegate.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ImmutableEnumSet)) return this.delegate.equals(object);
        object = ((ImmutableEnumSet)object).delegate;
        return this.delegate.equals(object);
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }

    @Override
    public int hashCode() {
        int n;
        int result = this.hashCode;
        if (result == 0) {
            n = this.hashCode = this.delegate.hashCode();
            return n;
        }
        n = result;
        return n;
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    Object writeReplace() {
        return new EnumSerializedForm<E>(this.delegate);
    }
}

