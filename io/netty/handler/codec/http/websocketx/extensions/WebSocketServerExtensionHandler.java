/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionUtil;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WebSocketServerExtensionHandler
extends ChannelDuplexHandler {
    private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
    private List<WebSocketServerExtension> validExtensions;

    public WebSocketServerExtensionHandler(WebSocketServerExtensionHandshaker ... extensionHandshakers) {
        if (extensionHandshakers == null) {
            throw new NullPointerException((String)"extensionHandshakers");
        }
        if (extensionHandshakers.length == 0) {
            throw new IllegalArgumentException((String)"extensionHandshakers must contains at least one handshaker");
        }
        this.extensionHandshakers = Arrays.asList(extensionHandshakers);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest request;
        String extensionsHeader;
        if (msg instanceof HttpRequest && WebSocketExtensionUtil.isWebsocketUpgrade((HttpHeaders)(request = (HttpRequest)msg).headers()) && (extensionsHeader = request.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS)) != null) {
            List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions((String)extensionsHeader);
            int rsv = 0;
            for (WebSocketExtensionData extensionData : extensions) {
                Iterator<WebSocketServerExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
                WebSocketExtension validExtension = null;
                while (validExtension == null && extensionHandshakersIterator.hasNext()) {
                    WebSocketServerExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
                    validExtension = extensionHandshaker.handshakeExtension((WebSocketExtensionData)extensionData);
                }
                if (validExtension == null || (validExtension.rsv() & rsv) != 0) continue;
                if (this.validExtensions == null) {
                    this.validExtensions = new ArrayList<WebSocketServerExtension>((int)1);
                }
                rsv |= validExtension.rsv();
                this.validExtensions.add((WebSocketServerExtension)validExtension);
            }
        }
        super.channelRead((ChannelHandlerContext)ctx, (Object)msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof HttpResponse && WebSocketExtensionUtil.isWebsocketUpgrade((HttpHeaders)((HttpResponse)msg).headers()) && this.validExtensions != null) {
            HttpResponse response = (HttpResponse)msg;
            String headerValue = response.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            for (WebSocketServerExtension extension : this.validExtensions) {
                WebSocketExtensionData extensionData = extension.newReponseData();
                headerValue = WebSocketExtensionUtil.appendExtension((String)headerValue, (String)extensionData.name(), extensionData.parameters());
            }
            promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((WebSocketServerExtensionHandler)this, (ChannelHandlerContext)ctx){
                final /* synthetic */ ChannelHandlerContext val$ctx;
                final /* synthetic */ WebSocketServerExtensionHandler this$0;
                {
                    this.this$0 = this$0;
                    this.val$ctx = channelHandlerContext;
                }

                public void operationComplete(io.netty.channel.ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        for (WebSocketServerExtension extension : WebSocketServerExtensionHandler.access$000((WebSocketServerExtensionHandler)this.this$0)) {
                            io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder decoder = extension.newExtensionDecoder();
                            io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder encoder = extension.newExtensionEncoder();
                            this.val$ctx.pipeline().addAfter((String)this.val$ctx.name(), (String)decoder.getClass().getName(), (io.netty.channel.ChannelHandler)decoder);
                            this.val$ctx.pipeline().addAfter((String)this.val$ctx.name(), (String)encoder.getClass().getName(), (io.netty.channel.ChannelHandler)encoder);
                        }
                    }
                    this.val$ctx.pipeline().remove((String)this.val$ctx.name());
                }
            });
            if (headerValue != null) {
                response.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, (Object)headerValue);
            }
        }
        super.write((ChannelHandlerContext)ctx, (Object)msg, (ChannelPromise)promise);
    }

    static /* synthetic */ List access$000(WebSocketServerExtensionHandler x0) {
        return x0.validExtensions;
    }
}

