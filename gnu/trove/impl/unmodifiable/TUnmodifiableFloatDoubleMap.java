/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatDoubleMap;
import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatDoubleMap
implements TFloatDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatDoubleMap m;
    private transient TFloatSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableFloatDoubleMap(TFloatDoubleMap m) {
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
    public boolean containsKey(float key) {
        return this.m.containsKey((float)key);
    }

    @Override
    public boolean containsValue(double val) {
        return this.m.containsValue((double)val);
    }

    @Override
    public double get(float key) {
        return this.m.get((float)key);
    }

    @Override
    public double put(float key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TFloatSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TFloatSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public float[] keys() {
        return this.m.keys();
    }

    @Override
    public float[] keys(float[] array) {
        return this.m.keys((float[])array);
    }

    @Override
    public TDoubleCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TDoubleCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public double[] values() {
        return this.m.values();
    }

    @Override
    public double[] values(double[] array) {
        return this.m.values((double[])array);
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
        return this.m.forEachKey((TFloatProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TFloatDoubleProcedure procedure) {
        return this.m.forEachEntry((TFloatDoubleProcedure)procedure);
    }

    @Override
    public TFloatDoubleIterator iterator() {
        return new TFloatDoubleIterator((TUnmodifiableFloatDoubleMap)this){
            TFloatDoubleIterator iter;
            final /* synthetic */ TUnmodifiableFloatDoubleMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableFloatDoubleMap.access$000((TUnmodifiableFloatDoubleMap)this.this$0).iterator();
            }

            public float key() {
                return this.iter.key();
            }

            public double value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public double setValue(double val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public double putIfAbsent(float key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(float key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TFloatDoubleMap access$000(TUnmodifiableFloatDoubleMap x0) {
        return x0.m;
    }
}

