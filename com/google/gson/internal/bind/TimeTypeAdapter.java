/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TimeTypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TimeTypeAdapter
extends TypeAdapter<Time> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        public <T> TypeAdapter<T> create(com.google.gson.Gson gson, com.google.gson.reflect.TypeToken<T> typeToken) {
            if (typeToken.getRawType() != Time.class) return null;
            TimeTypeAdapter timeTypeAdapter = new TimeTypeAdapter();
            return timeTypeAdapter;
        }
    };
    private final DateFormat format = new SimpleDateFormat((String)"hh:mm:ss a");

    @Override
    public synchronized Time read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            Date date = this.format.parse((String)in.nextString());
            return new Time((long)date.getTime());
        }
        catch (ParseException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
    }

    @Override
    public synchronized void write(JsonWriter out, Time value) throws IOException {
        out.value(value == null ? null : this.format.format((Date)value));
    }
}

