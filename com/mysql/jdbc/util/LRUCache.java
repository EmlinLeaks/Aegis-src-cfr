/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.util;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LRUCache<K, V>
extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1L;
    protected int maxElements;

    public LRUCache(int maxSize) {
        super((int)maxSize, (float)0.75f, (boolean)true);
        this.maxElements = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (this.size() <= this.maxElements) return false;
        return true;
    }
}

