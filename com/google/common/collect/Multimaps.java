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
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.FilteredEntryMultimap;
import com.google.common.collect.FilteredEntrySetMultimap;
import com.google.common.collect.FilteredKeyListMultimap;
import com.google.common.collect.FilteredKeyMultimap;
import com.google.common.collect.FilteredKeySetMultimap;
import com.google.common.collect.FilteredMultimap;
import com.google.common.collect.FilteredSetMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Synchronized;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Multimaps {
    private Multimaps() {
    }

    public static <K, V> Multimap<K, V> newMultimap(Map<K, Collection<V>> map, Supplier<? extends Collection<V>> factory) {
        return new CustomMultimap<K, V>(map, factory);
    }

    public static <K, V> ListMultimap<K, V> newListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> factory) {
        return new CustomListMultimap<K, V>(map, factory);
    }

    public static <K, V> SetMultimap<K, V> newSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> factory) {
        return new CustomSetMultimap<K, V>(map, factory);
    }

    public static <K, V> SortedSetMultimap<K, V> newSortedSetMultimap(Map<K, Collection<V>> map, Supplier<? extends SortedSet<V>> factory) {
        return new CustomSortedSetMultimap<K, V>(map, factory);
    }

    @CanIgnoreReturnValue
    public static <K, V, M extends Multimap<K, V>> M invertFrom(Multimap<? extends V, ? extends K> source, M dest) {
        Preconditions.checkNotNull(dest);
        Iterator<Map.Entry<V, K>> i$ = source.entries().iterator();
        while (i$.hasNext()) {
            Map.Entry<V, K> entry = i$.next();
            dest.put(entry.getValue(), entry.getKey());
        }
        return (M)dest;
    }

    public static <K, V> Multimap<K, V> synchronizedMultimap(Multimap<K, V> multimap) {
        return Synchronized.multimap(multimap, null);
    }

    public static <K, V> Multimap<K, V> unmodifiableMultimap(Multimap<K, V> delegate) {
        if (delegate instanceof UnmodifiableMultimap) return delegate;
        if (!(delegate instanceof ImmutableMultimap)) return new UnmodifiableMultimap<K, V>(delegate);
        return delegate;
    }

    @Deprecated
    public static <K, V> Multimap<K, V> unmodifiableMultimap(ImmutableMultimap<K, V> delegate) {
        return (Multimap)Preconditions.checkNotNull(delegate);
    }

    public static <K, V> SetMultimap<K, V> synchronizedSetMultimap(SetMultimap<K, V> multimap) {
        return Synchronized.setMultimap(multimap, null);
    }

    public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(SetMultimap<K, V> delegate) {
        if (delegate instanceof UnmodifiableSetMultimap) return delegate;
        if (!(delegate instanceof ImmutableSetMultimap)) return new UnmodifiableSetMultimap<K, V>(delegate);
        return delegate;
    }

    @Deprecated
    public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(ImmutableSetMultimap<K, V> delegate) {
        return (SetMultimap)Preconditions.checkNotNull(delegate);
    }

    public static <K, V> SortedSetMultimap<K, V> synchronizedSortedSetMultimap(SortedSetMultimap<K, V> multimap) {
        return Synchronized.sortedSetMultimap(multimap, null);
    }

    public static <K, V> SortedSetMultimap<K, V> unmodifiableSortedSetMultimap(SortedSetMultimap<K, V> delegate) {
        if (!(delegate instanceof UnmodifiableSortedSetMultimap)) return new UnmodifiableSortedSetMultimap<K, V>(delegate);
        return delegate;
    }

    public static <K, V> ListMultimap<K, V> synchronizedListMultimap(ListMultimap<K, V> multimap) {
        return Synchronized.listMultimap(multimap, null);
    }

    public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ListMultimap<K, V> delegate) {
        if (delegate instanceof UnmodifiableListMultimap) return delegate;
        if (!(delegate instanceof ImmutableListMultimap)) return new UnmodifiableListMultimap<K, V>(delegate);
        return delegate;
    }

    @Deprecated
    public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ImmutableListMultimap<K, V> delegate) {
        return (ListMultimap)Preconditions.checkNotNull(delegate);
    }

    private static <V> Collection<V> unmodifiableValueCollection(Collection<V> collection) {
        if (collection instanceof SortedSet) {
            return Collections.unmodifiableSortedSet((SortedSet)collection);
        }
        if (collection instanceof Set) {
            return Collections.unmodifiableSet((Set)collection);
        }
        if (!(collection instanceof List)) return Collections.unmodifiableCollection(collection);
        return Collections.unmodifiableList((List)collection);
    }

    private static <K, V> Collection<Map.Entry<K, V>> unmodifiableEntries(Collection<Map.Entry<K, V>> entries) {
        if (!(entries instanceof Set)) return new Maps.UnmodifiableEntries<K, V>(Collections.unmodifiableCollection(entries));
        return Maps.unmodifiableEntrySet((Set)entries);
    }

    @Beta
    public static <K, V> Map<K, List<V>> asMap(ListMultimap<K, V> multimap) {
        return multimap.asMap();
    }

    @Beta
    public static <K, V> Map<K, Set<V>> asMap(SetMultimap<K, V> multimap) {
        return multimap.asMap();
    }

    @Beta
    public static <K, V> Map<K, SortedSet<V>> asMap(SortedSetMultimap<K, V> multimap) {
        return multimap.asMap();
    }

    @Beta
    public static <K, V> Map<K, Collection<V>> asMap(Multimap<K, V> multimap) {
        return multimap.asMap();
    }

    public static <K, V> SetMultimap<K, V> forMap(Map<K, V> map) {
        return new MapMultimap<K, V>(map);
    }

    public static <K, V1, V2> Multimap<K, V2> transformValues(Multimap<K, V1> fromMultimap, Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        Maps.EntryTransformer<K, ? super V1, V2> transformer = Maps.asEntryTransformer(function);
        return Multimaps.transformEntries(fromMultimap, transformer);
    }

    public static <K, V1, V2> Multimap<K, V2> transformEntries(Multimap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesMultimap<K, V1, V2>(fromMap, transformer);
    }

    public static <K, V1, V2> ListMultimap<K, V2> transformValues(ListMultimap<K, V1> fromMultimap, Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        Maps.EntryTransformer<K, ? super V1, V2> transformer = Maps.asEntryTransformer(function);
        return Multimaps.transformEntries(fromMultimap, transformer);
    }

    public static <K, V1, V2> ListMultimap<K, V2> transformEntries(ListMultimap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
        return new TransformedEntriesListMultimap<K, V1, V2>(fromMap, transformer);
    }

    public static <K, V> ImmutableListMultimap<K, V> index(Iterable<V> values, Function<? super V, K> keyFunction) {
        return Multimaps.index(values.iterator(), keyFunction);
    }

    public static <K, V> ImmutableListMultimap<K, V> index(Iterator<V> values, Function<? super V, K> keyFunction) {
        Preconditions.checkNotNull(keyFunction);
        ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();
        while (values.hasNext()) {
            V value = values.next();
            Preconditions.checkNotNull(value, values);
            builder.put(keyFunction.apply(value), value);
        }
        return builder.build();
    }

    public static <K, V> Multimap<K, V> filterKeys(Multimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        if (unfiltered instanceof SetMultimap) {
            return Multimaps.filterKeys((SetMultimap)unfiltered, keyPredicate);
        }
        if (unfiltered instanceof ListMultimap) {
            return Multimaps.filterKeys((ListMultimap)unfiltered, keyPredicate);
        }
        if (unfiltered instanceof FilteredKeyMultimap) {
            FilteredKeyMultimap prev = (FilteredKeyMultimap)unfiltered;
            return new FilteredKeyMultimap<K, V>(prev.unfiltered, Predicates.and(prev.keyPredicate, keyPredicate));
        }
        if (!(unfiltered instanceof FilteredMultimap)) return new FilteredKeyMultimap<K, V>(unfiltered, keyPredicate);
        FilteredMultimap prev = (FilteredMultimap)unfiltered;
        return Multimaps.filterFiltered(prev, Maps.keyPredicateOnEntries(keyPredicate));
    }

    public static <K, V> SetMultimap<K, V> filterKeys(SetMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        if (unfiltered instanceof FilteredKeySetMultimap) {
            FilteredKeySetMultimap prev = (FilteredKeySetMultimap)unfiltered;
            return new FilteredKeySetMultimap<K, V>(prev.unfiltered(), Predicates.and(prev.keyPredicate, keyPredicate));
        }
        if (!(unfiltered instanceof FilteredSetMultimap)) return new FilteredKeySetMultimap<K, V>(unfiltered, keyPredicate);
        FilteredSetMultimap prev = (FilteredSetMultimap)unfiltered;
        return Multimaps.filterFiltered(prev, Maps.keyPredicateOnEntries(keyPredicate));
    }

    public static <K, V> ListMultimap<K, V> filterKeys(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        if (!(unfiltered instanceof FilteredKeyListMultimap)) return new FilteredKeyListMultimap<K, V>(unfiltered, keyPredicate);
        FilteredKeyListMultimap prev = (FilteredKeyListMultimap)unfiltered;
        return new FilteredKeyListMultimap<K, V>(prev.unfiltered(), Predicates.and(prev.keyPredicate, keyPredicate));
    }

    public static <K, V> Multimap<K, V> filterValues(Multimap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Multimaps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> SetMultimap<K, V> filterValues(SetMultimap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
        return Multimaps.filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
    }

    public static <K, V> Multimap<K, V> filterEntries(Multimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Multimap<K, V> multimap;
        Preconditions.checkNotNull(entryPredicate);
        if (unfiltered instanceof SetMultimap) {
            return Multimaps.filterEntries((SetMultimap)unfiltered, entryPredicate);
        }
        if (unfiltered instanceof FilteredMultimap) {
            multimap = Multimaps.filterFiltered((FilteredMultimap)unfiltered, entryPredicate);
            return multimap;
        }
        multimap = new FilteredEntryMultimap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
        return multimap;
    }

    public static <K, V> SetMultimap<K, V> filterEntries(SetMultimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        SetMultimap<K, V> setMultimap;
        Preconditions.checkNotNull(entryPredicate);
        if (unfiltered instanceof FilteredSetMultimap) {
            setMultimap = Multimaps.filterFiltered((FilteredSetMultimap)unfiltered, entryPredicate);
            return setMultimap;
        }
        setMultimap = new FilteredEntrySetMultimap<K, V>(Preconditions.checkNotNull(unfiltered), entryPredicate);
        return setMultimap;
    }

    private static <K, V> Multimap<K, V> filterFiltered(FilteredMultimap<K, V> multimap, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(multimap.entryPredicate(), entryPredicate);
        return new FilteredEntryMultimap<K, V>(multimap.unfiltered(), predicate);
    }

    private static <K, V> SetMultimap<K, V> filterFiltered(FilteredSetMultimap<K, V> multimap, Predicate<? super Map.Entry<K, V>> entryPredicate) {
        Predicate<? super Map.Entry<K, V>> predicate = Predicates.and(multimap.entryPredicate(), entryPredicate);
        return new FilteredEntrySetMultimap<K, V>(multimap.unfiltered(), predicate);
    }

    static boolean equalsImpl(Multimap<?, ?> multimap, @Nullable Object object) {
        if (object == multimap) {
            return true;
        }
        if (!(object instanceof Multimap)) return false;
        Multimap that = (Multimap)object;
        return multimap.asMap().equals(that.asMap());
    }

    static /* synthetic */ Collection access$000(Collection x0) {
        return Multimaps.unmodifiableValueCollection(x0);
    }

    static /* synthetic */ Collection access$100(Collection x0) {
        return Multimaps.unmodifiableEntries(x0);
    }
}

