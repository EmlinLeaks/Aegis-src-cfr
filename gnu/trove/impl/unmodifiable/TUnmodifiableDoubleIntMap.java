/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleIntMap;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleIntMap
implements TDoubleIntMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleIntMap m;
    private transient TDoubleSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableDoubleIntMap(TDoubleIntMap m) {
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
    public boolean containsValue(int val) {
        return this.m.containsValue((int)val);
    }

    @Override
    public int get(double key) {
        return this.m.get((double)key);
    }

    @Override
    public int put(double key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleIntMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Integer> map) {
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.m.forEachKey((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        return this.m.forEachValue((TIntProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TDoubleIntProcedure procedure) {
        return this.m.forEachEntry((TDoubleIntProcedure)procedure);
    }

    @Override
    public TDoubleIntIterator iterator() {
        return new TDoubleIntIterator((TUnmodifiableDoubleIntMap)this){
            TDoubleIntIterator iter;
            final /* synthetic */ TUnmodifiableDoubleIntMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableDoubleIntMap.access$000((TUnmodifiableDoubleIntMap)this.this$0).iterator();
            }

            public double key() {
                return this.iter.key();
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
    public int putIfAbsent(double key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleIntProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double key, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(double key, int adjust_amount, int put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TDoubleIntMap access$000(TUnmodifiableDoubleIntMap x0) {
        return x0.m;
    }
}

