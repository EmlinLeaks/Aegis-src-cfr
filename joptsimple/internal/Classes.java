/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Classes {
    private static final Map<Class<?>, Class<?>> WRAPPERS = new HashMap<Class<?>, Class<?>>((int)13);

    private Classes() {
        throw new UnsupportedOperationException();
    }

    public static String shortNameOf(String className) {
        return className.substring((int)(className.lastIndexOf((int)46) + 1));
    }

    public static <T> Class<T> wrapperOf(Class<T> clazz) {
        Class<Object> class_;
        if (clazz.isPrimitive()) {
            class_ = WRAPPERS.get(clazz);
            return class_;
        }
        class_ = clazz;
        return class_;
    }

    static {
        WRAPPERS.put(Boolean.TYPE, Boolean.class);
        WRAPPERS.put(Byte.TYPE, Byte.class);
        WRAPPERS.put(Character.TYPE, Character.class);
        WRAPPERS.put(Double.TYPE, Double.class);
        WRAPPERS.put(Float.TYPE, Float.class);
        WRAPPERS.put(Integer.TYPE, Integer.class);
        WRAPPERS.put(Long.TYPE, Long.class);
        WRAPPERS.put(Short.TYPE, Short.class);
        WRAPPERS.put(Void.TYPE, Void.class);
    }
}

