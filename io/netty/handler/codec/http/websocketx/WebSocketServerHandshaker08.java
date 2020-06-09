/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import java.nio.charset.Charset;

public class WebSocketServerHandshaker08
extends WebSocketServerHandshaker {
    public static final String WEBSOCKET_08_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public WebSocketServerHandshaker08(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength) {
        this((String)webSocketURL, (String)subprotocols, (boolean)allowExtensions, (int)maxFramePayloadLength, (boolean)false);
    }

    public WebSocketServerHandshaker08(String webSocketURL, String subprotocols, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        this((String)webSocketURL, (String)subprotocols, (WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().allowExtensions((boolean)allowExtensions).maxFramePayloadLength((int)maxFramePayloadLength).allowMaskMismatch((boolean)allowMaskMismatch).build());
    }

    public WebSocketServerHandshaker08(String webSocketURL, String subprotocols, WebSocketDecoderConfig decoderConfig) {
        super((WebSocketVersion)WebSocketVersion.V08, (String)webSocketURL, (String)subprotocols, (WebSocketDecoderConfig)decoderConfig);
    }

    @Override
    protected FullHttpResponse newHandshakeResponse(FullHttpRequest req, HttpHeaders headers) {
        String key = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY);
        if (key == null) {
            throw new WebSocketHandshakeException((String)"not a WebSocket request: missing key");
        }
        DefaultFullHttpResponse res = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.SWITCHING_PROTOCOLS, (ByteBuf)req.content().alloc().buffer((int)0));
        if (headers != null) {
            res.headers().add((HttpHeaders)headers);
        }
        String acceptSeed = key + WEBSOCKET_08_ACCEPT_GUID;
        byte[] sha1 = WebSocketUtil.sha1((byte[])acceptSeed.getBytes((Charset)CharsetUtil.US_ASCII));
        String accept = WebSocketUtil.base64((byte[])sha1);
        if (logger.isDebugEnabled()) {
            logger.debug((String)"WebSocket version 08 server handshake key: {}, response: {}", (Object)key, (Object)accept);
        }
        res.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET);
        res.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
        res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT, (Object)accept);
        String subprotocols = req.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
        if (subprotocols == null) return res;
        String selectedSubprotocol = this.selectSubprotocol((String)subprotocols);
        if (selectedSubprotocol == null) {
            if (!logger.isDebugEnabled()) return res;
            logger.debug((String)"Requested subprotocol(s) not supported: {}", (Object)subprotocols);
            return res;
        }
        res.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)selectedSubprotocol);
        return res;
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket08FrameDecoder((WebSocketDecoderConfig)this.decoderConfig());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket08FrameEncoder((boolean)false);
    }
}

