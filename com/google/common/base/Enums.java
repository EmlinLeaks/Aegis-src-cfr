/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@GwtCompatible(emulated=true)
public final class Enums {
    @GwtIncompatible
    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> enumConstantCache = new WeakHashMap<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>>();

    private Enums() {
    }

    @GwtIncompatible
    public static Field getField(Enum<?> enumValue) {
        Class<?> clazz = enumValue.getDeclaringClass();
        try {
            return clazz.getDeclaredField((String)enumValue.name());
        }
        catch (NoSuchFieldException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }

    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
        Preconditions.checkNotNull(enumClass);
        Preconditions.checkNotNull(value);
        return Platform.getEnumIfPresent(enumClass, (String)value);
    }

    @GwtIncompatible
    private static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> populateCache(Class<T> enumClass) {
        HashMap<String, WeakReference<Enum<?>>> result = new HashMap<String, WeakReference<Enum<?>>>();
        Iterator<E> i$ = EnumSet.allOf(enumClass).iterator();
        do {
            if (!i$.hasNext()) {
                enumConstantCache.put(enumClass, result);
                return result;
            }
            Enum enumInstance = (Enum)i$.next();
            result.put((String)enumInstance.name(), new WeakReference<Enum>(enumInstance));
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @GwtIncompatible
    static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> getEnumConstants(Class<T> enumClass) {
        Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> map = enumConstantCache;
        // MONITORENTER : map
        Map<String, WeakReference<Enum>> constants = enumConstantCache.get(enumClass);
        if (constants == null) {
            constants = Enums.populateCache(enumClass);
        }
        // MONITOREXIT : map
        return constants;
    }

    public static <T extends Enum<T>> Converter<String, T> stringConverter(Class<T> enumClass) {
        return new StringConverter<T>(enumClass);
    }
}

