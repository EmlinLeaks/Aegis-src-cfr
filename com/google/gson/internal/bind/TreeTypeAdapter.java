/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public final class TreeTypeAdapter<T>
extends TypeAdapter<T> {
    private final JsonSerializer<T> serializer;
    private final JsonDeserializer<T> deserializer;
    private final Gson gson;
    private final TypeToken<T> typeToken;
    private final TypeAdapterFactory skipPast;
    private final TreeTypeAdapter<T> context = new GsonContextImpl((TreeTypeAdapter)this, null);
    private TypeAdapter<T> delegate;

    public TreeTypeAdapter(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer, Gson gson, TypeToken<T> typeToken, TypeAdapterFactory skipPast) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.gson = gson;
        this.typeToken = typeToken;
        this.skipPast = skipPast;
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (this.deserializer == null) {
            return (T)this.delegate().read((JsonReader)in);
        }
        JsonElement value = Streams.parse((JsonReader)in);
        if (!value.isJsonNull()) return (T)this.deserializer.deserialize((JsonElement)value, (Type)this.typeToken.getType(), this.context);
        return (T)null;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (this.serializer == null) {
            this.delegate().write((JsonWriter)out, value);
            return;
        }
        if (value == null) {
            out.nullValue();
            return;
        }
        JsonElement tree = this.serializer.serialize(value, (Type)this.typeToken.getType(), this.context);
        Streams.write((JsonElement)tree, (JsonWriter)out);
    }

    private TypeAdapter<T> delegate() {
        TypeAdapter<T> typeAdapter;
        TypeAdapter<T> d = this.delegate;
        if (d != null) {
            typeAdapter = d;
            return typeAdapter;
        }
        typeAdapter = this.delegate = this.gson.getDelegateAdapter((TypeAdapterFactory)this.skipPast, this.typeToken);
        return typeAdapter;
    }

    public static TypeAdapterFactory newFactory(TypeToken<?> exactType, Object typeAdapter) {
        return new SingleTypeFactory((Object)typeAdapter, exactType, (boolean)false, null);
    }

    public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken<?> exactType, Object typeAdapter) {
        boolean matchRawType = exactType.getType() == exactType.getRawType();
        return new SingleTypeFactory((Object)typeAdapter, exactType, (boolean)matchRawType, null);
    }

    public static TypeAdapterFactory newTypeHierarchyFactory(Class<?> hierarchyType, Object typeAdapter) {
        return new SingleTypeFactory((Object)typeAdapter, null, (boolean)false, hierarchyType);
    }

    static /* synthetic */ Gson access$100(TreeTypeAdapter x0) {
        return x0.gson;
    }
}

