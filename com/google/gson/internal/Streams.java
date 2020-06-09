/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class Streams {
    private Streams() {
        throw new UnsupportedOperationException();
    }

    public static JsonElement parse(JsonReader reader) throws JsonParseException {
        boolean isEmpty = true;
        try {
            reader.peek();
            return TypeAdapters.JSON_ELEMENT.read((JsonReader)reader);
        }
        catch (EOFException e) {
            if (!isEmpty) throw new JsonSyntaxException((Throwable)e);
            return JsonNull.INSTANCE;
        }
        catch (MalformedJsonException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
        catch (NumberFormatException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
    }

    public static void write(JsonElement element, JsonWriter writer) throws IOException {
        TypeAdapters.JSON_ELEMENT.write((JsonWriter)writer, (JsonElement)element);
    }

    public static Writer writerForAppendable(Appendable appendable) {
        Writer writer;
        if (appendable instanceof Writer) {
            writer = (Writer)appendable;
            return writer;
        }
        writer = new AppendableWriter((Appendable)appendable);
        return writer;
    }
}

