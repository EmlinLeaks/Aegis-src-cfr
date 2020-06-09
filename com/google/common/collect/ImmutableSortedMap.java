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
import com.google.common.base.Preconditions;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMapFauxverideShim;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.RegularImmutableList;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public final class ImmutableSortedMap<K, V>
extends ImmutableSortedMapFauxverideShim<K, V>
implements NavigableMap<K, V> {
    private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
    private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new ImmutableSortedMap<C, E>(ImmutableSortedSet.emptySet(Ordering.<C>natural()), ImmutableList.<E>of());
    private final transient RegularImmutableSortedSet<K> keySet;
    private final transient ImmutableList<V> valueList;
    private transient ImmutableSortedMap<K, V> descendingMap;
    private static final long serialVersionUID = 0L;

    static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> comparator) {
        if (!Ordering.natural().equals(comparator)) return new ImmutableSortedMap<K, E>(ImmutableSortedSet.emptySet(comparator), ImmutableList.<E>of());
        return ImmutableSortedMap.of();
    }

    public static <K, V> ImmutableSortedMap<K, V> of() {
        return NATURAL_EMPTY_MAP;
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1) {
        return ImmutableSortedMap.of(Ordering.<C>natural(), k1, v1);
    }

    private static <K, V> ImmutableSortedMap<K, V> of(Comparator<? super K> comparator, K k1, V v1) {
        return new ImmutableSortedMap<K, V>(new RegularImmutableSortedSet<K>(ImmutableList.of(k1), Preconditions.checkNotNull(comparator)), ImmutableList.of(v1));
    }

    private static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> ofEntries(ImmutableMapEntry<K, V> ... entries) {
        return ImmutableSortedMap.fromEntries(Ordering.<C>natural(), (boolean)false, entries, (int)entries.length);
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2) {
        return ImmutableSortedMap.ofEntries(ImmutableSortedMap.entryOf(k1, v1), ImmutableSortedMap.entryOf(k2, v2));
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return ImmutableSortedMap.ofEntries(ImmutableSortedMap.entryOf(k1, v1), ImmutableSortedMap.entryOf(k2, v2), ImmutableSortedMap.entryOf(k3, v3));
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return ImmutableSortedMap.ofEntries(ImmutableSortedMap.entryOf(k1, v1), ImmutableSortedMap.entryOf(k2, v2), ImmutableSortedMap.entryOf(k3, v3), ImmutableSortedMap.entryOf(k4, v4));
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return ImmutableSortedMap.ofEntries(ImmutableSortedMap.entryOf(k1, v1), ImmutableSortedMap.entryOf(k2, v2), ImmutableSortedMap.entryOf(k3, v3), ImmutableSortedMap.entryOf(k4, v4), ImmutableSortedMap.entryOf(k5, v5));
    }

    public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
        Ordering naturalOrder = (Ordering)NATURAL_ORDER;
        return ImmutableSortedMap.copyOfInternal(map, naturalOrder);
    }

    public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
        return ImmutableSortedMap.copyOfInternal(map, Preconditions.checkNotNull(comparator));
    }

    @Beta
    public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
        Ordering naturalOrder = (Ordering)NATURAL_ORDER;
        return ImmutableSortedMap.copyOf(entries, naturalOrder);
    }

    @Beta
    public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries, Comparator<? super K> comparator) {
        return ImmutableSortedMap.fromEntries(Preconditions.checkNotNull(comparator), (boolean)false, entries);
    }

    public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> map) {
        Comparator<Object> comparator = map.comparator();
        if (comparator == null) {
            comparator = NATURAL_ORDER;
        }
        if (!(map instanceof ImmutableSortedMap)) return ImmutableSortedMap.fromEntries(comparator, (boolean)true, map.entrySet());
        ImmutableSortedMap kvMap = (ImmutableSortedMap)map;
        if (kvMap.isPartialView()) return ImmutableSortedMap.fromEntries(comparator, (boolean)true, map.entrySet());
        return kvMap;
    }

    private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
        boolean sameComparator = false;
        if (map instanceof SortedMap) {
            SortedMap sortedMap = (SortedMap)map;
            Comparator<K> comparator2 = sortedMap.comparator();
            sameComparator = comparator2 == null ? comparator == NATURAL_ORDER : comparator.equals(comparator2);
        }
        if (!sameComparator) return ImmutableSortedMap.fromEntries(comparator, (boolean)sameComparator, map.entrySet());
        if (!(map instanceof ImmutableSortedMap)) return ImmutableSortedMap.fromEntries(comparator, (boolean)sameComparator, map.entrySet());
        ImmutableSortedMap kvMap = (ImmutableSortedMap)map;
        if (kvMap.isPartialView()) return ImmutableSortedMap.fromEntries(comparator, (boolean)sameComparator, map.entrySet());
        return kvMap;
    }

    private static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean sameComparator, Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
        Map.Entry[] entryArray = Iterables.toArray(entries, EMPTY_ENTRY_ARRAY);
        return ImmutableSortedMap.fromEntries(comparator, (boolean)sameComparator, entryArray, (int)entryArray.length);
    }

    private static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean sameComparator, Map.Entry<K, V>[] entryArray, int size) {
        switch (size) {
            case 0: {
                return ImmutableSortedMap.emptyMap(comparator);
            }
            case 1: {
                return ImmutableSortedMap.of(comparator, entryArray[0].getKey(), entryArray[0].getValue());
            }
        }
        Object[] keys = new Object[size];
        Object[] values = new Object[size];
        if (sameComparator) {
            int i = 0;
            while (i < size) {
                K key = entryArray[i].getKey();
                V value = entryArray[i].getValue();
                CollectPreconditions.checkEntryNotNull(key, value);
                keys[i] = key;
                values[i] = value;
                ++i;
            }
            return new ImmutableSortedMap<K, E>(new RegularImmutableSortedSet<K>(new RegularImmutableList<E>((Object[])keys), comparator), new RegularImmutableList<E>((Object[])values));
        }
        Arrays.sort(entryArray, (int)0, (int)size, Ordering.from(comparator).onKeys());
        K prevKey = entryArray[0].getKey();
        keys[0] = prevKey;
        values[0] = entryArray[0].getValue();
        int i = 1;
        while (i < size) {
            K key = entryArray[i].getKey();
            V value = entryArray[i].getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            keys[i] = key;
            values[i] = value;
            ImmutableSortedMap.checkNoConflict((boolean)(comparator.compare(prevKey, key) != 0), (String)"key", entryArray[i - 1], entryArray[i]);
            prevKey = key;
            ++i;
        }
        return new ImmutableSortedMap<K, E>(new RegularImmutableSortedSet<K>(new RegularImmutableList<E>((Object[])keys), comparator), new RegularImmutableList<E>((Object[])values));
    }

    public static <K extends Comparable<?>, V> Builder<K, V> naturalOrder() {
        return new Builder<C, V>(Ordering.<C>natural());
    }

    public static <K, V> Builder<K, V> orderedBy(Comparator<K> comparator) {
        return new Builder<K, V>(comparator);
    }

    public static <K extends Comparable<?>, V> Builder<K, V> reverseOrder() {
        return new Builder<S, V>(Ordering.natural().reverse());
    }

    ImmutableSortedMap(RegularImmutableSortedSet<K> keySet, ImmutableList<V> valueList) {
        this(keySet, valueList, null);
    }

    ImmutableSortedMap(RegularImmutableSortedSet<K> keySet, ImmutableList<V> valueList, ImmutableSortedMap<K, V> descendingMap) {
        this.keySet = keySet;
        this.valueList = valueList;
        this.descendingMap = descendingMap;
    }

    @Override
    public int size() {
        return this.valueList.size();
    }

    @Override
    public V get(@Nullable Object key) {
        V v;
        int index = this.keySet.indexOf((Object)key);
        if (index == -1) {
            v = null;
            return (V)((V)v);
        }
        v = (V)this.valueList.get((int)index);
        return (V)v;
    }

    @Override
    boolean isPartialView() {
        if (this.keySet.isPartialView()) return true;
        if (this.valueList.isPartialView()) return true;
        return false;
    }

    @Override
    public ImmutableSet<Map.Entry<K, V>> entrySet() {
        return super.entrySet();
    }

    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        EntrySet entrySet;
        if (this.isEmpty()) {
            entrySet = ImmutableSet.of();
            return entrySet;
        }
        class EntrySet
        extends com.google.common.collect.ImmutableMapEntrySet<K, V> {
            final /* synthetic */ ImmutableSortedMap this$0;

            EntrySet(ImmutableSortedMap immutableSortedMap) {
                this.this$0 = immutableSortedMap;
            }

            public com.google.common.collect.UnmodifiableIterator<Map.Entry<K, V>> iterator() {
                return this.asList().iterator();
            }

            ImmutableList<Map.Entry<K, V>> createAsList() {
                return new com.google.common.collect.ImmutableAsList<Map.Entry<K, V>>((EntrySet)this){
                    final /* synthetic */ EntrySet this$1;
                    {
                        this.this$1 = entrySet;
                    }

                    public Map.Entry<K, V> get(int index) {
                        return Maps.immutableEntry(ImmutableSortedMap.access$200((ImmutableSortedMap)this.this$1.this$0).asList().get((int)index), ImmutableSortedMap.access$300((ImmutableSortedMap)this.this$1.this$0).get((int)index));
                    }

                    ImmutableCollection<Map.Entry<K, V>> delegateCollection() {
                        return this.this$1;
                    }
                };
            }

            com.google.common.collect.ImmutableMap<K, V> map() {
                return this.this$0;
            }
        }
        entrySet = new EntrySet((ImmutableSortedMap)this);
        return entrySet;
    }

    @Override
    public ImmutableSortedSet<K> keySet() {
        return this.keySet;
    }

    @Override
    public ImmutableCollection<V> values() {
        return this.valueList;
    }

    @Override
    public Comparator<? super K> comparator() {
        return ((ImmutableSortedSet)this.keySet()).comparator();
    }

    @Override
    public K firstKey() {
        return (K)((ImmutableSortedSet)this.keySet()).first();
    }

    @Override
    public K lastKey() {
        return (K)((ImmutableSortedSet)this.keySet()).last();
    }

    private ImmutableSortedMap<K, V> getSubMap(int fromIndex, int toIndex) {
        if (fromIndex == 0 && toIndex == this.size()) {
            return this;
        }
        if (fromIndex != toIndex) return new ImmutableSortedMap<K, V>(this.keySet.getSubSet((int)fromIndex, (int)toIndex), this.valueList.subList((int)fromIndex, (int)toIndex));
        return ImmutableSortedMap.emptyMap(this.comparator());
    }

    @Override
    public ImmutableSortedMap<K, V> headMap(K toKey) {
        return this.headMap(toKey, (boolean)false);
    }

    @Override
    public ImmutableSortedMap<K, V> headMap(K toKey, boolean inclusive) {
        return this.getSubMap((int)0, (int)this.keySet.headIndex(Preconditions.checkNotNull(toKey), (boolean)inclusive));
    }

    @Override
    public ImmutableSortedMap<K, V> subMap(K fromKey, K toKey) {
        return this.subMap(fromKey, (boolean)true, toKey, (boolean)false);
    }

    @Override
    public ImmutableSortedMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        Preconditions.checkNotNull(fromKey);
        Preconditions.checkNotNull(toKey);
        Preconditions.checkArgument((boolean)(this.comparator().compare(fromKey, toKey) <= 0), (String)"expected fromKey <= toKey but %s > %s", fromKey, toKey);
        return ((ImmutableSortedMap)this.headMap(toKey, (boolean)toInclusive)).tailMap(fromKey, (boolean)fromInclusive);
    }

    @Override
    public ImmutableSortedMap<K, V> tailMap(K fromKey) {
        return this.tailMap(fromKey, (boolean)true);
    }

    @Override
    public ImmutableSortedMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return this.getSubMap((int)this.keySet.tailIndex(Preconditions.checkNotNull(fromKey), (boolean)inclusive), (int)this.size());
    }

    @Override
    public Map.Entry<K, V> lowerEntry(K key) {
        return ((ImmutableSortedMap)this.headMap(key, (boolean)false)).lastEntry();
    }

    @Override
    public K lowerKey(K key) {
        return (K)Maps.keyOrNull(this.lowerEntry(key));
    }

    @Override
    public Map.Entry<K, V> floorEntry(K key) {
        return ((ImmutableSortedMap)this.headMap(key, (boolean)true)).lastEntry();
    }

    @Override
    public K floorKey(K key) {
        return (K)Maps.keyOrNull(this.floorEntry(key));
    }

    @Override
    public Map.Entry<K, V> ceilingEntry(K key) {
        return ((ImmutableSortedMap)this.tailMap(key, (boolean)true)).firstEntry();
    }

    @Override
    public K ceilingKey(K key) {
        return (K)Maps.keyOrNull(this.ceilingEntry(key));
    }

    @Override
    public Map.Entry<K, V> higherEntry(K key) {
        return ((ImmutableSortedMap)this.tailMap(key, (boolean)false)).firstEntry();
    }

    @Override
    public K higherKey(K key) {
        return (K)Maps.keyOrNull(this.higherEntry(key));
    }

    @Override
    public Map.Entry<K, V> firstEntry() {
        if (this.isEmpty()) {
            return null;
        }
        Map.Entry entry = (Map.Entry)((ImmutableSet)this.entrySet()).asList().get((int)0);
        return entry;
    }

    @Override
    public Map.Entry<K, V> lastEntry() {
        if (this.isEmpty()) {
            return null;
        }
        Map.Entry entry = (Map.Entry)((ImmutableSet)this.entrySet()).asList().get((int)(this.size() - 1));
        return entry;
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final Map.Entry<K, V> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final Map.Entry<K, V> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableSortedMap<K, V> descendingMap() {
        ImmutableSortedMap<Object, V> result = this.descendingMap;
        if (result != null) return result;
        if (!this.isEmpty()) return new ImmutableSortedMap<K, V>((RegularImmutableSortedSet)this.keySet.descendingSet(), this.valueList.reverse(), this);
        return ImmutableSortedMap.emptyMap(Ordering.from(this.comparator()).reverse());
    }

    @Override
    public ImmutableSortedSet<K> navigableKeySet() {
        return this.keySet;
    }

    @Override
    public ImmutableSortedSet<K> descendingKeySet() {
        return this.keySet.descendingSet();
    }

    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }

    static /* synthetic */ ImmutableSortedMap access$000(Comparator x0, Object x1, Object x2) {
        return ImmutableSortedMap.of(x0, x1, x2);
    }

    static /* synthetic */ ImmutableSortedMap access$100(Comparator x0, boolean x1, Map.Entry[] x2, int x3) {
        return ImmutableSortedMap.fromEntries(x0, (boolean)x1, x2, (int)x3);
    }

    static /* synthetic */ RegularImmutableSortedSet access$200(ImmutableSortedMap x0) {
        return x0.keySet;
    }

    static /* synthetic */ ImmutableList access$300(ImmutableSortedMap x0) {
        return x0.valueList;
    }
}

