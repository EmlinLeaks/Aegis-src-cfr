/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteByteMap;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.map.TByteByteMap;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteByteMap
implements TByteByteMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteByteMap m;
    private transient TByteSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableByteByteMap(TByteByteMap m) {
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
    public boolean containsValue(byte val) {
        return this.m.containsValue((byte)val);
    }

    @Override
    public byte get(byte key) {
        return this.m.get((byte)key);
    }

    @Override
    public byte put(byte key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteByteMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Byte> map) {
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
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.m.forEachKey((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure procedure) {
        return this.m.forEachValue((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TByteByteProcedure procedure) {
        return this.m.forEachEntry((TByteByteProcedure)procedure);
    }

    @Override
    public TByteByteIterator iterator() {
        return new TByteByteIterator((TUnmodifiableByteByteMap)this){
            TByteByteIterator iter;
            final /* synthetic */ TUnmodifiableByteByteMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableByteByteMap.access$000((TUnmodifiableByteByteMap)this.this$0).iterator();
            }

            public byte key() {
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
    public byte putIfAbsent(byte key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteByteProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte key, byte amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(byte key, byte adjust_amount, byte put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TByteByteMap access$000(TUnmodifiableByteByteMap x0) {
        return x0.m;
    }
}

