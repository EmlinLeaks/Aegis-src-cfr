/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {
    public JsonElement parse(String json) throws JsonSyntaxException {
        return this.parse((Reader)new StringReader((String)json));
    }

    public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
        try {
            JsonReader jsonReader = new JsonReader((Reader)json);
            JsonElement element = this.parse((JsonReader)jsonReader);
            if (element.isJsonNull()) return element;
            if (jsonReader.peek() == JsonToken.END_DOCUMENT) return element;
            throw new JsonSyntaxException((String)"Did not consume the entire document.");
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

    public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
        boolean lenient = json.isLenient();
        json.setLenient((boolean)true);
        try {
            JsonElement jsonElement = Streams.parse((JsonReader)json);
            return jsonElement;
        }
        catch (StackOverflowError e) {
            throw new JsonParseException((String)("Failed parsing JSON source: " + json + " to Json"), (Throwable)e);
        }
        catch (OutOfMemoryError e) {
            throw new JsonParseException((String)("Failed parsing JSON source: " + json + " to Json"), (Throwable)e);
        }
        finally {
            json.setLenient((boolean)lenient);
        }
    }
}

