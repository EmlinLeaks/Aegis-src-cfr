/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

public final class ByteTag
extends Tag<Byte> {
    private final byte value;

    public ByteTag(String name, boolean value) {
        this((String)name, (byte)((byte)(value ? 1 : 0)));
    }

    public ByteTag(String name, byte value) {
        super((TagType)TagType.TAG_BYTE, (String)name);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return Byte.valueOf((byte)this.value);
    }

    public boolean getBooleanValue() {
        if (this.value == 0) return false;
        return true;
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name == null) return "TAG_Byte" + append + ": " + this.value;
        if (name.equals((Object)"")) return "TAG_Byte" + append + ": " + this.value;
        append = "(\"" + this.getName() + "\")";
        return "TAG_Byte" + append + ": " + this.value;
    }

    @Override
    public ByteTag clone() {
        return new ByteTag((String)this.getName(), (byte)this.value);
    }

    public static Boolean getBooleanValue(Tag<?> t) {
        if (t == null) {
            return null;
        }
        try {
            ByteTag byteTag = (ByteTag)t;
            return Boolean.valueOf((boolean)byteTag.getBooleanValue());
        }
        catch (ClassCastException e) {
            return null;
        }
    }
}

