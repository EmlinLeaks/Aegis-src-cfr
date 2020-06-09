/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;

public abstract class AbstractRemoteAddressFilter<T extends SocketAddress>
extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.handleNewChannel((ChannelHandlerContext)ctx);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.handleNewChannel((ChannelHandlerContext)ctx)) {
            throw new IllegalStateException((String)("cannot determine to accept or reject a channel: " + ctx.channel()));
        }
        ctx.fireChannelActive();
    }

    private boolean handleNewChannel(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress == null) {
            return false;
        }
        ctx.pipeline().remove((ChannelHandler)this);
        if (this.accept((ChannelHandlerContext)ctx, remoteAddress)) {
            this.channelAccepted((ChannelHandlerContext)ctx, remoteAddress);
            return true;
        }
        ChannelFuture rejectedFuture = this.channelRejected((ChannelHandlerContext)ctx, remoteAddress);
        if (rejectedFuture != null) {
            rejectedFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
            return true;
        }
        ctx.close();
        return true;
    }

    protected abstract boolean accept(ChannelHandlerContext var1, T var2) throws Exception;

    protected void channelAccepted(ChannelHandlerContext ctx, T remoteAddress) {
    }

    protected ChannelFuture channelRejected(ChannelHandlerContext ctx, T remoteAddress) {
        return null;
    }
}

