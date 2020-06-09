/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.TagType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class Tag<T>
implements Comparable<Tag<?>> {
    private final String name;
    private final TagType type;

    public Tag(TagType type) {
        this((TagType)type, (String)"");
    }

    public Tag(TagType type, String name) {
        this.name = name;
        this.type = type;
    }

    public final String getName() {
        return this.name;
    }

    public TagType getType() {
        return this.type;
    }

    public abstract T getValue();

    public static Map<String, Tag<?>> cloneMap(Map<String, Tag<?>> map) {
        if (map == null) {
            return null;
        }
        HashMap<String, Tag<?>> newMap = new HashMap<String, Tag<?>>();
        Iterator<Map.Entry<String, Tag<?>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Tag<?>> entry = iterator.next();
            newMap.put((String)entry.getKey(), entry.getValue().clone());
        }
        return newMap;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag)other;
        if (!this.getValue().equals(tag.getValue())) return false;
        if (!this.getName().equals((Object)tag.getName())) return false;
        return true;
    }

    @Override
    public int compareTo(Tag other) {
        if (this.equals((Object)other)) {
            return 0;
        }
        if (!other.getName().equals((Object)this.getName())) return this.getName().compareTo((String)other.getName());
        throw new IllegalStateException((String)"Cannot compare two Tags with the same name but different values for sorting");
    }

    public abstract Tag<T> clone();
}

