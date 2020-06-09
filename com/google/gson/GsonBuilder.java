/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.DefaultDateTypeAdapter;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GsonBuilder {
    private Excluder excluder = Excluder.DEFAULT;
    private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
    private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
    private final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap<Type, InstanceCreator<?>>();
    private final List<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>();
    private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList<TypeAdapterFactory>();
    private boolean serializeNulls = false;
    private String datePattern;
    private int dateStyle = 2;
    private int timeStyle = 2;
    private boolean complexMapKeySerialization = false;
    private boolean serializeSpecialFloatingPointValues = false;
    private boolean escapeHtmlChars = true;
    private boolean prettyPrinting = false;
    private boolean generateNonExecutableJson = false;
    private boolean lenient = false;

    public GsonBuilder setVersion(double ignoreVersionsAfter) {
        this.excluder = this.excluder.withVersion((double)ignoreVersionsAfter);
        return this;
    }

    public GsonBuilder excludeFieldsWithModifiers(int ... modifiers) {
        this.excluder = this.excluder.withModifiers((int[])modifiers);
        return this;
    }

    public GsonBuilder generateNonExecutableJson() {
        this.generateNonExecutableJson = true;
        return this;
    }

    public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
        this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
        return this;
    }

    public GsonBuilder serializeNulls() {
        this.serializeNulls = true;
        return this;
    }

    public GsonBuilder enableComplexMapKeySerialization() {
        this.complexMapKeySerialization = true;
        return this;
    }

    public GsonBuilder disableInnerClassSerialization() {
        this.excluder = this.excluder.disableInnerClassSerialization();
        return this;
    }

    public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy) {
        this.longSerializationPolicy = serializationPolicy;
        return this;
    }

    public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy namingConvention) {
        this.fieldNamingPolicy = namingConvention;
        return this;
    }

    public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
        this.fieldNamingPolicy = fieldNamingStrategy;
        return this;
    }

    public GsonBuilder setExclusionStrategies(ExclusionStrategy ... strategies) {
        ExclusionStrategy[] arrexclusionStrategy = strategies;
        int n = arrexclusionStrategy.length;
        int n2 = 0;
        while (n2 < n) {
            ExclusionStrategy strategy = arrexclusionStrategy[n2];
            this.excluder = this.excluder.withExclusionStrategy((ExclusionStrategy)strategy, (boolean)true, (boolean)true);
            ++n2;
        }
        return this;
    }

    public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy strategy) {
        this.excluder = this.excluder.withExclusionStrategy((ExclusionStrategy)strategy, (boolean)true, (boolean)false);
        return this;
    }

    public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy strategy) {
        this.excluder = this.excluder.withExclusionStrategy((ExclusionStrategy)strategy, (boolean)false, (boolean)true);
        return this;
    }

    public GsonBuilder setPrettyPrinting() {
        this.prettyPrinting = true;
        return this;
    }

    public GsonBuilder setLenient() {
        this.lenient = true;
        return this;
    }

    public GsonBuilder disableHtmlEscaping() {
        this.escapeHtmlChars = false;
        return this;
    }

    public GsonBuilder setDateFormat(String pattern) {
        this.datePattern = pattern;
        return this;
    }

    public GsonBuilder setDateFormat(int style) {
        this.dateStyle = style;
        this.datePattern = null;
        return this;
    }

    public GsonBuilder setDateFormat(int dateStyle, int timeStyle) {
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
        this.datePattern = null;
        return this;
    }

    public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter) {
        $Gson$Preconditions.checkArgument((boolean)(typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof InstanceCreator || typeAdapter instanceof TypeAdapter));
        if (typeAdapter instanceof InstanceCreator) {
            this.instanceCreators.put((Type)type, (InstanceCreator)typeAdapter);
        }
        if (typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer) {
            TypeToken<?> typeToken = TypeToken.get((Type)type);
            this.factories.add((TypeAdapterFactory)TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, (Object)typeAdapter));
        }
        if (!(typeAdapter instanceof TypeAdapter)) return this;
        this.factories.add((TypeAdapterFactory)TypeAdapters.newFactory(TypeToken.get((Type)type), (TypeAdapter)typeAdapter));
        return this;
    }

    public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory) {
        this.factories.add((TypeAdapterFactory)factory);
        return this;
    }

    public GsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
        $Gson$Preconditions.checkArgument((boolean)(typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof TypeAdapter));
        if (typeAdapter instanceof JsonDeserializer || typeAdapter instanceof JsonSerializer) {
            this.hierarchyFactories.add((int)0, (TypeAdapterFactory)TreeTypeAdapter.newTypeHierarchyFactory(baseType, (Object)typeAdapter));
        }
        if (!(typeAdapter instanceof TypeAdapter)) return this;
        this.factories.add((TypeAdapterFactory)TypeAdapters.newTypeHierarchyFactory(baseType, (TypeAdapter)typeAdapter));
        return this;
    }

    public GsonBuilder serializeSpecialFloatingPointValues() {
        this.serializeSpecialFloatingPointValues = true;
        return this;
    }

    public Gson create() {
        ArrayList<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>();
        factories.addAll(this.factories);
        Collections.reverse(factories);
        factories.addAll(this.hierarchyFactories);
        this.addTypeAdaptersForDate((String)this.datePattern, (int)this.dateStyle, (int)this.timeStyle, factories);
        return new Gson((Excluder)this.excluder, (FieldNamingStrategy)this.fieldNamingPolicy, this.instanceCreators, (boolean)this.serializeNulls, (boolean)this.complexMapKeySerialization, (boolean)this.generateNonExecutableJson, (boolean)this.escapeHtmlChars, (boolean)this.prettyPrinting, (boolean)this.lenient, (boolean)this.serializeSpecialFloatingPointValues, (LongSerializationPolicy)this.longSerializationPolicy, factories);
    }

    private void addTypeAdaptersForDate(String datePattern, int dateStyle, int timeStyle, List<TypeAdapterFactory> factories) {
        DefaultDateTypeAdapter dateTypeAdapter;
        if (datePattern != null && !"".equals((Object)datePattern.trim())) {
            dateTypeAdapter = new DefaultDateTypeAdapter((String)datePattern);
        } else {
            if (dateStyle == 2) return;
            if (timeStyle == 2) return;
            dateTypeAdapter = new DefaultDateTypeAdapter((int)dateStyle, (int)timeStyle);
        }
        factories.add((TypeAdapterFactory)TreeTypeAdapter.newFactory(TypeToken.get(java.util.Date.class), (Object)dateTypeAdapter));
        factories.add((TypeAdapterFactory)TreeTypeAdapter.newFactory(TypeToken.get(Timestamp.class), (Object)dateTypeAdapter));
        factories.add((TypeAdapterFactory)TreeTypeAdapter.newFactory(TypeToken.get(Date.class), (Object)dateTypeAdapter));
    }
}

