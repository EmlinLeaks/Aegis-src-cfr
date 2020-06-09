/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessDoubleList;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableDoubleList
extends TUnmodifiableDoubleCollection
implements TDoubleList {
    static final long serialVersionUID = -283967356065247728L;
    final TDoubleList list;

    public TUnmodifiableDoubleList(TDoubleList list) {
        super((TDoubleCollection)list);
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
    public double get(int index) {
        return this.list.get((int)index);
    }

    @Override
    public int indexOf(double o) {
        return this.list.indexOf((double)o);
    }

    @Override
    public int lastIndexOf(double o) {
        return this.list.lastIndexOf((double)o);
    }

    @Override
    public double[] toArray(int offset, int len) {
        return this.list.toArray((int)offset, (int)len);
    }

    @Override
    public double[] toArray(double[] dest, int offset, int len) {
        return this.list.toArray((double[])dest, (int)offset, (int)len);
    }

    @Override
    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray((double[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    @Override
    public boolean forEachDescending(TDoubleProcedure procedure) {
        return this.list.forEachDescending((TDoubleProcedure)procedure);
    }

    @Override
    public int binarySearch(double value) {
        return this.list.binarySearch((double)value);
    }

    @Override
    public int binarySearch(double value, int fromIndex, int toIndex) {
        return this.list.binarySearch((double)value, (int)fromIndex, (int)toIndex);
    }

    @Override
    public int indexOf(int offset, double value) {
        return this.list.indexOf((int)offset, (double)value);
    }

    @Override
    public int lastIndexOf(int offset, double value) {
        return this.list.lastIndexOf((int)offset, (double)value);
    }

    @Override
    public TDoubleList grep(TDoubleProcedure condition) {
        return this.list.grep((TDoubleProcedure)condition);
    }

    @Override
    public TDoubleList inverseGrep(TDoubleProcedure condition) {
        return this.list.inverseGrep((TDoubleProcedure)condition);
    }

    @Override
    public double max() {
        return this.list.max();
    }

    @Override
    public double min() {
        return this.list.min();
    }

    @Override
    public double sum() {
        return this.list.sum();
    }

    @Override
    public TDoubleList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableDoubleList((TDoubleList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object readResolve() {
        TUnmodifiableDoubleList tUnmodifiableDoubleList;
        if (this.list instanceof RandomAccess) {
            tUnmodifiableDoubleList = new TUnmodifiableRandomAccessDoubleList((TDoubleList)this.list);
            return tUnmodifiableDoubleList;
        }
        tUnmodifiableDoubleList = this;
        return tUnmodifiableDoubleList;
    }

    @Override
    public void add(double[] vals) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(double[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, double[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, double[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double set(int offset, double val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, double[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, double[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double replace(int offset, double val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
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
    public void fill(double val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int fromIndex, int toIndex, double val) {
        throw new UnsupportedOperationException();
    }
}

