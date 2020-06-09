/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.RegularImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true, emulated=true)
final class RegularImmutableMap<K, V>
extends ImmutableMap<K, V> {
    private final transient Map.Entry<K, V>[] entries;
    private final transient ImmutableMapEntry<K, V>[] table;
    private final transient int mask;
    private static final double MAX_LOAD_FACTOR = 1.2;
    private static final long serialVersionUID = 0L;

    static <K, V> RegularImmutableMap<K, V> fromEntries(Map.Entry<K, V> ... entries) {
        return RegularImmutableMap.fromEntryArray((int)entries.length, entries);
    }

    static <K, V> RegularImmutableMap<K, V> fromEntryArray(int n, Map.Entry<K, V>[] entryArray) {
        Preconditions.checkPositionIndex((int)n, (int)entryArray.length);
        Map.Entry<K, V>[] entries = n == entryArray.length ? entryArray : ImmutableMapEntry.createEntryArray((int)n);
        int tableSize = Hashing.closedTableSize((int)n, (double)1.2);
        ImmutableMapEntry<K, V>[] table = ImmutableMapEntry.createEntryArray((int)tableSize);
        int mask = tableSize - 1;
        int entryIndex = 0;
        while (entryIndex < n) {
            ImmutableMapEntry newEntry;
            Map.Entry<K, V> entry = entryArray[entryIndex];
            K key = entry.getKey();
            V value = entry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            int tableIndex = Hashing.smear((int)key.hashCode()) & mask;
            ImmutableMapEntry<K, V> existing = table[tableIndex];
            if (existing == null) {
                boolean reusable = entry instanceof ImmutableMapEntry && ((ImmutableMapEntry)entry).isReusable();
                newEntry = reusable ? (ImmutableMapEntry<K, V>)entry : new ImmutableMapEntry<K, V>(key, value);
            } else {
                newEntry = new ImmutableMapEntry.NonTerminalImmutableMapEntry<K, V>(key, value, existing);
            }
            table[tableIndex] = newEntry;
            entries[entryIndex] = newEntry;
            RegularImmutableMap.checkNoConflictInKeyBucket(key, newEntry, existing);
            ++entryIndex;
        }
        return new RegularImmutableMap<K, V>(entries, table, (int)mask);
    }

    private RegularImmutableMap(Map.Entry<K, V>[] entries, ImmutableMapEntry<K, V>[] table, int mask) {
        this.entries = entries;
        this.table = table;
        this.mask = mask;
    }

    static void checkNoConflictInKeyBucket(Object key, Map.Entry<?, ?> entry, @Nullable ImmutableMapEntry<?, ?> keyBucketHead) {
        while (keyBucketHead != null) {
            RegularImmutableMap.checkNoConflict((boolean)(!key.equals(keyBucketHead.getKey())), (String)"key", entry, keyBucketHead);
            keyBucketHead = keyBucketHead.getNextInKeyBucket();
        }
    }

    @Override
    public V get(@Nullable Object key) {
        return (V)RegularImmutableMap.get((Object)key, this.table, (int)this.mask);
    }

    @Nullable
    static <V> V get(@Nullable Object key, ImmutableMapEntry<?, V>[] keyTable, int mask) {
        if (key == null) {
            return (V)null;
        }
        int index = Hashing.smear((int)key.hashCode()) & mask;
        ImmutableMapEntry<?, V> entry = keyTable[index];
        while (entry != null) {
            K candidateKey = entry.getKey();
            if (key.equals(candidateKey)) {
                return (V)entry.getValue();
            }
            entry = entry.getNextInKeyBucket();
        }
        return (V)null;
    }

    @Override
    public int size() {
        return this.entries.length;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return new ImmutableMapEntrySet.RegularEntrySet<K, V>(this, this.entries);
    }

    @Override
    ImmutableSet<K> createKeySet() {
        return new KeySet<K, V>(this);
    }

    @Override
    ImmutableCollection<V> createValues() {
        return new Values<K, V>(this);
    }

    static /* synthetic */ Map.Entry[] access$000(RegularImmutableMap x0) {
        return x0.entries;
    }
}

