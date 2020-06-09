/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntShortMap;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntShortMap
implements TIntShortMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntShortMap m;
    private transient TIntSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableIntShortMap(TIntShortMap m) {
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
    public boolean containsKey(int key) {
        return this.m.containsKey((int)key);
    }

    @Override
    public boolean containsValue(short val) {
        return this.m.containsValue((short)val);
    }

    @Override
    public short get(int key) {
        return this.m.get((int)key);
    }

    @Override
    public short put(int key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntShortMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Short> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TIntSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TIntSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public int[] keys() {
        return this.m.keys();
    }

    @Override
    public int[] keys(int[] array) {
        return this.m.keys((int[])array);
    }

    @Override
    public TShortCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TShortCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public short[] values() {
        return this.m.values();
    }

    @Override
    public short[] values(short[] array) {
        return this.m.values((short[])array);
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
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure procedure) {
        return this.m.forEachKey((TIntProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        return this.m.forEachValue((TShortProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TIntShortProcedure procedure) {
        return this.m.forEachEntry((TIntShortProcedure)procedure);
    }

    @Override
    public TIntShortIterator iterator() {
        return new TIntShortIterator((TUnmodifiableIntShortMap)this){
            TIntShortIterator iter;
            final /* synthetic */ TUnmodifiableIntShortMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableIntShortMap.access$000((TUnmodifiableIntShortMap)this.this$0).iterator();
            }

            public int key() {
                return this.iter.key();
            }

            public short value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public short setValue(short val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public short putIfAbsent(int key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntShortProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int key, short amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(int key, short adjust_amount, short put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TIntShortMap access$000(TUnmodifiableIntShortMap x0) {
        return x0.m;
    }
}

