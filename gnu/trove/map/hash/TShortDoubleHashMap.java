/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TShortDoubleHash;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.hash.TShortDoubleHashMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TShortDoubleHashMap
extends TShortDoubleHash
implements TShortDoubleMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient double[] _values;

    public TShortDoubleHashMap() {
    }

    public TShortDoubleHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TShortDoubleHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TShortDoubleHashMap(int initialCapacity, float loadFactor, short noEntryKey, double noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (short)noEntryKey, (double)noEntryValue);
    }

    public TShortDoubleHashMap(short[] keys, double[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((short)keys[i], (double)values[i]);
            ++i;
        }
    }

    public TShortDoubleHashMap(TShortDoubleMap map) {
        super((int)map.size());
        if (map instanceof TShortDoubleHashMap) {
            TShortDoubleHashMap hashmap = (TShortDoubleHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((short[])this._set, (short)this.no_entry_key);
            }
            if (this.no_entry_value != 0.0) {
                Arrays.fill((double[])this._values, (double)this.no_entry_value);
            }
            this.setUp((int)TShortDoubleHashMap.saturatedCast((long)TShortDoubleHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TShortDoubleMap)map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new double[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        short[] oldKeys = this._set;
        double[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new short[newCapacity];
        this._values = new double[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            short o = oldKeys[i];
            int index = this.insertKey((short)o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public double put(short key, double value) {
        int index = this.insertKey((short)key);
        return this.doPut((short)key, (double)value, (int)index);
    }

    @Override
    public double putIfAbsent(short key, double value) {
        int index = this.insertKey((short)key);
        if (index >= 0) return this.doPut((short)key, (double)value, (int)index);
        return this._values[-index - 1];
    }

    private double doPut(short key, double value, int index) {
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
    public void putAll(Map<? extends Short, ? extends Double> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Short, ? extends Double>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Short, ? extends Double> entry = iterator.next();
            this.put((short)entry.getKey().shortValue(), (double)entry.getValue().doubleValue());
        }
    }

    @Override
    public void putAll(TShortDoubleMap map) {
        this.ensureCapacity((int)map.size());
        TShortDoubleIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((short)iter.key(), (double)iter.value());
        }
    }

    @Override
    public double get(short key) {
        double d;
        int index = this.index((short)key);
        if (index < 0) {
            d = this.no_entry_value;
            return d;
        }
        d = this._values[index];
        return d;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((short[])this._set, (int)0, (int)this._set.length, (short)this.no_entry_key);
        Arrays.fill((double[])this._values, (int)0, (int)this._values.length, (double)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public double remove(short key) {
        double prev = this.no_entry_value;
        int index = this.index((short)key);
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
    public TShortSet keySet() {
        return new TKeyView((TShortDoubleHashMap)this);
    }

    @Override
    public short[] keys() {
        short[] keys = new short[this.size()];
        if (keys.length == 0) {
            return keys;
        }
        short[] k = this._set;
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
    public short[] keys(short[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new short[size];
        }
        short[] keys = this._set;
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
    public TDoubleCollection valueCollection() {
        return new TValueView((TShortDoubleHashMap)this);
    }

    @Override
    public double[] values() {
        double[] vals = new double[this.size()];
        if (vals.length == 0) {
            return vals;
        }
        double[] v = this._values;
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
    public double[] values(double[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new double[size];
        }
        double[] v = this._values;
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
    public boolean containsValue(double val) {
        byte[] states = this._states;
        double[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (states[i] != 1 || val != vals[i]);
        return true;
    }

    @Override
    public boolean containsKey(short key) {
        return this.contains((short)key);
    }

    @Override
    public TShortDoubleIterator iterator() {
        return new TShortDoubleHashIterator((TShortDoubleHashMap)this, (TShortDoubleHashMap)this);
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.forEach((TShortProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        byte[] states = this._states;
        double[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((double)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TShortDoubleProcedure procedure) {
        byte[] states = this._states;
        short[] keys = this._set;
        double[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((short)keys[i], (double)values[i]));
        return false;
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        byte[] states = this._states;
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute((double)values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TShortDoubleProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        short[] keys = this._set;
        double[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((short)keys[i], (double)values[i])) continue;
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
    public boolean increment(short key) {
        return this.adjustValue((short)key, (double)1.0);
    }

    @Override
    public boolean adjustValue(short key, double amount) {
        int index = this.index((short)key);
        if (index < 0) {
            return false;
        }
        double[] arrd = this._values;
        int n = index;
        arrd[n] = arrd[n] + amount;
        return true;
    }

    @Override
    public double adjustOrPutValue(short key, double adjust_amount, double put_amount) {
        boolean isNewMapping;
        double newValue;
        int index = this.insertKey((short)key);
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
        byte previousState = this._states[index];
        if (!isNewMapping) return newValue;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return newValue;
    }

    /*
     * Unable to fully structure code
     */
    public boolean equals(Object other) {
        if (!(other instanceof TShortDoubleMap)) {
            return false;
        }
        that = (TShortDoubleMap)other;
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
            if (!that.containsKey((short)key)) {
                return false;
            }
            this_value = values[i];
            that_value = that.get((short)key);
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
            hashcode += HashFunctions.hash((int)this._set[i]) ^ HashFunctions.hash((double)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TShortDoubleProcedure)new TShortDoubleProcedure((TShortDoubleHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TShortDoubleHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(short key, double value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((int)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((double)value);
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
            out.writeShort((int)this._set[i]);
            out.writeDouble((double)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            short key = in.readShort();
            double val = in.readDouble();
            this.put((short)key, (double)val);
        }
    }

    static /* synthetic */ short access$000(TShortDoubleHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TShortDoubleHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TShortDoubleHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ double access$300(TShortDoubleHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ double access$400(TShortDoubleHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TShortDoubleHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TShortDoubleHashMap x0) {
        return x0._size;
    }
}

