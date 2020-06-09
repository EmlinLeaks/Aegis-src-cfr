/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultimap<K, V>
implements Multimap<K, V> {
    private transient Collection<Map.Entry<K, V>> entries;
    private transient Set<K> keySet;
    private transient Multiset<K> keys;
    private transient Collection<V> values;
    private transient Map<K, Collection<V>> asMap;

    AbstractMultimap() {
    }

    @Override
    public boolean isEmpty() {
        if (this.size() != 0) return false;
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        Collection<V> collection;
        Iterator<Collection<V>> i$ = this.asMap().values().iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!(collection = i$.next()).contains((Object)value));
        return true;
    }

    @Override
    public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
        Collection<V> collection = this.asMap().get((Object)key);
        if (collection == null) return false;
        if (!collection.contains((Object)value)) return false;
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        Collection<V> collection = this.asMap().get((Object)key);
        if (collection == null) return false;
        if (!collection.remove((Object)value)) return false;
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean put(@Nullable K key, @Nullable V value) {
        return this.get(key).add(value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean putAll(@Nullable K key, Iterable<? extends V> values) {
        Preconditions.checkNotNull(values);
        if (values instanceof Collection) {
            Collection valueCollection = (Collection)values;
            if (valueCollection.isEmpty()) return false;
            if (!this.get(key).addAll(valueCollection)) return false;
            return true;
        }
        Iterator<V> valueItr = values.iterator();
        if (!valueItr.hasNext()) return false;
        if (!Iterators.addAll(this.get(key), valueItr)) return false;
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        boolean changed = false;
        Iterator<Map.Entry<K, V>> i$ = multimap.entries().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            changed |= this.put(entry.getKey(), entry.getValue());
        }
        return changed;
    }

    @CanIgnoreReturnValue
    @Override
    public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
        Preconditions.checkNotNull(values);
        Collection<V> result = this.removeAll(key);
        this.putAll(key, values);
        return result;
    }

    @Override
    public Collection<Map.Entry<K, V>> entries() {
        Collection<Map.Entry<K, V>> collection;
        Collection<Map.Entry<K, V>> result = this.entries;
        if (result == null) {
            collection = this.entries = this.createEntries();
            return collection;
        }
        collection = result;
        return collection;
    }

    Collection<Map.Entry<K, V>> createEntries() {
        if (!(this instanceof SetMultimap)) return new Entries((AbstractMultimap)this, null);
        return new EntrySet((AbstractMultimap)this, null);
    }

    abstract Iterator<Map.Entry<K, V>> entryIterator();

    @Override
    public Set<K> keySet() {
        Set<K> set;
        Set<K> result = this.keySet;
        if (result == null) {
            set = this.keySet = this.createKeySet();
            return set;
        }
        set = result;
        return set;
    }

    Set<K> createKeySet() {
        return new Maps.KeySet<K, Collection<V>>(this.asMap());
    }

    @Override
    public Multiset<K> keys() {
        Multiset<K> multiset;
        Multiset<K> result = this.keys;
        if (result == null) {
            multiset = this.keys = this.createKeys();
            return multiset;
        }
        multiset = result;
        return multiset;
    }

    Multiset<K> createKeys() {
        return new Multimaps.Keys<K, V>(this);
    }

    @Override
    public Collection<V> values() {
        Collection<V> collection;
        Collection<V> result = this.values;
        if (result == null) {
            collection = this.values = this.createValues();
            return collection;
        }
        collection = result;
        return collection;
    }

    Collection<V> createValues() {
        return new Values((AbstractMultimap)this);
    }

    Iterator<V> valueIterator() {
        return Maps.valueIterator(this.entries().iterator());
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> map;
        Map<K, Collection<V>> result = this.asMap;
        if (result == null) {
            map = this.asMap = this.createAsMap();
            return map;
        }
        map = result;
        return map;
    }

    abstract Map<K, Collection<V>> createAsMap();

    @Override
    public boolean equals(@Nullable Object object) {
        return Multimaps.equalsImpl(this, (Object)object);
    }

    @Override
    public int hashCode() {
        return this.asMap().hashCode();
    }

    public String toString() {
        return this.asMap().toString();
    }
}

