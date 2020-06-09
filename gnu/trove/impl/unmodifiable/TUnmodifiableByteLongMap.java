/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteLongMap;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteLongMap
implements TByteLongMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteLongMap m;
    private transient TByteSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableByteLongMap(TByteLongMap m) {
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
    public boolean containsKey(byte key) {
        return this.m.containsKey((byte)key);
    }

    @Override
    public boolean containsValue(long val) {
        return this.m.containsValue((long)val);
    }

    @Override
    public long get(byte key) {
        return this.m.get((byte)key);
    }

    @Override
    public long put(byte key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteLongMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Long> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TByteSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TByteSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public byte[] keys() {
        return this.m.keys();
    }

    @Override
    public byte[] keys(byte[] array) {
        return this.m.keys((byte[])array);
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
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.m.forEachKey((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure procedure) {
        return this.m.forEachValue((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TByteLongProcedure procedure) {
        return this.m.forEachEntry((TByteLongProcedure)procedure);
    }

    @Override
    public TByteLongIterator iterator() {
        return new TByteLongIterator((TUnmodifiableByteLongMap)this){
            TByteLongIterator iter;
            final /* synthetic */ TUnmodifiableByteLongMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableByteLongMap.access$000((TUnmodifiableByteLongMap)this.this$0).iterator();
            }

            public byte key() {
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
    public long putIfAbsent(byte key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteLongProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte key, long amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(byte key, long adjust_amount, long put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TByteLongMap access$000(TUnmodifiableByteLongMap x0) {
        return x0.m;
    }
}

