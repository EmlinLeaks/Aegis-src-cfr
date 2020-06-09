/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharHash;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TCharObjectHashMap<V>
extends TCharHash
implements TCharObjectMap<V>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TCharObjectProcedure<V> PUT_ALL_PROC = new TCharObjectProcedure<V>((TCharObjectHashMap)this){
        final /* synthetic */ TCharObjectHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(char key, V value) {
            this.this$0.put((char)key, value);
            return true;
        }
    };
    protected transient V[] _values;
    protected char no_entry_key;

    public TCharObjectHashMap() {
    }

    public TCharObjectHashMap(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_key = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TCharObjectHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TCharObjectHashMap(int initialCapacity, float loadFactor, char noEntryKey) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = noEntryKey;
    }

    public TCharObjectHashMap(TCharObjectMap<? extends V> map) {
        this((int)map.size(), (float)0.5f, (char)map.getNoEntryKey());
        this.putAll(map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new Object[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        char[] oldKeys = this._set;
        V[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new char[newCapacity];
        this._values = new Object[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            char o = oldKeys[i];
            int index = this.insertKey((char)o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public char getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(char key) {
        return this.contains((char)key);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public boolean containsValue(Object val) {
        states = this._states;
        vals = this._values;
        if (null == val) {
            i = vals.length;
            do {
                if (i-- <= 0) return false;
            } while (states[i] != 1 || null != vals[i]);
            return true;
        }
        i = vals.length;
        do lbl-1000: // 3 sources:
        {
            if (i-- <= 0) return false;
            if (states[i] != 1) ** GOTO lbl-1000
            if (val == vals[i]) return true;
        } while (!val.equals(vals[i]));
        return true;
    }

    @Override
    public V get(char key) {
        V v;
        int index = this.index((char)key);
        if (index < 0) {
            v = null;
            return (V)((V)v);
        }
        v = (V)this._values[index];
        return (V)v;
    }

    @Override
    public V put(char key, V value) {
        int index = this.insertKey((char)key);
        return (V)this.doPut(value, (int)index);
    }

    @Override
    public V putIfAbsent(char key, V value) {
        int index = this.insertKey((char)key);
        if (index >= 0) return (V)this.doPut(value, (int)index);
        return (V)this._values[-index - 1];
    }

    private V doPut(V value, int index) {
        V previous = null;
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index - 1;
            previous = (V)this._values[index];
            isNewMapping = false;
        }
        this._values[index] = value;
        if (!isNewMapping) return (V)previous;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return (V)previous;
    }

    @Override
    public V remove(char key) {
        V prev = null;
        int index = this.index((char)key);
        if (index < 0) return (V)prev;
        prev = (V)this._values[index];
        this.removeAt((int)index);
        return (V)prev;
    }

    @Override
    protected void removeAt(int index) {
        this._values[index] = null;
        super.removeAt((int)index);
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> map) {
        Set<Map.Entry<Character, V>> set = map.entrySet();
        Iterator<Map.Entry<Character, V>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Character, V> entry = iterator.next();
            this.put((char)entry.getKey().charValue(), entry.getValue());
        }
    }

    @Override
    public void putAll(TCharObjectMap<? extends V> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((char[])this._set, (int)0, (int)this._set.length, (char)this.no_entry_key);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
        Arrays.fill((Object[])this._values, (int)0, (int)this._values.length, null);
    }

    @Override
    public TCharSet keySet() {
        return new KeyView((TCharObjectHashMap)this);
    }

    @Override
    public char[] keys() {
        char[] keys = new char[this.size()];
        char[] k = this._set;
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
    public char[] keys(char[] dest) {
        if (dest.length < this._size) {
            dest = new char[this._size];
        }
        char[] k = this._set;
        byte[] states = this._states;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            dest[j++] = k[i];
        }
        return dest;
    }

    @Override
    public Collection<V> valueCollection() {
        return new ValueView((TCharObjectHashMap)this);
    }

    @Override
    public Object[] values() {
        Object[] vals = new Object[this.size()];
        V[] v = this._values;
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
    public V[] values(V[] dest) {
        if (dest.length < this._size) {
            dest = (Object[])Array.newInstance(dest.getClass().getComponentType(), (int)this._size);
        }
        V[] v = this._values;
        byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            dest[j++] = v[i];
        }
        return dest;
    }

    @Override
    public TCharObjectIterator<V> iterator() {
        return new TCharObjectHashIterator<V>((TCharObjectHashMap)this, this);
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.forEach((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        byte[] states = this._states;
        V[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute(values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TCharObjectProcedure<? super V> procedure) {
        byte[] states = this._states;
        char[] keys = this._set;
        V[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((char)keys[i], values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TCharObjectProcedure<? super V> procedure) {
        boolean modified = false;
        byte[] states = this._states;
        char[] keys = this._set;
        V[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((char)keys[i], values[i])) continue;
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
    public void transformValues(TObjectFunction<V, V> function) {
        byte[] states = this._states;
        V[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute(values[i]);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TCharObjectMap)) {
            return false;
        }
        that = (TCharObjectMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        try {
            iter = this.iterator();
            do lbl-1000: // 3 sources:
            {
                if (iter.hasNext() == false) return true;
                iter.advance();
                key = iter.key();
                value = iter.value();
                if (value != null) continue;
                if (that.get((char)key) != null) return false;
                if (that.containsKey((char)key)) ** GOTO lbl-1000
                return false;
            } while (value.equals(that.get((char)key)));
            return false;
        }
        catch (ClassCastException iter) {
            // empty catch block
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        V[] values = this._values;
        byte[] states = this._states;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            hashcode += HashFunctions.hash((int)this._set[i]) ^ (values[i] == null ? 0 : values[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeChar((int)this.no_entry_key);
        out.writeInt((int)this._size);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeChar((int)this._set[i]);
            out.writeObject(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.no_entry_key = in.readChar();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            char key = in.readChar();
            Object val = in.readObject();
            this.put((char)key, val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TCharObjectProcedure<V>((TCharObjectHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TCharObjectHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(char key, Object value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)",");
                }
                this.val$buf.append((char)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((Object)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    static /* synthetic */ int access$000(TCharObjectHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$100(TCharObjectHashMap x0) {
        return x0._size;
    }
}

