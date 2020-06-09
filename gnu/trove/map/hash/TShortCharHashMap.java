/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TShortCharHash;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.hash.TShortCharHashMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
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

public class TShortCharHashMap
extends TShortCharHash
implements TShortCharMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TShortCharHashMap() {
    }

    public TShortCharHashMap(int initialCapacity) {
        super((int)initialCapacity);
    }

    public TShortCharHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
    }

    public TShortCharHashMap(int initialCapacity, float loadFactor, short noEntryKey, char noEntryValue) {
        super((int)initialCapacity, (float)loadFactor, (short)noEntryKey, (char)noEntryValue);
    }

    public TShortCharHashMap(short[] keys, char[] values) {
        super((int)Math.max((int)keys.length, (int)values.length));
        int size = Math.min((int)keys.length, (int)values.length);
        int i = 0;
        while (i < size) {
            this.put((short)keys[i], (char)values[i]);
            ++i;
        }
    }

    public TShortCharHashMap(TShortCharMap map) {
        super((int)map.size());
        if (map instanceof TShortCharHashMap) {
            TShortCharHashMap hashmap = (TShortCharHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill((short[])this._set, (short)this.no_entry_key);
            }
            if (this.no_entry_value != '\u0000') {
                Arrays.fill((char[])this._values, (char)this.no_entry_value);
            }
            this.setUp((int)TShortCharHashMap.saturatedCast((long)TShortCharHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll((TShortCharMap)map);
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
        short[] oldKeys = this._set;
        char[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new short[newCapacity];
        this._values = new char[newCapacity];
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
    public char put(short key, char value) {
        int index = this.insertKey((short)key);
        return this.doPut((short)key, (char)value, (int)index);
    }

    @Override
    public char putIfAbsent(short key, char value) {
        int index = this.insertKey((short)key);
        if (index >= 0) return this.doPut((short)key, (char)value, (int)index);
        return this._values[-index - 1];
    }

    private char doPut(short key, char value, int index) {
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
    public void putAll(Map<? extends Short, ? extends Character> map) {
        this.ensureCapacity((int)map.size());
        Iterator<Map.Entry<? extends Short, ? extends Character>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<? extends Short, ? extends Character> entry = iterator.next();
            this.put((short)entry.getKey().shortValue(), (char)entry.getValue().charValue());
        }
    }

    @Override
    public void putAll(TShortCharMap map) {
        this.ensureCapacity((int)map.size());
        TShortCharIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put((short)iter.key(), (char)iter.value());
        }
    }

    @Override
    public char get(short key) {
        char c;
        int index = this.index((short)key);
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
        Arrays.fill((short[])this._set, (int)0, (int)this._set.length, (short)this.no_entry_key);
        Arrays.fill((char[])this._values, (int)0, (int)this._values.length, (char)this.no_entry_value);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        if (0 != this._size) return false;
        return true;
    }

    @Override
    public char remove(short key) {
        char prev = this.no_entry_value;
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
        return new TKeyView((TShortCharHashMap)this);
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
    public TCharCollection valueCollection() {
        return new TValueView((TShortCharHashMap)this);
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
    public boolean containsKey(short key) {
        return this.contains((short)key);
    }

    @Override
    public TShortCharIterator iterator() {
        return new TShortCharHashIterator((TShortCharHashMap)this, (TShortCharHashMap)this);
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.forEach((TShortProcedure)procedure);
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
    public boolean forEachEntry(TShortCharProcedure procedure) {
        byte[] states = this._states;
        short[] keys = this._set;
        char[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((short)keys[i], (char)values[i]));
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
    public boolean retainEntries(TShortCharProcedure procedure) {
        boolean modified = false;
        byte[] states = this._states;
        short[] keys = this._set;
        char[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((short)keys[i], (char)values[i])) continue;
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
        return this.adjustValue((short)key, (char)'\u0001');
    }

    @Override
    public boolean adjustValue(short key, char amount) {
        int index = this.index((short)key);
        if (index < 0) {
            return false;
        }
        char[] arrc = this._values;
        int n = index;
        arrc[n] = (char)(arrc[n] + amount);
        return true;
    }

    @Override
    public char adjustOrPutValue(short key, char adjust_amount, char put_amount) {
        boolean isNewMapping;
        char newValue;
        int index = this.insertKey((short)key);
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
        if (!(other instanceof TShortCharMap)) {
            return false;
        }
        that = (TShortCharMap)other;
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
        this.forEachEntry((TShortCharProcedure)new TShortCharProcedure((TShortCharHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TShortCharHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(short key, char value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)", ");
                }
                this.val$buf.append((int)key);
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
            out.writeShort((int)this._set[i]);
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
            short key = in.readShort();
            char val = in.readChar();
            this.put((short)key, (char)val);
        }
    }

    static /* synthetic */ short access$000(TShortCharHashMap x0) {
        return x0.no_entry_key;
    }

    static /* synthetic */ int access$100(TShortCharHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TShortCharHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ char access$300(TShortCharHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ char access$400(TShortCharHashMap x0) {
        return x0.no_entry_value;
    }

    static /* synthetic */ int access$500(TShortCharHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$600(TShortCharHashMap x0) {
        return x0._size;
    }
}

