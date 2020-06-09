/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TLongCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableLongCollection
implements TLongCollection,
Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    final TLongCollection c;

    public TUnmodifiableLongCollection(TLongCollection c) {
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
    public boolean contains(long o) {
        return this.c.contains((long)o);
    }

    @Override
    public long[] toArray() {
        return this.c.toArray();
    }

    @Override
    public long[] toArray(long[] a) {
        return this.c.toArray((long[])a);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public long getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TLongProcedure procedure) {
        return this.c.forEach((TLongProcedure)procedure);
    }

    @Override
    public TLongIterator iterator() {
        return new TLongIterator((TUnmodifiableLongCollection)this){
            TLongIterator i;
            final /* synthetic */ TUnmodifiableLongCollection this$0;
            {
                this.this$0 = this$0;
                this.i = this.this$0.c.iterator();
            }

            public boolean hasNext() {
                return this.i.hasNext();
            }

            public long next() {
                return this.i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(long e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(long o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(TLongCollection coll) {
        return this.c.containsAll((TLongCollection)coll);
    }

    @Override
    public boolean containsAll(long[] array) {
        return this.c.containsAll((long[])array);
    }

    @Override
    public boolean addAll(TLongCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Long> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(long[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TLongCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(long[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TLongCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(long[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}

