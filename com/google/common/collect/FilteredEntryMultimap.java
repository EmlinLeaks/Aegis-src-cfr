/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.FilteredEntryMultimap;
import com.google.common.collect.FilteredMultimap;
import com.google.common.collect.FilteredMultimapValues;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
class FilteredEntryMultimap<K, V>
extends AbstractMultimap<K, V>
implements FilteredMultimap<K, V> {
    final Multimap<K, V> unfiltered;
    final Predicate<? super Map.Entry<K, V>> predicate;

    FilteredEntryMultimap(Multimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate) {
        this.unfiltered = Preconditions.checkNotNull(unfiltered);
        this.predicate = Preconditions.checkNotNull(predicate);
    }

    @Override
    public Multimap<K, V> unfiltered() {
        return this.unfiltered;
    }

    @Override
    public Predicate<? super Map.Entry<K, V>> entryPredicate() {
        return this.predicate;
    }

    @Override
    public int size() {
        return this.entries().size();
    }

    private boolean satisfies(K key, V value) {
        return this.predicate.apply(Maps.immutableEntry(key, value));
    }

    static <E> Collection<E> filterCollection(Collection<E> collection, Predicate<? super E> predicate) {
        if (!(collection instanceof Set)) return Collections2.filter(collection, predicate);
        return Sets.filter((Set)collection, predicate);
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (this.asMap().get((Object)key) == null) return false;
        return true;
    }

    @Override
    public Collection<V> removeAll(@Nullable Object key) {
        return MoreObjects.firstNonNull(this.asMap().remove((Object)key), this.unmodifiableEmptyCollection());
    }

    Collection<V> unmodifiableEmptyCollection() {
        Collection<T> collection;
        if (this.unfiltered instanceof SetMultimap) {
            collection = Collections.emptySet();
            return collection;
        }
        collection = Collections.emptyList();
        return collection;
    }

    @Override
    public void clear() {
        this.entries().clear();
    }

    @Override
    public Collection<V> get(K key) {
        return FilteredEntryMultimap.filterCollection(this.unfiltered.get(key), new ValuePredicate((FilteredEntryMultimap)this, key));
    }

    @Override
    Collection<Map.Entry<K, V>> createEntries() {
        return FilteredEntryMultimap.filterCollection(this.unfiltered.entries(), this.predicate);
    }

    @Override
    Collection<V> createValues() {
        return new FilteredMultimapValues<K, V>(this);
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        throw new AssertionError((Object)"should never be called");
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return new AsMap((FilteredEntryMultimap)this);
    }

    @Override
    public Set<K> keySet() {
        return this.asMap().keySet();
    }

    boolean removeEntriesIf(Predicate<? super Map.Entry<K, Collection<V>>> predicate) {
        Iterator<Map.Entry<K, Collection<V>>> entryIterator = this.unfiltered.asMap().entrySet().iterator();
        boolean changed = false;
        while (entryIterator.hasNext()) {
            Map.Entry<K, Collection<V>> entry = entryIterator.next();
            K key = entry.getKey();
            Collection<V> collection = FilteredEntryMultimap.filterCollection(entry.getValue(), new ValuePredicate((FilteredEntryMultimap)this, key));
            if (collection.isEmpty() || !predicate.apply(Maps.immutableEntry(key, collection))) continue;
            if (collection.size() == entry.getValue().size()) {
                entryIterator.remove();
            } else {
                collection.clear();
            }
            changed = true;
        }
        return changed;
    }

    @Override
    Multiset<K> createKeys() {
        return new Keys((FilteredEntryMultimap)this);
    }

    static /* synthetic */ boolean access$000(FilteredEntryMultimap x0, Object x1, Object x2) {
        return x0.satisfies(x1, x2);
    }
}

