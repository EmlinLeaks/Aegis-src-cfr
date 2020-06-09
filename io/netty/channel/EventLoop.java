/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.OrderedEventExecutor;

public interface EventLoop
extends OrderedEventExecutor,
EventLoopGroup {
    @Override
    public EventLoopGroup parent();
}

