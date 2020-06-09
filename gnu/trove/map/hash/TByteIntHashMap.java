/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteIntHash;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.map.TByteIntMap;
import gnu.trove.map.hash.TByteIntHashMap;
import gnu.trove.procedure.TByteIntProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TByteIntHashMap
extends TByteIntHash
implements TByteIntMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TByteIntHashMap() {
    }

    public TByteIntHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TByteIntHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TByteIntHashMap(int initialCapacity, float loadFactor, byte noEntryKey, int noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (byte)noEntryKey, (int)noEntryValue);
    }

    public TByteIntHashMap(byte[] keys, int[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((byte)keys[i], (int)values[i]);
            ++i;
        }
    }

    public TByteIntHashMap(TByteIntMap map) {
        super((int)map.size());
        if (map instanceof TByteIntHashMap) {
            TByteIntHashMap hashmap = (TByteIntHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((byte[])this._set, (byte)this.no_entry_key);
            }
            if (this.no_entry_value != 0) {
                Arrays.fill((int[])this._values, (int)this.no_entry_value);
            }
            this.setUp((int)TByteIntHashMap.saturatedCast((long)TByteIntHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TByteIntMap)map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new int[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        byte[] oldKeys = this._set;
        int[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._values = new int[newCapacity];
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
    public int put(byte key, int value) {
        int index = this.insertKey((byte)key);
        return this.doPut((byte)key, (int)value, (int)index);
    }

    @Override
    public int putIfAbsent(byte key, int value) {
        int index = this.insertKey((byte)key);
        if (index >= 0) return this.doPut((byte)key, (int)value, (int)index);
        return this._values[-index - 1];
    }

    private int doPut(byte key, int value, int index) {
        int previous = this.no_entry_value;
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
    public void putAll(Map<? extends Byte, ? extends Integer> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Byte, ? extends Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Byte, ? extends Integer> entry = iterator.next();
            this.put((byte)entry.getKey().byteValue(), (int)entry.getValue().intValue());
        }
    }

    @Override
    public void putAll(TByteIntMap map) {
        this.ensureCapacity((int)map.size());
        TByteIntIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((byte)iter.key(), (int)iter.value());
        }
    }

    @Override
    public int get(byte key) {
        int n;
        int index = this.index((byte)key);
        if (index < 0) {
            n = this.no_entry_value;
            return n;
        }
        n = this._values[index];
        return n;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((byte[])this._set, (int)0, (int)this._set.length, (byte)this.no_entry_key);
        Arrays.fill((int[])this._values, (int)0, (int)this._values.length, (int)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public int remove(byte key) {
        int prev = this.no_entry_value;
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
        return new TKeyView((TByteIntHashMap)this);
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
    public TIntCollection valueCollection() {
        return new TValueView((TByteIntHashMap)this);
    }

    @Override
    public int[] values() {
        int[] vals = new int[this.size()];
        if (vals.length == 0) {
            return vals;
        }
        int[] v = this._values;
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
    public int[] values(int[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new int[size];
        }
        int[] v = this._values;
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
    public boolean containsValue(int val) {
        byte[] states = this._states;
        int[] vals = this._values;
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
    public TByteIntIterator iterator() {
        return new TByteIntHashIterator((TByteIntHashMap)this, (TByteIntHashMap)this);
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.forEach((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        byte[] states = this._states;
        int[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((int)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TByteIntProcedure procedure) {
        byte[] states = this._states;
        byte[] keys = this._set;
        int[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((byte)keys[i], (int)values[i]));
        return false;
    }

    @Override
    public void transformValues(TIntFunction function) {
        byte[] states = this._states;
        int[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute((int)values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TByteIntProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        byte[] keys = this._set;
        int[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((byte)keys[i], (int)values[i])) continue;
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
        return this.adjustValue((byte)key, (int)1);
    }

    @Override
    public boolean adjustValue(byte key, int amount) {
        int index = this.index((byte)key);
        if (index < 0) {
            return false;
        }
        int[] arrn = this._values;
        int n = index;
        arrn[n] = arrn[n] + amount;
        return true;
    }

    @Override
    public int adjustOrPutValue(byte key, int adjust_amount, int put_amount) {
        boolean isNewMapping;
        int newValue;
        int index = this.insertKey((byte)key);
        if (index < 0) {
            index = -index - 1;
            int[] arrn = this._values;
            int n = index;
            int n2 = arrn[n] + adjust_amount;
            arrn[n] = n2;
            newValue = n2;
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
        if (!(other instanceof TByteIntMap)) {
            return false;
        }
        that = (TByteIntMap)other;
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
        this.forEachEntry((TByteIntProcedure)new TByteIntProcedure((TByteIntHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TByteIntHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(byte key, int value) {
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
            out.writeInt((int)this._values[i]);
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
            int val = in.readInt();
            this.put((byte)key, (int)val);
        }
    }

    static /* synthetic */ byte access$000(TByteIntHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TByteIntHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TByteIntHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$300(TByteIntHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$400(TByteIntHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TByteIntHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TByteIntHashMap x0) {
        return x0._size;
    }
}

