/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import java.util.Arrays;

public class ShortArrayTag
extends Tag<short[]> {
    private final short[] value;

    public ShortArrayTag(String name, short[] value) {
        super((TagType)TagType.TAG_SHORT_ARRAY, (String)name);
        this.value = value;
    }

    @Override
    public short[] getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder hex = new StringBuilder();
        short[] arrs = this.value;
        int n = arrs.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                String name = this.getName();
                String append = "";
                if (name == null) return "TAG_Short_Array" + append + ": " + hex.toString();
                if (name.equals((Object)"")) return "TAG_Short_Array" + append + ": " + hex.toString();
                append = "(\"" + this.getName() + "\")";
                return "TAG_Short_Array" + append + ": " + hex.toString();
            }
            short s = arrs[n2];
            String hexDigits = Integer.toHexString((int)s).toUpperCase();
            if (hexDigits.length() == 1) {
                hex.append((String)"0");
            }
            hex.append((String)hexDigits).append((String)" ");
            ++n2;
        } while (true);
    }

    @Override
    public ShortArrayTag clone() {
        short[] clonedArray = this.cloneArray((short[])this.value);
        return new ShortArrayTag((String)this.getName(), (short[])clonedArray);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ShortArrayTag)) {
            return false;
        }
        ShortArrayTag tag = (ShortArrayTag)other;
        if (!Arrays.equals((short[])this.value, (short[])tag.value)) return false;
        if (!this.getName().equals((Object)tag.getName())) return false;
        return true;
    }

    private short[] cloneArray(short[] shortArray) {
        if (shortArray == null) {
            return null;
        }
        int length = shortArray.length;
        short[] newArray = new short[length];
        System.arraycopy((Object)shortArray, (int)0, (Object)newArray, (int)0, (int)length);
        return shortArray;
    }
}

