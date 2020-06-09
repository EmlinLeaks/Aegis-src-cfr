/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
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

public class TObjectFloatHashMap<K>
extends TObjectHash<K>
implements TObjectFloatMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectFloatProcedure<K> PUT_ALL_PROC = new TObjectFloatProcedure<K>((TObjectFloatHashMap)this){
        final /* synthetic */ TObjectFloatHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(K key, float value) {
            this.this$0.put(key, (float)value);
            return true;
        }
    };
    protected transient float[] _values;
    protected float no_entry_value;

    public TObjectFloatHashMap() {
        this.no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TObjectFloatHashMap(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TObjectFloatHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TObjectFloatHashMap(int initialCapacity, float loadFactor, float noEntryValue) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value == 0.0f) return;
        Arrays.fill((float[])this._values, (float)this.no_entry_value);
    }

    public TObjectFloatHashMap(TObjectFloatMap<? extends K> map) {
        this((int)map.size(), (float)0.5f, (float)map.getNoEntryValue());
        if (map instanceof TObjectFloatHashMap) {
            TObjectFloatHashMap hashmap = (TObjectFloatHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_value != 0.0f) {
                Arrays.fill((float[])this._values, (float)this.no_entry_value);
            }
            this.setUp((int)TObjectFloatHashMap.saturatedCast((long)TObjectFloatHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new float[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        float[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        this._values = new float[newCapacity];
        Arrays.fill((float[])this._values, (float)this.no_entry_value);
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
    public float getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains((Object)key);
    }

    @Override
    public boolean containsValue(float val) {
        Object[] keys = this._set;
        float[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]);
        return true;
    }

    @Override
    public float get(Object key) {
        float f;
        int index = this.index((Object)key);
        if (index < 0) {
            f = this.no_entry_value;
            return f;
        }
        f = this._values[index];
        return f;
    }

    @Override
    public float put(K key, float value) {
        int index = this.insertKey(key);
        return this.doPut((float)value, (int)index);
    }

    @Override
    public float putIfAbsent(K key, float value) {
        int index = this.insertKey(key);
        if (index >= 0) return this.doPut((float)value, (int)index);
        return this._values[-index - 1];
    }

    private float doPut(float value, int index) {
        float previous = this.no_entry_value;
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
    public float remove(Object key) {
        float prev = this.no_entry_value;
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
    public void putAll(Map<? extends K, ? extends Float> map) {
        Set<Map.Entry<K, Float>> set = map.entrySet();
        Iterator<Map.Entry<K, Float>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Float> entry = iterator.next();
            this.put(entry.getKey(), (float)entry.getValue().floatValue());
        }
    }

    @Override
    public void putAll(TObjectFloatMap<? extends K> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((Object[])this._set, (int)0, (int)this._set.length, (Object)FREE);
        Arrays.fill((float[])this._values, (int)0, (int)this._values.length, (float)this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new KeyView((TObjectFloatHashMap)this);
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
    public TFloatCollection valueCollection() {
        return new TFloatValueCollection((TObjectFloatHashMap)this);
    }

    @Override
    public float[] values() {
        float[] vals = new float[this.size()];
        float[] v = this._values;
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
    public float[] values(float[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new float[size];
        }
        float[] v = this._values;
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
    public TObjectFloatIterator<K> iterator() {
        return new TObjectFloatHashIterator<K>((TObjectFloatHashMap)this, this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, (float)1.0f);
    }

    @Override
    public boolean adjustValue(K key, float amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        float[] arrf = this._values;
        int n = index;
        arrf[n] = arrf[n] + amount;
        return true;
    }

    @Override
    public float adjustOrPutValue(K key, float adjust_amount, float put_amount) {
        float newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = -index - 1;
            float[] arrf = this._values;
            int n = index;
            float f = arrf[n] + adjust_amount;
            arrf[n] = f;
            newValue = f;
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
    public boolean forEachValue(TFloatProcedure procedure) {
        Object[] keys = this._set;
        float[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute((float)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TObjectFloatProcedure<? super K> procedure) {
        Object[] keys = this._set;
        float[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (float)values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectFloatProcedure<? super K> procedure) {
        boolean modified = false;
        Object[] keys = this._set;
        float[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (float)values[i])) continue;
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
    public void transformValues(TFloatFunction function) {
        Object[] keys = this._set;
        float[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute((float)values[i]);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectFloatMap)) {
            return false;
        }
        that = (TObjectFloatMap)other;
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
        float[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            hashcode += HashFunctions.hash((float)values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeFloat((float)this.no_entry_value);
        out.writeInt((int)this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject((Object)this._set[i]);
            out.writeFloat((float)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.no_entry_value = in.readFloat();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            Object key = in.readObject();
            float val = in.readFloat();
            this.put(key, (float)val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TObjectFloatProcedure<K>((TObjectFloatHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TObjectFloatHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(K key, float value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)",");
                }
                this.val$buf.append(key).append((String)"=").append((float)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    static /* synthetic */ int access$100(TObjectFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TObjectFloatHashMap x0) {
        return x0._size;
    }
}

