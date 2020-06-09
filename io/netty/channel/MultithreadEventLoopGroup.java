/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class MultithreadEventLoopGroup
extends MultithreadEventExecutorGroup
implements EventLoopGroup {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MultithreadEventLoopGroup.class);
    private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max((int)1, (int)SystemPropertyUtil.getInt((String)"io.netty.eventLoopThreads", (int)(NettyRuntime.availableProcessors() * 2)));

    protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object ... args) {
        super((int)(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads), (Executor)executor, (Object[])args);
    }

    protected MultithreadEventLoopGroup(int nThreads, ThreadFactory threadFactory, Object ... args) {
        super((int)(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads), (ThreadFactory)threadFactory, (Object[])args);
    }

    protected MultithreadEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, Object ... args) {
        super((int)(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads), (Executor)executor, (EventExecutorChooserFactory)chooserFactory, (Object[])args);
    }

    @Override
    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(this.getClass(), (int)10);
    }

    @Override
    public EventLoop next() {
        return (EventLoop)super.next();
    }

    @Override
    protected abstract EventLoop newChild(Executor var1, Object ... var2) throws Exception;

    @Override
    public ChannelFuture register(Channel channel) {
        return this.next().register((Channel)channel);
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return this.next().register((ChannelPromise)promise);
    }

    @Deprecated
    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return this.next().register((Channel)channel, (ChannelPromise)promise);
    }

    static {
        if (!logger.isDebugEnabled()) return;
        logger.debug((String)"-Dio.netty.eventLoopThreads: {}", (Object)Integer.valueOf((int)DEFAULT_EVENT_LOOP_THREADS));
    }
}

