/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongIntMap;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongIntMap
implements TLongIntMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongIntMap m;
    private transient TLongSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableLongIntMap(TLongIntMap m) {
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
    public boolean containsValue(int val) {
        return this.m.containsValue((int)val);
    }

    @Override
    public int get(long key) {
        return this.m.get((long)key);
    }

    @Override
    public int put(long key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongIntMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Integer> map) {
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
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.m.forEachKey((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        return this.m.forEachValue((TIntProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TLongIntProcedure procedure) {
        return this.m.forEachEntry((TLongIntProcedure)procedure);
    }

    @Override
    public TLongIntIterator iterator() {
        return new TLongIntIterator((TUnmodifiableLongIntMap)this){
            TLongIntIterator iter;
            final /* synthetic */ TUnmodifiableLongIntMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableLongIntMap.access$000((TUnmodifiableLongIntMap)this.this$0).iterator();
            }

            public long key() {
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
    public int putIfAbsent(long key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongIntProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long key, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(long key, int adjust_amount, int put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TLongIntMap access$000(TUnmodifiableLongIntMap x0) {
        return x0.m;
    }
}

