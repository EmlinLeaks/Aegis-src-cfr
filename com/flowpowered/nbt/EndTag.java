/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

public final class EndTag
extends Tag<Object> {
    public EndTag() {
        super((TagType)TagType.TAG_END);
    }

    @Override
    public Object getValue() {
        return null;
    }

    public String toString() {
        return "TAG_End";
    }

    @Override
    public EndTag clone() {
        return new EndTag();
    }
}

