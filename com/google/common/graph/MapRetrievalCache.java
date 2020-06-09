/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MapRetrievalCache;
import java.util.Map;
import javax.annotation.Nullable;

class MapRetrievalCache<K, V>
extends MapIteratorCache<K, V> {
    @Nullable
    private transient CacheEntry<K, V> cacheEntry1;
    @Nullable
    private transient CacheEntry<K, V> cacheEntry2;

    MapRetrievalCache(Map<K, V> backingMap) {
        super(backingMap);
    }

    @Override
    public V get(@Nullable Object key) {
        V value = this.getIfCached((Object)key);
        if (value != null) {
            return (V)value;
        }
        value = this.getWithoutCaching((Object)key);
        if (value == null) return (V)value;
        this.addToCache(key, value);
        return (V)value;
    }

    @Override
    protected V getIfCached(@Nullable Object key) {
        V value = super.getIfCached((Object)key);
        if (value != null) {
            return (V)value;
        }
        CacheEntry<K, V> entry = this.cacheEntry1;
        if (entry != null && entry.key == key) {
            return (V)entry.value;
        }
        entry = this.cacheEntry2;
        if (entry == null) return (V)null;
        if (entry.key != key) return (V)null;
        this.addToCache(entry);
        return (V)entry.value;
    }

    @Override
    protected void clearCache() {
        super.clearCache();
        this.cacheEntry1 = null;
        this.cacheEntry2 = null;
    }

    private void addToCache(K key, V value) {
        this.addToCache(new CacheEntry<K, V>(key, value));
    }

    private void addToCache(CacheEntry<K, V> entry) {
        this.cacheEntry2 = this.cacheEntry1;
        this.cacheEntry1 = entry;
    }
}

