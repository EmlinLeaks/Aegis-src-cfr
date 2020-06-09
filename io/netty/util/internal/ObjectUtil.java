/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import java.util.Collection;

public final class ObjectUtil {
    private ObjectUtil() {
    }

    public static <T> T checkNotNull(T arg, String text) {
        if (arg != null) return (T)arg;
        throw new NullPointerException((String)text);
    }

    public static int checkPositive(int i, String name) {
        if (i > 0) return i;
        throw new IllegalArgumentException((String)(name + ": " + i + " (expected: > 0)"));
    }

    public static long checkPositive(long i, String name) {
        if (i > 0L) return i;
        throw new IllegalArgumentException((String)(name + ": " + i + " (expected: > 0)"));
    }

    public static int checkPositiveOrZero(int i, String name) {
        if (i >= 0) return i;
        throw new IllegalArgumentException((String)(name + ": " + i + " (expected: >= 0)"));
    }

    public static long checkPositiveOrZero(long i, String name) {
        if (i >= 0L) return i;
        throw new IllegalArgumentException((String)(name + ": " + i + " (expected: >= 0)"));
    }

    public static <T> T[] checkNonEmpty(T[] array, String name) {
        ObjectUtil.checkNotNull(array, (String)name);
        ObjectUtil.checkPositive((int)array.length, (String)(name + ".length"));
        return array;
    }

    public static <T extends Collection<?>> T checkNonEmpty(T collection, String name) {
        ObjectUtil.checkNotNull(collection, (String)name);
        ObjectUtil.checkPositive((int)collection.size(), (String)(name + ".size"));
        return (T)collection;
    }

    public static int intValue(Integer wrapper, int defaultValue) {
        int n;
        if (wrapper != null) {
            n = wrapper.intValue();
            return n;
        }
        n = defaultValue;
        return n;
    }

    public static long longValue(Long wrapper, long defaultValue) {
        long l;
        if (wrapper != null) {
            l = wrapper.longValue();
            return l;
        }
        l = defaultValue;
        return l;
    }
}

