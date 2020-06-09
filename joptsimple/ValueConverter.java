/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

public interface ValueConverter<V> {
    public V convert(String var1);

    public Class<? extends V> valueType();

    public String valuePattern();
}

