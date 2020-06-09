/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteLongHash;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.map.hash.TByteLongHashMap;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TByteLongHashMap
extends TByteLongHash
implements TByteLongMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TByteLongHashMap() {
    }

    public TByteLongHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TByteLongHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TByteLongHashMap(int initialCapacity, float loadFactor, byte noEntryKey, long noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (byte)noEntryKey, (long)noEntryValue);
    }

    public TByteLongHashMap(byte[] keys, long[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((byte)keys[i], (long)values[i]);
            ++i;
        }
    }

    public TByteLongHashMap(TByteLongMap map) {
        super((int)map.size());
        if (map instanceof TByteLongHashMap) {
            TByteLongHashMap hashmap = (TByteLongHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((byte[])this._set, (byte)this.no_entry_key);
            }
            if (this.no_entry_value != 0L) {
                Arrays.fill((long[])this._values, (long)this.no_entry_value);
            }
            this.setUp((int)TByteLongHashMap.saturatedCast((long)TByteLongHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TByteLongMap)map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new long[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        byte[] oldKeys = this._set;
        long[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._values = new long[newCapacity];
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
    public long put(byte key, long value) {
        int index = this.insertKey((byte)key);
        return this.doPut((byte)key, (long)value, (int)index);
    }

    @Override
    public long putIfAbsent(byte key, long value) {
        int index = this.insertKey((byte)key);
        if (index >= 0) return this.doPut((byte)key, (long)value, (int)index);
        return this._values[-index - 1];
    }

    private long doPut(byte key, long value, int index) {
        long previous = this.no_entry_value;
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
    public void putAll(Map<? extends Byte, ? extends Long> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Byte, ? extends Long>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Byte, ? extends Long> entry = iterator.next();
            this.put((byte)entry.getKey().byteValue(), (long)entry.getValue().longValue());
        }
    }

    @Override
    public void putAll(TByteLongMap map) {
        this.ensureCapacity((int)map.size());
        TByteLongIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((byte)iter.key(), (long)iter.value());
        }
    }

    @Override
    public long get(byte key) {
        long l;
        int index = this.index((byte)key);
        if (index < 0) {
            l = this.no_entry_value;
            return l;
        }
        l = this._values[index];
        return l;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((byte[])this._set, (int)0, (int)this._set.length, (byte)this.no_entry_key);
        Arrays.fill((long[])this._values, (int)0, (int)this._values.length, (long)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public long remove(byte key) {
        long prev = this.no_entry_value;
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
        return new TKeyView((TByteLongHashMap)this);
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
    public TLongCollection valueCollection() {
        return new TValueView((TByteLongHashMap)this);
    }

    @Override
    public long[] values() {
        long[] vals = new long[this.size()];
        if (vals.length == 0) {
            return vals;
        }
        long[] v = this._values;
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
    public long[] values(long[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new long[size];
        }
        long[] v = this._values;
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
    public boolean containsValue(long val) {
        byte[] states = this._states;
        long[] vals = this._values;
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
    public TByteLongIterator iterator() {
        return new TByteLongHashIterator((TByteLongHashMap)this, (TByteLongHashMap)this);
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.forEach((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure procedure) {
        byte[] states = this._states;
        long[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((long)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TByteLongProcedure procedure) {
        byte[] states = this._states;
        byte[] keys = this._set;
        long[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((byte)keys[i], (long)values[i]));
        return false;
    }

    @Override
    public void transformValues(TLongFunction function) {
        byte[] states = this._states;
        long[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute((long)values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TByteLongProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        byte[] keys = this._set;
        long[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((byte)keys[i], (long)values[i])) continue;
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
        return this.adjustValue((byte)key, (long)1L);
    }

    @Override
    public boolean adjustValue(byte key, long amount) {
        int index = this.index((byte)key);
        if (index < 0) {
            return false;
        }
        long[] arrl = this._values;
        int n = index;
        arrl[n] = arrl[n] + amount;
        return true;
    }

    @Override
    public long adjustOrPutValue(byte key, long adjust_amount, long put_amount) {
        boolean isNewMapping;
        long newValue;
        int index = this.insertKey((byte)key);
        if (index < 0) {
            index = -index - 1;
            long[] arrl = this._values;
            int n = index;
            long l = arrl[n] + adjust_amount;
            arrl[n] = l;
            newValue = l;
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
        if (!(other instanceof TByteLongMap)) {
            return false;
        }
        that = (TByteLongMap)other;
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
            hashcode += HashFunctions.hash((int)this._set[i]) ^ HashFunctions.hash((long)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TByteLongProcedure)new TByteLongProcedure((TByteLongHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TByteLongHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(byte key, long value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((int)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((long)value);
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
            out.writeLong((long)this._values[i]);
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
            long val = in.readLong();
            this.put((byte)key, (long)val);
        }
    }

    static /* synthetic */ byte access$000(TByteLongHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TByteLongHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TByteLongHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ long access$300(TByteLongHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ long access$400(TByteLongHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TByteLongHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TByteLongHashMap x0) {
        return x0._size;
    }
}

