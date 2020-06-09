/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SqlDateTypeAdapter
extends TypeAdapter<java.sql.Date> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        public <T> TypeAdapter<T> create(com.google.gson.Gson gson, com.google.gson.reflect.TypeToken<T> typeToken) {
            if (typeToken.getRawType() != java.sql.Date.class) return null;
            SqlDateTypeAdapter sqlDateTypeAdapter = new SqlDateTypeAdapter();
            return sqlDateTypeAdapter;
        }
    };
    private final DateFormat format = new SimpleDateFormat((String)"MMM d, yyyy");

    @Override
    public synchronized java.sql.Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            long utilDate = this.format.parse((String)in.nextString()).getTime();
            return new java.sql.Date((long)utilDate);
        }
        catch (ParseException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
    }

    @Override
    public synchronized void write(JsonWriter out, java.sql.Date value) throws IOException {
        out.value(value == null ? null : this.format.format((Date)value));
    }
}

