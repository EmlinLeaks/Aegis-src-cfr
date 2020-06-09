/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.GenericProperty;
import org.yaml.snakeyaml.util.ArrayUtils;

public class FieldProperty
extends GenericProperty {
    private final Field field;

    public FieldProperty(Field field) {
        super((String)field.getName(), field.getType(), (Type)field.getGenericType());
        this.field = field;
        field.setAccessible((boolean)true);
    }

    @Override
    public void set(Object object, Object value) throws Exception {
        this.field.set((Object)object, (Object)value);
    }

    @Override
    public Object get(Object object) {
        try {
            return this.field.get((Object)object);
        }
        catch (Exception e) {
            throw new YAMLException((String)("Unable to access field " + this.field.getName() + " on object " + object + " : " + e));
        }
    }

    @Override
    public List<Annotation> getAnnotations() {
        return ArrayUtils.toUnmodifiableList(this.field.getAnnotations());
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return (A)this.field.getAnnotation(annotationType);
    }
}

