/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractListMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public final class ArrayListMultimap<K, V>
extends AbstractListMultimap<K, V> {
    private static final int DEFAULT_VALUES_PER_KEY = 3;
    @VisibleForTesting
    transient int expectedValuesPerKey;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <K, V> ArrayListMultimap<K, V> create() {
        return new ArrayListMultimap<K, V>();
    }

    public static <K, V> ArrayListMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey) {
        return new ArrayListMultimap<K, V>((int)expectedKeys, (int)expectedValuesPerKey);
    }

    public static <K, V> ArrayListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new ArrayListMultimap<K, V>(multimap);
    }

    private ArrayListMultimap() {
        super(new HashMap<K, V>());
        this.expectedValuesPerKey = 3;
    }

    private ArrayListMultimap(int expectedKeys, int expectedValuesPerKey) {
        super(Maps.newHashMapWithExpectedSize((int)expectedKeys));
        CollectPreconditions.checkNonnegative((int)expectedValuesPerKey, (String)"expectedValuesPerKey");
        this.expectedValuesPerKey = expectedValuesPerKey;
    }

    private ArrayListMultimap(Multimap<? extends K, ? extends V> multimap) {
        this((int)multimap.keySet().size(), (int)(multimap instanceof ArrayListMultimap ? ((ArrayListMultimap)multimap).expectedValuesPerKey : 3));
        this.putAll(multimap);
    }

    @Override
    List<V> createCollection() {
        return new ArrayList<E>((int)this.expectedValuesPerKey);
    }

    public void trimToSize() {
        Iterator<Collection<V>> i$ = this.backingMap().values().iterator();
        while (i$.hasNext()) {
            Collection<V> collection = i$.next();
            ArrayList arrayList = (ArrayList)collection;
            arrayList.trimToSize();
        }
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        Serialization.writeMultimap(this, (ObjectOutputStream)stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.expectedValuesPerKey = 3;
        int distinctKeys = Serialization.readCount((ObjectInputStream)stream);
        HashMap<K, V> map = Maps.newHashMap();
        this.setMap(map);
        Serialization.populateMultimap(this, (ObjectInputStream)stream, (int)distinctKeys);
    }
}

