/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import java.lang.annotation.Annotation;

public final class JsonAdapterAnnotationTypeAdapterFactory
implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
        this.constructorConstructor = constructorConstructor;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
        Class<T> rawType = targetType.getRawType();
        JsonAdapter annotation = rawType.getAnnotation(JsonAdapter.class);
        if (annotation != null) return this.getTypeAdapter((ConstructorConstructor)this.constructorConstructor, (Gson)gson, targetType, (JsonAdapter)annotation);
        return null;
    }

    TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken<?> type, JsonAdapter annotation) {
        TypeAdapter<?> typeAdapter;
        ? instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();
        if (instance instanceof TypeAdapter) {
            typeAdapter = (TypeAdapter<?>)instance;
        } else if (instance instanceof TypeAdapterFactory) {
            typeAdapter = ((TypeAdapterFactory)instance).create((Gson)gson, type);
        } else {
            if (!(instance instanceof JsonSerializer)) {
                if (!(instance instanceof JsonDeserializer)) throw new IllegalArgumentException((String)"@JsonAdapter value must be TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer reference.");
            }
            JsonSerializer serializer = instance instanceof JsonSerializer ? (JsonSerializer)instance : null;
            JsonDeserializer deserializer = instance instanceof JsonDeserializer ? (JsonDeserializer)instance : null;
            typeAdapter = new TreeTypeAdapter<?>(serializer, deserializer, (Gson)gson, type, null);
        }
        if (typeAdapter == null) return typeAdapter;
        if (!annotation.nullSafe()) return typeAdapter;
        return typeAdapter.nullSafe();
    }
}

