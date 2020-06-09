/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@GwtCompatible
public abstract class AbstractCache<K, V>
implements Cache<K, V> {
    protected AbstractCache() {
    }

    @Override
    public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
        LinkedHashMap<?, V> result = Maps.newLinkedHashMap();
        Iterator<?> i$ = keys.iterator();
        while (i$.hasNext()) {
            ? key = i$.next();
            if (result.containsKey(key)) continue;
            ? castKey = key;
            V value = this.getIfPresent(key);
            if (value == null) continue;
            result.put(castKey, value);
        }
        return ImmutableMap.copyOf(result);
    }

    @Override
    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Iterator<Map.Entry<K, V>> i$ = m.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateAll(Iterable<?> keys) {
        Iterator<?> i$ = keys.iterator();
        while (i$.hasNext()) {
            ? key = i$.next();
            this.invalidate(key);
        }
    }

    @Override
    public void invalidateAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheStats stats() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        throw new UnsupportedOperationException();
    }
}

