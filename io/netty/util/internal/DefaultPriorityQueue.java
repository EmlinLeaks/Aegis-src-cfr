/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public final class DefaultPriorityQueue<T extends PriorityQueueNode>
extends AbstractQueue<T>
implements PriorityQueue<T> {
    private static final PriorityQueueNode[] EMPTY_ARRAY = new PriorityQueueNode[0];
    private final Comparator<T> comparator;
    private T[] queue;
    private int size;

    public DefaultPriorityQueue(Comparator<T> comparator, int initialSize) {
        this.comparator = ObjectUtil.checkNotNull(comparator, (String)"comparator");
        this.queue = initialSize != 0 ? new PriorityQueueNode[initialSize] : EMPTY_ARRAY;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        if (this.size != 0) return false;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof PriorityQueueNode)) {
            return false;
        }
        PriorityQueueNode node = (PriorityQueueNode)o;
        return this.contains((PriorityQueueNode)node, (int)node.priorityQueueIndex(this));
    }

    @Override
    public boolean containsTyped(T node) {
        return this.contains(node, (int)node.priorityQueueIndex(this));
    }

    @Override
    public void clear() {
        int i = 0;
        do {
            if (i >= this.size) {
                this.size = 0;
                return;
            }
            T node = this.queue[i];
            if (node != null) {
                node.priorityQueueIndex(this, (int)-1);
                this.queue[i] = null;
            }
            ++i;
        } while (true);
    }

    @Override
    public void clearIgnoringIndexes() {
        this.size = 0;
    }

    @Override
    public boolean offer(T e) {
        if (e.priorityQueueIndex(this) != -1) {
            throw new IllegalArgumentException((String)("e.priorityQueueIndex(): " + e.priorityQueueIndex(this) + " (expected: " + -1 + ") + e: " + e));
        }
        if (this.size >= this.queue.length) {
            this.queue = (PriorityQueueNode[])Arrays.copyOf(this.queue, (int)(this.queue.length + (this.queue.length < 64 ? this.queue.length + 2 : this.queue.length >>> 1)));
        }
        this.bubbleUp((int)this.size++, e);
        return true;
    }

    @Override
    public T poll() {
        if (this.size == 0) {
            return (T)null;
        }
        T result = this.queue[0];
        result.priorityQueueIndex(this, (int)-1);
        T last = this.queue[--this.size];
        this.queue[this.size] = null;
        if (this.size == 0) return (T)result;
        this.bubbleDown((int)0, last);
        return (T)result;
    }

    @Override
    public T peek() {
        T t;
        if (this.size == 0) {
            t = null;
            return (T)((T)t);
        }
        t = (T)this.queue[0];
        return (T)t;
    }

    @Override
    public boolean remove(Object o) {
        try {
            PriorityQueueNode node = (PriorityQueueNode)o;
            return this.removeTyped(node);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean removeTyped(T node) {
        int i = node.priorityQueueIndex(this);
        if (!this.contains(node, (int)i)) {
            return false;
        }
        node.priorityQueueIndex(this, (int)-1);
        if (--this.size == 0 || this.size == i) {
            this.queue[i] = null;
            return true;
        }
        T moved = this.queue[i] = this.queue[this.size];
        this.queue[this.size] = null;
        if (this.comparator.compare(node, moved) < 0) {
            this.bubbleDown((int)i, moved);
            return true;
        }
        this.bubbleUp((int)i, moved);
        return true;
    }

    @Override
    public void priorityChanged(T node) {
        int i = node.priorityQueueIndex(this);
        if (!this.contains(node, (int)i)) {
            return;
        }
        if (i == 0) {
            this.bubbleDown((int)i, node);
            return;
        }
        int iParent = i - 1 >>> 1;
        T parent = this.queue[iParent];
        if (this.comparator.compare(node, parent) < 0) {
            this.bubbleUp((int)i, node);
            return;
        }
        this.bubbleDown((int)i, node);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.queue, (int)this.size);
    }

    @Override
    public <X> X[] toArray(X[] a) {
        if (a.length < this.size) {
            return Arrays.copyOf(this.queue, (int)this.size, a.getClass());
        }
        System.arraycopy(this.queue, (int)0, a, (int)0, (int)this.size);
        if (a.length <= this.size) return a;
        a[this.size] = null;
        return a;
    }

    @Override
    public Iterator<T> iterator() {
        return new PriorityQueueIterator((DefaultPriorityQueue)this, null);
    }

    private boolean contains(PriorityQueueNode node, int i) {
        if (i < 0) return false;
        if (i >= this.size) return false;
        if (!node.equals(this.queue[i])) return false;
        return true;
    }

    private void bubbleDown(int k, T node) {
        int half = this.size >>> 1;
        while (k < half) {
            int iChild = (k << 1) + 1;
            T child = this.queue[iChild];
            int rightChild = iChild + 1;
            if (rightChild < this.size && this.comparator.compare(child, this.queue[rightChild]) > 0) {
                iChild = rightChild;
                child = this.queue[iChild];
            }
            if (this.comparator.compare(node, child) <= 0) break;
            this.queue[k] = child;
            child.priorityQueueIndex(this, (int)k);
            k = iChild;
        }
        this.queue[k] = node;
        node.priorityQueueIndex(this, (int)k);
    }

    private void bubbleUp(int k, T node) {
        int iParent;
        T parent;
        while (k > 0 && this.comparator.compare(node, parent = this.queue[iParent = k - 1 >>> 1]) < 0) {
            this.queue[k] = parent;
            parent.priorityQueueIndex(this, (int)k);
            k = iParent;
        }
        this.queue[k] = node;
        node.priorityQueueIndex(this, (int)k);
    }

    static /* synthetic */ int access$100(DefaultPriorityQueue x0) {
        return x0.size;
    }

    static /* synthetic */ PriorityQueueNode[] access$200(DefaultPriorityQueue x0) {
        return x0.queue;
    }
}

