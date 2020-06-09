/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.ByteObjectMap;
import java.util.Map;

public interface ByteObjectMap<V>
extends Map<Byte, V> {
    public V get(byte var1);

    @Override
    public V put(byte var1, V var2);

    public V remove(byte var1);

    public Iterable<PrimitiveEntry<V>> entries();

    public boolean containsKey(byte var1);
}

