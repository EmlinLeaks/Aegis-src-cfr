/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

public final class LongTag
extends Tag<Long> {
    private final long value;

    public LongTag(String name, long value) {
        super((TagType)TagType.TAG_LONG, (String)name);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return Long.valueOf((long)this.value);
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name == null) return "TAG_Long" + append + ": " + this.value;
        if (name.equals((Object)"")) return "TAG_Long" + append + ": " + this.value;
        append = "(\"" + this.getName() + "\")";
        return "TAG_Long" + append + ": " + this.value;
    }

    @Override
    public LongTag clone() {
        return new LongTag((String)this.getName(), (long)this.value);
    }
}

