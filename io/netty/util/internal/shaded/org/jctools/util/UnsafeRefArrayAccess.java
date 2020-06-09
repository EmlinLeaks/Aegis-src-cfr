/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.util;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;

public final class UnsafeRefArrayAccess {
    public static final long REF_ARRAY_BASE;
    public static final int REF_ELEMENT_SHIFT;

    public static <E> void spElement(E[] buffer, long offset, E e) {
        UnsafeAccess.UNSAFE.putObject(buffer, (long)offset, e);
    }

    public static <E> void soElement(E[] buffer, long offset, E e) {
        UnsafeAccess.UNSAFE.putOrderedObject(buffer, (long)offset, e);
    }

    public static <E> E lpElement(E[] buffer, long offset) {
        return (E)UnsafeAccess.UNSAFE.getObject(buffer, (long)offset);
    }

    public static <E> E lvElement(E[] buffer, long offset) {
        return (E)UnsafeAccess.UNSAFE.getObjectVolatile(buffer, (long)offset);
    }

    public static long calcElementOffset(long index) {
        return REF_ARRAY_BASE + (index << REF_ELEMENT_SHIFT);
    }

    static {
        int scale = UnsafeAccess.UNSAFE.arrayIndexScale(Object[].class);
        if (4 == scale) {
            REF_ELEMENT_SHIFT = 2;
        } else {
            if (8 != scale) throw new IllegalStateException((String)"Unknown pointer size");
            REF_ELEMENT_SHIFT = 3;
        }
        REF_ARRAY_BASE = (long)UnsafeAccess.UNSAFE.arrayBaseOffset(Object[].class);
    }
}

