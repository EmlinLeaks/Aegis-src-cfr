/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleObjectMap;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableDoubleObjectMap<V>
implements TDoubleObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleObjectMap<V> m;
    private transient TDoubleSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableDoubleObjectMap(TDoubleObjectMap<V> m) {
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
    public boolean containsKey(double key) {
        return this.m.containsKey((double)key);
    }

    @Override
    public boolean containsValue(Object val) {
        return this.m.containsValue((Object)val);
    }

    @Override
    public V get(double key) {
        return (V)this.m.get((double)key);
    }

    @Override
    public V put(double key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleObjectMap<? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TDoubleSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TDoubleSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public double[] keys() {
        return this.m.keys();
    }

    @Override
    public double[] keys(double[] array) {
        return this.m.keys((double[])array);
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.m.forEachKey((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TDoubleObjectProcedure<? super V> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TDoubleObjectIterator<V> iterator() {
        return new TDoubleObjectIterator<V>((TUnmodifiableDoubleObjectMap)this){
            TDoubleObjectIterator<V> iter;
            final /* synthetic */ TUnmodifiableDoubleObjectMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableDoubleObjectMap.access$000((TUnmodifiableDoubleObjectMap)this.this$0).iterator();
            }

            public double key() {
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
    public V putIfAbsent(double key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleObjectProcedure<? super V> procedure) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TDoubleObjectMap access$000(TUnmodifiableDoubleObjectMap x0) {
        return x0.m;
    }
}

