/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.RegularImmutableBiMap;
import com.google.common.collect.RegularImmutableMap;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
class RegularImmutableBiMap<K, V>
extends ImmutableBiMap<K, V> {
    static final RegularImmutableBiMap<Object, Object> EMPTY = new RegularImmutableBiMap<?, ?>(null, null, ImmutableMap.EMPTY_ENTRY_ARRAY, (int)0, (int)0);
    static final double MAX_LOAD_FACTOR = 1.2;
    private final transient ImmutableMapEntry<K, V>[] keyTable;
    private final transient ImmutableMapEntry<K, V>[] valueTable;
    private final transient Map.Entry<K, V>[] entries;
    private final transient int mask;
    private final transient int hashCode;
    @LazyInit
    @RetainedWith
    private transient ImmutableBiMap<V, K> inverse;

    static <K, V> RegularImmutableBiMap<K, V> fromEntries(Map.Entry<K, V> ... entries) {
        return RegularImmutableBiMap.fromEntryArray((int)entries.length, entries);
    }

    static <K, V> RegularImmutableBiMap<K, V> fromEntryArray(int n, Map.Entry<K, V>[] entryArray) {
        Preconditions.checkPositionIndex((int)n, (int)entryArray.length);
        int tableSize = Hashing.closedTableSize((int)n, (double)1.2);
        int mask = tableSize - 1;
        ImmutableMapEntry<K, V>[] keyTable = ImmutableMapEntry.createEntryArray((int)tableSize);
        ImmutableMapEntry<K, V>[] valueTable = ImmutableMapEntry.createEntryArray((int)tableSize);
        Map.Entry<K, V>[] entries = n == entryArray.length ? entryArray : ImmutableMapEntry.createEntryArray((int)n);
        int hashCode = 0;
        int i = 0;
        while (i < n) {
            ImmutableMapEntry newEntry;
            Map.Entry<K, V> entry = entryArray[i];
            K key = entry.getKey();
            V value = entry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            int keyHash = key.hashCode();
            int valueHash = value.hashCode();
            int keyBucket = Hashing.smear((int)keyHash) & mask;
            int valueBucket = Hashing.smear((int)valueHash) & mask;
            ImmutableMapEntry<K, V> nextInKeyBucket = keyTable[keyBucket];
            RegularImmutableMap.checkNoConflictInKeyBucket(key, entry, nextInKeyBucket);
            ImmutableMapEntry<K, V> nextInValueBucket = valueTable[valueBucket];
            RegularImmutableBiMap.checkNoConflictInValueBucket(value, entry, nextInValueBucket);
            if (nextInValueBucket == null && nextInKeyBucket == null) {
                boolean reusable = entry instanceof ImmutableMapEntry && ((ImmutableMapEntry)entry).isReusable();
                newEntry = reusable ? (ImmutableMapEntry<K, V>)entry : new ImmutableMapEntry<K, V>(key, value);
            } else {
                newEntry = new ImmutableMapEntry.NonTerminalImmutableBiMapEntry<K, V>(key, value, nextInKeyBucket, nextInValueBucket);
            }
            keyTable[keyBucket] = newEntry;
            valueTable[valueBucket] = newEntry;
            entries[i] = newEntry;
            hashCode += keyHash ^ valueHash;
            ++i;
        }
        return new RegularImmutableBiMap<K, V>(keyTable, valueTable, entries, (int)mask, (int)hashCode);
    }

    private RegularImmutableBiMap(ImmutableMapEntry<K, V>[] keyTable, ImmutableMapEntry<K, V>[] valueTable, Map.Entry<K, V>[] entries, int mask, int hashCode) {
        this.keyTable = keyTable;
        this.valueTable = valueTable;
        this.entries = entries;
        this.mask = mask;
        this.hashCode = hashCode;
    }

    private static void checkNoConflictInValueBucket(Object value, Map.Entry<?, ?> entry, @Nullable ImmutableMapEntry<?, ?> valueBucketHead) {
        while (valueBucketHead != null) {
            RegularImmutableBiMap.checkNoConflict((boolean)(!value.equals(valueBucketHead.getValue())), (String)"value", entry, valueBucketHead);
            valueBucketHead = valueBucketHead.getNextInValueBucket();
        }
    }

    @Nullable
    @Override
    public V get(@Nullable Object key) {
        V v;
        if (this.keyTable == null) {
            v = null;
            return (V)((V)v);
        }
        v = (V)RegularImmutableMap.get((Object)key, this.keyTable, (int)this.mask);
        return (V)v;
    }

    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        ImmutableMapEntrySet.RegularEntrySet<K, V> regularEntrySet;
        if (this.isEmpty()) {
            regularEntrySet = ImmutableSet.of();
            return regularEntrySet;
        }
        regularEntrySet = new ImmutableMapEntrySet.RegularEntrySet<K, V>(this, this.entries);
        return regularEntrySet;
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public int size() {
        return this.entries.length;
    }

    @Override
    public ImmutableBiMap<V, K> inverse() {
        Inverse inverse;
        if (this.isEmpty()) {
            return ImmutableBiMap.of();
        }
        Inverse result = this.inverse;
        if (result == null) {
            inverse = this.inverse = new Inverse((RegularImmutableBiMap)this, null);
            return inverse;
        }
        inverse = result;
        return inverse;
    }

    static /* synthetic */ ImmutableMapEntry[] access$100(RegularImmutableBiMap x0) {
        return x0.valueTable;
    }

    static /* synthetic */ int access$200(RegularImmutableBiMap x0) {
        return x0.mask;
    }

    static /* synthetic */ int access$300(RegularImmutableBiMap x0) {
        return x0.hashCode;
    }

    static /* synthetic */ Map.Entry[] access$400(RegularImmutableBiMap x0) {
        return x0.entries;
    }
}

