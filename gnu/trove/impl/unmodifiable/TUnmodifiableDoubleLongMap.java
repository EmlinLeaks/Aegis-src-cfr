/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleLongMap;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleLongMap
implements TDoubleLongMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleLongMap m;
    private transient TDoubleSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableDoubleLongMap(TDoubleLongMap m) {
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
    public boolean containsValue(long val) {
        return this.m.containsValue((long)val);
    }

    @Override
    public long get(double key) {
        return this.m.get((double)key);
    }

    @Override
    public long put(double key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleLongMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Long> map) {
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
    public TLongCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TLongCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public long[] values() {
        return this.m.values();
    }

    @Override
    public long[] values(long[] array) {
        return this.m.values((long[])array);
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
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.m.forEachKey((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure procedure) {
        return this.m.forEachValue((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TDoubleLongProcedure procedure) {
        return this.m.forEachEntry((TDoubleLongProcedure)procedure);
    }

    @Override
    public TDoubleLongIterator iterator() {
        return new TDoubleLongIterator((TUnmodifiableDoubleLongMap)this){
            TDoubleLongIterator iter;
            final /* synthetic */ TUnmodifiableDoubleLongMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableDoubleLongMap.access$000((TUnmodifiableDoubleLongMap)this.this$0).iterator();
            }

            public double key() {
                return this.iter.key();
            }

            public long value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public long setValue(long val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public long putIfAbsent(double key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleLongProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double key, long amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(double key, long adjust_amount, long put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TDoubleLongMap access$000(TUnmodifiableDoubleLongMap x0) {
        return x0.m;
    }
}

