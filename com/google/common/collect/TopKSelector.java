/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
final class TopKSelector<T> {
    private final int k;
    private final Comparator<? super T> comparator;
    private final T[] buffer;
    private int bufferSize;
    private T threshold;

    public static <T extends Comparable<? super T>> TopKSelector<T> least(int k) {
        return TopKSelector.least((int)k, Ordering.<C>natural());
    }

    public static <T extends Comparable<? super T>> TopKSelector<T> greatest(int k) {
        return TopKSelector.greatest((int)k, Ordering.<C>natural());
    }

    public static <T> TopKSelector<T> least(int k, Comparator<? super T> comparator) {
        return new TopKSelector<T>(comparator, (int)k);
    }

    public static <T> TopKSelector<T> greatest(int k, Comparator<? super T> comparator) {
        return new TopKSelector<S>(Ordering.from(comparator).reverse(), (int)k);
    }

    private TopKSelector(Comparator<? super T> comparator, int k) {
        this.comparator = Preconditions.checkNotNull(comparator, (Object)"comparator");
        this.k = k;
        Preconditions.checkArgument((boolean)(k >= 0), (String)"k must be nonnegative, was %s", (int)k);
        this.buffer = new Object[k * 2];
        this.bufferSize = 0;
        this.threshold = null;
    }

    public void offer(@Nullable T elem) {
        if (this.k == 0) {
            return;
        }
        if (this.bufferSize == 0) {
            this.buffer[0] = elem;
            this.threshold = elem;
            this.bufferSize = 1;
            return;
        }
        if (this.bufferSize < this.k) {
            this.buffer[this.bufferSize++] = elem;
            if (this.comparator.compare(elem, this.threshold) <= 0) return;
            this.threshold = elem;
            return;
        }
        if (this.comparator.compare(elem, this.threshold) >= 0) return;
        this.buffer[this.bufferSize++] = elem;
        if (this.bufferSize != 2 * this.k) return;
        this.trim();
    }

    private void trim() {
        int left = 0;
        int right = 2 * this.k - 1;
        int minThresholdPosition = 0;
        int iterations = 0;
        int maxIterations = IntMath.log2((int)(right - left), (RoundingMode)RoundingMode.CEILING) * 3;
        while (left < right) {
            int pivotIndex = left + right + 1 >>> 1;
            int pivotNewIndex = this.partition((int)left, (int)right, (int)pivotIndex);
            if (pivotNewIndex > this.k) {
                right = pivotNewIndex - 1;
            } else {
                if (pivotNewIndex >= this.k) break;
                left = Math.max((int)pivotNewIndex, (int)(left + 1));
                minThresholdPosition = pivotNewIndex;
            }
            if (++iterations < maxIterations) continue;
            Arrays.sort(this.buffer, (int)left, (int)right, this.comparator);
            break;
        }
        this.bufferSize = this.k;
        this.threshold = this.buffer[minThresholdPosition];
        int i = minThresholdPosition + 1;
        while (i < this.k) {
            if (this.comparator.compare(this.buffer[i], this.threshold) > 0) {
                this.threshold = this.buffer[i];
            }
            ++i;
        }
    }

    private int partition(int left, int right, int pivotIndex) {
        T pivotValue = this.buffer[pivotIndex];
        this.buffer[pivotIndex] = this.buffer[right];
        int pivotNewIndex = left;
        int i = left;
        do {
            if (i >= right) {
                this.buffer[right] = this.buffer[pivotNewIndex];
                this.buffer[pivotNewIndex] = pivotValue;
                return pivotNewIndex;
            }
            if (this.comparator.compare(this.buffer[i], pivotValue) < 0) {
                this.swap((int)pivotNewIndex, (int)i);
                ++pivotNewIndex;
            }
            ++i;
        } while (true);
    }

    private void swap(int i, int j) {
        T tmp = this.buffer[i];
        this.buffer[i] = this.buffer[j];
        this.buffer[j] = tmp;
    }

    public void offerAll(Iterable<? extends T> elements) {
        this.offerAll(elements.iterator());
    }

    public void offerAll(Iterator<? extends T> elements) {
        while (elements.hasNext()) {
            this.offer(elements.next());
        }
    }

    public List<T> topK() {
        Arrays.sort(this.buffer, (int)0, (int)this.bufferSize, this.comparator);
        if (this.bufferSize <= this.k) return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(this.buffer, (int)this.bufferSize)));
        Arrays.fill((Object[])this.buffer, (int)this.k, (int)this.buffer.length, null);
        this.bufferSize = this.k;
        this.threshold = this.buffer[this.k - 1];
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(this.buffer, (int)this.bufferSize)));
    }
}

