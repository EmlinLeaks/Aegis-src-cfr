/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharObjectMap;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableCharObjectMap<V>
implements TCharObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharObjectMap<V> m;
    private transient TCharSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableCharObjectMap(TCharObjectMap<V> m) {
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
    public boolean containsKey(char key) {
        return this.m.containsKey((char)key);
    }

    @Override
    public boolean containsValue(Object val) {
        return this.m.containsValue((Object)val);
    }

    @Override
    public V get(char key) {
        return (V)this.m.get((char)key);
    }

    @Override
    public V put(char key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharObjectMap<? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TCharSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TCharSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public char[] keys() {
        return this.m.keys();
    }

    @Override
    public char[] keys(char[] array) {
        return this.m.keys((char[])array);
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.m.forEachKey((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TCharObjectProcedure<? super V> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TCharObjectIterator<V> iterator() {
        return new TCharObjectIterator<V>((TUnmodifiableCharObjectMap)this){
            TCharObjectIterator<V> iter;
            final /* synthetic */ TUnmodifiableCharObjectMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableCharObjectMap.access$000((TUnmodifiableCharObjectMap)this.this$0).iterator();
            }

            public char key() {
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
    public V putIfAbsent(char key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharObjectProcedure<? super V> procedure) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TCharObjectMap access$000(TUnmodifiableCharObjectMap x0) {
        return x0.m;
    }
}

