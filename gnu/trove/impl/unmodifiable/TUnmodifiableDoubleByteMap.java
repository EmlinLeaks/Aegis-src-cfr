/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleByteMap;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleByteMap
implements TDoubleByteMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleByteMap m;
    private transient TDoubleSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableDoubleByteMap(TDoubleByteMap m) {
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
    public boolean containsValue(byte val) {
        return this.m.containsValue((byte)val);
    }

    @Override
    public byte get(double key) {
        return this.m.get((double)key);
    }

    @Override
    public byte put(double key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleByteMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Byte> map) {
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.m.forEachKey((TDoubleProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure procedure) {
        return this.m.forEachValue((TByteProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TDoubleByteProcedure procedure) {
        return this.m.forEachEntry((TDoubleByteProcedure)procedure);
    }

    @Override
    public TDoubleByteIterator iterator() {
        return new TDoubleByteIterator((TUnmodifiableDoubleByteMap)this){
            TDoubleByteIterator iter;
            final /* synthetic */ TUnmodifiableDoubleByteMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableDoubleByteMap.access$000((TUnmodifiableDoubleByteMap)this.this$0).iterator();
            }

            public double key() {
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
    public byte putIfAbsent(double key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleByteProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double key, byte amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(double key, byte adjust_amount, byte put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TDoubleByteMap access$000(TUnmodifiableDoubleByteMap x0) {
        return x0.m;
    }
}

