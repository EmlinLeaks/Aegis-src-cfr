/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongFloatMap;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongFloatMap
implements TLongFloatMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongFloatMap m;
    private transient TLongSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableLongFloatMap(TLongFloatMap m) {
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
    public boolean containsValue(float val) {
        return this.m.containsValue((float)val);
    }

    @Override
    public float get(long key) {
        return this.m.get((long)key);
    }

    @Override
    public float put(long key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongFloatMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Float> map) {
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

    public boolean equals(Object o) {
        if (o == this) return true;
        if (this.m.equals((Object)o)) return true;
        return false;
    }

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
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.m.forEachKey((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        return this.m.forEachValue((TFloatProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TLongFloatProcedure procedure) {
        return this.m.forEachEntry((TLongFloatProcedure)procedure);
    }

    @Override
    public TLongFloatIterator iterator() {
        return new TLongFloatIterator((TUnmodifiableLongFloatMap)this){
            TLongFloatIterator iter;
            final /* synthetic */ TUnmodifiableLongFloatMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableLongFloatMap.access$000((TUnmodifiableLongFloatMap)this.this$0).iterator();
            }

            public long key() {
                return this.iter.key();
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
    public float putIfAbsent(long key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongFloatProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long key, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(long key, float adjust_amount, float put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TLongFloatMap access$000(TUnmodifiableLongFloatMap x0) {
        return x0.m;
    }
}

