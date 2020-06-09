/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.query;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.query.QueryHandler;

public class RemoteQuery {
    private final ProxyServer bungee;
    private final ListenerInfo listener;

    public void start(Class<? extends Channel> channel, InetSocketAddress address, EventLoopGroup eventLoop, ChannelFutureListener future) {
        ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(channel)).group((EventLoopGroup)eventLoop)).handler((ChannelHandler)new QueryHandler((ProxyServer)this.bungee, (ListenerInfo)this.listener))).localAddress((SocketAddress)address)).bind().addListener((GenericFutureListener<? extends Future<? super Void>>)future);
    }

    public RemoteQuery(ProxyServer bungee, ListenerInfo listener) {
        this.bungee = bungee;
        this.listener = listener;
    }
}

