/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Synchronized;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

@GwtCompatible(emulated=true)
public final class Queues {
    private Queues() {
    }

    @GwtIncompatible
    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity) {
        return new ArrayBlockingQueue<E>((int)capacity);
    }

    public static <E> ArrayDeque<E> newArrayDeque() {
        return new ArrayDeque<E>();
    }

    public static <E> ArrayDeque<E> newArrayDeque(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new ArrayDeque<E>(Collections2.cast(elements));
        }
        ArrayDeque<E> deque = new ArrayDeque<E>();
        Iterables.addAll(deque, elements);
        return deque;
    }

    @GwtIncompatible
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<E>();
    }

    @GwtIncompatible
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new ConcurrentLinkedQueue<E>(Collections2.cast(elements));
        }
        ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
        Iterables.addAll(queue, elements);
        return queue;
    }

    @GwtIncompatible
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque() {
        return new LinkedBlockingDeque<E>();
    }

    @GwtIncompatible
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
        return new LinkedBlockingDeque<E>((int)capacity);
    }

    @GwtIncompatible
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedBlockingDeque<E>(Collections2.cast(elements));
        }
        LinkedBlockingDeque<E> deque = new LinkedBlockingDeque<E>();
        Iterables.addAll(deque, elements);
        return deque;
    }

    @GwtIncompatible
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
        return new LinkedBlockingQueue<E>();
    }

    @GwtIncompatible
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int capacity) {
        return new LinkedBlockingQueue<E>((int)capacity);
    }

    @GwtIncompatible
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedBlockingQueue<E>(Collections2.cast(elements));
        }
        LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<E>();
        Iterables.addAll(queue, elements);
        return queue;
    }

    @GwtIncompatible
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
        return new PriorityBlockingQueue<E>();
    }

    @GwtIncompatible
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new PriorityBlockingQueue<E>(Collections2.cast(elements));
        }
        PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<E>();
        Iterables.addAll(queue, elements);
        return queue;
    }

    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue() {
        return new PriorityQueue<E>();
    }

    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new PriorityQueue<E>(Collections2.cast(elements));
        }
        PriorityQueue<E> queue = new PriorityQueue<E>();
        Iterables.addAll(queue, elements);
        return queue;
    }

    @GwtIncompatible
    public static <E> SynchronousQueue<E> newSynchronousQueue() {
        return new SynchronousQueue<E>();
    }

    @Beta
    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <E> int drain(BlockingQueue<E> q, Collection<? super E> buffer, int numElements, long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(buffer);
        long deadline = System.nanoTime() + unit.toNanos((long)timeout);
        int added = 0;
        while (added < numElements) {
            if ((added += q.drainTo(buffer, (int)(numElements - added))) >= numElements) continue;
            E e = q.poll((long)(deadline - System.nanoTime()), (TimeUnit)TimeUnit.NANOSECONDS);
            if (e == null) {
                return added;
            }
            buffer.add(e);
            ++added;
        }
        return added;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Beta
    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <E> int drainUninterruptibly(BlockingQueue<E> q, Collection<? super E> buffer, int numElements, long timeout, TimeUnit unit) {
        Preconditions.checkNotNull(buffer);
        long deadline = System.nanoTime() + unit.toNanos((long)timeout);
        int added = 0;
        boolean interrupted = false;
        try {
            while (added < numElements) {
                E e;
                if ((added += q.drainTo(buffer, (int)(numElements - added))) >= numElements) continue;
                do {
                    try {
                        e = q.poll((long)(deadline - System.nanoTime()), (TimeUnit)TimeUnit.NANOSECONDS);
                    }
                    catch (InterruptedException ex) {
                        interrupted = true;
                        continue;
                    }
                    break;
                } while (true);
                if (e == null) {
                    return added;
                }
                buffer.add(e);
                ++added;
            }
            return added;
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static <E> Queue<E> synchronizedQueue(Queue<E> queue) {
        return Synchronized.queue(queue, null);
    }

    public static <E> Deque<E> synchronizedDeque(Deque<E> deque) {
        return Synchronized.deque(deque, null);
    }
}

