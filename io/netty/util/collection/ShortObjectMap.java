/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.ShortObjectMap;
import java.util.Map;

public interface ShortObjectMap<V>
extends Map<Short, V> {
    public V get(short var1);

    @Override
    public V put(short var1, V var2);

    public V remove(short var1);

    public Iterable<PrimitiveEntry<V>> entries();

    public boolean containsKey(short var1);
}

