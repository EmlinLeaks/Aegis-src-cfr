/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.ByteCollections;
import io.netty.util.collection.ByteObjectMap;

public final class ByteCollections {
    private static final ByteObjectMap<Object> EMPTY_MAP = new EmptyMap(null);

    private ByteCollections() {
    }

    public static <V> ByteObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> ByteObjectMap<V> unmodifiableMap(ByteObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }
}

