/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;

public interface MessagePassingQueue<T> {
    public static final int UNBOUNDED_CAPACITY = -1;

    public boolean offer(T var1);

    public T poll();

    public T peek();

    public int size();

    public void clear();

    public boolean isEmpty();

    public int capacity();

    public boolean relaxedOffer(T var1);

    public T relaxedPoll();

    public T relaxedPeek();

    public int drain(Consumer<T> var1);

    public int fill(Supplier<T> var1);

    public int drain(Consumer<T> var1, int var2);

    public int fill(Supplier<T> var1, int var2);

    public void drain(Consumer<T> var1, WaitStrategy var2, ExitCondition var3);

    public void fill(Supplier<T> var1, WaitStrategy var2, ExitCondition var3);
}

