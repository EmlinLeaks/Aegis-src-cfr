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
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.EmptyImmutableSetMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Serialization;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public class ImmutableSetMultimap<K, V>
extends ImmutableMultimap<K, V>
implements SetMultimap<K, V> {
    private final transient ImmutableSet<V> emptySet;
    @LazyInit
    @RetainedWith
    private transient ImmutableSetMultimap<V, K> inverse;
    private transient ImmutableSet<Map.Entry<K, V>> entries;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <K, V> ImmutableSetMultimap<K, V> of() {
        return EmptyImmutableSetMultimap.INSTANCE;
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1) {
        Builder<K, V> builder = ImmutableSetMultimap.builder();
        builder.put(k1, v1);
        return builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2) {
        Builder<K, V> builder = ImmutableSetMultimap.builder();
        builder.put(k1, v1);
        builder.put(k2, v2);
        return builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Builder<K, V> builder = ImmutableSetMultimap.builder();
        builder.put(k1, v1);
        builder.put(k2, v2);
        builder.put(k3, v3);
        return builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Builder<K, V> builder = ImmutableSetMultimap.builder();
        builder.put(k1, v1);
        builder.put(k2, v2);
        builder.put(k3, v3);
        builder.put(k4, v4);
        return builder.build();
    }

    public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Builder<K, V> builder = ImmutableSetMultimap.builder();
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

    public static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
        return ImmutableSetMultimap.copyOf(multimap, null);
    }

    private static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap, Comparator<? super V> valueComparator) {
        ImmutableSetMultimap kvMultimap;
        Preconditions.checkNotNull(multimap);
        if (multimap.isEmpty() && valueComparator == null) {
            return ImmutableSetMultimap.of();
        }
        if (multimap instanceof ImmutableSetMultimap && !(kvMultimap = (ImmutableSetMultimap)multimap).isPartialView()) {
            return kvMultimap;
        }
        ImmutableMap.Builder<K, ImmutableSet<V>> builder = new ImmutableMap.Builder<K, ImmutableSet<V>>((int)multimap.asMap().size());
        int size = 0;
        Iterator<Map.Entry<K, Collection<V>>> i$ = multimap.asMap().entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, Collection<V>> entry = i$.next();
            K key = entry.getKey();
            Collection<? extends V> values = entry.getValue();
            ImmutableSet<V> set = ImmutableSetMultimap.valueSet(valueComparator, values);
            if (set.isEmpty()) continue;
            builder.put(key, set);
            size += set.size();
        }
        return new ImmutableSetMultimap<K, V>(builder.build(), (int)size, valueComparator);
    }

    @Beta
    public static <K, V> ImmutableSetMultimap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
        return ((Builder)new Builder<K, V>().putAll(entries)).build();
    }

    ImmutableSetMultimap(ImmutableMap<K, ImmutableSet<V>> map, int size, @Nullable Comparator<? super V> valueComparator) {
        super(map, (int)size);
        this.emptySet = ImmutableSetMultimap.emptySet(valueComparator);
    }

    @Override
    public ImmutableSet<V> get(@Nullable K key) {
        ImmutableSet set = (ImmutableSet)this.map.get(key);
        return MoreObjects.firstNonNull(set, this.emptySet);
    }

    @Override
    public ImmutableSetMultimap<V, K> inverse() {
        ImmutableSetMultimap<V, K> immutableSetMultimap;
        ImmutableSetMultimap<V, K> result = this.inverse;
        if (result == null) {
            immutableSetMultimap = this.inverse = this.invert();
            return immutableSetMultimap;
        }
        immutableSetMultimap = result;
        return immutableSetMultimap;
    }

    private ImmutableSetMultimap<V, K> invert() {
        Builder<K, V> builder = ImmutableSetMultimap.builder();
        Iterator i$ = ((ImmutableSet)this.entries()).iterator();
        do {
            if (!i$.hasNext()) {
                ImmutableMultimap invertedMultimap = builder.build();
                ((ImmutableSetMultimap)invertedMultimap).inverse = this;
                return invertedMultimap;
            }
            Map.Entry entry = (Map.Entry)i$.next();
            builder.put(entry.getValue(), entry.getKey());
        } while (true);
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public ImmutableSet<V> removeAll(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public ImmutableSet<V> replaceValues(K key, Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableSet<Map.Entry<K, V>> entries() {
        ImmutableSet<Map.Entry<K, V>> immutableSet;
        ImmutableSet<Map.Entry<K, V>> result = this.entries;
        if (result == null) {
            immutableSet = this.entries = new EntrySet<K, V>(this);
            return immutableSet;
        }
        immutableSet = result;
        return immutableSet;
    }

    private static <V> ImmutableSet<V> valueSet(@Nullable Comparator<? super V> valueComparator, Collection<? extends V> values) {
        ImmutableSet<? extends V> immutableSet;
        if (valueComparator == null) {
            immutableSet = ImmutableSet.copyOf(values);
            return immutableSet;
        }
        immutableSet = ImmutableSortedSet.copyOf(valueComparator, values);
        return immutableSet;
    }

    private static <V> ImmutableSet<V> emptySet(@Nullable Comparator<? super V> valueComparator) {
        ImmutableSet<E> immutableSet;
        if (valueComparator == null) {
            immutableSet = ImmutableSet.of();
            return immutableSet;
        }
        immutableSet = ImmutableSortedSet.emptySet(valueComparator);
        return immutableSet;
    }

    private static <V> ImmutableSet.Builder<V> valuesBuilder(@Nullable Comparator<? super V> valueComparator) {
        ImmutableSet.Builder<E> builder;
        if (valueComparator == null) {
            builder = new ImmutableSet.Builder<E>();
            return builder;
        }
        builder = new ImmutableSortedSet.Builder<V>(valueComparator);
        return builder;
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.valueComparator());
        Serialization.writeMultimap(this, (ObjectOutputStream)stream);
    }

    @Nullable
    Comparator<? super V> valueComparator() {
        if (!(this.emptySet instanceof ImmutableSortedSet)) return null;
        Comparator<E> comparator = ((ImmutableSortedSet)this.emptySet).comparator();
        return comparator;
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int valueCount;
        ImmutableMap<K, V> tmpMap;
        stream.defaultReadObject();
        Comparator valueComparator = (Comparator)stream.readObject();
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
            ImmutableSet.Builder<V> valuesBuilder = ImmutableSetMultimap.valuesBuilder(valueComparator);
            for (int j = 0; j < valueCount; ++j) {
                valuesBuilder.add((Object)stream.readObject());
            }
            ImmutableCollection valueSet = valuesBuilder.build();
            if (valueSet.size() != valueCount) {
                throw new InvalidObjectException((String)("Duplicate key-value pairs exist for key " + key));
            }
            builder.put(key, valueSet);
        }
        try {
            tmpMap = builder.build();
        }
        catch (IllegalArgumentException e) {
            throw (InvalidObjectException)new InvalidObjectException((String)e.getMessage()).initCause((Throwable)e);
        }
        ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set((ImmutableMultimap)this, tmpMap);
        ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set((ImmutableMultimap)this, (int)tmpSize);
        ImmutableMultimap.FieldSettersHolder.EMPTY_SET_FIELD_SETTER.set((ImmutableSetMultimap)this, ImmutableSetMultimap.emptySet(valueComparator));
    }

    static /* synthetic */ ImmutableSetMultimap access$000(Multimap x0, Comparator x1) {
        return ImmutableSetMultimap.copyOf(x0, x1);
    }
}

