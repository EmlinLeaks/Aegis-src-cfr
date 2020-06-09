/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleFloatMap;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleFloatMap
implements TDoubleFloatMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleFloatMap m;
    private transient TDoubleSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableDoubleFloatMap(TDoubleFloatMap m) {
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
    public boolean containsValue(float val) {
        return this.m.containsValue((float)val);
    }

    @Override
    public float get(double key) {
        return this.m.get((double)key);
    }

    @Override
    public float put(double key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleFloatMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Float> map) {
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.m.forEachKey((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        return this.m.forEachValue((TFloatProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TDoubleFloatProcedure procedure) {
        return this.m.forEachEntry((TDoubleFloatProcedure)procedure);
    }

    @Override
    public TDoubleFloatIterator iterator() {
        return new TDoubleFloatIterator((TUnmodifiableDoubleFloatMap)this){
            TDoubleFloatIterator iter;
            final /* synthetic */ TUnmodifiableDoubleFloatMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableDoubleFloatMap.access$000((TUnmodifiableDoubleFloatMap)this.this$0).iterator();
            }

            public double key() {
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
    public float putIfAbsent(double key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleFloatProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double key, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(double key, float adjust_amount, float put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TDoubleFloatMap access$000(TUnmodifiableDoubleFloatMap x0) {
        return x0.m;
    }
}

