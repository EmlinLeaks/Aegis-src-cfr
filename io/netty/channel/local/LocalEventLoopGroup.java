/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.local;

import io.netty.channel.DefaultEventLoopGroup;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class LocalEventLoopGroup
extends DefaultEventLoopGroup {
    public LocalEventLoopGroup() {
    }

    public LocalEventLoopGroup(int nThreads) {
        super((int)nThreads);
    }

    public LocalEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        super((int)nThreads, (ThreadFactory)threadFactory);
    }
}

