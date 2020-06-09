/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

public final class FloatTag
extends Tag<Float> {
    private final float value;

    public FloatTag(String name, float value) {
        super((TagType)TagType.TAG_FLOAT, (String)name);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return Float.valueOf((float)this.value);
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name == null) return "TAG_Float" + append + ": " + this.value;
        if (name.equals((Object)"")) return "TAG_Float" + append + ": " + this.value;
        append = "(\"" + this.getName() + "\")";
        return "TAG_Float" + append + ": " + this.value;
    }

    @Override
    public FloatTag clone() {
        return new FloatTag((String)this.getName(), (float)this.value);
    }
}

