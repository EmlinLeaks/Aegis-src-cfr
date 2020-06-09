/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TLongCharHash;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.hash.TLongCharHashMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
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

public class TLongCharHashMap
extends TLongCharHash
implements TLongCharMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TLongCharHashMap() {
    }

    public TLongCharHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TLongCharHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TLongCharHashMap(int initialCapacity, float loadFactor, long noEntryKey, char noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (long)noEntryKey, (char)noEntryValue);
    }

    public TLongCharHashMap(long[] keys, char[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((long)keys[i], (char)values[i]);
            ++i;
        }
    }

    public TLongCharHashMap(TLongCharMap map) {
        super((int)map.size());
        if (map instanceof TLongCharHashMap) {
            TLongCharHashMap hashmap = (TLongCharHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill((long[])this._set, (long)this.no_entry_key);
            }
            if (this.no_entry_value != '\u0000') {
                Arrays.fill((char[])this._values, (char)this.no_entry_value);
            }
            this.setUp((int)TLongCharHashMap.saturatedCast((long)TLongCharHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TLongCharMap)map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new char[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        long[] oldKeys = this._set;
        char[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._values = new char[newCapacity];
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
    public char put(long key, char value) {
        int index = this.insertKey((long)key);
        return this.doPut((long)key, (char)value, (int)index);
    }

    @Override
    public char putIfAbsent(long key, char value) {
        int index = this.insertKey((long)key);
        if (index >= 0) return this.doPut((long)key, (char)value, (int)index);
        return this._values[-index - 1];
    }

    private char doPut(long key, char value, int index) {
        char previous = this.no_entry_value;
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
    public void putAll(Map<? extends Long, ? extends Character> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Long, ? extends Character>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Long, ? extends Character> entry = iterator.next();
            this.put((long)entry.getKey().longValue(), (char)entry.getValue().charValue());
        }
    }

    @Override
    public void putAll(TLongCharMap map) {
        this.ensureCapacity((int)map.size());
        TLongCharIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((long)iter.key(), (char)iter.value());
        }
    }

    @Override
    public char get(long key) {
        char c;
        int index = this.index((long)key);
        if (index < 0) {
            c = this.no_entry_value;
            return c;
        }
        c = this._values[index];
        return c;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((long[])this._set, (int)0, (int)this._set.length, (long)this.no_entry_key);
        Arrays.fill((char[])this._values, (int)0, (int)this._values.length, (char)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public char remove(long key) {
        char prev = this.no_entry_value;
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
        return new TKeyView((TLongCharHashMap)this);
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
    public TCharCollection valueCollection() {
        return new TValueView((TLongCharHashMap)this);
    }

    @Override
    public char[] values() {
        char[] vals = new char[this.size()];
        if (vals.length == 0) {
            return vals;
        }
        char[] v = this._values;
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
    public char[] values(char[] array) {
        int size = this.size();
        if (size == 0) {
            return array;
        }
        if (array.length < size) {
            array = new char[size];
        }
        char[] v = this._values;
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
    public boolean containsValue(char val) {
        byte[] states = this._states;
        char[] vals = this._values;
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
    public TLongCharIterator iterator() {
        return new TLongCharHashIterator((TLongCharHashMap)this, (TLongCharHashMap)this);
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.forEach((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        byte[] states = this._states;
        char[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((char)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TLongCharProcedure procedure) {
        byte[] states = this._states;
        long[] keys = this._set;
        char[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((long)keys[i], (char)values[i]));
        return false;
    }

    @Override
    public void transformValues(TCharFunction function) {
        byte[] states = this._states;
        char[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute((char)values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TLongCharProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        long[] keys = this._set;
        char[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((long)keys[i], (char)values[i])) continue;
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
        return this.adjustValue((long)key, (char)'\u0001');
    }

    @Override
    public boolean adjustValue(long key, char amount) {
        int index = this.index((long)key);
        if (index < 0) {
            return false;
        }
        char[] arrc = this._values;
        int n = index;
        arrc[n] = (char)(arrc[n] + amount);
        return true;
    }

    @Override
    public char adjustOrPutValue(long key, char adjust_amount, char put_amount) {
        boolean isNewMapping;
        char newValue;
        int index = this.insertKey((long)key);
        if (index < 0) {
            index = -index - 1;
            char[] arrc = this._values;
            int n = index;
            char c = (char)(arrc[n] + adjust_amount);
            arrc[n] = c;
            newValue = c;
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
        if (!(other instanceof TLongCharMap)) {
            return false;
        }
        that = (TLongCharMap)other;
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
        this.forEachEntry((TLongCharProcedure)new TLongCharProcedure((TLongCharHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TLongCharHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(long key, char value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((long)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((char)value);
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
            out.writeChar((int)this._values[i]);
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
            char val = in.readChar();
            this.put((long)key, (char)val);
        }
    }

    static /* synthetic */ long access$000(TLongCharHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TLongCharHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TLongCharHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ char access$300(TLongCharHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ char access$400(TLongCharHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TLongCharHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TLongCharHashMap x0) {
        return x0._size;
    }
}

