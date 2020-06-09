/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TShortIntHash;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.map.TShortIntMap;
import gnu.trove.map.hash.TShortIntHashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
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

public class TShortIntHashMap
extends TShortIntHash
implements TShortIntMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TShortIntHashMap() {
    }

    public TShortIntHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TShortIntHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TShortIntHashMap(int initialCapacity, float loadFactor, short noEntryKey, int noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (short)noEntryKey, (int)noEntryValue);
    }

    public TShortIntHashMap(short[] keys, int[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((short)keys[i], (int)values[i]);
            ++i;
        }
    }

    public TShortIntHashMap(TShortIntMap map) {
        super((int)map.size());
        if (map instanceof TShortIntHashMap) {
            TShortIntHashMap hashmap = (TShortIntHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((short[])this._set, (short)this.no_entry_key);
            }
            if (this.no_entry_value != 0) {
                Arrays.fill((int[])this._values, (int)this.no_entry_value);
            }
            this.setUp((int)TShortIntHashMap.saturatedCast((long)TShortIntHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TShortIntMap)map);
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
        short[] oldKeys = this._set;
        int[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new short[newCapacity];
        this._values = new int[newCapacity];
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
    public int put(short key, int value) {
        int index = this.insertKey((short)key);
        return this.doPut((short)key, (int)value, (int)index);
    }

    @Override
    public int putIfAbsent(short key, int value) {
        int index = this.insertKey((short)key);
        if (index >= 0) return this.doPut((short)key, (int)value, (int)index);
        return this._values[-index - 1];
    }

    private int doPut(short key, int value, int index) {
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
    public void putAll(Map<? extends Short, ? extends Integer> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Short, ? extends Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Short, ? extends Integer> entry = iterator.next();
            this.put((short)entry.getKey().shortValue(), (int)entry.getValue().intValue());
        }
    }

    @Override
    public void putAll(TShortIntMap map) {
        this.ensureCapacity((int)map.size());
        TShortIntIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((short)iter.key(), (int)iter.value());
        }
    }

    @Override
    public int get(short key) {
        int n;
        int index = this.index((short)key);
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
        Arrays.fill((short[])this._set, (int)0, (int)this._set.length, (short)this.no_entry_key);
        Arrays.fill((int[])this._values, (int)0, (int)this._values.length, (int)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public int remove(short key) {
        int prev = this.no_entry_value;
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
        return new TKeyView((TShortIntHashMap)this);
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
    public TIntCollection valueCollection() {
        return new TValueView((TShortIntHashMap)this);
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
    public boolean containsKey(short key) {
        return this.contains((short)key);
    }

    @Override
    public TShortIntIterator iterator() {
        return new TShortIntHashIterator((TShortIntHashMap)this, (TShortIntHashMap)this);
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.forEach((TShortProcedure)procedure);
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
    public boolean forEachEntry(TShortIntProcedure procedure) {
        byte[] states = this._states;
        short[] keys = this._set;
        int[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((short)keys[i], (int)values[i]));
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
    public boolean retainEntries(TShortIntProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        short[] keys = this._set;
        int[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((short)keys[i], (int)values[i])) continue;
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
        return this.adjustValue((short)key, (int)1);
    }

    @Override
    public boolean adjustValue(short key, int amount) {
        int index = this.index((short)key);
        if (index < 0) {
            return false;
        }
        int[] arrn = this._values;
        int n = index;
        arrn[n] = arrn[n] + amount;
        return true;
    }

    @Override
    public int adjustOrPutValue(short key, int adjust_amount, int put_amount) {
        boolean isNewMapping;
        int newValue;
        int index = this.insertKey((short)key);
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
        if (!(other instanceof TShortIntMap)) {
            return false;
        }
        that = (TShortIntMap)other;
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
            hashcode += HashFunctions.hash((int)this._set[i]) ^ HashFunctions.hash((int)this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry((TShortIntProcedure)new TShortIntProcedure((TShortIntHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TShortIntHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(short key, int value) {
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
            out.writeShort((int)this._set[i]);
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
            short key = in.readShort();
            int val = in.readInt();
            this.put((short)key, (int)val);
        }
    }

    static /* synthetic */ short access$000(TShortIntHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TShortIntHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TShortIntHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$300(TShortIntHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$400(TShortIntHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TShortIntHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TShortIntHashMap x0) {
        return x0._size;
    }
}

