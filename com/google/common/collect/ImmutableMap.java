/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableEnumMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapKeySet;
import com.google.common.collect.ImmutableMapValues;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableMap<K, V>
implements Map<K, V>,
Serializable {
    static final Map.Entry<?, ?>[] EMPTY_ENTRY_ARRAY = new Map.Entry[0];
    @LazyInit
    private transient ImmutableSet<Map.Entry<K, V>> entrySet;
    @LazyInit
    private transient ImmutableSet<K> keySet;
    @LazyInit
    private transient ImmutableCollection<V> values;
    @LazyInit
    private transient ImmutableSetMultimap<K, V> multimapView;

    public static <K, V> ImmutableMap<K, V> of() {
        return ImmutableBiMap.of();
    }

    public static <K, V> ImmutableMap<K, V> of(K k1, V v1) {
        return ImmutableBiMap.of(k1, v1);
    }

    public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
        return RegularImmutableMap.fromEntries(ImmutableMap.entryOf(k1, v1), ImmutableMap.entryOf(k2, v2));
    }

    public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return RegularImmutableMap.fromEntries(ImmutableMap.entryOf(k1, v1), ImmutableMap.entryOf(k2, v2), ImmutableMap.entryOf(k3, v3));
    }

    public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return RegularImmutableMap.fromEntries(ImmutableMap.entryOf(k1, v1), ImmutableMap.entryOf(k2, v2), ImmutableMap.entryOf(k3, v3), ImmutableMap.entryOf(k4, v4));
    }

    public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return RegularImmutableMap.fromEntries(ImmutableMap.entryOf(k1, v1), ImmutableMap.entryOf(k2, v2), ImmutableMap.entryOf(k3, v3), ImmutableMap.entryOf(k4, v4), ImmutableMap.entryOf(k5, v5));
    }

    static <K, V> ImmutableMapEntry<K, V> entryOf(K key, V value) {
        return new ImmutableMapEntry<K, V>(key, value);
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }

    static void checkNoConflict(boolean safe, String conflictDescription, Map.Entry<?, ?> entry1, Map.Entry<?, ?> entry2) {
        if (safe) return;
        throw new IllegalArgumentException((String)("Multiple entries with same " + conflictDescription + ": " + entry1 + " and " + entry2));
    }

    public static <K, V> ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
        if (map instanceof ImmutableMap && !(map instanceof ImmutableSortedMap)) {
            ImmutableMap kvMap = (ImmutableMap)map;
            if (kvMap.isPartialView()) return ImmutableMap.copyOf(map.entrySet());
            return kvMap;
        }
        if (!(map instanceof EnumMap)) return ImmutableMap.copyOf(map.entrySet());
        return ImmutableMap.copyOfEnumMap((EnumMap)map);
    }

    @Beta
    public static <K, V> ImmutableMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
        Map.Entry<?, ?>[] entryArray = Iterables.toArray(entries, EMPTY_ENTRY_ARRAY);
        switch (entryArray.length) {
            case 0: {
                return ImmutableMap.of();
            }
            case 1: {
                Map.Entry<?, ?> onlyEntry = entryArray[0];
                return ImmutableMap.of(onlyEntry.getKey(), onlyEntry.getValue());
            }
        }
        return RegularImmutableMap.fromEntries(entryArray);
    }

    private static <K extends Enum<K>, V> ImmutableMap<K, V> copyOfEnumMap(EnumMap<K, ? extends V> original) {
        EnumMap<K, V> copy = new EnumMap<K, V>(original);
        Iterator<Map.Entry<K, V>> i$ = copy.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            CollectPreconditions.checkEntryNotNull(entry.getKey(), entry.getValue());
        }
        return ImmutableEnumMap.asImmutable(copy);
    }

    ImmutableMap() {
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V put(K k, V v) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @CanIgnoreReturnValue
    @Override
    public final V remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        if (this.size() != 0) return false;
        return true;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (this.get((Object)key) == null) return false;
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return ((ImmutableCollection)this.values()).contains((Object)value);
    }

    @Override
    public abstract V get(@Nullable Object var1);

    @Override
    public ImmutableSet<Map.Entry<K, V>> entrySet() {
        ImmutableSet<Map.Entry<K, V>> immutableSet;
        ImmutableSet<Map.Entry<K, V>> result = this.entrySet;
        if (result == null) {
            immutableSet = this.entrySet = this.createEntrySet();
            return immutableSet;
        }
        immutableSet = result;
        return immutableSet;
    }

    abstract ImmutableSet<Map.Entry<K, V>> createEntrySet();

    @Override
    public ImmutableSet<K> keySet() {
        ImmutableSet<K> immutableSet;
        ImmutableSet<K> result = this.keySet;
        if (result == null) {
            immutableSet = this.keySet = this.createKeySet();
            return immutableSet;
        }
        immutableSet = result;
        return immutableSet;
    }

    ImmutableSet<K> createKeySet() {
        ImmutableMapKeySet<K, V> immutableMapKeySet;
        if (this.isEmpty()) {
            immutableMapKeySet = ImmutableSet.of();
            return immutableMapKeySet;
        }
        immutableMapKeySet = new ImmutableMapKeySet<K, V>(this);
        return immutableMapKeySet;
    }

    UnmodifiableIterator<K> keyIterator() {
        Iterator entryIterator = ((ImmutableSet)this.entrySet()).iterator();
        return new UnmodifiableIterator<K>((ImmutableMap)this, (UnmodifiableIterator)entryIterator){
            final /* synthetic */ UnmodifiableIterator val$entryIterator;
            final /* synthetic */ ImmutableMap this$0;
            {
                this.this$0 = immutableMap;
                this.val$entryIterator = unmodifiableIterator;
            }

            public boolean hasNext() {
                return this.val$entryIterator.hasNext();
            }

            public K next() {
                return (K)((Map.Entry)this.val$entryIterator.next()).getKey();
            }
        };
    }

    @Override
    public ImmutableCollection<V> values() {
        ImmutableCollection<V> immutableCollection;
        ImmutableCollection<V> result = this.values;
        if (result == null) {
            immutableCollection = this.values = this.createValues();
            return immutableCollection;
        }
        immutableCollection = result;
        return immutableCollection;
    }

    ImmutableCollection<V> createValues() {
        return new ImmutableMapValues<K, V>(this);
    }

    public ImmutableSetMultimap<K, V> asMultimap() {
        ImmutableSetMultimap<K, V> immutableSetMultimap;
        if (this.isEmpty()) {
            return ImmutableSetMultimap.of();
        }
        ImmutableSetMultimap<K, V> result = this.multimapView;
        if (result == null) {
            immutableSetMultimap = this.multimapView = new ImmutableSetMultimap<K, V>(new MapViewOfValuesAsSingletonSets((ImmutableMap)this, null), (int)this.size(), null);
            return immutableSetMultimap;
        }
        immutableSetMultimap = result;
        return immutableSetMultimap;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return Maps.equalsImpl(this, (Object)object);
    }

    abstract boolean isPartialView();

    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }

    boolean isHashCodeFast() {
        return false;
    }

    public String toString() {
        return Maps.toStringImpl(this);
    }

    Object writeReplace() {
        return new SerializedForm(this);
    }
}

