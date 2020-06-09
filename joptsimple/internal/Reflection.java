/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import joptsimple.ValueConverter;
import joptsimple.internal.Classes;
import joptsimple.internal.ConstructorInvokingValueConverter;
import joptsimple.internal.MethodInvokingValueConverter;
import joptsimple.internal.ReflectionException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Reflection {
    private Reflection() {
        throw new UnsupportedOperationException();
    }

    public static <V> ValueConverter<V> findConverter(Class<V> clazz) {
        Class<V> maybeWrapper = Classes.wrapperOf(clazz);
        ValueConverter<V> valueOf = Reflection.valueOfConverter(maybeWrapper);
        if (valueOf != null) {
            return valueOf;
        }
        ValueConverter<V> constructor = Reflection.constructorConverter(maybeWrapper);
        if (constructor == null) throw new IllegalArgumentException((String)(clazz + " is not a value type"));
        return constructor;
    }

    private static <V> ValueConverter<V> valueOfConverter(Class<V> clazz) {
        try {
            Method valueOf = clazz.getDeclaredMethod((String)"valueOf", String.class);
            if (!Reflection.meetsConverterRequirements((Method)valueOf, clazz)) return null;
            return new MethodInvokingValueConverter<V>((Method)valueOf, clazz);
        }
        catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static <V> ValueConverter<V> constructorConverter(Class<V> clazz) {
        try {
            return new ConstructorInvokingValueConverter<V>(clazz.getConstructor(String.class));
        }
        catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    public static <T> T instantiate(Constructor<T> constructor, Object ... args) {
        try {
            return (T)constructor.newInstance((Object[])args);
        }
        catch (Exception ex) {
            throw Reflection.reflectionException((Exception)ex);
        }
    }

    public static Object invoke(Method method, Object ... args) {
        try {
            return method.invoke(null, (Object[])args);
        }
        catch (Exception ex) {
            throw Reflection.reflectionException((Exception)ex);
        }
    }

    public static <V> V convertWith(ValueConverter<V> converter, String raw) {
        String string;
        if (converter == null) {
            string = raw;
            return (V)((V)string);
        }
        string = converter.convert((String)raw);
        return (V)string;
    }

    private static boolean meetsConverterRequirements(Method method, Class<?> expectedReturnType) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic((int)modifiers)) return false;
        if (!Modifier.isStatic((int)modifiers)) return false;
        if (!expectedReturnType.equals(method.getReturnType())) return false;
        return true;
    }

    private static RuntimeException reflectionException(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return new ReflectionException((Throwable)ex);
        }
        if (ex instanceof InvocationTargetException) {
            return new ReflectionException((Throwable)ex.getCause());
        }
        if (!(ex instanceof RuntimeException)) return new ReflectionException((Throwable)ex);
        return (RuntimeException)ex;
    }
}

