/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessCharList;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableCharList
extends TUnmodifiableCharCollection
implements TCharList {
    static final long serialVersionUID = -283967356065247728L;
    final TCharList list;

    public TUnmodifiableCharList(TCharList list) {
        super((TCharCollection)list);
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (this.list.equals((Object)o)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public char get(int index) {
        return this.list.get((int)index);
    }

    @Override
    public int indexOf(char o) {
        return this.list.indexOf((char)o);
    }

    @Override
    public int lastIndexOf(char o) {
        return this.list.lastIndexOf((char)o);
    }

    @Override
    public char[] toArray(int offset, int len) {
        return this.list.toArray((int)offset, (int)len);
    }

    @Override
    public char[] toArray(char[] dest, int offset, int len) {
        return this.list.toArray((char[])dest, (int)offset, (int)len);
    }

    @Override
    public char[] toArray(char[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray((char[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    @Override
    public boolean forEachDescending(TCharProcedure procedure) {
        return this.list.forEachDescending((TCharProcedure)procedure);
    }

    @Override
    public int binarySearch(char value) {
        return this.list.binarySearch((char)value);
    }

    @Override
    public int binarySearch(char value, int fromIndex, int toIndex) {
        return this.list.binarySearch((char)value, (int)fromIndex, (int)toIndex);
    }

    @Override
    public int indexOf(int offset, char value) {
        return this.list.indexOf((int)offset, (char)value);
    }

    @Override
    public int lastIndexOf(int offset, char value) {
        return this.list.lastIndexOf((int)offset, (char)value);
    }

    @Override
    public TCharList grep(TCharProcedure condition) {
        return this.list.grep((TCharProcedure)condition);
    }

    @Override
    public TCharList inverseGrep(TCharProcedure condition) {
        return this.list.inverseGrep((TCharProcedure)condition);
    }

    @Override
    public char max() {
        return this.list.max();
    }

    @Override
    public char min() {
        return this.list.min();
    }

    @Override
    public char sum() {
        return this.list.sum();
    }

    @Override
    public TCharList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableCharList((TCharList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object readResolve() {
        TUnmodifiableCharList tUnmodifiableCharList;
        if (this.list instanceof RandomAccess) {
            tUnmodifiableCharList = new TUnmodifiableRandomAccessCharList((TCharList)this.list);
            return tUnmodifiableCharList;
        }
        tUnmodifiableCharList = this;
        return tUnmodifiableCharList;
    }

    @Override
    public void add(char[] vals) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(char[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, char[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, char[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char set(int offset, char val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, char[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, char[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char replace(int offset, char val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reverse(int from, int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shuffle(Random rand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(char val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int fromIndex, int toIndex, char val) {
        throw new UnsupportedOperationException();
    }
}

