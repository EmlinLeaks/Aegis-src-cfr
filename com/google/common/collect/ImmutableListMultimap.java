/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.EmptyImmutableListMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Serialization;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public class ImmutableListMultimap<K, V>
extends ImmutableMultimap<K, V>
implements ListMultimap<K, V> {
    @LazyInit
    @RetainedWith
    private transient ImmutableListMultimap<V, K> inverse;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <K, V> ImmutableListMultimap<K, V> of() {
        return EmptyImmutableListMultimap.INSTANCE;
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1) {
        Builder<K, V> builder = ImmutableListMultimap.builder();
        builder.put(k1, v1);
        return builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2) {
        Builder<K, V> builder = ImmutableListMultimap.builder();
        builder.put(k1, v1);
        builder.put(k2, v2);
        return builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Builder<K, V> builder = ImmutableListMultimap.builder();
        builder.put(k1, v1);
        builder.put(k2, v2);
        builder.put(k3, v3);
        return builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Builder<K, V> builder = ImmutableListMultimap.builder();
        builder.put(k1, v1);
        builder.put(k2, v2);
        builder.put(k3, v3);
        builder.put(k4, v4);
        return builder.build();
    }

    public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Builder<K, V> builder = ImmutableListMultimap.builder();
        builder.put(k1, v1);
        builder.put(k2, v2);
        builder.put(k3, v3);
        builder.put(k4, v4);
        builder.put(k5, v5);
        return builder.build();
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }

    public static <K, V> ImmutableListMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
        ImmutableListMultimap kvMultimap;
        if (multimap.isEmpty()) {
            return ImmutableListMultimap.of();
        }
        if (multimap instanceof ImmutableListMultimap && !(kvMultimap = (ImmutableListMultimap)multimap).isPartialView()) {
            return kvMultimap;
        }
        ImmutableMap.Builder<K, ImmutableList<V>> builder = new ImmutableMap.Builder<K, ImmutableList<V>>((int)multimap.asMap().size());
        int size = 0;
        Iterator<Map.Entry<K, Collection<V>>> i$ = multimap.asMap().entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, Collection<V>> entry = i$.next();
            ImmutableList<V> list = ImmutableList.copyOf(entry.getValue());
            if (list.isEmpty()) continue;
            builder.put(entry.getKey(), list);
            size += list.size();
        }
        return new ImmutableListMultimap<K, V>(builder.build(), (int)size);
    }

    @Beta
    public static <K, V> ImmutableListMultimap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
        return ((Builder)new Builder<K, V>().putAll(entries)).build();
    }

    ImmutableListMultimap(ImmutableMap<K, ImmutableList<V>> map, int size) {
        super(map, (int)size);
    }

    @Override
    public ImmutableList<V> get(@Nullable K key) {
        ImmutableList immutableList;
        ImmutableList list = (ImmutableList)this.map.get(key);
        if (list == null) {
            immutableList = ImmutableList.of();
            return immutableList;
        }
        immutableList = list;
        return immutableList;
    }

    @Override
    public ImmutableListMultimap<V, K> inverse() {
        ImmutableListMultimap<V, K> immutableListMultimap;
        ImmutableListMultimap<V, K> result = this.inverse;
        if (result == null) {
            immutableListMultimap = this.inverse = this.invert();
            return immutableListMultimap;
        }
        immutableListMultimap = result;
        return immutableListMultimap;
    }

    private ImmutableListMultimap<V, K> invert() {
        Builder<K, V> builder = ImmutableListMultimap.builder();
        Iterator i$ = ((ImmutableCollection)this.entries()).iterator();
        do {
            if (!i$.hasNext()) {
                ImmutableMultimap invertedMultimap = builder.build();
                ((ImmutableListMultimap)invertedMultimap).inverse = this;
                return invertedMultimap;
            }
            Map.Entry entry = (Map.Entry)i$.next();
            builder.put(entry.getValue(), entry.getKey());
        } while (true);
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public ImmutableList<V> removeAll(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public ImmutableList<V> replaceValues(K key, Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        Serialization.writeMultimap(this, (ObjectOutputStream)stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        ImmutableMap<K, V> tmpMap;
        int valueCount;
        stream.defaultReadObject();
        int keyCount = stream.readInt();
        if (keyCount < 0) {
            throw new InvalidObjectException((String)("Invalid key count " + keyCount));
        }
        ImmutableMap.Builder<Object, ImmutableCollection> builder = ImmutableMap.builder();
        int tmpSize = 0;
        for (int i = 0; i < keyCount; tmpSize += valueCount, ++i) {
            Object key = stream.readObject();
            valueCount = stream.readInt();
            if (valueCount <= 0) {
                throw new InvalidObjectException((String)("Invalid value count " + valueCount));
            }
            ImmutableList.Builder<E> valuesBuilder = ImmutableList.builder();
            for (int j = 0; j < valueCount; ++j) {
                valuesBuilder.add((Object)stream.readObject());
            }
            builder.put(key, valuesBuilder.build());
        }
        try {
            tmpMap = builder.build();
        }
        catch (IllegalArgumentException e) {
            throw (InvalidObjectException)new InvalidObjectException((String)e.getMessage()).initCause((Throwable)e);
        }
        ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set((ImmutableMultimap)this, tmpMap);
        ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set((ImmutableMultimap)this, (int)tmpSize);
    }
}

