/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

public final class ShortTag
extends Tag<Short> {
    private final short value;

    public ShortTag(String name, short value) {
        super((TagType)TagType.TAG_SHORT, (String)name);
        this.value = value;
    }

    @Override
    public Short getValue() {
        return Short.valueOf((short)this.value);
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name == null) return "TAG_Short" + append + ": " + this.value;
        if (name.equals((Object)"")) return "TAG_Short" + append + ": " + this.value;
        append = "(\"" + this.getName() + "\")";
        return "TAG_Short" + append + ": " + this.value;
    }

    @Override
    public ShortTag clone() {
        return new ShortTag((String)this.getName(), (short)this.value);
    }
}

