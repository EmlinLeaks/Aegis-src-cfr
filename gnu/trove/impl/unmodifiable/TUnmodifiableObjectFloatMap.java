/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectFloatMap;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectFloatMap<K>
implements TObjectFloatMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectFloatMap<K> m;
    private transient Set<K> keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableObjectFloatMap(TObjectFloatMap<K> m) {
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
    public boolean containsValue(float val) {
        return this.m.containsValue((float)val);
    }

    @Override
    public float get(Object key) {
        return this.m.get((Object)key);
    }

    @Override
    public float put(K key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectFloatMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
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
    public TFloatCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TFloatCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public float[] values() {
        return this.m.values();
    }

    @Override
    public float[] values(float[] array) {
        return this.m.values((float[])array);
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
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        return this.m.forEachValue((TFloatProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TObjectFloatProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectFloatIterator<K> iterator() {
        return new TObjectFloatIterator<K>((TUnmodifiableObjectFloatMap)this){
            TObjectFloatIterator<K> iter;
            final /* synthetic */ TUnmodifiableObjectFloatMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableObjectFloatMap.access$000((TUnmodifiableObjectFloatMap)this.this$0).iterator();
            }

            public K key() {
                return (K)this.iter.key();
            }

            public float value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public float setValue(float val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public float putIfAbsent(K key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectFloatProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(K key, float adjust_amount, float put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TObjectFloatMap access$000(TUnmodifiableObjectFloatMap x0) {
        return x0.m;
    }
}

