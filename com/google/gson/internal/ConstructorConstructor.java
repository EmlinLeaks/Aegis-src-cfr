/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;

public final class ConstructorConstructor {
    private final Map<Type, InstanceCreator<?>> instanceCreators;

    public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators) {
        this.instanceCreators = instanceCreators;
    }

    public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        Class<T> rawType = typeToken.getRawType();
        InstanceCreator<?> typeCreator = this.instanceCreators.get((Object)type);
        if (typeCreator != null) {
            return new ObjectConstructor<T>((ConstructorConstructor)this, typeCreator, (Type)type){
                final /* synthetic */ InstanceCreator val$typeCreator;
                final /* synthetic */ Type val$type;
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                    this.val$typeCreator = instanceCreator;
                    this.val$type = type;
                }

                public T construct() {
                    return (T)this.val$typeCreator.createInstance((Type)this.val$type);
                }
            };
        }
        InstanceCreator<?> rawTypeCreator = this.instanceCreators.get(rawType);
        if (rawTypeCreator != null) {
            return new ObjectConstructor<T>((ConstructorConstructor)this, rawTypeCreator, (Type)type){
                final /* synthetic */ InstanceCreator val$rawTypeCreator;
                final /* synthetic */ Type val$type;
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                    this.val$rawTypeCreator = instanceCreator;
                    this.val$type = type;
                }

                public T construct() {
                    return (T)this.val$rawTypeCreator.createInstance((Type)this.val$type);
                }
            };
        }
        ObjectConstructor<T> defaultConstructor = this.newDefaultConstructor(rawType);
        if (defaultConstructor != null) {
            return defaultConstructor;
        }
        ObjectConstructor<T> defaultImplementation = this.newDefaultImplementationConstructor((Type)type, rawType);
        if (defaultImplementation == null) return this.newUnsafeAllocator((Type)type, rawType);
        return defaultImplementation;
    }

    private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
        try {
            Constructor<T> constructor = rawType.getDeclaredConstructor(new Class[0]);
            if (constructor.isAccessible()) return new ObjectConstructor<T>((ConstructorConstructor)this, constructor){
                final /* synthetic */ Constructor val$constructor;
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                    this.val$constructor = constructor;
                }

                public T construct() {
                    try {
                        Object[] args = null;
                        return (T)this.val$constructor.newInstance(args);
                    }
                    catch (java.lang.InstantiationException e) {
                        throw new java.lang.RuntimeException((String)("Failed to invoke " + this.val$constructor + " with no args"), (java.lang.Throwable)e);
                    }
                    catch (java.lang.reflect.InvocationTargetException e) {
                        throw new java.lang.RuntimeException((String)("Failed to invoke " + this.val$constructor + " with no args"), (java.lang.Throwable)e.getTargetException());
                    }
                    catch (java.lang.IllegalAccessException e) {
                        throw new java.lang.AssertionError((Object)e);
                    }
                }
            };
            constructor.setAccessible((boolean)true);
            return new /* invalid duplicate definition of identical inner class */;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type type, Class<? super T> rawType) {
        if (Collection.class.isAssignableFrom(rawType)) {
            if (SortedSet.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>((ConstructorConstructor)this){
                    final /* synthetic */ ConstructorConstructor this$0;
                    {
                        this.this$0 = this$0;
                    }

                    public T construct() {
                        return (T)new java.util.TreeSet<E>();
                    }
                };
            }
            if (EnumSet.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>((ConstructorConstructor)this, (Type)type){
                    final /* synthetic */ Type val$type;
                    final /* synthetic */ ConstructorConstructor this$0;
                    {
                        this.this$0 = this$0;
                        this.val$type = type;
                    }

                    public T construct() {
                        if (!(this.val$type instanceof ParameterizedType)) throw new com.google.gson.JsonIOException((String)("Invalid EnumSet type: " + this.val$type.toString()));
                        Type elementType = ((ParameterizedType)this.val$type).getActualTypeArguments()[0];
                        if (!(elementType instanceof Class)) throw new com.google.gson.JsonIOException((String)("Invalid EnumSet type: " + this.val$type.toString()));
                        return (T)EnumSet.noneOf((Class)elementType);
                    }
                };
            }
            if (Set.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>((ConstructorConstructor)this){
                    final /* synthetic */ ConstructorConstructor this$0;
                    {
                        this.this$0 = this$0;
                    }

                    public T construct() {
                        return (T)new java.util.LinkedHashSet<E>();
                    }
                };
            }
            if (!Queue.class.isAssignableFrom(rawType)) return new ObjectConstructor<T>((ConstructorConstructor)this){
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                }

                public T construct() {
                    return (T)new java.util.ArrayList<E>();
                }
            };
            return new ObjectConstructor<T>((ConstructorConstructor)this){
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                }

                public T construct() {
                    return (T)new java.util.ArrayDeque<E>();
                }
            };
        }
        if (!Map.class.isAssignableFrom(rawType)) return null;
        if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
            return new ObjectConstructor<T>((ConstructorConstructor)this){
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                }

                public T construct() {
                    return (T)new java.util.concurrent.ConcurrentSkipListMap<K, V>();
                }
            };
        }
        if (ConcurrentMap.class.isAssignableFrom(rawType)) {
            return new ObjectConstructor<T>((ConstructorConstructor)this){
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                }

                public T construct() {
                    return (T)new java.util.concurrent.ConcurrentHashMap<K, V>();
                }
            };
        }
        if (SortedMap.class.isAssignableFrom(rawType)) {
            return new ObjectConstructor<T>((ConstructorConstructor)this){
                final /* synthetic */ ConstructorConstructor this$0;
                {
                    this.this$0 = this$0;
                }

                public T construct() {
                    return (T)new java.util.TreeMap<K, V>();
                }
            };
        }
        if (!(type instanceof ParameterizedType)) return new ObjectConstructor<T>((ConstructorConstructor)this){
            final /* synthetic */ ConstructorConstructor this$0;
            {
                this.this$0 = this$0;
            }

            public T construct() {
                return (T)new com.google.gson.internal.LinkedTreeMap<K, V>();
            }
        };
        if (String.class.isAssignableFrom(TypeToken.get((Type)((ParameterizedType)type).getActualTypeArguments()[0]).getRawType())) return new /* invalid duplicate definition of identical inner class */;
        return new ObjectConstructor<T>((ConstructorConstructor)this){
            final /* synthetic */ ConstructorConstructor this$0;
            {
                this.this$0 = this$0;
            }

            public T construct() {
                return (T)new java.util.LinkedHashMap<K, V>();
            }
        };
    }

    private <T> ObjectConstructor<T> newUnsafeAllocator(Type type, Class<? super T> rawType) {
        return new ObjectConstructor<T>((ConstructorConstructor)this, rawType, (Type)type){
            private final com.google.gson.internal.UnsafeAllocator unsafeAllocator;
            final /* synthetic */ Class val$rawType;
            final /* synthetic */ Type val$type;
            final /* synthetic */ ConstructorConstructor this$0;
            {
                this.this$0 = this$0;
                this.val$rawType = class_;
                this.val$type = type;
                this.unsafeAllocator = com.google.gson.internal.UnsafeAllocator.create();
            }

            public T construct() {
                try {
                    T newInstance = this.unsafeAllocator.newInstance(this.val$rawType);
                    return (T)newInstance;
                }
                catch (java.lang.Exception e) {
                    throw new java.lang.RuntimeException((String)("Unable to invoke no-args constructor for " + this.val$type + ". Register an InstanceCreator with Gson for this type may fix this problem."), (java.lang.Throwable)e);
                }
            }
        };
    }

    public String toString() {
        return this.instanceCreators.toString();
    }
}

