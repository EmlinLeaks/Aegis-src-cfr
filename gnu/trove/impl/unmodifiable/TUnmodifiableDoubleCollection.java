/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableDoubleCollection
implements TDoubleCollection,
Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    final TDoubleCollection c;

    public TUnmodifiableDoubleCollection(TDoubleCollection c) {
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
    public boolean contains(double o) {
        return this.c.contains((double)o);
    }

    @Override
    public double[] toArray() {
        return this.c.toArray();
    }

    @Override
    public double[] toArray(double[] a) {
        return this.c.toArray((double[])a);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public double getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TDoubleProcedure procedure) {
        return this.c.forEach((TDoubleProcedure)procedure);
    }

    @Override
    public TDoubleIterator iterator() {
        return new TDoubleIterator((TUnmodifiableDoubleCollection)this){
            TDoubleIterator i;
            final /* synthetic */ TUnmodifiableDoubleCollection this$0;
            {
                this.this$0 = this$0;
                this.i = this.this$0.c.iterator();
            }

            public boolean hasNext() {
                return this.i.hasNext();
            }

            public double next() {
                return this.i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(double e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(double o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(TDoubleCollection coll) {
        return this.c.containsAll((TDoubleCollection)coll);
    }

    @Override
    public boolean containsAll(double[] array) {
        return this.c.containsAll((double[])array);
    }

    @Override
    public boolean addAll(TDoubleCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Double> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(double[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TDoubleCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(double[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TDoubleCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(double[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}

