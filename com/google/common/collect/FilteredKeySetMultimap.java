/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.FilteredKeyMultimap;
import com.google.common.collect.FilteredKeySetMultimap;
import com.google.common.collect.FilteredSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible
final class FilteredKeySetMultimap<K, V>
extends FilteredKeyMultimap<K, V>
implements FilteredSetMultimap<K, V> {
    FilteredKeySetMultimap(SetMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        super(unfiltered, keyPredicate);
    }

    @Override
    public SetMultimap<K, V> unfiltered() {
        return (SetMultimap)this.unfiltered;
    }

    @Override
    public Set<V> get(K key) {
        return (Set)super.get(key);
    }

    @Override
    public Set<V> removeAll(Object key) {
        return (Set)super.removeAll((Object)key);
    }

    @Override
    public Set<V> replaceValues(K key, Iterable<? extends V> values) {
        return (Set)super.replaceValues(key, values);
    }

    @Override
    public Set<Map.Entry<K, V>> entries() {
        return (Set)super.entries();
    }

    @Override
    Set<Map.Entry<K, V>> createEntries() {
        return new EntrySet((FilteredKeySetMultimap)this);
    }
}

