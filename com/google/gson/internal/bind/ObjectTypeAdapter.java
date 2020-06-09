/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class ObjectTypeAdapter
extends TypeAdapter<Object> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        public <T> TypeAdapter<T> create(Gson gson, com.google.gson.reflect.TypeToken<T> type) {
            if (type.getRawType() != Object.class) return null;
            return new ObjectTypeAdapter((Gson)gson);
        }
    };
    private final Gson gson;

    ObjectTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (2.$SwitchMap$com$google$gson$stream$JsonToken[token.ordinal()]) {
            case 1: {
                ArrayList<Object> list = new ArrayList<Object>();
                in.beginArray();
                do {
                    if (!in.hasNext()) {
                        in.endArray();
                        return list;
                    }
                    list.add(this.read((JsonReader)in));
                } while (true);
            }
            case 2: {
                LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
                in.beginObject();
                do {
                    if (!in.hasNext()) {
                        in.endObject();
                        return map;
                    }
                    map.put(in.nextName(), this.read((JsonReader)in));
                } while (true);
            }
            case 3: {
                return in.nextString();
            }
            case 4: {
                return Double.valueOf((double)in.nextDouble());
            }
            case 5: {
                return Boolean.valueOf((boolean)in.nextBoolean());
            }
            case 6: {
                in.nextNull();
                return null;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        TypeAdapter<?> typeAdapter = this.gson.getAdapter(value.getClass());
        if (typeAdapter instanceof ObjectTypeAdapter) {
            out.beginObject();
            out.endObject();
            return;
        }
        typeAdapter.write((JsonWriter)out, value);
    }
}

