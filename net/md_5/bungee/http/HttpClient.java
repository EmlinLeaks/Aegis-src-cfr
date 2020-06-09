/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.http;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.http.HttpInitializer;
import net.md_5.bungee.netty.PipelineUtils;

public class HttpClient {
    public static final int TIMEOUT = 5000;
    private static final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder().expireAfterWrite((long)1L, (TimeUnit)TimeUnit.MINUTES).build();

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    public static void get(String url, EventLoop eventLoop, Callback<String> callback) {
        Preconditions.checkNotNull(url, (Object)"url");
        Preconditions.checkNotNull(eventLoop, (Object)"eventLoop");
        Preconditions.checkNotNull(callback, (Object)"callBack");
        uri = URI.create((String)url);
        Preconditions.checkNotNull(uri.getScheme(), (Object)"scheme");
        Preconditions.checkNotNull(uri.getHost(), (Object)"host");
        ssl = uri.getScheme().equals((Object)"https");
        port = uri.getPort();
        if (port == -1) {
            var6_6 = uri.getScheme();
            var7_7 = -1;
            switch (var6_6.hashCode()) {
                case 3213448: {
                    if (!var6_6.equals((Object)"http")) break;
                    var7_7 = 0;
                    break;
                }
                case 99617003: {
                    if (!var6_6.equals((Object)"https")) break;
                    var7_7 = 1;
                }
            }
            switch (var7_7) {
                case 0: {
                    port = 80;
                    ** break;
                }
                case 1: {
                    port = 443;
                    ** break;
                }
            }
            throw new IllegalArgumentException((String)("Unknown scheme " + uri.getScheme()));
        }
lbl33: // 4 sources:
        if ((inetHost = HttpClient.addressCache.getIfPresent((Object)uri.getHost())) == null) {
            try {
                inetHost = InetAddress.getByName((String)uri.getHost());
            }
            catch (UnknownHostException ex) {
                callback.done(null, (Throwable)ex);
                return;
            }
            HttpClient.addressCache.put((String)uri.getHost(), (InetAddress)inetHost);
        }
        future = new ChannelFutureListener((URI)uri, callback){
            final /* synthetic */ URI val$uri;
            final /* synthetic */ Callback val$callback;
            {
                this.val$uri = uRI;
                this.val$callback = callback;
            }

            public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                if (!future.isSuccess()) {
                    HttpClient.access$000().invalidate((Object)this.val$uri.getHost());
                    this.val$callback.done(null, (Throwable)future.cause());
                    return;
                }
                String path = this.val$uri.getRawPath() + (this.val$uri.getRawQuery() == null ? "" : "?" + this.val$uri.getRawQuery());
                io.netty.handler.codec.http.DefaultHttpRequest request = new io.netty.handler.codec.http.DefaultHttpRequest((io.netty.handler.codec.http.HttpVersion)io.netty.handler.codec.http.HttpVersion.HTTP_1_1, (io.netty.handler.codec.http.HttpMethod)io.netty.handler.codec.http.HttpMethod.GET, (String)path);
                request.headers().set((String)"Host", (Object)this.val$uri.getHost());
                future.channel().writeAndFlush((Object)request);
            }
        };
        ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(PipelineUtils.getChannel())).group((EventLoopGroup)eventLoop)).handler((ChannelHandler)new HttpInitializer(callback, (boolean)ssl, (String)uri.getHost(), (int)port))).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf((int)5000))).remoteAddress((InetAddress)inetHost, (int)port).connect().addListener((GenericFutureListener<? extends Future<? super Void>>)future);
    }

    private HttpClient() {
    }

    static /* synthetic */ Cache access$000() {
        return addressCache;
    }
}

