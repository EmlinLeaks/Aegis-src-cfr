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
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JsonStreamParser
implements Iterator<JsonElement> {
    private final JsonReader parser;
    private final Object lock;

    public JsonStreamParser(String json) {
        this((Reader)new StringReader((String)json));
    }

    public JsonStreamParser(Reader reader) {
        this.parser = new JsonReader((Reader)reader);
        this.parser.setLenient((boolean)true);
        this.lock = new Object();
    }

    @Override
    public JsonElement next() throws JsonParseException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            return Streams.parse((JsonReader)this.parser);
        }
        catch (StackOverflowError e) {
            throw new JsonParseException((String)"Failed parsing JSON source to Json", (Throwable)e);
        }
        catch (OutOfMemoryError e) {
            throw new JsonParseException((String)"Failed parsing JSON source to Json", (Throwable)e);
        }
        catch (JsonParseException e) {
            RuntimeException runtimeException;
            if (e.getCause() instanceof EOFException) {
                runtimeException = new NoSuchElementException();
                throw runtimeException;
            }
            runtimeException = e;
            throw runtimeException;
        }
    }

    @Override
    public boolean hasNext() {
        Object object = this.lock;
        // MONITORENTER : object
        try {
            if (this.parser.peek() != JsonToken.END_DOCUMENT) {
                return true;
            }
            boolean bl = false;
            // MONITOREXIT : object
            return bl;
        }
        catch (MalformedJsonException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

