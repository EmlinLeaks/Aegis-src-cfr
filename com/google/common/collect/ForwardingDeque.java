/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingQueue;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

@GwtIncompatible
public abstract class ForwardingDeque<E>
extends ForwardingQueue<E>
implements Deque<E> {
    protected ForwardingDeque() {
    }

    @Override
    protected abstract Deque<E> delegate();

    @Override
    public void addFirst(E e) {
        this.delegate().addFirst(e);
    }

    @Override
    public void addLast(E e) {
        this.delegate().addLast(e);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return this.delegate().descendingIterator();
    }

    @Override
    public E getFirst() {
        return (E)this.delegate().getFirst();
    }

    @Override
    public E getLast() {
        return (E)this.delegate().getLast();
    }

    @CanIgnoreReturnValue
    @Override
    public boolean offerFirst(E e) {
        return this.delegate().offerFirst(e);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean offerLast(E e) {
        return this.delegate().offerLast(e);
    }

    @Override
    public E peekFirst() {
        return (E)this.delegate().peekFirst();
    }

    @Override
    public E peekLast() {
        return (E)this.delegate().peekLast();
    }

    @CanIgnoreReturnValue
    @Override
    public E pollFirst() {
        return (E)this.delegate().pollFirst();
    }

    @CanIgnoreReturnValue
    @Override
    public E pollLast() {
        return (E)this.delegate().pollLast();
    }

    @CanIgnoreReturnValue
    @Override
    public E pop() {
        return (E)this.delegate().pop();
    }

    @Override
    public void push(E e) {
        this.delegate().push(e);
    }

    @CanIgnoreReturnValue
    @Override
    public E removeFirst() {
        return (E)this.delegate().removeFirst();
    }

    @CanIgnoreReturnValue
    @Override
    public E removeLast() {
        return (E)this.delegate().removeLast();
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeFirstOccurrence(Object o) {
        return this.delegate().removeFirstOccurrence((Object)o);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean removeLastOccurrence(Object o) {
        return this.delegate().removeLastOccurrence((Object)o);
    }
}

