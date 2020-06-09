/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
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

public class TLongObjectHashMap<V>
extends TLongHash
implements TLongObjectMap<V>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TLongObjectProcedure<V> PUT_ALL_PROC = new TLongObjectProcedure<V>((TLongObjectHashMap)this){
        final /* synthetic */ TLongObjectHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(long key, V value) {
            this.this$0.put((long)key, value);
            return true;
        }
    };
    protected transient V[] _values;
    protected long no_entry_key;

    public TLongObjectHashMap() {
    }

    public TLongObjectHashMap(int initialCapacity) {
        super((int)initialCapacity);
        this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TLongObjectHashMap(int initialCapacity, float loadFactor) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TLongObjectHashMap(int initialCapacity, float loadFactor, long noEntryKey) {
        super((int)initialCapacity, (float)loadFactor);
        this.no_entry_key = noEntryKey;
    }

    public TLongObjectHashMap(TLongObjectMap<? extends V> map) {
        this((int)map.size(), (float)0.5f, (long)map.getNoEntryKey());
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
        long[] oldKeys = this._set;
        V[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._values = new Object[newCapacity];
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
    public long getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(long key) {
        return this.contains((long)key);
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
    public V get(long key) {
        V v;
        int index = this.index((long)key);
        if (index < 0) {
            v = null;
            return (V)((V)v);
        }
        v = (V)this._values[index];
        return (V)v;
    }

    @Override
    public V put(long key, V value) {
        int index = this.insertKey((long)key);
        return (V)this.doPut(value, (int)index);
    }

    @Override
    public V putIfAbsent(long key, V value) {
        int index = this.insertKey((long)key);
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
    public V remove(long key) {
        V prev = null;
        int index = this.index((long)key);
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
    public void putAll(Map<? extends Long, ? extends V> map) {
        Set<Map.Entry<Long, V>> set = map.entrySet();
        Iterator<Map.Entry<Long, V>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, V> entry = iterator.next();
            this.put((long)entry.getKey().longValue(), entry.getValue());
        }
    }

    @Override
    public void putAll(TLongObjectMap<? extends V> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((long[])this._set, (int)0, (int)this._set.length, (long)this.no_entry_key);
        Arrays.fill((byte[])this._states, (int)0, (int)this._states.length, (byte)0);
        Arrays.fill((Object[])this._values, (int)0, (int)this._values.length, null);
    }

    @Override
    public TLongSet keySet() {
        return new KeyView((TLongObjectHashMap)this);
    }

    @Override
    public long[] keys() {
        long[] keys = new long[this.size()];
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
    public long[] keys(long[] dest) {
        if (dest.length < this._size) {
            dest = new long[this._size];
        }
        long[] k = this._set;
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
        return new ValueView((TLongObjectHashMap)this);
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
    public TLongObjectIterator<V> iterator() {
        return new TLongObjectHashIterator<V>((TLongObjectHashMap)this, this);
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.forEach((TLongProcedure)procedure);
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
    public boolean forEachEntry(TLongObjectProcedure<? super V> procedure) {
        byte[] states = this._states;
        long[] keys = this._set;
        V[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (states[i] != 1 || procedure.execute((long)keys[i], values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TLongObjectProcedure<? super V> procedure) {
        boolean modified = false;
        byte[] states = this._states;
        long[] keys = this._set;
        V[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute((long)keys[i], values[i])) continue;
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
        if (!(other instanceof TLongObjectMap)) {
            return false;
        }
        that = (TLongObjectMap)other;
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
                if (that.get((long)key) != null) return false;
                if (that.containsKey((long)key)) ** GOTO lbl-1000
                return false;
            } while (value.equals(that.get((long)key)));
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
            hashcode += HashFunctions.hash((long)this._set[i]) ^ (values[i] == null ? 0 : values[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeLong((long)this.no_entry_key);
        out.writeInt((int)this._size);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeLong((long)this._set[i]);
            out.writeObject(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.no_entry_key = in.readLong();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            long key = in.readLong();
            Object val = in.readObject();
            this.put((long)key, val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TLongObjectProcedure<V>((TLongObjectHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TLongObjectHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(long key, Object value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)",");
                }
                this.val$buf.append((long)key);
                this.val$buf.append((String)"=");
                this.val$buf.append((Object)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    static /* synthetic */ int access$000(TLongObjectHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$100(TLongObjectHashMap x0) {
        return x0._size;
    }
}

