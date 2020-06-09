/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.address;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.address.DynamicAddressConnectHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;

public abstract class DynamicAddressConnectHandler
extends ChannelOutboundHandlerAdapter {
    @Override
    public final void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        SocketAddress local;
        SocketAddress remote;
        try {
            remote = this.remoteAddress((SocketAddress)remoteAddress, (SocketAddress)localAddress);
            local = this.localAddress((SocketAddress)remoteAddress, (SocketAddress)localAddress);
        }
        catch (Exception e) {
            promise.setFailure((Throwable)e);
            return;
        }
        ctx.connect((SocketAddress)remote, (SocketAddress)local, (ChannelPromise)promise).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((DynamicAddressConnectHandler)this){
            final /* synthetic */ DynamicAddressConnectHandler this$0;
            {
                this.this$0 = this$0;
            }

            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) return;
                future.channel().pipeline().remove((io.netty.channel.ChannelHandler)this.this$0);
            }
        });
    }

    protected SocketAddress localAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        return localAddress;
    }

    protected SocketAddress remoteAddress(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        return remoteAddress;
    }
}

