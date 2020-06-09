/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapBasedMultiset;
import com.google.common.collect.Count;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Serialization;
import com.google.common.collect.WellBehavedMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@GwtCompatible(emulated=true)
public final class EnumMultiset<E extends Enum<E>>
extends AbstractMapBasedMultiset<E> {
    private transient Class<E> type;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <E extends Enum<E>> EnumMultiset<E> create(Class<E> type) {
        return new EnumMultiset<E>(type);
    }

    public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> elements) {
        Iterator<E> iterator = elements.iterator();
        Preconditions.checkArgument((boolean)iterator.hasNext(), (Object)"EnumMultiset constructor passed empty Iterable");
        EnumMultiset<E> multiset = new EnumMultiset<E>(((Enum)iterator.next()).getDeclaringClass());
        Iterables.addAll(multiset, elements);
        return multiset;
    }

    public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> elements, Class<E> type) {
        EnumMultiset<E> result = EnumMultiset.create(type);
        Iterables.addAll(result, elements);
        return result;
    }

    private EnumMultiset(Class<E> type) {
        super(WellBehavedMap.wrap(new EnumMap<E, V>(type)));
        this.type = type;
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.type);
        Serialization.writeMultiset(this, (ObjectOutputStream)stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        Class localType;
        stream.defaultReadObject();
        this.type = localType = (Class)stream.readObject();
        this.setBackingMap(WellBehavedMap.wrap(new EnumMap<E, V>(this.type)));
        Serialization.populateMultiset(this, (ObjectInputStream)stream);
    }
}

