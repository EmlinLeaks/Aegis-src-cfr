/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCharMap;
import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.map.TIntCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntCharMap
implements TIntCharMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntCharMap m;
    private transient TIntSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableIntCharMap(TIntCharMap m) {
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
    public boolean containsKey(int key) {
        return this.m.containsKey((int)key);
    }

    @Override
    public boolean containsValue(char val) {
        return this.m.containsValue((char)val);
    }

    @Override
    public char get(int key) {
        return this.m.get((int)key);
    }

    @Override
    public char put(int key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntCharMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Character> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TIntSet keySet() {
        if (this.keySet != null) return this.keySet;
        this.keySet = TCollections.unmodifiableSet((TIntSet)this.m.keySet());
        return this.keySet;
    }

    @Override
    public int[] keys() {
        return this.m.keys();
    }

    @Override
    public int[] keys(int[] array) {
        return this.m.keys((int[])array);
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
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure procedure) {
        return this.m.forEachKey((TIntProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        return this.m.forEachValue((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TIntCharProcedure procedure) {
        return this.m.forEachEntry((TIntCharProcedure)procedure);
    }

    @Override
    public TIntCharIterator iterator() {
        return new TIntCharIterator((TUnmodifiableIntCharMap)this){
            TIntCharIterator iter;
            final /* synthetic */ TUnmodifiableIntCharMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableIntCharMap.access$000((TUnmodifiableIntCharMap)this.this$0).iterator();
            }

            public int key() {
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
    public char putIfAbsent(int key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntCharProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int key, char amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(int key, char adjust_amount, char put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TIntCharMap access$000(TUnmodifiableIntCharMap x0) {
        return x0.m;
    }
}

