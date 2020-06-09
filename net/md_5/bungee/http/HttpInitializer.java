/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.http;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.http.HttpHandler;

public class HttpInitializer
extends ChannelInitializer<Channel> {
    private final Callback<String> callback;
    private final boolean ssl;
    private final String host;
    private final int port;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast((String)"timeout", (ChannelHandler)new ReadTimeoutHandler((long)5000L, (TimeUnit)TimeUnit.MILLISECONDS));
        if (this.ssl) {
            SSLEngine engine = SslContext.newClientContext().newEngine((ByteBufAllocator)ch.alloc(), (String)this.host, (int)this.port);
            ch.pipeline().addLast((String)"ssl", (ChannelHandler)new SslHandler((SSLEngine)engine));
        }
        ch.pipeline().addLast((String)"http", (ChannelHandler)new HttpClientCodec());
        ch.pipeline().addLast((String)"handler", (ChannelHandler)new HttpHandler(this.callback));
    }

    public HttpInitializer(Callback<String> callback, boolean ssl, String host, int port) {
        this.callback = callback;
        this.ssl = ssl;
        this.host = host;
        this.port = port;
    }
}

