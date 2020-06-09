/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

public final class DoubleTag
extends Tag<Double> {
    private final double value;

    public DoubleTag(String name, double value) {
        super((TagType)TagType.TAG_DOUBLE, (String)name);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return Double.valueOf((double)this.value);
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name == null) return "TAG_Double" + append + ": " + this.value;
        if (name.equals((Object)"")) return "TAG_Double" + append + ": " + this.value;
        append = "(\"" + this.getName() + "\")";
        return "TAG_Double" + append + ": " + this.value;
    }

    @Override
    public DoubleTag clone() {
        return new DoubleTag((String)this.getName(), (double)this.value);
    }
}

