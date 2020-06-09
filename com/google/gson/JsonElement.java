/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class JsonElement {
    abstract JsonElement deepCopy();

    public boolean isJsonArray() {
        return this instanceof JsonArray;
    }

    public boolean isJsonObject() {
        return this instanceof JsonObject;
    }

    public boolean isJsonPrimitive() {
        return this instanceof JsonPrimitive;
    }

    public boolean isJsonNull() {
        return this instanceof JsonNull;
    }

    public JsonObject getAsJsonObject() {
        if (!this.isJsonObject()) throw new IllegalStateException((String)("Not a JSON Object: " + this));
        return (JsonObject)this;
    }

    public JsonArray getAsJsonArray() {
        if (!this.isJsonArray()) throw new IllegalStateException((String)"This is not a JSON Array.");
        return (JsonArray)this;
    }

    public JsonPrimitive getAsJsonPrimitive() {
        if (!this.isJsonPrimitive()) throw new IllegalStateException((String)"This is not a JSON Primitive.");
        return (JsonPrimitive)this;
    }

    public JsonNull getAsJsonNull() {
        if (!this.isJsonNull()) throw new IllegalStateException((String)"This is not a JSON Null.");
        return (JsonNull)this;
    }

    public boolean getAsBoolean() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    Boolean getAsBooleanWrapper() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public Number getAsNumber() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public String getAsString() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public double getAsDouble() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public float getAsFloat() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public long getAsLong() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public int getAsInt() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public byte getAsByte() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public char getAsCharacter() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public short getAsShort() {
        throw new UnsupportedOperationException((String)this.getClass().getSimpleName());
    }

    public String toString() {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter((Writer)stringWriter);
            jsonWriter.setLenient((boolean)true);
            Streams.write((JsonElement)this, (JsonWriter)jsonWriter);
            return stringWriter.toString();
        }
        catch (IOException e) {
            throw new AssertionError((Object)e);
        }
    }
}

