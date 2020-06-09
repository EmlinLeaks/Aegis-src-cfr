/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Hashing;
import com.google.common.collect.Maps;
import com.google.common.collect.Serialization;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class HashBiMap<K, V>
extends Maps.IteratorBasedAbstractMap<K, V>
implements BiMap<K, V>,
Serializable {
    private static final double LOAD_FACTOR = 1.0;
    private transient BiEntry<K, V>[] hashTableKToV;
    private transient BiEntry<K, V>[] hashTableVToK;
    private transient BiEntry<K, V> firstInKeyInsertionOrder;
    private transient BiEntry<K, V> lastInKeyInsertionOrder;
    private transient int size;
    private transient int mask;
    private transient int modCount;
    @RetainedWith
    private transient BiMap<V, K> inverse;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <K, V> HashBiMap<K, V> create() {
        return HashBiMap.create((int)16);
    }

    public static <K, V> HashBiMap<K, V> create(int expectedSize) {
        return new HashBiMap<K, V>((int)expectedSize);
    }

    public static <K, V> HashBiMap<K, V> create(Map<? extends K, ? extends V> map) {
        HashBiMap<K, V> bimap = HashBiMap.create((int)map.size());
        bimap.putAll(map);
        return bimap;
    }

    private HashBiMap(int expectedSize) {
        this.init((int)expectedSize);
    }

    private void init(int expectedSize) {
        CollectPreconditions.checkNonnegative((int)expectedSize, (String)"expectedSize");
        int tableSize = Hashing.closedTableSize((int)expectedSize, (double)1.0);
        this.hashTableKToV = this.createTable((int)tableSize);
        this.hashTableVToK = this.createTable((int)tableSize);
        this.firstInKeyInsertionOrder = null;
        this.lastInKeyInsertionOrder = null;
        this.size = 0;
        this.mask = tableSize - 1;
        this.modCount = 0;
    }

    private void delete(BiEntry<K, V> entry) {
        int keyBucket = entry.keyHash & this.mask;
        BiEntry<K, V> prevBucketEntry = null;
        BiEntry<K, V> bucketEntry = this.hashTableKToV[keyBucket];
        do {
            if (bucketEntry == entry) {
                if (prevBucketEntry == null) {
                    this.hashTableKToV[keyBucket] = entry.nextInKToVBucket;
                    break;
                }
                prevBucketEntry.nextInKToVBucket = entry.nextInKToVBucket;
                break;
            }
            prevBucketEntry = bucketEntry;
            bucketEntry = bucketEntry.nextInKToVBucket;
        } while (true);
        int valueBucket = entry.valueHash & this.mask;
        prevBucketEntry = null;
        BiEntry<K, V> bucketEntry2 = this.hashTableVToK[valueBucket];
        do {
            if (bucketEntry2 == entry) {
                if (prevBucketEntry == null) {
                    this.hashTableVToK[valueBucket] = entry.nextInVToKBucket;
                    break;
                }
                prevBucketEntry.nextInVToKBucket = entry.nextInVToKBucket;
                break;
            }
            prevBucketEntry = bucketEntry2;
            bucketEntry2 = bucketEntry2.nextInVToKBucket;
        } while (true);
        if (entry.prevInKeyInsertionOrder == null) {
            this.firstInKeyInsertionOrder = entry.nextInKeyInsertionOrder;
        } else {
            entry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = entry.nextInKeyInsertionOrder;
        }
        if (entry.nextInKeyInsertionOrder == null) {
            this.lastInKeyInsertionOrder = entry.prevInKeyInsertionOrder;
        } else {
            entry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = entry.prevInKeyInsertionOrder;
        }
        --this.size;
        ++this.modCount;
    }

    private void insert(BiEntry<K, V> entry, @Nullable BiEntry<K, V> oldEntryForKey) {
        int keyBucket = entry.keyHash & this.mask;
        entry.nextInKToVBucket = this.hashTableKToV[keyBucket];
        this.hashTableKToV[keyBucket] = entry;
        int valueBucket = entry.valueHash & this.mask;
        entry.nextInVToKBucket = this.hashTableVToK[valueBucket];
        this.hashTableVToK[valueBucket] = entry;
        if (oldEntryForKey == null) {
            entry.prevInKeyInsertionOrder = this.lastInKeyInsertionOrder;
            entry.nextInKeyInsertionOrder = null;
            if (this.lastInKeyInsertionOrder == null) {
                this.firstInKeyInsertionOrder = entry;
            } else {
                this.lastInKeyInsertionOrder.nextInKeyInsertionOrder = entry;
            }
            this.lastInKeyInsertionOrder = entry;
        } else {
            entry.prevInKeyInsertionOrder = oldEntryForKey.prevInKeyInsertionOrder;
            if (entry.prevInKeyInsertionOrder == null) {
                this.firstInKeyInsertionOrder = entry;
            } else {
                entry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = entry;
            }
            entry.nextInKeyInsertionOrder = oldEntryForKey.nextInKeyInsertionOrder;
            if (entry.nextInKeyInsertionOrder == null) {
                this.lastInKeyInsertionOrder = entry;
            } else {
                entry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = entry;
            }
        }
        ++this.size;
        ++this.modCount;
    }

    private BiEntry<K, V> seekByKey(@Nullable Object key, int keyHash) {
        BiEntry<K, V> entry = this.hashTableKToV[keyHash & this.mask];
        while (entry != null) {
            if (keyHash == entry.keyHash && Objects.equal((Object)key, (Object)entry.key)) {
                return entry;
            }
            entry = entry.nextInKToVBucket;
        }
        return null;
    }

    private BiEntry<K, V> seekByValue(@Nullable Object value, int valueHash) {
        BiEntry<K, V> entry = this.hashTableVToK[valueHash & this.mask];
        while (entry != null) {
            if (valueHash == entry.valueHash && Objects.equal((Object)value, (Object)entry.value)) {
                return entry;
            }
            entry = entry.nextInVToKBucket;
        }
        return null;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (this.seekByKey((Object)key, (int)Hashing.smearedHash((Object)key)) == null) return false;
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        if (this.seekByValue((Object)value, (int)Hashing.smearedHash((Object)value)) == null) return false;
        return true;
    }

    @Nullable
    @Override
    public V get(@Nullable Object key) {
        return (V)Maps.valueOrNull(this.seekByKey((Object)key, (int)Hashing.smearedHash((Object)key)));
    }

    @CanIgnoreReturnValue
    @Override
    public V put(@Nullable K key, @Nullable V value) {
        return (V)this.put(key, value, (boolean)false);
    }

    @CanIgnoreReturnValue
    @Override
    public V forcePut(@Nullable K key, @Nullable V value) {
        return (V)this.put(key, value, (boolean)true);
    }

    private V put(@Nullable K key, @Nullable V value, boolean force) {
        int keyHash = Hashing.smearedHash(key);
        int valueHash = Hashing.smearedHash(value);
        BiEntry<K, V> oldEntryForKey = this.seekByKey(key, (int)keyHash);
        if (oldEntryForKey != null && valueHash == oldEntryForKey.valueHash && Objects.equal(value, (Object)oldEntryForKey.value)) {
            return (V)value;
        }
        BiEntry<K, V> oldEntryForValue = this.seekByValue(value, (int)valueHash);
        if (oldEntryForValue != null) {
            if (!force) throw new IllegalArgumentException((String)("value already present: " + value));
            this.delete(oldEntryForValue);
        }
        BiEntry<K, V> newEntry = new BiEntry<K, V>(key, (int)keyHash, value, (int)valueHash);
        if (oldEntryForKey != null) {
            this.delete(oldEntryForKey);
            this.insert(newEntry, oldEntryForKey);
            oldEntryForKey.prevInKeyInsertionOrder = null;
            oldEntryForKey.nextInKeyInsertionOrder = null;
            this.rehashIfNecessary();
            return (V)oldEntryForKey.value;
        }
        this.insert(newEntry, null);
        this.rehashIfNecessary();
        return (V)null;
    }

    @Nullable
    private K putInverse(@Nullable V value, @Nullable K key, boolean force) {
        int valueHash = Hashing.smearedHash(value);
        int keyHash = Hashing.smearedHash(key);
        BiEntry<K, V> oldEntryForValue = this.seekByValue(value, (int)valueHash);
        if (oldEntryForValue != null && keyHash == oldEntryForValue.keyHash && Objects.equal(key, (Object)oldEntryForValue.key)) {
            return (K)key;
        }
        BiEntry<K, V> oldEntryForKey = this.seekByKey(key, (int)keyHash);
        if (oldEntryForKey != null) {
            if (!force) throw new IllegalArgumentException((String)("value already present: " + key));
            this.delete(oldEntryForKey);
        }
        if (oldEntryForValue != null) {
            this.delete(oldEntryForValue);
        }
        BiEntry<K, V> newEntry = new BiEntry<K, V>(key, (int)keyHash, value, (int)valueHash);
        this.insert(newEntry, oldEntryForKey);
        if (oldEntryForKey != null) {
            oldEntryForKey.prevInKeyInsertionOrder = null;
            oldEntryForKey.nextInKeyInsertionOrder = null;
        }
        this.rehashIfNecessary();
        return (K)Maps.keyOrNull(oldEntryForValue);
    }

    private void rehashIfNecessary() {
        BiEntry<K, V>[] oldKToV = this.hashTableKToV;
        if (!Hashing.needsResizing((int)this.size, (int)oldKToV.length, (double)1.0)) return;
        int newTableSize = oldKToV.length * 2;
        this.hashTableKToV = this.createTable((int)newTableSize);
        this.hashTableVToK = this.createTable((int)newTableSize);
        this.mask = newTableSize - 1;
        this.size = 0;
        BiEntry<K, V> entry = this.firstInKeyInsertionOrder;
        do {
            if (entry == null) {
                ++this.modCount;
                return;
            }
            this.insert(entry, entry);
            entry = entry.nextInKeyInsertionOrder;
        } while (true);
    }

    private BiEntry<K, V>[] createTable(int length) {
        return new BiEntry[length];
    }

    @CanIgnoreReturnValue
    @Override
    public V remove(@Nullable Object key) {
        BiEntry<K, V> entry = this.seekByKey((Object)key, (int)Hashing.smearedHash((Object)key));
        if (entry == null) {
            return (V)null;
        }
        this.delete(entry);
        entry.prevInKeyInsertionOrder = null;
        entry.nextInKeyInsertionOrder = null;
        return (V)entry.value;
    }

    @Override
    public void clear() {
        this.size = 0;
        Arrays.fill((Object[])this.hashTableKToV, null);
        Arrays.fill((Object[])this.hashTableVToK, null);
        this.firstInKeyInsertionOrder = null;
        this.lastInKeyInsertionOrder = null;
        ++this.modCount;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Set<K> keySet() {
        return new KeySet((HashBiMap)this);
    }

    @Override
    public Set<V> values() {
        return this.inverse().keySet();
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new HashBiMap<K, V>((HashBiMap)this){
            final /* synthetic */ HashBiMap this$0;
            {
                this.this$0 = hashBiMap;
                super((HashBiMap)hashBiMap);
            }

            Map.Entry<K, V> output(BiEntry<K, V> entry) {
                return new com.google.common.collect.HashBiMap$1$MapEntry(this, entry);
            }
        };
    }

    @Override
    public BiMap<V, K> inverse() {
        Inverse inverse;
        if (this.inverse == null) {
            inverse = this.inverse = new Inverse((HashBiMap)this, null);
            return inverse;
        }
        inverse = this.inverse;
        return inverse;
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        Serialization.writeMap(this, (ObjectOutputStream)stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.init((int)16);
        int size = Serialization.readCount((ObjectInputStream)stream);
        Serialization.populateMap(this, (ObjectInputStream)stream, (int)size);
    }

    static /* synthetic */ BiEntry access$000(HashBiMap x0) {
        return x0.firstInKeyInsertionOrder;
    }

    static /* synthetic */ int access$100(HashBiMap x0) {
        return x0.modCount;
    }

    static /* synthetic */ void access$200(HashBiMap x0, BiEntry x1) {
        x0.delete(x1);
    }

    static /* synthetic */ BiEntry access$300(HashBiMap x0, Object x1, int x2) {
        return x0.seekByKey((Object)x1, (int)x2);
    }

    static /* synthetic */ BiEntry access$400(HashBiMap x0, Object x1, int x2) {
        return x0.seekByValue((Object)x1, (int)x2);
    }

    static /* synthetic */ void access$500(HashBiMap x0, BiEntry x1, BiEntry x2) {
        x0.insert(x1, x2);
    }

    static /* synthetic */ int access$700(HashBiMap x0) {
        return x0.size;
    }

    static /* synthetic */ Object access$800(HashBiMap x0, Object x1, Object x2, boolean x3) {
        return x0.putInverse(x1, x2, (boolean)x3);
    }
}

