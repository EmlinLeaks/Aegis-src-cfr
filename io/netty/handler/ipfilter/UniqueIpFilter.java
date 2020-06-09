/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.handler.ipfilter.UniqueIpFilter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ConcurrentSet;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

@ChannelHandler.Sharable
public class UniqueIpFilter
extends AbstractRemoteAddressFilter<InetSocketAddress> {
    private final Set<InetAddress> connected = new ConcurrentSet();

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        InetAddress remoteIp = remoteAddress.getAddress();
        if (!this.connected.add((InetAddress)remoteIp)) {
            return false;
        }
        ctx.channel().closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((UniqueIpFilter)this, (InetAddress)remoteIp){
            final /* synthetic */ InetAddress val$remoteIp;
            final /* synthetic */ UniqueIpFilter this$0;
            {
                this.this$0 = this$0;
                this.val$remoteIp = inetAddress;
            }

            public void operationComplete(ChannelFuture future) throws Exception {
                UniqueIpFilter.access$000((UniqueIpFilter)this.this$0).remove((java.lang.Object)this.val$remoteIp);
            }
        });
        return true;
    }

    static /* synthetic */ Set access$000(UniqueIpFilter x0) {
        return x0.connected;
    }
}

