/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharIntMap;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharIntMap
implements TCharIntMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharIntMap m;
    private transient TCharSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableCharIntMap(TCharIntMap m) {
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
    public boolean containsValue(int val) {
        return this.m.containsValue((int)val);
    }

    @Override
    public int get(char key) {
        return this.m.get((char)key);
    }

    @Override
    public int put(char key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharIntMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Integer> map) {
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
    public TIntCollection valueCollection() {
        if (this.values != null) return this.values;
        this.values = TCollections.unmodifiableCollection((TIntCollection)this.m.valueCollection());
        return this.values;
    }

    @Override
    public int[] values() {
        return this.m.values();
    }

    @Override
    public int[] values(int[] array) {
        return this.m.values((int[])array);
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.m.forEachKey((TCharProcedure)procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        return this.m.forEachValue((TIntProcedure)procedure);
    }

    @Override
    public boolean forEachEntry(TCharIntProcedure procedure) {
        return this.m.forEachEntry((TCharIntProcedure)procedure);
    }

    @Override
    public TCharIntIterator iterator() {
        return new TCharIntIterator((TUnmodifiableCharIntMap)this){
            TCharIntIterator iter;
            final /* synthetic */ TUnmodifiableCharIntMap this$0;
            {
                this.this$0 = this$0;
                this.iter = TUnmodifiableCharIntMap.access$000((TUnmodifiableCharIntMap)this.this$0).iterator();
            }

            public char key() {
                return this.iter.key();
            }

            public int value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public int setValue(int val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int putIfAbsent(char key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharIntProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char key, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(char key, int adjust_amount, int put_amount) {
        throw new UnsupportedOperationException();
    }

    static /* synthetic */ TCharIntMap access$000(TUnmodifiableCharIntMap x0) {
        return x0.m;
    }
}

