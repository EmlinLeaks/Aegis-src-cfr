/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import java.util.Queue;

public interface EventLoopTaskQueueFactory {
    public Queue<Runnable> newTaskQueue(int var1);
}

