/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedChannelPool
extends SimpleChannelPool {
    private final EventExecutor executor;
    private final long acquireTimeoutNanos;
    private final Runnable timeoutTask;
    private final Queue<AcquireTask> pendingAcquireQueue = new ArrayDeque<AcquireTask>();
    private final int maxConnections;
    private final int maxPendingAcquires;
    private final AtomicInteger acquiredChannelCount = new AtomicInteger();
    private int pendingAcquireCount;
    private boolean closed;

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections) {
        this((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (int)maxConnections, (int)Integer.MAX_VALUE);
    }

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, int maxConnections, int maxPendingAcquires) {
        this((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (ChannelHealthChecker)ChannelHealthChecker.ACTIVE, null, (long)-1L, (int)maxConnections, (int)maxPendingAcquires);
    }

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires) {
        this((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (ChannelHealthChecker)healthCheck, (AcquireTimeoutAction)action, (long)acquireTimeoutMillis, (int)maxConnections, (int)maxPendingAcquires, (boolean)true);
    }

    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck) {
        this((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (ChannelHealthChecker)healthCheck, (AcquireTimeoutAction)action, (long)acquireTimeoutMillis, (int)maxConnections, (int)maxPendingAcquires, (boolean)releaseHealthCheck, (boolean)true);
    }

    /*
     * Unable to fully structure code
     */
    public FixedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, AcquireTimeoutAction action, long acquireTimeoutMillis, int maxConnections, int maxPendingAcquires, boolean releaseHealthCheck, boolean lastRecentUsed) {
        super((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (ChannelHealthChecker)healthCheck, (boolean)releaseHealthCheck, (boolean)lastRecentUsed);
        if (maxConnections < 1) {
            throw new IllegalArgumentException((String)("maxConnections: " + maxConnections + " (expected: >= 1)"));
        }
        if (maxPendingAcquires < 1) {
            throw new IllegalArgumentException((String)("maxPendingAcquires: " + maxPendingAcquires + " (expected: >= 1)"));
        }
        if (action == null && acquireTimeoutMillis == -1L) {
            this.timeoutTask = null;
            this.acquireTimeoutNanos = -1L;
        } else {
            if (action == null && acquireTimeoutMillis != -1L) {
                throw new NullPointerException((String)"action");
            }
            if (action != null && acquireTimeoutMillis < 0L) {
                throw new IllegalArgumentException((String)("acquireTimeoutMillis: " + acquireTimeoutMillis + " (expected: >= 0)"));
            }
            this.acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos((long)acquireTimeoutMillis);
            switch (7.$SwitchMap$io$netty$channel$pool$FixedChannelPool$AcquireTimeoutAction[action.ordinal()]) {
                case 1: {
                    this.timeoutTask = new TimeoutTask((FixedChannelPool)this){
                        final /* synthetic */ FixedChannelPool this$0;
                        {
                            this.this$0 = this$0;
                            super((FixedChannelPool)this$0, null);
                        }

                        public void onTimeout(AcquireTask task) {
                            task.promise.setFailure((Throwable)new java.util.concurrent.TimeoutException(this, (String)"Acquire operation took longer then configured maximum time"){
                                final /* synthetic */ 1 this$1;
                                {
                                    this.this$1 = this$1;
                                    super((String)x0);
                                }

                                public synchronized Throwable fillInStackTrace() {
                                    return this;
                                }
                            });
                        }
                    };
                    ** break;
                }
                case 2: {
                    this.timeoutTask = new TimeoutTask((FixedChannelPool)this){
                        final /* synthetic */ FixedChannelPool this$0;
                        {
                            this.this$0 = this$0;
                            super((FixedChannelPool)this$0, null);
                        }

                        public void onTimeout(AcquireTask task) {
                            task.acquired();
                            FixedChannelPool.access$101((FixedChannelPool)this.this$0, task.promise);
                        }
                    };
                    ** break;
                }
            }
            throw new Error();
        }
lbl25: // 3 sources:
        this.executor = bootstrap.config().group().next();
        this.maxConnections = maxConnections;
        this.maxPendingAcquires = maxPendingAcquires;
    }

    public int acquiredChannelCount() {
        return this.acquiredChannelCount.get();
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        try {
            if (this.executor.inEventLoop()) {
                this.acquire0(promise);
                return promise;
            }
            this.executor.execute((Runnable)new Runnable((FixedChannelPool)this, promise){
                final /* synthetic */ Promise val$promise;
                final /* synthetic */ FixedChannelPool this$0;
                {
                    this.this$0 = this$0;
                    this.val$promise = promise;
                }

                public void run() {
                    FixedChannelPool.access$200((FixedChannelPool)this.this$0, (Promise)this.val$promise);
                }
            });
            return promise;
        }
        catch (Throwable cause) {
            promise.setFailure((Throwable)cause);
        }
        return promise;
    }

    private void acquire0(Promise<Channel> promise) {
        assert (this.executor.inEventLoop());
        if (this.closed) {
            promise.setFailure((Throwable)new IllegalStateException((String)"FixedChannelPool was closed"));
            return;
        }
        if (this.acquiredChannelCount.get() < this.maxConnections) {
            assert (this.acquiredChannelCount.get() >= 0);
            Promise<Channel> p = this.executor.newPromise();
            AcquireListener l = new AcquireListener((FixedChannelPool)this, promise);
            l.acquired();
            p.addListener(l);
            super.acquire(p);
            return;
        }
        if (this.pendingAcquireCount >= this.maxPendingAcquires) {
            this.tooManyOutstanding(promise);
        } else {
            AcquireTask task = new AcquireTask((FixedChannelPool)this, promise);
            if (this.pendingAcquireQueue.offer((AcquireTask)task)) {
                ++this.pendingAcquireCount;
                if (this.timeoutTask != null) {
                    task.timeoutFuture = this.executor.schedule((Runnable)this.timeoutTask, (long)this.acquireTimeoutNanos, (TimeUnit)TimeUnit.NANOSECONDS);
                }
            } else {
                this.tooManyOutstanding(promise);
            }
        }
        if ($assertionsDisabled) return;
        if (this.pendingAcquireCount > 0) return;
        throw new AssertionError();
    }

    private void tooManyOutstanding(Promise<?> promise) {
        promise.setFailure((Throwable)new IllegalStateException((String)"Too many outstanding acquire operations"));
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        ObjectUtil.checkNotNull(promise, (String)"promise");
        Promise<Void> p = this.executor.newPromise();
        super.release((Channel)channel, p.addListener(new FutureListener<Void>((FixedChannelPool)this, (Channel)channel, promise){
            static final /* synthetic */ boolean $assertionsDisabled;
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ FixedChannelPool this$0;
            {
                this.this$0 = this$0;
                this.val$channel = channel;
                this.val$promise = promise;
            }

            public void operationComplete(Future<Void> future) throws java.lang.Exception {
                if (!$assertionsDisabled && !FixedChannelPool.access$300((FixedChannelPool)this.this$0).inEventLoop()) {
                    throw new AssertionError();
                }
                if (FixedChannelPool.access$400((FixedChannelPool)this.this$0)) {
                    this.val$channel.close();
                    this.val$promise.setFailure((Throwable)new IllegalStateException((String)"FixedChannelPool was closed"));
                    return;
                }
                if (future.isSuccess()) {
                    FixedChannelPool.access$500((FixedChannelPool)this.this$0);
                    this.val$promise.setSuccess(null);
                    return;
                }
                Throwable cause = future.cause();
                if (!(cause instanceof IllegalArgumentException)) {
                    FixedChannelPool.access$500((FixedChannelPool)this.this$0);
                }
                this.val$promise.setFailure((Throwable)future.cause());
            }

            static {
                $assertionsDisabled = !FixedChannelPool.class.desiredAssertionStatus();
            }
        }));
        return promise;
    }

    private void decrementAndRunTaskQueue() {
        int currentCount = this.acquiredChannelCount.decrementAndGet();
        assert (currentCount >= 0);
        this.runTaskQueue();
    }

    private void runTaskQueue() {
        AcquireTask task;
        while (this.acquiredChannelCount.get() < this.maxConnections && (task = this.pendingAcquireQueue.poll()) != null) {
            ScheduledFuture<?> timeoutFuture = task.timeoutFuture;
            if (timeoutFuture != null) {
                timeoutFuture.cancel((boolean)false);
            }
            --this.pendingAcquireCount;
            task.acquired();
            super.acquire(task.promise);
        }
        assert (this.pendingAcquireCount >= 0);
        if ($assertionsDisabled) return;
        if (this.acquiredChannelCount.get() >= 0) return;
        throw new AssertionError();
    }

    @Override
    public void close() {
        try {
            this.closeAsync().await();
            return;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException((Throwable)e);
        }
    }

    public Future<Void> closeAsync() {
        if (this.executor.inEventLoop()) {
            return this.close0();
        }
        Promise<Void> closeComplete = this.executor.newPromise();
        this.executor.execute((Runnable)new Runnable((FixedChannelPool)this, closeComplete){
            final /* synthetic */ Promise val$closeComplete;
            final /* synthetic */ FixedChannelPool this$0;
            {
                this.this$0 = this$0;
                this.val$closeComplete = promise;
            }

            public void run() {
                FixedChannelPool.access$1100((FixedChannelPool)this.this$0).addListener(new FutureListener<Void>(this){
                    final /* synthetic */ 5 this$1;
                    {
                        this.this$1 = this$1;
                    }

                    public void operationComplete(Future<Void> f) throws java.lang.Exception {
                        if (f.isSuccess()) {
                            this.this$1.val$closeComplete.setSuccess(null);
                            return;
                        }
                        this.this$1.val$closeComplete.setFailure((Throwable)f.cause());
                    }
                });
            }
        });
        return closeComplete;
    }

    private Future<Void> close0() {
        assert (this.executor.inEventLoop());
        if (this.closed) return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
        this.closed = true;
        do {
            AcquireTask task;
            if ((task = this.pendingAcquireQueue.poll()) == null) {
                this.acquiredChannelCount.set((int)0);
                this.pendingAcquireCount = 0;
                return GlobalEventExecutor.INSTANCE.submit((Callable)new Callable<Void>((FixedChannelPool)this){
                    final /* synthetic */ FixedChannelPool this$0;
                    {
                        this.this$0 = this$0;
                    }

                    public Void call() throws java.lang.Exception {
                        FixedChannelPool.access$1201((FixedChannelPool)this.this$0);
                        return null;
                    }
                });
            }
            ScheduledFuture<?> f = task.timeoutFuture;
            if (f != null) {
                f.cancel((boolean)false);
            }
            task.promise.setFailure((Throwable)new ClosedChannelException());
        } while (true);
    }

    static /* synthetic */ Future access$101(FixedChannelPool x0, Promise x1) {
        return super.acquire((Promise<Channel>)x1);
    }

    static /* synthetic */ void access$200(FixedChannelPool x0, Promise x1) {
        x0.acquire0((Promise<Channel>)x1);
    }

    static /* synthetic */ EventExecutor access$300(FixedChannelPool x0) {
        return x0.executor;
    }

    static /* synthetic */ boolean access$400(FixedChannelPool x0) {
        return x0.closed;
    }

    static /* synthetic */ void access$500(FixedChannelPool x0) {
        x0.decrementAndRunTaskQueue();
    }

    static /* synthetic */ long access$600(FixedChannelPool x0) {
        return x0.acquireTimeoutNanos;
    }

    static /* synthetic */ Queue access$700(FixedChannelPool x0) {
        return x0.pendingAcquireQueue;
    }

    static /* synthetic */ int access$806(FixedChannelPool x0) {
        return --x0.pendingAcquireCount;
    }

    static /* synthetic */ void access$900(FixedChannelPool x0) {
        x0.runTaskQueue();
    }

    static /* synthetic */ AtomicInteger access$1000(FixedChannelPool x0) {
        return x0.acquiredChannelCount;
    }

    static /* synthetic */ Future access$1100(FixedChannelPool x0) {
        return x0.close0();
    }

    static /* synthetic */ void access$1201(FixedChannelPool x0) {
        super.close();
    }
}

