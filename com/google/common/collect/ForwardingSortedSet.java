/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSet<E>
extends ForwardingSet<E>
implements SortedSet<E> {
    protected ForwardingSortedSet() {
    }

    @Override
    protected abstract SortedSet<E> delegate();

    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }

    @Override
    public E first() {
        return (E)this.delegate().first();
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return this.delegate().headSet(toElement);
    }

    @Override
    public E last() {
        return (E)this.delegate().last();
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return this.delegate().subSet(fromElement, toElement);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return this.delegate().tailSet(fromElement);
    }

    private int unsafeCompare(Object o1, Object o2) {
        int n;
        Comparator<E> comparator = this.comparator();
        if (comparator == null) {
            n = ((Comparable)o1).compareTo(o2);
            return n;
        }
        n = comparator.compare(o1, o2);
        return n;
    }

    @Beta
    @Override
    protected boolean standardContains(@Nullable Object object) {
        try {
            ForwardingSortedSet self = this;
            Object ceiling = self.tailSet(object).first();
            if (this.unsafeCompare((Object)ceiling, (Object)object) != 0) return false;
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (NoSuchElementException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    @Beta
    @Override
    protected boolean standardRemove(@Nullable Object object) {
        try {
            ForwardingSortedSet self = this;
            Iterator<E> iterator = self.tailSet(object).iterator();
            if (!iterator.hasNext()) return false;
            E ceiling = iterator.next();
            if (this.unsafeCompare(ceiling, (Object)object) != 0) return false;
            iterator.remove();
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    @Beta
    protected SortedSet<E> standardSubSet(E fromElement, E toElement) {
        return this.tailSet(fromElement).headSet(toElement);
    }
}

