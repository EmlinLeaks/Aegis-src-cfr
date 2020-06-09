/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.NoSuchElementException;

@GwtCompatible
abstract class MultitransformedIterator<F, T>
implements Iterator<T> {
    final Iterator<? extends F> backingIterator;
    private Iterator<? extends T> current = Iterators.emptyIterator();
    private Iterator<? extends T> removeFrom;

    MultitransformedIterator(Iterator<? extends F> backingIterator) {
        this.backingIterator = Preconditions.checkNotNull(backingIterator);
    }

    abstract Iterator<? extends T> transform(F var1);

    @Override
    public boolean hasNext() {
        Preconditions.checkNotNull(this.current);
        if (this.current.hasNext()) {
            return true;
        }
        do {
            if (!this.backingIterator.hasNext()) return false;
            this.current = this.transform(this.backingIterator.next());
            Preconditions.checkNotNull(this.current);
        } while (!this.current.hasNext());
        return true;
    }

    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.removeFrom = this.current;
        return (T)this.current.next();
    }

    @Override
    public void remove() {
        CollectPreconditions.checkRemove((boolean)(this.removeFrom != null));
        this.removeFrom.remove();
        this.removeFrom = null;
    }
}

