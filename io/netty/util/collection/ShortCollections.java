/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.ShortCollections;
import io.netty.util.collection.ShortObjectMap;

public final class ShortCollections {
    private static final ShortObjectMap<Object> EMPTY_MAP = new EmptyMap(null);

    private ShortCollections() {
    }

    public static <V> ShortObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> ShortObjectMap<V> unmodifiableMap(ShortObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }
}

