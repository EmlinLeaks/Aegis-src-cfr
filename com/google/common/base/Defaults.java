/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtIncompatible
public final class Defaults {
    private static final Map<Class<?>, Object> DEFAULTS;

    private Defaults() {
    }

    private static <T> void put(Map<Class<?>, Object> map, Class<T> type, T value) {
        map.put(type, value);
    }

    @Nullable
    public static <T> T defaultValue(Class<T> type) {
        Object t = DEFAULTS.get(Preconditions.checkNotNull(type));
        return (T)t;
    }

    static {
        HashMap<Class<?>, Object> map = new HashMap<Class<?>, Object>();
        Defaults.put(map, Boolean.TYPE, Boolean.valueOf((boolean)false));
        Defaults.put(map, Character.TYPE, Character.valueOf((char)'\u0000'));
        Defaults.put(map, Byte.TYPE, Byte.valueOf((byte)0));
        Defaults.put(map, Short.TYPE, Short.valueOf((short)0));
        Defaults.put(map, Integer.TYPE, Integer.valueOf((int)0));
        Defaults.put(map, Long.TYPE, Long.valueOf((long)0L));
        Defaults.put(map, Float.TYPE, Float.valueOf((float)0.0f));
        Defaults.put(map, Double.TYPE, Double.valueOf((double)0.0));
        DEFAULTS = Collections.unmodifiableMap(map);
    }
}

