/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Synchronized;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class Synchronized {
    private Synchronized() {
    }

    private static <E> Collection<E> collection(Collection<E> collection, @Nullable Object mutex) {
        return new SynchronizedCollection<E>(collection, (Object)mutex, null);
    }

    @VisibleForTesting
    static <E> Set<E> set(Set<E> set, @Nullable Object mutex) {
        return new SynchronizedSet<E>(set, (Object)mutex);
    }

    private static <E> SortedSet<E> sortedSet(SortedSet<E> set, @Nullable Object mutex) {
        return new SynchronizedSortedSet<E>(set, (Object)mutex);
    }

    private static <E> List<E> list(List<E> list, @Nullable Object mutex) {
        SynchronizedList synchronizedList;
        if (list instanceof RandomAccess) {
            synchronizedList = new SynchronizedRandomAccessList<E>(list, (Object)mutex);
            return synchronizedList;
        }
        synchronizedList = new SynchronizedList<E>(list, (Object)mutex);
        return synchronizedList;
    }

    static <E> Multiset<E> multiset(Multiset<E> multiset, @Nullable Object mutex) {
        if (multiset instanceof SynchronizedMultiset) return multiset;
        if (!(multiset instanceof ImmutableMultiset)) return new SynchronizedMultiset<E>(multiset, (Object)mutex);
        return multiset;
    }

    static <K, V> Multimap<K, V> multimap(Multimap<K, V> multimap, @Nullable Object mutex) {
        if (multimap instanceof SynchronizedMultimap) return multimap;
        if (!(multimap instanceof ImmutableMultimap)) return new SynchronizedMultimap<K, V>(multimap, (Object)mutex);
        return multimap;
    }

    static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> multimap, @Nullable Object mutex) {
        if (multimap instanceof SynchronizedListMultimap) return multimap;
        if (!(multimap instanceof ImmutableListMultimap)) return new SynchronizedListMultimap<K, V>(multimap, (Object)mutex);
        return multimap;
    }

    static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> multimap, @Nullable Object mutex) {
        if (multimap instanceof SynchronizedSetMultimap) return multimap;
        if (!(multimap instanceof ImmutableSetMultimap)) return new SynchronizedSetMultimap<K, V>(multimap, (Object)mutex);
        return multimap;
    }

    static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> multimap, @Nullable Object mutex) {
        if (!(multimap instanceof SynchronizedSortedSetMultimap)) return new SynchronizedSortedSetMultimap<K, V>(multimap, (Object)mutex);
        return multimap;
    }

    private static <E> Collection<E> typePreservingCollection(Collection<E> collection, @Nullable Object mutex) {
        if (collection instanceof SortedSet) {
            return Synchronized.sortedSet((SortedSet)collection, (Object)mutex);
        }
        if (collection instanceof Set) {
            return Synchronized.set((Set)collection, (Object)mutex);
        }
        if (!(collection instanceof List)) return Synchronized.collection(collection, (Object)mutex);
        return Synchronized.list((List)collection, (Object)mutex);
    }

    private static <E> Set<E> typePreservingSet(Set<E> set, @Nullable Object mutex) {
        if (!(set instanceof SortedSet)) return Synchronized.set(set, (Object)mutex);
        return Synchronized.sortedSet((SortedSet)set, (Object)mutex);
    }

    @VisibleForTesting
    static <K, V> Map<K, V> map(Map<K, V> map, @Nullable Object mutex) {
        return new SynchronizedMap<K, V>(map, (Object)mutex);
    }

    static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> sortedMap, @Nullable Object mutex) {
        return new SynchronizedSortedMap<K, V>(sortedMap, (Object)mutex);
    }

    static <K, V> BiMap<K, V> biMap(BiMap<K, V> bimap, @Nullable Object mutex) {
        if (bimap instanceof SynchronizedBiMap) return bimap;
        if (!(bimap instanceof ImmutableBiMap)) return new SynchronizedBiMap<K, V>(bimap, (Object)mutex, null, null);
        return bimap;
    }

    @GwtIncompatible
    static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet, @Nullable Object mutex) {
        return new SynchronizedNavigableSet<E>(navigableSet, (Object)mutex);
    }

    @GwtIncompatible
    static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet) {
        return Synchronized.navigableSet(navigableSet, null);
    }

    @GwtIncompatible
    static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap) {
        return Synchronized.navigableMap(navigableMap, null);
    }

    @GwtIncompatible
    static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap, @Nullable Object mutex) {
        return new SynchronizedNavigableMap<K, V>(navigableMap, (Object)mutex);
    }

    @GwtIncompatible
    private static <K, V> Map.Entry<K, V> nullableSynchronizedEntry(@Nullable Map.Entry<K, V> entry, @Nullable Object mutex) {
        if (entry != null) return new SynchronizedEntry<K, V>(entry, (Object)mutex);
        return null;
    }

    static <E> Queue<E> queue(Queue<E> queue, @Nullable Object mutex) {
        SynchronizedQueue<E> synchronizedQueue;
        if (queue instanceof SynchronizedQueue) {
            synchronizedQueue = queue;
            return synchronizedQueue;
        }
        synchronizedQueue = new SynchronizedQueue<E>(queue, (Object)mutex);
        return synchronizedQueue;
    }

    static <E> Deque<E> deque(Deque<E> deque, @Nullable Object mutex) {
        return new SynchronizedDeque<E>(deque, (Object)mutex);
    }

    static /* synthetic */ SortedSet access$100(SortedSet x0, Object x1) {
        return Synchronized.sortedSet(x0, (Object)x1);
    }

    static /* synthetic */ List access$200(List x0, Object x1) {
        return Synchronized.list(x0, (Object)x1);
    }

    static /* synthetic */ Set access$300(Set x0, Object x1) {
        return Synchronized.typePreservingSet(x0, (Object)x1);
    }

    static /* synthetic */ Collection access$400(Collection x0, Object x1) {
        return Synchronized.typePreservingCollection(x0, (Object)x1);
    }

    static /* synthetic */ Collection access$500(Collection x0, Object x1) {
        return Synchronized.collection(x0, (Object)x1);
    }

    static /* synthetic */ Map.Entry access$700(Map.Entry x0, Object x1) {
        return Synchronized.nullableSynchronizedEntry(x0, (Object)x1);
    }
}

