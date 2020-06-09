/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.LongCollections;
import io.netty.util.collection.LongObjectMap;

public final class LongCollections {
    private static final LongObjectMap<Object> EMPTY_MAP = new EmptyMap(null);

    private LongCollections() {
    }

    public static <V> LongObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> LongObjectMap<V> unmodifiableMap(LongObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }
}

