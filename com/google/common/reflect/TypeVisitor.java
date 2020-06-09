/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.google.common.reflect;

import com.google.common.collect.Sets;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
abstract class TypeVisitor {
    private final Set<Type> visited = Sets.newHashSet();

    TypeVisitor() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void visit(Type ... types) {
        Type[] arr$ = types;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type type = arr$[i$];
            if (type != null && this.visited.add((Type)type)) {
                boolean succeeded = false;
                try {
                    if (type instanceof TypeVariable) {
                        this.visitTypeVariable((TypeVariable)type);
                    } else if (type instanceof WildcardType) {
                        this.visitWildcardType((WildcardType)((WildcardType)type));
                    } else if (type instanceof ParameterizedType) {
                        this.visitParameterizedType((ParameterizedType)((ParameterizedType)type));
                    } else if (type instanceof Class) {
                        this.visitClass((Class)type);
                    } else {
                        if (!(type instanceof GenericArrayType)) throw new AssertionError((Object)("Unknown type: " + type));
                        this.visitGenericArrayType((GenericArrayType)((GenericArrayType)type));
                    }
                    succeeded = true;
                }
                finally {
                    if (!succeeded) {
                        this.visited.remove((Object)type);
                    }
                }
            }
            ++i$;
        }
    }

    void visitClass(Class<?> t) {
    }

    void visitGenericArrayType(GenericArrayType t) {
    }

    void visitParameterizedType(ParameterizedType t) {
    }

    void visitTypeVariable(TypeVariable<?> t) {
    }

    void visitWildcardType(WildcardType t) {
    }
}

