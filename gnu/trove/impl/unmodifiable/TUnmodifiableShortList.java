/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableShortList
extends TUnmodifiableShortCollection
implements TShortList {
    static final long serialVersionUID = -283967356065247728L;
    final TShortList list;

    public TUnmodifiableShortList(TShortList list) {
        super((TShortCollection)list);
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
    public short get(int index) {
        return this.list.get((int)index);
    }

    @Override
    public int indexOf(short o) {
        return this.list.indexOf((short)o);
    }

    @Override
    public int lastIndexOf(short o) {
        return this.list.lastIndexOf((short)o);
    }

    @Override
    public short[] toArray(int offset, int len) {
        return this.list.toArray((int)offset, (int)len);
    }

    @Override
    public short[] toArray(short[] dest, int offset, int len) {
        return this.list.toArray((short[])dest, (int)offset, (int)len);
    }

    @Override
    public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray((short[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    @Override
    public boolean forEachDescending(TShortProcedure procedure) {
        return this.list.forEachDescending((TShortProcedure)procedure);
    }

    @Override
    public int binarySearch(short value) {
        return this.list.binarySearch((short)value);
    }

    @Override
    public int binarySearch(short value, int fromIndex, int toIndex) {
        return this.list.binarySearch((short)value, (int)fromIndex, (int)toIndex);
    }

    @Override
    public int indexOf(int offset, short value) {
        return this.list.indexOf((int)offset, (short)value);
    }

    @Override
    public int lastIndexOf(int offset, short value) {
        return this.list.lastIndexOf((int)offset, (short)value);
    }

    @Override
    public TShortList grep(TShortProcedure condition) {
        return this.list.grep((TShortProcedure)condition);
    }

    @Override
    public TShortList inverseGrep(TShortProcedure condition) {
        return this.list.inverseGrep((TShortProcedure)condition);
    }

    @Override
    public short max() {
        return this.list.max();
    }

    @Override
    public short min() {
        return this.list.min();
    }

    @Override
    public short sum() {
        return this.list.sum();
    }

    @Override
    public TShortList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableShortList((TShortList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object readResolve() {
        TUnmodifiableShortList tUnmodifiableShortList;
        if (this.list instanceof RandomAccess) {
            tUnmodifiableShortList = new TUnmodifiableRandomAccessShortList((TShortList)this.list);
            return tUnmodifiableShortList;
        }
        tUnmodifiableShortList = this;
        return tUnmodifiableShortList;
    }

    @Override
    public void add(short[] vals) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(short[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, short[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, short[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short set(int offset, short val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, short[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, short[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short replace(int offset, short val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction function) {
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
    public void fill(short val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int fromIndex, int toIndex, short val) {
        throw new UnsupportedOperationException();
    }
}

