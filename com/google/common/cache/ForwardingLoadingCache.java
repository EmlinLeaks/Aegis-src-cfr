/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.cache.Cache;
import com.google.common.cache.ForwardingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;

@GwtIncompatible
public abstract class ForwardingLoadingCache<K, V>
extends ForwardingCache<K, V>
implements LoadingCache<K, V> {
    protected ForwardingLoadingCache() {
    }

    @Override
    protected abstract LoadingCache<K, V> delegate();

    @Override
    public V get(K key) throws ExecutionException {
        return (V)this.delegate().get(key);
    }

    @Override
    public V getUnchecked(K key) {
        return (V)this.delegate().getUnchecked(key);
    }

    @Override
    public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
        return this.delegate().getAll(keys);
    }

    @Override
    public V apply(K key) {
        return (V)this.delegate().apply(key);
    }

    @Override
    public void refresh(K key) {
        this.delegate().refresh(key);
    }
}

