/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@GwtCompatible
public final class AtomicLongMap<K> {
    private final ConcurrentHashMap<K, AtomicLong> map;
    private transient Map<K, Long> asMap;

    private AtomicLongMap(ConcurrentHashMap<K, AtomicLong> map) {
        this.map = Preconditions.checkNotNull(map);
    }

    public static <K> AtomicLongMap<K> create() {
        return new AtomicLongMap<K>(new ConcurrentHashMap<K, V>());
    }

    public static <K> AtomicLongMap<K> create(Map<? extends K, ? extends Long> m) {
        AtomicLongMap<? extends K> result = AtomicLongMap.create();
        result.putAll(m);
        return result;
    }

    public long get(K key) {
        AtomicLong atomic = this.map.get(key);
        if (atomic == null) {
            return 0L;
        }
        long l = atomic.get();
        return l;
    }

    @CanIgnoreReturnValue
    public long incrementAndGet(K key) {
        return this.addAndGet(key, (long)1L);
    }

    @CanIgnoreReturnValue
    public long decrementAndGet(K key) {
        return this.addAndGet(key, (long)-1L);
    }

    @CanIgnoreReturnValue
    public long addAndGet(K key, long delta) {
        long newValue;
        block0 : do {
            long oldValue;
            AtomicLong atomic;
            if ((atomic = this.map.get(key)) == null && (atomic = this.map.putIfAbsent(key, (AtomicLong)new AtomicLong((long)delta))) == null) {
                return delta;
            }
            do {
                if ((oldValue = atomic.get()) != 0L) continue;
                if (!this.map.replace(key, (AtomicLong)atomic, (AtomicLong)new AtomicLong((long)delta))) continue block0;
                return delta;
            } while (!atomic.compareAndSet((long)oldValue, (long)(newValue = oldValue + delta)));
            break;
        } while (true);
        return newValue;
    }

    @CanIgnoreReturnValue
    public long getAndIncrement(K key) {
        return this.getAndAdd(key, (long)1L);
    }

    @CanIgnoreReturnValue
    public long getAndDecrement(K key) {
        return this.getAndAdd(key, (long)-1L);
    }

    @CanIgnoreReturnValue
    public long getAndAdd(K key, long delta) {
        long oldValue;
        block0 : do {
            long newValue;
            AtomicLong atomic;
            if ((atomic = this.map.get(key)) == null && (atomic = this.map.putIfAbsent(key, (AtomicLong)new AtomicLong((long)delta))) == null) {
                return 0L;
            }
            do {
                if ((oldValue = atomic.get()) != 0L) continue;
                if (!this.map.replace(key, (AtomicLong)atomic, (AtomicLong)new AtomicLong((long)delta))) continue block0;
                return 0L;
            } while (!atomic.compareAndSet((long)oldValue, (long)(newValue = oldValue + delta)));
            break;
        } while (true);
        return oldValue;
    }

    @CanIgnoreReturnValue
    public long put(K key, long newValue) {
        long oldValue;
        block0 : do {
            AtomicLong atomic;
            if ((atomic = this.map.get(key)) == null && (atomic = this.map.putIfAbsent(key, (AtomicLong)new AtomicLong((long)newValue))) == null) {
                return 0L;
            }
            do {
                if ((oldValue = atomic.get()) != 0L) continue;
                if (!this.map.replace(key, (AtomicLong)atomic, (AtomicLong)new AtomicLong((long)newValue))) continue block0;
                return 0L;
            } while (!atomic.compareAndSet((long)oldValue, (long)newValue));
            break;
        } while (true);
        return oldValue;
    }

    public void putAll(Map<? extends K, ? extends Long> m) {
        Iterator<Map.Entry<K, Long>> i$ = m.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, Long> entry = i$.next();
            this.put(entry.getKey(), (long)entry.getValue().longValue());
        }
    }

    @CanIgnoreReturnValue
    public long remove(K key) {
        long oldValue;
        AtomicLong atomic = this.map.get(key);
        if (atomic == null) {
            return 0L;
        }
        while ((oldValue = atomic.get()) != 0L && !atomic.compareAndSet((long)oldValue, (long)0L)) {
        }
        this.map.remove(key, (Object)atomic);
        return oldValue;
    }

    @Beta
    @CanIgnoreReturnValue
    public boolean removeIfZero(K key) {
        return this.remove(key, (long)0L);
    }

    public void removeAllZeros() {
        Iterator<Map.Entry<K, AtomicLong>> entryIterator = this.map.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<K, AtomicLong> entry = entryIterator.next();
            AtomicLong atomic = entry.getValue();
            if (atomic == null || atomic.get() != 0L) continue;
            entryIterator.remove();
        }
    }

    public long sum() {
        long sum = 0L;
        Iterator<AtomicLong> i$ = this.map.values().iterator();
        while (i$.hasNext()) {
            AtomicLong value = i$.next();
            sum += value.get();
        }
        return sum;
    }

    public Map<K, Long> asMap() {
        Map<K, Long> map;
        Map<K, Long> result = this.asMap;
        if (result == null) {
            map = this.asMap = this.createAsMap();
            return map;
        }
        map = result;
        return map;
    }

    private Map<K, Long> createAsMap() {
        return Collections.unmodifiableMap(Maps.transformValues(this.map, new Function<AtomicLong, Long>((AtomicLongMap)this){
            final /* synthetic */ AtomicLongMap this$0;
            {
                this.this$0 = atomicLongMap;
            }

            public Long apply(AtomicLong atomic) {
                return Long.valueOf((long)atomic.get());
            }
        }));
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey((Object)key);
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public void clear() {
        this.map.clear();
    }

    public String toString() {
        return this.map.toString();
    }

    long putIfAbsent(K key, long newValue) {
        AtomicLong atomic;
        do {
            if ((atomic = this.map.get(key)) == null && (atomic = this.map.putIfAbsent(key, (AtomicLong)new AtomicLong((long)newValue))) == null) {
                return 0L;
            }
            long oldValue = atomic.get();
            if (oldValue != 0L) return oldValue;
        } while (!this.map.replace(key, (AtomicLong)atomic, (AtomicLong)new AtomicLong((long)newValue)));
        return 0L;
    }

    boolean replace(K key, long expectedOldValue, long newValue) {
        if (expectedOldValue == 0L) {
            if (this.putIfAbsent(key, (long)newValue) != 0L) return false;
            return true;
        }
        AtomicLong atomic = this.map.get(key);
        if (atomic == null) {
            return false;
        }
        boolean bl = atomic.compareAndSet((long)expectedOldValue, (long)newValue);
        return bl;
    }

    boolean remove(K key, long value) {
        AtomicLong atomic = this.map.get(key);
        if (atomic == null) {
            return false;
        }
        long oldValue = atomic.get();
        if (oldValue != value) {
            return false;
        }
        if (oldValue != 0L) {
            if (!atomic.compareAndSet((long)oldValue, (long)0L)) return false;
        }
        this.map.remove(key, (Object)atomic);
        return true;
    }
}

