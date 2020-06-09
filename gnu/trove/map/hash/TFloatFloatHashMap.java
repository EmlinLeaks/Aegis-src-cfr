/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatFloatHash;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.map.hash.TFloatFloatHashMap;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TFloatFloatHashMap
extends TFloatFloatHash
implements TFloatFloatMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TFloatFloatHashMap() {
    }

    public TFloatFloatHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TFloatFloatHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TFloatFloatHashMap(int initialCapacity, float loadFactor, float noEntryKey, float noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (float)noEntryKey, (float)noEntryValue);
    }

    public TFloatFloatHashMap(float[] keys, float[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((float)keys[i], (float)values[i]);
            ++i;
        }
    }

    public TFloatFloatHashMap(TFloatFloatMap map) {
        super((int)map.size());
        if (map instanceof TFloatFloatHashMap) {
            TFloatFloatHashMap hashmap = (TFloatFloatHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0.0f) {
                Arrays.fill((float[])this._set, (float)this.no_entry_key);
            }
            if (this.no_entry_value != 0.0f) {
                Arrays.fill((float[])this._values, (float)this.no_entry_value);
            }
            this.setUp((int)TFloatFloatHashMap.saturatedCast((long)TFloatFloatHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TFloatFloatMap)map);
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
        float[] oldKeys = this._set;
        float[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new float[newCapacity];
        this._values = new float[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            float o = oldKeys[i];
            int index = this.insertKey((float)o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public float put(float key, float value) {
        int index = this.insertKey((float)key);
        return this.doPut((float)key, (float)value, (int)index);
    }

    @Override
    public float putIfAbsent(float key, float value) {
        int index = this.insertKey((float)key);
        if (index >= 0) return this.doPut((float)key, (float)value, (int)index);
        return this._values[-index - 1];
    }

    private float doPut(float key, float value, int index) {
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
    public void putAll(Map<? extends Float, ? extends Float> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Float, ? extends Float>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Float, ? extends Float> entry = iterator.next();
            this.put((float)entry.getKey().floatValue(), (float)entry.getValue().floatValue());
        }
    }

    @Override
    public void putAll(TFloatFloatMap map) {
        this.ensureCapacity((int)map.size());
        TFloatFloatIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((float)iter.key(), (float)iter.value());
        }
    }

    @Override
    public float get(float key) {
        float f;
        int index = this.index((float)key);
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
        Arrays.fill((float[])this._set, (int)0, (int)this._set.length, (float)this.no_entry_key);
        Arrays.fill((float[])this._values, (int)0, (int)this._values.length, (float)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public float remove(float key) {
        float prev = this.no_entry_value;
        int index = this.index((float)key);
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
    public TFloatSet keySet() {
        return new TKeyView((TFloatFloatHashMap)this);
    }

    @Override
    public float[] keys() {
        float[] keys = new float[this.size()];
        if (keys.length == 0) {
            return keys;
        }
        float[] k = this._set;
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
    public float[] keys(float[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new float[size];
        }
        float[] keys = this._set;
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
        return new TValueView((TFloatFloatHashMap)this);
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
    public boolean containsKey(float key) {
        return this.contains((float)key);
    }

    @Override
    public TFloatFloatIterator iterator() {
        return new TFloatFloatHashIterator((TFloatFloatHashMap)this, (TFloatFloatHashMap)this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
        return this.forEach((TFloatProcedure)procedure);
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
    public boolean forEachEntry(TFloatFloatProcedure procedure) {
        byte[] states = this._states;
        float[] keys = this._set;
        float[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((float)keys[i], (float)values[i]));
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
    public boolean retainEntries(TFloatFloatProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        float[] keys = this._set;
        float[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((float)keys[i], (float)values[i])) continue;
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
    public boolean increment(float key) {
        return this.adjustValue((float)key, (float)1.0f);
    }

    @Override
    public boolean adjustValue(float key, float amount) {
        int index = this.index((float)key);
        if (index < 0) {
            return false;
        }
        float[] arrf = this._values;
        int n = index;
        arrf[n] = arrf[n] + amount;
        return true;
    }

    @Override
    public float adjustOrPutValue(float key, float adjust_amount, float put_amount) {
        float newValue;
        boolean isNewMapping;
        int index = this.insertKey((float)key);
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
        if (!(other instanceof TFloatFloatMap)) {
            return false;
        }
        that = (TFloatFloatMap)other;
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
            if (!that.containsKey((float)key)) {
                return false;
            }
            this_value = values[i];
            that_value = that.get((float)key);
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
            hashcode += HashFunctions.hash((float)this._set[i]) ^ HashFunctions.hash((float)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TFloatFloatProcedure)new TFloatFloatProcedure((TFloatFloatHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TFloatFloatHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(float key, float value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((float)key);
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
            out.writeFloat((float)this._set[i]);
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
            float key = in.readFloat();
            float val = in.readFloat();
            this.put((float)key, (float)val);
        }
    }

    static /* synthetic */ float access$000(TFloatFloatHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TFloatFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TFloatFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ float access$300(TFloatFloatHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ float access$400(TFloatFloatHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TFloatFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TFloatFloatHashMap x0) {
        return x0._size;
    }
}

