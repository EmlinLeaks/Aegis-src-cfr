/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible
public abstract class ForwardingConcurrentMap<K, V>
extends ForwardingMap<K, V>
implements ConcurrentMap<K, V> {
    protected ForwardingConcurrentMap() {
    }

    @Override
    protected abstract ConcurrentMap<K, V> delegate();

    @CanIgnoreReturnValue
    @Override
    public V putIfAbsent(K key, V value) {
        return (V)this.delegate().putIfAbsent(key, value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean remove(Object key, Object value) {
        return this.delegate().remove((Object)key, (Object)value);
    }

    @CanIgnoreReturnValue
    @Override
    public V replace(K key, V value) {
        return (V)this.delegate().replace(key, value);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return this.delegate().replace(key, oldValue, newValue);
    }
}

