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
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedLists;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible
@Beta
final class SortedLists {
    private SortedLists() {
    }

    public static <E extends Comparable> int binarySearch(List<? extends E> list, E e, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
        Preconditions.checkNotNull(e);
        return SortedLists.binarySearch(list, e, Ordering.<C>natural(), (KeyPresentBehavior)presentBehavior, (KeyAbsentBehavior)absentBehavior);
    }

    public static <E, K extends Comparable> int binarySearch(List<E> list, Function<? super E, K> keyFunction, @Nullable K key, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
        return SortedLists.binarySearch(list, keyFunction, key, Ordering.<C>natural(), (KeyPresentBehavior)presentBehavior, (KeyAbsentBehavior)absentBehavior);
    }

    public static <E, K> int binarySearch(List<E> list, Function<? super E, K> keyFunction, @Nullable K key, Comparator<? super K> keyComparator, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
        return SortedLists.binarySearch(Lists.transform(list, keyFunction), key, keyComparator, (KeyPresentBehavior)presentBehavior, (KeyAbsentBehavior)absentBehavior);
    }

    public static <E> int binarySearch(List<? extends E> list, @Nullable E key, Comparator<? super E> comparator, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
        Preconditions.checkNotNull(comparator);
        Preconditions.checkNotNull(list);
        Preconditions.checkNotNull(presentBehavior);
        Preconditions.checkNotNull(absentBehavior);
        if (!(list instanceof RandomAccess)) {
            list = Lists.newArrayList(list);
        }
        int lower = 0;
        int upper = list.size() - 1;
        while (lower <= upper) {
            int middle = lower + upper >>> 1;
            int c = comparator.compare(key, list.get((int)middle));
            if (c < 0) {
                upper = middle - 1;
                continue;
            }
            if (c <= 0) return lower + presentBehavior.resultIndex(comparator, key, list.subList((int)lower, (int)(upper + 1)), (int)(middle - lower));
            lower = middle + 1;
        }
        return absentBehavior.resultIndex((int)lower);
    }
}

