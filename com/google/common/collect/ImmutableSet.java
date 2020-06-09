/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableEnumSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.RegularImmutableAsList;
import com.google.common.collect.RegularImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.SingletonImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableSet<E>
extends ImmutableCollection<E>
implements Set<E> {
    static final int MAX_TABLE_SIZE = 1073741824;
    private static final double DESIRED_LOAD_FACTOR = 0.7;
    private static final int CUTOFF = 751619276;
    @LazyInit
    private transient ImmutableList<E> asList;

    public static <E> ImmutableSet<E> of() {
        return RegularImmutableSet.EMPTY;
    }

    public static <E> ImmutableSet<E> of(E element) {
        return new SingletonImmutableSet<E>(element);
    }

    public static <E> ImmutableSet<E> of(E e1, E e2) {
        return ImmutableSet.construct((int)2, (Object[])new Object[]{e1, e2});
    }

    public static <E> ImmutableSet<E> of(E e1, E e2, E e3) {
        return ImmutableSet.construct((int)3, (Object[])new Object[]{e1, e2, e3});
    }

    public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4) {
        return ImmutableSet.construct((int)4, (Object[])new Object[]{e1, e2, e3, e4});
    }

    public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5) {
        return ImmutableSet.construct((int)5, (Object[])new Object[]{e1, e2, e3, e4, e5});
    }

    @SafeVarargs
    public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E ... others) {
        int paramCount = 6;
        Object[] elements = new Object[6 + others.length];
        elements[0] = e1;
        elements[1] = e2;
        elements[2] = e3;
        elements[3] = e4;
        elements[4] = e5;
        elements[5] = e6;
        System.arraycopy(others, (int)0, (Object)elements, (int)6, (int)others.length);
        return ImmutableSet.construct((int)elements.length, (Object[])elements);
    }

    private static <E> ImmutableSet<E> construct(int n, Object ... elements) {
        switch (n) {
            case 0: {
                return ImmutableSet.of();
            }
            case 1: {
                Object elem = elements[0];
                return ImmutableSet.of(elem);
            }
        }
        int tableSize = ImmutableSet.chooseTableSize((int)n);
        Object[] table = new Object[tableSize];
        int mask = tableSize - 1;
        int hashCode = 0;
        int uniques = 0;
        int i = 0;
        do {
            if (i >= n) {
                Arrays.fill((Object[])elements, (int)uniques, (int)n, null);
                if (uniques == 1) {
                    Object element = elements[0];
                    return new SingletonImmutableSet<Object>(element, (int)hashCode);
                }
                if (tableSize != ImmutableSet.chooseTableSize((int)uniques)) {
                    return ImmutableSet.construct((int)uniques, (Object[])elements);
                }
                Object[] uniqueElements = uniques < elements.length ? ObjectArrays.arraysCopyOf(elements, (int)uniques) : elements;
                return new RegularImmutableSet<E>((Object[])uniqueElements, (int)hashCode, (Object[])table, (int)mask);
            }
            Object element = ObjectArrays.checkElementNotNull((Object)elements[i], (int)i);
            int hash = element.hashCode();
            int j = Hashing.smear((int)hash);
            do {
                Object value;
                int index;
                if ((value = table[index = j & mask]) == null) {
                    elements[uniques++] = element;
                    table[index] = element;
                    hashCode += hash;
                    break;
                }
                if (value.equals((Object)element)) break;
                ++j;
            } while (true);
            ++i;
        } while (true);
    }

    @VisibleForTesting
    static int chooseTableSize(int setSize) {
        if (setSize < 751619276) {
            int tableSize = Integer.highestOneBit((int)(setSize - 1)) << 1;
            while ((double)tableSize * 0.7 < (double)setSize) {
                tableSize <<= 1;
            }
            return tableSize;
        }
        Preconditions.checkArgument((boolean)(setSize < 1073741824), (Object)"collection too large");
        return 1073741824;
    }

    public static <E> ImmutableSet<E> copyOf(Collection<? extends E> elements) {
        if (elements instanceof ImmutableSet && !(elements instanceof ImmutableSortedSet)) {
            ImmutableSet set = (ImmutableSet)elements;
            if (!set.isPartialView()) {
                return set;
            }
        } else if (elements instanceof EnumSet) {
            return ImmutableSet.copyOfEnumSet((EnumSet)((EnumSet)elements));
        }
        Object[] array = elements.toArray();
        return ImmutableSet.construct((int)array.length, (Object[])array);
    }

    public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> elements) {
        ImmutableSet<Object> immutableSet;
        if (elements instanceof Collection) {
            immutableSet = ImmutableSet.copyOf((Collection)elements);
            return immutableSet;
        }
        immutableSet = ImmutableSet.copyOf(elements.iterator());
        return immutableSet;
    }

    public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> elements) {
        if (!elements.hasNext()) {
            return ImmutableSet.of();
        }
        E first = elements.next();
        if (elements.hasNext()) return ((Builder)((Builder)new Builder<E>().add(first)).addAll(elements)).build();
        return ImmutableSet.of(first);
    }

    public static <E> ImmutableSet<E> copyOf(E[] elements) {
        switch (elements.length) {
            case 0: {
                return ImmutableSet.of();
            }
            case 1: {
                return ImmutableSet.of(elements[0]);
            }
        }
        return ImmutableSet.construct((int)elements.length, (Object[])((Object[])elements.clone()));
    }

    private static ImmutableSet copyOfEnumSet(EnumSet enumSet) {
        return ImmutableEnumSet.asImmutable(EnumSet.copyOf(enumSet));
    }

    ImmutableSet() {
    }

    boolean isHashCodeFast() {
        return false;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ImmutableSet)) return Sets.equalsImpl(this, (Object)object);
        if (!this.isHashCodeFast()) return Sets.equalsImpl(this, (Object)object);
        if (!((ImmutableSet)object).isHashCodeFast()) return Sets.equalsImpl(this, (Object)object);
        if (this.hashCode() == object.hashCode()) return Sets.equalsImpl(this, (Object)object);
        return false;
    }

    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this);
    }

    @Override
    public abstract UnmodifiableIterator<E> iterator();

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
        return new RegularImmutableAsList<E>(this, (Object[])this.toArray());
    }

    @Override
    Object writeReplace() {
        return new SerializedForm((Object[])this.toArray());
    }

    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }

    static /* synthetic */ ImmutableSet access$000(int x0, Object[] x1) {
        return ImmutableSet.construct((int)x0, (Object[])x1);
    }
}

