/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessFloatList;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableFloatList
extends TUnmodifiableFloatCollection
implements TFloatList {
    static final long serialVersionUID = -283967356065247728L;
    final TFloatList list;

    public TUnmodifiableFloatList(TFloatList list) {
        super((TFloatCollection)list);
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
    public float get(int index) {
        return this.list.get((int)index);
    }

    @Override
    public int indexOf(float o) {
        return this.list.indexOf((float)o);
    }

    @Override
    public int lastIndexOf(float o) {
        return this.list.lastIndexOf((float)o);
    }

    @Override
    public float[] toArray(int offset, int len) {
        return this.list.toArray((int)offset, (int)len);
    }

    @Override
    public float[] toArray(float[] dest, int offset, int len) {
        return this.list.toArray((float[])dest, (int)offset, (int)len);
    }

    @Override
    public float[] toArray(float[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray((float[])dest, (int)source_pos, (int)dest_pos, (int)len);
    }

    @Override
    public boolean forEachDescending(TFloatProcedure procedure) {
        return this.list.forEachDescending((TFloatProcedure)procedure);
    }

    @Override
    public int binarySearch(float value) {
        return this.list.binarySearch((float)value);
    }

    @Override
    public int binarySearch(float value, int fromIndex, int toIndex) {
        return this.list.binarySearch((float)value, (int)fromIndex, (int)toIndex);
    }

    @Override
    public int indexOf(int offset, float value) {
        return this.list.indexOf((int)offset, (float)value);
    }

    @Override
    public int lastIndexOf(int offset, float value) {
        return this.list.lastIndexOf((int)offset, (float)value);
    }

    @Override
    public TFloatList grep(TFloatProcedure condition) {
        return this.list.grep((TFloatProcedure)condition);
    }

    @Override
    public TFloatList inverseGrep(TFloatProcedure condition) {
        return this.list.inverseGrep((TFloatProcedure)condition);
    }

    @Override
    public float max() {
        return this.list.max();
    }

    @Override
    public float min() {
        return this.list.min();
    }

    @Override
    public float sum() {
        return this.list.sum();
    }

    @Override
    public TFloatList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableFloatList((TFloatList)this.list.subList((int)fromIndex, (int)toIndex));
    }

    private Object readResolve() {
        TUnmodifiableFloatList tUnmodifiableFloatList;
        if (this.list instanceof RandomAccess) {
            tUnmodifiableFloatList = new TUnmodifiableRandomAccessFloatList((TFloatList)this.list);
            return tUnmodifiableFloatList;
        }
        tUnmodifiableFloatList = this;
        return tUnmodifiableFloatList;
    }

    @Override
    public void add(float[] vals) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(float[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, float[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int offset, float[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float set(int offset, float val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, float[] values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int offset, float[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float replace(int offset, float val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
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
    public void fill(float val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int fromIndex, int toIndex, float val) {
        throw new UnsupportedOperationException();
    }
}

