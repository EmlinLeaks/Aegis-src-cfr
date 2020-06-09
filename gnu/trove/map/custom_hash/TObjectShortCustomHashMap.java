/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.custom_hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.custom_hash.TObjectShortCustomHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TObjectShortCustomHashMap<K>
extends TCustomObjectHash<K>
implements TObjectShortMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectShortProcedure<K> PUT_ALL_PROC = new TObjectShortProcedure<K>((TObjectShortCustomHashMap)this){
        final /* synthetic */ TObjectShortCustomHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(K key, short value) {
            this.this$0.put(key, (short)value);
            return true;
        }
    };
    protected transient short[] _values;
    protected short no_entry_value;

    public TObjectShortCustomHashMap() {
    }

    public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy) {
        super(strategy);
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
        super(strategy, (int)initialCapacity);
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
        super(strategy, (int)initialCapacity, (float)loadFactor);
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor, short noEntryValue) {
        super(strategy, (int)initialCapacity, (float)loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value == 0) return;
        Arrays.fill((short[])this._values, (short)this.no_entry_value);
    }

    public TObjectShortCustomHashMap(HashingStrategy<? super K> strategy, TObjectShortMap<? extends K> map) {
        this(strategy, (int)map.size(), (float)0.5f, (short)map.getNoEntryValue());
        if (map instanceof TObjectShortCustomHashMap) {
            TObjectShortCustomHashMap hashmap = (TObjectShortCustomHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_value = hashmap.no_entry_value;
            this.strategy = hashmap.strategy;
            if (this.no_entry_value != 0) {
                Arrays.fill((short[])this._values, (short)this.no_entry_value);
            }
            this.setUp((int)TObjectShortCustomHashMap.saturatedCast((long)TObjectShortCustomHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new short[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        short[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        this._values = new short[newCapacity];
        Arrays.fill((short[])this._values, (short)this.no_entry_value);
        int i = oldCapacity;
        while (i-- > 0) {
            Object o = oldKeys[i];
            if (o == FREE || o == REMOVED) continue;
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation((Object)this._set[-index - 1], (Object)o);
            }
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public short getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains((Object)key);
    }

    @Override
    public boolean containsValue(short val) {
        Object[] keys = this._set;
        short[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]);
        return true;
    }

    @Override
    public short get(Object key) {
        short s;
        int index = this.index((Object)key);
        if (index < 0) {
            s = this.no_entry_value;
            return s;
        }
        s = this._values[index];
        return s;
    }

    @Override
    public short put(K key, short value) {
        int index = this.insertKey(key);
        return this.doPut((short)value, (int)index);
    }

    @Override
    public short putIfAbsent(K key, short value) {
        int index = this.insertKey(key);
        if (index >= 0) return this.doPut((short)value, (int)index);
        return this._values[-index - 1];
    }

    private short doPut(short value, int index) {
        short previous = this.no_entry_value;
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index - 1;
            previous = this._values[index];
            isNewMapping = false;
        }
        this._values[index] = value;
        if (!isNewMapping) return previous;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return previous;
    }

    @Override
    public short remove(Object key) {
        short prev = this.no_entry_value;
        int index = this.index((Object)key);
        if (index < 0) return prev;
        prev = this._values[index];
        this.removeAt((int)index);
        return prev;
    }

    @Override
    protected void removeAt(int index) {
        this._values[index] = this.no_entry_value;
        super.removeAt((int)index);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Short> map) {
        Set<Map.Entry<K, Short>> set = map.entrySet();
        Iterator<Map.Entry<K, Short>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Short> entry = iterator.next();
            this.put(entry.getKey(), (short)entry.getValue().shortValue());
        }
    }

    @Override
    public void putAll(TObjectShortMap<? extends K> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((Object[])this._set, (int)0, (int)this._set.length, (Object)FREE);
        Arrays.fill((short[])this._values, (int)0, (int)this._values.length, (short)this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new KeyView((TObjectShortCustomHashMap)this);
    }

    @Override
    public Object[] keys() {
        Object[] keys = new Object[this.size()];
        Object[] k = this._set;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (k[i] == FREE || k[i] == REMOVED) continue;
            keys[j++] = k[i];
        }
        return keys;
    }

    @Override
    public K[] keys(K[] a) {
        int size = this.size();
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), (int)size);
        }
        Object[] k = this._set;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (k[i] == FREE || k[i] == REMOVED) continue;
            a[j++] = k[i];
        }
        return a;
    }

    @Override
    public TShortCollection valueCollection() {
        return new TShortValueCollection((TObjectShortCustomHashMap)this);
    }

    @Override
    public short[] values() {
        short[] vals = new short[this.size()];
        short[] v = this._values;
        Object[] keys = this._set;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            vals[j++] = v[i];
        }
        return vals;
    }

    @Override
    public short[] values(short[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new short[size];
        }
        short[] v = this._values;
        Object[] keys = this._set;
        int i = v.length;
        int j = 0;
        do {
            if (i-- <= 0) {
                if (array.length <= size) return array;
                array[size] = this.no_entry_value;
                return array;
            }
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            array[j++] = v[i];
        } while (true);
    }

    @Override
    public TObjectShortIterator<K> iterator() {
        return new TObjectShortHashIterator<K>((TObjectShortCustomHashMap)this, this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, (short)1);
    }

    @Override
    public boolean adjustValue(K key, short amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        short[] arrs = this._values;
        int n = index;
        arrs[n] = (short)(arrs[n] + amount);
        return true;
    }

    @Override
    public short adjustOrPutValue(K key, short adjust_amount, short put_amount) {
        boolean isNewMapping;
        short newValue;
        int index = this.insertKey(key);
        if (index < 0) {
            index = -index - 1;
            short[] arrs = this._values;
            int n = index;
            short s = (short)(arrs[n] + adjust_amount);
            arrs[n] = s;
            newValue = s;
            isNewMapping = false;
        } else {
            newValue = this._values[index] = put_amount;
            isNewMapping = true;
        }
        if (!isNewMapping) return newValue;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return newValue;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        Object[] keys = this._set;
        short[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute((short)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TObjectShortProcedure<? super K> procedure) {
        Object[] keys = this._set;
        short[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (short)values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectShortProcedure<? super K> procedure) {
        boolean modified = false;
        Object[] keys = this._set;
        short[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (short)values[i])) continue;
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
    public void transformValues(TShortFunction function) {
        Object[] keys = this._set;
        short[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute((short)values[i]);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectShortMap)) {
            return false;
        }
        that = (TObjectShortMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        try {
            iter = this.iterator();
            do lbl-1000: // 3 sources:
            {
                if (iter.hasNext() == false) return true;
                iter.advance();
                key = iter.key();
                value = iter.value();
                if (value != this.no_entry_value) continue;
                if (that.get(key) != that.getNoEntryValue()) return false;
                if (that.containsKey(key)) ** GOTO lbl-1000
                return false;
            } while (value == that.get(key));
            return false;
        }
        catch (ClassCastException iter) {
            // empty catch block
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        Object[] keys = this._set;
        short[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            hashcode += HashFunctions.hash((int)values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeObject((Object)this.strategy);
        out.writeShort((int)this.no_entry_value);
        out.writeInt((int)this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject((Object)this._set[i]);
            out.writeShort((int)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.strategy = (HashingStrategy)in.readObject();
        this.no_entry_value = in.readShort();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            Object key = in.readObject();
            short val = in.readShort();
            this.put(key, (short)val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TObjectShortProcedure<K>((TObjectShortCustomHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TObjectShortCustomHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(K key, short value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)",");
                }
                this.val$buf.append(key).append((String)"=").append((int)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    static /* synthetic */ int access$100(TObjectShortCustomHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TObjectShortCustomHashMap x0) {
        return x0._size;
    }
}

