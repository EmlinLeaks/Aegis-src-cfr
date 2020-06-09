/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingCollection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Queue;

@GwtCompatible
public abstract class ForwardingQueue<E>
extends ForwardingCollection<E>
implements Queue<E> {
    protected ForwardingQueue() {
    }

    @Override
    protected abstract Queue<E> delegate();

    @CanIgnoreReturnValue
    @Override
    public boolean offer(E o) {
        return this.delegate().offer(o);
    }

    @CanIgnoreReturnValue
    @Override
    public E poll() {
        return (E)this.delegate().poll();
    }

    @CanIgnoreReturnValue
    @Override
    public E remove() {
        return (E)this.delegate().remove();
    }

    @Override
    public E peek() {
        return (E)this.delegate().peek();
    }

    @Override
    public E element() {
        return (E)this.delegate().element();
    }

    protected boolean standardOffer(E e) {
        try {
            return this.add(e);
        }
        catch (IllegalStateException caught) {
            return false;
        }
    }

    protected E standardPeek() {
        try {
            return (E)this.element();
        }
        catch (NoSuchElementException caught) {
            return (E)null;
        }
    }

    protected E standardPoll() {
        try {
            return (E)this.remove();
        }
        catch (NoSuchElementException caught) {
            return (E)null;
        }
    }
}

