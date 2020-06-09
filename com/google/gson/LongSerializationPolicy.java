/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.LongSerializationPolicy;

public enum LongSerializationPolicy {
    DEFAULT{

        public JsonElement serialize(Long value) {
            return new com.google.gson.JsonPrimitive((java.lang.Number)value);
        }
    }
    ,
    STRING{

        public JsonElement serialize(Long value) {
            return new com.google.gson.JsonPrimitive((String)String.valueOf((Object)value));
        }
    };
    

    public abstract JsonElement serialize(Long var1);
}

