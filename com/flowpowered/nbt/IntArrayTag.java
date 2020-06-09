/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import java.util.Arrays;

public class IntArrayTag
extends Tag<int[]> {
    private final int[] value;

    public IntArrayTag(String name, int[] value) {
        super((TagType)TagType.TAG_INT_ARRAY, (String)name);
        this.value = value;
    }

    @Override
    public int[] getValue() {
        return this.value;
    }

    public String toString() {
        StringBuilder hex = new StringBuilder();
        int[] arrn = this.value;
        int n = arrn.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                String name = this.getName();
                String append = "";
                if (name == null) return "TAG_Int_Array" + append + ": " + hex.toString();
                if (name.equals((Object)"")) return "TAG_Int_Array" + append + ": " + hex.toString();
                append = "(\"" + this.getName() + "\")";
                return "TAG_Int_Array" + append + ": " + hex.toString();
            }
            int s = arrn[n2];
            String hexDigits = Integer.toHexString((int)s).toUpperCase();
            if (hexDigits.length() == 1) {
                hex.append((String)"0");
            }
            hex.append((String)hexDigits).append((String)" ");
            ++n2;
        } while (true);
    }

    @Override
    public IntArrayTag clone() {
        int[] clonedArray = this.cloneArray((int[])this.value);
        return new IntArrayTag((String)this.getName(), (int[])clonedArray);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof IntArrayTag)) {
            return false;
        }
        IntArrayTag tag = (IntArrayTag)other;
        if (!Arrays.equals((int[])this.value, (int[])tag.value)) return false;
        if (!this.getName().equals((Object)tag.getName())) return false;
        return true;
    }

    private int[] cloneArray(int[] intArray) {
        if (intArray == null) {
            return null;
        }
        int length = intArray.length;
        byte[] newArray = new byte[length];
        System.arraycopy((Object)intArray, (int)0, (Object)newArray, (int)0, (int)length);
        return intArray;
    }
}

