/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.holder;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.holder.Field;

public class FieldValue<T> {
    private T value;
    private final Field<T> field;
    private final String key;
    private final T defaultValue;

    public FieldValue(String key, Field<T> field) {
        this((String)key, field, null);
    }

    public FieldValue(String key, Field<T> field, T defaultValue) {
        this.field = field;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public T load(CompoundTag tag) {
        Object subTag = tag.getValue().get((Object)this.key);
        if (subTag == null) {
            this.value = this.defaultValue;
            return (T)this.value;
        }
        this.value = this.field.getValue(subTag);
        return (T)this.value;
    }

    public void save(CompoundMap tag) {
        T value = this.value;
        if (value == null && (value = this.defaultValue) == null) {
            return;
        }
        Tag<?> t = this.field.getValue((String)this.key, value);
        tag.put(t);
    }

    public T get() {
        return (T)this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public static <T> FieldValue<T> from(String name, Field<T> field, T defaultValue) {
        return new FieldValue<T>((String)name, field, defaultValue);
    }

    public static <T> FieldValue<T> from(String name, Field<T> field) {
        return new FieldValue<T>((String)name, field);
    }
}

