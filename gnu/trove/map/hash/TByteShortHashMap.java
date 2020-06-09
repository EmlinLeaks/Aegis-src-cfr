/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteShortHash;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.hash.TByteShortHashMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TByteShortHashMap
extends TByteShortHash
implements TByteShortMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TByteShortHashMap() {
    }

    public TByteShortHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TByteShortHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TByteShortHashMap(int initialCapacity, float loadFactor, byte noEntryKey, short noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (byte)noEntryKey, (short)noEntryValue);
    }

    public TByteShortHashMap(byte[] keys, short[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((byte)keys[i], (short)values[i]);
            ++i;
        }
    }

    public TByteShortHashMap(TByteShortMap map) {
        super((int)map.size());
        if (map instanceof TByteShortHashMap) {
            TByteShortHashMap hashmap = (TByteShortHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((byte[])this._set, (byte)this.no_entry_key);
            }
            if (this.no_entry_value != 0) {
                Arrays.fill((short[])this._values, (short)this.no_entry_value);
            }
            this.setUp((int)TByteShortHashMap.saturatedCast((long)TByteShortHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TByteShortMap)map);
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
        byte[] oldKeys = this._set;
        short[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._values = new short[newCapacity];
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
    public short put(byte key, short value) {
        int index = this.insertKey((byte)key);
        return this.doPut((byte)key, (short)value, (int)index);
    }

    @Override
    public short putIfAbsent(byte key, short value) {
        int index = this.insertKey((byte)key);
        if (index >= 0) return this.doPut((byte)key, (short)value, (int)index);
        return this._values[-index - 1];
    }

    private short doPut(byte key, short value, int index) {
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
    public void putAll(Map<? extends Byte, ? extends Short> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Byte, ? extends Short>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Byte, ? extends Short> entry = iterator.next();
            this.put((byte)entry.getKey().byteValue(), (short)entry.getValue().shortValue());
        }
    }

    @Override
    public void putAll(TByteShortMap map) {
        this.ensureCapacity((int)map.size());
        TByteShortIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((byte)iter.key(), (short)iter.value());
        }
    }

    @Override
    public short get(byte key) {
        short s;
        int index = this.index((byte)key);
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
        Arrays.fill((byte[])this._set, (int)0, (int)this._set.length, (byte)this.no_entry_key);
        Arrays.fill((short[])this._values, (int)0, (int)this._values.length, (short)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public short remove(byte key) {
        short prev = this.no_entry_value;
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
        return new TKeyView((TByteShortHashMap)this);
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
    public TShortCollection valueCollection() {
        return new TValueView((TByteShortHashMap)this);
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
    public boolean containsKey(byte key) {
        return this.contains((byte)key);
    }

    @Override
    public TByteShortIterator iterator() {
        return new TByteShortHashIterator((TByteShortHashMap)this, (TByteShortHashMap)this);
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.forEach((TByteProcedure)procedure);
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
    public boolean forEachEntry(TByteShortProcedure procedure) {
        byte[] states = this._states;
        byte[] keys = this._set;
        short[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((byte)keys[i], (short)values[i]));
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
    public boolean retainEntries(TByteShortProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        byte[] keys = this._set;
        short[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((byte)keys[i], (short)values[i])) continue;
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
        return this.adjustValue((byte)key, (short)1);
    }

    @Override
    public boolean adjustValue(byte key, short amount) {
        int index = this.index((byte)key);
        if (index < 0) {
            return false;
        }
        short[] arrs = this._values;
        int n = index;
        arrs[n] = (short)(arrs[n] + amount);
        return true;
    }

    @Override
    public short adjustOrPutValue(byte key, short adjust_amount, short put_amount) {
        boolean isNewMapping;
        short newValue;
        int index = this.insertKey((byte)key);
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
        if (!(other instanceof TByteShortMap)) {
            return false;
        }
        that = (TByteShortMap)other;
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
            hashcode += HashFunctions.hash((int)this._set[i]) ^ HashFunctions.hash((int)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TByteShortProcedure)new TByteShortProcedure((TByteShortHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TByteShortHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(byte key, short value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((int)key);
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
            out.writeByte((int)this._set[i]);
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
            byte key = in.readByte();
            short val = in.readShort();
            this.put((byte)key, (short)val);
        }
    }

    static /* synthetic */ byte access$000(TByteShortHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TByteShortHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TByteShortHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ short access$300(TByteShortHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ short access$400(TByteShortHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TByteShortHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TByteShortHashMap x0) {
        return x0._size;
    }
}

