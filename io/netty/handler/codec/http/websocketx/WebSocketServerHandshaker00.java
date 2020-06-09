/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocket00FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import io.netty.util.internal.logging.InternalLogger;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketServerHandshaker00
extends WebSocketServerHandshaker {
    private static final Pattern BEGINNING_DIGIT = Pattern.compile((String)"[^0-9]");
    private static final Pattern BEGINNING_SPACE = Pattern.compile((String)"[^ ]");

    public WebSocketServerHandshaker00(String webSocketURL, String subprotocols, int maxFramePayloadLength) {
        this((String)webSocketURL, (String)subprotocols, (WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().maxFramePayloadLength((int)maxFramePayloadLength).build());
    }

    public WebSocketServerHandshaker00(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
        super((WebSocketVersion)WebSocketVersion.V00, (String)webSocketURL, (String)subprotocols, (WebSocketDecoderConfig)decoderConfig);
    }

    @Override
    protected FullHttpResponse newHandshakeResponse(FullHttpRequest req, HttpHeaders headers) {
        if (!req.headers().containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, (boolean)true)) throw new WebSocketHandshakeException((String)"not a WebSocket handshake request: missing upgrade");
        if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase((CharSequence)req.headers().get((CharSequence)HttpHeaderNames.UPGRADE))) {
            throw new WebSocketHandshakeException((String)"not a WebSocket handshake request: missing upgrade");
        }
        boolean isHixie76 = req.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1) && req.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2);
        String origin = req.headers().get((CharSequence)HttpHeaderNames.ORIGIN);
        if (origin == null && !isHixie76) {
            throw new WebSocketHandshakeException((String)("Missing origin header, got only " + req.headers().names()));
        }
        DefaultFullHttpResponse res = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)new HttpResponseStatus((int)101, (String)(isHixie76 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake")), (ByteBuf)req.content().alloc().buffer((int)0));
        if (headers != null) {
            res.headers().add((HttpHeaders)headers);
        }
        res.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET);
        res.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
        if (!isHixie76) {
            res.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_ORIGIN, (Object)origin);
            res.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_LOCATION, (Object)this.uri());
            String protocol = req.headers().get((CharSequence)HttpHeaderNames.WEBSOCKET_PROTOCOL);
            if (protocol == null) return res;
            res.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_PROTOCOL, (Object)this.selectSubprotocol((String)protocol));
            return res;
        }
        res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, (Object)origin);
        res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_LOCATION, (Object)this.uri());
        String subprotocols = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
        if (subprotocols != null) {
            String selectedSubprotocol = this.selectSubprotocol((String)subprotocols);
            if (selectedSubprotocol == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug((String)"Requested subprotocol(s) not supported: {}", (Object)subprotocols);
                }
            } else {
                res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)selectedSubprotocol);
            }
        }
        String key1 = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1);
        String key2 = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2);
        int a = (int)(Long.parseLong((String)BEGINNING_DIGIT.matcher((CharSequence)key1).replaceAll((String)"")) / (long)BEGINNING_SPACE.matcher((CharSequence)key1).replaceAll((String)"").length());
        int b = (int)(Long.parseLong((String)BEGINNING_DIGIT.matcher((CharSequence)key2).replaceAll((String)"")) / (long)BEGINNING_SPACE.matcher((CharSequence)key2).replaceAll((String)"").length());
        long c = req.content().readLong();
        ByteBuf input = Unpooled.wrappedBuffer((byte[])new byte[16]).setIndex((int)0, (int)0);
        input.writeInt((int)a);
        input.writeInt((int)b);
        input.writeLong((long)c);
        res.content().writeBytes((byte[])WebSocketUtil.md5((byte[])input.array()));
        return res;
    }

    @Override
    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
        return channel.writeAndFlush((Object)frame, (ChannelPromise)promise);
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder((WebSocketDecoderConfig)this.decoderConfig());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }
}

