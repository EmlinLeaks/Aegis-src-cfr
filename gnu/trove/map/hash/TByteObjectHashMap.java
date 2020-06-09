/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteHash;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
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

public class TByteObjectHashMap<V>
extends TByteHash
implements TByteObjectMap<V>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TByteObjectProcedure<V> PUT_ALL_PROC = new TByteObjectProcedure<V>((TByteObjectHashMap)this){
        final /* synthetic */ TByteObjectHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(byte key, V value) {
            this.this$0.put((byte)key, value);
            return true;
        }
    };
    protected transient V[] _values;
    protected byte no_entry_key;

    public TByteObjectHashMap() {
    }

    public TByteObjectHashMap(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_key = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
    }

    public TByteObjectHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
    }

    public TByteObjectHashMap(int initialCapacity, float loadFactor, byte noEntryKey) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = noEntryKey;
    }

    public TByteObjectHashMap(TByteObjectMap<? extends V> map) {
        this((int)map.size(), (float)0.5f, (byte)map.getNoEntryKey());
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
        byte[] oldKeys = this._set;
        V[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._values = new Object[newCapacity];
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
    public byte getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(byte key) {
        return this.contains((byte)key);
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
    public V get(byte key) {
        V v;
        int index = this.index((byte)key);
        if (index < 0) {
            v = null;
            return (V)((V)v);
        }
        v = (V)this._values[index];
        return (V)v;
    }

    @Override
    public V put(byte key, V value) {
        int index = this.insertKey((byte)key);
        return (V)this.doPut(value, (int)index);
    }

    @Override
    public V putIfAbsent(byte key, V value) {
        int index = this.insertKey((byte)key);
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
    public V remove(byte key) {
        V prev = null;
        int index = this.index((byte)key);
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
    public void putAll(Map<? extends Byte, ? extends V> map) {
        Set<Map.Entry<Byte, V>> set = map.entrySet();
        Iterator<Map.Entry<Byte, V>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Byte, V> entry = iterator.next();
            this.put((byte)entry.getKey().byteValue(), entry.getValue());
        }
    }

    @Override
    public void putAll(TByteObjectMap<? extends V> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((byte[])this._set, (int)0, (int)this._set.length, (byte)this.no_entry_key);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
        Arrays.fill((Object[])this._values, (int)0, (int)this._values.length, null);
    }

    @Override
    public TByteSet keySet() {
        return new KeyView((TByteObjectHashMap)this);
    }

    @Override
    public byte[] keys() {
        byte[] keys = new byte[this.size()];
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
    public byte[] keys(byte[] dest) {
        if (dest.length < this._size) {
            dest = new byte[this._size];
        }
        byte[] k = this._set;
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
        return new ValueView((TByteObjectHashMap)this);
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
    public TByteObjectIterator<V> iterator() {
        return new TByteObjectHashIterator<V>((TByteObjectHashMap)this, this);
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.forEach((TByteProcedure)procedure);
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
    public boolean forEachEntry(TByteObjectProcedure<? super V> procedure) {
        byte[] states = this._states;
        byte[] keys = this._set;
        V[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((byte)keys[i], values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TByteObjectProcedure<? super V> procedure) {
        boolean modified = false;
        byte[] states = this._states;
        byte[] keys = this._set;
        V[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((byte)keys[i], values[i])) continue;
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
        if (!(other instanceof TByteObjectMap)) {
            return false;
        }
        that = (TByteObjectMap)other;
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
                if (that.get((byte)key) != null) return false;
                if (that.containsKey((byte)key)) ** GOTO lbl-1000
                return false;
            } while (value.equals(that.get((byte)key)));
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
        out.writeByte((int)this.no_entry_key);
        out.writeInt((int)this._size);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeByte((int)this._set[i]);
            out.writeObject(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.no_entry_key = in.readByte();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            byte key = in.readByte();
            Object val = in.readObject();
            this.put((byte)key, val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TByteObjectProcedure<V>((TByteObjectHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TByteObjectHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(byte key, Object value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)",");
                }
                this.val$buf.append((int)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((Object)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    static /* synthetic */ int access$000(TByteObjectHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$100(TByteObjectHashMap x0) {
        return x0._size;
    }
}

