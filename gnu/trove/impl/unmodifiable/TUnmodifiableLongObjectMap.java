/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableLongObjectMap<V>
implements TLongObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongObjectMap<V> m;
    private transient TLongSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableLongObjectMap(TLongObjectMap<V> m) {
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
    public boolean containsKey(long key) {
        return this.m.containsKey((long)key);
    }

    @Override
    public boolean containsValue(Object val) {
        return this.m.containsValue((Object)val);
    }

    @Override
    public V get(long key) {
        return (V)this.m.get((long)key);
    }

    @Override
    public V put(long key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongObjectMap<? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TLongSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TLongSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public long[] keys() {
        return this.m.keys();
    }

    @Override
    public long[] keys(long[] array) {
        return this.m.keys((long[])array);
    }

    @Override
    public Collection<V> valueCollection() {
        if (this.values != null) return this.values;
        this.values = Collections.unmodifiableCollection(this.m.valueCollection());
        return this.values;
    }

    @Override
    public Object[] values() {
        return this.m.values();
    }

    @Override
    public V[] values(V[] array) {
        return this.m.values(array);
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
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.m.forEachKey((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TLongObjectProcedure<? super V> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TLongObjectIterator<V> iterator() {
        return new TLongObjectIterator<V>((TUnmodifiableLongObjectMap)this){
            TLongObjectIterator<V> iter;
            final /* synthetic */ TUnmodifiableLongObjectMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableLongObjectMap.access$000((TUnmodifiableLongObjectMap)this.this$0).iterator();
            }

            public long key() {
                return this.iter.key();
            }

            public V value() {
                return (V)this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public V setValue(V val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public V putIfAbsent(long key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongObjectProcedure<? super V> procedure) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TLongObjectMap access$000(TUnmodifiableLongObjectMap x0) {
        return x0.m;
    }
}

