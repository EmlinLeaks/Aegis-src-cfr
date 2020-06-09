/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongByteMap;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.map.TLongByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongByteMap
implements TLongByteMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongByteMap m;
    private transient TLongSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableLongByteMap(TLongByteMap m) {
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
    public boolean containsValue(byte val) {
        return this.m.containsValue((byte)val);
    }

    @Override
    public byte get(long key) {
        return this.m.get((long)key);
    }

    @Override
    public byte put(long key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongByteMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Byte> map) {
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
    public TByteCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TByteCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public byte[] values() {
        return this.m.values();
    }

    @Override
    public byte[] values(byte[] array) {
        return this.m.values((byte[])array);
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
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.m.forEachKey((TLongProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure procedure) {
        return this.m.forEachValue((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TLongByteProcedure procedure) {
        return this.m.forEachEntry((TLongByteProcedure)procedure);
    }

    @Override
    public TLongByteIterator iterator() {
        return new TLongByteIterator((TUnmodifiableLongByteMap)this){
            TLongByteIterator iter;
            final /* synthetic */ TUnmodifiableLongByteMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableLongByteMap.access$000((TUnmodifiableLongByteMap)this.this$0).iterator();
            }

            public long key() {
                return this.iter.key();
            }

            public byte value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public byte setValue(byte val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public byte putIfAbsent(long key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongByteProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long key, byte amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(long key, byte adjust_amount, byte put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TLongByteMap access$000(TUnmodifiableLongByteMap x0) {
        return x0.m;
    }
}

