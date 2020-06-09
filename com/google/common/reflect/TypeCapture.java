/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.reflect;

import com.google.common.base.Preconditions;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class TypeCapture<T> {
    TypeCapture() {
    }

    final Type capture() {
        Type superclass = this.getClass().getGenericSuperclass();
        Preconditions.checkArgument((boolean)(superclass instanceof ParameterizedType), (String)"%s isn't parameterized", (Object)superclass);
        return ((ParameterizedType)superclass).getActualTypeArguments()[0];
    }
}

