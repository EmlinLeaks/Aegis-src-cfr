/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleDoubleMap;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleDoubleMap
implements TDoubleDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleDoubleMap m;
    private transient TDoubleSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableDoubleDoubleMap(TDoubleDoubleMap m) {
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
    public boolean containsValue(double val) {
        return this.m.containsValue((double)val);
    }

    @Override
    public double get(double key) {
        return this.m.get((double)key);
    }

    @Override
    public double put(double key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> map) {
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.m.forEachKey((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TDoubleDoubleProcedure procedure) {
        return this.m.forEachEntry((TDoubleDoubleProcedure)procedure);
    }

    @Override
    public TDoubleDoubleIterator iterator() {
        return new TDoubleDoubleIterator((TUnmodifiableDoubleDoubleMap)this){
            TDoubleDoubleIterator iter;
            final /* synthetic */ TUnmodifiableDoubleDoubleMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableDoubleDoubleMap.access$000((TUnmodifiableDoubleDoubleMap)this.this$0).iterator();
            }

            public double key() {
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
    public double putIfAbsent(double key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(double key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TDoubleDoubleMap access$000(TUnmodifiableDoubleDoubleMap x0) {
        return x0.m;
    }
}

