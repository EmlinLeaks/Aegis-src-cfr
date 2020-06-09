/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@GwtIncompatible
final class Serialization {
    private Serialization() {
    }

    static int readCount(ObjectInputStream stream) throws IOException {
        return stream.readInt();
    }

    static <K, V> void writeMap(Map<K, V> map, ObjectOutputStream stream) throws IOException {
        stream.writeInt((int)map.size());
        Iterator<Map.Entry<K, V>> i$ = map.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    static <K, V> void populateMap(Map<K, V> map, ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int size = stream.readInt();
        Serialization.populateMap(map, (ObjectInputStream)stream, (int)size);
    }

    static <K, V> void populateMap(Map<K, V> map, ObjectInputStream stream, int size) throws IOException, ClassNotFoundException {
        int i = 0;
        while (i < size) {
            Object key = stream.readObject();
            Object value = stream.readObject();
            map.put(key, value);
            ++i;
        }
    }

    static <E> void writeMultiset(Multiset<E> multiset, ObjectOutputStream stream) throws IOException {
        int entryCount = multiset.entrySet().size();
        stream.writeInt((int)entryCount);
        Iterator<Multiset.Entry<E>> i$ = multiset.entrySet().iterator();
        while (i$.hasNext()) {
            Multiset.Entry<E> entry = i$.next();
            stream.writeObject(entry.getElement());
            stream.writeInt((int)entry.getCount());
        }
    }

    static <E> void populateMultiset(Multiset<E> multiset, ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int distinctElements = stream.readInt();
        Serialization.populateMultiset(multiset, (ObjectInputStream)stream, (int)distinctElements);
    }

    static <E> void populateMultiset(Multiset<E> multiset, ObjectInputStream stream, int distinctElements) throws IOException, ClassNotFoundException {
        int i = 0;
        while (i < distinctElements) {
            Object element = stream.readObject();
            int count = stream.readInt();
            multiset.add(element, (int)count);
            ++i;
        }
    }

    static <K, V> void writeMultimap(Multimap<K, V> multimap, ObjectOutputStream stream) throws IOException {
        stream.writeInt((int)multimap.asMap().size());
        Iterator<Map.Entry<K, Collection<V>>> i$ = multimap.asMap().entrySet().iterator();
        block0 : while (i$.hasNext()) {
            Map.Entry<K, Collection<V>> entry = i$.next();
            stream.writeObject(entry.getKey());
            stream.writeInt((int)entry.getValue().size());
            Iterator<V> i$2 = entry.getValue().iterator();
            do {
                if (!i$2.hasNext()) continue block0;
                V value = i$2.next();
                stream.writeObject(value);
            } while (true);
            break;
        }
        return;
    }

    static <K, V> void populateMultimap(Multimap<K, V> multimap, ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int distinctKeys = stream.readInt();
        Serialization.populateMultimap(multimap, (ObjectInputStream)stream, (int)distinctKeys);
    }

    static <K, V> void populateMultimap(Multimap<K, V> multimap, ObjectInputStream stream, int distinctKeys) throws IOException, ClassNotFoundException {
        int i = 0;
        while (i < distinctKeys) {
            Object key = stream.readObject();
            Collection<V> values = multimap.get(key);
            int valueCount = stream.readInt();
            for (int j = 0; j < valueCount; ++j) {
                Object value = stream.readObject();
                values.add(value);
            }
            ++i;
        }
    }

    static <T> FieldSetter<T> getFieldSetter(Class<T> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField((String)fieldName);
            return new FieldSetter<T>((Field)field, null);
        }
        catch (NoSuchFieldException e) {
            throw new AssertionError((Object)e);
        }
    }
}

