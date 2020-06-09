/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CompoundMap
implements Map<String, Tag<?>>,
Iterable<Tag<?>> {
    private final Map<String, Tag<?>> map;
    private final boolean sort;
    private final boolean reverse;

    public CompoundMap() {
        this(null, (boolean)false, (boolean)false);
    }

    public CompoundMap(List<Tag<?>> initial) {
        this(initial, (boolean)false, (boolean)false);
    }

    public CompoundMap(Map<String, Tag<?>> initial) {
        this(initial.values(), (boolean)false, (boolean)false);
    }

    @Deprecated
    public CompoundMap(HashMap<String, Tag<?>> initial) {
        this(initial);
    }

    public CompoundMap(CompoundMap initial) {
        this(initial.values(), (boolean)initial.sort, (boolean)initial.reverse);
    }

    public CompoundMap(boolean sort, boolean reverse) {
        this(null, (boolean)sort, (boolean)reverse);
    }

    public CompoundMap(Iterable<Tag<?>> initial, boolean sort, boolean reverse) {
        this.sort = reverse ? true : sort;
        this.reverse = reverse;
        this.map = !sort ? new LinkedHashMap<String, Tag<?>>() : (reverse ? new TreeMap<T, Tag<?>>(Collections.<T>reverseOrder()) : new TreeMap<String, Tag<?>>());
        if (initial == null) return;
        Iterator<Tag<?>> iterator = initial.iterator();
        while (iterator.hasNext()) {
            Tag<?> t = iterator.next();
            this.put(t);
        }
    }

    public Tag<?> put(Tag<?> tag) {
        return this.map.put((String)tag.getName(), tag);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey((Object)key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue((Object)value);
    }

    @Override
    public Set<Map.Entry<String, Tag<?>>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public Tag<?> get(Object key) {
        return this.map.get((Object)key);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Tag<?> put(String key, Tag<?> value) {
        return this.map.put((String)key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Tag<?>> values) {
        this.map.putAll(values);
    }

    @Override
    public Tag remove(Object key) {
        return this.map.remove((Object)key);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Collection<Tag<?>> values() {
        return this.map.values();
    }

    @Override
    public Iterator<Tag<?>> iterator() {
        return this.values().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompoundMap)) return false;
        CompoundMap other = (CompoundMap)o;
        Iterator<Tag<?>> iThis = this.iterator();
        Iterator<Tag<?>> iOther = other.iterator();
        while (iThis.hasNext() && iOther.hasNext()) {
            Tag<?> tOther;
            Tag<?> tThis = iThis.next();
            if (tThis.equals(tOther = iOther.next())) continue;
            return false;
        }
        if (iThis.hasNext()) return false;
        if (!iOther.hasNext()) return true;
        return false;
    }
}

