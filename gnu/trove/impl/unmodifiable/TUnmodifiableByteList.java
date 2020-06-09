/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessByteList;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableByteList
extends TUnmodifiableByteCollection
implements TByteList {
    static final long serialVersionUID = -283967356065247728L;
    final TByteList list;

    public TUnmodifiableByteList(TByteList list) {
        super((TByteCollection)list);
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
    public byte get(int index) {
        return this.list.get((int)index);
    }

    @Override
    public int indexOf(byte o) {
        return this.list.indexOf((byte)o);
    }

    @Override
    public int lastIndexOf(byte o) {
        return this.list.lastIndexOf((byte)o);
    }

    @Override
    public byte[] toArray(int offset, int len) {
        return this.list.toArray((int)offset, (int)len);
    }

    @Override
    public byte[] toArray(byte[] dest, int offset, int len) {
        return this.list.toArray((byte[])dest, (int)offset, (int)len);
    }

    @Override
    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray((byte[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    @Override
    public boolean forEachDescending(TByteProcedure procedure) {
        return this.list.forEachDescending((TByteProcedure)procedure);
    }

    @Override
    public int binarySearch(byte value) {
        return this.list.binarySearch((byte)value);
    }

    @Override
    public int binarySearch(byte value, int fromIndex, int toIndex) {
        return this.list.binarySearch((byte)value, (int)fromIndex, (int)toIndex);
    }

    @Override
    public int indexOf(int offset, byte value) {
        return this.list.indexOf((int)offset, (byte)value);
    }

    @Override
    public int lastIndexOf(int offset, byte value) {
        return this.list.lastIndexOf((int)offset, (byte)value);
    }

    @Override
    public TByteList grep(TByteProcedure condition) {
        return this.list.grep((TByteProcedure)condition);
    }

    @Override
    public TByteList inverseGrep(TByteProcedure condition) {
        return this.list.inverseGrep((TByteProcedure)condition);
    }

    @Override
    public byte max() {
        return this.list.max();
    }

    @Override
    public byte min() {
        return this.list.min();
    }

    @Override
    public byte sum() {
        return this.list.sum();
    }

    @Override
    public TByteList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableByteList((TByteList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object readResolve() {
        TUnmodifiableByteList tUnmodifiableByteList;
        if (this.list instanceof RandomAccess) {
            tUnmodifiableByteList = new TUnmodifiableRandomAccessByteList((TByteList)this.list);
            return tUnmodifiableByteList;
        }
        tUnmodifiableByteList = this;
        return tUnmodifiableByteList;
    }

    @Override
    public void add(byte[] vals) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(byte[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, byte[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, byte[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte set(int offset, byte val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, byte[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, byte[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte replace(int offset, byte val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction function) {
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
    public void fill(byte val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int fromIndex, int toIndex, byte val) {
        throw new UnsupportedOperationException();
    }
}

