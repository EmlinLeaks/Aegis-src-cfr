/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.RegularImmutableAsList;
import com.google.common.collect.RegularImmutableMultiset;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableMultiset<E>
extends ImmutableCollection<E>
implements Multiset<E> {
    @LazyInit
    private transient ImmutableList<E> asList;
    @LazyInit
    private transient ImmutableSet<Multiset.Entry<E>> entrySet;

    public static <E> ImmutableMultiset<E> of() {
        return RegularImmutableMultiset.EMPTY;
    }

    public static <E> ImmutableMultiset<E> of(E element) {
        return ImmutableMultiset.copyFromElements(element);
    }

    public static <E> ImmutableMultiset<E> of(E e1, E e2) {
        return ImmutableMultiset.copyFromElements(e1, e2);
    }

    public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3) {
        return ImmutableMultiset.copyFromElements(e1, e2, e3);
    }

    public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4) {
        return ImmutableMultiset.copyFromElements(e1, e2, e3, e4);
    }

    public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5) {
        return ImmutableMultiset.copyFromElements(e1, e2, e3, e4, e5);
    }

    public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E ... others) {
        return ((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)new Builder<E>().add(e1)).add(e2)).add(e3)).add(e4)).add(e5)).add(e6)).add((Object[])others)).build();
    }

    public static <E> ImmutableMultiset<E> copyOf(E[] elements) {
        return ImmutableMultiset.copyFromElements(elements);
    }

    public static <E> ImmutableMultiset<E> copyOf(Iterable<? extends E> elements) {
        ImmutableMultiset result;
        if (elements instanceof ImmutableMultiset && !(result = (ImmutableMultiset)elements).isPartialView()) {
            return result;
        }
        Multiset<E> multiset = elements instanceof Multiset ? Multisets.cast(elements) : LinkedHashMultiset.create(elements);
        return ImmutableMultiset.copyFromEntries(multiset.entrySet());
    }

    private static <E> ImmutableMultiset<E> copyFromElements(E ... elements) {
        LinkedHashMultiset<E> multiset = LinkedHashMultiset.create();
        Collections.addAll(multiset, elements);
        return ImmutableMultiset.copyFromEntries(multiset.entrySet());
    }

    static <E> ImmutableMultiset<E> copyFromEntries(Collection<? extends Multiset.Entry<? extends E>> entries) {
        if (!entries.isEmpty()) return new RegularImmutableMultiset<E>(entries);
        return ImmutableMultiset.of();
    }

    public static <E> ImmutableMultiset<E> copyOf(Iterator<? extends E> elements) {
        LinkedHashMultiset<E> multiset = LinkedHashMultiset.create();
        Iterators.addAll(multiset, elements);
        return ImmutableMultiset.copyFromEntries(multiset.entrySet());
    }

    ImmutableMultiset() {
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        Iterator entryIterator = ((ImmutableSet)this.entrySet()).iterator();
        return new UnmodifiableIterator<E>((ImmutableMultiset)this, (Iterator)entryIterator){
            int remaining;
            E element;
            final /* synthetic */ Iterator val$entryIterator;
            final /* synthetic */ ImmutableMultiset this$0;
            {
                this.this$0 = immutableMultiset;
                this.val$entryIterator = iterator;
            }

            public boolean hasNext() {
                if (this.remaining > 0) return true;
                if (this.val$entryIterator.hasNext()) return true;
                return false;
            }

            public E next() {
                if (this.remaining <= 0) {
                    Multiset.Entry entry = (Multiset.Entry)this.val$entryIterator.next();
                    this.element = entry.getElement();
                    this.remaining = entry.getCount();
                }
                --this.remaining;
                return (E)this.element;
            }
        };
    }

    @Override
    public ImmutableList<E> asList() {
        ImmutableList<E> immutableList;
        ImmutableList<E> result = this.asList;
        if (result == null) {
            immutableList = this.asList = this.createAsList();
            return immutableList;
        }
        immutableList = result;
        return immutableList;
    }

    ImmutableList<E> createAsList() {
        if (!this.isEmpty()) return new RegularImmutableAsList<E>(this, (Object[])this.toArray());
        return ImmutableList.of();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (this.count((Object)object) <= 0) return false;
        return true;
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final int add(E element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final int remove(Object element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final int setCount(E element, int count) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean setCount(E element, int oldCount, int newCount) {
        throw new UnsupportedOperationException();
    }

    @GwtIncompatible
    @Override
    int copyIntoArray(Object[] dst, int offset) {
        Iterator i$ = ((ImmutableSet)this.entrySet()).iterator();
        while (i$.hasNext()) {
            Multiset.Entry entry = (Multiset.Entry)i$.next();
            Arrays.fill((Object[])dst, (int)offset, (int)(offset + entry.getCount()), entry.getElement());
            offset += entry.getCount();
        }
        return offset;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return Multisets.equalsImpl(this, (Object)object);
    }

    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }

    @Override
    public String toString() {
        return ((AbstractCollection)((Object)this.entrySet())).toString();
    }

    @Override
    public ImmutableSet<Multiset.Entry<E>> entrySet() {
        ImmutableSet<Multiset.Entry<E>> immutableSet;
        ImmutableSet<Multiset.Entry<E>> es = this.entrySet;
        if (es == null) {
            immutableSet = this.entrySet = this.createEntrySet();
            return immutableSet;
        }
        immutableSet = es;
        return immutableSet;
    }

    private final ImmutableSet<Multiset.Entry<E>> createEntrySet() {
        EntrySet entrySet;
        if (this.isEmpty()) {
            entrySet = ImmutableSet.of();
            return entrySet;
        }
        entrySet = new EntrySet((ImmutableMultiset)this, null);
        return entrySet;
    }

    abstract Multiset.Entry<E> getEntry(int var1);

    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }

    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
}

