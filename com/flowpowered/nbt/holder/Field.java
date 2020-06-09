/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.holder;

import com.flowpowered.nbt.Tag;

public interface Field<T> {
    public T getValue(Tag<?> var1) throws IllegalArgumentException;

    public Tag<?> getValue(String var1, T var2);
}

