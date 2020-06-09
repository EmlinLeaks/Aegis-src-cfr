/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.ServerBootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerBootstrap
extends AbstractBootstrap<ServerBootstrap, ServerChannel> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
    private final Map<ChannelOption<?>, Object> childOptions = new ConcurrentHashMap<ChannelOption<?>, Object>();
    private final Map<AttributeKey<?>, Object> childAttrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
    private final ServerBootstrapConfig config = new ServerBootstrapConfig((ServerBootstrap)this);
    private volatile EventLoopGroup childGroup;
    private volatile ChannelHandler childHandler;

    public ServerBootstrap() {
    }

    private ServerBootstrap(ServerBootstrap bootstrap) {
        super(bootstrap);
        this.childGroup = bootstrap.childGroup;
        this.childHandler = bootstrap.childHandler;
        this.childOptions.putAll(bootstrap.childOptions);
        this.childAttrs.putAll(bootstrap.childAttrs);
    }

    @Override
    public ServerBootstrap group(EventLoopGroup group) {
        return this.group((EventLoopGroup)group, (EventLoopGroup)group);
    }

    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group((EventLoopGroup)parentGroup);
        ObjectUtil.checkNotNull(childGroup, (String)"childGroup");
        if (this.childGroup != null) {
            throw new IllegalStateException((String)"childGroup set already");
        }
        this.childGroup = childGroup;
        return this;
    }

    public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
        ObjectUtil.checkNotNull(childOption, (String)"childOption");
        if (value == null) {
            this.childOptions.remove(childOption);
            return this;
        }
        this.childOptions.put(childOption, value);
        return this;
    }

    public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
        ObjectUtil.checkNotNull(childKey, (String)"childKey");
        if (value == null) {
            this.childAttrs.remove(childKey);
            return this;
        }
        this.childAttrs.put(childKey, value);
        return this;
    }

    public ServerBootstrap childHandler(ChannelHandler childHandler) {
        this.childHandler = ObjectUtil.checkNotNull(childHandler, (String)"childHandler");
        return this;
    }

    @Override
    void init(Channel channel) {
        ServerBootstrap.setChannelOptions((Channel)channel, this.options0().entrySet().toArray(ServerBootstrap.newOptionArray((int)0)), (InternalLogger)logger);
        ServerBootstrap.setAttributes((Channel)channel, this.attrs0().entrySet().toArray(ServerBootstrap.newAttrArray((int)0)));
        ChannelPipeline p = channel.pipeline();
        EventLoopGroup currentChildGroup = this.childGroup;
        ChannelHandler currentChildHandler = this.childHandler;
        Map.Entry[] currentChildOptions = this.childOptions.entrySet().toArray(ServerBootstrap.newOptionArray((int)0));
        Map.Entry[] currentChildAttrs = this.childAttrs.entrySet().toArray(ServerBootstrap.newAttrArray((int)0));
        p.addLast((ChannelHandler[])new ChannelHandler[]{new ChannelInitializer<Channel>((ServerBootstrap)this, (EventLoopGroup)currentChildGroup, (ChannelHandler)currentChildHandler, (Map.Entry[])currentChildOptions, (Map.Entry[])currentChildAttrs){
            final /* synthetic */ EventLoopGroup val$currentChildGroup;
            final /* synthetic */ ChannelHandler val$currentChildHandler;
            final /* synthetic */ Map.Entry[] val$currentChildOptions;
            final /* synthetic */ Map.Entry[] val$currentChildAttrs;
            final /* synthetic */ ServerBootstrap this$0;
            {
                this.this$0 = this$0;
                this.val$currentChildGroup = eventLoopGroup;
                this.val$currentChildHandler = channelHandler;
                this.val$currentChildOptions = arrentry;
                this.val$currentChildAttrs = arrentry2;
            }

            public void initChannel(Channel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler handler = ServerBootstrap.access$000((ServerBootstrap)this.this$0).handler();
                if (handler != null) {
                    pipeline.addLast((ChannelHandler[])new ChannelHandler[]{handler});
                }
                ch.eventLoop().execute((java.lang.Runnable)new java.lang.Runnable(this, (ChannelPipeline)pipeline, (Channel)ch){
                    final /* synthetic */ ChannelPipeline val$pipeline;
                    final /* synthetic */ Channel val$ch;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = this$1;
                        this.val$pipeline = channelPipeline;
                        this.val$ch = channel;
                    }

                    public void run() {
                        this.val$pipeline.addLast((ChannelHandler[])new ChannelHandler[]{new io.netty.bootstrap.ServerBootstrap$ServerBootstrapAcceptor((Channel)this.val$ch, (EventLoopGroup)this.this$1.val$currentChildGroup, (ChannelHandler)this.this$1.val$currentChildHandler, this.this$1.val$currentChildOptions, this.this$1.val$currentChildAttrs)});
                    }
                });
            }
        }});
    }

    @Override
    public ServerBootstrap validate() {
        super.validate();
        if (this.childHandler == null) {
            throw new IllegalStateException((String)"childHandler not set");
        }
        if (this.childGroup != null) return this;
        logger.warn((String)"childGroup is not set. Using parentGroup instead.");
        this.childGroup = this.config.group();
        return this;
    }

    @Override
    public ServerBootstrap clone() {
        return new ServerBootstrap((ServerBootstrap)this);
    }

    @Deprecated
    public EventLoopGroup childGroup() {
        return this.childGroup;
    }

    final ChannelHandler childHandler() {
        return this.childHandler;
    }

    final Map<ChannelOption<?>, Object> childOptions() {
        return ServerBootstrap.copiedMap(this.childOptions);
    }

    final Map<AttributeKey<?>, Object> childAttrs() {
        return ServerBootstrap.copiedMap(this.childAttrs);
    }

    public final ServerBootstrapConfig config() {
        return this.config;
    }

    static /* synthetic */ ServerBootstrapConfig access$000(ServerBootstrap x0) {
        return x0.config;
    }

    static /* synthetic */ InternalLogger access$100() {
        return logger;
    }
}

