/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Deque;

public class SimpleChannelPool
implements ChannelPool {
    private final AttributeKey<SimpleChannelPool> poolKey = AttributeKey.newInstance((String)("channelPool." + System.identityHashCode((Object)this)));
    private final Deque<Channel> deque = PlatformDependent.newConcurrentDeque();
    private final ChannelPoolHandler handler;
    private final ChannelHealthChecker healthCheck;
    private final Bootstrap bootstrap;
    private final boolean releaseHealthCheck;
    private final boolean lastRecentUsed;

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
        this((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (ChannelHealthChecker)ChannelHealthChecker.ACTIVE);
    }

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck) {
        this((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (ChannelHealthChecker)healthCheck, (boolean)true);
    }

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck) {
        this((Bootstrap)bootstrap, (ChannelPoolHandler)handler, (ChannelHealthChecker)healthCheck, (boolean)releaseHealthCheck, (boolean)true);
    }

    public SimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, ChannelHealthChecker healthCheck, boolean releaseHealthCheck, boolean lastRecentUsed) {
        this.handler = ObjectUtil.checkNotNull(handler, (String)"handler");
        this.healthCheck = ObjectUtil.checkNotNull(healthCheck, (String)"healthCheck");
        this.releaseHealthCheck = releaseHealthCheck;
        this.bootstrap = ObjectUtil.checkNotNull(bootstrap, (String)"bootstrap").clone();
        this.bootstrap.handler((ChannelHandler)new ChannelInitializer<Channel>((SimpleChannelPool)this, (ChannelPoolHandler)handler){
            static final /* synthetic */ boolean $assertionsDisabled;
            final /* synthetic */ ChannelPoolHandler val$handler;
            final /* synthetic */ SimpleChannelPool this$0;
            {
                this.this$0 = this$0;
                this.val$handler = channelPoolHandler;
            }

            protected void initChannel(Channel ch) throws Exception {
                if (!$assertionsDisabled && !ch.eventLoop().inEventLoop()) {
                    throw new java.lang.AssertionError();
                }
                this.val$handler.channelCreated((Channel)ch);
            }

            static {
                $assertionsDisabled = !SimpleChannelPool.class.desiredAssertionStatus();
            }
        });
        this.lastRecentUsed = lastRecentUsed;
    }

    protected Bootstrap bootstrap() {
        return this.bootstrap;
    }

    protected ChannelPoolHandler handler() {
        return this.handler;
    }

    protected ChannelHealthChecker healthChecker() {
        return this.healthCheck;
    }

    protected boolean releaseHealthCheck() {
        return this.releaseHealthCheck;
    }

    @Override
    public final Future<Channel> acquire() {
        return this.acquire(this.bootstrap.config().group().next().newPromise());
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        ObjectUtil.checkNotNull(promise, (String)"promise");
        return this.acquireHealthyFromPoolOrNew(promise);
    }

    private Future<Channel> acquireHealthyFromPoolOrNew(Promise<Channel> promise) {
        try {
            Channel ch = this.pollChannel();
            if (ch == null) {
                Bootstrap bs = this.bootstrap.clone();
                bs.attr(this.poolKey, this);
                ChannelFuture f = this.connectChannel((Bootstrap)bs);
                if (f.isDone()) {
                    this.notifyConnect((ChannelFuture)f, promise);
                    return promise;
                }
                f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((SimpleChannelPool)this, promise){
                    final /* synthetic */ Promise val$promise;
                    final /* synthetic */ SimpleChannelPool this$0;
                    {
                        this.this$0 = this$0;
                        this.val$promise = promise;
                    }

                    public void operationComplete(ChannelFuture future) throws Exception {
                        SimpleChannelPool.access$000((SimpleChannelPool)this.this$0, (ChannelFuture)future, (Promise)this.val$promise);
                    }
                });
                return promise;
            }
            EventLoop loop = ch.eventLoop();
            if (loop.inEventLoop()) {
                this.doHealthCheck((Channel)ch, promise);
                return promise;
            }
            loop.execute((Runnable)new Runnable((SimpleChannelPool)this, (Channel)ch, promise){
                final /* synthetic */ Channel val$ch;
                final /* synthetic */ Promise val$promise;
                final /* synthetic */ SimpleChannelPool this$0;
                {
                    this.this$0 = this$0;
                    this.val$ch = channel;
                    this.val$promise = promise;
                }

                public void run() {
                    SimpleChannelPool.access$100((SimpleChannelPool)this.this$0, (Channel)this.val$ch, (Promise)this.val$promise);
                }
            });
            return promise;
        }
        catch (Throwable cause) {
            promise.tryFailure((Throwable)cause);
        }
        return promise;
    }

    private void notifyConnect(ChannelFuture future, Promise<Channel> promise) throws Exception {
        if (future.isSuccess()) {
            Channel channel = future.channel();
            this.handler.channelAcquired((Channel)channel);
            if (promise.trySuccess((Channel)channel)) return;
            this.release((Channel)channel);
            return;
        }
        promise.tryFailure((Throwable)future.cause());
    }

    private void doHealthCheck(Channel ch, Promise<Channel> promise) {
        assert (ch.eventLoop().inEventLoop());
        Future<Boolean> f = this.healthCheck.isHealthy((Channel)ch);
        if (f.isDone()) {
            this.notifyHealthCheck(f, (Channel)ch, promise);
            return;
        }
        f.addListener((GenericFutureListener<Future<Boolean>>)new FutureListener<Boolean>((SimpleChannelPool)this, (Channel)ch, promise){
            final /* synthetic */ Channel val$ch;
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ SimpleChannelPool this$0;
            {
                this.this$0 = this$0;
                this.val$ch = channel;
                this.val$promise = promise;
            }

            public void operationComplete(Future<Boolean> future) throws Exception {
                SimpleChannelPool.access$200((SimpleChannelPool)this.this$0, future, (Channel)this.val$ch, (Promise)this.val$promise);
            }
        });
    }

    private void notifyHealthCheck(Future<Boolean> future, Channel ch, Promise<Channel> promise) {
        assert (ch.eventLoop().inEventLoop());
        if (!future.isSuccess()) {
            this.closeChannel((Channel)ch);
            this.acquireHealthyFromPoolOrNew(promise);
            return;
        }
        if (!future.getNow().booleanValue()) {
            this.closeChannel((Channel)ch);
            this.acquireHealthyFromPoolOrNew(promise);
            return;
        }
        try {
            ch.attr(this.poolKey).set((SimpleChannelPool)this);
            this.handler.channelAcquired((Channel)ch);
            promise.setSuccess((Channel)ch);
            return;
        }
        catch (Throwable cause) {
            this.closeAndFail((Channel)ch, (Throwable)cause, promise);
            return;
        }
    }

    protected ChannelFuture connectChannel(Bootstrap bs) {
        return bs.connect();
    }

    @Override
    public final Future<Void> release(Channel channel) {
        return this.release((Channel)channel, channel.eventLoop().newPromise());
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        ObjectUtil.checkNotNull(channel, (String)"channel");
        ObjectUtil.checkNotNull(promise, (String)"promise");
        try {
            EventLoop loop = channel.eventLoop();
            if (loop.inEventLoop()) {
                this.doReleaseChannel((Channel)channel, promise);
                return promise;
            }
            loop.execute((Runnable)new Runnable((SimpleChannelPool)this, (Channel)channel, promise){
                final /* synthetic */ Channel val$channel;
                final /* synthetic */ Promise val$promise;
                final /* synthetic */ SimpleChannelPool this$0;
                {
                    this.this$0 = this$0;
                    this.val$channel = channel;
                    this.val$promise = promise;
                }

                public void run() {
                    SimpleChannelPool.access$300((SimpleChannelPool)this.this$0, (Channel)this.val$channel, (Promise)this.val$promise);
                }
            });
            return promise;
        }
        catch (Throwable cause) {
            this.closeAndFail((Channel)channel, (Throwable)cause, promise);
        }
        return promise;
    }

    private void doReleaseChannel(Channel channel, Promise<Void> promise) {
        assert (channel.eventLoop().inEventLoop());
        if (channel.attr(this.poolKey).getAndSet(null) != this) {
            this.closeAndFail((Channel)channel, (Throwable)new IllegalArgumentException((String)("Channel " + channel + " was not acquired from this ChannelPool")), promise);
            return;
        }
        try {
            if (this.releaseHealthCheck) {
                this.doHealthCheckOnRelease((Channel)channel, promise);
                return;
            }
            this.releaseAndOffer((Channel)channel, promise);
            return;
        }
        catch (Throwable cause) {
            this.closeAndFail((Channel)channel, (Throwable)cause, promise);
        }
    }

    private void doHealthCheckOnRelease(Channel channel, Promise<Void> promise) throws Exception {
        Future<Boolean> f = this.healthCheck.isHealthy((Channel)channel);
        if (f.isDone()) {
            this.releaseAndOfferIfHealthy((Channel)channel, promise, f);
            return;
        }
        f.addListener((GenericFutureListener<Future<Boolean>>)new FutureListener<Boolean>((SimpleChannelPool)this, (Channel)channel, promise, f){
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ Promise val$promise;
            final /* synthetic */ Future val$f;
            final /* synthetic */ SimpleChannelPool this$0;
            {
                this.this$0 = this$0;
                this.val$channel = channel;
                this.val$promise = promise;
                this.val$f = future;
            }

            public void operationComplete(Future<Boolean> future) throws Exception {
                SimpleChannelPool.access$400((SimpleChannelPool)this.this$0, (Channel)this.val$channel, (Promise)this.val$promise, (Future)this.val$f);
            }
        });
    }

    private void releaseAndOfferIfHealthy(Channel channel, Promise<Void> promise, Future<Boolean> future) throws Exception {
        if (future.getNow().booleanValue()) {
            this.releaseAndOffer((Channel)channel, promise);
            return;
        }
        this.handler.channelReleased((Channel)channel);
        promise.setSuccess(null);
    }

    private void releaseAndOffer(Channel channel, Promise<Void> promise) throws Exception {
        if (this.offerChannel((Channel)channel)) {
            this.handler.channelReleased((Channel)channel);
            promise.setSuccess(null);
            return;
        }
        this.closeAndFail((Channel)channel, (Throwable)new IllegalStateException((SimpleChannelPool)this, (String)"ChannelPool full"){
            final /* synthetic */ SimpleChannelPool this$0;
            {
                this.this$0 = this$0;
                super((String)x0);
            }

            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        }, promise);
    }

    private void closeChannel(Channel channel) {
        channel.attr(this.poolKey).getAndSet(null);
        channel.close();
    }

    private void closeAndFail(Channel channel, Throwable cause, Promise<?> promise) {
        this.closeChannel((Channel)channel);
        promise.tryFailure((Throwable)cause);
    }

    protected Channel pollChannel() {
        Channel channel;
        if (this.lastRecentUsed) {
            channel = this.deque.pollLast();
            return channel;
        }
        channel = this.deque.pollFirst();
        return channel;
    }

    protected boolean offerChannel(Channel channel) {
        return this.deque.offer((Channel)channel);
    }

    @Override
    public void close() {
        Channel channel;
        while ((channel = this.pollChannel()) != null) {
            channel.close().awaitUninterruptibly();
        }
        return;
    }

    static /* synthetic */ void access$000(SimpleChannelPool x0, ChannelFuture x1, Promise x2) throws Exception {
        x0.notifyConnect((ChannelFuture)x1, (Promise<Channel>)x2);
    }

    static /* synthetic */ void access$100(SimpleChannelPool x0, Channel x1, Promise x2) {
        x0.doHealthCheck((Channel)x1, (Promise<Channel>)x2);
    }

    static /* synthetic */ void access$200(SimpleChannelPool x0, Future x1, Channel x2, Promise x3) {
        x0.notifyHealthCheck((Future<Boolean>)x1, (Channel)x2, (Promise<Channel>)x3);
    }

    static /* synthetic */ void access$300(SimpleChannelPool x0, Channel x1, Promise x2) {
        x0.doReleaseChannel((Channel)x1, (Promise<Void>)x2);
    }

    static /* synthetic */ void access$400(SimpleChannelPool x0, Channel x1, Promise x2, Future x3) throws Exception {
        x0.releaseAndOfferIfHealthy((Channel)x1, (Promise<Void>)x2, (Future<Boolean>)x3);
    }
}

