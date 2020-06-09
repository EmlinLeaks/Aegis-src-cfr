/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class JsonTreeWriter
extends JsonWriter {
    private static final Writer UNWRITABLE_WRITER = new Writer(){

        public void write(char[] buffer, int offset, int counter) {
            throw new java.lang.AssertionError();
        }

        public void flush() throws IOException {
            throw new java.lang.AssertionError();
        }

        public void close() throws IOException {
            throw new java.lang.AssertionError();
        }
    };
    private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive((String)"closed");
    private final List<JsonElement> stack = new ArrayList<JsonElement>();
    private String pendingName;
    private JsonElement product = JsonNull.INSTANCE;

    public JsonTreeWriter() {
        super((Writer)UNWRITABLE_WRITER);
    }

    public JsonElement get() {
        if (this.stack.isEmpty()) return this.product;
        throw new IllegalStateException((String)("Expected one JSON element but was " + this.stack));
    }

    private JsonElement peek() {
        return this.stack.get((int)(this.stack.size() - 1));
    }

    private void put(JsonElement value) {
        if (this.pendingName != null) {
            if (!value.isJsonNull() || this.getSerializeNulls()) {
                JsonObject object = (JsonObject)this.peek();
                object.add((String)this.pendingName, (JsonElement)value);
            }
            this.pendingName = null;
            return;
        }
        if (this.stack.isEmpty()) {
            this.product = value;
            return;
        }
        JsonElement element = this.peek();
        if (!(element instanceof JsonArray)) throw new IllegalStateException();
        ((JsonArray)element).add((JsonElement)value);
    }

    @Override
    public JsonWriter beginArray() throws IOException {
        JsonArray array = new JsonArray();
        this.put((JsonElement)array);
        this.stack.add((JsonElement)array);
        return this;
    }

    @Override
    public JsonWriter endArray() throws IOException {
        if (this.stack.isEmpty()) throw new IllegalStateException();
        if (this.pendingName != null) {
            throw new IllegalStateException();
        }
        JsonElement element = this.peek();
        if (!(element instanceof JsonArray)) throw new IllegalStateException();
        this.stack.remove((int)(this.stack.size() - 1));
        return this;
    }

    @Override
    public JsonWriter beginObject() throws IOException {
        JsonObject object = new JsonObject();
        this.put((JsonElement)object);
        this.stack.add((JsonElement)object);
        return this;
    }

    @Override
    public JsonWriter endObject() throws IOException {
        if (this.stack.isEmpty()) throw new IllegalStateException();
        if (this.pendingName != null) {
            throw new IllegalStateException();
        }
        JsonElement element = this.peek();
        if (!(element instanceof JsonObject)) throw new IllegalStateException();
        this.stack.remove((int)(this.stack.size() - 1));
        return this;
    }

    @Override
    public JsonWriter name(String name) throws IOException {
        if (this.stack.isEmpty()) throw new IllegalStateException();
        if (this.pendingName != null) {
            throw new IllegalStateException();
        }
        JsonElement element = this.peek();
        if (!(element instanceof JsonObject)) throw new IllegalStateException();
        this.pendingName = name;
        return this;
    }

    @Override
    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.put((JsonElement)new JsonPrimitive((String)value));
        return this;
    }

    @Override
    public JsonWriter nullValue() throws IOException {
        this.put((JsonElement)JsonNull.INSTANCE);
        return this;
    }

    @Override
    public JsonWriter value(boolean value) throws IOException {
        this.put((JsonElement)new JsonPrimitive((Boolean)Boolean.valueOf((boolean)value)));
        return this;
    }

    @Override
    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.put((JsonElement)new JsonPrimitive((Boolean)value));
        return this;
    }

    @Override
    public JsonWriter value(double value) throws IOException {
        if (!this.isLenient()) {
            if (Double.isNaN((double)value)) throw new IllegalArgumentException((String)("JSON forbids NaN and infinities: " + value));
            if (Double.isInfinite((double)value)) {
                throw new IllegalArgumentException((String)("JSON forbids NaN and infinities: " + value));
            }
        }
        this.put((JsonElement)new JsonPrimitive((Number)Double.valueOf((double)value)));
        return this;
    }

    @Override
    public JsonWriter value(long value) throws IOException {
        this.put((JsonElement)new JsonPrimitive((Number)Long.valueOf((long)value)));
        return this;
    }

    @Override
    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        if (!this.isLenient()) {
            double d = value.doubleValue();
            if (Double.isNaN((double)d)) throw new IllegalArgumentException((String)("JSON forbids NaN and infinities: " + value));
            if (Double.isInfinite((double)d)) {
                throw new IllegalArgumentException((String)("JSON forbids NaN and infinities: " + value));
            }
        }
        this.put((JsonElement)new JsonPrimitive((Number)value));
        return this;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        if (!this.stack.isEmpty()) {
            throw new IOException((String)"Incomplete document");
        }
        this.stack.add((JsonElement)SENTINEL_CLOSED);
    }
}

