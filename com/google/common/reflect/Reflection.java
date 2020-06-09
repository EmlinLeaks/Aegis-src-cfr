/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Beta
public final class Reflection {
    public static String getPackageName(Class<?> clazz) {
        return Reflection.getPackageName((String)clazz.getName());
    }

    public static String getPackageName(String classFullName) {
        int lastDot = classFullName.lastIndexOf((int)46);
        if (lastDot < 0) {
            return "";
        }
        String string = classFullName.substring((int)0, (int)lastDot);
        return string;
    }

    public static void initialize(Class<?> ... classes) {
        Class<?>[] arr$ = classes;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Class<?> clazz = arr$[i$];
            try {
                Class.forName((String)clazz.getName(), (boolean)true, (ClassLoader)clazz.getClassLoader());
            }
            catch (ClassNotFoundException e) {
                throw new AssertionError((Object)e);
            }
            ++i$;
        }
    }

    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        Preconditions.checkNotNull(handler);
        Preconditions.checkArgument((boolean)interfaceType.isInterface(), (String)"%s is not an interface", interfaceType);
        Object object = Proxy.newProxyInstance((ClassLoader)interfaceType.getClassLoader(), new Class[]{interfaceType}, (InvocationHandler)handler);
        return (T)interfaceType.cast((Object)object);
    }

    private Reflection() {
    }
}

