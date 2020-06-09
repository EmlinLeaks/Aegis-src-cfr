/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
public final class Collections2 {
    static final Joiner STANDARD_JOINER = Joiner.on((String)", ").useForNull((String)"null");

    private Collections2() {
    }

    public static <E> Collection<E> filter(Collection<E> unfiltered, Predicate<? super E> predicate) {
        if (!(unfiltered instanceof FilteredCollection)) return new FilteredCollection<E>(Preconditions.checkNotNull(unfiltered), Preconditions.checkNotNull(predicate));
        return ((FilteredCollection)unfiltered).createCombined(predicate);
    }

    static boolean safeContains(Collection<?> collection, @Nullable Object object) {
        Preconditions.checkNotNull(collection);
        try {
            return collection.contains((Object)object);
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    static boolean safeRemove(Collection<?> collection, @Nullable Object object) {
        Preconditions.checkNotNull(collection);
        try {
            return collection.remove((Object)object);
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    public static <F, T> Collection<T> transform(Collection<F> fromCollection, Function<? super F, T> function) {
        return new TransformedCollection<F, T>(fromCollection, function);
    }

    static boolean containsAllImpl(Collection<?> self, Collection<?> c) {
        return Iterables.all(c, Predicates.in(self));
    }

    static String toStringImpl(Collection<?> collection) {
        StringBuilder sb = Collections2.newStringBuilderForCollection((int)collection.size()).append((char)'[');
        STANDARD_JOINER.appendTo((StringBuilder)sb, Iterables.transform(collection, new Function<Object, Object>(collection){
            final /* synthetic */ Collection val$collection;
            {
                this.val$collection = collection;
            }

            public Object apply(Object input) {
                if (input == this.val$collection) {
                    return "(this Collection)";
                }
                Object object = input;
                return object;
            }
        }));
        return sb.append((char)']').toString();
    }

    static StringBuilder newStringBuilderForCollection(int size) {
        CollectPreconditions.checkNonnegative((int)size, (String)"size");
        return new StringBuilder((int)((int)Math.min((long)((long)size * 8L), (long)0x40000000L)));
    }

    static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection)iterable;
    }

    @Beta
    public static <E extends Comparable<? super E>> Collection<List<E>> orderedPermutations(Iterable<E> elements) {
        return Collections2.orderedPermutations(elements, Ordering.<C>natural());
    }

    @Beta
    public static <E> Collection<List<E>> orderedPermutations(Iterable<E> elements, Comparator<? super E> comparator) {
        return new OrderedPermutationCollection<E>(elements, comparator);
    }

    @Beta
    public static <E> Collection<List<E>> permutations(Collection<E> elements) {
        return new PermutationCollection<E>(ImmutableList.copyOf(elements));
    }

    private static boolean isPermutation(List<?> first, List<?> second) {
        if (first.size() != second.size()) {
            return false;
        }
        HashMultiset<?> firstMultiset = HashMultiset.create(first);
        HashMultiset<?> secondMultiset = HashMultiset.create(second);
        return firstMultiset.equals(secondMultiset);
    }

    private static boolean isPositiveInt(long n) {
        if (n < 0L) return false;
        if (n > Integer.MAX_VALUE) return false;
        return true;
    }

    static /* synthetic */ boolean access$000(long x0) {
        return Collections2.isPositiveInt((long)x0);
    }

    static /* synthetic */ boolean access$100(List x0, List x1) {
        return Collections2.isPermutation(x0, x1);
    }
}

