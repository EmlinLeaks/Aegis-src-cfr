/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.util;

import com.flowpowered.nbt.Tag;

public class NBTMapper {
    public static Object toTagValue(Tag<?> t) {
        if (t != null) return t.getValue();
        return null;
    }

    public static <T> T getTagValue(Tag<?> t, Class<? extends T> clazz) {
        Object o = NBTMapper.toTagValue(t);
        if (o == null) {
            return (T)null;
        }
        try {
            return (T)clazz.cast((Object)o);
        }
        catch (ClassCastException e) {
            return (T)null;
        }
    }

    public static <T, U extends T> T toTagValue(Tag<?> t, Class<? extends T> clazz, U defaultValue) {
        Object o = NBTMapper.toTagValue(t);
        if (o == null) {
            return (T)defaultValue;
        }
        try {
            T value = clazz.cast((Object)o);
            return (T)value;
        }
        catch (ClassCastException e) {
            return (T)defaultValue;
        }
    }
}

