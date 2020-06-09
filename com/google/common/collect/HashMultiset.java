/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.AbstractMapBasedMultiset;
import com.google.common.collect.Count;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public final class HashMultiset<E>
extends AbstractMapBasedMultiset<E> {
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <E> HashMultiset<E> create() {
        return new HashMultiset<E>();
    }

    public static <E> HashMultiset<E> create(int distinctElements) {
        return new HashMultiset<E>((int)distinctElements);
    }

    public static <E> HashMultiset<E> create(Iterable<? extends E> elements) {
        HashMultiset<E> multiset = HashMultiset.create((int)Multisets.inferDistinctElements(elements));
        Iterables.addAll(multiset, elements);
        return multiset;
    }

    private HashMultiset() {
        super(new HashMap<K, V>());
    }

    private HashMultiset(int distinctElements) {
        super(Maps.newHashMapWithExpectedSize((int)distinctElements));
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        Serialization.writeMultiset(this, (ObjectOutputStream)stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int distinctElements = Serialization.readCount((ObjectInputStream)stream);
        this.setBackingMap(Maps.<K, V>newHashMap());
        Serialization.populateMultiset(this, (ObjectInputStream)stream, (int)distinctElements);
    }
}

