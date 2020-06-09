/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.google.gson.internal.bind.TimeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public final class Gson {
    static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
    static final boolean DEFAULT_LENIENT = false;
    static final boolean DEFAULT_PRETTY_PRINT = false;
    static final boolean DEFAULT_ESCAPE_HTML = true;
    static final boolean DEFAULT_SERIALIZE_NULLS = false;
    static final boolean DEFAULT_COMPLEX_MAP_KEYS = false;
    static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
    private static final TypeToken<?> NULL_KEY_SURROGATE = new TypeToken<Object>(){};
    private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
    private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls = new ThreadLocal<T>();
    private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = new ConcurrentHashMap<TypeToken<?>, TypeAdapter<?>>();
    private final List<TypeAdapterFactory> factories;
    private final ConstructorConstructor constructorConstructor;
    private final Excluder excluder;
    private final FieldNamingStrategy fieldNamingStrategy;
    private final boolean serializeNulls;
    private final boolean htmlSafe;
    private final boolean generateNonExecutableJson;
    private final boolean prettyPrinting;
    private final boolean lenient;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;

    public Gson() {
        this((Excluder)Excluder.DEFAULT, (FieldNamingStrategy)FieldNamingPolicy.IDENTITY, Collections.emptyMap(), (boolean)false, (boolean)false, (boolean)false, (boolean)true, (boolean)false, (boolean)false, (boolean)false, (LongSerializationPolicy)LongSerializationPolicy.DEFAULT, Collections.emptyList());
    }

    Gson(Excluder excluder, FieldNamingStrategy fieldNamingStrategy, Map<Type, InstanceCreator<?>> instanceCreators, boolean serializeNulls, boolean complexMapKeySerialization, boolean generateNonExecutableGson, boolean htmlSafe, boolean prettyPrinting, boolean lenient, boolean serializeSpecialFloatingPointValues, LongSerializationPolicy longSerializationPolicy, List<TypeAdapterFactory> typeAdapterFactories) {
        this.constructorConstructor = new ConstructorConstructor(instanceCreators);
        this.excluder = excluder;
        this.fieldNamingStrategy = fieldNamingStrategy;
        this.serializeNulls = serializeNulls;
        this.generateNonExecutableJson = generateNonExecutableGson;
        this.htmlSafe = htmlSafe;
        this.prettyPrinting = prettyPrinting;
        this.lenient = lenient;
        ArrayList<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>();
        factories.add(TypeAdapters.JSON_ELEMENT_FACTORY);
        factories.add(ObjectTypeAdapter.FACTORY);
        factories.add(excluder);
        factories.addAll(typeAdapterFactories);
        factories.add(TypeAdapters.STRING_FACTORY);
        factories.add(TypeAdapters.INTEGER_FACTORY);
        factories.add(TypeAdapters.BOOLEAN_FACTORY);
        factories.add(TypeAdapters.BYTE_FACTORY);
        factories.add(TypeAdapters.SHORT_FACTORY);
        TypeAdapter<Number> longAdapter = Gson.longAdapter((LongSerializationPolicy)longSerializationPolicy);
        factories.add(TypeAdapters.newFactory(Long.TYPE, Long.class, longAdapter));
        factories.add(TypeAdapters.newFactory(Double.TYPE, Double.class, this.doubleAdapter((boolean)serializeSpecialFloatingPointValues)));
        factories.add(TypeAdapters.newFactory(Float.TYPE, Float.class, this.floatAdapter((boolean)serializeSpecialFloatingPointValues)));
        factories.add(TypeAdapters.NUMBER_FACTORY);
        factories.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
        factories.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
        factories.add(TypeAdapters.newFactory(AtomicLong.class, Gson.atomicLongAdapter(longAdapter)));
        factories.add(TypeAdapters.newFactory(AtomicLongArray.class, Gson.atomicLongArrayAdapter(longAdapter)));
        factories.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
        factories.add(TypeAdapters.CHARACTER_FACTORY);
        factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
        factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
        factories.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
        factories.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
        factories.add(TypeAdapters.URL_FACTORY);
        factories.add(TypeAdapters.URI_FACTORY);
        factories.add(TypeAdapters.UUID_FACTORY);
        factories.add(TypeAdapters.CURRENCY_FACTORY);
        factories.add(TypeAdapters.LOCALE_FACTORY);
        factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
        factories.add(TypeAdapters.BIT_SET_FACTORY);
        factories.add(DateTypeAdapter.FACTORY);
        factories.add(TypeAdapters.CALENDAR_FACTORY);
        factories.add(TimeTypeAdapter.FACTORY);
        factories.add(SqlDateTypeAdapter.FACTORY);
        factories.add(TypeAdapters.TIMESTAMP_FACTORY);
        factories.add(ArrayTypeAdapter.FACTORY);
        factories.add(TypeAdapters.CLASS_FACTORY);
        factories.add(new CollectionTypeAdapterFactory((ConstructorConstructor)this.constructorConstructor));
        factories.add(new MapTypeAdapterFactory((ConstructorConstructor)this.constructorConstructor, (boolean)complexMapKeySerialization));
        this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory((ConstructorConstructor)this.constructorConstructor);
        factories.add(this.jsonAdapterFactory);
        factories.add(TypeAdapters.ENUM_FACTORY);
        factories.add(new ReflectiveTypeAdapterFactory((ConstructorConstructor)this.constructorConstructor, (FieldNamingStrategy)fieldNamingStrategy, (Excluder)excluder, (JsonAdapterAnnotationTypeAdapterFactory)this.jsonAdapterFactory));
        this.factories = Collections.unmodifiableList(factories);
    }

    public Excluder excluder() {
        return this.excluder;
    }

    public FieldNamingStrategy fieldNamingStrategy() {
        return this.fieldNamingStrategy;
    }

    public boolean serializeNulls() {
        return this.serializeNulls;
    }

    public boolean htmlSafe() {
        return this.htmlSafe;
    }

    private TypeAdapter<Number> doubleAdapter(boolean serializeSpecialFloatingPointValues) {
        if (!serializeSpecialFloatingPointValues) return new TypeAdapter<Number>((Gson)this){
            final /* synthetic */ Gson this$0;
            {
                this.this$0 = this$0;
            }

            public Double read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) return Double.valueOf((double)in.nextDouble());
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, Number value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                double doubleValue = value.doubleValue();
                Gson.checkValidFloatingPoint((double)doubleValue);
                out.value((Number)value);
            }
        };
        return TypeAdapters.DOUBLE;
    }

    private TypeAdapter<Number> floatAdapter(boolean serializeSpecialFloatingPointValues) {
        if (!serializeSpecialFloatingPointValues) return new TypeAdapter<Number>((Gson)this){
            final /* synthetic */ Gson this$0;
            {
                this.this$0 = this$0;
            }

            public Float read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) return Float.valueOf((float)((float)in.nextDouble()));
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, Number value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                float floatValue = value.floatValue();
                Gson.checkValidFloatingPoint((double)((double)floatValue));
                out.value((Number)value);
            }
        };
        return TypeAdapters.FLOAT;
    }

    static void checkValidFloatingPoint(double value) {
        if (Double.isNaN((double)value)) throw new IllegalArgumentException((String)(value + " is not a valid double value as per JSON specification. To override this behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method."));
        if (!Double.isInfinite((double)value)) return;
        throw new IllegalArgumentException((String)(value + " is not a valid double value as per JSON specification. To override this behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method."));
    }

    private static TypeAdapter<Number> longAdapter(LongSerializationPolicy longSerializationPolicy) {
        if (longSerializationPolicy != LongSerializationPolicy.DEFAULT) return new TypeAdapter<Number>(){

            public Number read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.NULL) return Long.valueOf((long)in.nextLong());
                in.nextNull();
                return null;
            }

            public void write(JsonWriter out, Number value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.value((String)value.toString());
            }
        };
        return TypeAdapters.LONG;
    }

    private static TypeAdapter<AtomicLong> atomicLongAdapter(TypeAdapter<Number> longAdapter) {
        return new TypeAdapter<AtomicLong>(longAdapter){
            final /* synthetic */ TypeAdapter val$longAdapter;
            {
                this.val$longAdapter = typeAdapter;
            }

            public void write(JsonWriter out, AtomicLong value) throws IOException {
                this.val$longAdapter.write((JsonWriter)out, Long.valueOf((long)value.get()));
            }

            public AtomicLong read(JsonReader in) throws IOException {
                Number value = (Number)this.val$longAdapter.read((JsonReader)in);
                return new AtomicLong((long)value.longValue());
            }
        }.nullSafe();
    }

    private static TypeAdapter<AtomicLongArray> atomicLongArrayAdapter(TypeAdapter<Number> longAdapter) {
        return new TypeAdapter<AtomicLongArray>(longAdapter){
            final /* synthetic */ TypeAdapter val$longAdapter;
            {
                this.val$longAdapter = typeAdapter;
            }

            public void write(JsonWriter out, AtomicLongArray value) throws IOException {
                out.beginArray();
                int i = 0;
                int length = value.length();
                do {
                    if (i >= length) {
                        out.endArray();
                        return;
                    }
                    this.val$longAdapter.write((JsonWriter)out, Long.valueOf((long)value.get((int)i)));
                    ++i;
                } while (true);
            }

            public AtomicLongArray read(JsonReader in) throws IOException {
                ArrayList<Long> list = new ArrayList<Long>();
                in.beginArray();
                while (in.hasNext()) {
                    long value = ((Number)this.val$longAdapter.read((JsonReader)in)).longValue();
                    list.add(Long.valueOf((long)value));
                }
                in.endArray();
                int length = list.size();
                AtomicLongArray array = new AtomicLongArray((int)length);
                int i = 0;
                while (i < length) {
                    array.set((int)i, (long)((Long)list.get((int)i)).longValue());
                    ++i;
                }
                return array;
            }
        }.nullSafe();
    }

    public <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
        FutureTypeAdapter<?> ongoingCall;
        TypeAdapter<?> cached = this.typeTokenCache.get(type == null ? NULL_KEY_SURROGATE : type);
        if (cached != null) {
            return cached;
        }
        Map<TypeToken<?>, FutureTypeAdapter<?>> threadCalls = this.calls.get();
        boolean requiresThreadLocalCleanup = false;
        if (threadCalls == null) {
            threadCalls = new HashMap<TypeToken<?>, FutureTypeAdapter<?>>();
            this.calls.set(threadCalls);
            requiresThreadLocalCleanup = true;
        }
        if ((ongoingCall = threadCalls.get(type)) != null) {
            return ongoingCall;
        }
        try {
            TypeAdapterFactory factory;
            TypeAdapter<T> candidate;
            FutureTypeAdapter<T> call = new FutureTypeAdapter<T>();
            threadCalls.put(type, call);
            Iterator<TypeAdapterFactory> iterator = this.factories.iterator();
            do {
                if (!iterator.hasNext()) throw new IllegalArgumentException((String)("GSON cannot handle " + type));
            } while ((candidate = (factory = iterator.next()).create((Gson)this, type)) == null);
            call.setDelegate(candidate);
            this.typeTokenCache.put(type, candidate);
            TypeAdapter<T> typeAdapter = candidate;
            return typeAdapter;
        }
        finally {
            threadCalls.remove(type);
            if (requiresThreadLocalCleanup) {
                this.calls.remove();
            }
        }
    }

    public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory skipPast, TypeToken<T> type) {
        if (!this.factories.contains((Object)skipPast)) {
            skipPast = this.jsonAdapterFactory;
        }
        boolean skipPastFound = false;
        Iterator<TypeAdapterFactory> iterator = this.factories.iterator();
        while (iterator.hasNext()) {
            TypeAdapterFactory factory = iterator.next();
            if (!skipPastFound) {
                if (factory != skipPast) continue;
                skipPastFound = true;
                continue;
            }
            TypeAdapter<T> candidate = factory.create((Gson)this, type);
            if (candidate != null) return candidate;
        }
        throw new IllegalArgumentException((String)("GSON cannot serialize " + type));
    }

    public <T> TypeAdapter<T> getAdapter(Class<T> type) {
        return this.getAdapter(TypeToken.get(type));
    }

    public JsonElement toJsonTree(Object src) {
        if (src != null) return this.toJsonTree((Object)src, src.getClass());
        return JsonNull.INSTANCE;
    }

    public JsonElement toJsonTree(Object src, Type typeOfSrc) {
        JsonTreeWriter writer = new JsonTreeWriter();
        this.toJson((Object)src, (Type)typeOfSrc, (JsonWriter)writer);
        return writer.get();
    }

    public String toJson(Object src) {
        if (src != null) return this.toJson((Object)src, src.getClass());
        return this.toJson((JsonElement)JsonNull.INSTANCE);
    }

    public String toJson(Object src, Type typeOfSrc) {
        StringWriter writer = new StringWriter();
        this.toJson((Object)src, (Type)typeOfSrc, (Appendable)writer);
        return writer.toString();
    }

    public void toJson(Object src, Appendable writer) throws JsonIOException {
        if (src != null) {
            this.toJson((Object)src, src.getClass(), (Appendable)writer);
            return;
        }
        this.toJson((JsonElement)JsonNull.INSTANCE, (Appendable)writer);
    }

    public void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        try {
            JsonWriter jsonWriter = this.newJsonWriter((Writer)Streams.writerForAppendable((Appendable)writer));
            this.toJson((Object)src, (Type)typeOfSrc, (JsonWriter)jsonWriter);
            return;
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
    }

    public void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
        TypeAdapter<?> adapter = this.getAdapter(TypeToken.get((Type)typeOfSrc));
        boolean oldLenient = writer.isLenient();
        writer.setLenient((boolean)true);
        boolean oldHtmlSafe = writer.isHtmlSafe();
        writer.setHtmlSafe((boolean)this.htmlSafe);
        boolean oldSerializeNulls = writer.getSerializeNulls();
        writer.setSerializeNulls((boolean)this.serializeNulls);
        try {
            adapter.write((JsonWriter)writer, src);
            return;
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
        finally {
            writer.setLenient((boolean)oldLenient);
            writer.setHtmlSafe((boolean)oldHtmlSafe);
            writer.setSerializeNulls((boolean)oldSerializeNulls);
        }
    }

    public String toJson(JsonElement jsonElement) {
        StringWriter writer = new StringWriter();
        this.toJson((JsonElement)jsonElement, (Appendable)writer);
        return writer.toString();
    }

    public void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
        try {
            JsonWriter jsonWriter = this.newJsonWriter((Writer)Streams.writerForAppendable((Appendable)writer));
            this.toJson((JsonElement)jsonElement, (JsonWriter)jsonWriter);
            return;
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
    }

    public JsonWriter newJsonWriter(Writer writer) throws IOException {
        if (this.generateNonExecutableJson) {
            writer.write((String)JSON_NON_EXECUTABLE_PREFIX);
        }
        JsonWriter jsonWriter = new JsonWriter((Writer)writer);
        if (this.prettyPrinting) {
            jsonWriter.setIndent((String)"  ");
        }
        jsonWriter.setSerializeNulls((boolean)this.serializeNulls);
        return jsonWriter;
    }

    public JsonReader newJsonReader(Reader reader) {
        JsonReader jsonReader = new JsonReader((Reader)reader);
        jsonReader.setLenient((boolean)this.lenient);
        return jsonReader;
    }

    public void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
        boolean oldLenient = writer.isLenient();
        writer.setLenient((boolean)true);
        boolean oldHtmlSafe = writer.isHtmlSafe();
        writer.setHtmlSafe((boolean)this.htmlSafe);
        boolean oldSerializeNulls = writer.getSerializeNulls();
        writer.setSerializeNulls((boolean)this.serializeNulls);
        try {
            Streams.write((JsonElement)jsonElement, (JsonWriter)writer);
            return;
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
        finally {
            writer.setLenient((boolean)oldLenient);
            writer.setHtmlSafe((boolean)oldHtmlSafe);
            writer.setSerializeNulls((boolean)oldSerializeNulls);
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        T object = this.fromJson((String)json, classOfT);
        return (T)Primitives.wrap(classOfT).cast(object);
    }

    public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        if (json == null) {
            return (T)null;
        }
        StringReader reader = new StringReader((String)json);
        T target = this.fromJson((Reader)reader, (Type)typeOfT);
        return (T)target;
    }

    public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        JsonReader jsonReader = this.newJsonReader((Reader)json);
        T object = this.fromJson((JsonReader)jsonReader, classOfT);
        Gson.assertFullConsumption(object, (JsonReader)jsonReader);
        return (T)Primitives.wrap(classOfT).cast(object);
    }

    public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        JsonReader jsonReader = this.newJsonReader((Reader)json);
        T object = this.fromJson((JsonReader)jsonReader, (Type)typeOfT);
        Gson.assertFullConsumption(object, (JsonReader)jsonReader);
        return (T)object;
    }

    private static void assertFullConsumption(Object obj, JsonReader reader) {
        try {
            if (obj == null) return;
            if (reader.peek() == JsonToken.END_DOCUMENT) return;
            throw new JsonIOException((String)"JSON document was not fully consumed.");
        }
        catch (MalformedJsonException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
    }

    public <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        boolean isEmpty = true;
        boolean oldLenient = reader.isLenient();
        reader.setLenient((boolean)true);
        try {
            ? object;
            reader.peek();
            isEmpty = false;
            TypeToken<?> typeToken = TypeToken.get((Type)typeOfT);
            TypeAdapter<?> typeAdapter = this.getAdapter(typeToken);
            ? obj = object = typeAdapter.read((JsonReader)reader);
            return (T)((T)obj);
        }
        catch (EOFException e) {
            if (!isEmpty) throw new JsonSyntaxException((Throwable)e);
            T typeAdapter = null;
            return (T)((T)typeAdapter);
        }
        catch (IllegalStateException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
        catch (IOException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
        finally {
            reader.setLenient((boolean)oldLenient);
        }
    }

    public <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        T object = this.fromJson((JsonElement)json, classOfT);
        return (T)Primitives.wrap(classOfT).cast(object);
    }

    public <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
        if (json != null) return (T)this.fromJson((JsonReader)new JsonTreeReader((JsonElement)json), (Type)typeOfT);
        return (T)null;
    }

    public String toString() {
        return "{serializeNulls:" + this.serializeNulls + "factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
    }
}

