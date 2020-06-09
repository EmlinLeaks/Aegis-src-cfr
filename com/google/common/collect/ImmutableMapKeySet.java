/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.Weak
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapKeySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.j2objc.annotations.Weak;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
final class ImmutableMapKeySet<K, V>
extends ImmutableSet.Indexed<K> {
    @Weak
    private final ImmutableMap<K, V> map;

    ImmutableMapKeySet(ImmutableMap<K, V> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public UnmodifiableIterator<K> iterator() {
        return this.map.keyIterator();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.map.containsKey((Object)object);
    }

    @Override
    K get(int index) {
        return (K)((Map.Entry)((ImmutableSet)this.map.entrySet()).asList().get((int)index)).getKey();
    }

    @Override
    boolean isPartialView() {
        return true;
    }

    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new KeySetSerializedForm<K>(this.map);
    }
}

