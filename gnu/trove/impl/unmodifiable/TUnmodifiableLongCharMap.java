/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCharMap;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongCharMap
implements TLongCharMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongCharMap m;
    private transient TLongSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableLongCharMap(TLongCharMap m) {
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
    public boolean containsValue(char val) {
        return this.m.containsValue((char)val);
    }

    @Override
    public char get(long key) {
        return this.m.get((long)key);
    }

    @Override
    public char put(long key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongCharMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Character> map) {
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
    public TCharCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TCharCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public char[] values() {
        return this.m.values();
    }

    @Override
    public char[] values(char[] array) {
        return this.m.values((char[])array);
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
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.m.forEachKey((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        return this.m.forEachValue((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TLongCharProcedure procedure) {
        return this.m.forEachEntry((TLongCharProcedure)procedure);
    }

    @Override
    public TLongCharIterator iterator() {
        return new TLongCharIterator((TUnmodifiableLongCharMap)this){
            TLongCharIterator iter;
            final /* synthetic */ TUnmodifiableLongCharMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableLongCharMap.access$000((TUnmodifiableLongCharMap)this.this$0).iterator();
            }

            public long key() {
                return this.iter.key();
            }

            public char value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public char setValue(char val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public char putIfAbsent(long key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongCharProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long key, char amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(long key, char adjust_amount, char put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TLongCharMap access$000(TUnmodifiableLongCharMap x0) {
        return x0.m;
    }
}

