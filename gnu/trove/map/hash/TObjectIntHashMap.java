/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TObjectIntHashMap<K>
extends TObjectHash<K>
implements TObjectIntMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectIntProcedure<K> PUT_ALL_PROC = new TObjectIntProcedure<K>((TObjectIntHashMap)this){
        final /* synthetic */ TObjectIntHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(K key, int value) {
            this.this$0.put(key, (int)value);
            return true;
        }
    };
    protected transient int[] _values;
    protected int no_entry_value;

    public TObjectIntHashMap() {
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TObjectIntHashMap(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TObjectIntHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TObjectIntHashMap(int initialCapacity, float loadFactor, int noEntryValue) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value == 0) return;
        Arrays.fill((int[])this._values, (int)this.no_entry_value);
    }

    public TObjectIntHashMap(TObjectIntMap<? extends K> map) {
        this((int)map.size(), (float)0.5f, (int)map.getNoEntryValue());
        if (map instanceof TObjectIntHashMap) {
            TObjectIntHashMap hashmap = (TObjectIntHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill((int[])this._values, (int)this.no_entry_value);
            }
            this.setUp((int)TObjectIntHashMap.saturatedCast((long)TObjectIntHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new int[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        int[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        this._values = new int[newCapacity];
        Arrays.fill((int[])this._values, (int)this.no_entry_value);
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldKeys[i] == FREE || oldKeys[i] == REMOVED) continue;
            Object o = oldKeys[i];
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation((Object)this._set[-index - 1], (Object)o);
            }
            this._set[index] = o;
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains((Object)key);
    }

    @Override
    public boolean containsValue(int val) {
        Object[] keys = this._set;
        int[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]);
        return true;
    }

    @Override
    public int get(Object key) {
        int n;
        int index = this.index((Object)key);
        if (index < 0) {
            n = this.no_entry_value;
            return n;
        }
        n = this._values[index];
        return n;
    }

    @Override
    public int put(K key, int value) {
        int index = this.insertKey(key);
        return this.doPut((int)value, (int)index);
    }

    @Override
    public int putIfAbsent(K key, int value) {
        int index = this.insertKey(key);
        if (index >= 0) return this.doPut((int)value, (int)index);
        return this._values[-index - 1];
    }

    private int doPut(int value, int index) {
        int previous = this.no_entry_value;
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
    public int remove(Object key) {
        int prev = this.no_entry_value;
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
    public void putAll(Map<? extends K, ? extends Integer> map) {
        Set<Map.Entry<K, Integer>> set = map.entrySet();
        Iterator<Map.Entry<K, Integer>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Integer> entry = iterator.next();
            this.put(entry.getKey(), (int)entry.getValue().intValue());
        }
    }

    @Override
    public void putAll(TObjectIntMap<? extends K> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((Object[])this._set, (int)0, (int)this._set.length, (Object)FREE);
        Arrays.fill((int[])this._values, (int)0, (int)this._values.length, (int)this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new KeyView((TObjectIntHashMap)this);
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
    public TIntCollection valueCollection() {
        return new TIntValueCollection((TObjectIntHashMap)this);
    }

    @Override
    public int[] values() {
        int[] vals = new int[this.size()];
        int[] v = this._values;
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
    public int[] values(int[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new int[size];
        }
        int[] v = this._values;
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
    public TObjectIntIterator<K> iterator() {
        return new TObjectIntHashIterator<K>((TObjectIntHashMap)this, this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, (int)1);
    }

    @Override
    public boolean adjustValue(K key, int amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        int[] arrn = this._values;
        int n = index;
        arrn[n] = arrn[n] + amount;
        return true;
    }

    @Override
    public int adjustOrPutValue(K key, int adjust_amount, int put_amount) {
        boolean isNewMapping;
        int newValue;
        int index = this.insertKey(key);
        if (index < 0) {
            index = -index - 1;
            int[] arrn = this._values;
            int n = index;
            int n2 = arrn[n] + adjust_amount;
            arrn[n] = n2;
            newValue = n2;
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
    public boolean forEachValue(TIntProcedure procedure) {
        Object[] keys = this._set;
        int[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute((int)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TObjectIntProcedure<? super K> procedure) {
        Object[] keys = this._set;
        int[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (int)values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectIntProcedure<? super K> procedure) {
        boolean modified = false;
        Object[] keys = this._set;
        int[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (int)values[i])) continue;
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
    public void transformValues(TIntFunction function) {
        Object[] keys = this._set;
        int[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute((int)values[i]);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectIntMap)) {
            return false;
        }
        that = (TObjectIntMap)other;
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
        int[] values = this._values;
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
        out.writeInt((int)this.no_entry_value);
        out.writeInt((int)this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject((Object)this._set[i]);
            out.writeInt((int)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.no_entry_value = in.readInt();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            Object key = in.readObject();
            int val = in.readInt();
            this.put(key, (int)val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TObjectIntProcedure<K>((TObjectIntHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TObjectIntHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(K key, int value) {
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

    static /* synthetic */ int access$100(TObjectIntHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TObjectIntHashMap x0) {
        return x0._size;
    }
}

