/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TCustomHashMap<K, V>
extends TCustomObjectHash<K>
implements TMap<K, V>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient V[] _values;

    public TCustomHashMap() {
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy) {
        super(strategy);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
        super(strategy, (int)initialCapacity);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
        super(strategy, (int)initialCapacity, (float)loadFactor);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, Map<? extends K, ? extends V> map) {
        this(strategy, (int)map.size());
        this.putAll(map);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, TCustomHashMap<? extends K, ? extends V> map) {
        this(strategy, (int)map.size());
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new Object[capacity];
        return capacity;
    }

    @Override
    public V put(K key, V value) {
        int index = this.insertKey(key);
        return (V)this.doPut(value, (int)index);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        int index = this.insertKey(key);
        if (index >= 0) return (V)this.doPut(value, (int)index);
        return (V)this._values[-index - 1];
    }

    private V doPut(V value, int index) {
        V previous = null;
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index - 1;
            previous = (V)this._values[index];
            isNewMapping = false;
        }
        this._values[index] = value;
        if (!isNewMapping) return (V)previous;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return (V)previous;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Map)) {
            return false;
        }
        Map that = (Map)other;
        if (that.size() == this.size()) return this.forEachEntry(new EqProcedure<K, V>(that));
        return false;
    }

    @Override
    public int hashCode() {
        HashProcedure p = new HashProcedure((TCustomHashMap)this, null);
        this.forEachEntry(p);
        return p.getHashCode();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TObjectObjectProcedure<K, V>((TCustomHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TCustomHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(K key, V value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append(key);
                this.val$buf.append((String)"=");
                this.val$buf.append(value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        V[] values = this._values;
        Object[] set = this._set;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (set[i] == FREE || set[i] == REMOVED || procedure.execute(values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> procedure) {
        Object[] keys = this._set;
        V[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> procedure) {
        boolean modified = false;
        Object[] keys = this._set;
        V[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], values[i])) continue;
                this.removeAt((int)i);
                modified = true;
            }
            return modified;
        }
        finally {
            this.reenableAutoCompaction((boolean)true);
        }
    }

    @Override
    public void transformValues(TObjectFunction<V, V> function) {
        V[] values = this._values;
        Object[] set = this._set;
        int i = values.length;
        while (i-- > 0) {
            if (set[i] == FREE || set[i] == REMOVED) continue;
            values[i] = function.execute(values[i]);
        }
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        int oldSize = this.size();
        Object[] oldKeys = this._set;
        V[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        this._values = new Object[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            Object o = oldKeys[i];
            if (o == FREE || o == REMOVED) continue;
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation((Object)this._set[-index - 1], (Object)o, (int)this.size(), (int)oldSize, (Object[])oldKeys);
            }
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public V get(Object key) {
        int index = this.index((Object)key);
        if (index < 0) return (V)null;
        if (this.strategy.equals(this._set[index], key)) return (V)this._values[index];
        return (V)null;
    }

    @Override
    public void clear() {
        if (this.size() == 0) {
            return;
        }
        super.clear();
        Arrays.fill((Object[])this._set, (int)0, (int)this._set.length, (Object)FREE);
        Arrays.fill((Object[])this._values, (int)0, (int)this._values.length, null);
    }

    @Override
    public V remove(Object key) {
        V prev = null;
        int index = this.index((Object)key);
        if (index < 0) return (V)prev;
        prev = (V)this._values[index];
        this.removeAt((int)index);
        return (V)prev;
    }

    @Override
    public void removeAt(int index) {
        this._values[index] = null;
        super.removeAt((int)index);
    }

    @Override
    public Collection<V> values() {
        return new ValueView((TCustomHashMap)this);
    }

    @Override
    public Set<K> keySet() {
        return new KeyView((TCustomHashMap)this);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntryView((TCustomHashMap)this);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public boolean containsValue(Object val) {
        set = this._set;
        vals = this._values;
        if (null == val) {
            i = vals.length;
            do {
                if (i-- <= 0) return false;
            } while (set[i] == TCustomHashMap.FREE || set[i] == TCustomHashMap.REMOVED || val != vals[i]);
            return true;
        }
        i = vals.length;
        do lbl-1000: // 3 sources:
        {
            if (i-- <= 0) return false;
            if (set[i] == TCustomHashMap.FREE || set[i] == TCustomHashMap.REMOVED) ** GOTO lbl-1000
            if (val == vals[i]) return true;
        } while (!this.strategy.equals(val, vals[i]));
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains((Object)key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> e = iterator.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)1);
        super.writeExternal((ObjectOutput)out);
        out.writeInt((int)this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject((Object)this._set[i]);
            out.writeObject(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        if (version != 0) {
            super.readExternal((ObjectInput)in);
        }
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            Object key = in.readObject();
            Object val = in.readObject();
            this.put(key, val);
        }
    }

    static /* synthetic */ HashingStrategy access$200(TCustomHashMap x0) {
        return x0.strategy;
    }

    static /* synthetic */ int access$300(TCustomHashMap x0, Object x1) {
        return x0.index((Object)x1);
    }

    static /* synthetic */ HashingStrategy access$400(TCustomHashMap x0) {
        return x0.strategy;
    }

    static /* synthetic */ HashingStrategy access$500(TCustomHashMap x0) {
        return x0.strategy;
    }

    static /* synthetic */ HashingStrategy access$600(TCustomHashMap x0) {
        return x0.strategy;
    }
}

