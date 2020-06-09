/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.IntCollections;
import io.netty.util.collection.IntObjectMap;

public final class IntCollections {
    private static final IntObjectMap<Object> EMPTY_MAP = new EmptyMap(null);

    private IntCollections() {
    }

    public static <V> IntObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> IntObjectMap<V> unmodifiableMap(IntObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }
}

