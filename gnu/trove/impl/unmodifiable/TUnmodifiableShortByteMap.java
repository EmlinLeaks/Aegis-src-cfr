/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortByteMap;
import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.map.TShortByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TShortByteProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortByteMap
implements TShortByteMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortByteMap m;
    private transient TShortSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableShortByteMap(TShortByteMap m) {
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
    public boolean containsValue(byte val) {
        return this.m.containsValue((byte)val);
    }

    @Override
    public byte get(short key) {
        return this.m.get((short)key);
    }

    @Override
    public byte put(short key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortByteMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Byte> map) {
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.m.forEachKey((TShortProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure procedure) {
        return this.m.forEachValue((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TShortByteProcedure procedure) {
        return this.m.forEachEntry((TShortByteProcedure)procedure);
    }

    @Override
    public TShortByteIterator iterator() {
        return new TShortByteIterator((TUnmodifiableShortByteMap)this){
            TShortByteIterator iter;
            final /* synthetic */ TUnmodifiableShortByteMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableShortByteMap.access$000((TUnmodifiableShortByteMap)this.this$0).iterator();
            }

            public short key() {
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
    public byte putIfAbsent(short key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortByteProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short key, byte amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(short key, byte adjust_amount, byte put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TShortByteMap access$000(TUnmodifiableShortByteMap x0) {
        return x0.m;
    }
}

