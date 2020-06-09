/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.FilteredKeyMultimap;
import com.google.common.collect.FilteredMultimap;
import com.google.common.collect.FilteredMultimapValues;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
class FilteredKeyMultimap<K, V>
extends AbstractMultimap<K, V>
implements FilteredMultimap<K, V> {
    final Multimap<K, V> unfiltered;
    final Predicate<? super K> keyPredicate;

    FilteredKeyMultimap(Multimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        this.unfiltered = Preconditions.checkNotNull(unfiltered);
        this.keyPredicate = Preconditions.checkNotNull(keyPredicate);
    }

    @Override
    public Multimap<K, V> unfiltered() {
        return this.unfiltered;
    }

    @Override
    public Predicate<? super Map.Entry<K, V>> entryPredicate() {
        return Maps.keyPredicateOnEntries(this.keyPredicate);
    }

    @Override
    public int size() {
        int size = 0;
        Iterator<Collection<V>> i$ = this.asMap().values().iterator();
        while (i$.hasNext()) {
            Collection<V> collection = i$.next();
            size += collection.size();
        }
        return size;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (!this.unfiltered.containsKey((Object)key)) return false;
        Object k = key;
        return this.keyPredicate.apply(k);
    }

    @Override
    public Collection<V> removeAll(Object key) {
        Collection<V> collection;
        if (this.containsKey((Object)key)) {
            collection = this.unfiltered.removeAll((Object)key);
            return collection;
        }
        collection = this.unmodifiableEmptyCollection();
        return collection;
    }

    Collection<V> unmodifiableEmptyCollection() {
        if (!(this.unfiltered instanceof SetMultimap)) return ImmutableList.of();
        return ImmutableSet.of();
    }

    @Override
    public void clear() {
        this.keySet().clear();
    }

    @Override
    Set<K> createKeySet() {
        return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
    }

    @Override
    public Collection<V> get(K key) {
        if (this.keyPredicate.apply(key)) {
            return this.unfiltered.get(key);
        }
        if (!(this.unfiltered instanceof SetMultimap)) return new AddRejectingList<K, V>(key);
        return new AddRejectingSet<K, V>(key);
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        throw new AssertionError((Object)"should never be called");
    }

    @Override
    Collection<Map.Entry<K, V>> createEntries() {
        return new Entries((FilteredKeyMultimap)this);
    }

    @Override
    Collection<V> createValues() {
        return new FilteredMultimapValues<K, V>(this);
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return Maps.filterKeys(this.unfiltered.asMap(), this.keyPredicate);
    }

    @Override
    Multiset<K> createKeys() {
        return Multisets.filter(this.unfiltered.keys(), this.keyPredicate);
    }
}

