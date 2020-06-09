/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.ThreadPerChannelEventLoop;
import io.netty.channel.ThreadPerChannelEventLoopGroup;
import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import io.netty.util.internal.ThrowableUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ThreadPerChannelEventLoopGroup
extends AbstractEventExecutorGroup
implements EventLoopGroup {
    private final Object[] childArgs;
    private final int maxChannels;
    final Executor executor;
    final Set<EventLoop> activeChildren = Collections.newSetFromMap(PlatformDependent.<K, V>newConcurrentHashMap());
    final Queue<EventLoop> idleChildren = new ConcurrentLinkedQueue<EventLoop>();
    private final ChannelException tooManyChannels;
    private volatile boolean shuttingDown;
    private final Promise<?> terminationFuture = new DefaultPromise<?>((EventExecutor)GlobalEventExecutor.INSTANCE);
    private final FutureListener<Object> childTerminationListener = new FutureListener<Object>((ThreadPerChannelEventLoopGroup)this){
        final /* synthetic */ ThreadPerChannelEventLoopGroup this$0;
        {
            this.this$0 = this$0;
        }

        public void operationComplete(Future<Object> future) throws Exception {
            if (!this.this$0.isTerminated()) return;
            ThreadPerChannelEventLoopGroup.access$000((ThreadPerChannelEventLoopGroup)this.this$0).trySuccess(null);
        }
    };

    protected ThreadPerChannelEventLoopGroup() {
        this((int)0);
    }

    protected ThreadPerChannelEventLoopGroup(int maxChannels) {
        this((int)maxChannels, (ThreadFactory)Executors.defaultThreadFactory(), (Object[])new Object[0]);
    }

    protected ThreadPerChannelEventLoopGroup(int maxChannels, ThreadFactory threadFactory, Object ... args) {
        this((int)maxChannels, (Executor)new ThreadPerTaskExecutor((ThreadFactory)threadFactory), (Object[])args);
    }

    protected ThreadPerChannelEventLoopGroup(int maxChannels, Executor executor, Object ... args) {
        if (maxChannels < 0) {
            throw new IllegalArgumentException((String)String.format((String)"maxChannels: %d (expected: >= 0)", (Object[])new Object[]{Integer.valueOf((int)maxChannels)}));
        }
        if (executor == null) {
            throw new NullPointerException((String)"executor");
        }
        this.childArgs = args == null ? EmptyArrays.EMPTY_OBJECTS : (Object[])args.clone();
        this.maxChannels = maxChannels;
        this.executor = executor;
        this.tooManyChannels = ThrowableUtil.unknownStackTrace(ChannelException.newStatic((String)("too many channels (max: " + maxChannels + ')'), null), ThreadPerChannelEventLoopGroup.class, (String)"nextChild()");
    }

    protected EventLoop newChild(Object ... args) throws Exception {
        return new ThreadPerChannelEventLoop((ThreadPerChannelEventLoopGroup)this);
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return new ReadOnlyIterator<EventExecutor>(this.activeChildren.iterator());
    }

    @Override
    public EventLoop next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        this.shuttingDown = true;
        for (EventLoop l : this.activeChildren) {
            l.shutdownGracefully((long)quietPeriod, (long)timeout, (TimeUnit)unit);
        }
        Iterator<EventLoop> iterator = this.idleChildren.iterator();
        do {
            EventLoop l;
            if (!iterator.hasNext()) {
                if (!this.isTerminated()) return this.terminationFuture();
                this.terminationFuture.trySuccess(null);
                return this.terminationFuture();
            }
            l = iterator.next();
            l.shutdownGracefully((long)quietPeriod, (long)timeout, (TimeUnit)unit);
        } while (true);
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Deprecated
    @Override
    public void shutdown() {
        this.shuttingDown = true;
        for (EventLoop l : this.activeChildren) {
            l.shutdown();
        }
        Iterator<EventLoop> iterator = this.idleChildren.iterator();
        do {
            EventLoop l;
            if (!iterator.hasNext()) {
                if (!this.isTerminated()) return;
                this.terminationFuture.trySuccess(null);
                return;
            }
            l = iterator.next();
            l.shutdown();
        } while (true);
    }

    @Override
    public boolean isShuttingDown() {
        EventLoop l2;
        for (EventLoop l2 : this.activeChildren) {
            if (l2.isShuttingDown()) continue;
            return false;
        }
        Iterator<EventLoop> iterator = this.idleChildren.iterator();
        do {
            if (!iterator.hasNext()) return true;
        } while ((l2 = iterator.next()).isShuttingDown());
        return false;
    }

    @Override
    public boolean isShutdown() {
        EventLoop l2;
        for (EventLoop l2 : this.activeChildren) {
            if (l2.isShutdown()) continue;
            return false;
        }
        Iterator<EventLoop> iterator = this.idleChildren.iterator();
        do {
            if (!iterator.hasNext()) return true;
        } while ((l2 = iterator.next()).isShutdown());
        return false;
    }

    @Override
    public boolean isTerminated() {
        EventLoop l2;
        for (EventLoop l2 : this.activeChildren) {
            if (l2.isTerminated()) continue;
            return false;
        }
        Iterator<EventLoop> iterator = this.idleChildren.iterator();
        do {
            if (!iterator.hasNext()) return true;
        } while ((l2 = iterator.next()).isTerminated());
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long timeLeft;
        EventLoop l;
        long deadline = System.nanoTime() + unit.toNanos((long)timeout);
        Iterator<EventLoop> iterator = this.activeChildren.iterator();
        do {
            if (!iterator.hasNext()) break;
            l = iterator.next();
            do {
                if ((timeLeft = deadline - System.nanoTime()) > 0L) continue;
                return this.isTerminated();
            } while (!l.awaitTermination((long)timeLeft, (TimeUnit)TimeUnit.NANOSECONDS));
        } while (true);
        iterator = this.idleChildren.iterator();
        while (iterator.hasNext()) {
            l = iterator.next();
            do {
                if ((timeLeft = deadline - System.nanoTime()) > 0L) continue;
                return this.isTerminated();
            } while (!l.awaitTermination((long)timeLeft, (TimeUnit)TimeUnit.NANOSECONDS));
        }
        return this.isTerminated();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        if (channel == null) {
            throw new NullPointerException((String)"channel");
        }
        try {
            EventLoop l = this.nextChild();
            return l.register((ChannelPromise)new DefaultChannelPromise((Channel)channel, (EventExecutor)l));
        }
        catch (Throwable t) {
            return new FailedChannelFuture((Channel)channel, (EventExecutor)GlobalEventExecutor.INSTANCE, (Throwable)t);
        }
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        try {
            return this.nextChild().register((ChannelPromise)promise);
        }
        catch (Throwable t) {
            promise.setFailure((Throwable)t);
            return promise;
        }
    }

    @Deprecated
    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        if (channel == null) {
            throw new NullPointerException((String)"channel");
        }
        try {
            return this.nextChild().register((Channel)channel, (ChannelPromise)promise);
        }
        catch (Throwable t) {
            promise.setFailure((Throwable)t);
            return promise;
        }
    }

    private EventLoop nextChild() throws Exception {
        if (this.shuttingDown) {
            throw new RejectedExecutionException((String)"shutting down");
        }
        EventLoop loop = this.idleChildren.poll();
        if (loop == null) {
            if (this.maxChannels > 0 && this.activeChildren.size() >= this.maxChannels) {
                throw this.tooManyChannels;
            }
            loop = this.newChild((Object[])this.childArgs);
            loop.terminationFuture().addListener(this.childTerminationListener);
        }
        this.activeChildren.add((EventLoop)loop);
        return loop;
    }

    static /* synthetic */ Promise access$000(ThreadPerChannelEventLoopGroup x0) {
        return x0.terminationFuture;
    }
}

