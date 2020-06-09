/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharShortMap;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharShortMap
implements TCharShortMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharShortMap m;
    private transient TCharSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableCharShortMap(TCharShortMap m) {
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
    public boolean containsValue(short val) {
        return this.m.containsValue((short)val);
    }

    @Override
    public short get(char key) {
        return this.m.get((char)key);
    }

    @Override
    public short put(char key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharShortMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Short> map) {
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.m.forEachKey((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        return this.m.forEachValue((TShortProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TCharShortProcedure procedure) {
        return this.m.forEachEntry((TCharShortProcedure)procedure);
    }

    @Override
    public TCharShortIterator iterator() {
        return new TCharShortIterator((TUnmodifiableCharShortMap)this){
            TCharShortIterator iter;
            final /* synthetic */ TUnmodifiableCharShortMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableCharShortMap.access$000((TUnmodifiableCharShortMap)this.this$0).iterator();
            }

            public char key() {
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
    public short putIfAbsent(char key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharShortProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char key, short amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(char key, short adjust_amount, short put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TCharShortMap access$000(TUnmodifiableCharShortMap x0) {
        return x0.m;
    }
}

