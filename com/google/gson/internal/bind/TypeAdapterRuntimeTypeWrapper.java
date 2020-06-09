/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class TypeAdapterRuntimeTypeWrapper<T>
extends TypeAdapter<T> {
    private final Gson context;
    private final TypeAdapter<T> delegate;
    private final Type type;

    TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
        this.context = context;
        this.delegate = delegate;
        this.type = type;
    }

    @Override
    public T read(JsonReader in) throws IOException {
        return (T)this.delegate.read((JsonReader)in);
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        TypeAdapter<Object> chosen = this.delegate;
        Type runtimeType = this.getRuntimeTypeIfMoreSpecific((Type)this.type, value);
        if (runtimeType != this.type) {
            TypeAdapter<?> runtimeTypeAdapter = this.context.getAdapter(TypeToken.get((Type)runtimeType));
            chosen = !(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter) ? runtimeTypeAdapter : (!(this.delegate instanceof ReflectiveTypeAdapterFactory.Adapter) ? this.delegate : runtimeTypeAdapter);
        }
        chosen.write((JsonWriter)out, value);
    }

    private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
        if (value == null) return type;
        if (type == Object.class) return value.getClass();
        if (type instanceof TypeVariable) return value.getClass();
        if (!(type instanceof Class)) return type;
        return value.getClass();
    }
}

