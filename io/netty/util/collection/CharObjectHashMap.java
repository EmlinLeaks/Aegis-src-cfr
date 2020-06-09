/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.collection;

import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.collection.CharObjectMap;
import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CharObjectHashMap<V>
implements CharObjectMap<V> {
    public static final int DEFAULT_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private int maxSize;
    private final float loadFactor;
    private char[] keys;
    private V[] values;
    private int size;
    private int mask;
    private final Set<Character> keySet = new KeySet((CharObjectHashMap)this, null);
    private final Set<Map.Entry<Character, V>> entrySet = new EntrySet((CharObjectHashMap)this, null);
    private final Iterable<CharObjectMap.PrimitiveEntry<V>> entries = new Iterable<CharObjectMap.PrimitiveEntry<V>>((CharObjectHashMap)this){
        final /* synthetic */ CharObjectHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public Iterator<CharObjectMap.PrimitiveEntry<V>> iterator() {
            return new io.netty.util.collection.CharObjectHashMap$PrimitiveIterator((CharObjectHashMap)this.this$0, null);
        }
    };

    public CharObjectHashMap() {
        this((int)8, (float)0.5f);
    }

    public CharObjectHashMap(int initialCapacity) {
        this((int)initialCapacity, (float)0.5f);
    }

    public CharObjectHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f) throw new IllegalArgumentException((String)"loadFactor must be > 0 and <= 1");
        if (loadFactor > 1.0f) {
            throw new IllegalArgumentException((String)"loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = MathUtil.safeFindNextPositivePowerOfTwo((int)initialCapacity);
        this.mask = capacity - 1;
        this.keys = new char[capacity];
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
    public V get(char key) {
        V v;
        int index = this.indexOf((char)key);
        if (index == -1) {
            v = null;
            return (V)((V)v);
        }
        v = (V)CharObjectHashMap.toExternal(this.values[index]);
        return (V)v;
    }

    @Override
    public V put(char key, V value) {
        int startIndex;
        int index = startIndex = this.hashIndex((char)key);
        do {
            if (this.values[index] == null) {
                this.keys[index] = key;
                this.values[index] = CharObjectHashMap.toInternal(value);
                this.growSize();
                return (V)null;
            }
            if (this.keys[index] != key) continue;
            V previousValue = this.values[index];
            this.values[index] = CharObjectHashMap.toInternal(value);
            return (V)CharObjectHashMap.toExternal(previousValue);
        } while ((index = this.probeNext((int)index)) != startIndex);
        throw new IllegalStateException((String)"Unable to insert");
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> sourceMap) {
        if (sourceMap instanceof CharObjectHashMap) {
            CharObjectHashMap source = (CharObjectHashMap)sourceMap;
            int i = 0;
            while (i < source.values.length) {
                V sourceValue = source.values[i];
                if (sourceValue != null) {
                    this.put((char)source.keys[i], sourceValue);
                }
                ++i;
            }
            return;
        }
        Iterator<Map.Entry<Character, V>> source = sourceMap.entrySet().iterator();
        while (source.hasNext()) {
            Map.Entry<Character, V> entry = source.next();
            this.put((Character)entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(char key) {
        int index = this.indexOf((char)key);
        if (index == -1) {
            return (V)null;
        }
        V prev = this.values[index];
        this.removeAt((int)index);
        return (V)CharObjectHashMap.toExternal(prev);
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
        Arrays.fill((char[])this.keys, (char)'\u0000');
        Arrays.fill((Object[])this.values, null);
        this.size = 0;
    }

    @Override
    public boolean containsKey(char key) {
        if (this.indexOf((char)key) < 0) return false;
        return true;
    }

    @Override
    public boolean containsValue(Object value) {
        Object v1 = CharObjectHashMap.toInternal(value);
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
    public Iterable<CharObjectMap.PrimitiveEntry<V>> entries() {
        return this.entries;
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>((CharObjectHashMap)this){
            final /* synthetic */ CharObjectHashMap this$0;
            {
                this.this$0 = this$0;
            }

            public Iterator<V> iterator() {
                return new Iterator<V>(this){
                    final CharObjectHashMap<V> iter;
                    final /* synthetic */ 2 this$1;
                    {
                        this.this$1 = this$1;
                        this.iter = new io.netty.util.collection.CharObjectHashMap$PrimitiveIterator((CharObjectHashMap)this.this$1.this$0, null);
                    }

                    public boolean hasNext() {
                        return ((io.netty.util.collection.CharObjectHashMap$PrimitiveIterator)((Object)this.iter)).hasNext();
                    }

                    public V next() {
                        return (V)((io.netty.util.collection.CharObjectHashMap$PrimitiveIterator)((Object)this.iter)).next().value();
                    }

                    public void remove() {
                        ((io.netty.util.collection.CharObjectHashMap$PrimitiveIterator)((Object)this.iter)).remove();
                    }
                };
            }

            public int size() {
                return CharObjectHashMap.access$300((CharObjectHashMap)this.this$0);
            }
        };
    }

    @Override
    public int hashCode() {
        int hash = this.size;
        char[] arrc = this.keys;
        int n = arrc.length;
        int n2 = 0;
        while (n2 < n) {
            char key = arrc[n2];
            hash ^= CharObjectHashMap.hashCode((char)key);
            ++n2;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharObjectMap)) {
            return false;
        }
        CharObjectMap other = (CharObjectMap)obj;
        if (this.size != other.size()) {
            return false;
        }
        int i = 0;
        while (i < this.values.length) {
            V value = this.values[i];
            if (value != null) {
                char key = this.keys[i];
                V otherValue = other.get((char)key);
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
        return this.containsKey((char)this.objectToKey((Object)key));
    }

    @Override
    public V get(Object key) {
        return (V)this.get((char)this.objectToKey((Object)key));
    }

    @Override
    public V put(Character key, V value) {
        return (V)this.put((char)this.objectToKey((Object)key), value);
    }

    @Override
    public V remove(Object key) {
        return (V)this.remove((char)this.objectToKey((Object)key));
    }

    @Override
    public Set<Character> keySet() {
        return this.keySet;
    }

    @Override
    public Set<Map.Entry<Character, V>> entrySet() {
        return this.entrySet;
    }

    private char objectToKey(Object key) {
        return ((Character)key).charValue();
    }

    private int indexOf(char key) {
        int startIndex;
        int index = startIndex = this.hashIndex((char)key);
        do {
            if (this.values[index] == null) {
                return -1;
            }
            if (key != this.keys[index]) continue;
            return index;
        } while ((index = this.probeNext((int)index)) != startIndex);
        return -1;
    }

    private int hashIndex(char key) {
        return CharObjectHashMap.hashCode((char)key) & this.mask;
    }

    private static int hashCode(char key) {
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
        this.keys[index] = '\u0000';
        this.values[index] = null;
        int nextFree = index;
        int i = this.probeNext((int)index);
        V value = this.values[i];
        while (value != null) {
            char key = this.keys[i];
            int bucket = this.hashIndex((char)key);
            if (i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
                this.keys[nextFree] = key;
                this.values[nextFree] = value;
                this.keys[i] = '\u0000';
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
        char[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new char[newCapacity];
        Object[] temp = new Object[newCapacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize((int)newCapacity);
        this.mask = newCapacity - 1;
        int i = 0;
        while (i < oldVals.length) {
            V oldVal = oldVals[i];
            if (oldVal != null) {
                char oldKey = oldKeys[i];
                int index = this.hashIndex((char)oldKey);
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
                sb.append((String)this.keyToString((char)this.keys[i])).append((char)'=').append((Object)(value == this ? "(this Map)" : CharObjectHashMap.toExternal(value)));
                first = false;
            }
            ++i;
        }
        return sb.append((char)'}').toString();
    }

    protected String keyToString(char key) {
        return Character.toString((char)key);
    }

    static /* synthetic */ int access$300(CharObjectHashMap x0) {
        return x0.size;
    }

    static /* synthetic */ Set access$500(CharObjectHashMap x0) {
        return x0.entrySet;
    }

    static /* synthetic */ Object[] access$600(CharObjectHashMap x0) {
        return x0.values;
    }

    static /* synthetic */ boolean access$700(CharObjectHashMap x0, int x1) {
        return x0.removeAt((int)x1);
    }

    static /* synthetic */ char[] access$800(CharObjectHashMap x0) {
        return x0.keys;
    }

    static /* synthetic */ Object access$900(Object x0) {
        return CharObjectHashMap.toExternal(x0);
    }

    static /* synthetic */ Object access$1000(Object x0) {
        return CharObjectHashMap.toInternal(x0);
    }
}

