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
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.ObjectArrays;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Iterables {
    private Iterables() {
    }

    public static <T> Iterable<T> unmodifiableIterable(Iterable<? extends T> iterable) {
        Preconditions.checkNotNull(iterable);
        if (iterable instanceof UnmodifiableIterable) return iterable;
        if (!(iterable instanceof ImmutableCollection)) return new UnmodifiableIterable<T>(iterable, null);
        return iterable;
    }

    @Deprecated
    public static <E> Iterable<E> unmodifiableIterable(ImmutableCollection<E> iterable) {
        return (Iterable)Preconditions.checkNotNull(iterable);
    }

    public static int size(Iterable<?> iterable) {
        int n;
        if (iterable instanceof Collection) {
            n = ((Collection)iterable).size();
            return n;
        }
        n = Iterators.size(iterable.iterator());
        return n;
    }

    public static boolean contains(Iterable<?> iterable, @Nullable Object element) {
        if (!(iterable instanceof Collection)) return Iterators.contains(iterable.iterator(), (Object)element);
        Collection collection = (Collection)iterable;
        return Collections2.safeContains(collection, (Object)element);
    }

    @CanIgnoreReturnValue
    public static boolean removeAll(Iterable<?> removeFrom, Collection<?> elementsToRemove) {
        boolean bl;
        if (removeFrom instanceof Collection) {
            bl = ((Collection)removeFrom).removeAll(Preconditions.checkNotNull(elementsToRemove));
            return bl;
        }
        bl = Iterators.removeAll(removeFrom.iterator(), elementsToRemove);
        return bl;
    }

    @CanIgnoreReturnValue
    public static boolean retainAll(Iterable<?> removeFrom, Collection<?> elementsToRetain) {
        boolean bl;
        if (removeFrom instanceof Collection) {
            bl = ((Collection)removeFrom).retainAll(Preconditions.checkNotNull(elementsToRetain));
            return bl;
        }
        bl = Iterators.retainAll(removeFrom.iterator(), elementsToRetain);
        return bl;
    }

    @CanIgnoreReturnValue
    public static <T> boolean removeIf(Iterable<T> removeFrom, Predicate<? super T> predicate) {
        if (!(removeFrom instanceof RandomAccess)) return Iterators.removeIf(removeFrom.iterator(), predicate);
        if (!(removeFrom instanceof List)) return Iterators.removeIf(removeFrom.iterator(), predicate);
        return Iterables.removeIfFromRandomAccessList((List)removeFrom, Preconditions.checkNotNull(predicate));
    }

    private static <T> boolean removeIfFromRandomAccessList(List<T> list, Predicate<? super T> predicate) {
        int from;
        int to = 0;
        for (from = 0; from < list.size(); ++from) {
            T element = list.get((int)from);
            if (predicate.apply(element)) continue;
            if (from > to) {
                try {
                    list.set((int)to, element);
                }
                catch (UnsupportedOperationException e) {
                    Iterables.slowRemoveIfForRemainingElements(list, predicate, (int)to, (int)from);
                    return true;
                }
                catch (IllegalArgumentException e) {
                    Iterables.slowRemoveIfForRemainingElements(list, predicate, (int)to, (int)from);
                    return true;
                }
            }
            ++to;
        }
        list.subList((int)to, (int)list.size()).clear();
        if (from == to) return false;
        return true;
    }

    private static <T> void slowRemoveIfForRemainingElements(List<T> list, Predicate<? super T> predicate, int to, int from) {
        int n;
        for (n = list.size() - 1; n > from; --n) {
            if (!predicate.apply(list.get((int)n))) continue;
            list.remove((int)n);
        }
        n = from - 1;
        while (n >= to) {
            list.remove((int)n);
            --n;
        }
    }

    @Nullable
    static <T> T removeFirstMatching(Iterable<T> removeFrom, Predicate<? super T> predicate) {
        T next;
        Preconditions.checkNotNull(predicate);
        Iterator<T> iterator = removeFrom.iterator();
        do {
            if (!iterator.hasNext()) return (T)null;
        } while (!predicate.apply(next = iterator.next()));
        iterator.remove();
        return (T)next;
    }

    public static boolean elementsEqual(Iterable<?> iterable1, Iterable<?> iterable2) {
        if (!(iterable1 instanceof Collection)) return Iterators.elementsEqual(iterable1.iterator(), iterable2.iterator());
        if (!(iterable2 instanceof Collection)) return Iterators.elementsEqual(iterable1.iterator(), iterable2.iterator());
        Collection collection1 = (Collection)iterable1;
        Collection collection2 = (Collection)iterable2;
        if (collection1.size() == collection2.size()) return Iterators.elementsEqual(iterable1.iterator(), iterable2.iterator());
        return false;
    }

    public static String toString(Iterable<?> iterable) {
        return Iterators.toString(iterable.iterator());
    }

    public static <T> T getOnlyElement(Iterable<T> iterable) {
        return (T)Iterators.getOnlyElement(iterable.iterator());
    }

    @Nullable
    public static <T> T getOnlyElement(Iterable<? extends T> iterable, @Nullable T defaultValue) {
        return (T)Iterators.getOnlyElement(iterable.iterator(), defaultValue);
    }

    @GwtIncompatible
    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
        return Iterables.toArray(iterable, ObjectArrays.newArray(type, (int)0));
    }

    static <T> T[] toArray(Iterable<? extends T> iterable, T[] array) {
        Collection<T> collection = Iterables.castOrCopyToCollection(iterable);
        return collection.toArray(array);
    }

    static Object[] toArray(Iterable<?> iterable) {
        return Iterables.castOrCopyToCollection(iterable).toArray();
    }

    private static <E> Collection<E> castOrCopyToCollection(Iterable<E> iterable) {
        ArrayList<E> arrayList;
        if (iterable instanceof Collection) {
            arrayList = (ArrayList<E>)iterable;
            return arrayList;
        }
        arrayList = Lists.newArrayList(iterable.iterator());
        return arrayList;
    }

    @CanIgnoreReturnValue
    public static <T> boolean addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd) {
        if (!(elementsToAdd instanceof Collection)) return Iterators.addAll(addTo, Preconditions.checkNotNull(elementsToAdd).iterator());
        Collection<? extends T> c = Collections2.cast(elementsToAdd);
        return addTo.addAll(c);
    }

    public static int frequency(Iterable<?> iterable, @Nullable Object element) {
        if (iterable instanceof Multiset) {
            return ((Multiset)iterable).count((Object)element);
        }
        if (!(iterable instanceof Set)) return Iterators.frequency(iterable.iterator(), (Object)element);
        if (!((Set)iterable).contains((Object)element)) return 0;
        return 1;
    }

    public static <T> Iterable<T> cycle(Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        return new FluentIterable<T>(iterable){
            final /* synthetic */ Iterable val$iterable;
            {
                this.val$iterable = iterable;
            }

            public Iterator<T> iterator() {
                return Iterators.cycle(this.val$iterable);
            }

            public String toString() {
                return this.val$iterable.toString() + " (cycled)";
            }
        };
    }

    public static <T> Iterable<T> cycle(T ... elements) {
        return Iterables.cycle(Lists.newArrayList(elements));
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
        return FluentIterable.concat(a, b);
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c) {
        return FluentIterable.concat(a, b, c);
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c, Iterable<? extends T> d) {
        return FluentIterable.concat(a, b, c, d);
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> ... inputs) {
        return Iterables.concat(ImmutableList.copyOf(inputs));
    }

    public static <T> Iterable<T> concat(Iterable<? extends Iterable<? extends T>> inputs) {
        return FluentIterable.concat(inputs);
    }

    public static <T> Iterable<List<T>> partition(Iterable<T> iterable, int size) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument((boolean)(size > 0));
        return new FluentIterable<List<T>>(iterable, (int)size){
            final /* synthetic */ Iterable val$iterable;
            final /* synthetic */ int val$size;
            {
                this.val$iterable = iterable;
                this.val$size = n;
            }

            public Iterator<List<T>> iterator() {
                return Iterators.partition(this.val$iterable.iterator(), (int)this.val$size);
            }
        };
    }

    public static <T> Iterable<List<T>> paddedPartition(Iterable<T> iterable, int size) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument((boolean)(size > 0));
        return new FluentIterable<List<T>>(iterable, (int)size){
            final /* synthetic */ Iterable val$iterable;
            final /* synthetic */ int val$size;
            {
                this.val$iterable = iterable;
                this.val$size = n;
            }

            public Iterator<List<T>> iterator() {
                return Iterators.paddedPartition(this.val$iterable.iterator(), (int)this.val$size);
            }
        };
    }

    public static <T> Iterable<T> filter(Iterable<T> unfiltered, Predicate<? super T> retainIfTrue) {
        Preconditions.checkNotNull(unfiltered);
        Preconditions.checkNotNull(retainIfTrue);
        return new FluentIterable<T>(unfiltered, retainIfTrue){
            final /* synthetic */ Iterable val$unfiltered;
            final /* synthetic */ Predicate val$retainIfTrue;
            {
                this.val$unfiltered = iterable;
                this.val$retainIfTrue = predicate;
            }

            public Iterator<T> iterator() {
                return Iterators.filter(this.val$unfiltered.iterator(), this.val$retainIfTrue);
            }
        };
    }

    @GwtIncompatible
    public static <T> Iterable<T> filter(Iterable<?> unfiltered, Class<T> desiredType) {
        Preconditions.checkNotNull(unfiltered);
        Preconditions.checkNotNull(desiredType);
        return new FluentIterable<T>(unfiltered, desiredType){
            final /* synthetic */ Iterable val$unfiltered;
            final /* synthetic */ Class val$desiredType;
            {
                this.val$unfiltered = iterable;
                this.val$desiredType = class_;
            }

            public Iterator<T> iterator() {
                return Iterators.filter(this.val$unfiltered.iterator(), this.val$desiredType);
            }
        };
    }

    public static <T> boolean any(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.any(iterable.iterator(), predicate);
    }

    public static <T> boolean all(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.all(iterable.iterator(), predicate);
    }

    public static <T> T find(Iterable<T> iterable, Predicate<? super T> predicate) {
        return (T)Iterators.find(iterable.iterator(), predicate);
    }

    @Nullable
    public static <T> T find(Iterable<? extends T> iterable, Predicate<? super T> predicate, @Nullable T defaultValue) {
        return (T)Iterators.find(iterable.iterator(), predicate, defaultValue);
    }

    public static <T> Optional<T> tryFind(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.tryFind(iterable.iterator(), predicate);
    }

    public static <T> int indexOf(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterators.indexOf(iterable.iterator(), predicate);
    }

    public static <F, T> Iterable<T> transform(Iterable<F> fromIterable, Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(fromIterable);
        Preconditions.checkNotNull(function);
        return new FluentIterable<T>(fromIterable, function){
            final /* synthetic */ Iterable val$fromIterable;
            final /* synthetic */ Function val$function;
            {
                this.val$fromIterable = iterable;
                this.val$function = function;
            }

            public Iterator<T> iterator() {
                return Iterators.transform(this.val$fromIterable.iterator(), this.val$function);
            }
        };
    }

    public static <T> T get(Iterable<T> iterable, int position) {
        Object object;
        Preconditions.checkNotNull(iterable);
        if (iterable instanceof List) {
            object = ((List)iterable).get((int)position);
            return (T)((T)object);
        }
        object = Iterators.get(iterable.iterator(), (int)position);
        return (T)object;
    }

    @Nullable
    public static <T> T get(Iterable<? extends T> iterable, int position, @Nullable T defaultValue) {
        T t;
        Preconditions.checkNotNull(iterable);
        Iterators.checkNonnegative((int)position);
        if (!(iterable instanceof List)) {
            Iterator<? extends T> iterator = iterable.iterator();
            Iterators.advance(iterator, (int)position);
            return (T)Iterators.getNext(iterator, defaultValue);
        }
        List<T> list = Lists.cast(iterable);
        if (position < list.size()) {
            t = list.get((int)position);
            return (T)((T)t);
        }
        t = defaultValue;
        return (T)t;
    }

    @Nullable
    public static <T> T getFirst(Iterable<? extends T> iterable, @Nullable T defaultValue) {
        return (T)Iterators.getNext(iterable.iterator(), defaultValue);
    }

    public static <T> T getLast(Iterable<T> iterable) {
        if (!(iterable instanceof List)) return (T)Iterators.getLast(iterable.iterator());
        List list = (List)iterable;
        if (!list.isEmpty()) return (T)Iterables.getLastInNonemptyList(list);
        throw new NoSuchElementException();
    }

    @Nullable
    public static <T> T getLast(Iterable<? extends T> iterable, @Nullable T defaultValue) {
        if (!(iterable instanceof Collection)) return (T)Iterators.getLast(iterable.iterator(), defaultValue);
        Collection<T> c = Collections2.cast(iterable);
        if (c.isEmpty()) {
            return (T)defaultValue;
        }
        if (!(iterable instanceof List)) return (T)Iterators.getLast(iterable.iterator(), defaultValue);
        return (T)Iterables.getLastInNonemptyList(Lists.cast(iterable));
    }

    private static <T> T getLastInNonemptyList(List<T> list) {
        return (T)list.get((int)(list.size() - 1));
    }

    public static <T> Iterable<T> skip(Iterable<T> iterable, int numberToSkip) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument((boolean)(numberToSkip >= 0), (Object)"number to skip cannot be negative");
        if (!(iterable instanceof List)) return new FluentIterable<T>(iterable, (int)numberToSkip){
            final /* synthetic */ Iterable val$iterable;
            final /* synthetic */ int val$numberToSkip;
            {
                this.val$iterable = iterable;
                this.val$numberToSkip = n;
            }

            public Iterator<T> iterator() {
                Iterator<T> iterator = this.val$iterable.iterator();
                Iterators.advance(iterator, (int)this.val$numberToSkip);
                return new Iterator<T>(this, iterator){
                    boolean atStart;
                    final /* synthetic */ Iterator val$iterator;
                    final /* synthetic */ 8 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$iterator = iterator;
                        this.atStart = true;
                    }

                    public boolean hasNext() {
                        return this.val$iterator.hasNext();
                    }

                    public T next() {
                        E result = this.val$iterator.next();
                        this.atStart = false;
                        return (T)result;
                    }

                    public void remove() {
                        com.google.common.collect.CollectPreconditions.checkRemove((boolean)(!this.atStart));
                        this.val$iterator.remove();
                    }
                };
            }
        };
        List list = (List)iterable;
        return new FluentIterable<T>((List)list, (int)numberToSkip){
            final /* synthetic */ List val$list;
            final /* synthetic */ int val$numberToSkip;
            {
                this.val$list = list;
                this.val$numberToSkip = n;
            }

            public Iterator<T> iterator() {
                int toSkip = java.lang.Math.min((int)this.val$list.size(), (int)this.val$numberToSkip);
                return this.val$list.subList((int)toSkip, (int)this.val$list.size()).iterator();
            }
        };
    }

    public static <T> Iterable<T> limit(Iterable<T> iterable, int limitSize) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkArgument((boolean)(limitSize >= 0), (Object)"limit is negative");
        return new FluentIterable<T>(iterable, (int)limitSize){
            final /* synthetic */ Iterable val$iterable;
            final /* synthetic */ int val$limitSize;
            {
                this.val$iterable = iterable;
                this.val$limitSize = n;
            }

            public Iterator<T> iterator() {
                return Iterators.limit(this.val$iterable.iterator(), (int)this.val$limitSize);
            }
        };
    }

    public static <T> Iterable<T> consumingIterable(Iterable<T> iterable) {
        if (iterable instanceof Queue) {
            return new FluentIterable<T>(iterable){
                final /* synthetic */ Iterable val$iterable;
                {
                    this.val$iterable = iterable;
                }

                public Iterator<T> iterator() {
                    return new com.google.common.collect.ConsumingQueueIterator<T>((Queue)this.val$iterable);
                }

                public String toString() {
                    return "Iterables.consumingIterable(...)";
                }
            };
        }
        Preconditions.checkNotNull(iterable);
        return new FluentIterable<T>(iterable){
            final /* synthetic */ Iterable val$iterable;
            {
                this.val$iterable = iterable;
            }

            public Iterator<T> iterator() {
                return Iterators.consumingIterator(this.val$iterable.iterator());
            }

            public String toString() {
                return "Iterables.consumingIterable(...)";
            }
        };
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection)iterable).isEmpty();
        }
        if (iterable.iterator().hasNext()) return false;
        return true;
    }

    @Beta
    public static <T> Iterable<T> mergeSorted(Iterable<? extends Iterable<? extends T>> iterables, Comparator<? super T> comparator) {
        Preconditions.checkNotNull(iterables, (Object)"iterables");
        Preconditions.checkNotNull(comparator, (Object)"comparator");
        FluentIterable<T> iterable = new FluentIterable<T>(iterables, comparator){
            final /* synthetic */ Iterable val$iterables;
            final /* synthetic */ Comparator val$comparator;
            {
                this.val$iterables = iterable;
                this.val$comparator = comparator;
            }

            public Iterator<T> iterator() {
                return Iterators.mergeSorted(Iterables.transform(this.val$iterables, Iterables.<? extends T>toIterator()), this.val$comparator);
            }
        };
        return new UnmodifiableIterable<T>((Iterable)iterable, null);
    }

    static <T> Function<Iterable<? extends T>, Iterator<? extends T>> toIterator() {
        return new Function<Iterable<? extends T>, Iterator<? extends T>>(){

            public Iterator<? extends T> apply(Iterable<? extends T> iterable) {
                return iterable.iterator();
            }
        };
    }
}

