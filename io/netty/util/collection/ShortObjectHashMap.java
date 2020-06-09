/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.ShortObjectHashMap;
import io.netty.util.collection.ShortObjectMap;
import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ShortObjectHashMap<V>
implements ShortObjectMap<V> {
    public static final int DEFAULT_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private int maxSize;
    private final float loadFactor;
    private short[] keys;
    private V[] values;
    private int size;
    private int mask;
    private final Set<Short> keySet = new KeySet((ShortObjectHashMap)this, null);
    private final Set<Map.Entry<Short, V>> entrySet = new EntrySet((ShortObjectHashMap)this, null);
    private final Iterable<ShortObjectMap.PrimitiveEntry<V>> entries = new Iterable<ShortObjectMap.PrimitiveEntry<V>>((ShortObjectHashMap)this){
        final /* synthetic */ ShortObjectHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public Iterator<ShortObjectMap.PrimitiveEntry<V>> iterator() {
            return new io.netty.util.collection.ShortObjectHashMap$PrimitiveIterator((ShortObjectHashMap)this.this$0, null);
        }
    };

    public ShortObjectHashMap() {
        this((int)8, (float)0.5f);
    }

    public ShortObjectHashMap(int initialCapacity) {
        this((int)initialCapacity, (float)0.5f);
    }

    public ShortObjectHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f) throw new IllegalArgumentException((String)"loadFactor must be > 0 and <= 1");
        if (loadFactor > 1.0f) {
            throw new IllegalArgumentException((String)"loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = MathUtil.safeFindNextPositivePowerOfTwo((int)initialCapacity);
        this.mask = capacity - 1;
        this.keys = new short[capacity];
        Object[] temp = new Object[capacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize((int)capacity);
    }

    private static <T> T toExternal(T value) {
        T t;
        assert (value != null) : "null is not a legitimate internal value. Concurrent Modification?";
        if (value == NULL_VALUE) {
            t = null;
            return (T)((T)t);
        }
        t = (T)value;
        return (T)t;
    }

    private static <T> T toInternal(T value) {
        Object object;
        if (value == null) {
            object = NULL_VALUE;
            return (T)((T)object);
        }
        object = value;
        return (T)object;
    }

    @Override
    public V get(short key) {
        V v;
        int index = this.indexOf((short)key);
        if (index == -1) {
            v = null;
            return (V)((V)v);
        }
        v = (V)ShortObjectHashMap.toExternal(this.values[index]);
        return (V)v;
    }

    @Override
    public V put(short key, V value) {
        int startIndex;
        int index = startIndex = this.hashIndex((short)key);
        do {
            if (this.values[index] == null) {
                this.keys[index] = key;
                this.values[index] = ShortObjectHashMap.toInternal(value);
                this.growSize();
                return (V)null;
            }
            if (this.keys[index] != key) continue;
            V previousValue = this.values[index];
            this.values[index] = ShortObjectHashMap.toInternal(value);
            return (V)ShortObjectHashMap.toExternal(previousValue);
        } while ((index = this.probeNext((int)index)) != startIndex);
        throw new IllegalStateException((String)"Unable to insert");
    }

    @Override
    public void putAll(Map<? extends Short, ? extends V> sourceMap) {
        if (sourceMap instanceof ShortObjectHashMap) {
            ShortObjectHashMap source = (ShortObjectHashMap)sourceMap;
            int i = 0;
            while (i < source.values.length) {
                V sourceValue = source.values[i];
                if (sourceValue != null) {
                    this.put((short)source.keys[i], sourceValue);
                }
                ++i;
            }
            return;
        }
        Iterator<Map.Entry<Short, V>> source = sourceMap.entrySet().iterator();
        while (source.hasNext()) {
            Map.Entry<Short, V> entry = source.next();
            this.put((Short)entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(short key) {
        int index = this.indexOf((short)key);
        if (index == -1) {
            return (V)null;
        }
        V prev = this.values[index];
        this.removeAt((int)index);
        return (V)ShortObjectHashMap.toExternal(prev);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        if (this.size != 0) return false;
        return true;
    }

    @Override
    public void clear() {
        Arrays.fill((short[])this.keys, (short)0);
        Arrays.fill((Object[])this.values, null);
        this.size = 0;
    }

    @Override
    public boolean containsKey(short key) {
        if (this.indexOf((short)key) < 0) return false;
        return true;
    }

    @Override
    public boolean containsValue(Object value) {
        Object v1 = ShortObjectHashMap.toInternal(value);
        V[] arrV = this.values;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V v2 = arrV[n2];
            if (v2 != null && v2.equals((Object)v1)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    @Override
    public Iterable<ShortObjectMap.PrimitiveEntry<V>> entries() {
        return this.entries;
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>((ShortObjectHashMap)this){
            final /* synthetic */ ShortObjectHashMap this$0;
            {
                this.this$0 = this$0;
            }

            public Iterator<V> iterator() {
                return new Iterator<V>(this){
                    final ShortObjectHashMap<V> iter;
                    final /* synthetic */ 2 this$1;
                    {
                        this.this$1 = this$1;
                        this.iter = new io.netty.util.collection.ShortObjectHashMap$PrimitiveIterator((ShortObjectHashMap)this.this$1.this$0, null);
                    }

                    public boolean hasNext() {
                        return ((io.netty.util.collection.ShortObjectHashMap$PrimitiveIterator)((Object)this.iter)).hasNext();
                    }

                    public V next() {
                        return (V)((io.netty.util.collection.ShortObjectHashMap$PrimitiveIterator)((Object)this.iter)).next().value();
                    }

                    public void remove() {
                        ((io.netty.util.collection.ShortObjectHashMap$PrimitiveIterator)((Object)this.iter)).remove();
                    }
                };
            }

            public int size() {
                return ShortObjectHashMap.access$300((ShortObjectHashMap)this.this$0);
            }
        };
    }

    @Override
    public int hashCode() {
        int hash = this.size;
        short[] arrs = this.keys;
        int n = arrs.length;
        int n2 = 0;
        while (n2 < n) {
            short key = arrs[n2];
            hash ^= ShortObjectHashMap.hashCode((short)key);
            ++n2;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ShortObjectMap)) {
            return false;
        }
        ShortObjectMap other = (ShortObjectMap)obj;
        if (this.size != other.size()) {
            return false;
        }
        int i = 0;
        while (i < this.values.length) {
            V value = this.values[i];
            if (value != null) {
                short key = this.keys[i];
                V otherValue = other.get((short)key);
                if (value == NULL_VALUE ? otherValue != null : !value.equals(otherValue)) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.containsKey((short)this.objectToKey((Object)key));
    }

    @Override
    public V get(Object key) {
        return (V)this.get((short)this.objectToKey((Object)key));
    }

    @Override
    public V put(Short key, V value) {
        return (V)this.put((short)this.objectToKey((Object)key), value);
    }

    @Override
    public V remove(Object key) {
        return (V)this.remove((short)this.objectToKey((Object)key));
    }

    @Override
    public Set<Short> keySet() {
        return this.keySet;
    }

    @Override
    public Set<Map.Entry<Short, V>> entrySet() {
        return this.entrySet;
    }

    private short objectToKey(Object key) {
        return ((Short)key).shortValue();
    }

    private int indexOf(short key) {
        int startIndex;
        int index = startIndex = this.hashIndex((short)key);
        do {
            if (this.values[index] == null) {
                return -1;
            }
            if (key != this.keys[index]) continue;
            return index;
        } while ((index = this.probeNext((int)index)) != startIndex);
        return -1;
    }

    private int hashIndex(short key) {
        return ShortObjectHashMap.hashCode((short)key) & this.mask;
    }

    private static int hashCode(short key) {
        return key;
    }

    private int probeNext(int index) {
        return index + 1 & this.mask;
    }

    private void growSize() {
        ++this.size;
        if (this.size <= this.maxSize) return;
        if (this.keys.length == Integer.MAX_VALUE) {
            throw new IllegalStateException((String)("Max capacity reached at size=" + this.size));
        }
        this.rehash((int)(this.keys.length << 1));
    }

    private boolean removeAt(int index) {
        --this.size;
        this.keys[index] = 0;
        this.values[index] = null;
        int nextFree = index;
        int i = this.probeNext((int)index);
        V value = this.values[i];
        while (value != null) {
            short key = this.keys[i];
            int bucket = this.hashIndex((short)key);
            if (i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
                this.keys[nextFree] = key;
                this.values[nextFree] = value;
                this.keys[i] = 0;
                this.values[i] = null;
                nextFree = i;
            }
            i = this.probeNext((int)i);
            value = this.values[i];
        }
        if (nextFree == index) return false;
        return true;
    }

    private int calcMaxSize(int capacity) {
        int upperBound = capacity - 1;
        return Math.min((int)upperBound, (int)((int)((float)capacity * this.loadFactor)));
    }

    private void rehash(int newCapacity) {
        short[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new short[newCapacity];
        Object[] temp = new Object[newCapacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize((int)newCapacity);
        this.mask = newCapacity - 1;
        int i = 0;
        while (i < oldVals.length) {
            V oldVal = oldVals[i];
            if (oldVal != null) {
                short oldKey = oldKeys[i];
                int index = this.hashIndex((short)oldKey);
                do {
                    if (this.values[index] == null) {
                        this.keys[index] = oldKey;
                        this.values[index] = oldVal;
                        break;
                    }
                    index = this.probeNext((int)index);
                } while (true);
            }
            ++i;
        }
    }

    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder((int)(4 * this.size));
        sb.append((char)'{');
        boolean first = true;
        int i = 0;
        while (i < this.values.length) {
            V value = this.values[i];
            if (value != null) {
                if (!first) {
                    sb.append((String)", ");
                }
                sb.append((String)this.keyToString((short)this.keys[i])).append((char)'=').append((Object)(value == this ? "(this Map)" : ShortObjectHashMap.toExternal(value)));
                first = false;
            }
            ++i;
        }
        return sb.append((char)'}').toString();
    }

    protected String keyToString(short key) {
        return Short.toString((short)key);
    }

    static /* synthetic */ int access$300(ShortObjectHashMap x0) {
        return x0.size;
    }

    static /* synthetic */ Set access$500(ShortObjectHashMap x0) {
        return x0.entrySet;
    }

    static /* synthetic */ Object[] access$600(ShortObjectHashMap x0) {
        return x0.values;
    }

    static /* synthetic */ boolean access$700(ShortObjectHashMap x0, int x1) {
        return x0.removeAt((int)x1);
    }

    static /* synthetic */ short[] access$800(ShortObjectHashMap x0) {
        return x0.keys;
    }

    static /* synthetic */ Object access$900(Object x0) {
        return ShortObjectHashMap.toExternal(x0);
    }

    static /* synthetic */ Object access$1000(Object x0) {
        return ShortObjectHashMap.toInternal(x0);
    }
}

