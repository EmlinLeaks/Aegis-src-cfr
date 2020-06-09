/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Excluder
implements TypeAdapterFactory,
Cloneable {
    private static final double IGNORE_VERSIONS = -1.0;
    public static final Excluder DEFAULT = new Excluder();
    private double version = -1.0;
    private int modifiers = 136;
    private boolean serializeInnerClasses = true;
    private boolean requireExpose;
    private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
    private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();

    protected Excluder clone() {
        try {
            return (Excluder)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError((Object)e);
        }
    }

    public Excluder withVersion(double ignoreVersionsAfter) {
        Excluder result = this.clone();
        result.version = ignoreVersionsAfter;
        return result;
    }

    public Excluder withModifiers(int ... modifiers) {
        Excluder result = this.clone();
        result.modifiers = 0;
        int[] arrn = modifiers;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int modifier = arrn[n2];
            result.modifiers |= modifier;
            ++n2;
        }
        return result;
    }

    public Excluder disableInnerClassSerialization() {
        Excluder result = this.clone();
        result.serializeInnerClasses = false;
        return result;
    }

    public Excluder excludeFieldsWithoutExposeAnnotation() {
        Excluder result = this.clone();
        result.requireExpose = true;
        return result;
    }

    public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy, boolean serialization, boolean deserialization) {
        Excluder result = this.clone();
        if (serialization) {
            result.serializationStrategies = new ArrayList<ExclusionStrategy>(this.serializationStrategies);
            result.serializationStrategies.add((ExclusionStrategy)exclusionStrategy);
        }
        if (!deserialization) return result;
        result.deserializationStrategies = new ArrayList<ExclusionStrategy>(this.deserializationStrategies);
        result.deserializationStrategies.add((ExclusionStrategy)exclusionStrategy);
        return result;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = type.getRawType();
        boolean skipSerialize = this.excludeClass(rawType, (boolean)true);
        boolean skipDeserialize = this.excludeClass(rawType, (boolean)false);
        if (skipSerialize) return new TypeAdapter<T>((Excluder)this, (boolean)skipDeserialize, (boolean)skipSerialize, (Gson)gson, type){
            private TypeAdapter<T> delegate;
            final /* synthetic */ boolean val$skipDeserialize;
            final /* synthetic */ boolean val$skipSerialize;
            final /* synthetic */ Gson val$gson;
            final /* synthetic */ TypeToken val$type;
            final /* synthetic */ Excluder this$0;
            {
                this.this$0 = this$0;
                this.val$skipDeserialize = bl;
                this.val$skipSerialize = bl2;
                this.val$gson = gson;
                this.val$type = typeToken;
            }

            public T read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                if (!this.val$skipDeserialize) return (T)this.delegate().read((com.google.gson.stream.JsonReader)in);
                in.skipValue();
                return (T)null;
            }

            public void write(com.google.gson.stream.JsonWriter out, T value) throws java.io.IOException {
                if (this.val$skipSerialize) {
                    out.nullValue();
                    return;
                }
                this.delegate().write((com.google.gson.stream.JsonWriter)out, value);
            }

            private TypeAdapter<T> delegate() {
                TypeAdapter<T> typeAdapter;
                TypeAdapter<T> d = this.delegate;
                if (d != null) {
                    typeAdapter = d;
                    return typeAdapter;
                }
                typeAdapter = this.delegate = this.val$gson.getDelegateAdapter((TypeAdapterFactory)this.this$0, this.val$type);
                return typeAdapter;
            }
        };
        if (skipDeserialize) return new /* invalid duplicate definition of identical inner class */;
        return null;
    }

    public boolean excludeField(Field field, boolean serialize) {
        ExclusionStrategy exclusionStrategy;
        if ((this.modifiers & field.getModifiers()) != 0) {
            return true;
        }
        if (this.version != -1.0 && !this.isValidVersion((Since)field.getAnnotation(Since.class), (Until)field.getAnnotation(Until.class))) {
            return true;
        }
        if (field.isSynthetic()) {
            return true;
        }
        if (this.requireExpose) {
            Expose annotation = field.getAnnotation(Expose.class);
            if (annotation == null) return true;
            if (serialize ? !annotation.serialize() : !annotation.deserialize()) {
                return true;
            }
        }
        if (!this.serializeInnerClasses && this.isInnerClass(field.getType())) {
            return true;
        }
        if (this.isAnonymousOrLocal(field.getType())) {
            return true;
        }
        List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
        if (list.isEmpty()) return false;
        FieldAttributes fieldAttributes = new FieldAttributes((Field)field);
        Iterator<ExclusionStrategy> iterator = list.iterator();
        do {
            if (!iterator.hasNext()) return false;
        } while (!(exclusionStrategy = iterator.next()).shouldSkipField((FieldAttributes)fieldAttributes));
        return true;
    }

    public boolean excludeClass(Class<?> clazz, boolean serialize) {
        ExclusionStrategy exclusionStrategy;
        if (this.version != -1.0 && !this.isValidVersion((Since)clazz.getAnnotation(Since.class), (Until)clazz.getAnnotation(Until.class))) {
            return true;
        }
        if (!this.serializeInnerClasses && this.isInnerClass(clazz)) {
            return true;
        }
        if (this.isAnonymousOrLocal(clazz)) {
            return true;
        }
        List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
        Iterator<ExclusionStrategy> iterator = list.iterator();
        do {
            if (!iterator.hasNext()) return false;
        } while (!(exclusionStrategy = iterator.next()).shouldSkipClass(clazz));
        return true;
    }

    private boolean isAnonymousOrLocal(Class<?> clazz) {
        if (Enum.class.isAssignableFrom(clazz)) return false;
        if (clazz.isAnonymousClass()) return true;
        if (!clazz.isLocalClass()) return false;
        return true;
    }

    private boolean isInnerClass(Class<?> clazz) {
        if (!clazz.isMemberClass()) return false;
        if (this.isStatic(clazz)) return false;
        return true;
    }

    private boolean isStatic(Class<?> clazz) {
        if ((clazz.getModifiers() & 8) == 0) return false;
        return true;
    }

    private boolean isValidVersion(Since since, Until until) {
        if (!this.isValidSince((Since)since)) return false;
        if (!this.isValidUntil((Until)until)) return false;
        return true;
    }

    private boolean isValidSince(Since annotation) {
        if (annotation == null) return true;
        double annotationVersion = annotation.value();
        if (!(annotationVersion > this.version)) return true;
        return false;
    }

    private boolean isValidUntil(Until annotation) {
        if (annotation == null) return true;
        double annotationVersion = annotation.value();
        if (!(annotationVersion <= this.version)) return true;
        return false;
    }
}

