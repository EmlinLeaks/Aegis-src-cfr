/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class JsonObject
extends JsonElement {
    private final LinkedTreeMap<String, JsonElement> members = new LinkedTreeMap<K, V>();

    @Override
    JsonObject deepCopy() {
        JsonObject result = new JsonObject();
        Iterator<Map.Entry<String, JsonElement>> iterator = this.members.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            result.add((String)entry.getKey(), (JsonElement)entry.getValue().deepCopy());
        }
        return result;
    }

    public void add(String property, JsonElement value) {
        if (value == null) {
            value = JsonNull.INSTANCE;
        }
        this.members.put((String)property, (JsonElement)value);
    }

    public JsonElement remove(String property) {
        return this.members.remove((Object)property);
    }

    public void addProperty(String property, String value) {
        this.add((String)property, (JsonElement)this.createJsonElement((Object)value));
    }

    public void addProperty(String property, Number value) {
        this.add((String)property, (JsonElement)this.createJsonElement((Object)value));
    }

    public void addProperty(String property, Boolean value) {
        this.add((String)property, (JsonElement)this.createJsonElement((Object)value));
    }

    public void addProperty(String property, Character value) {
        this.add((String)property, (JsonElement)this.createJsonElement((Object)value));
    }

    private JsonElement createJsonElement(Object value) {
        JsonElement jsonElement;
        if (value == null) {
            jsonElement = JsonNull.INSTANCE;
            return jsonElement;
        }
        jsonElement = new JsonPrimitive((Object)value);
        return jsonElement;
    }

    public Set<Map.Entry<String, JsonElement>> entrySet() {
        return this.members.entrySet();
    }

    public int size() {
        return this.members.size();
    }

    public boolean has(String memberName) {
        return this.members.containsKey((Object)memberName);
    }

    public JsonElement get(String memberName) {
        return this.members.get((Object)memberName);
    }

    public JsonPrimitive getAsJsonPrimitive(String memberName) {
        return (JsonPrimitive)this.members.get((Object)memberName);
    }

    public JsonArray getAsJsonArray(String memberName) {
        return (JsonArray)this.members.get((Object)memberName);
    }

    public JsonObject getAsJsonObject(String memberName) {
        return (JsonObject)this.members.get((Object)memberName);
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof JsonObject)) return false;
        if (!((JsonObject)o).members.equals(this.members)) return false;
        return true;
    }

    public int hashCode() {
        return this.members.hashCode();
    }
}

