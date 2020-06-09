/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleShortHash;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.hash.TDoubleShortHashMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TDoubleShortHashMap
extends TDoubleShortHash
implements TDoubleShortMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TDoubleShortHashMap() {
    }

    public TDoubleShortHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TDoubleShortHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TDoubleShortHashMap(int initialCapacity, float loadFactor, double noEntryKey, short noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (double)noEntryKey, (short)noEntryValue);
    }

    public TDoubleShortHashMap(double[] keys, short[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((double)keys[i], (short)values[i]);
            ++i;
        }
    }

    public TDoubleShortHashMap(TDoubleShortMap map) {
        super((int)map.size());
        if (map instanceof TDoubleShortHashMap) {
            TDoubleShortHashMap hashmap = (TDoubleShortHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0.0) {
                Arrays.fill((double[])this._set, (double)this.no_entry_key);
            }
            if (this.no_entry_value != 0) {
                Arrays.fill((short[])this._values, (short)this.no_entry_value);
            }
            this.setUp((int)TDoubleShortHashMap.saturatedCast((long)TDoubleShortHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TDoubleShortMap)map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new short[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        double[] oldKeys = this._set;
        short[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new double[newCapacity];
        this._values = new short[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            double o = oldKeys[i];
            int index = this.insertKey((double)o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public short put(double key, short value) {
        int index = this.insertKey((double)key);
        return this.doPut((double)key, (short)value, (int)index);
    }

    @Override
    public short putIfAbsent(double key, short value) {
        int index = this.insertKey((double)key);
        if (index >= 0) return this.doPut((double)key, (short)value, (int)index);
        return this._values[-index - 1];
    }

    private short doPut(double key, short value, int index) {
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
    public void putAll(Map<? extends Double, ? extends Short> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Double, ? extends Short>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Double, ? extends Short> entry = iterator.next();
            this.put((double)entry.getKey().doubleValue(), (short)entry.getValue().shortValue());
        }
    }

    @Override
    public void putAll(TDoubleShortMap map) {
        this.ensureCapacity((int)map.size());
        TDoubleShortIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((double)iter.key(), (short)iter.value());
        }
    }

    @Override
    public short get(double key) {
        short s;
        int index = this.index((double)key);
        if (index < 0) {
            s = this.no_entry_value;
            return s;
        }
        s = this._values[index];
        return s;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((double[])this._set, (int)0, (int)this._set.length, (double)this.no_entry_key);
        Arrays.fill((short[])this._values, (int)0, (int)this._values.length, (short)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public short remove(double key) {
        short prev = this.no_entry_value;
        int index = this.index((double)key);
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
    public TDoubleSet keySet() {
        return new TKeyView((TDoubleShortHashMap)this);
    }

    @Override
    public double[] keys() {
        double[] keys = new double[this.size()];
        if (keys.length == 0) {
            return keys;
        }
        double[] k = this._set;
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
    public double[] keys(double[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new double[size];
        }
        double[] keys = this._set;
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
    public TShortCollection valueCollection() {
        return new TValueView((TDoubleShortHashMap)this);
    }

    @Override
    public short[] values() {
        short[] vals = new short[this.size()];
        if (vals.length == 0) {
            return vals;
        }
        short[] v = this._values;
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
    public short[] values(short[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new short[size];
        }
        short[] v = this._values;
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
    public boolean containsValue(short val) {
        byte[] states = this._states;
        short[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (states[i] != 1 || val != vals[i]);
        return true;
    }

    @Override
    public boolean containsKey(double key) {
        return this.contains((double)key);
    }

    @Override
    public TDoubleShortIterator iterator() {
        return new TDoubleShortHashIterator((TDoubleShortHashMap)this, (TDoubleShortHashMap)this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.forEach((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        byte[] states = this._states;
        short[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((short)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TDoubleShortProcedure procedure) {
        byte[] states = this._states;
        double[] keys = this._set;
        short[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((double)keys[i], (short)values[i]));
        return false;
    }

    @Override
    public void transformValues(TShortFunction function) {
        byte[] states = this._states;
        short[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute((short)values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TDoubleShortProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        double[] keys = this._set;
        short[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((double)keys[i], (short)values[i])) continue;
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
    public boolean increment(double key) {
        return this.adjustValue((double)key, (short)1);
    }

    @Override
    public boolean adjustValue(double key, short amount) {
        int index = this.index((double)key);
        if (index < 0) {
            return false;
        }
        short[] arrs = this._values;
        int n = index;
        arrs[n] = (short)(arrs[n] + amount);
        return true;
    }

    @Override
    public short adjustOrPutValue(double key, short adjust_amount, short put_amount) {
        boolean isNewMapping;
        short newValue;
        int index = this.insertKey((double)key);
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
        byte previousState = this._states[index];
        if (!isNewMapping) return newValue;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return newValue;
    }

    /*
     * Unable to fully structure code
     */
    public boolean equals(Object other) {
        if (!(other instanceof TDoubleShortMap)) {
            return false;
        }
        that = (TDoubleShortMap)other;
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
            if (!that.containsKey((double)key)) {
                return false;
            }
            this_value = values[i];
            that_value = that.get((double)key);
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
            hashcode += HashFunctions.hash((double)this._set[i]) ^ HashFunctions.hash((int)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TDoubleShortProcedure)new TDoubleShortProcedure((TDoubleShortHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TDoubleShortHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(double key, short value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((double)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((int)value);
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
            out.writeDouble((double)this._set[i]);
            out.writeShort((int)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            double key = in.readDouble();
            short val = in.readShort();
            this.put((double)key, (short)val);
        }
    }

    static /* synthetic */ double access$000(TDoubleShortHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TDoubleShortHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TDoubleShortHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ short access$300(TDoubleShortHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ short access$400(TDoubleShortHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TDoubleShortHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TDoubleShortHashMap x0) {
        return x0._size;
    }
}

