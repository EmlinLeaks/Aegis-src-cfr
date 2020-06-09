/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectIntMap<K>
implements TObjectIntMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectIntMap<K> m;
    private transient Set<K> keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableObjectIntMap(TObjectIntMap<K> m) {
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
    public boolean containsValue(int val) {
        return this.m.containsValue((int)val);
    }

    @Override
    public int get(Object key) {
        return this.m.get((Object)key);
    }

    @Override
    public int put(K key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectIntMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Integer> map) {
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
    public TIntCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TIntCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public int[] values() {
        return this.m.values();
    }

    @Override
    public int[] values(int[] array) {
        return this.m.values((int[])array);
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        return this.m.forEachValue((TIntProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TObjectIntProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectIntIterator<K> iterator() {
        return new TObjectIntIterator<K>((TUnmodifiableObjectIntMap)this){
            TObjectIntIterator<K> iter;
            final /* synthetic */ TUnmodifiableObjectIntMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableObjectIntMap.access$000((TUnmodifiableObjectIntMap)this.this$0).iterator();
            }

            public K key() {
                return (K)this.iter.key();
            }

            public int value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public int setValue(int val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int putIfAbsent(K key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectIntProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(K key, int adjust_amount, int put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TObjectIntMap access$000(TUnmodifiableObjectIntMap x0) {
        return x0.m;
    }
}

