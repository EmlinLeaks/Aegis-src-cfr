/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Element;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

@Beta
public abstract class Invokable<T, R>
extends Element
implements GenericDeclaration {
    <M extends AccessibleObject> Invokable(M member) {
        super(member);
    }

    public static Invokable<?, Object> from(Method method) {
        return new MethodInvokable<T>((Method)method);
    }

    public static <T> Invokable<T, T> from(Constructor<T> constructor) {
        return new ConstructorInvokable<T>(constructor);
    }

    public abstract boolean isOverridable();

    public abstract boolean isVarArgs();

    @CanIgnoreReturnValue
    public final R invoke(@Nullable T receiver, Object ... args) throws InvocationTargetException, IllegalAccessException {
        return (R)this.invokeInternal(receiver, (Object[])Preconditions.checkNotNull(args));
    }

    public final TypeToken<? extends R> getReturnType() {
        return TypeToken.of((Type)this.getGenericReturnType());
    }

    public final ImmutableList<Parameter> getParameters() {
        Type[] parameterTypes = this.getGenericParameterTypes();
        Annotation[][] annotations = this.getParameterAnnotations();
        ImmutableList.Builder<E> builder = ImmutableList.builder();
        int i = 0;
        while (i < parameterTypes.length) {
            builder.add((Object)new Parameter(this, (int)i, TypeToken.of((Type)parameterTypes[i]), (Annotation[])annotations[i]));
            ++i;
        }
        return builder.build();
    }

    public final ImmutableList<TypeToken<? extends Throwable>> getExceptionTypes() {
        ImmutableList.Builder<E> builder = ImmutableList.builder();
        Type[] arr$ = this.getGenericExceptionTypes();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Type type = arr$[i$];
            TypeToken<?> exceptionType = TypeToken.of((Type)type);
            builder.add(exceptionType);
            ++i$;
        }
        return builder.build();
    }

    public final <R1 extends R> Invokable<T, R1> returning(Class<R1> returnType) {
        return this.returning(TypeToken.of(returnType));
    }

    public final <R1 extends R> Invokable<T, R1> returning(TypeToken<R1> returnType) {
        if (returnType.isSupertypeOf(this.getReturnType())) return this;
        throw new IllegalArgumentException((String)("Invokable is known to return " + this.getReturnType() + ", not " + returnType));
    }

    public final Class<? super T> getDeclaringClass() {
        return super.getDeclaringClass();
    }

    public TypeToken<T> getOwnerType() {
        return TypeToken.of(this.getDeclaringClass());
    }

    abstract Object invokeInternal(@Nullable Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException;

    abstract Type[] getGenericParameterTypes();

    abstract Type[] getGenericExceptionTypes();

    abstract Annotation[][] getParameterAnnotations();

    abstract Type getGenericReturnType();
}

