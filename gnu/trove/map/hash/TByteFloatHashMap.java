/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteFloatHash;
import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.map.hash.TByteFloatHashMap;
import gnu.trove.procedure.TByteFloatProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TByteFloatHashMap
extends TByteFloatHash
implements TByteFloatMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TByteFloatHashMap() {
    }

    public TByteFloatHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TByteFloatHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TByteFloatHashMap(int initialCapacity, float loadFactor, byte noEntryKey, float noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (byte)noEntryKey, (float)noEntryValue);
    }

    public TByteFloatHashMap(byte[] keys, float[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((byte)keys[i], (float)values[i]);
            ++i;
        }
    }

    public TByteFloatHashMap(TByteFloatMap map) {
        super((int)map.size());
        if (map instanceof TByteFloatHashMap) {
            TByteFloatHashMap hashmap = (TByteFloatHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((byte[])this._set, (byte)this.no_entry_key);
            }
            if (this.no_entry_value != 0.0f) {
                Arrays.fill((float[])this._values, (float)this.no_entry_value);
            }
            this.setUp((int)TByteFloatHashMap.saturatedCast((long)TByteFloatHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TByteFloatMap)map);
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
        byte[] oldKeys = this._set;
        float[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._values = new float[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            byte o = oldKeys[i];
            int index = this.insertKey((byte)o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public float put(byte key, float value) {
        int index = this.insertKey((byte)key);
        return this.doPut((byte)key, (float)value, (int)index);
    }

    @Override
    public float putIfAbsent(byte key, float value) {
        int index = this.insertKey((byte)key);
        if (index >= 0) return this.doPut((byte)key, (float)value, (int)index);
        return this._values[-index - 1];
    }

    private float doPut(byte key, float value, int index) {
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
    public void putAll(Map<? extends Byte, ? extends Float> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Byte, ? extends Float>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Byte, ? extends Float> entry = iterator.next();
            this.put((byte)entry.getKey().byteValue(), (float)entry.getValue().floatValue());
        }
    }

    @Override
    public void putAll(TByteFloatMap map) {
        this.ensureCapacity((int)map.size());
        TByteFloatIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((byte)iter.key(), (float)iter.value());
        }
    }

    @Override
    public float get(byte key) {
        float f;
        int index = this.index((byte)key);
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
        Arrays.fill((byte[])this._set, (int)0, (int)this._set.length, (byte)this.no_entry_key);
        Arrays.fill((float[])this._values, (int)0, (int)this._values.length, (float)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public float remove(byte key) {
        float prev = this.no_entry_value;
        int index = this.index((byte)key);
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
    public TByteSet keySet() {
        return new TKeyView((TByteFloatHashMap)this);
    }

    @Override
    public byte[] keys() {
        byte[] keys = new byte[this.size()];
        if (keys.length == 0) {
            return keys;
        }
        byte[] k = this._set;
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
    public byte[] keys(byte[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new byte[size];
        }
        byte[] keys = this._set;
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
        return new TValueView((TByteFloatHashMap)this);
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
    public boolean containsKey(byte key) {
        return this.contains((byte)key);
    }

    @Override
    public TByteFloatIterator iterator() {
        return new TByteFloatHashIterator((TByteFloatHashMap)this, (TByteFloatHashMap)this);
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.forEach((TByteProcedure)procedure);
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
    public boolean forEachEntry(TByteFloatProcedure procedure) {
        byte[] states = this._states;
        byte[] keys = this._set;
        float[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((byte)keys[i], (float)values[i]));
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
    public boolean retainEntries(TByteFloatProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        byte[] keys = this._set;
        float[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((byte)keys[i], (float)values[i])) continue;
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
    public boolean increment(byte key) {
        return this.adjustValue((byte)key, (float)1.0f);
    }

    @Override
    public boolean adjustValue(byte key, float amount) {
        int index = this.index((byte)key);
        if (index < 0) {
            return false;
        }
        float[] arrf = this._values;
        int n = index;
        arrf[n] = arrf[n] + amount;
        return true;
    }

    @Override
    public float adjustOrPutValue(byte key, float adjust_amount, float put_amount) {
        float newValue;
        boolean isNewMapping;
        int index = this.insertKey((byte)key);
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
        if (!(other instanceof TByteFloatMap)) {
            return false;
        }
        that = (TByteFloatMap)other;
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
            if (!that.containsKey((byte)key)) {
                return false;
            }
            this_value = values[i];
            that_value = that.get((byte)key);
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
        this.forEachEntry((TByteFloatProcedure)new TByteFloatProcedure((TByteFloatHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TByteFloatHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(byte key, float value) {
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
            out.writeByte((int)this._set[i]);
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
            byte key = in.readByte();
            float val = in.readFloat();
            this.put((byte)key, (float)val);
        }
    }

    static /* synthetic */ byte access$000(TByteFloatHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TByteFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TByteFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ float access$300(TByteFloatHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ float access$400(TByteFloatHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TByteFloatHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TByteFloatHashMap x0) {
        return x0._size;
    }
}

