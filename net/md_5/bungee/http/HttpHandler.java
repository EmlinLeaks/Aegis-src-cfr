/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import java.nio.charset.Charset;
import net.md_5.bungee.api.Callback;

public class HttpHandler
extends SimpleChannelInboundHandler<HttpObject> {
    private final Callback<String> callback;
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            this.callback.done(null, (Throwable)cause);
            return;
        }
        finally {
            ctx.channel().close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse)msg;
            int responseCode = response.getStatus().code();
            if (responseCode == HttpResponseStatus.NO_CONTENT.code()) {
                this.done((ChannelHandlerContext)ctx);
                return;
            }
            if (responseCode != HttpResponseStatus.OK.code()) {
                throw new IllegalStateException((String)("Expected HTTP response 200 OK, got " + response.getStatus()));
            }
        }
        if (!(msg instanceof HttpContent)) return;
        HttpContent content = (HttpContent)msg;
        this.buffer.append((String)content.content().toString((Charset)Charset.forName((String)"UTF-8")));
        if (!(msg instanceof LastHttpContent)) return;
        this.done((ChannelHandlerContext)ctx);
    }

    private void done(ChannelHandlerContext ctx) {
        try {
            this.callback.done((String)this.buffer.toString(), null);
            return;
        }
        finally {
            ctx.channel().close();
        }
    }

    public HttpHandler(Callback<String> callback) {
        this.callback = callback;
    }
}

