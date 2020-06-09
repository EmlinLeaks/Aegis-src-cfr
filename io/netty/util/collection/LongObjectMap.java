/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.LongObjectMap;
import java.util.Map;

public interface LongObjectMap<V>
extends Map<Long, V> {
    public V get(long var1);

    @Override
    public V put(long var1, V var3);

    public V remove(long var1);

    public Iterable<PrimitiveEntry<V>> entries();

    public boolean containsKey(long var1);
}

