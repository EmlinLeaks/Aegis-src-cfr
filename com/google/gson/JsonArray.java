/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class JsonArray
extends JsonElement
implements Iterable<JsonElement> {
    private final List<JsonElement> elements = new ArrayList<JsonElement>();

    @Override
    JsonArray deepCopy() {
        JsonArray result = new JsonArray();
        Iterator<JsonElement> iterator = this.elements.iterator();
        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            result.add((JsonElement)element.deepCopy());
        }
        return result;
    }

    public void add(Boolean bool) {
        this.elements.add((JsonElement)(bool == null ? JsonNull.INSTANCE : new JsonPrimitive((Boolean)bool)));
    }

    public void add(Character character) {
        this.elements.add((JsonElement)(character == null ? JsonNull.INSTANCE : new JsonPrimitive((Character)character)));
    }

    public void add(Number number) {
        this.elements.add((JsonElement)(number == null ? JsonNull.INSTANCE : new JsonPrimitive((Number)number)));
    }

    public void add(String string) {
        this.elements.add((JsonElement)(string == null ? JsonNull.INSTANCE : new JsonPrimitive((String)string)));
    }

    public void add(JsonElement element) {
        if (element == null) {
            element = JsonNull.INSTANCE;
        }
        this.elements.add((JsonElement)element);
    }

    public void addAll(JsonArray array) {
        this.elements.addAll(array.elements);
    }

    public JsonElement set(int index, JsonElement element) {
        return this.elements.set((int)index, (JsonElement)element);
    }

    public boolean remove(JsonElement element) {
        return this.elements.remove((Object)element);
    }

    public JsonElement remove(int index) {
        return this.elements.remove((int)index);
    }

    public boolean contains(JsonElement element) {
        return this.elements.contains((Object)element);
    }

    public int size() {
        return this.elements.size();
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return this.elements.iterator();
    }

    public JsonElement get(int i) {
        return this.elements.get((int)i);
    }

    @Override
    public Number getAsNumber() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsNumber();
    }

    @Override
    public String getAsString() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsString();
    }

    @Override
    public double getAsDouble() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsDouble();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsBigInteger();
    }

    @Override
    public float getAsFloat() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsFloat();
    }

    @Override
    public long getAsLong() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsLong();
    }

    @Override
    public int getAsInt() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsInt();
    }

    @Override
    public byte getAsByte() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsByte();
    }

    @Override
    public char getAsCharacter() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsCharacter();
    }

    @Override
    public short getAsShort() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsShort();
    }

    @Override
    public boolean getAsBoolean() {
        if (this.elements.size() != 1) throw new IllegalStateException();
        return this.elements.get((int)0).getAsBoolean();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof JsonArray)) return false;
        if (!((JsonArray)o).elements.equals(this.elements)) return false;
        return true;
    }

    public int hashCode() {
        return this.elements.hashCode();
    }
}

