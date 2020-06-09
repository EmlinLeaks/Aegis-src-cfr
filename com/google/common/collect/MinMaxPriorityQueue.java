/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;
import com.google.common.math.IntMath;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Beta
@GwtCompatible
public final class MinMaxPriorityQueue<E>
extends AbstractQueue<E> {
    private final MinMaxPriorityQueue<E> minHeap;
    private final MinMaxPriorityQueue<E> maxHeap;
    @VisibleForTesting
    final int maximumSize;
    private Object[] queue;
    private int size;
    private int modCount;
    private static final int EVEN_POWERS_OF_TWO = 1431655765;
    private static final int ODD_POWERS_OF_TWO = -1431655766;
    private static final int DEFAULT_CAPACITY = 11;

    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create() {
        return new Builder<B>(Ordering.<C>natural(), null).create();
    }

    public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create(Iterable<? extends E> initialContents) {
        return new Builder<B>(Ordering.<C>natural(), null).create(initialContents);
    }

    public static <B> Builder<B> orderedBy(Comparator<B> comparator) {
        return new Builder<B>(comparator, null);
    }

    public static Builder<Comparable> expectedSize(int expectedSize) {
        return new Builder<B>(Ordering.<C>natural(), null).expectedSize((int)expectedSize);
    }

    public static Builder<Comparable> maximumSize(int maximumSize) {
        return new Builder<B>(Ordering.<C>natural(), null).maximumSize((int)maximumSize);
    }

    private MinMaxPriorityQueue(Builder<? super E> builder, int queueSize) {
        Ordering ordering = builder.ordering();
        this.minHeap = new Heap((MinMaxPriorityQueue)this, ordering);
        this.maxHeap = new Heap((MinMaxPriorityQueue)this, ordering.reverse());
        ((Heap)this.minHeap).otherHeap = this.maxHeap;
        ((Heap)this.maxHeap).otherHeap = this.minHeap;
        this.maximumSize = builder.maximumSize;
        this.queue = new Object[queueSize];
    }

    @Override
    public int size() {
        return this.size;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean add(E element) {
        this.offer(element);
        return true;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean addAll(Collection<? extends E> newElements) {
        boolean modified = false;
        Iterator<E> i$ = newElements.iterator();
        while (i$.hasNext()) {
            E element = i$.next();
            this.offer(element);
            modified = true;
        }
        return modified;
    }

    @CanIgnoreReturnValue
    @Override
    public boolean offer(E element) {
        Preconditions.checkNotNull(element);
        ++this.modCount;
        int insertIndex = this.size++;
        this.growIfNeeded();
        this.heapForIndex((int)insertIndex).bubbleUp((int)insertIndex, element);
        if (this.size <= this.maximumSize) return true;
        if (this.pollLast() != element) return true;
        return false;
    }

    @CanIgnoreReturnValue
    @Override
    public E poll() {
        E e;
        if (this.isEmpty()) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.removeAndGet((int)0);
        return (E)e;
    }

    E elementData(int index) {
        return (E)this.queue[index];
    }

    @Override
    public E peek() {
        E e;
        if (this.isEmpty()) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.elementData((int)0);
        return (E)e;
    }

    private int getMaxElementIndex() {
        switch (this.size) {
            case 1: {
                return 0;
            }
            case 2: {
                return 1;
            }
        }
        if (((Heap)((Object)this.maxHeap)).compareElements((int)1, (int)2) > 0) return 2;
        return 1;
    }

    @CanIgnoreReturnValue
    public E pollFirst() {
        return (E)this.poll();
    }

    @CanIgnoreReturnValue
    public E removeFirst() {
        return (E)this.remove();
    }

    public E peekFirst() {
        return (E)this.peek();
    }

    @CanIgnoreReturnValue
    public E pollLast() {
        E e;
        if (this.isEmpty()) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.removeAndGet((int)this.getMaxElementIndex());
        return (E)e;
    }

    @CanIgnoreReturnValue
    public E removeLast() {
        if (!this.isEmpty()) return (E)this.removeAndGet((int)this.getMaxElementIndex());
        throw new NoSuchElementException();
    }

    public E peekLast() {
        E e;
        if (this.isEmpty()) {
            e = null;
            return (E)((E)e);
        }
        e = (E)this.elementData((int)this.getMaxElementIndex());
        return (E)e;
    }

    @VisibleForTesting
    @CanIgnoreReturnValue
    MoveDesc<E> removeAt(int index) {
        Preconditions.checkPositionIndex((int)index, (int)this.size);
        ++this.modCount;
        --this.size;
        if (this.size == index) {
            this.queue[this.size] = null;
            return null;
        }
        E actualLastElement = this.elementData((int)this.size);
        int lastElementAt = this.heapForIndex((int)this.size).getCorrectLastElement(actualLastElement);
        E toTrickle = this.elementData((int)this.size);
        this.queue[this.size] = null;
        MoveDesc<E> changes = this.fillHole((int)index, toTrickle);
        if (lastElementAt >= index) return changes;
        if (changes != null) return new MoveDesc<E>(actualLastElement, changes.replaced);
        return new MoveDesc<E>(actualLastElement, toTrickle);
    }

    private MoveDesc<E> fillHole(int index, E toTrickle) {
        int vacated;
        Heap heap = this.heapForIndex((int)index);
        int bubbledTo = heap.bubbleUpAlternatingLevels((int)(vacated = heap.fillHoleAt((int)index)), toTrickle);
        if (bubbledTo == vacated) {
            return heap.tryCrossOverAndBubbleUp((int)index, (int)vacated, toTrickle);
        }
        if (bubbledTo >= index) return null;
        MoveDesc<E> moveDesc = new MoveDesc<E>(toTrickle, this.elementData((int)index));
        return moveDesc;
    }

    private E removeAndGet(int index) {
        E value = this.elementData((int)index);
        this.removeAt((int)index);
        return (E)value;
    }

    private MinMaxPriorityQueue<E> heapForIndex(int i) {
        MinMaxPriorityQueue<E> minMaxPriorityQueue;
        if (MinMaxPriorityQueue.isEvenLevel((int)i)) {
            minMaxPriorityQueue = this.minHeap;
            return minMaxPriorityQueue;
        }
        minMaxPriorityQueue = this.maxHeap;
        return minMaxPriorityQueue;
    }

    @VisibleForTesting
    static boolean isEvenLevel(int index) {
        int oneBased = ~(~(index + 1));
        Preconditions.checkState((boolean)(oneBased > 0), (Object)"negative index");
        if ((oneBased & 1431655765) <= (oneBased & -1431655766)) return false;
        return true;
    }

    @VisibleForTesting
    boolean isIntact() {
        int i = 1;
        while (i < this.size) {
            if (!((Heap)this.heapForIndex((int)i)).verifyIndex((int)((int)i))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return new QueueIterator((MinMaxPriorityQueue)this, null);
    }

    @Override
    public void clear() {
        int i = 0;
        do {
            if (i >= this.size) {
                this.size = 0;
                return;
            }
            this.queue[i] = null;
            ++i;
        } while (true);
    }

    @Override
    public Object[] toArray() {
        Object[] copyTo = new Object[this.size];
        System.arraycopy((Object)this.queue, (int)0, (Object)copyTo, (int)0, (int)this.size);
        return copyTo;
    }

    public Comparator<? super E> comparator() {
        return ((Heap)this.minHeap).ordering;
    }

    @VisibleForTesting
    int capacity() {
        return this.queue.length;
    }

    @VisibleForTesting
    static int initialQueueSize(int configuredExpectedSize, int maximumSize, Iterable<?> initialContents) {
        int result = configuredExpectedSize == -1 ? 11 : configuredExpectedSize;
        if (!(initialContents instanceof Collection)) return MinMaxPriorityQueue.capAtMaximumSize((int)result, (int)maximumSize);
        int initialSize = ((Collection)initialContents).size();
        result = Math.max((int)result, (int)initialSize);
        return MinMaxPriorityQueue.capAtMaximumSize((int)result, (int)maximumSize);
    }

    private void growIfNeeded() {
        if (this.size <= this.queue.length) return;
        int newCapacity = this.calculateNewCapacity();
        Object[] newQueue = new Object[newCapacity];
        System.arraycopy((Object)this.queue, (int)0, (Object)newQueue, (int)0, (int)this.queue.length);
        this.queue = newQueue;
    }

    private int calculateNewCapacity() {
        int oldCapacity = this.queue.length;
        int newCapacity = oldCapacity < 64 ? (oldCapacity + 1) * 2 : IntMath.checkedMultiply((int)(oldCapacity / 2), (int)3);
        return MinMaxPriorityQueue.capAtMaximumSize((int)newCapacity, (int)this.maximumSize);
    }

    private static int capAtMaximumSize(int queueSize, int maximumSize) {
        return Math.min((int)(queueSize - 1), (int)maximumSize) + 1;
    }

    static /* synthetic */ Object[] access$500(MinMaxPriorityQueue x0) {
        return x0.queue;
    }

    static /* synthetic */ int access$600(MinMaxPriorityQueue x0) {
        return x0.size;
    }

    static /* synthetic */ int access$700(MinMaxPriorityQueue x0) {
        return x0.modCount;
    }
}

