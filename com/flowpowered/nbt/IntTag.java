/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

public final class IntTag
extends Tag<Integer> {
    private final int value;

    public IntTag(String name, int value) {
        super((TagType)TagType.TAG_INT, (String)name);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return Integer.valueOf((int)this.value);
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name == null) return "TAG_Int" + append + ": " + this.value;
        if (name.equals((Object)"")) return "TAG_Int" + append + ": " + this.value;
        append = "(\"" + this.getName() + "\")";
        return "TAG_Int" + append + ": " + this.value;
    }

    @Override
    public IntTag clone() {
        return new IntTag((String)this.getName(), (int)this.value);
    }
}

