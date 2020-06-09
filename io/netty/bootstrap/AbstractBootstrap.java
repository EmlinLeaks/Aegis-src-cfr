/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.FailedChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel>
implements Cloneable {
    volatile EventLoopGroup group;
    private volatile ChannelFactory<? extends C> channelFactory;
    private volatile SocketAddress localAddress;
    private final Map<ChannelOption<?>, Object> options = new ConcurrentHashMap<ChannelOption<?>, Object>();
    private final Map<AttributeKey<?>, Object> attrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
    private volatile ChannelHandler handler;

    AbstractBootstrap() {
    }

    AbstractBootstrap(AbstractBootstrap<B, C> bootstrap) {
        this.group = bootstrap.group;
        this.channelFactory = bootstrap.channelFactory;
        this.handler = bootstrap.handler;
        this.localAddress = bootstrap.localAddress;
        this.options.putAll(bootstrap.options);
        this.attrs.putAll(bootstrap.attrs);
    }

    public B group(EventLoopGroup group) {
        ObjectUtil.checkNotNull(group, (String)"group");
        if (this.group != null) {
            throw new IllegalStateException((String)"group set already");
        }
        this.group = group;
        return (B)this.self();
    }

    private B self() {
        return (B)this;
    }

    public B channel(Class<? extends C> channelClass) {
        return (B)this.channelFactory(new ReflectiveChannelFactory<C>(ObjectUtil.checkNotNull(channelClass, (String)"channelClass")));
    }

    @Deprecated
    public B channelFactory(ChannelFactory<? extends C> channelFactory) {
        ObjectUtil.checkNotNull(channelFactory, (String)"channelFactory");
        if (this.channelFactory != null) {
            throw new IllegalStateException((String)"channelFactory set already");
        }
        this.channelFactory = channelFactory;
        return (B)this.self();
    }

    public B channelFactory(io.netty.channel.ChannelFactory<? extends C> channelFactory) {
        return (B)this.channelFactory(channelFactory);
    }

    public B localAddress(SocketAddress localAddress) {
        this.localAddress = localAddress;
        return (B)this.self();
    }

    public B localAddress(int inetPort) {
        return (B)this.localAddress((SocketAddress)new InetSocketAddress((int)inetPort));
    }

    public B localAddress(String inetHost, int inetPort) {
        return (B)this.localAddress((SocketAddress)SocketUtils.socketAddress((String)inetHost, (int)inetPort));
    }

    public B localAddress(InetAddress inetHost, int inetPort) {
        return (B)this.localAddress((SocketAddress)new InetSocketAddress((InetAddress)inetHost, (int)inetPort));
    }

    public <T> B option(ChannelOption<T> option, T value) {
        ObjectUtil.checkNotNull(option, (String)"option");
        if (value == null) {
            this.options.remove(option);
            return (B)((B)this.self());
        }
        this.options.put(option, value);
        return (B)this.self();
    }

    public <T> B attr(AttributeKey<T> key, T value) {
        ObjectUtil.checkNotNull(key, (String)"key");
        if (value == null) {
            this.attrs.remove(key);
            return (B)((B)this.self());
        }
        this.attrs.put(key, value);
        return (B)this.self();
    }

    public B validate() {
        if (this.group == null) {
            throw new IllegalStateException((String)"group not set");
        }
        if (this.channelFactory != null) return (B)this.self();
        throw new IllegalStateException((String)"channel or channelFactory not set");
    }

    public abstract B clone();

    public ChannelFuture register() {
        this.validate();
        return this.initAndRegister();
    }

    public ChannelFuture bind() {
        this.validate();
        SocketAddress localAddress = this.localAddress;
        if (localAddress != null) return this.doBind((SocketAddress)localAddress);
        throw new IllegalStateException((String)"localAddress not set");
    }

    public ChannelFuture bind(int inetPort) {
        return this.bind((SocketAddress)new InetSocketAddress((int)inetPort));
    }

    public ChannelFuture bind(String inetHost, int inetPort) {
        return this.bind((SocketAddress)SocketUtils.socketAddress((String)inetHost, (int)inetPort));
    }

    public ChannelFuture bind(InetAddress inetHost, int inetPort) {
        return this.bind((SocketAddress)new InetSocketAddress((InetAddress)inetHost, (int)inetPort));
    }

    public ChannelFuture bind(SocketAddress localAddress) {
        this.validate();
        return this.doBind((SocketAddress)ObjectUtil.checkNotNull(localAddress, (String)"localAddress"));
    }

    private ChannelFuture doBind(SocketAddress localAddress) {
        ChannelFuture regFuture = this.initAndRegister();
        Channel channel = regFuture.channel();
        if (regFuture.cause() != null) {
            return regFuture;
        }
        if (regFuture.isDone()) {
            ChannelPromise promise = channel.newPromise();
            AbstractBootstrap.doBind0((ChannelFuture)regFuture, (Channel)channel, (SocketAddress)localAddress, (ChannelPromise)promise);
            return promise;
        }
        PendingRegistrationPromise promise = new PendingRegistrationPromise((Channel)channel);
        regFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((AbstractBootstrap)this, (PendingRegistrationPromise)promise, (ChannelFuture)regFuture, (Channel)channel, (SocketAddress)localAddress){
            final /* synthetic */ PendingRegistrationPromise val$promise;
            final /* synthetic */ ChannelFuture val$regFuture;
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ SocketAddress val$localAddress;
            final /* synthetic */ AbstractBootstrap this$0;
            {
                this.this$0 = this$0;
                this.val$promise = pendingRegistrationPromise;
                this.val$regFuture = channelFuture;
                this.val$channel = channel;
                this.val$localAddress = socketAddress;
            }

            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    this.val$promise.setFailure((Throwable)cause);
                    return;
                }
                this.val$promise.registered();
                AbstractBootstrap.access$000((ChannelFuture)this.val$regFuture, (Channel)this.val$channel, (SocketAddress)this.val$localAddress, (ChannelPromise)this.val$promise);
            }
        });
        return promise;
    }

    final ChannelFuture initAndRegister() {
        Channel channel = null;
        try {
            channel = (Channel)this.channelFactory.newChannel();
            this.init((Channel)channel);
        }
        catch (Throwable t) {
            if (channel == null) return new DefaultChannelPromise((Channel)new FailedChannel(), (EventExecutor)GlobalEventExecutor.INSTANCE).setFailure((Throwable)t);
            channel.unsafe().closeForcibly();
            return new DefaultChannelPromise((Channel)channel, (EventExecutor)GlobalEventExecutor.INSTANCE).setFailure((Throwable)t);
        }
        ChannelFuture regFuture = this.config().group().register((Channel)channel);
        if (regFuture.cause() == null) return regFuture;
        if (channel.isRegistered()) {
            channel.close();
            return regFuture;
        }
        channel.unsafe().closeForcibly();
        return regFuture;
    }

    abstract void init(Channel var1) throws Exception;

    private static void doBind0(ChannelFuture regFuture, Channel channel, SocketAddress localAddress, ChannelPromise promise) {
        channel.eventLoop().execute((Runnable)new Runnable((ChannelFuture)regFuture, (Channel)channel, (SocketAddress)localAddress, (ChannelPromise)promise){
            final /* synthetic */ ChannelFuture val$regFuture;
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ SocketAddress val$localAddress;
            final /* synthetic */ ChannelPromise val$promise;
            {
                this.val$regFuture = channelFuture;
                this.val$channel = channel;
                this.val$localAddress = socketAddress;
                this.val$promise = channelPromise;
            }

            public void run() {
                if (this.val$regFuture.isSuccess()) {
                    this.val$channel.bind((SocketAddress)this.val$localAddress, (ChannelPromise)this.val$promise).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
                    return;
                }
                this.val$promise.setFailure((Throwable)this.val$regFuture.cause());
            }
        });
    }

    public B handler(ChannelHandler handler) {
        this.handler = ObjectUtil.checkNotNull(handler, (String)"handler");
        return (B)this.self();
    }

    @Deprecated
    public final EventLoopGroup group() {
        return this.group;
    }

    public abstract AbstractBootstrapConfig<B, C> config();

    final Map<ChannelOption<?>, Object> options0() {
        return this.options;
    }

    final Map<AttributeKey<?>, Object> attrs0() {
        return this.attrs;
    }

    final SocketAddress localAddress() {
        return this.localAddress;
    }

    final ChannelFactory<? extends C> channelFactory() {
        return this.channelFactory;
    }

    final ChannelHandler handler() {
        return this.handler;
    }

    final Map<ChannelOption<?>, Object> options() {
        return AbstractBootstrap.copiedMap(this.options);
    }

    final Map<AttributeKey<?>, Object> attrs() {
        return AbstractBootstrap.copiedMap(this.attrs);
    }

    static <K, V> Map<K, V> copiedMap(Map<K, V> map) {
        if (!map.isEmpty()) return Collections.unmodifiableMap(new HashMap<K, V>(map));
        return Collections.emptyMap();
    }

    static void setAttributes(Channel channel, Map.Entry<AttributeKey<?>, Object>[] attrs) {
        Map.Entry<AttributeKey<?>, Object>[] arrentry = attrs;
        int n = arrentry.length;
        int n2 = 0;
        while (n2 < n) {
            Map.Entry<AttributeKey<?>, Object> e = arrentry[n2];
            AttributeKey<?> key = e.getKey();
            channel.attr(key).set(e.getValue());
            ++n2;
        }
    }

    static void setChannelOptions(Channel channel, Map.Entry<ChannelOption<?>, Object>[] options, InternalLogger logger) {
        Map.Entry<ChannelOption<?>, Object>[] arrentry = options;
        int n = arrentry.length;
        int n2 = 0;
        while (n2 < n) {
            Map.Entry<ChannelOption<?>, Object> e = arrentry[n2];
            AbstractBootstrap.setChannelOption((Channel)channel, e.getKey(), (Object)e.getValue(), (InternalLogger)logger);
            ++n2;
        }
    }

    static Map.Entry<AttributeKey<?>, Object>[] newAttrArray(int size) {
        return new Map.Entry[size];
    }

    static Map.Entry<ChannelOption<?>, Object>[] newOptionArray(int size) {
        return new Map.Entry[size];
    }

    private static void setChannelOption(Channel channel, ChannelOption<?> option, Object value, InternalLogger logger) {
        try {
            if (channel.config().setOption(option, value)) return;
            logger.warn((String)"Unknown channel option '{}' for channel '{}'", option, (Object)channel);
            return;
        }
        catch (Throwable t) {
            logger.warn((String)"Failed to set channel option '{}' with value '{}' for channel '{}'", (Object[])new Object[]{option, value, channel, t});
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((char)'(').append(this.config()).append((char)')');
        return buf.toString();
    }

    static /* synthetic */ void access$000(ChannelFuture x0, Channel x1, SocketAddress x2, ChannelPromise x3) {
        AbstractBootstrap.doBind0((ChannelFuture)x0, (Channel)x1, (SocketAddress)x2, (ChannelPromise)x3);
    }
}

