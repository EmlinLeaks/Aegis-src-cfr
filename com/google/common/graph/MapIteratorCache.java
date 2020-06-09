/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.graph.MapIteratorCache;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class MapIteratorCache<K, V> {
    private final Map<K, V> backingMap;
    @Nullable
    private transient Map.Entry<K, V> entrySetCache;

    MapIteratorCache(Map<K, V> backingMap) {
        this.backingMap = Preconditions.checkNotNull(backingMap);
    }

    @CanIgnoreReturnValue
    public V put(@Nullable K key, @Nullable V value) {
        this.clearCache();
        return (V)this.backingMap.put(key, value);
    }

    @CanIgnoreReturnValue
    public V remove(@Nullable Object key) {
        this.clearCache();
        return (V)this.backingMap.remove((Object)key);
    }

    public void clear() {
        this.clearCache();
        this.backingMap.clear();
    }

    public V get(@Nullable Object key) {
        V v;
        V value = this.getIfCached((Object)key);
        if (value != null) {
            v = value;
            return (V)((V)v);
        }
        v = this.getWithoutCaching((Object)key);
        return (V)v;
    }

    public final V getWithoutCaching(@Nullable Object key) {
        return (V)this.backingMap.get((Object)key);
    }

    public final boolean containsKey(@Nullable Object key) {
        if (this.getIfCached((Object)key) != null) return true;
        if (this.backingMap.containsKey((Object)key)) return true;
        return false;
    }

    public final Set<K> unmodifiableKeySet() {
        return new AbstractSet<K>((MapIteratorCache)this){
            final /* synthetic */ MapIteratorCache this$0;
            {
                this.this$0 = mapIteratorCache;
            }

            public com.google.common.collect.UnmodifiableIterator<K> iterator() {
                java.util.Iterator<Map.Entry<K, V>> entryIterator = MapIteratorCache.access$000((MapIteratorCache)this.this$0).entrySet().iterator();
                return new com.google.common.collect.UnmodifiableIterator<K>(this, entryIterator){
                    final /* synthetic */ java.util.Iterator val$entryIterator;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                        this.val$entryIterator = iterator;
                    }

                    public boolean hasNext() {
                        return this.val$entryIterator.hasNext();
                    }

                    public K next() {
                        Map.Entry entry = (Map.Entry)this.val$entryIterator.next();
                        MapIteratorCache.access$102((MapIteratorCache)this.this$1.this$0, (Map.Entry)entry);
                        return (K)entry.getKey();
                    }
                };
            }

            public int size() {
                return MapIteratorCache.access$000((MapIteratorCache)this.this$0).size();
            }

            public boolean contains(@Nullable Object key) {
                return this.this$0.containsKey((Object)key);
            }
        };
    }

    protected V getIfCached(@Nullable Object key) {
        Map.Entry<K, V> entry = this.entrySetCache;
        if (entry == null) return (V)null;
        if (entry.getKey() != key) return (V)null;
        return (V)entry.getValue();
    }

    protected void clearCache() {
        this.entrySetCache = null;
    }

    static /* synthetic */ Map access$000(MapIteratorCache x0) {
        return x0.backingMap;
    }

    static /* synthetic */ Map.Entry access$102(MapIteratorCache x0, Map.Entry x1) {
        x0.entrySetCache = x1;
        return x0.entrySetCache;
    }
}

