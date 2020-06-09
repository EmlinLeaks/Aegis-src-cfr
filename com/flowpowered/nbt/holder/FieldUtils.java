/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.holder;

import com.flowpowered.nbt.Tag;

public class FieldUtils {
    private FieldUtils() {
    }

    public static <T extends Tag<?>> T checkTagCast(Tag<?> tag, Class<T> type) throws IllegalArgumentException {
        if (tag == null) {
            throw new IllegalArgumentException((String)("Expected tag of type " + type.getName() + ", was null"));
        }
        if (type.isInstance(tag)) return (T)((Tag)type.cast(tag));
        throw new IllegalArgumentException((String)("Expected tag to be a " + type.getName() + ", was a " + tag.getClass().getName()));
    }
}

