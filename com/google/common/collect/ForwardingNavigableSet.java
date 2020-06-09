/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.Iterators;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

@GwtIncompatible
public abstract class ForwardingNavigableSet<E>
extends ForwardingSortedSet<E>
implements NavigableSet<E> {
    protected ForwardingNavigableSet() {
    }

    @Override
    protected abstract NavigableSet<E> delegate();

    @Override
    public E lower(E e) {
        return (E)this.delegate().lower(e);
    }

    protected E standardLower(E e) {
        return (E)Iterators.getNext(this.headSet(e, (boolean)false).descendingIterator(), null);
    }

    @Override
    public E floor(E e) {
        return (E)this.delegate().floor(e);
    }

    protected E standardFloor(E e) {
        return (E)Iterators.getNext(this.headSet(e, (boolean)true).descendingIterator(), null);
    }

    @Override
    public E ceiling(E e) {
        return (E)this.delegate().ceiling(e);
    }

    protected E standardCeiling(E e) {
        return (E)Iterators.getNext(this.tailSet(e, (boolean)true).iterator(), null);
    }

    @Override
    public E higher(E e) {
        return (E)this.delegate().higher(e);
    }

    protected E standardHigher(E e) {
        return (E)Iterators.getNext(this.tailSet(e, (boolean)false).iterator(), null);
    }

    @Override
    public E pollFirst() {
        return (E)this.delegate().pollFirst();
    }

    protected E standardPollFirst() {
        return (E)Iterators.pollNext(this.iterator());
    }

    @Override
    public E pollLast() {
        return (E)this.delegate().pollLast();
    }

    protected E standardPollLast() {
        return (E)Iterators.pollNext(this.descendingIterator());
    }

    protected E standardFirst() {
        return (E)this.iterator().next();
    }

    protected E standardLast() {
        return (E)this.descendingIterator().next();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return this.delegate().descendingSet();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return this.delegate().descendingIterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return this.delegate().subSet(fromElement, (boolean)fromInclusive, toElement, (boolean)toInclusive);
    }

    @Beta
    protected NavigableSet<E> standardSubSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return this.tailSet(fromElement, (boolean)fromInclusive).headSet(toElement, (boolean)toInclusive);
    }

    @Override
    protected SortedSet<E> standardSubSet(E fromElement, E toElement) {
        return this.subSet(fromElement, (boolean)true, toElement, (boolean)false);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return this.delegate().headSet(toElement, (boolean)inclusive);
    }

    protected SortedSet<E> standardHeadSet(E toElement) {
        return this.headSet(toElement, (boolean)false);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return this.delegate().tailSet(fromElement, (boolean)inclusive);
    }

    protected SortedSet<E> standardTailSet(E fromElement) {
        return this.tailSet(fromElement, (boolean)true);
    }
}

