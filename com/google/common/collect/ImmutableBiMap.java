/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.RegularImmutableBiMap;
import com.google.common.collect.SingletonImmutableBiMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableBiMap<K, V>
extends ImmutableMap<K, V>
implements BiMap<K, V> {
    public static <K, V> ImmutableBiMap<K, V> of() {
        return RegularImmutableBiMap.EMPTY;
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1) {
        return new SingletonImmutableBiMap<K, V>(k1, v1);
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2) {
        return RegularImmutableBiMap.fromEntries(ImmutableBiMap.entryOf(k1, v1), ImmutableBiMap.entryOf(k2, v2));
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return RegularImmutableBiMap.fromEntries(ImmutableBiMap.entryOf(k1, v1), ImmutableBiMap.entryOf(k2, v2), ImmutableBiMap.entryOf(k3, v3));
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return RegularImmutableBiMap.fromEntries(ImmutableBiMap.entryOf(k1, v1), ImmutableBiMap.entryOf(k2, v2), ImmutableBiMap.entryOf(k3, v3), ImmutableBiMap.entryOf(k4, v4));
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return RegularImmutableBiMap.fromEntries(ImmutableBiMap.entryOf(k1, v1), ImmutableBiMap.entryOf(k2, v2), ImmutableBiMap.entryOf(k3, v3), ImmutableBiMap.entryOf(k4, v4), ImmutableBiMap.entryOf(k5, v5));
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }

    public static <K, V> ImmutableBiMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
        if (!(map instanceof ImmutableBiMap)) return ImmutableBiMap.copyOf(map.entrySet());
        ImmutableBiMap bimap = (ImmutableBiMap)map;
        if (bimap.isPartialView()) return ImmutableBiMap.copyOf(map.entrySet());
        return bimap;
    }

    @Beta
    public static <K, V> ImmutableBiMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
        Map.Entry[] entryArray = Iterables.toArray(entries, EMPTY_ENTRY_ARRAY);
        switch (entryArray.length) {
            case 0: {
                return ImmutableBiMap.of();
            }
            case 1: {
                Map.Entry entry = entryArray[0];
                return ImmutableBiMap.of(entry.getKey(), entry.getValue());
            }
        }
        return RegularImmutableBiMap.fromEntries(entryArray);
    }

    ImmutableBiMap() {
    }

    @Override
    public abstract ImmutableBiMap<V, K> inverse();

    @Override
    public ImmutableSet<V> values() {
        return ((ImmutableMap)((Object)this.inverse())).keySet();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public V forcePut(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }
}

