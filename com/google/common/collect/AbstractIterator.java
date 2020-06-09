/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.NoSuchElementException;

@GwtCompatible
public abstract class AbstractIterator<T>
extends UnmodifiableIterator<T> {
    private State state = State.NOT_READY;
    private T next;

    protected AbstractIterator() {
    }

    protected abstract T computeNext();

    @CanIgnoreReturnValue
    protected final T endOfData() {
        this.state = State.DONE;
        return (T)null;
    }

    @CanIgnoreReturnValue
    @Override
    public final boolean hasNext() {
        Preconditions.checkState((boolean)(this.state != State.FAILED));
        switch (1.$SwitchMap$com$google$common$collect$AbstractIterator$State[this.state.ordinal()]) {
            case 1: {
                return false;
            }
            case 2: {
                return true;
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

    @CanIgnoreReturnValue
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

    public final T peek() {
        if (this.hasNext()) return (T)this.next;
        throw new NoSuchElementException();
    }
}

