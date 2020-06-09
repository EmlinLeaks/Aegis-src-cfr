/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import java.util.Arrays;

public final class ByteArrayTag
extends Tag<byte[]> {
    private final byte[] value;

    public ByteArrayTag(String name, byte[] value) {
        super((TagType)TagType.TAG_BYTE_ARRAY, (String)name);
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder hex = new StringBuilder();
        byte[] arrby = this.value;
        int n = arrby.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                String name = this.getName();
                String append = "";
                if (name == null) return "TAG_Byte_Array" + append + ": " + hex.toString();
                if (name.equals((Object)"")) return "TAG_Byte_Array" + append + ": " + hex.toString();
                append = "(\"" + this.getName() + "\")";
                return "TAG_Byte_Array" + append + ": " + hex.toString();
            }
            byte b = arrby[n2];
            String hexDigits = Integer.toHexString((int)b).toUpperCase();
            if (hexDigits.length() == 1) {
                hex.append((String)"0");
            }
            hex.append((String)hexDigits).append((String)" ");
            ++n2;
        } while (true);
    }

    @Override
    public ByteArrayTag clone() {
        byte[] clonedArray = this.cloneArray((byte[])this.value);
        return new ByteArrayTag((String)this.getName(), (byte[])clonedArray);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ByteArrayTag)) {
            return false;
        }
        ByteArrayTag tag = (ByteArrayTag)other;
        if (!Arrays.equals((byte[])this.value, (byte[])tag.value)) return false;
        if (!this.getName().equals((Object)tag.getName())) return false;
        return true;
    }

    private byte[] cloneArray(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        int length = byteArray.length;
        byte[] newArray = new byte[length];
        System.arraycopy((Object)byteArray, (int)0, (Object)newArray, (int)0, (int)length);
        return newArray;
    }
}

