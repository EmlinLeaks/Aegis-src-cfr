/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.util.ISO8601Utils;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

final class DefaultDateTypeAdapter
implements JsonSerializer<java.util.Date>,
JsonDeserializer<java.util.Date> {
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;

    DefaultDateTypeAdapter() {
        this((DateFormat)DateFormat.getDateTimeInstance((int)2, (int)2, (Locale)Locale.US), (DateFormat)DateFormat.getDateTimeInstance((int)2, (int)2));
    }

    DefaultDateTypeAdapter(String datePattern) {
        this((DateFormat)new SimpleDateFormat((String)datePattern, (Locale)Locale.US), (DateFormat)new SimpleDateFormat((String)datePattern));
    }

    DefaultDateTypeAdapter(int style) {
        this((DateFormat)DateFormat.getDateInstance((int)style, (Locale)Locale.US), (DateFormat)DateFormat.getDateInstance((int)style));
    }

    public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
        this((DateFormat)DateFormat.getDateTimeInstance((int)dateStyle, (int)timeStyle, (Locale)Locale.US), (DateFormat)DateFormat.getDateTimeInstance((int)dateStyle, (int)timeStyle));
    }

    DefaultDateTypeAdapter(DateFormat enUsFormat, DateFormat localFormat) {
        this.enUsFormat = enUsFormat;
        this.localFormat = localFormat;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public JsonElement serialize(java.util.Date src, Type typeOfSrc, JsonSerializationContext context) {
        DateFormat dateFormat = this.localFormat;
        // MONITORENTER : dateFormat
        String dateFormatAsString = this.enUsFormat.format((java.util.Date)src);
        // MONITOREXIT : dateFormat
        return new JsonPrimitive((String)dateFormatAsString);
    }

    @Override
    public java.util.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException((String)"The date should be a string value");
        }
        java.util.Date date = this.deserializeToDate((JsonElement)json);
        if (typeOfT == java.util.Date.class) {
            return date;
        }
        if (typeOfT == Timestamp.class) {
            return new Timestamp((long)date.getTime());
        }
        if (typeOfT != Date.class) throw new IllegalArgumentException((String)(this.getClass() + " cannot deserialize to " + typeOfT));
        return new Date((long)date.getTime());
    }

    private java.util.Date deserializeToDate(JsonElement json) {
        DateFormat dateFormat = this.localFormat;
        // MONITORENTER : dateFormat
        try {
            // MONITOREXIT : dateFormat
            return this.localFormat.parse((String)json.getAsString());
        }
        catch (ParseException parseException) {
            try {
                // MONITOREXIT : dateFormat
                return this.enUsFormat.parse((String)json.getAsString());
            }
            catch (ParseException parseException2) {
                try {
                    // MONITOREXIT : dateFormat
                    return ISO8601Utils.parse((String)json.getAsString(), (ParsePosition)new ParsePosition((int)0));
                }
                catch (ParseException e) {
                    throw new JsonSyntaxException((String)json.getAsString(), (Throwable)e);
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((String)DefaultDateTypeAdapter.class.getSimpleName());
        sb.append((char)'(').append((String)this.localFormat.getClass().getSimpleName()).append((char)')');
        return sb.toString();
    }
}

