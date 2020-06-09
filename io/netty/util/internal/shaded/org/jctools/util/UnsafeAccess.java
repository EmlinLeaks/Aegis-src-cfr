/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import sun.misc.Unsafe;

public class UnsafeAccess {
    public static final boolean SUPPORTS_GET_AND_SET;
    public static final Unsafe UNSAFE;

    static {
        Unsafe instance;
        try {
            Field field = Unsafe.class.getDeclaredField((String)"theUnsafe");
            field.setAccessible((boolean)true);
            instance = (Unsafe)field.get(null);
        }
        catch (Exception ignored) {
            try {
                Constructor<T> c = Unsafe.class.getDeclaredConstructor(new Class[0]);
                c.setAccessible((boolean)true);
                instance = (Unsafe)c.newInstance((Object[])new Object[0]);
            }
            catch (Exception e) {
                SUPPORTS_GET_AND_SET = false;
                throw new RuntimeException((Throwable)e);
            }
        }
        boolean getAndSetSupport = false;
        try {
            Unsafe.class.getMethod((String)"getAndSetObject", Object.class, Long.TYPE, Object.class);
            getAndSetSupport = true;
        }
        catch (Exception e) {
            // empty catch block
        }
        UNSAFE = instance;
        SUPPORTS_GET_AND_SET = getAndSetSupport;
    }
}

