/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
    public static final TypeAdapter<Class> CLASS = new TypeAdapter<Class>(){

        public void write(com.google.gson.stream.JsonWriter out, Class value) throws java.io.IOException {
            if (value != null) throw new UnsupportedOperationException((String)("Attempted to serialize java.lang.Class: " + value.getName() + ". Forgot to register a type adapter?"));
            out.nullValue();
        }

        public Class read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) throw new UnsupportedOperationException((String)"Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
            in.nextNull();
            return null;
        }
    };
    public static final TypeAdapterFactory CLASS_FACTORY = TypeAdapters.newFactory(Class.class, CLASS);
    public static final TypeAdapter<BitSet> BIT_SET = new TypeAdapter<BitSet>(){

        /*
         * Unable to fully structure code
         * Enabled unnecessary exception pruning
         */
        public BitSet read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            bitset = new BitSet();
            in.beginArray();
            i = 0;
            tokenType = in.peek();
            block7 : do {
                if (tokenType == com.google.gson.stream.JsonToken.END_ARRAY) {
                    in.endArray();
                    return bitset;
                }
                switch (com.google.gson.internal.bind.TypeAdapters$36.$SwitchMap$com$google$gson$stream$JsonToken[tokenType.ordinal()]) {
                    case 1: {
                        set = in.nextInt() != 0;
                        ** break;
                    }
                    case 2: {
                        set = in.nextBoolean();
                        ** break;
                    }
                    case 3: {
                        stringValue = in.nextString();
                        try {
                            set = Integer.parseInt((String)stringValue) != 0;
                            ** break;
                        }
                        catch (java.lang.NumberFormatException e) {
                            throw new com.google.gson.JsonSyntaxException((String)("Error: Expecting: bitset number value (1, 0), Found: " + stringValue));
                        }
lbl26: // 3 sources:
                        if (set) {
                            bitset.set((int)i);
                        }
                        ++i;
                        tokenType = in.peek();
                        continue block7;
                    }
                }
                break;
            } while (true);
            throw new com.google.gson.JsonSyntaxException((String)("Invalid bitset value type: " + (Object)tokenType));
        }

        public void write(com.google.gson.stream.JsonWriter out, BitSet src) throws java.io.IOException {
            if (src == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            int i = 0;
            do {
                if (i >= src.length()) {
                    out.endArray();
                    return;
                }
                int value = src.get((int)i) ? 1 : 0;
                out.value((long)((long)value));
                ++i;
            } while (true);
        }
    };
    public static final TypeAdapterFactory BIT_SET_FACTORY = TypeAdapters.newFactory(BitSet.class, BIT_SET);
    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>(){

        public Boolean read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (in.peek() != com.google.gson.stream.JsonToken.STRING) return Boolean.valueOf((boolean)in.nextBoolean());
            return Boolean.valueOf((boolean)Boolean.parseBoolean((String)in.nextString()));
        }

        public void write(com.google.gson.stream.JsonWriter out, Boolean value) throws java.io.IOException {
            out.value((Boolean)value);
        }
    };
    public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>(){

        public Boolean read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) return Boolean.valueOf((String)in.nextString());
            in.nextNull();
            return null;
        }

        public void write(com.google.gson.stream.JsonWriter out, Boolean value) throws java.io.IOException {
            out.value((String)(value == null ? "null" : value.toString()));
        }
    };
    public static final TypeAdapterFactory BOOLEAN_FACTORY = TypeAdapters.newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
    public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>(){

        public Number read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                int intValue = in.nextInt();
                return Byte.valueOf((byte)((byte)intValue));
            }
            catch (java.lang.NumberFormatException e) {
                throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, Number value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapterFactory BYTE_FACTORY = TypeAdapters.newFactory(Byte.TYPE, Byte.class, BYTE);
    public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>(){

        public Number read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Short.valueOf((short)((short)in.nextInt()));
            }
            catch (java.lang.NumberFormatException e) {
                throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, Number value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapterFactory SHORT_FACTORY = TypeAdapters.newFactory(Short.TYPE, Short.class, SHORT);
    public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>(){

        public Number read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Integer.valueOf((int)in.nextInt());
            }
            catch (java.lang.NumberFormatException e) {
                throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, Number value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapterFactory INTEGER_FACTORY = TypeAdapters.newFactory(Integer.TYPE, Integer.class, INTEGER);
    public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER = new TypeAdapter<AtomicInteger>(){

        public AtomicInteger read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            try {
                return new AtomicInteger((int)in.nextInt());
            }
            catch (java.lang.NumberFormatException e) {
                throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, AtomicInteger value) throws java.io.IOException {
            out.value((long)((long)value.get()));
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY = TypeAdapters.newFactory(AtomicInteger.class, ATOMIC_INTEGER);
    public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN = new TypeAdapter<AtomicBoolean>(){

        public AtomicBoolean read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            return new AtomicBoolean((boolean)in.nextBoolean());
        }

        public void write(com.google.gson.stream.JsonWriter out, AtomicBoolean value) throws java.io.IOException {
            out.value((boolean)value.get());
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY = TypeAdapters.newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
    public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = new TypeAdapter<AtomicIntegerArray>(){

        public AtomicIntegerArray read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            java.util.ArrayList<Integer> list = new java.util.ArrayList<Integer>();
            in.beginArray();
            while (in.hasNext()) {
                try {
                    int integer = in.nextInt();
                    list.add(Integer.valueOf((int)integer));
                }
                catch (java.lang.NumberFormatException e) {
                    throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
                }
            }
            in.endArray();
            int length = list.size();
            AtomicIntegerArray array = new AtomicIntegerArray((int)length);
            int i = 0;
            while (i < length) {
                array.set((int)i, (int)((Integer)list.get((int)i)).intValue());
                ++i;
            }
            return array;
        }

        public void write(com.google.gson.stream.JsonWriter out, AtomicIntegerArray value) throws java.io.IOException {
            out.beginArray();
            int i = 0;
            int length = value.length();
            do {
                if (i >= length) {
                    out.endArray();
                    return;
                }
                out.value((long)((long)value.get((int)i)));
                ++i;
            } while (true);
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY = TypeAdapters.newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
    public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>(){

        public Number read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return java.lang.Long.valueOf((long)in.nextLong());
            }
            catch (java.lang.NumberFormatException e) {
                throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, Number value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>(){

        public Number read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) return java.lang.Float.valueOf((float)((float)in.nextDouble()));
            in.nextNull();
            return null;
        }

        public void write(com.google.gson.stream.JsonWriter out, Number value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>(){

        public Number read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) return java.lang.Double.valueOf((double)in.nextDouble());
            in.nextNull();
            return null;
        }

        public void write(com.google.gson.stream.JsonWriter out, Number value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapter<Number> NUMBER = new TypeAdapter<Number>(){

        public Number read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            com.google.gson.stream.JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL: {
                    in.nextNull();
                    return null;
                }
                case NUMBER: {
                    return new com.google.gson.internal.LazilyParsedNumber((String)in.nextString());
                }
            }
            throw new com.google.gson.JsonSyntaxException((String)("Expecting number, got: " + (Object)((Object)jsonToken)));
        }

        public void write(com.google.gson.stream.JsonWriter out, Number value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapterFactory NUMBER_FACTORY = TypeAdapters.newFactory(Number.class, NUMBER);
    public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>(){

        public Character read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String str = in.nextString();
            if (str.length() == 1) return Character.valueOf((char)str.charAt((int)0));
            throw new com.google.gson.JsonSyntaxException((String)("Expecting character, got: " + str));
        }

        public void write(com.google.gson.stream.JsonWriter out, Character value) throws java.io.IOException {
            out.value((String)(value == null ? null : String.valueOf((Object)value)));
        }
    };
    public static final TypeAdapterFactory CHARACTER_FACTORY = TypeAdapters.newFactory(Character.TYPE, Character.class, CHARACTER);
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>(){

        public String read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            com.google.gson.stream.JsonToken peek = in.peek();
            if (peek == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (peek != com.google.gson.stream.JsonToken.BOOLEAN) return in.nextString();
            return Boolean.toString((boolean)in.nextBoolean());
        }

        public void write(com.google.gson.stream.JsonWriter out, String value) throws java.io.IOException {
            out.value((String)value);
        }
    };
    public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>(){

        public BigDecimal read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new BigDecimal((String)in.nextString());
            }
            catch (java.lang.NumberFormatException e) {
                throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, BigDecimal value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>(){

        public BigInteger read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new BigInteger((String)in.nextString());
            }
            catch (java.lang.NumberFormatException e) {
                throw new com.google.gson.JsonSyntaxException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, BigInteger value) throws java.io.IOException {
            out.value((Number)value);
        }
    };
    public static final TypeAdapterFactory STRING_FACTORY = TypeAdapters.newFactory(String.class, STRING);
    public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>(){

        public StringBuilder read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) return new StringBuilder((String)in.nextString());
            in.nextNull();
            return null;
        }

        public void write(com.google.gson.stream.JsonWriter out, StringBuilder value) throws java.io.IOException {
            out.value((String)(value == null ? null : value.toString()));
        }
    };
    public static final TypeAdapterFactory STRING_BUILDER_FACTORY = TypeAdapters.newFactory(StringBuilder.class, STRING_BUILDER);
    public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>(){

        public StringBuffer read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) return new StringBuffer((String)in.nextString());
            in.nextNull();
            return null;
        }

        public void write(com.google.gson.stream.JsonWriter out, StringBuffer value) throws java.io.IOException {
            out.value((String)(value == null ? null : value.toString()));
        }
    };
    public static final TypeAdapterFactory STRING_BUFFER_FACTORY = TypeAdapters.newFactory(StringBuffer.class, STRING_BUFFER);
    public static final TypeAdapter<URL> URL = new TypeAdapter<URL>(){

        public URL read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String nextString = in.nextString();
            if ("null".equals((Object)nextString)) {
                return null;
            }
            URL uRL = new URL((String)nextString);
            return uRL;
        }

        public void write(com.google.gson.stream.JsonWriter out, URL value) throws java.io.IOException {
            out.value((String)(value == null ? null : value.toExternalForm()));
        }
    };
    public static final TypeAdapterFactory URL_FACTORY = TypeAdapters.newFactory(URL.class, URL);
    public static final TypeAdapter<URI> URI = new TypeAdapter<URI>(){

        public URI read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                String nextString = in.nextString();
                if ("null".equals((Object)nextString)) {
                    return null;
                }
                URI uRI = new URI((String)nextString);
                return uRI;
            }
            catch (java.net.URISyntaxException e) {
                throw new com.google.gson.JsonIOException((java.lang.Throwable)e);
            }
        }

        public void write(com.google.gson.stream.JsonWriter out, URI value) throws java.io.IOException {
            out.value((String)(value == null ? null : value.toASCIIString()));
        }
    };
    public static final TypeAdapterFactory URI_FACTORY = TypeAdapters.newFactory(URI.class, URI);
    public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>(){

        public InetAddress read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) return InetAddress.getByName((String)in.nextString());
            in.nextNull();
            return null;
        }

        public void write(com.google.gson.stream.JsonWriter out, InetAddress value) throws java.io.IOException {
            out.value((String)(value == null ? null : value.getHostAddress()));
        }
    };
    public static final TypeAdapterFactory INET_ADDRESS_FACTORY = TypeAdapters.newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
    public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>(){

        public UUID read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() != com.google.gson.stream.JsonToken.NULL) return UUID.fromString((String)in.nextString());
            in.nextNull();
            return null;
        }

        public void write(com.google.gson.stream.JsonWriter out, UUID value) throws java.io.IOException {
            out.value((String)(value == null ? null : value.toString()));
        }
    };
    public static final TypeAdapterFactory UUID_FACTORY = TypeAdapters.newFactory(UUID.class, UUID);
    public static final TypeAdapter<Currency> CURRENCY = new TypeAdapter<Currency>(){

        public Currency read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            return Currency.getInstance((String)in.nextString());
        }

        public void write(com.google.gson.stream.JsonWriter out, Currency value) throws java.io.IOException {
            out.value((String)value.getCurrencyCode());
        }
    }.nullSafe();
    public static final TypeAdapterFactory CURRENCY_FACTORY = TypeAdapters.newFactory(Currency.class, CURRENCY);
    public static final TypeAdapterFactory TIMESTAMP_FACTORY = new TypeAdapterFactory(){

        public <T> TypeAdapter<T> create(com.google.gson.Gson gson, TypeToken<T> typeToken) {
            if (typeToken.getRawType() != java.sql.Timestamp.class) {
                return null;
            }
            TypeAdapter<java.util.Date> dateTypeAdapter = gson.getAdapter(java.util.Date.class);
            return new TypeAdapter<java.sql.Timestamp>(this, dateTypeAdapter){
                final /* synthetic */ TypeAdapter val$dateTypeAdapter;
                final /* synthetic */ 26 this$0;
                {
                    this.this$0 = this$0;
                    this.val$dateTypeAdapter = typeAdapter;
                }

                public java.sql.Timestamp read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                    java.util.Date date = (java.util.Date)this.val$dateTypeAdapter.read((com.google.gson.stream.JsonReader)in);
                    if (date == null) return null;
                    java.sql.Timestamp timestamp = new java.sql.Timestamp((long)date.getTime());
                    return timestamp;
                }

                public void write(com.google.gson.stream.JsonWriter out, java.sql.Timestamp value) throws java.io.IOException {
                    this.val$dateTypeAdapter.write((com.google.gson.stream.JsonWriter)out, value);
                }
            };
        }
    };
    public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>(){
        private static final String YEAR = "year";
        private static final String MONTH = "month";
        private static final String DAY_OF_MONTH = "dayOfMonth";
        private static final String HOUR_OF_DAY = "hourOfDay";
        private static final String MINUTE = "minute";
        private static final String SECOND = "second";

        public Calendar read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            in.beginObject();
            int year = 0;
            int month = 0;
            int dayOfMonth = 0;
            int hourOfDay = 0;
            int minute = 0;
            int second = 0;
            do {
                if (in.peek() == com.google.gson.stream.JsonToken.END_OBJECT) {
                    in.endObject();
                    return new GregorianCalendar((int)year, (int)month, (int)dayOfMonth, (int)hourOfDay, (int)minute, (int)second);
                }
                String name = in.nextName();
                int value = in.nextInt();
                if ("year".equals((Object)name)) {
                    year = value;
                    continue;
                }
                if ("month".equals((Object)name)) {
                    month = value;
                    continue;
                }
                if ("dayOfMonth".equals((Object)name)) {
                    dayOfMonth = value;
                    continue;
                }
                if ("hourOfDay".equals((Object)name)) {
                    hourOfDay = value;
                    continue;
                }
                if ("minute".equals((Object)name)) {
                    minute = value;
                    continue;
                }
                if (!"second".equals((Object)name)) continue;
                second = value;
            } while (true);
        }

        public void write(com.google.gson.stream.JsonWriter out, Calendar value) throws java.io.IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.name((String)"year");
            out.value((long)((long)value.get((int)1)));
            out.name((String)"month");
            out.value((long)((long)value.get((int)2)));
            out.name((String)"dayOfMonth");
            out.value((long)((long)value.get((int)5)));
            out.name((String)"hourOfDay");
            out.value((long)((long)value.get((int)11)));
            out.name((String)"minute");
            out.value((long)((long)value.get((int)12)));
            out.name((String)"second");
            out.value((long)((long)value.get((int)13)));
            out.endObject();
        }
    };
    public static final TypeAdapterFactory CALENDAR_FACTORY = TypeAdapters.newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
    public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>(){

        public Locale read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String locale = in.nextString();
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer((String)locale, (String)"_");
            String language = null;
            String country = null;
            String variant = null;
            if (tokenizer.hasMoreElements()) {
                language = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreElements()) {
                country = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreElements()) {
                variant = tokenizer.nextToken();
            }
            if (country == null && variant == null) {
                return new Locale((String)language);
            }
            if (variant != null) return new Locale((String)language, (String)country, (String)variant);
            return new Locale((String)language, (String)country);
        }

        public void write(com.google.gson.stream.JsonWriter out, Locale value) throws java.io.IOException {
            out.value((String)(value == null ? null : value.toString()));
        }
    };
    public static final TypeAdapterFactory LOCALE_FACTORY = TypeAdapters.newFactory(Locale.class, LOCALE);
    public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter<JsonElement>(){

        public JsonElement read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            switch (in.peek()) {
                case STRING: {
                    return new com.google.gson.JsonPrimitive((String)in.nextString());
                }
                case NUMBER: {
                    String number = in.nextString();
                    return new com.google.gson.JsonPrimitive((Number)new com.google.gson.internal.LazilyParsedNumber((String)number));
                }
                case BOOLEAN: {
                    return new com.google.gson.JsonPrimitive((Boolean)Boolean.valueOf((boolean)in.nextBoolean()));
                }
                case NULL: {
                    in.nextNull();
                    return com.google.gson.JsonNull.INSTANCE;
                }
                case BEGIN_ARRAY: {
                    com.google.gson.JsonArray array = new com.google.gson.JsonArray();
                    in.beginArray();
                    do {
                        if (!in.hasNext()) {
                            in.endArray();
                            return array;
                        }
                        array.add((JsonElement)this.read((com.google.gson.stream.JsonReader)in));
                    } while (true);
                }
                case BEGIN_OBJECT: {
                    com.google.gson.JsonObject object = new com.google.gson.JsonObject();
                    in.beginObject();
                    do {
                        if (!in.hasNext()) {
                            in.endObject();
                            return object;
                        }
                        object.add((String)in.nextName(), (JsonElement)this.read((com.google.gson.stream.JsonReader)in));
                    } while (true);
                }
            }
            throw new java.lang.IllegalArgumentException();
        }

        public void write(com.google.gson.stream.JsonWriter out, JsonElement value) throws java.io.IOException {
            if (value == null || value.isJsonNull()) {
                out.nullValue();
                return;
            }
            if (value.isJsonPrimitive()) {
                com.google.gson.JsonPrimitive primitive = value.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    out.value((Number)primitive.getAsNumber());
                    return;
                }
                if (primitive.isBoolean()) {
                    out.value((boolean)primitive.getAsBoolean());
                    return;
                }
                out.value((String)primitive.getAsString());
                return;
            }
            if (value.isJsonArray()) {
                out.beginArray();
                java.util.Iterator<JsonElement> primitive = value.getAsJsonArray().iterator();
                do {
                    if (!primitive.hasNext()) {
                        out.endArray();
                        return;
                    }
                    JsonElement e = primitive.next();
                    this.write((com.google.gson.stream.JsonWriter)out, (JsonElement)e);
                } while (true);
            }
            if (!value.isJsonObject()) throw new java.lang.IllegalArgumentException((String)("Couldn't write " + value.getClass()));
            out.beginObject();
            java.util.Iterator<java.util.Map$Entry<String, JsonElement>> primitive = value.getAsJsonObject().entrySet().iterator();
            do {
                if (!primitive.hasNext()) {
                    out.endObject();
                    return;
                }
                java.util.Map$Entry<String, JsonElement> e = primitive.next();
                out.name((String)e.getKey());
                this.write((com.google.gson.stream.JsonWriter)out, (JsonElement)e.getValue());
            } while (true);
        }
    };
    public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = TypeAdapters.newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
    public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory(){

        public <T> TypeAdapter<T> create(com.google.gson.Gson gson, TypeToken<T> typeToken) {
            Class<T> rawType = typeToken.getRawType();
            if (!java.lang.Enum.class.isAssignableFrom(rawType)) return null;
            if (rawType == java.lang.Enum.class) {
                return null;
            }
            if (rawType.isEnum()) return new com.google.gson.internal.bind.TypeAdapters$EnumTypeAdapter<T>(rawType);
            rawType = rawType.getSuperclass();
            return new com.google.gson.internal.bind.TypeAdapters$EnumTypeAdapter<T>(rawType);
        }
    };

    private TypeAdapters() {
        throw new UnsupportedOperationException();
    }

    public static <TT> TypeAdapterFactory newFactory(TypeToken<TT> type, TypeAdapter<TT> typeAdapter) {
        return new TypeAdapterFactory(type, typeAdapter){
            final /* synthetic */ TypeToken val$type;
            final /* synthetic */ TypeAdapter val$typeAdapter;
            {
                this.val$type = typeToken;
                this.val$typeAdapter = typeAdapter;
            }

            public <T> TypeAdapter<T> create(com.google.gson.Gson gson, TypeToken<T> typeToken) {
                if (!typeToken.equals((Object)this.val$type)) return null;
                TypeAdapter typeAdapter = this.val$typeAdapter;
                return typeAdapter;
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(Class<TT> type, TypeAdapter<TT> typeAdapter) {
        return new TypeAdapterFactory(type, typeAdapter){
            final /* synthetic */ Class val$type;
            final /* synthetic */ TypeAdapter val$typeAdapter;
            {
                this.val$type = class_;
                this.val$typeAdapter = typeAdapter;
            }

            public <T> TypeAdapter<T> create(com.google.gson.Gson gson, TypeToken<T> typeToken) {
                if (typeToken.getRawType() != this.val$type) return null;
                TypeAdapter typeAdapter = this.val$typeAdapter;
                return typeAdapter;
            }

            public String toString() {
                return "Factory[type=" + this.val$type.getName() + ",adapter=" + this.val$typeAdapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(Class<TT> unboxed, Class<TT> boxed, TypeAdapter<? super TT> typeAdapter) {
        return new TypeAdapterFactory(unboxed, boxed, typeAdapter){
            final /* synthetic */ Class val$unboxed;
            final /* synthetic */ Class val$boxed;
            final /* synthetic */ TypeAdapter val$typeAdapter;
            {
                this.val$unboxed = class_;
                this.val$boxed = class_2;
                this.val$typeAdapter = typeAdapter;
            }

            public <T> TypeAdapter<T> create(com.google.gson.Gson gson, TypeToken<T> typeToken) {
                Class<T> rawType = typeToken.getRawType();
                if (rawType != this.val$unboxed && rawType != this.val$boxed) {
                    return null;
                }
                TypeAdapter typeAdapter = this.val$typeAdapter;
                return typeAdapter;
            }

            public String toString() {
                return "Factory[type=" + this.val$boxed.getName() + "+" + this.val$unboxed.getName() + ",adapter=" + this.val$typeAdapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(Class<TT> base, Class<? extends TT> sub, TypeAdapter<? super TT> typeAdapter) {
        return new TypeAdapterFactory(base, sub, typeAdapter){
            final /* synthetic */ Class val$base;
            final /* synthetic */ Class val$sub;
            final /* synthetic */ TypeAdapter val$typeAdapter;
            {
                this.val$base = class_;
                this.val$sub = class_2;
                this.val$typeAdapter = typeAdapter;
            }

            public <T> TypeAdapter<T> create(com.google.gson.Gson gson, TypeToken<T> typeToken) {
                Class<T> rawType = typeToken.getRawType();
                if (rawType != this.val$base && rawType != this.val$sub) {
                    return null;
                }
                TypeAdapter typeAdapter = this.val$typeAdapter;
                return typeAdapter;
            }

            public String toString() {
                return "Factory[type=" + this.val$base.getName() + "+" + this.val$sub.getName() + ",adapter=" + this.val$typeAdapter + "]";
            }
        };
    }

    public static <T1> TypeAdapterFactory newTypeHierarchyFactory(Class<T1> clazz, TypeAdapter<T1> typeAdapter) {
        return new TypeAdapterFactory(clazz, typeAdapter){
            final /* synthetic */ Class val$clazz;
            final /* synthetic */ TypeAdapter val$typeAdapter;
            {
                this.val$clazz = class_;
                this.val$typeAdapter = typeAdapter;
            }

            public <T2> TypeAdapter<T2> create(com.google.gson.Gson gson, TypeToken<T2> typeToken) {
                Class<T2> requestedType = typeToken.getRawType();
                if (this.val$clazz.isAssignableFrom(requestedType)) return new TypeAdapter<T1>(this, requestedType){
                    final /* synthetic */ Class val$requestedType;
                    final /* synthetic */ 35 this$0;
                    {
                        this.this$0 = this$0;
                        this.val$requestedType = class_;
                    }

                    public void write(com.google.gson.stream.JsonWriter out, T1 value) throws java.io.IOException {
                        this.this$0.val$typeAdapter.write((com.google.gson.stream.JsonWriter)out, value);
                    }

                    public T1 read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                        T result = this.this$0.val$typeAdapter.read((com.google.gson.stream.JsonReader)in);
                        if (result == null) return (T1)result;
                        if (this.val$requestedType.isInstance(result)) return (T1)result;
                        throw new com.google.gson.JsonSyntaxException((String)("Expected a " + this.val$requestedType.getName() + " but was " + result.getClass().getName()));
                    }
                };
                return null;
            }

            public String toString() {
                return "Factory[typeHierarchy=" + this.val$clazz.getName() + ",adapter=" + this.val$typeAdapter + "]";
            }
        };
    }
}

