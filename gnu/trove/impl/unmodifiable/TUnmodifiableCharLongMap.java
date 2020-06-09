/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharLongMap;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.map.TCharLongMap;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharLongMap
implements TCharLongMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharLongMap m;
    private transient TCharSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableCharLongMap(TCharLongMap m) {
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
    public boolean containsKey(char key) {
        return this.m.containsKey((char)key);
    }

    @Override
    public boolean containsValue(long val) {
        return this.m.containsValue((long)val);
    }

    @Override
    public long get(char key) {
        return this.m.get((char)key);
    }

    @Override
    public long put(char key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharLongMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Long> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TCharSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TCharSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public char[] keys() {
        return this.m.keys();
    }

    @Override
    public char[] keys(char[] array) {
        return this.m.keys((char[])array);
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.m.forEachKey((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure procedure) {
        return this.m.forEachValue((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TCharLongProcedure procedure) {
        return this.m.forEachEntry((TCharLongProcedure)procedure);
    }

    @Override
    public TCharLongIterator iterator() {
        return new TCharLongIterator((TUnmodifiableCharLongMap)this){
            TCharLongIterator iter;
            final /* synthetic */ TUnmodifiableCharLongMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableCharLongMap.access$000((TUnmodifiableCharLongMap)this.this$0).iterator();
            }

            public char key() {
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
    public long putIfAbsent(char key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharLongProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char key, long amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(char key, long adjust_amount, long put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TCharLongMap access$000(TUnmodifiableCharLongMap x0) {
        return x0.m;
    }
}

