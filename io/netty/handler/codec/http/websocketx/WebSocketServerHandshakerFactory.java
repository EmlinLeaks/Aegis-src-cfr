/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker00;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker07;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker08;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;

public class WebSocketServerHandshakerFactory {
    private final String webSocketURL;
    private final String subprotocols;
    private final WebSocketDecoderConfig decoderConfig;

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions) {
        this((String)webSocketURL, (String)subprotocols, (boolean)allowExtensions, (int)65536);
    }

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
        this((String)webSocketURL, (String)subprotocols, (boolean)allowExtensions, (int)maxFramePayloadLength, (boolean)false);
    }

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        this((String)webSocketURL, (String)subprotocols, (WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().allowExtensions((boolean)allowExtensions).maxFramePayloadLength((int)maxFramePayloadLength).allowMaskMismatch((boolean)allowMaskMismatch).build());
    }

    public WebSocketServerHandshakerFactory(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
        this.webSocketURL = webSocketURL;
        this.subprotocols = subprotocols;
        this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, (String)"decoderConfig");
    }

    public WebSocketServerHandshaker newHandshaker(HttpRequest req) {
        String version = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION);
        if (version == null) return new WebSocketServerHandshaker00((String)this.webSocketURL, (String)this.subprotocols, (WebSocketDecoderConfig)this.decoderConfig);
        if (version.equals((Object)WebSocketVersion.V13.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker13((String)this.webSocketURL, (String)this.subprotocols, (WebSocketDecoderConfig)this.decoderConfig);
        }
        if (version.equals((Object)WebSocketVersion.V08.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker08((String)this.webSocketURL, (String)this.subprotocols, (WebSocketDecoderConfig)this.decoderConfig);
        }
        if (!version.equals((Object)WebSocketVersion.V07.toHttpHeaderValue())) return null;
        return new WebSocketServerHandshaker07((String)this.webSocketURL, (String)this.subprotocols, (WebSocketDecoderConfig)this.decoderConfig);
    }

    @Deprecated
    public static void sendUnsupportedWebSocketVersionResponse(Channel channel) {
        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse((Channel)channel);
    }

    public static ChannelFuture sendUnsupportedVersionResponse(Channel channel) {
        return WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse((Channel)channel, (ChannelPromise)channel.newPromise());
    }

    public static ChannelFuture sendUnsupportedVersionResponse(Channel channel, ChannelPromise promise) {
        DefaultFullHttpResponse res = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.UPGRADE_REQUIRED, (ByteBuf)channel.alloc().buffer((int)0));
        res.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, (Object)WebSocketVersion.V13.toHttpHeaderValue());
        HttpUtil.setContentLength((HttpMessage)res, (long)0L);
        return channel.writeAndFlush((Object)res, (ChannelPromise)promise);
    }
}

