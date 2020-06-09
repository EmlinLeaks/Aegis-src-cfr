/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.CharCollections;
import io.netty.util.collection.CharObjectMap;

public final class CharCollections {
    private static final CharObjectMap<Object> EMPTY_MAP = new EmptyMap(null);

    private CharCollections() {
    }

    public static <V> CharObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> CharObjectMap<V> unmodifiableMap(CharObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }
}

