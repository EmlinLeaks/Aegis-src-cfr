/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.TypeAdapterRuntimeTypeWrapper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public final class ArrayTypeAdapter<E>
extends TypeAdapter<Object> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        public <T> TypeAdapter<T> create(Gson gson, com.google.gson.reflect.TypeToken<T> typeToken) {
            Type type = typeToken.getType();
            if (!(type instanceof java.lang.reflect.GenericArrayType)) {
                if (!(type instanceof Class)) return null;
                if (!((Class)type).isArray()) {
                    return null;
                }
            }
            Type componentType = com.google.gson.internal.$Gson$Types.getArrayComponentType((Type)type);
            TypeAdapter<?> componentTypeAdapter = gson.getAdapter(com.google.gson.reflect.TypeToken.get((Type)componentType));
            return new ArrayTypeAdapter<?>((Gson)gson, componentTypeAdapter, com.google.gson.internal.$Gson$Types.getRawType((Type)componentType));
        }
    };
    private final Class<E> componentType;
    private final TypeAdapter<E> componentTypeAdapter;

    public ArrayTypeAdapter(Gson context, TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
        this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>((Gson)context, componentTypeAdapter, componentType);
        this.componentType = componentType;
    }

    @Override
    public Object read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        ArrayList<E> list = new ArrayList<E>();
        in.beginArray();
        while (in.hasNext()) {
            E instance = this.componentTypeAdapter.read((JsonReader)in);
            list.add(instance);
        }
        in.endArray();
        Object array = Array.newInstance(this.componentType, (int)list.size());
        int i = 0;
        while (i < list.size()) {
            Array.set((Object)array, (int)i, list.get((int)i));
            ++i;
        }
        return array;
    }

    @Override
    public void write(JsonWriter out, Object array) throws IOException {
        if (array == null) {
            out.nullValue();
            return;
        }
        out.beginArray();
        int i = 0;
        int length = Array.getLength((Object)array);
        do {
            if (i >= length) {
                out.endArray();
                return;
            }
            Object value = Array.get((Object)array, (int)i);
            this.componentTypeAdapter.write((JsonWriter)out, value);
            ++i;
        } while (true);
    }
}

