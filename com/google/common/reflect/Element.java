/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.reflect;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import javax.annotation.Nullable;

class Element
extends AccessibleObject
implements Member {
    private final AccessibleObject accessibleObject;
    private final Member member;

    <M extends AccessibleObject> Element(M member) {
        Preconditions.checkNotNull(member);
        this.accessibleObject = member;
        this.member = (Member)member;
    }

    public TypeToken<?> getOwnerType() {
        return TypeToken.of(this.getDeclaringClass());
    }

    @Override
    public final boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return this.accessibleObject.isAnnotationPresent(annotationClass);
    }

    public final <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return (A)this.accessibleObject.getAnnotation(annotationClass);
    }

    @Override
    public final Annotation[] getAnnotations() {
        return this.accessibleObject.getAnnotations();
    }

    @Override
    public final Annotation[] getDeclaredAnnotations() {
        return this.accessibleObject.getDeclaredAnnotations();
    }

    @Override
    public final void setAccessible(boolean flag) throws SecurityException {
        this.accessibleObject.setAccessible((boolean)flag);
    }

    @Override
    public final boolean isAccessible() {
        return this.accessibleObject.isAccessible();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.member.getDeclaringClass();
    }

    @Override
    public final String getName() {
        return this.member.getName();
    }

    @Override
    public final int getModifiers() {
        return this.member.getModifiers();
    }

    @Override
    public final boolean isSynthetic() {
        return this.member.isSynthetic();
    }

    public final boolean isPublic() {
        return Modifier.isPublic((int)this.getModifiers());
    }

    public final boolean isProtected() {
        return Modifier.isProtected((int)this.getModifiers());
    }

    public final boolean isPackagePrivate() {
        if (this.isPrivate()) return false;
        if (this.isPublic()) return false;
        if (this.isProtected()) return false;
        return true;
    }

    public final boolean isPrivate() {
        return Modifier.isPrivate((int)this.getModifiers());
    }

    public final boolean isStatic() {
        return Modifier.isStatic((int)this.getModifiers());
    }

    public final boolean isFinal() {
        return Modifier.isFinal((int)this.getModifiers());
    }

    public final boolean isAbstract() {
        return Modifier.isAbstract((int)this.getModifiers());
    }

    public final boolean isNative() {
        return Modifier.isNative((int)this.getModifiers());
    }

    public final boolean isSynchronized() {
        return Modifier.isSynchronized((int)this.getModifiers());
    }

    final boolean isVolatile() {
        return Modifier.isVolatile((int)this.getModifiers());
    }

    final boolean isTransient() {
        return Modifier.isTransient((int)this.getModifiers());
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Element)) return false;
        Element that = (Element)obj;
        if (!this.getOwnerType().equals(that.getOwnerType())) return false;
        if (!this.member.equals((Object)that.member)) return false;
        return true;
    }

    public int hashCode() {
        return this.member.hashCode();
    }

    public String toString() {
        return this.member.toString();
    }
}

