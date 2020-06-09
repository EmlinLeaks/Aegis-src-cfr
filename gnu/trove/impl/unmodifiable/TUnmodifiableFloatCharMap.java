/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCharMap;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatCharMap
implements TFloatCharMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatCharMap m;
    private transient TFloatSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableFloatCharMap(TFloatCharMap m) {
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
    public boolean containsValue(char val) {
        return this.m.containsValue((char)val);
    }

    @Override
    public char get(float key) {
        return this.m.get((float)key);
    }

    @Override
    public char put(float key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatCharMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Character> map) {
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
        return this.m.forEachKey((TFloatProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        return this.m.forEachValue((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TFloatCharProcedure procedure) {
        return this.m.forEachEntry((TFloatCharProcedure)procedure);
    }

    @Override
    public TFloatCharIterator iterator() {
        return new TFloatCharIterator((TUnmodifiableFloatCharMap)this){
            TFloatCharIterator iter;
            final /* synthetic */ TUnmodifiableFloatCharMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableFloatCharMap.access$000((TUnmodifiableFloatCharMap)this.this$0).iterator();
            }

            public float key() {
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
    public char putIfAbsent(float key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatCharProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float key, char amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(float key, char adjust_amount, char put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TFloatCharMap access$000(TUnmodifiableFloatCharMap x0) {
        return x0.m;
    }
}

