/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatShortMap;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatShortMap
implements TFloatShortMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatShortMap m;
    private transient TFloatSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableFloatShortMap(TFloatShortMap m) {
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
    public boolean containsKey(float key) {
        return this.m.containsKey((float)key);
    }

    @Override
    public boolean containsValue(short val) {
        return this.m.containsValue((short)val);
    }

    @Override
    public short get(float key) {
        return this.m.get((float)key);
    }

    @Override
    public short put(float key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatShortMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Short> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TFloatSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TFloatSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public float[] keys() {
        return this.m.keys();
    }

    @Override
    public float[] keys(float[] array) {
        return this.m.keys((float[])array);
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
        return this.m.forEachKey((TFloatProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        return this.m.forEachValue((TShortProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TFloatShortProcedure procedure) {
        return this.m.forEachEntry((TFloatShortProcedure)procedure);
    }

    @Override
    public TFloatShortIterator iterator() {
        return new TFloatShortIterator((TUnmodifiableFloatShortMap)this){
            TFloatShortIterator iter;
            final /* synthetic */ TUnmodifiableFloatShortMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableFloatShortMap.access$000((TUnmodifiableFloatShortMap)this.this$0).iterator();
            }

            public float key() {
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
    public short putIfAbsent(float key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatShortProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float key, short amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(float key, short adjust_amount, short put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TFloatShortMap access$000(TUnmodifiableFloatShortMap x0) {
        return x0.m;
    }
}

