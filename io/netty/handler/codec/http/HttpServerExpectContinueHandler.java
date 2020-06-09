/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpServerExpectContinueHandler
extends ChannelInboundHandlerAdapter {
    private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.EXPECTATION_FAILED, (ByteBuf)Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse ACCEPT = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.CONTINUE, (ByteBuf)Unpooled.EMPTY_BUFFER);

    protected HttpResponse acceptMessage(HttpRequest request) {
        return ACCEPT.retainedDuplicate();
    }

    protected HttpResponse rejectResponse(HttpRequest request) {
        return EXPECTATION_FAILED.retainedDuplicate();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest req;
        if (msg instanceof HttpRequest && HttpUtil.is100ContinueExpected((HttpMessage)(req = (HttpRequest)msg))) {
            HttpResponse accept = this.acceptMessage((HttpRequest)req);
            if (accept == null) {
                HttpResponse rejection = this.rejectResponse((HttpRequest)req);
                ReferenceCountUtil.release((Object)msg);
                ctx.writeAndFlush((Object)rejection).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
                return;
            }
            ctx.writeAndFlush((Object)accept).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
            req.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
        }
        super.channelRead((ChannelHandlerContext)ctx, (Object)msg);
    }

    static {
        EXPECTATION_FAILED.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)Integer.valueOf((int)0));
        ACCEPT.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)Integer.valueOf((int)0));
    }
}

