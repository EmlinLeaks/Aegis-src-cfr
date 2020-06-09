/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Constraint;
import com.google.common.collect.Constraints;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;

@GwtCompatible
final class Constraints {
    private Constraints() {
    }

    public static <E> Collection<E> constrainedCollection(Collection<E> collection, Constraint<? super E> constraint) {
        return new ConstrainedCollection<E>(collection, constraint);
    }

    public static <E> Set<E> constrainedSet(Set<E> set, Constraint<? super E> constraint) {
        return new ConstrainedSet<E>(set, constraint);
    }

    public static <E> SortedSet<E> constrainedSortedSet(SortedSet<E> sortedSet, Constraint<? super E> constraint) {
        return new ConstrainedSortedSet<E>(sortedSet, constraint);
    }

    public static <E> List<E> constrainedList(List<E> list, Constraint<? super E> constraint) {
        ConstrainedList constrainedList;
        if (list instanceof RandomAccess) {
            constrainedList = new ConstrainedRandomAccessList<E>(list, constraint);
            return constrainedList;
        }
        constrainedList = new ConstrainedList<E>(list, constraint);
        return constrainedList;
    }

    private static <E> ListIterator<E> constrainedListIterator(ListIterator<E> listIterator, Constraint<? super E> constraint) {
        return new ConstrainedListIterator<E>(listIterator, constraint);
    }

    static <E> Collection<E> constrainedTypePreservingCollection(Collection<E> collection, Constraint<E> constraint) {
        if (collection instanceof SortedSet) {
            return Constraints.constrainedSortedSet((SortedSet)collection, constraint);
        }
        if (collection instanceof Set) {
            return Constraints.constrainedSet((Set)collection, constraint);
        }
        if (!(collection instanceof List)) return Constraints.constrainedCollection(collection, constraint);
        return Constraints.constrainedList((List)collection, constraint);
    }

    private static <E> Collection<E> checkElements(Collection<E> elements, Constraint<? super E> constraint) {
        ArrayList<E> copy = Lists.newArrayList(elements);
        Iterator<E> i$ = copy.iterator();
        while (i$.hasNext()) {
            E element = i$.next();
            constraint.checkElement(element);
        }
        return copy;
    }

    static /* synthetic */ Collection access$000(Collection x0, Constraint x1) {
        return Constraints.checkElements(x0, x1);
    }

    static /* synthetic */ ListIterator access$100(ListIterator x0, Constraint x1) {
        return Constraints.constrainedListIterator(x0, x1);
    }
}

