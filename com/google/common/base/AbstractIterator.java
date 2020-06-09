/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.AbstractIterator;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractIterator<T>
implements Iterator<T> {
    private State state = State.NOT_READY;
    private T next;

    protected AbstractIterator() {
    }

    protected abstract T computeNext();

    @Nullable
    @CanIgnoreReturnValue
    protected final T endOfData() {
        this.state = State.DONE;
        return (T)null;
    }

    @Override
    public final boolean hasNext() {
        Preconditions.checkState((boolean)(this.state != State.FAILED));
        switch (1.$SwitchMap$com$google$common$base$AbstractIterator$State[this.state.ordinal()]) {
            case 1: {
                return true;
            }
            case 2: {
                return false;
            }
        }
        return this.tryToComputeNext();
    }

    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        this.next = this.computeNext();
        if (this.state == State.DONE) return false;
        this.state = State.READY;
        return true;
    }

    @Override
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.state = State.NOT_READY;
        T result = this.next;
        this.next = null;
        return (T)result;
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}

