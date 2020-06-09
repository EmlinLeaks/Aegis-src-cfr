/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TLongByteHash;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.hash.TLongByteHashMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TLongByteHashMap
extends TLongByteHash
implements TLongByteMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient byte[] _values;

    public TLongByteHashMap() {
    }

    public TLongByteHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TLongByteHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TLongByteHashMap(int initialCapacity, float loadFactor, long noEntryKey, byte noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (long)noEntryKey, (byte)noEntryValue);
    }

    public TLongByteHashMap(long[] keys, byte[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((long)keys[i], (byte)values[i]);
            ++i;
        }
    }

    public TLongByteHashMap(TLongByteMap map) {
        super((int)map.size());
        if (map instanceof TLongByteHashMap) {
            TLongByteHashMap hashmap = (TLongByteHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill((long[])this._set, (long)this.no_entry_key);
            }
            if (this.no_entry_value != 0) {
                Arrays.fill((byte[])this._values, (byte)this.no_entry_value);
            }
            this.setUp((int)TLongByteHashMap.saturatedCast((long)TLongByteHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TLongByteMap)map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new byte[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        long[] oldKeys = this._set;
        byte[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._values = new byte[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            long o = oldKeys[i];
            int index = this.insertKey((long)o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public byte put(long key, byte value) {
        int index = this.insertKey((long)key);
        return this.doPut((long)key, (byte)value, (int)index);
    }

    @Override
    public byte putIfAbsent(long key, byte value) {
        int index = this.insertKey((long)key);
        if (index >= 0) return this.doPut((long)key, (byte)value, (int)index);
        return this._values[-index - 1];
    }

    private byte doPut(long key, byte value, int index) {
        byte previous = this.no_entry_value;
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
    public void putAll(Map<? extends Long, ? extends Byte> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Long, ? extends Byte>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Long, ? extends Byte> entry = iterator.next();
            this.put((long)entry.getKey().longValue(), (byte)entry.getValue().byteValue());
        }
    }

    @Override
    public void putAll(TLongByteMap map) {
        this.ensureCapacity((int)map.size());
        TLongByteIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((long)iter.key(), (byte)iter.value());
        }
    }

    @Override
    public byte get(long key) {
        byte by;
        int index = this.index((long)key);
        if (index < 0) {
            by = this.no_entry_value;
            return by;
        }
        by = this._values[index];
        return by;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((long[])this._set, (int)0, (int)this._set.length, (long)this.no_entry_key);
        Arrays.fill((byte[])this._values, (int)0, (int)this._values.length, (byte)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public byte remove(long key) {
        byte prev = this.no_entry_value;
        int index = this.index((long)key);
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
    public TLongSet keySet() {
        return new TKeyView((TLongByteHashMap)this);
    }

    @Override
    public long[] keys() {
        long[] keys = new long[this.size()];
        if (keys.length == 0) {
            return keys;
        }
        long[] k = this._set;
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
    public long[] keys(long[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new long[size];
        }
        long[] keys = this._set;
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
    public TByteCollection valueCollection() {
        return new TValueView((TLongByteHashMap)this);
    }

    @Override
    public byte[] values() {
        byte[] vals = new byte[this.size()];
        if (vals.length == 0) {
            return vals;
        }
        byte[] v = this._values;
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
    public byte[] values(byte[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new byte[size];
        }
        byte[] v = this._values;
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
    public boolean containsValue(byte val) {
        byte[] states = this._states;
        byte[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (states[i] != 1 || val != vals[i]);
        return true;
    }

    @Override
    public boolean containsKey(long key) {
        return this.contains((long)key);
    }

    @Override
    public TLongByteIterator iterator() {
        return new TLongByteHashIterator((TLongByteHashMap)this, (TLongByteHashMap)this);
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.forEach((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure procedure) {
        byte[] states = this._states;
        byte[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((byte)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TLongByteProcedure procedure) {
        byte[] states = this._states;
        long[] keys = this._set;
        byte[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((long)keys[i], (byte)values[i]));
        return false;
    }

    @Override
    public void transformValues(TByteFunction function) {
        byte[] states = this._states;
        byte[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute((byte)values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TLongByteProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        long[] keys = this._set;
        byte[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((long)keys[i], (byte)values[i])) continue;
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
    public boolean increment(long key) {
        return this.adjustValue((long)key, (byte)1);
    }

    @Override
    public boolean adjustValue(long key, byte amount) {
        int index = this.index((long)key);
        if (index < 0) {
            return false;
        }
        byte[] arrby = this._values;
        int n = index;
        arrby[n] = (byte)(arrby[n] + amount);
        return true;
    }

    @Override
    public byte adjustOrPutValue(long key, byte adjust_amount, byte put_amount) {
        byte newValue;
        boolean isNewMapping;
        int index = this.insertKey((long)key);
        if (index < 0) {
            index = -index - 1;
            byte[] arrby = this._values;
            int n = index;
            byte by = (byte)(arrby[n] + adjust_amount);
            arrby[n] = by;
            newValue = by;
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
        if (!(other instanceof TLongByteMap)) {
            return false;
        }
        that = (TLongByteMap)other;
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
            if (!that.containsKey((long)key)) {
                return false;
            }
            this_value = values[i];
            that_value = that.get((long)key);
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
            hashcode += HashFunctions.hash((long)this._set[i]) ^ HashFunctions.hash((int)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TLongByteProcedure)new TLongByteProcedure((TLongByteHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TLongByteHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(long key, byte value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((long)key);
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
            out.writeLong((long)this._set[i]);
            out.writeByte((int)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            long key = in.readLong();
            byte val = in.readByte();
            this.put((long)key, (byte)val);
        }
    }

    static /* synthetic */ long access$000(TLongByteHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TLongByteHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TLongByteHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ byte access$300(TLongByteHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ byte access$400(TLongByteHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TLongByteHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TLongByteHashMap x0) {
        return x0._size;
    }
}

