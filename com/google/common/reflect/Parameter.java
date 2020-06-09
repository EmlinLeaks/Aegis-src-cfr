/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
public final class Parameter
implements AnnotatedElement {
    private final Invokable<?, ?> declaration;
    private final int position;
    private final TypeToken<?> type;
    private final ImmutableList<Annotation> annotations;

    Parameter(Invokable<?, ?> declaration, int position, TypeToken<?> type, Annotation[] annotations) {
        this.declaration = declaration;
        this.position = position;
        this.type = type;
        this.annotations = ImmutableList.copyOf(annotations);
    }

    public TypeToken<?> getType() {
        return this.type;
    }

    public Invokable<?, ?> getDeclaringInvokable() {
        return this.declaration;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        if (this.getAnnotation(annotationType) == null) return false;
        return true;
    }

    @Nullable
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        Annotation annotation;
        Preconditions.checkNotNull(annotationType);
        Iterator i$ = this.annotations.iterator();
        do {
            if (!i$.hasNext()) return (A)null;
        } while (!annotationType.isInstance((Object)(annotation = (Annotation)i$.next())));
        return (A)((Annotation)annotationType.cast((Object)annotation));
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.getDeclaredAnnotations();
    }

    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return this.getDeclaredAnnotationsByType(annotationType);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.annotations.toArray(new Annotation[this.annotations.size()]);
    }

    @Nullable
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationType) {
        Preconditions.checkNotNull(annotationType);
        return (A)((Annotation)FluentIterable.from(this.annotations).filter(annotationType).first().orNull());
    }

    public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationType) {
        return (Annotation[])FluentIterable.from(this.annotations).filter(annotationType).toArray(annotationType);
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Parameter)) return false;
        Parameter that = (Parameter)obj;
        if (this.position != that.position) return false;
        if (!this.declaration.equals(that.declaration)) return false;
        return true;
    }

    public int hashCode() {
        return this.position;
    }

    public String toString() {
        return this.type + " arg" + this.position;
    }
}

