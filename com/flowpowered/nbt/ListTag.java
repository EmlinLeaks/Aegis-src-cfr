/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ListTag<T extends Tag<?>>
extends Tag<List<T>> {
    private final Class<T> type;
    private final List<T> value;

    public ListTag(String name, Class<T> type, List<T> value) {
        super((TagType)TagType.TAG_LIST, (String)name);
        this.type = type;
        this.value = Collections.unmodifiableList(value);
    }

    public Class<T> getElementType() {
        return this.type;
    }

    @Override
    public List<T> getValue() {
        return this.value;
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name != null && !name.equals((Object)"")) {
            append = "(\"" + this.getName() + "\")";
        }
        StringBuilder bldr = new StringBuilder();
        bldr.append((String)"TAG_List").append((String)append).append((String)": ").append((int)this.value.size()).append((String)" entries of type ").append((String)TagType.getByTagClass(this.type).getTypeName()).append((String)"\r\n{\r\n");
        Iterator<T> iterator = this.value.iterator();
        do {
            if (!iterator.hasNext()) {
                bldr.append((String)"}");
                return bldr.toString();
            }
            Tag t = (Tag)iterator.next();
            bldr.append((String)"   ").append((String)t.toString().replaceAll((String)"\r\n", (String)"\r\n   ")).append((String)"\r\n");
        } while (true);
    }

    @Override
    public ListTag<T> clone() {
        ArrayList<Object> newList = new ArrayList<Object>();
        Iterator<T> iterator = this.value.iterator();
        while (iterator.hasNext()) {
            Tag v = (Tag)iterator.next();
            newList.add(v.clone());
        }
        return new ListTag<T>((String)this.getName(), this.type, newList);
    }
}

