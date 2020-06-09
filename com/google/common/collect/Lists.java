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
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.CartesianList;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Lists {
    private Lists() {
    }

    @GwtCompatible(serializable=true)
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    @CanIgnoreReturnValue
    @GwtCompatible(serializable=true)
    public static <E> ArrayList<E> newArrayList(E ... elements) {
        Preconditions.checkNotNull(elements);
        int capacity = Lists.computeArrayListCapacity((int)elements.length);
        ArrayList<E> list = new ArrayList<E>((int)capacity);
        Collections.addAll(list, elements);
        return list;
    }

    @VisibleForTesting
    static int computeArrayListCapacity(int arraySize) {
        CollectPreconditions.checkNonnegative((int)arraySize, (String)"arraySize");
        return Ints.saturatedCast((long)(5L + (long)arraySize + (long)(arraySize / 10)));
    }

    @CanIgnoreReturnValue
    @GwtCompatible(serializable=true)
    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        ArrayList<? extends E> arrayList;
        Preconditions.checkNotNull(elements);
        if (elements instanceof Collection) {
            arrayList = new ArrayList<E>(Collections2.cast(elements));
            return arrayList;
        }
        arrayList = Lists.newArrayList(elements.iterator());
        return arrayList;
    }

    @CanIgnoreReturnValue
    @GwtCompatible(serializable=true)
    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = Lists.newArrayList();
        Iterators.addAll(list, elements);
        return list;
    }

    @GwtCompatible(serializable=true)
    public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize) {
        CollectPreconditions.checkNonnegative((int)initialArraySize, (String)"initialArraySize");
        return new ArrayList<E>((int)initialArraySize);
    }

    @GwtCompatible(serializable=true)
    public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize) {
        return new ArrayList<E>((int)Lists.computeArrayListCapacity((int)estimatedSize));
    }

    @GwtCompatible(serializable=true)
    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }

    @GwtCompatible(serializable=true)
    public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> elements) {
        LinkedList<E> list = Lists.newLinkedList();
        Iterables.addAll(list, elements);
        return list;
    }

    @GwtIncompatible
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<E>();
    }

    @GwtIncompatible
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> elements) {
        Collection<? extends E> elementsCollection = elements instanceof Collection ? Collections2.cast(elements) : Lists.newArrayList(elements);
        return new CopyOnWriteArrayList<E>(elementsCollection);
    }

    public static <E> List<E> asList(@Nullable E first, E[] rest) {
        return new OnePlusArrayList<E>(first, rest);
    }

    public static <E> List<E> asList(@Nullable E first, @Nullable E second, E[] rest) {
        return new TwoPlusArrayList<E>(first, second, rest);
    }

    public static <B> List<List<B>> cartesianProduct(List<? extends List<? extends B>> lists) {
        return CartesianList.create(lists);
    }

    public static <B> List<List<B>> cartesianProduct(List<? extends B> ... lists) {
        return Lists.cartesianProduct(Arrays.asList(lists));
    }

    public static <F, T> List<T> transform(List<F> fromList, Function<? super F, ? extends T> function) {
        AbstractList abstractList;
        if (fromList instanceof RandomAccess) {
            abstractList = new TransformingRandomAccessList<F, T>(fromList, function);
            return abstractList;
        }
        abstractList = new TransformingSequentialList<F, T>(fromList, function);
        return abstractList;
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        Partition partition;
        Preconditions.checkNotNull(list);
        Preconditions.checkArgument((boolean)(size > 0));
        if (list instanceof RandomAccess) {
            partition = new RandomAccessPartition<T>(list, (int)size);
            return partition;
        }
        partition = new Partition<T>(list, (int)size);
        return partition;
    }

    public static ImmutableList<Character> charactersOf(String string) {
        return new StringAsImmutableList((String)Preconditions.checkNotNull(string));
    }

    @Beta
    public static List<Character> charactersOf(CharSequence sequence) {
        return new CharSequenceAsList((CharSequence)Preconditions.checkNotNull(sequence));
    }

    public static <T> List<T> reverse(List<T> list) {
        if (list instanceof ImmutableList) {
            return ((ImmutableList)list).reverse();
        }
        if (list instanceof ReverseList) {
            return ((ReverseList)list).getForwardList();
        }
        if (!(list instanceof RandomAccess)) return new ReverseList<T>(list);
        return new RandomAccessReverseList<T>(list);
    }

    static int hashCodeImpl(List<?> list) {
        int hashCode = 1;
        Iterator<?> i$ = list.iterator();
        while (i$.hasNext()) {
            ? o = i$.next();
            hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
            hashCode = ~(~hashCode);
        }
        return hashCode;
    }

    static boolean equalsImpl(List<?> thisList, @Nullable Object other) {
        if (other == Preconditions.checkNotNull(thisList)) {
            return true;
        }
        if (!(other instanceof List)) {
            return false;
        }
        List otherList = (List)other;
        int size = thisList.size();
        if (size != otherList.size()) {
            return false;
        }
        if (!(thisList instanceof RandomAccess)) return Iterators.elementsEqual(thisList.iterator(), otherList.iterator());
        if (!(otherList instanceof RandomAccess)) return Iterators.elementsEqual(thisList.iterator(), otherList.iterator());
        int i = 0;
        while (i < size) {
            if (!Objects.equal(thisList.get((int)i), otherList.get((int)i))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    static <E> boolean addAllImpl(List<E> list, int index, Iterable<? extends E> elements) {
        boolean changed = false;
        ListIterator<E> listIterator = list.listIterator((int)index);
        Iterator<E> i$ = elements.iterator();
        while (i$.hasNext()) {
            E e = i$.next();
            listIterator.add(e);
            changed = true;
        }
        return changed;
    }

    static int indexOfImpl(List<?> list, @Nullable Object element) {
        if (list instanceof RandomAccess) {
            return Lists.indexOfRandomAccess(list, (Object)element);
        }
        ListIterator<?> listIterator = list.listIterator();
        do {
            if (!listIterator.hasNext()) return -1;
        } while (!Objects.equal((Object)element, listIterator.next()));
        return listIterator.previousIndex();
    }

    private static int indexOfRandomAccess(List<?> list, @Nullable Object element) {
        int size = list.size();
        if (element == null) {
            int i = 0;
            while (i < size) {
                if (list.get((int)i) == null) {
                    return i;
                }
                ++i;
            }
            return -1;
        }
        int i = 0;
        while (i < size) {
            if (element.equals(list.get((int)i))) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    static int lastIndexOfImpl(List<?> list, @Nullable Object element) {
        if (list instanceof RandomAccess) {
            return Lists.lastIndexOfRandomAccess(list, (Object)element);
        }
        ListIterator<?> listIterator = list.listIterator((int)list.size());
        do {
            if (!listIterator.hasPrevious()) return -1;
        } while (!Objects.equal((Object)element, listIterator.previous()));
        return listIterator.nextIndex();
    }

    private static int lastIndexOfRandomAccess(List<?> list, @Nullable Object element) {
        if (element == null) {
            int i = list.size() - 1;
            while (i >= 0) {
                if (list.get((int)i) == null) {
                    return i;
                }
                --i;
            }
            return -1;
        }
        int i = list.size() - 1;
        while (i >= 0) {
            if (element.equals(list.get((int)i))) {
                return i;
            }
            --i;
        }
        return -1;
    }

    static <E> ListIterator<E> listIteratorImpl(List<E> list, int index) {
        return new AbstractListWrapper<E>(list).listIterator((int)index);
    }

    static <E> List<E> subListImpl(List<E> list, int fromIndex, int toIndex) {
        AbstractListWrapper wrapper;
        if (list instanceof RandomAccess) {
            wrapper = new RandomAccessListWrapper<E>(list){
                private static final long serialVersionUID = 0L;

                public ListIterator<E> listIterator(int index) {
                    return this.backingList.listIterator((int)index);
                }
            };
            return wrapper.subList((int)fromIndex, (int)toIndex);
        }
        wrapper = new AbstractListWrapper<E>(list){
            private static final long serialVersionUID = 0L;

            public ListIterator<E> listIterator(int index) {
                return this.backingList.listIterator((int)index);
            }
        };
        return wrapper.subList((int)fromIndex, (int)toIndex);
    }

    static <T> List<T> cast(Iterable<T> iterable) {
        return (List)iterable;
    }
}

