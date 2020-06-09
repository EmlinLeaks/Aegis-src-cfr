/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.AbstractConstant;
import io.netty.util.AttributeKey;
import io.netty.util.ConstantPool;

public final class AttributeKey<T>
extends AbstractConstant<AttributeKey<T>> {
    private static final ConstantPool<AttributeKey<Object>> pool = new ConstantPool<AttributeKey<Object>>(){

        protected AttributeKey<Object> newConstant(int id, String name) {
            return new AttributeKey<Object>((int)id, (String)name);
        }
    };

    public static <T> AttributeKey<T> valueOf(String name) {
        return pool.valueOf((String)name);
    }

    public static boolean exists(String name) {
        return pool.exists((String)name);
    }

    public static <T> AttributeKey<T> newInstance(String name) {
        return pool.newInstance((String)name);
    }

    public static <T> AttributeKey<T> valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        return pool.valueOf(firstNameComponent, (String)secondNameComponent);
    }

    private AttributeKey(int id, String name) {
        super((int)id, (String)name);
    }
}

