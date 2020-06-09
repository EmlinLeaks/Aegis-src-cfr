/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
abstract class ImmutableMapEntrySet<K, V>
extends ImmutableSet<Map.Entry<K, V>> {
    ImmutableMapEntrySet() {
    }

    abstract ImmutableMap<K, V> map();

    @Override
    public int size() {
        return this.map().size();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (!(object instanceof Map.Entry)) return false;
        Map.Entry entry = (Map.Entry)object;
        V value = this.map().get(entry.getKey());
        if (value == null) return false;
        if (!value.equals(entry.getValue())) return false;
        return true;
    }

    @Override
    boolean isPartialView() {
        return this.map().isPartialView();
    }

    @GwtIncompatible
    @Override
    boolean isHashCodeFast() {
        return this.map().isHashCodeFast();
    }

    @Override
    public int hashCode() {
        return this.map().hashCode();
    }

    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new EntrySetSerializedForm<K, V>(this.map());
    }
}

