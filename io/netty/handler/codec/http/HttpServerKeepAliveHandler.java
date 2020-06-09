/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpServerKeepAliveHandler
extends ChannelDuplexHandler {
    private static final String MULTIPART_PREFIX = "multipart";
    private boolean persistentConnection = true;
    private int pendingResponses;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)msg;
            if (this.persistentConnection) {
                ++this.pendingResponses;
                this.persistentConnection = HttpUtil.isKeepAlive((HttpMessage)request);
            }
        }
        super.channelRead((ChannelHandlerContext)ctx, (Object)msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse)msg;
            this.trackResponse((HttpResponse)response);
            if (!HttpUtil.isKeepAlive((HttpMessage)response) || !HttpServerKeepAliveHandler.isSelfDefinedMessageLength((HttpResponse)response)) {
                this.pendingResponses = 0;
                this.persistentConnection = false;
            }
            if (!this.shouldKeepAlive()) {
                HttpUtil.setKeepAlive((HttpMessage)response, (boolean)false);
            }
        }
        if (msg instanceof LastHttpContent && !this.shouldKeepAlive()) {
            promise = promise.unvoid().addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
        super.write((ChannelHandlerContext)ctx, (Object)msg, (ChannelPromise)promise);
    }

    private void trackResponse(HttpResponse response) {
        if (HttpServerKeepAliveHandler.isInformational((HttpResponse)response)) return;
        --this.pendingResponses;
    }

    private boolean shouldKeepAlive() {
        if (this.pendingResponses != 0) return true;
        if (this.persistentConnection) return true;
        return false;
    }

    private static boolean isSelfDefinedMessageLength(HttpResponse response) {
        if (HttpUtil.isContentLengthSet((HttpMessage)response)) return true;
        if (HttpUtil.isTransferEncodingChunked((HttpMessage)response)) return true;
        if (HttpServerKeepAliveHandler.isMultipart((HttpResponse)response)) return true;
        if (HttpServerKeepAliveHandler.isInformational((HttpResponse)response)) return true;
        if (response.status().code() == HttpResponseStatus.NO_CONTENT.code()) return true;
        return false;
    }

    private static boolean isInformational(HttpResponse response) {
        if (response.status().codeClass() != HttpStatusClass.INFORMATIONAL) return false;
        return true;
    }

    private static boolean isMultipart(HttpResponse response) {
        String contentType = response.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
        if (contentType == null) return false;
        if (!contentType.regionMatches((boolean)true, (int)0, (String)MULTIPART_PREFIX, (int)0, (int)MULTIPART_PREFIX.length())) return false;
        return true;
    }
}

