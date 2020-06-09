/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public abstract class ChannelInitializer<C extends Channel>
extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
    private final Set<ChannelHandlerContext> initMap = Collections.newSetFromMap(new ConcurrentHashMap<K, V>());

    protected abstract void initChannel(C var1) throws Exception;

    @Override
    public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (this.initChannel((ChannelHandlerContext)ctx)) {
            ctx.pipeline().fireChannelRegistered();
            this.removeState((ChannelHandlerContext)ctx);
            return;
        }
        ctx.fireChannelRegistered();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (logger.isWarnEnabled()) {
            logger.warn((String)("Failed to initialize a channel. Closing: " + ctx.channel()), (Throwable)cause);
        }
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (!ctx.channel().isRegistered()) return;
        if (!this.initChannel((ChannelHandlerContext)ctx)) return;
        this.removeState((ChannelHandlerContext)ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.initMap.remove((Object)ctx);
    }

    private boolean initChannel(ChannelHandlerContext ctx) throws Exception {
        if (!this.initMap.add((ChannelHandlerContext)ctx)) return false;
        try {
            this.initChannel(ctx.channel());
            return true;
        }
        catch (Throwable cause) {
            this.exceptionCaught((ChannelHandlerContext)ctx, (Throwable)cause);
            return true;
        }
        finally {
            ChannelPipeline pipeline = ctx.pipeline();
            if (pipeline.context((ChannelHandler)this) != null) {
                pipeline.remove((ChannelHandler)this);
            }
        }
    }

    private void removeState(ChannelHandlerContext ctx) {
        if (ctx.isRemoved()) {
            this.initMap.remove((Object)ctx);
            return;
        }
        ctx.executor().execute((Runnable)new Runnable((ChannelInitializer)this, (ChannelHandlerContext)ctx){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelInitializer this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
            }

            public void run() {
                ChannelInitializer.access$000((ChannelInitializer)this.this$0).remove((Object)this.val$ctx);
            }
        });
    }

    static /* synthetic */ Set access$000(ChannelInitializer x0) {
        return x0.initMap;
    }
}

