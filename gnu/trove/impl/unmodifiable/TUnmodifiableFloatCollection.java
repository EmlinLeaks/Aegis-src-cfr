/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableFloatCollection
implements TFloatCollection,
Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    final TFloatCollection c;

    public TUnmodifiableFloatCollection(TFloatCollection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        this.c = c;
    }

    @Override
    public int size() {
        return this.c.size();
    }

    @Override
    public boolean isEmpty() {
        return this.c.isEmpty();
    }

    @Override
    public boolean contains(float o) {
        return this.c.contains((float)o);
    }

    @Override
    public float[] toArray() {
        return this.c.toArray();
    }

    @Override
    public float[] toArray(float[] a) {
        return this.c.toArray((float[])a);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public float getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TFloatProcedure procedure) {
        return this.c.forEach((TFloatProcedure)procedure);
    }

    @Override
    public TFloatIterator iterator() {
        return new TFloatIterator((TUnmodifiableFloatCollection)this){
            TFloatIterator i;
            final /* synthetic */ TUnmodifiableFloatCollection this$0;
            {
                this.this$0 = this$0;
                this.i = this.this$0.c.iterator();
            }

            public boolean hasNext() {
                return this.i.hasNext();
            }

            public float next() {
                return this.i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(float e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(float o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(TFloatCollection coll) {
        return this.c.containsAll((TFloatCollection)coll);
    }

    @Override
    public boolean containsAll(float[] array) {
        return this.c.containsAll((float[])array);
    }

    @Override
    public boolean addAll(TFloatCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Float> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(float[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TFloatCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(float[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TFloatCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(float[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}

