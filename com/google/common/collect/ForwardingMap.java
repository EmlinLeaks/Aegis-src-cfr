/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMap<K, V>
extends ForwardingObject
implements Map<K, V> {
    protected ForwardingMap() {
    }

    @Override
    protected abstract Map<K, V> delegate();

    @Override
    public int size() {
        return this.delegate().size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(Object object) {
        return (V)this.delegate().remove((Object)object);
    }

    @Override
    public void clear() {
        this.delegate().clear();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.delegate().containsKey((Object)key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return this.delegate().containsValue((Object)value);
    }

    @Override
    public V get(@Nullable Object key) {
        return (V)this.delegate().get((Object)key);
    }

    @CanIgnoreReturnValue
    @Override
    public V put(K key, V value) {
        return (V)this.delegate().put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        this.delegate().putAll(map);
    }

    @Override
    public Set<K> keySet() {
        return this.delegate().keySet();
    }

    @Override
    public Collection<V> values() {
        return this.delegate().values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.delegate().entrySet();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) return true;
        if (this.delegate().equals((Object)object)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }

    protected void standardPutAll(Map<? extends K, ? extends V> map) {
        Maps.putAllImpl(this, map);
    }

    @Beta
    protected V standardRemove(@Nullable Object key) {
        Map.Entry<K, V> entry;
        Iterator<Map.Entry<K, V>> entryIterator = this.entrySet().iterator();
        do {
            if (!entryIterator.hasNext()) return (V)null;
        } while (!Objects.equal((entry = entryIterator.next()).getKey(), (Object)key));
        V value = entry.getValue();
        entryIterator.remove();
        return (V)value;
    }

    protected void standardClear() {
        Iterators.clear(this.entrySet().iterator());
    }

    @Beta
    protected boolean standardContainsKey(@Nullable Object key) {
        return Maps.containsKeyImpl(this, (Object)key);
    }

    protected boolean standardContainsValue(@Nullable Object value) {
        return Maps.containsValueImpl(this, (Object)value);
    }

    protected boolean standardIsEmpty() {
        if (this.entrySet().iterator().hasNext()) return false;
        return true;
    }

    protected boolean standardEquals(@Nullable Object object) {
        return Maps.equalsImpl(this, (Object)object);
    }

    protected int standardHashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }

    protected String standardToString() {
        return Maps.toStringImpl(this);
    }
}

