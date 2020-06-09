/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.BoundType;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableEnumSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.Synchronized;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Sets {
    private Sets() {
    }

    @GwtCompatible(serializable=true)
    public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(E anElement, E ... otherElements) {
        return ImmutableEnumSet.asImmutable(EnumSet.of(anElement, otherElements));
    }

    @GwtCompatible(serializable=true)
    public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(Iterable<E> elements) {
        if (elements instanceof ImmutableEnumSet) {
            return (ImmutableEnumSet)elements;
        }
        if (elements instanceof Collection) {
            Collection collection = (Collection)elements;
            if (!collection.isEmpty()) return ImmutableEnumSet.asImmutable(EnumSet.copyOf(collection));
            return ImmutableSet.of();
        }
        Iterator<E> itr = elements.iterator();
        if (!itr.hasNext()) return ImmutableSet.of();
        EnumSet<Enum> enumSet = EnumSet.of((Enum)itr.next());
        Iterators.addAll(enumSet, itr);
        return ImmutableEnumSet.asImmutable(enumSet);
    }

    public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
        EnumSet<E> set = EnumSet.noneOf(elementType);
        Iterables.addAll(set, iterable);
        return set;
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    public static <E> HashSet<E> newHashSet(E ... elements) {
        HashSet<E> set = Sets.newHashSetWithExpectedSize((int)elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
        return new HashSet<E>((int)Maps.capacity((int)expectedSize));
    }

    public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
        HashSet<? extends E> hashSet;
        if (elements instanceof Collection) {
            hashSet = new HashSet<E>(Collections2.cast(elements));
            return hashSet;
        }
        hashSet = Sets.newHashSet(elements.iterator());
        return hashSet;
    }

    public static <E> HashSet<E> newHashSet(Iterator<? extends E> elements) {
        HashSet<E> set = Sets.newHashSet();
        Iterators.addAll(set, elements);
        return set;
    }

    public static <E> Set<E> newConcurrentHashSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<K, V>());
    }

    public static <E> Set<E> newConcurrentHashSet(Iterable<? extends E> elements) {
        Set<E> set = Sets.newConcurrentHashSet();
        Iterables.addAll(set, elements);
        return set;
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<E>();
    }

    public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
        return new LinkedHashSet<E>((int)Maps.capacity((int)expectedSize));
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedHashSet<E>(Collections2.cast(elements));
        }
        LinkedHashSet<E> set = Sets.newLinkedHashSet();
        Iterables.addAll(set, elements);
        return set;
    }

    public static <E extends Comparable> TreeSet<E> newTreeSet() {
        return new TreeSet<E>();
    }

    public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> elements) {
        TreeSet<E> set = Sets.newTreeSet();
        Iterables.addAll(set, elements);
        return set;
    }

    public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
        return new TreeSet<E>(Preconditions.checkNotNull(comparator));
    }

    public static <E> Set<E> newIdentityHashSet() {
        return Collections.newSetFromMap(Maps.<K, V>newIdentityHashMap());
    }

    @GwtIncompatible
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet<E>();
    }

    @GwtIncompatible
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> elements) {
        Collection<? extends E> elementsCollection = elements instanceof Collection ? Collections2.cast(elements) : Lists.newArrayList(elements);
        return new CopyOnWriteArraySet<E>(elementsCollection);
    }

    public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection) {
        if (collection instanceof EnumSet) {
            return EnumSet.complementOf((EnumSet)collection);
        }
        Preconditions.checkArgument((boolean)(!collection.isEmpty()), (Object)"collection is empty; use the other version of this method");
        Class<E> type = ((Enum)collection.iterator().next()).getDeclaringClass();
        return Sets.makeComplementByHand(collection, type);
    }

    public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection, Class<E> type) {
        EnumSet<E> enumSet;
        Preconditions.checkNotNull(collection);
        if (collection instanceof EnumSet) {
            enumSet = EnumSet.complementOf((EnumSet)collection);
            return enumSet;
        }
        enumSet = Sets.makeComplementByHand(collection, type);
        return enumSet;
    }

    private static <E extends Enum<E>> EnumSet<E> makeComplementByHand(Collection<E> collection, Class<E> type) {
        EnumSet<E> result = EnumSet.allOf(type);
        result.removeAll(collection);
        return result;
    }

    @Deprecated
    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Collections.newSetFromMap(map);
    }

    public static <E> SetView<E> union(Set<? extends E> set1, Set<? extends E> set2) {
        Preconditions.checkNotNull(set1, (Object)"set1");
        Preconditions.checkNotNull(set2, (Object)"set2");
        SetView<? extends E> set2minus1 = Sets.difference(set2, set1);
        return new SetView<E>(set1, set2minus1, set2){
            final /* synthetic */ Set val$set1;
            final /* synthetic */ Set val$set2minus1;
            final /* synthetic */ Set val$set2;
            {
                this.val$set1 = set;
                this.val$set2minus1 = set2;
                this.val$set2 = set3;
                super(null);
            }

            public int size() {
                return com.google.common.math.IntMath.saturatedAdd((int)this.val$set1.size(), (int)this.val$set2minus1.size());
            }

            public boolean isEmpty() {
                if (!this.val$set1.isEmpty()) return false;
                if (!this.val$set2.isEmpty()) return false;
                return true;
            }

            public com.google.common.collect.UnmodifiableIterator<E> iterator() {
                return Iterators.unmodifiableIterator(Iterators.concat(this.val$set1.iterator(), this.val$set2minus1.iterator()));
            }

            public boolean contains(Object object) {
                if (this.val$set1.contains((Object)object)) return true;
                if (this.val$set2.contains((Object)object)) return true;
                return false;
            }

            public <S extends Set<E>> S copyInto(S set) {
                set.addAll(this.val$set1);
                set.addAll(this.val$set2);
                return (S)set;
            }

            public ImmutableSet<E> immutableCopy() {
                return ((com.google.common.collect.ImmutableSet$Builder)((com.google.common.collect.ImmutableSet$Builder)new com.google.common.collect.ImmutableSet$Builder<E>().addAll((Iterable)this.val$set1)).addAll((Iterable)this.val$set2)).build();
            }
        };
    }

    public static <E> SetView<E> intersection(Set<E> set1, Set<?> set2) {
        Preconditions.checkNotNull(set1, (Object)"set1");
        Preconditions.checkNotNull(set2, (Object)"set2");
        Predicate<?> inSet2 = Predicates.in(set2);
        return new SetView<E>(set1, inSet2, set2){
            final /* synthetic */ Set val$set1;
            final /* synthetic */ Predicate val$inSet2;
            final /* synthetic */ Set val$set2;
            {
                this.val$set1 = set;
                this.val$inSet2 = predicate;
                this.val$set2 = set2;
                super(null);
            }

            public com.google.common.collect.UnmodifiableIterator<E> iterator() {
                return Iterators.filter(this.val$set1.iterator(), this.val$inSet2);
            }

            public int size() {
                return Iterators.size(this.iterator());
            }

            public boolean isEmpty() {
                if (this.iterator().hasNext()) return false;
                return true;
            }

            public boolean contains(Object object) {
                if (!this.val$set1.contains((Object)object)) return false;
                if (!this.val$set2.contains((Object)object)) return false;
                return true;
            }

            public boolean containsAll(Collection<?> collection) {
                if (!this.val$set1.containsAll(collection)) return false;
                if (!this.val$set2.containsAll(collection)) return false;
                return true;
            }
        };
    }

    public static <E> SetView<E> difference(Set<E> set1, Set<?> set2) {
        Preconditions.checkNotNull(set1, (Object)"set1");
        Preconditions.checkNotNull(set2, (Object)"set2");
        Predicate<?> notInSet2 = Predicates.not(Predicates.in(set2));
        return new SetView<E>(set1, notInSet2, set2){
            final /* synthetic */ Set val$set1;
            final /* synthetic */ Predicate val$notInSet2;
            final /* synthetic */ Set val$set2;
            {
                this.val$set1 = set;
                this.val$notInSet2 = predicate;
                this.val$set2 = set2;
                super(null);
            }

            public com.google.common.collect.UnmodifiableIterator<E> iterator() {
                return Iterators.filter(this.val$set1.iterator(), this.val$notInSet2);
            }

            public int size() {
                return Iterators.size(this.iterator());
            }

            public boolean isEmpty() {
                return this.val$set2.containsAll(this.val$set1);
            }

            public boolean contains(Object element) {
                if (!this.val$set1.contains((Object)element)) return false;
                if (this.val$set2.contains((Object)element)) return false;
                return true;
            }
        };
    }

    public static <E> SetView<E> symmetricDifference(Set<? extends E> set1, Set<? extends E> set2) {
        Preconditions.checkNotNull(set1, (Object)"set1");
        Preconditions.checkNotNull(set2, (Object)"set2");
        return new SetView<E>(set1, set2){
            final /* synthetic */ Set val$set1;
            final /* synthetic */ Set val$set2;
            {
                this.val$set1 = set;
                this.val$set2 = set2;
                super(null);
            }

            public com.google.common.collect.UnmodifiableIterator<E> iterator() {
                Iterator<E> itr1 = this.val$set1.iterator();
                Iterator<E> itr2 = this.val$set2.iterator();
                return new com.google.common.collect.AbstractIterator<E>(this, itr1, itr2){
                    final /* synthetic */ Iterator val$itr1;
                    final /* synthetic */ Iterator val$itr2;
                    final /* synthetic */ 4 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$itr1 = iterator;
                        this.val$itr2 = iterator2;
                    }

                    public E computeNext() {
                        E elem2;
                        while (this.val$itr1.hasNext()) {
                            E elem1 = this.val$itr1.next();
                            if (this.this$0.val$set2.contains(elem1)) continue;
                            return (E)elem1;
                        }
                        do {
                            if (!this.val$itr2.hasNext()) return (E)this.endOfData();
                        } while (this.this$0.val$set1.contains(elem2 = this.val$itr2.next()));
                        return (E)elem2;
                    }
                };
            }

            public int size() {
                return Iterators.size(this.iterator());
            }

            public boolean isEmpty() {
                return this.val$set1.equals((Object)this.val$set2);
            }

            public boolean contains(Object element) {
                return this.val$set1.contains((Object)element) ^ this.val$set2.contains((Object)element);
            }
        };
    }

    public static <E> Set<E> filter(Set<E> unfiltered, Predicate<? super E> predicate) {
        if (unfiltered instanceof SortedSet) {
            return Sets.filter((SortedSet)unfiltered, predicate);
        }
        if (!(unfiltered instanceof FilteredSet)) return new FilteredSet<E>(Preconditions.checkNotNull(unfiltered), Preconditions.checkNotNull(predicate));
        FilteredSet filtered = (FilteredSet)unfiltered;
        Predicate<? super E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
        return new FilteredSet<E>((Set)filtered.unfiltered, combinedPredicate);
    }

    public static <E> SortedSet<E> filter(SortedSet<E> unfiltered, Predicate<? super E> predicate) {
        if (!(unfiltered instanceof FilteredSet)) return new FilteredSortedSet<E>(Preconditions.checkNotNull(unfiltered), Preconditions.checkNotNull(predicate));
        FilteredSet filtered = (FilteredSet)((Object)unfiltered);
        Predicate<? super E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
        return new FilteredSortedSet<E>((SortedSet)filtered.unfiltered, combinedPredicate);
    }

    @GwtIncompatible
    public static <E> NavigableSet<E> filter(NavigableSet<E> unfiltered, Predicate<? super E> predicate) {
        if (!(unfiltered instanceof FilteredSet)) return new FilteredNavigableSet<E>(Preconditions.checkNotNull(unfiltered), Preconditions.checkNotNull(predicate));
        FilteredSet filtered = (FilteredSet)((Object)unfiltered);
        Predicate<? super E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
        return new FilteredNavigableSet<E>((NavigableSet)filtered.unfiltered, combinedPredicate);
    }

    public static <B> Set<List<B>> cartesianProduct(List<? extends Set<? extends B>> sets) {
        return CartesianSet.create(sets);
    }

    public static <B> Set<List<B>> cartesianProduct(Set<? extends B> ... sets) {
        return Sets.cartesianProduct(Arrays.asList(sets));
    }

    @GwtCompatible(serializable=false)
    public static <E> Set<Set<E>> powerSet(Set<E> set) {
        return new PowerSet<E>(set);
    }

    static int hashCodeImpl(Set<?> s) {
        int hashCode = 0;
        Iterator<?> i$ = s.iterator();
        while (i$.hasNext()) {
            ? o = i$.next();
            hashCode += o != null ? o.hashCode() : 0;
            hashCode = ~(~hashCode);
        }
        return hashCode;
    }

    static boolean equalsImpl(Set<?> s, @Nullable Object object) {
        if (s == object) {
            return true;
        }
        if (!(object instanceof Set)) return false;
        Set o = (Set)object;
        try {
            if (s.size() != o.size()) return false;
            if (!s.containsAll(o)) return false;
            return true;
        }
        catch (NullPointerException ignored) {
            return false;
        }
        catch (ClassCastException ignored) {
            return false;
        }
    }

    @GwtIncompatible
    public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> set) {
        if (set instanceof ImmutableSortedSet) return set;
        if (!(set instanceof UnmodifiableNavigableSet)) return new UnmodifiableNavigableSet<E>(set);
        return set;
    }

    @GwtIncompatible
    public static <E> NavigableSet<E> synchronizedNavigableSet(NavigableSet<E> navigableSet) {
        return Synchronized.navigableSet(navigableSet);
    }

    static boolean removeAllImpl(Set<?> set, Iterator<?> iterator) {
        boolean changed = false;
        while (iterator.hasNext()) {
            changed |= set.remove(iterator.next());
        }
        return changed;
    }

    static boolean removeAllImpl(Set<?> set, Collection<?> collection) {
        Preconditions.checkNotNull(collection);
        if (collection instanceof Multiset) {
            collection = ((Multiset)collection).elementSet();
        }
        if (!(collection instanceof Set)) return Sets.removeAllImpl(set, collection.iterator());
        if (collection.size() <= set.size()) return Sets.removeAllImpl(set, collection.iterator());
        return Iterators.removeAll(set.iterator(), collection);
    }

    @Beta
    @GwtIncompatible
    public static <K extends Comparable<? super K>> NavigableSet<K> subSet(NavigableSet<K> set, Range<K> range) {
        boolean bl;
        if (set.comparator() != null && set.comparator() != Ordering.natural() && range.hasLowerBound() && range.hasUpperBound()) {
            Preconditions.checkArgument((boolean)(set.comparator().compare(range.lowerEndpoint(), range.upperEndpoint()) <= 0), (Object)"set is using a custom comparator which is inconsistent with the natural ordering.");
        }
        if (range.hasLowerBound() && range.hasUpperBound()) {
            boolean bl2;
            boolean bl3 = range.lowerBoundType() == BoundType.CLOSED;
            if (range.upperBoundType() == BoundType.CLOSED) {
                bl2 = true;
                return set.subSet(range.lowerEndpoint(), (boolean)bl3, range.upperEndpoint(), (boolean)bl2);
            }
            bl2 = false;
            return set.subSet(range.lowerEndpoint(), (boolean)bl3, range.upperEndpoint(), (boolean)bl2);
        }
        if (range.hasLowerBound()) {
            boolean bl4;
            if (range.lowerBoundType() == BoundType.CLOSED) {
                bl4 = true;
                return set.tailSet(range.lowerEndpoint(), (boolean)bl4);
            }
            bl4 = false;
            return set.tailSet(range.lowerEndpoint(), (boolean)bl4);
        }
        if (!range.hasUpperBound()) return Preconditions.checkNotNull(set);
        if (range.upperBoundType() == BoundType.CLOSED) {
            bl = true;
            return set.headSet(range.upperEndpoint(), (boolean)bl);
        }
        bl = false;
        return set.headSet(range.upperEndpoint(), (boolean)bl);
    }
}

