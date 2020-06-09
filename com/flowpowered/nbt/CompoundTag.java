/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import java.util.Collection;
import java.util.Iterator;

public class CompoundTag
extends Tag<CompoundMap> {
    private final CompoundMap value;

    public CompoundTag(String name, CompoundMap value) {
        super((TagType)TagType.TAG_COMPOUND, (String)name);
        this.value = value;
    }

    @Override
    public CompoundMap getValue() {
        return this.value;
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name != null && !name.equals((Object)"")) {
            append = "(\"" + this.getName() + "\")";
        }
        StringBuilder bldr = new StringBuilder();
        bldr.append((String)"TAG_Compound").append((String)append).append((String)": ").append((int)this.value.size()).append((String)" entries\r\n{\r\n");
        Iterator<Tag<?>> iterator = this.value.values().iterator();
        do {
            if (!iterator.hasNext()) {
                bldr.append((String)"}");
                return bldr.toString();
            }
            Tag<?> entry = iterator.next();
            bldr.append((String)"   ").append((String)entry.toString().replaceAll((String)"\r\n", (String)"\r\n   ")).append((String)"\r\n");
        } while (true);
    }

    @Override
    public CompoundTag clone() {
        CompoundMap map = new CompoundMap((CompoundMap)this.value);
        return new CompoundTag((String)this.getName(), (CompoundMap)map);
    }
}

