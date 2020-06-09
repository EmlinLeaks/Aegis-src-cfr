/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.WellBehavedMap;
import java.util.Map;
import java.util.Set;

@GwtCompatible
final class WellBehavedMap<K, V>
extends ForwardingMap<K, V> {
    private final Map<K, V> delegate;
    private Set<Map.Entry<K, V>> entrySet;

    private WellBehavedMap(Map<K, V> delegate) {
        this.delegate = delegate;
    }

    static <K, V> WellBehavedMap<K, V> wrap(Map<K, V> delegate) {
        return new WellBehavedMap<K, V>(delegate);
    }

    @Override
    protected Map<K, V> delegate() {
        return this.delegate;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = this.entrySet;
        if (es != null) {
            return es;
        }
        this.entrySet = new EntrySet((WellBehavedMap)this, null);
        return this.entrySet;
    }
}

