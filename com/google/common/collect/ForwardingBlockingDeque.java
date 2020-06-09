/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

@GwtIncompatible
public abstract class ForwardingBlockingDeque<E>
extends ForwardingDeque<E>
implements BlockingDeque<E> {
    protected ForwardingBlockingDeque() {
    }

    @Override
    protected abstract BlockingDeque<E> delegate();

    @Override
    public int remainingCapacity() {
        return this.delegate().remainingCapacity();
    }

    @Override
    public void putFirst(E e) throws InterruptedException {
        this.delegate().putFirst(e);
    }

    @Override
    public void putLast(E e) throws InterruptedException {
        this.delegate().putLast(e);
    }

    @Override
    public boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().offerFirst(e, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().offerLast(e, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public E takeFirst() throws InterruptedException {
        return (E)this.delegate().takeFirst();
    }

    @Override
    public E takeLast() throws InterruptedException {
        return (E)this.delegate().takeLast();
    }

    @Override
    public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return (E)this.delegate().pollFirst((long)timeout, (TimeUnit)unit);
    }

    @Override
    public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        return (E)this.delegate().pollLast((long)timeout, (TimeUnit)unit);
    }

    @Override
    public void put(E e) throws InterruptedException {
        this.delegate().put(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().offer(e, (long)timeout, (TimeUnit)unit);
    }

    @Override
    public E take() throws InterruptedException {
        return (E)this.delegate().take();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return (E)this.delegate().poll((long)timeout, (TimeUnit)unit);
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return this.delegate().drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return this.delegate().drainTo(c, (int)maxElements);
    }
}

