/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableCharCollection
implements TCharCollection,
Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    final TCharCollection c;

    public TUnmodifiableCharCollection(TCharCollection c) {
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
    public boolean contains(char o) {
        return this.c.contains((char)o);
    }

    @Override
    public char[] toArray() {
        return this.c.toArray();
    }

    @Override
    public char[] toArray(char[] a) {
        return this.c.toArray((char[])a);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public char getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TCharProcedure procedure) {
        return this.c.forEach((TCharProcedure)procedure);
    }

    @Override
    public TCharIterator iterator() {
        return new TCharIterator((TUnmodifiableCharCollection)this){
            TCharIterator i;
            final /* synthetic */ TUnmodifiableCharCollection this$0;
            {
                this.this$0 = this$0;
                this.i = this.this$0.c.iterator();
            }

            public boolean hasNext() {
                return this.i.hasNext();
            }

            public char next() {
                return this.i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(char e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(char o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(TCharCollection coll) {
        return this.c.containsAll((TCharCollection)coll);
    }

    @Override
    public boolean containsAll(char[] array) {
        return this.c.containsAll((char[])array);
    }

    @Override
    public boolean addAll(TCharCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Character> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(char[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TCharCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(char[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TCharCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(char[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}

