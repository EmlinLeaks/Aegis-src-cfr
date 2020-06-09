/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.oio;

import io.netty.channel.ThreadPerChannelEventLoopGroup;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class OioEventLoopGroup
extends ThreadPerChannelEventLoopGroup {
    public OioEventLoopGroup() {
        this((int)0);
    }

    public OioEventLoopGroup(int maxChannels) {
        this((int)maxChannels, (ThreadFactory)Executors.defaultThreadFactory());
    }

    public OioEventLoopGroup(int maxChannels, Executor executor) {
        super((int)maxChannels, (Executor)executor, (Object[])new Object[0]);
    }

    public OioEventLoopGroup(int maxChannels, ThreadFactory threadFactory) {
        super((int)maxChannels, (ThreadFactory)threadFactory, (Object[])new Object[0]);
    }
}

