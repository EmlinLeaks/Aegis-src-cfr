/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
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

public class TObjectDoubleHashMap<K>
extends TObjectHash<K>
implements TObjectDoubleMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectDoubleProcedure<K> PUT_ALL_PROC = new TObjectDoubleProcedure<K>((TObjectDoubleHashMap)this){
        final /* synthetic */ TObjectDoubleHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(K key, double value) {
            this.this$0.put(key, (double)value);
            return true;
        }
    };
    protected transient double[] _values;
    protected double no_entry_value;

    public TObjectDoubleHashMap() {
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleHashMap(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleHashMap(int initialCapacity, float loadFactor, double noEntryValue) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value == 0.0) return;
        Arrays.fill((double[])this._values, (double)this.no_entry_value);
    }

    public TObjectDoubleHashMap(TObjectDoubleMap<? extends K> map) {
        this((int)map.size(), (float)0.5f, (double)map.getNoEntryValue());
        if (map instanceof TObjectDoubleHashMap) {
            TObjectDoubleHashMap hashmap = (TObjectDoubleHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_value != 0.0) {
                Arrays.fill((double[])this._values, (double)this.no_entry_value);
            }
            this.setUp((int)TObjectDoubleHashMap.saturatedCast((long)TObjectDoubleHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new double[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        double[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        this._values = new double[newCapacity];
        Arrays.fill((double[])this._values, (double)this.no_entry_value);
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
    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains((Object)key);
    }

    @Override
    public boolean containsValue(double val) {
        Object[] keys = this._set;
        double[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]);
        return true;
    }

    @Override
    public double get(Object key) {
        double d;
        int index = this.index((Object)key);
        if (index < 0) {
            d = this.no_entry_value;
            return d;
        }
        d = this._values[index];
        return d;
    }

    @Override
    public double put(K key, double value) {
        int index = this.insertKey(key);
        return this.doPut((double)value, (int)index);
    }

    @Override
    public double putIfAbsent(K key, double value) {
        int index = this.insertKey(key);
        if (index >= 0) return this.doPut((double)value, (int)index);
        return this._values[-index - 1];
    }

    private double doPut(double value, int index) {
        double previous = this.no_entry_value;
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
    public double remove(Object key) {
        double prev = this.no_entry_value;
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
    public void putAll(Map<? extends K, ? extends Double> map) {
        Set<Map.Entry<K, Double>> set = map.entrySet();
        Iterator<Map.Entry<K, Double>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Double> entry = iterator.next();
            this.put(entry.getKey(), (double)entry.getValue().doubleValue());
        }
    }

    @Override
    public void putAll(TObjectDoubleMap<? extends K> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((Object[])this._set, (int)0, (int)this._set.length, (Object)FREE);
        Arrays.fill((double[])this._values, (int)0, (int)this._values.length, (double)this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new KeyView((TObjectDoubleHashMap)this);
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
    public TDoubleCollection valueCollection() {
        return new TDoubleValueCollection((TObjectDoubleHashMap)this);
    }

    @Override
    public double[] values() {
        double[] vals = new double[this.size()];
        double[] v = this._values;
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
    public double[] values(double[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new double[size];
        }
        double[] v = this._values;
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
    public TObjectDoubleIterator<K> iterator() {
        return new TObjectDoubleHashIterator<K>((TObjectDoubleHashMap)this, this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, (double)1.0);
    }

    @Override
    public boolean adjustValue(K key, double amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        double[] arrd = this._values;
        int n = index;
        arrd[n] = arrd[n] + amount;
        return true;
    }

    @Override
    public double adjustOrPutValue(K key, double adjust_amount, double put_amount) {
        boolean isNewMapping;
        double newValue;
        int index = this.insertKey(key);
        if (index < 0) {
            index = -index - 1;
            double[] arrd = this._values;
            int n = index;
            double d = arrd[n] + adjust_amount;
            arrd[n] = d;
            newValue = d;
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
    public boolean forEachValue(TDoubleProcedure procedure) {
        Object[] keys = this._set;
        double[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute((double)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TObjectDoubleProcedure<? super K> procedure) {
        Object[] keys = this._set;
        double[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (double)values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectDoubleProcedure<? super K> procedure) {
        boolean modified = false;
        Object[] keys = this._set;
        double[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (double)values[i])) continue;
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
    public void transformValues(TDoubleFunction function) {
        Object[] keys = this._set;
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute((double)values[i]);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectDoubleMap)) {
            return false;
        }
        that = (TObjectDoubleMap)other;
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
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            hashcode += HashFunctions.hash((double)values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeDouble((double)this.no_entry_value);
        out.writeInt((int)this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject((Object)this._set[i]);
            out.writeDouble((double)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.no_entry_value = in.readDouble();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            Object key = in.readObject();
            double val = in.readDouble();
            this.put(key, (double)val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TObjectDoubleProcedure<K>((TObjectDoubleHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TObjectDoubleHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(K key, double value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)",");
                }
                this.val$buf.append(key).append((String)"=").append((double)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    static /* synthetic */ int access$100(TObjectDoubleHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TObjectDoubleHashMap x0) {
        return x0._size;
    }
}

