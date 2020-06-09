/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.introspector;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.GenericProperty;
import org.yaml.snakeyaml.util.ArrayUtils;

public class MethodProperty
extends GenericProperty {
    private final PropertyDescriptor property;
    private final boolean readable;
    private final boolean writable;

    private static Type discoverGenericType(PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        if (readMethod != null) {
            return readMethod.getGenericReturnType();
        }
        Method writeMethod = property.getWriteMethod();
        if (writeMethod == null) return null;
        Type[] paramTypes = writeMethod.getGenericParameterTypes();
        if (paramTypes.length <= 0) return null;
        return paramTypes[0];
    }

    public MethodProperty(PropertyDescriptor property) {
        super((String)property.getName(), property.getPropertyType(), (Type)MethodProperty.discoverGenericType((PropertyDescriptor)property));
        this.property = property;
        this.readable = property.getReadMethod() != null;
        this.writable = property.getWriteMethod() != null;
    }

    @Override
    public void set(Object object, Object value) throws Exception {
        if (!this.writable) {
            throw new YAMLException((String)("No writable property '" + this.getName() + "' on class: " + object.getClass().getName()));
        }
        this.property.getWriteMethod().invoke((Object)object, (Object[])new Object[]{value});
    }

    @Override
    public Object get(Object object) {
        try {
            this.property.getReadMethod().setAccessible((boolean)true);
            return this.property.getReadMethod().invoke((Object)object, (Object[])new Object[0]);
        }
        catch (Exception e) {
            throw new YAMLException((String)("Unable to find getter for property '" + this.property.getName() + "' on object " + object + ":" + e));
        }
    }

    @Override
    public List<Annotation> getAnnotations() {
        if (this.isReadable() && this.isWritable()) {
            return ArrayUtils.toUnmodifiableCompositeList(this.property.getReadMethod().getAnnotations(), this.property.getWriteMethod().getAnnotations());
        }
        if (!this.isReadable()) return ArrayUtils.toUnmodifiableList(this.property.getWriteMethod().getAnnotations());
        return ArrayUtils.toUnmodifiableList(this.property.getReadMethod().getAnnotations());
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        A annotation = null;
        if (this.isReadable()) {
            annotation = (A)this.property.getReadMethod().getAnnotation(annotationType);
        }
        if (annotation != null) return (A)annotation;
        if (!this.isWritable()) return (A)annotation;
        annotation = (A)this.property.getWriteMethod().getAnnotation(annotationType);
        return (A)annotation;
    }

    @Override
    public boolean isWritable() {
        return this.writable;
    }

    @Override
    public boolean isReadable() {
        return this.readable;
    }
}

