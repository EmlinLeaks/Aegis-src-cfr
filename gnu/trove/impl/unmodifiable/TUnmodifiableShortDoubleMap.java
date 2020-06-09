/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortDoubleMap;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortDoubleMap
implements TShortDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortDoubleMap m;
    private transient TShortSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableShortDoubleMap(TShortDoubleMap m) {
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
    public boolean containsKey(short key) {
        return this.m.containsKey((short)key);
    }

    @Override
    public boolean containsValue(double val) {
        return this.m.containsValue((double)val);
    }

    @Override
    public double get(short key) {
        return this.m.get((short)key);
    }

    @Override
    public double put(short key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TShortSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TShortSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public short[] keys() {
        return this.m.keys();
    }

    @Override
    public short[] keys(short[] array) {
        return this.m.keys((short[])array);
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.m.forEachKey((TShortProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TShortDoubleProcedure procedure) {
        return this.m.forEachEntry((TShortDoubleProcedure)procedure);
    }

    @Override
    public TShortDoubleIterator iterator() {
        return new TShortDoubleIterator((TUnmodifiableShortDoubleMap)this){
            TShortDoubleIterator iter;
            final /* synthetic */ TUnmodifiableShortDoubleMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableShortDoubleMap.access$000((TUnmodifiableShortDoubleMap)this.this$0).iterator();
            }

            public short key() {
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
    public double putIfAbsent(short key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(short key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TShortDoubleMap access$000(TUnmodifiableShortDoubleMap x0) {
        return x0.m;
    }
}

