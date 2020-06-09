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
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.RegularImmutableList;
import com.google.common.collect.SingletonImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.UnmodifiableListIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableList<E>
extends ImmutableCollection<E>
implements List<E>,
RandomAccess {
    public static <E> ImmutableList<E> of() {
        return RegularImmutableList.EMPTY;
    }

    public static <E> ImmutableList<E> of(E element) {
        return new SingletonImmutableList<E>(element);
    }

    public static <E> ImmutableList<E> of(E e1, E e2) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4, e5});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4, e5, e6});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4, e5, e6, e7});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4, e5, e6, e7, e8});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9, e10});
    }

    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11) {
        return ImmutableList.construct((Object[])new Object[]{e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11});
    }

    @SafeVarargs
    public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E ... others) {
        Object[] array = new Object[12 + others.length];
        array[0] = e1;
        array[1] = e2;
        array[2] = e3;
        array[3] = e4;
        array[4] = e5;
        array[5] = e6;
        array[6] = e7;
        array[7] = e8;
        array[8] = e9;
        array[9] = e10;
        array[10] = e11;
        array[11] = e12;
        System.arraycopy(others, (int)0, (Object)array, (int)12, (int)others.length);
        return ImmutableList.construct((Object[])array);
    }

    public static <E> ImmutableList<E> copyOf(Iterable<? extends E> elements) {
        ImmutableList<Object> immutableList;
        Preconditions.checkNotNull(elements);
        if (elements instanceof Collection) {
            immutableList = ImmutableList.copyOf((Collection)elements);
            return immutableList;
        }
        immutableList = ImmutableList.copyOf(elements.iterator());
        return immutableList;
    }

    public static <E> ImmutableList<E> copyOf(Collection<? extends E> elements) {
        ImmutableList<E> immutableList;
        if (!(elements instanceof ImmutableCollection)) return ImmutableList.construct((Object[])elements.toArray());
        ImmutableList<E> list = ((ImmutableCollection)elements).asList();
        if (list.isPartialView()) {
            immutableList = ImmutableList.asImmutableList((Object[])list.toArray());
            return immutableList;
        }
        immutableList = list;
        return immutableList;
    }

    public static <E> ImmutableList<E> copyOf(Iterator<? extends E> elements) {
        if (!elements.hasNext()) {
            return ImmutableList.of();
        }
        E first = elements.next();
        if (elements.hasNext()) return ((Builder)((Builder)new Builder<E>().add(first)).addAll(elements)).build();
        return ImmutableList.of(first);
    }

    public static <E> ImmutableList<E> copyOf(E[] elements) {
        switch (elements.length) {
            case 0: {
                return ImmutableList.of();
            }
            case 1: {
                return new SingletonImmutableList<E>(elements[0]);
            }
        }
        return new RegularImmutableList<E>((Object[])ObjectArrays.checkElementsNotNull((Object[])((Object[])elements.clone())));
    }

    private static <E> ImmutableList<E> construct(Object ... elements) {
        return ImmutableList.asImmutableList((Object[])ObjectArrays.checkElementsNotNull((Object[])elements));
    }

    static <E> ImmutableList<E> asImmutableList(Object[] elements) {
        return ImmutableList.asImmutableList((Object[])elements, (int)elements.length);
    }

    static <E> ImmutableList<E> asImmutableList(Object[] elements, int length) {
        switch (length) {
            case 0: {
                return ImmutableList.of();
            }
            case 1: {
                return new SingletonImmutableList<Object>(elements[0]);
            }
        }
        if (length >= elements.length) return new RegularImmutableList<E>((Object[])elements);
        elements = ObjectArrays.arraysCopyOf(elements, (int)length);
        return new RegularImmutableList<E>((Object[])elements);
    }

    ImmutableList() {
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.listIterator();
    }

    @Override
    public UnmodifiableListIterator<E> listIterator() {
        return this.listIterator((int)0);
    }

    @Override
    public UnmodifiableListIterator<E> listIterator(int index) {
        return new AbstractIndexedListIterator<E>((ImmutableList)this, (int)this.size(), (int)index){
            final /* synthetic */ ImmutableList this$0;
            {
                this.this$0 = immutableList;
                super((int)x0, (int)x1);
            }

            protected E get(int index) {
                return (E)this.this$0.get((int)index);
            }
        };
    }

    @Override
    public int indexOf(@Nullable Object object) {
        if (object == null) {
            return -1;
        }
        int n = Lists.indexOfImpl(this, (Object)object);
        return n;
    }

    @Override
    public int lastIndexOf(@Nullable Object object) {
        if (object == null) {
            return -1;
        }
        int n = Lists.lastIndexOfImpl(this, (Object)object);
        return n;
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (this.indexOf((Object)object) < 0) return false;
        return true;
    }

    @Override
    public ImmutableList<E> subList(int fromIndex, int toIndex) {
        Preconditions.checkPositionIndexes((int)fromIndex, (int)toIndex, (int)this.size());
        int length = toIndex - fromIndex;
        if (length == this.size()) {
            return this;
        }
        switch (length) {
            case 0: {
                return ImmutableList.of();
            }
            case 1: {
                return ImmutableList.of(this.get((int)fromIndex));
            }
        }
        return this.subListUnchecked((int)fromIndex, (int)toIndex);
    }

    ImmutableList<E> subListUnchecked(int fromIndex, int toIndex) {
        return new SubList((ImmutableList)this, (int)fromIndex, (int)(toIndex - fromIndex));
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final boolean addAll(int index, Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final ImmutableList<E> asList() {
        return this;
    }

    @Override
    int copyIntoArray(Object[] dst, int offset) {
        int size = this.size();
        int i = 0;
        while (i < size) {
            dst[offset + i] = this.get((int)i);
            ++i;
        }
        return offset + size;
    }

    public ImmutableList<E> reverse() {
        ImmutableList immutableList;
        if (this.size() <= 1) {
            immutableList = this;
            return immutableList;
        }
        immutableList = new ReverseImmutableList<E>(this);
        return immutableList;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return Lists.equalsImpl(this, (Object)obj);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        int n = this.size();
        int i = 0;
        while (i < n) {
            hashCode = 31 * hashCode + this.get((int)i).hashCode();
            hashCode = ~(~hashCode);
            ++i;
        }
        return hashCode;
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException((String)"Use SerializedForm");
    }

    @Override
    Object writeReplace() {
        return new SerializedForm((Object[])this.toArray());
    }

    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
}

