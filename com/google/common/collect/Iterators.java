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
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Collections2;
import com.google.common.collect.ConsumingQueueIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.TransformedIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.UnmodifiableListIterator;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Iterators {
    static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR = new UnmodifiableListIterator<Object>(){

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new java.util.NoSuchElementException();
        }

        public boolean hasPrevious() {
            return false;
        }

        public Object previous() {
            throw new java.util.NoSuchElementException();
        }

        public int nextIndex() {
            return 0;
        }

        public int previousIndex() {
            return -1;
        }
    };
    private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>(){

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new java.util.NoSuchElementException();
        }

        public void remove() {
            com.google.common.collect.CollectPreconditions.checkRemove((boolean)false);
        }
    };

    private Iterators() {
    }

    static <T> UnmodifiableIterator<T> emptyIterator() {
        return Iterators.emptyListIterator();
    }

    static <T> UnmodifiableListIterator<T> emptyListIterator() {
        return EMPTY_LIST_ITERATOR;
    }

    static <T> Iterator<T> emptyModifiableIterator() {
        return EMPTY_MODIFIABLE_ITERATOR;
    }

    public static <T> UnmodifiableIterator<T> unmodifiableIterator(Iterator<? extends T> iterator) {
        Preconditions.checkNotNull(iterator);
        if (!(iterator instanceof UnmodifiableIterator)) return new UnmodifiableIterator<T>(iterator){
            final /* synthetic */ Iterator val$iterator;
            {
                this.val$iterator = iterator;
            }

            public boolean hasNext() {
                return this.val$iterator.hasNext();
            }

            public T next() {
                return (T)this.val$iterator.next();
            }
        };
        return (UnmodifiableIterator)iterator;
    }

    @Deprecated
    public static <T> UnmodifiableIterator<T> unmodifiableIterator(UnmodifiableIterator<T> iterator) {
        return Preconditions.checkNotNull(iterator);
    }

    public static int size(Iterator<?> iterator) {
        long count = 0L;
        while (iterator.hasNext()) {
            iterator.next();
            ++count;
        }
        return Ints.saturatedCast((long)count);
    }

    public static boolean contains(Iterator<?> iterator, @Nullable Object element) {
        return Iterators.any(iterator, Predicates.equalTo(element));
    }

    @CanIgnoreReturnValue
    public static boolean removeAll(Iterator<?> removeFrom, Collection<?> elementsToRemove) {
        return Iterators.removeIf(removeFrom, Predicates.in(elementsToRemove));
    }

    @CanIgnoreReturnValue
    public static <T> boolean removeIf(Iterator<T> removeFrom, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        boolean modified = false;
        while (removeFrom.hasNext()) {
            if (!predicate.apply(removeFrom.next())) continue;
            removeFrom.remove();
            modified = true;
        }
        return modified;
    }

    @CanIgnoreReturnValue
    public static boolean retainAll(Iterator<?> removeFrom, Collection<?> elementsToRetain) {
        return Iterators.removeIf(removeFrom, Predicates.not(Predicates.in(elementsToRetain)));
    }

    public static boolean elementsEqual(Iterator<?> iterator1, Iterator<?> iterator2) {
        while (iterator1.hasNext()) {
            ? o2;
            if (!iterator2.hasNext()) {
                return false;
            }
            ? o1 = iterator1.next();
            if (Objects.equal(o1, o2 = iterator2.next())) continue;
            return false;
        }
        if (iterator2.hasNext()) return false;
        return true;
    }

    public static String toString(Iterator<?> iterator) {
        return Collections2.STANDARD_JOINER.appendTo((StringBuilder)new StringBuilder().append((char)'['), iterator).append((char)']').toString();
    }

    @CanIgnoreReturnValue
    public static <T> T getOnlyElement(Iterator<T> iterator) {
        T first = iterator.next();
        if (!iterator.hasNext()) {
            return (T)first;
        }
        StringBuilder sb = new StringBuilder().append((String)"expected one element but was: <").append(first);
        for (int i = 0; i < 4 && iterator.hasNext(); ++i) {
            sb.append((String)", ").append(iterator.next());
        }
        if (iterator.hasNext()) {
            sb.append((String)", ...");
        }
        sb.append((char)'>');
        throw new IllegalArgumentException((String)sb.toString());
    }

    @Nullable
    @CanIgnoreReturnValue
    public static <T> T getOnlyElement(Iterator<? extends T> iterator, @Nullable T defaultValue) {
        T t;
        if (iterator.hasNext()) {
            t = Iterators.getOnlyElement(iterator);
            return (T)((T)t);
        }
        t = defaultValue;
        return (T)t;
    }

    @GwtIncompatible
    public static <T> T[] toArray(Iterator<? extends T> iterator, Class<T> type) {
        ArrayList<? extends T> list = Lists.newArrayList(iterator);
        return Iterables.toArray(list, type);
    }

    @CanIgnoreReturnValue
    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        Preconditions.checkNotNull(addTo);
        Preconditions.checkNotNull(iterator);
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add(iterator.next());
        }
        return wasModified;
    }

    public static int frequency(Iterator<?> iterator, @Nullable Object element) {
        return Iterators.size(Iterators.filter(iterator, Predicates.equalTo(element)));
    }

    public static <T> Iterator<T> cycle(Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        return new Iterator<T>(iterable){
            Iterator<T> iterator;
            final /* synthetic */ Iterable val$iterable;
            {
                this.val$iterable = iterable;
                this.iterator = Iterators.emptyModifiableIterator();
            }

            public boolean hasNext() {
                if (this.iterator.hasNext()) return true;
                if (this.val$iterable.iterator().hasNext()) return true;
                return false;
            }

            public T next() {
                if (this.iterator.hasNext()) return (T)this.iterator.next();
                this.iterator = this.val$iterable.iterator();
                if (this.iterator.hasNext()) return (T)this.iterator.next();
                throw new java.util.NoSuchElementException();
            }

            public void remove() {
                this.iterator.remove();
            }
        };
    }

    @SafeVarargs
    public static <T> Iterator<T> cycle(T ... elements) {
        return Iterators.cycle(Lists.newArrayList(elements));
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        return Iterators.concat(new ConsumingQueueIterator<Iterator>(a, b));
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        Preconditions.checkNotNull(c);
        return Iterators.concat(new ConsumingQueueIterator<Iterator>(a, b, c));
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c, Iterator<? extends T> d) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        Preconditions.checkNotNull(c);
        Preconditions.checkNotNull(d);
        return Iterators.concat(new ConsumingQueueIterator<Iterator>(a, b, c, d));
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> ... inputs) {
        Iterator<? extends T>[] arr$ = Preconditions.checkNotNull(inputs);
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Iterator<? extends T> input = arr$[i$];
            Preconditions.checkNotNull(input);
            ++i$;
        }
        return Iterators.concat(new ConsumingQueueIterator<Iterator<? extends T>>(inputs));
    }

    public static <T> Iterator<T> concat(Iterator<? extends Iterator<? extends T>> inputs) {
        return new ConcatenatedIterator<T>(inputs);
    }

    public static <T> UnmodifiableIterator<List<T>> partition(Iterator<T> iterator, int size) {
        return Iterators.partitionImpl(iterator, (int)size, (boolean)false);
    }

    public static <T> UnmodifiableIterator<List<T>> paddedPartition(Iterator<T> iterator, int size) {
        return Iterators.partitionImpl(iterator, (int)size, (boolean)true);
    }

    private static <T> UnmodifiableIterator<List<T>> partitionImpl(Iterator<T> iterator, int size, boolean pad) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkArgument((boolean)(size > 0));
        return new UnmodifiableIterator<List<T>>(iterator, (int)size, (boolean)pad){
            final /* synthetic */ Iterator val$iterator;
            final /* synthetic */ int val$size;
            final /* synthetic */ boolean val$pad;
            {
                this.val$iterator = iterator;
                this.val$size = n;
                this.val$pad = bl;
            }

            public boolean hasNext() {
                return this.val$iterator.hasNext();
            }

            public List<T> next() {
                List<Object> list;
                int count;
                if (!this.hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                Object[] array = new Object[this.val$size];
                for (count = 0; count < this.val$size && this.val$iterator.hasNext(); ++count) {
                    array[count] = this.val$iterator.next();
                }
                for (int i = count; i < this.val$size; ++i) {
                    array[i] = null;
                }
                List<Object> list2 = java.util.Collections.unmodifiableList(java.util.Arrays.asList(array));
                if (!this.val$pad && count != this.val$size) {
                    list = list2.subList((int)0, (int)count);
                    return list;
                }
                list = list2;
                return list;
            }
        };
    }

    public static <T> UnmodifiableIterator<T> filter(Iterator<T> unfiltered, Predicate<? super T> retainIfTrue) {
        Preconditions.checkNotNull(unfiltered);
        Preconditions.checkNotNull(retainIfTrue);
        return new AbstractIterator<T>(unfiltered, retainIfTrue){
            final /* synthetic */ Iterator val$unfiltered;
            final /* synthetic */ Predicate val$retainIfTrue;
            {
                this.val$unfiltered = iterator;
                this.val$retainIfTrue = predicate;
            }

            protected T computeNext() {
                E element;
                do {
                    if (!this.val$unfiltered.hasNext()) return (T)this.endOfData();
                } while (!this.val$retainIfTrue.apply(element = this.val$unfiltered.next()));
                return (T)element;
            }
        };
    }

    @GwtIncompatible
    public static <T> UnmodifiableIterator<T> filter(Iterator<?> unfiltered, Class<T> desiredType) {
        return Iterators.filter(unfiltered, Predicates.instanceOf(desiredType));
    }

    public static <T> boolean any(Iterator<T> iterator, Predicate<? super T> predicate) {
        if (Iterators.indexOf(iterator, predicate) == -1) return false;
        return true;
    }

    public static <T> boolean all(Iterator<T> iterator, Predicate<? super T> predicate) {
        T element;
        Preconditions.checkNotNull(predicate);
        do {
            if (!iterator.hasNext()) return true;
        } while (predicate.apply(element = iterator.next()));
        return false;
    }

    public static <T> T find(Iterator<T> iterator, Predicate<? super T> predicate) {
        return (T)Iterators.filter(iterator, predicate).next();
    }

    @Nullable
    public static <T> T find(Iterator<? extends T> iterator, Predicate<? super T> predicate, @Nullable T defaultValue) {
        return (T)Iterators.getNext(Iterators.filter(iterator, predicate), defaultValue);
    }

    public static <T> Optional<T> tryFind(Iterator<T> iterator, Predicate<? super T> predicate) {
        Optional<Object> optional;
        UnmodifiableIterator<T> filteredIterator = Iterators.filter(iterator, predicate);
        if (filteredIterator.hasNext()) {
            optional = Optional.of(filteredIterator.next());
            return optional;
        }
        optional = Optional.absent();
        return optional;
    }

    public static <T> int indexOf(Iterator<T> iterator, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate, (Object)"predicate");
        int i = 0;
        while (iterator.hasNext()) {
            T current = iterator.next();
            if (predicate.apply(current)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static <F, T> Iterator<T> transform(Iterator<F> fromIterator, Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(function);
        return new TransformedIterator<F, T>(fromIterator, function){
            final /* synthetic */ Function val$function;
            {
                this.val$function = function;
                super(x0);
            }

            T transform(F from) {
                return (T)this.val$function.apply(from);
            }
        };
    }

    public static <T> T get(Iterator<T> iterator, int position) {
        Iterators.checkNonnegative((int)position);
        int skipped = Iterators.advance(iterator, (int)position);
        if (iterator.hasNext()) return (T)iterator.next();
        throw new IndexOutOfBoundsException((String)("position (" + position + ") must be less than the number of elements that remained (" + skipped + ")"));
    }

    static void checkNonnegative(int position) {
        if (position >= 0) return;
        throw new IndexOutOfBoundsException((String)("position (" + position + ") must not be negative"));
    }

    @Nullable
    public static <T> T get(Iterator<? extends T> iterator, int position, @Nullable T defaultValue) {
        Iterators.checkNonnegative((int)position);
        Iterators.advance(iterator, (int)position);
        return (T)Iterators.getNext(iterator, defaultValue);
    }

    @Nullable
    public static <T> T getNext(Iterator<? extends T> iterator, @Nullable T defaultValue) {
        T t;
        if (iterator.hasNext()) {
            t = iterator.next();
            return (T)((T)t);
        }
        t = defaultValue;
        return (T)t;
    }

    public static <T> T getLast(Iterator<T> iterator) {
        T current;
        do {
            current = iterator.next();
        } while (iterator.hasNext());
        return (T)current;
    }

    @Nullable
    public static <T> T getLast(Iterator<? extends T> iterator, @Nullable T defaultValue) {
        T t;
        if (iterator.hasNext()) {
            t = Iterators.getLast(iterator);
            return (T)((T)t);
        }
        t = defaultValue;
        return (T)t;
    }

    @CanIgnoreReturnValue
    public static int advance(Iterator<?> iterator, int numberToAdvance) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkArgument((boolean)(numberToAdvance >= 0), (Object)"numberToAdvance must be nonnegative");
        int i = 0;
        while (i < numberToAdvance) {
            if (!iterator.hasNext()) return i;
            iterator.next();
            ++i;
        }
        return i;
    }

    public static <T> Iterator<T> limit(Iterator<T> iterator, int limitSize) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkArgument((boolean)(limitSize >= 0), (Object)"limit is negative");
        return new Iterator<T>((int)limitSize, iterator){
            private int count;
            final /* synthetic */ int val$limitSize;
            final /* synthetic */ Iterator val$iterator;
            {
                this.val$limitSize = n;
                this.val$iterator = iterator;
            }

            public boolean hasNext() {
                if (this.count >= this.val$limitSize) return false;
                if (!this.val$iterator.hasNext()) return false;
                return true;
            }

            public T next() {
                if (!this.hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                ++this.count;
                return (T)this.val$iterator.next();
            }

            public void remove() {
                this.val$iterator.remove();
            }
        };
    }

    public static <T> Iterator<T> consumingIterator(Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        return new UnmodifiableIterator<T>(iterator){
            final /* synthetic */ Iterator val$iterator;
            {
                this.val$iterator = iterator;
            }

            public boolean hasNext() {
                return this.val$iterator.hasNext();
            }

            public T next() {
                E next = this.val$iterator.next();
                this.val$iterator.remove();
                return (T)next;
            }

            public String toString() {
                return "Iterators.consumingIterator(...)";
            }
        };
    }

    @Nullable
    static <T> T pollNext(Iterator<T> iterator) {
        if (!iterator.hasNext()) return (T)null;
        T result = iterator.next();
        iterator.remove();
        return (T)result;
    }

    static void clear(Iterator<?> iterator) {
        Preconditions.checkNotNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    @SafeVarargs
    public static <T> UnmodifiableIterator<T> forArray(T ... array) {
        return Iterators.forArray(array, (int)0, (int)array.length, (int)0);
    }

    static <T> UnmodifiableListIterator<T> forArray(T[] array, int offset, int length, int index) {
        Preconditions.checkArgument((boolean)(length >= 0));
        int end = offset + length;
        Preconditions.checkPositionIndexes((int)offset, (int)end, (int)array.length);
        Preconditions.checkPositionIndex((int)index, (int)length);
        if (length != 0) return new AbstractIndexedListIterator<T>((int)length, (int)index, (Object[])array, (int)offset){
            final /* synthetic */ Object[] val$array;
            final /* synthetic */ int val$offset;
            {
                this.val$array = arrobject;
                this.val$offset = n;
                super((int)x0, (int)x1);
            }

            protected T get(int index) {
                return (T)this.val$array[this.val$offset + index];
            }
        };
        return Iterators.emptyListIterator();
    }

    public static <T> UnmodifiableIterator<T> singletonIterator(@Nullable T value) {
        return new UnmodifiableIterator<T>(value){
            boolean done;
            final /* synthetic */ Object val$value;
            {
                this.val$value = object;
            }

            public boolean hasNext() {
                if (this.done) return false;
                return true;
            }

            public T next() {
                if (this.done) {
                    throw new java.util.NoSuchElementException();
                }
                this.done = true;
                return (T)this.val$value;
            }
        };
    }

    public static <T> UnmodifiableIterator<T> forEnumeration(Enumeration<T> enumeration) {
        Preconditions.checkNotNull(enumeration);
        return new UnmodifiableIterator<T>(enumeration){
            final /* synthetic */ Enumeration val$enumeration;
            {
                this.val$enumeration = enumeration;
            }

            public boolean hasNext() {
                return this.val$enumeration.hasMoreElements();
            }

            public T next() {
                return (T)this.val$enumeration.nextElement();
            }
        };
    }

    public static <T> Enumeration<T> asEnumeration(Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        return new Enumeration<T>(iterator){
            final /* synthetic */ Iterator val$iterator;
            {
                this.val$iterator = iterator;
            }

            public boolean hasMoreElements() {
                return this.val$iterator.hasNext();
            }

            public T nextElement() {
                return (T)this.val$iterator.next();
            }
        };
    }

    public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> iterator) {
        if (!(iterator instanceof PeekingImpl)) return new PeekingImpl<T>(iterator);
        return (PeekingImpl)iterator;
    }

    @Deprecated
    public static <T> PeekingIterator<T> peekingIterator(PeekingIterator<T> iterator) {
        return Preconditions.checkNotNull(iterator);
    }

    @Beta
    public static <T> UnmodifiableIterator<T> mergeSorted(Iterable<? extends Iterator<? extends T>> iterators, Comparator<? super T> comparator) {
        Preconditions.checkNotNull(iterators, (Object)"iterators");
        Preconditions.checkNotNull(comparator, (Object)"comparator");
        return new MergingIterator<T>(iterators, comparator);
    }

    static <T> ListIterator<T> cast(Iterator<T> iterator) {
        return (ListIterator)iterator;
    }
}

