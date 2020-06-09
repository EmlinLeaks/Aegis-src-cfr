/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import com.google.gson.internal.$Gson$Preconditions;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Primitives {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

    private Primitives() {
        throw new UnsupportedOperationException();
    }

    private static void add(Map<Class<?>, Class<?>> forward, Map<Class<?>, Class<?>> backward, Class<?> key, Class<?> value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    public static boolean isPrimitive(Type type) {
        return PRIMITIVE_TO_WRAPPER_TYPE.containsKey((Object)type);
    }

    public static boolean isWrapperType(Type type) {
        return WRAPPER_TO_PRIMITIVE_TYPE.containsKey((Object)$Gson$Preconditions.checkNotNull(type));
    }

    public static <T> Class<T> wrap(Class<T> type) {
        Class<Object> class_;
        Class<?> wrapped = PRIMITIVE_TO_WRAPPER_TYPE.get($Gson$Preconditions.checkNotNull(type));
        if (wrapped == null) {
            class_ = type;
            return class_;
        }
        class_ = wrapped;
        return class_;
    }

    public static <T> Class<T> unwrap(Class<T> type) {
        Class<Object> class_;
        Class<?> unwrapped = WRAPPER_TO_PRIMITIVE_TYPE.get($Gson$Preconditions.checkNotNull(type));
        if (unwrapped == null) {
            class_ = type;
            return class_;
        }
        class_ = unwrapped;
        return class_;
    }

    static {
        HashMap<Class<?>, Class<?>> primToWrap = new HashMap<Class<?>, Class<?>>((int)16);
        HashMap<Class<?>, Class<?>> wrapToPrim = new HashMap<Class<?>, Class<?>>((int)16);
        Primitives.add(primToWrap, wrapToPrim, Boolean.TYPE, Boolean.class);
        Primitives.add(primToWrap, wrapToPrim, Byte.TYPE, Byte.class);
        Primitives.add(primToWrap, wrapToPrim, Character.TYPE, Character.class);
        Primitives.add(primToWrap, wrapToPrim, Double.TYPE, Double.class);
        Primitives.add(primToWrap, wrapToPrim, Float.TYPE, Float.class);
        Primitives.add(primToWrap, wrapToPrim, Integer.TYPE, Integer.class);
        Primitives.add(primToWrap, wrapToPrim, Long.TYPE, Long.class);
        Primitives.add(primToWrap, wrapToPrim, Short.TYPE, Short.class);
        Primitives.add(primToWrap, wrapToPrim, Void.TYPE, Void.class);
        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
        WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
    }
}

