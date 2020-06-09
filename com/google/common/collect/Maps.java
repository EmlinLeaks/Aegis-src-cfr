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
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.BiMap;
import com.google.common.collect.BoundType;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingNavigableSet;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.ImmutableEntry;
import com.google.common.collect.ImmutableEnumMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.SortedMapDifference;
import com.google.common.collect.Synchronized;
import com.google.common.collect.TransformedIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Maps {
    static final Joiner.MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator((String)"=");

    private Maps() {
    }

    static <K> Function<Map.Entry<K, ?>, K> keyFunction() {
        return EntryFunction.KEY;
    }

    static <V> Function<Map.Entry<?, V>, V> valueFunction() {
        return EntryFunction.VALUE;
    }

    static <K, V> Iterator<K> keyIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        return Iterators.transform(entryIterator, Maps.<K>keyFunction());
    }

    static <K, V> Iterator<V> valueIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        return Iterators.transform(entryIterator, Maps.<V>valueFunction());
    }

    @GwtCompatible(serializable=true)
    @Beta
    public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
        if (map instanceof ImmutableEnumMap) {
            return (ImmutableEnumMap)map;
        }
        if (map.isEmpty()) {
            return ImmutableMap.of();
        }
        Iterator<Map.Entry<K, V>> i$ = map.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            Preconditions.checkNotNull(entry.getKey());
            Preconditions.checkNotNull(entry.getValue());
        }
        return ImmutableEnumMap.asImmutable(new EnumMap<K, V>(map));
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<K, V>((int)Maps.capacity((int)expectedSize));
    }

    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            CollectPreconditions.checkNonnegative((int)expectedSize, (String)"expectedSize");
            return expectedSize + 1;
        }
        if (expectedSize >= 1073741824) return Integer.MAX_VALUE;
        return (int)((float)expectedSize / 0.75f + 1.0f);
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
        return new LinkedHashMap<K, V>((int)Maps.capacity((int)expectedSize));
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
        return new LinkedHashMap<K, V>(map);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new MapMaker().makeMap();
    }

    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map) {
        return new TreeMap<K, V>(map);
    }

    public static <C, K extends C, V> TreeMap<K, V> newTreeMap(@Nullable Comparator<C> comparator) {
        return new TreeMap<C, V>(comparator);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        return new EnumMap<K, V>(Preconditions.checkNotNull(type));
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap<K, V>(map);
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }

    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        if (!(left instanceof SortedMap)) return Maps.difference(left, right, Equivalence.equals());
        SortedMap sortedLeft = (SortedMap)left;
        return Maps.difference(sortedLeft, right);
    }

    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super V> valueEquivalence) {
        Preconditions.checkNotNull(valueEquivalence);
        LinkedHashMap<K, V> onlyOnLeft = Maps.newLinkedHashMap();
        LinkedHashMap<? extends K, ? extends V> onlyOnRight = new LinkedHashMap<K, V>(right);
        LinkedHashMap<K, V> onBoth = Maps.newLinkedHashMap();
        LinkedHashMap<K, V> differences = Maps.newLinkedHashMap();
        Maps.doDifference(left, right, valueEquivalence, onlyOnLeft, onlyOnRight, onBoth, differences);
        return new MapDifferenceImpl<K, V>(onlyOnLeft, onlyOnRight, onBoth, differences);
    }

    private static <K, V> void doDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super V> valueEquivalence, Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences) {
        Iterator<Map.Entry<K, V>> i$ = left.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            K leftKey = entry.getKey();
            V leftValue = entry.getValue();
            if (right.containsKey(leftKey)) {
                V rightValue = onlyOnRight.remove(leftKey);
                if (valueEquivalence.equivalent(leftValue, rightValue)) {
                    onBoth.put(leftKey, leftValue);
                    continue;
                }
                differences.put(leftKey, ValueDifferenceImpl.create(leftValue, rightValue));
                continue;
            }
            onlyOnLeft.put(leftKey, leftValue);
        }
    }

    private static <K, V> Map<K, V> unmodifiableMap(Map<K, ? extends V> map) {
        if (!(map instanceof SortedMap)) return Collections.unmodifiableMap(map);
        return Collections.unmodifiableSortedMap((SortedMap)map);
    }

    public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        Comparator<K> comparator = Maps.orNaturalOrder(left.comparator());
        TreeMap<K, V> onlyOnLeft = Maps.newTreeMap(comparator);
        TreeMap<? extends K, ? extends V> onlyOnRight = Maps.newTreeMap(comparator);
        onlyOnRight.putAll(right);
        TreeMap<K, V> onBoth = Maps.newTreeMap(comparator);
        TreeMap<K, V> differences = Maps.newTreeMap(comparator);
        Maps.doDifference(left, right, Equivalence.equals(), onlyOnLeft, onlyOnRight, onBoth, differences);
        return new SortedMapDifferenceImpl<K, V>(onlyOnLeft, onlyOnRight, onBoth, differences);
    }

    static <E> Comparator<? super E> orNaturalOrder(@Nullable Comparator<? super E> comparator) {
        if (comparator == null) return Ordering.natural();
        return comparator;
    }

    public static <K, V> Map<K, V> asMap(Set<K> set, Function<? super K, V> function) {
        return new AsMapView<K, V>(set, function);
    }

    public static <K, V> SortedMap<K, V> asMap(SortedSet<K> set, Function<? super K, V> function) {
        return new SortedAsMapView<K, V>(set, function);
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> asMap(NavigableSet<K> set, Function<? super K, V> function) {
        return new NavigableAsMapView<K, V>(set, function);
    }

    static <K, V> Iterator<Map.Entry<K, V>> asMapEntryIterator(Set<K> set, Function<? super K, V> function) {
        return new TransformedIterator<K, Map.Entry<K, V>>(set.iterator(), function){
            final /* synthetic */ Function val$function;
            {
                this.val$function = function;
                super(x0);
            }

            Map.Entry<K, V> transform(K key) {
                return Maps.immutableEntry(key, this.val$function.apply(key));
            }
        };
    }

    private static <E> Set<E> removeOnlySet(Set<E> set) {
        return new ForwardingSet<E>(set){
            final /* synthetic */ Set val$set;
            {
                this.val$set = set;
            }

            protected Set<E> delegate() {
                return this.val$set;
            }

            public boolean add(E element) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean addAll(Collection<? extends E> es) {
                throw new java.lang.UnsupportedOperationException();
            }
        };
    }

    private static <E> SortedSet<E> removeOnlySortedSet(SortedSet<E> set) {
        return new ForwardingSortedSet<E>(set){
            final /* synthetic */ SortedSet val$set;
            {
                this.val$set = sortedSet;
            }

            protected SortedSet<E> delegate() {
                return this.val$set;
            }

            public boolean add(E element) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean addAll(Collection<? extends E> es) {
                throw new java.lang.UnsupportedOperationException();
            }

            public SortedSet<E> headSet(E toElement) {
                return Maps.access$300(super.headSet(toElement));
            }

            public SortedSet<E> subSet(E fromElement, E toElement) {
                return Maps.access$300(super.subSet(fromElement, toElement));
            }

            public SortedSet<E> tailSet(E fromElement) {
                return Maps.access$300(super.tailSet(fromElement));
            }
        };
    }

    @GwtIncompatible
    private static <E> NavigableSet<E> removeOnlyNavigableSet(NavigableSet<E> set) {
        return new ForwardingNavigableSet<E>(set){
            final /* synthetic */ NavigableSet val$set;
            {
                this.val$set = navigableSet;
            }

            protected NavigableSet<E> delegate() {
                return this.val$set;
            }

            public boolean add(E element) {
                throw new java.lang.UnsupportedOperationException();
            }

            public boolean addAll(Collection<? extends E> es) {
                throw new java.lang.UnsupportedOperationException();
            }

            public SortedSet<E> headSet(E toElement) {
                return Maps.access$300(super.headSet(toElement));
            }

            public SortedSet<E> subSet(E fromElement, E toElement) {
                return Maps.access$300(super.subSet(fromElement, toElement));
            }

            public SortedSet<E> tailSet(E fromElement) {
                return Maps.access$300(super.tailSet(fromElement));
            }

            public NavigableSet<E> headSet(E toElement, boolean inclusive) {
                return Maps.access$400(super.headSet(toElement, (boolean)inclusive));
            }

            public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
                return Maps.access$400(super.tailSet(fromElement, (boolean)inclusive));
            }

            public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
                return Maps.access$400(super.subSet(fromElement, (boolean)fromInclusive, toElement, (boolean)toInclusive));
            }

            public NavigableSet<E> descendingSet() {
                return Maps.access$400(super.descendingSet());
            }
        };
    }

    public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> keys, Function<? super K, V> valueFunction) {
        return Maps.toMap(keys.iterator(), valueFunction);
    }

    public static <K, V> ImmutableMap<K, V> toMap(Iterator<K> keys, Function<? super K, V> valueFunction) {
        Preconditions.checkNotNull(valueFunction);
        LinkedHashMap<K, V> builder = Maps.newLinkedHashMap();
        while (keys.hasNext()) {
            K key = keys.next();
            builder.put(key, valueFunction.apply(key));
        }
        return ImmutableMap.copyOf(builder);
    }

    @CanIgnoreReturnValue
    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction) {
        return Maps.uniqueIndex(values.iterator(), keyFunction);
    }

    @CanIgnoreReturnValue
    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> values, Function<? super V, K> keyFunction) {
        Preconditions.checkNotNull(keyFunction);
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        while (values.hasNext()) {
            V value = values.next();
            builder.put(keyFunction.apply(value), value);
        }
        try {
            return builder.build();
        }
        catch (IllegalArgumentException duplicateKeys) {
            throw new IllegalArgumentException((String)(duplicateKeys.getMessage() + ". To index multiple values under a key, use Multimaps.index."));
        }
    }

    @GwtIncompatible
    public static ImmutableMap<String, String> fromProperties(Properties properties) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            builder.put(key, properties.getProperty((String)key));
        }
        return builder.build();
    }

    @GwtCompatible(serializable=true)
    public static <K, V> Map.Entry<K, V> immutableEntry(@Nullable K key, @Nullable V value) {
        return new ImmutableEntry<K, V>(key, value);
    }

    static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(Set<Map.Entry<K, V>> entrySet) {
        return new UnmodifiableEntrySet<K, V>(Collections.unmodifiableSet(entrySet));
    }

    static <K, V> Map.Entry<K, V> unmodifiableEntry(Map.Entry<? extends K, ? extends V> entry) {
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V>(entry){
            final /* synthetic */ Map.Entry val$entry;
            {
                this.val$entry = entry;
            }

            public K getKey() {
                return (K)this.val$entry.getKey();
            }

            public V getValue() {
                return (V)this.val$entry.getValue();
            }
        };
    }

    static <K, V> UnmodifiableIterator<Map.Entry<K, V>> unmodifiableEntryIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        return new UnmodifiableIterator<Map.Entry<K, V>>(entryIterator){
            final /* synthetic */ Iterator val$entryIterator;
            {
                this.val$entryIterator = iterator;
            }

            public boolean hasNext() {
                return this.val$entryIterator.hasNext();
            }

            public Map.Entry<K, V> next() {
                return Maps.unmodifiableEntry((Map.Entry)this.val$entryIterator.next());
            }
        };
    }

    @Beta
    public static <A, B> Converter<A, B> asConverter(BiMap<A, B> bimap) {
        return new BiMapConverter<A, B>(bimap);
    }

    public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap) {
        return Synchronized.biMap(bimap, null);
    }

    public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> bimap) {
        return new UnmodifiableBiMap<K, V>(bimap, null);
    }

    public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> fromMap, Function<? super V1, V2> function) {
        return Maps.transformEntries(fromMap, Maps.asEntryTransformer(function));
    }

    public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> fromMap, Function<? super V1, V2> function) {
        return Maps.transformEntries(fromMap, Maps.asEntryTransformer(function));
    }

    @GwtIncompatible
    public static <K, V1, V2> NavigableMap<K, V2> transformValues(NavigableMap<K, V1> fromMap, Function<? super V1, V2> function) {
        return Maps.transformEntries(fromMap, Maps.asEntryTransformer(function));
    }

    public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesMap<K, V1, V2>(fromMap, transformer);
    }

    public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesSortedMap<K, V1, V2>(fromMap, transformer);
    }

    @GwtIncompatible
    public static <K, V1, V2> NavigableMap<K, V2> transformEntries(NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesNavigableMap<K, V1, V2>(fromMap, transformer);
    }

    static <K, V1, V2> EntryTransformer<K, V1, V2> asEntryTransformer(Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        return new EntryTransformer<K, V1, V2>(function){
            final /* synthetic */ Function val$function;
            {
                this.val$function = function;
            }

            public V2 transformEntry(K key, V1 value) {
                return (V2)this.val$function.apply(value);
            }
        };
    }

    static <K, V1, V2> Function<V1, V2> asValueToValueFunction(EntryTransformer<? super K, V1, V2> transformer, K key) {
        Preconditions.checkNotNull(transformer);
        return new Function<V1, V2>(transformer, key){
            final /* synthetic */ EntryTransformer val$transformer;
            final /* synthetic */ Object val$key;
            {
                this.val$transformer = entryTransformer;
                this.val$key = object;
            }

            public V2 apply(@Nullable V1 v1) {
                return (V2)this.val$transformer.transformEntry(this.val$key, v1);
            }
        };
    }

    static <K, V1, V2> Function<Map.Entry<K, V1>, V2> asEntryToValueFunction(EntryTransformer<? super K, ? super V1, V2> transformer) {
        Preconditions.checkNotNull(transformer);
        return new Function<Map.Entry<K, V1>, V2>(transformer){
            final /* synthetic */ EntryTransformer val$transformer;
            {
                this.val$transformer = entryTransformer;
            }

            public V2 apply(Map.Entry<K, V1> entry) {
                return (V2)this.val$transformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }

    static <V2, K, V1> Map.Entry<K, V2> transformEntry(EntryTransformer<? super K, ? super V1, V2> transformer, Map.Entry<K, V1> entry) {
        Preconditions.checkNotNull(transformer);
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V2>(entry, transformer){
            final /* synthetic */ Map.Entry val$entry;
            final /* synthetic */ EntryTransformer val$transformer;
            {
                this.val$entry = entry;
                this.val$transformer = entryTransformer;
            }

            public K getKey() {
                return (K)this.val$entry.getKey();
            }

            public V2 getValue() {
                return (V2)this.val$transformer.transformEntry(this.val$entry.getKey(), this.val$entry.getValue());
            }
        };
    }

    static <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> asEntryToEntryFunction(EntryTransformer<? super K, ? super V1, V2> transformer) {
        Preconditions.checkNotNull(transformer);
        return new Function<Map.Entry<K, V1>, Map.Entry<K, V2>>(transformer){
            final /* synthetic */ EntryTransformer val$transformer;
            {
                this.val$transformer = entryTransformer;
            }

            public Map.Entry<K, V2> apply(Map.Entry<K, V1> entry) {
                return Maps.transformEntry(this.val$transformer, entry);
            }
        };
    }

    static <K> Predicate<Map.Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> keyPredicate) {
        return Predicates.compose(keyPredicate, Maps.<K>keyFunction());
    }

    static <V> Predicate<Map.Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> valuePredicate) {
        return Predicates.compose(valuePredicate, Maps.<V>valueFunction());
    }

    public static <K, V> Map<K, V> filterKeys(Map<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        Map<? super K, Object> map;
        Preconditions.checkNotNull(keyPredicate);
        Predicate<Map.Entry<? super K, ?>> entryPredicate = Maps.keyPredicateOnEntries(keyPredicate);
        if (unfiltered instanceof AbstractFilteredMap) {
            map = Maps.filterFiltered((AbstractFilteredMap)unfiltered, entryPredicate);
            return map;
        }
        map = new FilteredKeyMap<K, V>(Preconditions.checkNotNull(unfiltered), keyPredicate, entryPredicate);
        return map;
    }

    public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        return Maps.filterEntries(unfiltered, Maps.keyPredicateOnEntries(keyPredicate));
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> filterKeys(NavigableMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        return Maps.filterEntries(unfiltered, Maps.keyPredicateOnEntries(keyPredicate));
    }

    public static <K, V> BiMap<K, V> filterKeys(BiMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        Preconditions.checkNotNull(keyPredicate);
        return Maps.filterEntries(unfiltered, Maps.keyPredicateOnEntries(keyPredicate));
    }

    public static <K, V> Map<K, V> filterValues(Map<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> filterValues(NavigableMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> BiMap<K, V> filterValues(BiMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Maps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> Map<K, V> filterEntries(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Map<K, V> map;
        Preconditions.checkNotNull(entryPredicate);
        if (unfiltered instanceof AbstractFilteredMap) {
            map = Maps.filterFiltered((AbstractFilteredMap)unfiltered, entryPredicate);
            return map;
        }
        map = new FilteredEntryMap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
        return map;
    }

    public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        SortedMap<K, V> sortedMap;
        Preconditions.checkNotNull(entryPredicate);
        if (unfiltered instanceof FilteredEntrySortedMap) {
            sortedMap = Maps.filterFiltered((FilteredEntrySortedMap)unfiltered, entryPredicate);
            return sortedMap;
        }
        sortedMap = new FilteredEntrySortedMap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
        return sortedMap;
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> filterEntries(NavigableMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        NavigableMap<K, V> navigableMap;
        Preconditions.checkNotNull(entryPredicate);
        if (unfiltered instanceof FilteredEntryNavigableMap) {
            navigableMap = Maps.filterFiltered((FilteredEntryNavigableMap)unfiltered, entryPredicate);
            return navigableMap;
        }
        navigableMap = new FilteredEntryNavigableMap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
        return navigableMap;
    }

    public static <K, V> BiMap<K, V> filterEntries(BiMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        BiMap<K, V> biMap;
        Preconditions.checkNotNull(unfiltered);
        Preconditions.checkNotNull(entryPredicate);
        if (unfiltered instanceof FilteredEntryBiMap) {
            biMap = Maps.filterFiltered((FilteredEntryBiMap)unfiltered, entryPredicate);
            return biMap;
        }
        biMap = new FilteredEntryBiMap<K, V>(unfiltered, entryPredicate);
        return biMap;
    }

    private static <K, V> Map<K, V> filterFiltered(AbstractFilteredMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        return new FilteredEntryMap<K, V>(map.unfiltered, Predicates.and(map.predicate, entryPredicate));
    }

    private static <K, V> SortedMap<K, V> filterFiltered(FilteredEntrySortedMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
        return new FilteredEntrySortedMap<K, V>(map.sortedMap(), predicate);
    }

    @GwtIncompatible
    private static <K, V> NavigableMap<K, V> filterFiltered(FilteredEntryNavigableMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(map.entryPredicate, entryPredicate);
        return new FilteredEntryNavigableMap<K, V>(map.unfiltered, predicate);
    }

    private static <K, V> BiMap<K, V> filterFiltered(FilteredEntryBiMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
        return new FilteredEntryBiMap<K, V>(map.unfiltered(), predicate);
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> map) {
        Preconditions.checkNotNull(map);
        if (!(map instanceof UnmodifiableNavigableMap)) return new UnmodifiableNavigableMap<K, V>(map);
        return map;
    }

    @Nullable
    private static <K, V> Map.Entry<K, V> unmodifiableOrNull(@Nullable Map.Entry<K, ? extends V> entry) {
        if (entry == null) {
            return null;
        }
        Map.Entry<K, ? extends V> entry2 = Maps.unmodifiableEntry(entry);
        return entry2;
    }

    @GwtIncompatible
    public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> navigableMap) {
        return Synchronized.navigableMap(navigableMap);
    }

    static <V> V safeGet(Map<?, V> map, @Nullable Object key) {
        Preconditions.checkNotNull(map);
        try {
            return (V)map.get((Object)key);
        }
        catch (ClassCastException e) {
            return (V)null;
        }
        catch (NullPointerException e) {
            return (V)null;
        }
    }

    static boolean safeContainsKey(Map<?, ?> map, Object key) {
        Preconditions.checkNotNull(map);
        try {
            return map.containsKey((Object)key);
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    static <V> V safeRemove(Map<?, V> map, Object key) {
        Preconditions.checkNotNull(map);
        try {
            return (V)map.remove((Object)key);
        }
        catch (ClassCastException e) {
            return (V)null;
        }
        catch (NullPointerException e) {
            return (V)null;
        }
    }

    static boolean containsKeyImpl(Map<?, ?> map, @Nullable Object key) {
        return Iterators.contains(Maps.keyIterator(map.entrySet().iterator()), (Object)key);
    }

    static boolean containsValueImpl(Map<?, ?> map, @Nullable Object value) {
        return Iterators.contains(Maps.valueIterator(map.entrySet().iterator()), (Object)value);
    }

    static <K, V> boolean containsEntryImpl(Collection<Map.Entry<K, V>> c, Object o) {
        if (o instanceof Map.Entry) return c.contains(Maps.unmodifiableEntry((Map.Entry)o));
        return false;
    }

    static <K, V> boolean removeEntryImpl(Collection<Map.Entry<K, V>> c, Object o) {
        if (o instanceof Map.Entry) return c.remove(Maps.unmodifiableEntry((Map.Entry)o));
        return false;
    }

    static boolean equalsImpl(Map<?, ?> map, Object object) {
        if (map == object) {
            return true;
        }
        if (!(object instanceof Map)) return false;
        Map o = (Map)object;
        return map.entrySet().equals(o.entrySet());
    }

    static String toStringImpl(Map<?, ?> map) {
        StringBuilder sb = Collections2.newStringBuilderForCollection((int)map.size()).append((char)'{');
        STANDARD_JOINER.appendTo((StringBuilder)sb, map);
        return sb.append((char)'}').toString();
    }

    static <K, V> void putAllImpl(Map<K, V> self, Map<? extends K, ? extends V> map) {
        Iterator<Map.Entry<K, V>> i$ = map.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            self.put(entry.getKey(), entry.getValue());
        }
    }

    @Nullable
    static <K> K keyOrNull(@Nullable Map.Entry<K, ?> entry) {
        K k;
        if (entry == null) {
            k = null;
            return (K)((K)k);
        }
        k = (K)entry.getKey();
        return (K)k;
    }

    @Nullable
    static <V> V valueOrNull(@Nullable Map.Entry<?, V> entry) {
        V v;
        if (entry == null) {
            v = null;
            return (V)((V)v);
        }
        v = (V)entry.getValue();
        return (V)v;
    }

    static <E> ImmutableMap<E, Integer> indexMap(Collection<E> list) {
        ImmutableMap.Builder<E, Integer> builder = new ImmutableMap.Builder<E, Integer>((int)list.size());
        int i = 0;
        Iterator<E> i$ = list.iterator();
        while (i$.hasNext()) {
            E e = i$.next();
            builder.put(e, Integer.valueOf((int)i++));
        }
        return builder.build();
    }

    @Beta
    @GwtIncompatible
    public static <K extends Comparable<? super K>, V> NavigableMap<K, V> subMap(NavigableMap<K, V> map, Range<K> range) {
        boolean bl;
        if (map.comparator() != null && map.comparator() != Ordering.natural() && range.hasLowerBound() && range.hasUpperBound()) {
            Preconditions.checkArgument((boolean)(map.comparator().compare(range.lowerEndpoint(), range.upperEndpoint()) <= 0), (Object)"map is using a custom comparator which is inconsistent with the natural ordering.");
        }
        if (range.hasLowerBound() && range.hasUpperBound()) {
            boolean bl2;
            boolean bl3 = range.lowerBoundType() == BoundType.CLOSED;
            if (range.upperBoundType() == BoundType.CLOSED) {
                bl2 = true;
                return map.subMap(range.lowerEndpoint(), (boolean)bl3, range.upperEndpoint(), (boolean)bl2);
            }
            bl2 = false;
            return map.subMap(range.lowerEndpoint(), (boolean)bl3, range.upperEndpoint(), (boolean)bl2);
        }
        if (range.hasLowerBound()) {
            boolean bl4;
            if (range.lowerBoundType() == BoundType.CLOSED) {
                bl4 = true;
                return map.tailMap(range.lowerEndpoint(), (boolean)bl4);
            }
            bl4 = false;
            return map.tailMap(range.lowerEndpoint(), (boolean)bl4);
        }
        if (!range.hasUpperBound()) return Preconditions.checkNotNull(map);
        if (range.upperBoundType() == BoundType.CLOSED) {
            bl = true;
            return map.headMap(range.upperEndpoint(), (boolean)bl);
        }
        bl = false;
        return map.headMap(range.upperEndpoint(), (boolean)bl);
    }

    static /* synthetic */ Map access$100(Map x0) {
        return Maps.unmodifiableMap(x0);
    }

    static /* synthetic */ Set access$200(Set x0) {
        return Maps.removeOnlySet(x0);
    }

    static /* synthetic */ SortedSet access$300(SortedSet x0) {
        return Maps.removeOnlySortedSet(x0);
    }

    static /* synthetic */ NavigableSet access$400(NavigableSet x0) {
        return Maps.removeOnlyNavigableSet(x0);
    }

    static /* synthetic */ Map.Entry access$800(Map.Entry x0) {
        return Maps.unmodifiableOrNull(x0);
    }
}

