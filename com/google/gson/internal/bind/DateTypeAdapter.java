/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

public final class DateTypeAdapter
extends TypeAdapter<Date> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        public <T> TypeAdapter<T> create(com.google.gson.Gson gson, com.google.gson.reflect.TypeToken<T> typeToken) {
            if (typeToken.getRawType() != Date.class) return null;
            DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();
            return dateTypeAdapter;
        }
    };
    private final DateFormat enUsFormat = DateFormat.getDateTimeInstance((int)2, (int)2, (Locale)Locale.US);
    private final DateFormat localFormat = DateFormat.getDateTimeInstance((int)2, (int)2);

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.NULL) return this.deserializeToDate((String)in.nextString());
        in.nextNull();
        return null;
    }

    private synchronized Date deserializeToDate(String json) {
        try {
            return this.localFormat.parse((String)json);
        }
        catch (ParseException parseException) {
            try {
                return this.enUsFormat.parse((String)json);
            }
            catch (ParseException parseException2) {
                try {
                    return ISO8601Utils.parse((String)json, (ParsePosition)new ParsePosition((int)0));
                }
                catch (ParseException e) {
                    throw new JsonSyntaxException((String)json, (Throwable)e);
                }
            }
        }
    }

    @Override
    public synchronized void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString = this.enUsFormat.format((Date)value);
        out.value((String)dateFormatAsString);
    }
}

