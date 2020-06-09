/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public abstract class TypeAdapter<T> {
    public abstract void write(JsonWriter var1, T var2) throws IOException;

    public final void toJson(Writer out, T value) throws IOException {
        JsonWriter writer = new JsonWriter((Writer)out);
        this.write((JsonWriter)writer, value);
    }

    public final TypeAdapter<T> nullSafe() {
        return new TypeAdapter<T>((TypeAdapter)this){
            final /* synthetic */ TypeAdapter this$0;
            {
                this.this$0 = this$0;
            }

            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                this.this$0.write((JsonWriter)out, value);
            }

            public T read(JsonReader reader) throws IOException {
                if (reader.peek() != com.google.gson.stream.JsonToken.NULL) return (T)this.this$0.read((JsonReader)reader);
                reader.nextNull();
                return (T)null;
            }
        };
    }

    public final String toJson(T value) {
        StringWriter stringWriter = new StringWriter();
        try {
            this.toJson((Writer)stringWriter, value);
            return stringWriter.toString();
        }
        catch (IOException e) {
            throw new AssertionError((Object)e);
        }
    }

    public final JsonElement toJsonTree(T value) {
        try {
            JsonTreeWriter jsonWriter = new JsonTreeWriter();
            this.write((JsonWriter)jsonWriter, value);
            return jsonWriter.get();
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
    }

    public abstract T read(JsonReader var1) throws IOException;

    public final T fromJson(Reader in) throws IOException {
        JsonReader reader = new JsonReader((Reader)in);
        return (T)this.read((JsonReader)reader);
    }

    public final T fromJson(String json) throws IOException {
        return (T)this.fromJson((Reader)new StringReader((String)json));
    }

    public final T fromJsonTree(JsonElement jsonTree) {
        try {
            JsonTreeReader jsonReader = new JsonTreeReader((JsonElement)jsonTree);
            return (T)this.read((JsonReader)jsonReader);
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
    }
}

