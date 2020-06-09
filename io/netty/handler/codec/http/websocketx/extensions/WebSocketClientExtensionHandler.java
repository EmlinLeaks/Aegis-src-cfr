/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionUtil;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WebSocketClientExtensionHandler
extends ChannelDuplexHandler {
    private final List<WebSocketClientExtensionHandshaker> extensionHandshakers;

    public WebSocketClientExtensionHandler(WebSocketClientExtensionHandshaker ... extensionHandshakers) {
        if (extensionHandshakers == null) {
            throw new NullPointerException((String)"extensionHandshakers");
        }
        if (extensionHandshakers.length == 0) {
            throw new IllegalArgumentException((String)"extensionHandshakers must contains at least one handshaker");
        }
        this.extensionHandshakers = Arrays.asList(extensionHandshakers);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof HttpRequest && WebSocketExtensionUtil.isWebsocketUpgrade((HttpHeaders)((HttpRequest)msg).headers())) {
            HttpRequest request = (HttpRequest)msg;
            String headerValue = request.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            for (WebSocketClientExtensionHandshaker extensionHandshaker : this.extensionHandshakers) {
                WebSocketExtensionData extensionData = extensionHandshaker.newRequestData();
                headerValue = WebSocketExtensionUtil.appendExtension((String)headerValue, (String)extensionData.name(), extensionData.parameters());
            }
            request.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, (Object)headerValue);
        }
        super.write((ChannelHandlerContext)ctx, (Object)msg, (ChannelPromise)promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpResponse response;
        if (msg instanceof HttpResponse && WebSocketExtensionUtil.isWebsocketUpgrade((HttpHeaders)(response = (HttpResponse)msg).headers())) {
            String extensionsHeader = response.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            if (extensionsHeader != null) {
                List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions((String)extensionsHeader);
                ArrayList<WebSocketExtension> validExtensions = new ArrayList<WebSocketExtension>((int)extensions.size());
                int rsv = 0;
                for (WebSocketExtensionData extensionData : extensions) {
                    Iterator<WebSocketClientExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
                    WebSocketExtension validExtension = null;
                    while (validExtension == null && extensionHandshakersIterator.hasNext()) {
                        WebSocketClientExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
                        validExtension = extensionHandshaker.handshakeExtension((WebSocketExtensionData)extensionData);
                    }
                    if (validExtension == null) throw new CodecException((String)("invalid WebSocket Extension handshake for \"" + extensionsHeader + '\"'));
                    if ((validExtension.rsv() & rsv) != 0) throw new CodecException((String)("invalid WebSocket Extension handshake for \"" + extensionsHeader + '\"'));
                    rsv |= validExtension.rsv();
                    validExtensions.add(validExtension);
                }
                for (WebSocketClientExtension validExtension : validExtensions) {
                    WebSocketExtensionDecoder decoder = validExtension.newExtensionDecoder();
                    WebSocketExtensionEncoder encoder = validExtension.newExtensionEncoder();
                    ctx.pipeline().addAfter((String)ctx.name(), (String)decoder.getClass().getName(), (ChannelHandler)decoder);
                    ctx.pipeline().addAfter((String)ctx.name(), (String)encoder.getClass().getName(), (ChannelHandler)encoder);
                }
            }
            ctx.pipeline().remove((String)ctx.name());
        }
        super.channelRead((ChannelHandlerContext)ctx, (Object)msg);
    }
}

