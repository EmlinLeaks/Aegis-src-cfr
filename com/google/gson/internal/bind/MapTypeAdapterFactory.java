/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public final class MapTypeAdapterFactory
implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;
    final boolean complexMapKeySerialization;

    public MapTypeAdapterFactory(ConstructorConstructor constructorConstructor, boolean complexMapKeySerialization) {
        this.constructorConstructor = constructorConstructor;
        this.complexMapKeySerialization = complexMapKeySerialization;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        Class<T> rawType = typeToken.getRawType();
        if (!Map.class.isAssignableFrom(rawType)) {
            return null;
        }
        Class<?> rawTypeOfSrc = $Gson$Types.getRawType((Type)type);
        Type[] keyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes((Type)type, rawTypeOfSrc);
        TypeAdapter<?> keyAdapter = this.getKeyAdapter((Gson)gson, (Type)keyAndValueTypes[0]);
        TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get((Type)keyAndValueTypes[1]));
        ObjectConstructor<T> constructor = this.constructorConstructor.get(typeToken);
        return new Adapter<?, ?>((MapTypeAdapterFactory)this, (Gson)gson, (Type)keyAndValueTypes[0], keyAdapter, (Type)keyAndValueTypes[1], valueAdapter, constructor);
    }

    private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType) {
        TypeAdapter<Boolean> typeAdapter;
        if (keyType != Boolean.TYPE && keyType != Boolean.class) {
            typeAdapter = context.getAdapter(TypeToken.get((Type)keyType));
            return typeAdapter;
        }
        typeAdapter = TypeAdapters.BOOLEAN_AS_STRING;
        return typeAdapter;
    }
}

