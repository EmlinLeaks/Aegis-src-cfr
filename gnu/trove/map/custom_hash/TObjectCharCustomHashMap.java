/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.map.custom_hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.custom_hash.TObjectCharCustomHashMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TObjectCharCustomHashMap<K>
extends TCustomObjectHash<K>
implements TObjectCharMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectCharProcedure<K> PUT_ALL_PROC = new TObjectCharProcedure<K>((TObjectCharCustomHashMap)this){
        final /* synthetic */ TObjectCharCustomHashMap this$0;
        {
            this.this$0 = this$0;
        }

        public boolean execute(K key, char value) {
            this.this$0.put(key, (char)value);
            return true;
        }
    };
    protected transient char[] _values;
    protected char no_entry_value;

    public TObjectCharCustomHashMap() {
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy) {
        super(strategy);
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
        super(strategy, (int)initialCapacity);
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
        super(strategy, (int)initialCapacity, (float)loadFactor);
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor, char noEntryValue) {
        super(strategy, (int)initialCapacity, (float)loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value == '\u0000') return;
        Arrays.fill((char[])this._values, (char)this.no_entry_value);
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, TObjectCharMap<? extends K> map) {
        this(strategy, (int)map.size(), (float)0.5f, (char)map.getNoEntryValue());
        if (map instanceof TObjectCharCustomHashMap) {
            TObjectCharCustomHashMap hashmap = (TObjectCharCustomHashMap)map;
            this._loadFactor = Math.abs((float)hashmap._loadFactor);
            this.no_entry_value = hashmap.no_entry_value;
            this.strategy = hashmap.strategy;
            if (this.no_entry_value != '\u0000') {
                Arrays.fill((char[])this._values, (char)this.no_entry_value);
            }
            this.setUp((int)TObjectCharCustomHashMap.saturatedCast((long)TObjectCharCustomHashMap.fastCeil((double)(10.0 / (double)this._loadFactor))));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp((int)initialCapacity);
        this._values = new char[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        char[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill((Object[])this._set, (Object)FREE);
        this._values = new char[newCapacity];
        Arrays.fill((char[])this._values, (char)this.no_entry_value);
        int i = oldCapacity;
        while (i-- > 0) {
            Object o = oldKeys[i];
            if (o == FREE || o == REMOVED) continue;
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation((Object)this._set[-index - 1], (Object)o);
            }
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public char getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains((Object)key);
    }

    @Override
    public boolean containsValue(char val) {
        Object[] keys = this._set;
        char[] vals = this._values;
        int i = vals.length;
        do {
            if (i-- <= 0) return false;
        } while (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]);
        return true;
    }

    @Override
    public char get(Object key) {
        char c;
        int index = this.index((Object)key);
        if (index < 0) {
            c = this.no_entry_value;
            return c;
        }
        c = this._values[index];
        return c;
    }

    @Override
    public char put(K key, char value) {
        int index = this.insertKey(key);
        return this.doPut((char)value, (int)index);
    }

    @Override
    public char putIfAbsent(K key, char value) {
        int index = this.insertKey(key);
        if (index >= 0) return this.doPut((char)value, (int)index);
        return this._values[-index - 1];
    }

    private char doPut(char value, int index) {
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
    public char remove(Object key) {
        char prev = this.no_entry_value;
        int index = this.index((Object)key);
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
    public void putAll(Map<? extends K, ? extends Character> map) {
        Set<Map.Entry<K, Character>> set = map.entrySet();
        Iterator<Map.Entry<K, Character>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Character> entry = iterator.next();
            this.put(entry.getKey(), (char)entry.getValue().charValue());
        }
    }

    @Override
    public void putAll(TObjectCharMap<? extends K> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill((Object[])this._set, (int)0, (int)this._set.length, (Object)FREE);
        Arrays.fill((char[])this._values, (int)0, (int)this._values.length, (char)this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new KeyView((TObjectCharCustomHashMap)this);
    }

    @Override
    public Object[] keys() {
        Object[] keys = new Object[this.size()];
        Object[] k = this._set;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (k[i] == FREE || k[i] == REMOVED) continue;
            keys[j++] = k[i];
        }
        return keys;
    }

    @Override
    public K[] keys(K[] a) {
        int size = this.size();
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), (int)size);
        }
        Object[] k = this._set;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (k[i] == FREE || k[i] == REMOVED) continue;
            a[j++] = k[i];
        }
        return a;
    }

    @Override
    public TCharCollection valueCollection() {
        return new TCharValueCollection((TObjectCharCustomHashMap)this);
    }

    @Override
    public char[] values() {
        char[] vals = new char[this.size()];
        char[] v = this._values;
        Object[] keys = this._set;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            vals[j++] = v[i];
        }
        return vals;
    }

    @Override
    public char[] values(char[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new char[size];
        }
        char[] v = this._values;
        Object[] keys = this._set;
        int i = v.length;
        int j = 0;
        do {
            if (i-- <= 0) {
                if (array.length <= size) return array;
                array[size] = this.no_entry_value;
                return array;
            }
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            array[j++] = v[i];
        } while (true);
    }

    @Override
    public TObjectCharIterator<K> iterator() {
        return new TObjectCharHashIterator<K>((TObjectCharCustomHashMap)this, this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, (char)'\u0001');
    }

    @Override
    public boolean adjustValue(K key, char amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        char[] arrc = this._values;
        int n = index;
        arrc[n] = (char)(arrc[n] + amount);
        return true;
    }

    @Override
    public char adjustOrPutValue(K key, char adjust_amount, char put_amount) {
        boolean isNewMapping;
        char newValue;
        int index = this.insertKey(key);
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
        if (!isNewMapping) return newValue;
        this.postInsertHook((boolean)this.consumeFreeSlot);
        return newValue;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        Object[] keys = this._set;
        char[] values = this._values;
        int i = values.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute((char)values[i]));
        return false;
    }

    @Override
    public boolean forEachEntry(TObjectCharProcedure<? super K> procedure) {
        Object[] keys = this._set;
        char[] values = this._values;
        int i = keys.length;
        do {
            if (i-- <= 0) return true;
        } while (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (char)values[i]));
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectCharProcedure<? super K> procedure) {
        boolean modified = false;
        Object[] keys = this._set;
        char[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], (char)values[i])) continue;
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
    public void transformValues(TCharFunction function) {
        Object[] keys = this._set;
        char[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute((char)values[i]);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectCharMap)) {
            return false;
        }
        that = (TObjectCharMap)other;
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
                if (value != this.no_entry_value) continue;
                if (that.get(key) != that.getNoEntryValue()) return false;
                if (that.containsKey(key)) ** GOTO lbl-1000
                return false;
            } while (value == that.get(key));
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
        Object[] keys = this._set;
        char[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            hashcode += HashFunctions.hash((int)values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte((int)0);
        super.writeExternal((ObjectOutput)out);
        out.writeObject((Object)this.strategy);
        out.writeChar((int)this.no_entry_value);
        out.writeInt((int)this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject((Object)this._set[i]);
            out.writeChar((int)this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal((ObjectInput)in);
        this.strategy = (HashingStrategy)in.readObject();
        this.no_entry_value = in.readChar();
        int size = in.readInt();
        this.setUp((int)size);
        while (size-- > 0) {
            Object key = in.readObject();
            char val = in.readChar();
            this.put(key, (char)val);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder((String)"{");
        this.forEachEntry(new TObjectCharProcedure<K>((TObjectCharCustomHashMap)this, (StringBuilder)buf){
            private boolean first;
            final /* synthetic */ StringBuilder val$buf;
            final /* synthetic */ TObjectCharCustomHashMap this$0;
            {
                this.this$0 = this$0;
                this.val$buf = stringBuilder;
                this.first = true;
            }

            public boolean execute(K key, char value) {
                if (this.first) {
                    this.first = false;
                } else {
                    this.val$buf.append((String)",");
                }
                this.val$buf.append(key).append((String)"=").append((char)value);
                return true;
            }
        });
        buf.append((String)"}");
        return buf.toString();
    }

    static /* synthetic */ int access$100(TObjectCharCustomHashMap x0) {
        return x0._size;
    }

    static /* synthetic */ int access$200(TObjectCharCustomHashMap x0) {
        return x0._size;
    }
}

