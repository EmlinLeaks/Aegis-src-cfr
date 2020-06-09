/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import java.util.Set;

public interface CacheAdapter<K, V> {
    public V get(K var1);

    public void put(K var1, V var2);

    public void invalidate(K var1);

    public void invalidateAll(Set<K> var1);

    public void invalidateAll();
}

