/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TIntLongHash;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TIntLongHashMap
extends TIntLongHash
implements TIntLongMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TIntLongHashMap() {
    }

    public TIntLongHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TIntLongHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TIntLongHashMap(int initialCapacity, float loadFactor, int noEntryKey, long noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (int)noEntryKey, (long)noEntryValue);
    }

    public TIntLongHashMap(int[] keys, long[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((int)keys[i], (long)values[i]);
            ++i;
        }
    }

    public TIntLongHashMap(TIntLongMap map) {
        super((int)map.size());
        if (map instanceof TIntLongHashMap) {
            TIntLongHashMap hashmap = (TIntLongHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((int[])this._set, (int)this.no_entry_key);
            }
            if (this.no_entry_value != 0L) {
                Arrays.fill((long[])this._values, (long)this.no_entry_value);
            }
            this.setUp((int)TIntLongHashMap.saturatedCast((long)TIntLongHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TIntLongMap)map);
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
        int[] oldKeys = this._set;
        long[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new int[newCapacity];
        this._values = new long[newCapacity];
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
    public long put(int key, long value) {
        int index = this.insertKey((int)key);
        return this.doPut((int)key, (long)value, (int)index);
    }

    @Override
    public long putIfAbsent(int key, long value) {
        int index = this.insertKey((int)key);
        if (index >= 0) return this.doPut((int)key, (long)value, (int)index);
        return this._values[-index - 1];
    }

    private long doPut(int key, long value, int index) {
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
    public void putAll(Map<? extends Integer, ? extends Long> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Integer, ? extends Long>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Integer, ? extends Long> entry = iterator.next();
            this.put((int)entry.getKey().intValue(), (long)entry.getValue().longValue());
        }
    }

    @Override
    public void putAll(TIntLongMap map) {
        this.ensureCapacity((int)map.size());
        TIntLongIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((int)iter.key(), (long)iter.value());
        }
    }

    @Override
    public long get(int key) {
        long l;
        int index = this.index((int)key);
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
        Arrays.fill((int[])this._set, (int)0, (int)this._set.length, (int)this.no_entry_key);
        Arrays.fill((long[])this._values, (int)0, (int)this._values.length, (long)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public long remove(int key) {
        long prev = this.no_entry_value;
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
        return new TKeyView((TIntLongHashMap)this);
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
    public TLongCollection valueCollection() {
        return new TValueView((TIntLongHashMap)this);
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
    public boolean containsKey(int key) {
        return this.contains((int)key);
    }

    @Override
    public TIntLongIterator iterator() {
        return new TIntLongHashIterator((TIntLongHashMap)this, (TIntLongHashMap)this);
    }

    @Override
    public boolean forEachKey(TIntProcedure procedure) {
        return this.forEach((TIntProcedure)procedure);
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
    public boolean forEachEntry(TIntLongProcedure procedure) {
        byte[] states = this._states;
        int[] keys = this._set;
        long[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((int)keys[i], (long)values[i]));
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
    public boolean retainEntries(TIntLongProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        int[] keys = this._set;
        long[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((int)keys[i], (long)values[i])) continue;
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
        return this.adjustValue((int)key, (long)1L);
    }

    @Override
    public boolean adjustValue(int key, long amount) {
        int index = this.index((int)key);
        if (index < 0) {
            return false;
        }
        long[] arrl = this._values;
        int n = index;
        arrl[n] = arrl[n] + amount;
        return true;
    }

    @Override
    public long adjustOrPutValue(int key, long adjust_amount, long put_amount) {
        boolean isNewMapping;
        long newValue;
        int index = this.insertKey((int)key);
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
        if (!(other instanceof TIntLongMap)) {
            return false;
        }
        that = (TIntLongMap)other;
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
            hashcode += HashFunctions.hash((int)this._set[i]) ^ HashFunctions.hash((long)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TIntLongProcedure)new TIntLongProcedure((TIntLongHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TIntLongHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(int key, long value) {
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
            out.writeInt((int)this._set[i]);
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
            int key = in.readInt();
            long val = in.readLong();
            this.put((int)key, (long)val);
        }
    }

    static /* synthetic */ int access$000(TIntLongHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TIntLongHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TIntLongHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ long access$300(TIntLongHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ long access$400(TIntLongHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TIntLongHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TIntLongHashMap x0) {
        return x0._size;
    }
}

