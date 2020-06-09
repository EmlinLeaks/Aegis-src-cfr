/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectCharMap;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectCharMap<K>
implements TObjectCharMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectCharMap<K> m;
    private transient Set<K> keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableObjectCharMap(TObjectCharMap<K> m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
    }

    @Override
    public int size() {
        return this.m.size();
    }

    @Override
    public boolean isEmpty() {
        return this.m.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.m.containsKey((Object)key);
    }

    @Override
    public boolean containsValue(char val) {
        return this.m.containsValue((char)val);
    }

    @Override
    public char get(Object key) {
        return this.m.get((Object)key);
    }

    @Override
    public char put(K key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectCharMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Character> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = Collections.unmodifiableSet(this.m.keySet());
        return this.keySet;
    }

    @Override
    public Object[] keys() {
        return this.m.keys();
    }

    @Override
    public K[] keys(K[] array) {
        return this.m.keys(array);
    }

    @Override
    public TCharCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TCharCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public char[] values() {
        return this.m.values();
    }

    @Override
    public char[] values(char[] array) {
        return this.m.values((char[])array);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (this.m.equals((Object)o)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.m.hashCode();
    }

    public String toString() {
        return this.m.toString();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        return this.m.forEachValue((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TObjectCharProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectCharIterator<K> iterator() {
        return new TObjectCharIterator<K>((TUnmodifiableObjectCharMap)this){
            TObjectCharIterator<K> iter;
            final /* synthetic */ TUnmodifiableObjectCharMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableObjectCharMap.access$000((TUnmodifiableObjectCharMap)this.this$0).iterator();
            }

            public K key() {
                return (K)this.iter.key();
            }

            public char value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public char setValue(char val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public char putIfAbsent(K key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectCharProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, char amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(K key, char adjust_amount, char put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TObjectCharMap access$000(TUnmodifiableObjectCharMap x0) {
        return x0.m;
    }
}

