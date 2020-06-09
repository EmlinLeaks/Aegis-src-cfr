/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableEnumMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
final class ImmutableEnumMap<K extends Enum<K>, V>
extends ImmutableMap.IteratorBasedImmutableMap<K, V> {
    private final transient EnumMap<K, V> delegate;

    static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> map) {
        switch (map.size()) {
            case 0: {
                return ImmutableMap.of();
            }
            case 1: {
                Map.Entry<K, V> entry = Iterables.getOnlyElement(map.entrySet());
                return ImmutableMap.of(entry.getKey(), entry.getValue());
            }
        }
        return new ImmutableEnumMap<K, V>(map);
    }

    private ImmutableEnumMap(EnumMap<K, V> delegate) {
        this.delegate = delegate;
        Preconditions.checkArgument((boolean)(!delegate.isEmpty()));
    }

    @Override
    UnmodifiableIterator<K> keyIterator() {
        return Iterators.unmodifiableIterator(this.delegate.keySet().iterator());
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.delegate.containsKey((Object)key);
    }

    @Override
    public V get(Object key) {
        return (V)this.delegate.get((Object)key);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ImmutableEnumMap)) return this.delegate.equals(object);
        object = ((ImmutableEnumMap)object).delegate;
        return this.delegate.equals(object);
    }

    @Override
    UnmodifiableIterator<Map.Entry<K, V>> entryIterator() {
        return Maps.unmodifiableEntryIterator(this.delegate.entrySet().iterator());
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    Object writeReplace() {
        return new EnumSerializedForm<K, V>(this.delegate);
    }
}

