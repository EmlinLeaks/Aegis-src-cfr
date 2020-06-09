/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TIntFloatHash;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TIntFloatHashMap
extends TIntFloatHash
implements TIntFloatMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TIntFloatHashMap() {
    }

    public TIntFloatHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TIntFloatHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TIntFloatHashMap(int initialCapacity, float loadFactor, int noEntryKey, float noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (int)noEntryKey, (float)noEntryValue);
    }

    public TIntFloatHashMap(int[] keys, float[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((int)keys[i], (float)values[i]);
            ++i;
        }
    }

    public TIntFloatHashMap(TIntFloatMap map) {
        super((int)map.size());
        if (map instanceof TIntFloatHashMap) {
            TIntFloatHashMap hashmap = (TIntFloatHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((int[])this._set, (int)this.no_entry_key);
            }
            if (this.no_entry_value != 0.0f) {
                Arrays.fill((float[])this._values, (float)this.no_entry_value);
            }
            this.setUp((int)TIntFloatHashMap.saturatedCast((long)TIntFloatHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TIntFloatMap)map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new float[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        int[] oldKeys = this._set;
        float[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new int[newCapacity];
        this._values = new float[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            int o = oldKeys[i];
            int index = this.insertKey((int)o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public float put(int key, float value) {
        int index = this.insertKey((int)key);
        return this.doPut((int)key, (float)value, (int)index);
    }

    @Override
    public float putIfAbsent(int key, float value) {
        int index = this.insertKey((int)key);
        if (index >= 0) return this.doPut((int)key, (float)value, (int)index);
        return this._values[-index - 1];
    }

    private float doPut(int key, float value, int index) {
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
    public void putAll(Map<? extends Integer, ? extends Float> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Integer, ? extends Float>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Integer, ? extends Float> entry = iterator.next();
            this.put((int)entry.getKey().intValue(), (float)entry.getValue().floatValue());
        }
    }

    @Override
    public void putAll(TIntFloatMap map) {
        this.ensureCapacity((int)map.size());
        TIntFloatIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((int)iter.key(), (float)iter.value());
        }
    }

    @Override
    public float get(int key) {
        float f;
        int index = this.index((int)key);
        if (index < 0) {
            f = this.no_entry_value;
            return f;
        }
        f = this._values[index];
        return f;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((int[])this._set, (int)0, (int)this._set.length, (int)this.no_entry_key);
        Arrays.fill((float[])this._values, (int)0, (int)this._values.length, (float)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public float remove(int key) {
        float prev = this.no_entry_value;
        int index = this.index((int)key);
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
    public TIntSet keySet() {
        return new TKeyView((TIntFloatHashMap)this);
    }

    @Override
    public int[] keys() {
        int[] keys = new int[this.size()];
        if (keys.length == 0) {
            return keys;
        }
        int[] k = this._set;
        byte[] states = this._states;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            keys[j++] = k[i];
        }
        return keys;
    }

    @Override
    public int[] keys(int[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new int[size];
        }
        int[] keys = this._set;
        byte[] states = this._states;
        int i = keys.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            array[j++] = keys[i];
        }
        return array;
    }

    @Override
    public TFloatCollection valueCollection() {
        return new TValueView((TIntFloatHashMap)this);
    }

    @Override
    public float[] values() {
        float[] vals = new float[this.size()];
        if (vals.length == 0) {
            return vals;
        }
        float[] v = this._values;
        byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            vals[j++] = v[i];
        }
        return vals;
    }

    @Override
    public float[] values(float[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new float[size];
        }
        float[] v = this._values;
        byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            array[j++] = v[i];
        }
        return array;
    }

    @Override
    public boolean containsValue(float val) {
        byte[] states = this._states;
        float[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (states[i] != 1 || val != vals[i]);
        return true;
    }

    @Override
    public boolean containsKey(int key) {
        return this.contains((int)key);
    }

    @Override
    public TIntFloatIterator iterator() {
        return new TIntFloatHashIterator((TIntFloatHashMap)this, (TIntFloatHashMap)this);
    }

    @Override
    public boolean forEachKey(TIntProcedure procedure) {
        return this.forEach((TIntProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        byte[] states = this._states;
        float[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((float)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TIntFloatProcedure procedure) {
        byte[] states = this._states;
        int[] keys = this._set;
        float[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((int)keys[i], (float)values[i]));
        return false;
    }

    @Override
    public void transformValues(TFloatFunction function) {
        byte[] states = this._states;
        float[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute((float)values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TIntFloatProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        int[] keys = this._set;
        float[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((int)keys[i], (float)values[i])) continue;
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
    public boolean increment(int key) {
        return this.adjustValue((int)key, (float)1.0f);
    }

    @Override
    public boolean adjustValue(int key, float amount) {
        int index = this.index((int)key);
        if (index < 0) {
            return false;
        }
        float[] arrf = this._values;
        int n = index;
        arrf[n] = arrf[n] + amount;
        return true;
    }

    @Override
    public float adjustOrPutValue(int key, float adjust_amount, float put_amount) {
        float newValue;
        boolean isNewMapping;
        int index = this.insertKey((int)key);
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
        byte previousState = this._states[index];
        if (!isNewMapping) return newValue;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return newValue;
    }

    /*
     * Unable to fully structure code
     */
    public boolean equals(Object other) {
        if (!(other instanceof TIntFloatMap)) {
            return false;
        }
        that = (TIntFloatMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        values = this._values;
        states = this._states;
        this_no_entry_value = this.getNoEntryValue();
        that_no_entry_value = that.getNoEntryValue();
        i = values.length;
        do lbl-1000: // 4 sources:
        {
            if (i-- <= 0) return true;
            if (states[i] != 1) ** GOTO lbl-1000
            key = this._set[i];
            if (!that.containsKey((int)key)) {
                return false;
            }
            this_value = values[i];
            that_value = that.get((int)key);
            if (this_value == that_value) ** GOTO lbl-1000
            if (this_value != this_no_entry_value) return false;
        } while (that_value == that_no_entry_value);
        return false;
    }

    public int hashCode() {
        int hashcode = 0;
        byte[] states = this._states;
        int i = this._values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            hashcode += HashFunctions.hash((int)this._set[i]) ^ HashFunctions.hash((float)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TIntFloatProcedure)new TIntFloatProcedure((TIntFloatHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TIntFloatHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(int key, float value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((int)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((float)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeInt((int)this._size);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeInt((int)this._set[i]);
            out.writeFloat((float)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            int key = in.readInt();
            float val = in.readFloat();
            this.put((int)key, (float)val);
        }
    }

    static /* synthetic */ int access$000(TIntFloatHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TIntFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TIntFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ float access$300(TIntFloatHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ float access$400(TIntFloatHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TIntFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TIntFloatHashMap x0) {
        return x0._size;
    }
}

