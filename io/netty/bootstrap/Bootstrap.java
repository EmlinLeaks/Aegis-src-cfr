/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.DefaultAddressResolverGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;

public class Bootstrap
extends AbstractBootstrap<Bootstrap, Channel> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Bootstrap.class);
    private static final AddressResolverGroup<?> DEFAULT_RESOLVER = DefaultAddressResolverGroup.INSTANCE;
    private final BootstrapConfig config = new BootstrapConfig((Bootstrap)this);
    private volatile AddressResolverGroup<SocketAddress> resolver = DEFAULT_RESOLVER;
    private volatile SocketAddress remoteAddress;

    public Bootstrap() {
    }

    private Bootstrap(Bootstrap bootstrap) {
        super(bootstrap);
        this.resolver = bootstrap.resolver;
        this.remoteAddress = bootstrap.remoteAddress;
    }

    public Bootstrap resolver(AddressResolverGroup<?> resolver) {
        this.resolver = resolver == null ? DEFAULT_RESOLVER : resolver;
        return this;
    }

    public Bootstrap remoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    public Bootstrap remoteAddress(String inetHost, int inetPort) {
        this.remoteAddress = InetSocketAddress.createUnresolved((String)inetHost, (int)inetPort);
        return this;
    }

    public Bootstrap remoteAddress(InetAddress inetHost, int inetPort) {
        this.remoteAddress = new InetSocketAddress((InetAddress)inetHost, (int)inetPort);
        return this;
    }

    public ChannelFuture connect() {
        this.validate();
        SocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress != null) return this.doResolveAndConnect((SocketAddress)remoteAddress, (SocketAddress)this.config.localAddress());
        throw new IllegalStateException((String)"remoteAddress not set");
    }

    public ChannelFuture connect(String inetHost, int inetPort) {
        return this.connect((SocketAddress)InetSocketAddress.createUnresolved((String)inetHost, (int)inetPort));
    }

    public ChannelFuture connect(InetAddress inetHost, int inetPort) {
        return this.connect((SocketAddress)new InetSocketAddress((InetAddress)inetHost, (int)inetPort));
    }

    public ChannelFuture connect(SocketAddress remoteAddress) {
        ObjectUtil.checkNotNull(remoteAddress, (String)"remoteAddress");
        this.validate();
        return this.doResolveAndConnect((SocketAddress)remoteAddress, (SocketAddress)this.config.localAddress());
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        ObjectUtil.checkNotNull(remoteAddress, (String)"remoteAddress");
        this.validate();
        return this.doResolveAndConnect((SocketAddress)remoteAddress, (SocketAddress)localAddress);
    }

    private ChannelFuture doResolveAndConnect(SocketAddress remoteAddress, SocketAddress localAddress) {
        ChannelFuture regFuture = this.initAndRegister();
        Channel channel = regFuture.channel();
        if (regFuture.isDone()) {
            if (regFuture.isSuccess()) return this.doResolveAndConnect0((Channel)channel, (SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)channel.newPromise());
            return regFuture;
        }
        AbstractBootstrap.PendingRegistrationPromise promise = new AbstractBootstrap.PendingRegistrationPromise((Channel)channel);
        regFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((Bootstrap)this, (AbstractBootstrap.PendingRegistrationPromise)promise, (Channel)channel, (SocketAddress)remoteAddress, (SocketAddress)localAddress){
            final /* synthetic */ AbstractBootstrap.PendingRegistrationPromise val$promise;
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ SocketAddress val$remoteAddress;
            final /* synthetic */ SocketAddress val$localAddress;
            final /* synthetic */ Bootstrap this$0;
            {
                this.this$0 = this$0;
                this.val$promise = pendingRegistrationPromise;
                this.val$channel = channel;
                this.val$remoteAddress = socketAddress;
                this.val$localAddress = socketAddress2;
            }

            public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    this.val$promise.setFailure((Throwable)cause);
                    return;
                }
                this.val$promise.registered();
                Bootstrap.access$000((Bootstrap)this.this$0, (Channel)this.val$channel, (SocketAddress)this.val$remoteAddress, (SocketAddress)this.val$localAddress, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    private ChannelFuture doResolveAndConnect0(Channel channel, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        try {
            EventLoop eventLoop = channel.eventLoop();
            AddressResolver<SocketAddress> resolver = this.resolver.getResolver((EventExecutor)eventLoop);
            if (!resolver.isSupported((SocketAddress)remoteAddress) || resolver.isResolved((SocketAddress)remoteAddress)) {
                Bootstrap.doConnect((SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
                return promise;
            }
            Future<SocketAddress> resolveFuture = resolver.resolve((SocketAddress)remoteAddress);
            if (!resolveFuture.isDone()) {
                resolveFuture.addListener((GenericFutureListener<Future<SocketAddress>>)new FutureListener<SocketAddress>((Bootstrap)this, (Channel)channel, (ChannelPromise)promise, (SocketAddress)localAddress){
                    final /* synthetic */ Channel val$channel;
                    final /* synthetic */ ChannelPromise val$promise;
                    final /* synthetic */ SocketAddress val$localAddress;
                    final /* synthetic */ Bootstrap this$0;
                    {
                        this.this$0 = this$0;
                        this.val$channel = channel;
                        this.val$promise = channelPromise;
                        this.val$localAddress = socketAddress;
                    }

                    public void operationComplete(Future<SocketAddress> future) throws java.lang.Exception {
                        if (future.cause() != null) {
                            this.val$channel.close();
                            this.val$promise.setFailure((Throwable)future.cause());
                            return;
                        }
                        Bootstrap.access$100((SocketAddress)future.getNow(), (SocketAddress)this.val$localAddress, (ChannelPromise)this.val$promise);
                    }
                });
                return promise;
            }
            Throwable resolveFailureCause = resolveFuture.cause();
            if (resolveFailureCause != null) {
                channel.close();
                promise.setFailure((Throwable)resolveFailureCause);
                return promise;
            }
            Bootstrap.doConnect((SocketAddress)resolveFuture.getNow(), (SocketAddress)localAddress, (ChannelPromise)promise);
            return promise;
        }
        catch (Throwable cause) {
            promise.tryFailure((Throwable)cause);
        }
        return promise;
    }

    private static void doConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise connectPromise) {
        Channel channel = connectPromise.channel();
        channel.eventLoop().execute((Runnable)new Runnable((SocketAddress)localAddress, (Channel)channel, (SocketAddress)remoteAddress, (ChannelPromise)connectPromise){
            final /* synthetic */ SocketAddress val$localAddress;
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ SocketAddress val$remoteAddress;
            final /* synthetic */ ChannelPromise val$connectPromise;
            {
                this.val$localAddress = socketAddress;
                this.val$channel = channel;
                this.val$remoteAddress = socketAddress2;
                this.val$connectPromise = channelPromise;
            }

            public void run() {
                if (this.val$localAddress == null) {
                    this.val$channel.connect((SocketAddress)this.val$remoteAddress, (ChannelPromise)this.val$connectPromise);
                } else {
                    this.val$channel.connect((SocketAddress)this.val$remoteAddress, (SocketAddress)this.val$localAddress, (ChannelPromise)this.val$connectPromise);
                }
                this.val$connectPromise.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        });
    }

    @Override
    void init(Channel channel) {
        ChannelPipeline p = channel.pipeline();
        p.addLast((ChannelHandler[])new ChannelHandler[]{this.config.handler()});
        Bootstrap.setChannelOptions((Channel)channel, this.options0().entrySet().toArray(Bootstrap.newOptionArray((int)0)), (InternalLogger)logger);
        Bootstrap.setAttributes((Channel)channel, this.attrs0().entrySet().toArray(Bootstrap.newAttrArray((int)0)));
    }

    @Override
    public Bootstrap validate() {
        super.validate();
        if (this.config.handler() != null) return this;
        throw new IllegalStateException((String)"handler not set");
    }

    @Override
    public Bootstrap clone() {
        return new Bootstrap((Bootstrap)this);
    }

    public Bootstrap clone(EventLoopGroup group) {
        Bootstrap bs = new Bootstrap((Bootstrap)this);
        bs.group = group;
        return bs;
    }

    public final BootstrapConfig config() {
        return this.config;
    }

    final SocketAddress remoteAddress() {
        return this.remoteAddress;
    }

    final AddressResolverGroup<?> resolver() {
        return this.resolver;
    }

    static /* synthetic */ ChannelFuture access$000(Bootstrap x0, Channel x1, SocketAddress x2, SocketAddress x3, ChannelPromise x4) {
        return x0.doResolveAndConnect0((Channel)x1, (SocketAddress)x2, (SocketAddress)x3, (ChannelPromise)x4);
    }

    static /* synthetic */ void access$100(SocketAddress x0, SocketAddress x1, ChannelPromise x2) {
        Bootstrap.doConnect((SocketAddress)x0, (SocketAddress)x1, (ChannelPromise)x2);
    }
}

