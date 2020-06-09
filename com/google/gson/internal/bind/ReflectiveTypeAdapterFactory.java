/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory
implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;

    public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory) {
        this.constructorConstructor = constructorConstructor;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.excluder = excluder;
        this.jsonAdapterFactory = jsonAdapterFactory;
    }

    public boolean excludeField(Field f, boolean serialize) {
        return ReflectiveTypeAdapterFactory.excludeField((Field)f, (boolean)serialize, (Excluder)this.excluder);
    }

    static boolean excludeField(Field f, boolean serialize, Excluder excluder) {
        if (excluder.excludeClass(f.getType(), (boolean)serialize)) return false;
        if (excluder.excludeField((Field)f, (boolean)serialize)) return false;
        return true;
    }

    private List<String> getFieldNames(Field f) {
        SerializedName annotation = f.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = this.fieldNamingPolicy.translateName((Field)f);
            return Collections.singletonList(name);
        }
        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }
        ArrayList<String> fieldNames = new ArrayList<String>((int)(alternates.length + 1));
        fieldNames.add(serializedName);
        String[] arrstring = alternates;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String alternate = arrstring[n2];
            fieldNames.add((String)alternate);
            ++n2;
        }
        return fieldNames;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null;
        }
        ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
        return new Adapter<T>(constructor, this.getBoundFields((Gson)gson, type, raw));
    }

    private BoundField createBoundField(Gson context, Field field, String name, TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
        boolean jsonAdapterPresent;
        boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        TypeAdapter<?> mapped = null;
        if (annotation != null) {
            mapped = this.jsonAdapterFactory.getTypeAdapter((ConstructorConstructor)this.constructorConstructor, (Gson)context, fieldType, (JsonAdapter)annotation);
        }
        boolean bl = jsonAdapterPresent = mapped != null;
        if (mapped == null) {
            mapped = context.getAdapter(fieldType);
        }
        TypeAdapter<?> typeAdapter = mapped;
        return new BoundField((ReflectiveTypeAdapterFactory)this, (String)name, (boolean)serialize, (boolean)deserialize, (Field)field, (boolean)jsonAdapterPresent, typeAdapter, (Gson)context, fieldType, (boolean)isPrimitive){
            final /* synthetic */ Field val$field;
            final /* synthetic */ boolean val$jsonAdapterPresent;
            final /* synthetic */ TypeAdapter val$typeAdapter;
            final /* synthetic */ Gson val$context;
            final /* synthetic */ TypeToken val$fieldType;
            final /* synthetic */ boolean val$isPrimitive;
            final /* synthetic */ ReflectiveTypeAdapterFactory this$0;
            {
                this.this$0 = this$0;
                this.val$field = field;
                this.val$jsonAdapterPresent = bl;
                this.val$typeAdapter = typeAdapter;
                this.val$context = gson;
                this.val$fieldType = typeToken;
                this.val$isPrimitive = bl2;
                super((String)name, (boolean)serialized, (boolean)deserialized);
            }

            void write(com.google.gson.stream.JsonWriter writer, Object value) throws java.io.IOException, java.lang.IllegalAccessException {
                Object fieldValue = this.val$field.get((Object)value);
                TypeAdapter<T> t = this.val$jsonAdapterPresent ? this.val$typeAdapter : new com.google.gson.internal.bind.TypeAdapterRuntimeTypeWrapper<T>((Gson)this.val$context, this.val$typeAdapter, (Type)this.val$fieldType.getType());
                t.write((com.google.gson.stream.JsonWriter)writer, fieldValue);
            }

            void read(com.google.gson.stream.JsonReader reader, Object value) throws java.io.IOException, java.lang.IllegalAccessException {
                T fieldValue = this.val$typeAdapter.read((com.google.gson.stream.JsonReader)reader);
                if (fieldValue == null) {
                    if (this.val$isPrimitive) return;
                }
                this.val$field.set((Object)value, fieldValue);
            }

            public boolean writeField(Object value) throws java.io.IOException, java.lang.IllegalAccessException {
                if (!this.serialized) {
                    return false;
                }
                Object fieldValue = this.val$field.get((Object)value);
                if (fieldValue == value) return false;
                return true;
            }
        };
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
        LinkedHashMap<String, BoundField> result = new LinkedHashMap<String, BoundField>();
        if (raw.isInterface()) {
            return result;
        }
        Type declaredType = type.getType();
        while (raw != Object.class) {
            Field[] fields;
            for (Field field : fields = raw.getDeclaredFields()) {
                boolean serialize = this.excludeField((Field)field, (boolean)true);
                boolean deserialize = this.excludeField((Field)field, (boolean)false);
                if (!serialize && !deserialize) continue;
                field.setAccessible((boolean)true);
                Type fieldType = $Gson$Types.resolve((Type)type.getType(), raw, (Type)field.getGenericType());
                List<String> fieldNames = this.getFieldNames((Field)field);
                BoundField previous = null;
                for (int i = 0; i < fieldNames.size(); ++i) {
                    String name = fieldNames.get((int)i);
                    if (i != 0) {
                        serialize = false;
                    }
                    BoundField boundField = this.createBoundField((Gson)context, (Field)field, (String)name, TypeToken.get((Type)fieldType), (boolean)serialize, (boolean)deserialize);
                    BoundField replaced = result.put((String)name, (BoundField)boundField);
                    if (previous != null) continue;
                    previous = replaced;
                }
                if (previous == null) continue;
                throw new IllegalArgumentException((String)(declaredType + " declares multiple JSON fields named " + previous.name));
            }
            type = TypeToken.get((Type)$Gson$Types.resolve((Type)type.getType(), raw, (Type)raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }
}

