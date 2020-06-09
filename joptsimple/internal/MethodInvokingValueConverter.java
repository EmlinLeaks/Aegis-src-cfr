/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.lang.reflect.Method;
import joptsimple.ValueConverter;
import joptsimple.internal.Reflection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class MethodInvokingValueConverter<V>
implements ValueConverter<V> {
    private final Method method;
    private final Class<V> clazz;

    MethodInvokingValueConverter(Method method, Class<V> clazz) {
        this.method = method;
        this.clazz = clazz;
    }

    @Override
    public V convert(String value) {
        return (V)this.clazz.cast((Object)Reflection.invoke((Method)this.method, (Object[])new Object[]{value}));
    }

    @Override
    public Class<V> valueType() {
        return this.clazz;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}

