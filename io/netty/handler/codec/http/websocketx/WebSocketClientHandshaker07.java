/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocket07FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket07FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.URI;
import java.nio.charset.Charset;

public class WebSocketClientHandshaker07
extends WebSocketClientHandshaker {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker07.class);
    public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private String expectedChallengeResponseString;
    private final boolean allowExtensions;
    private final boolean performMasking;
    private final boolean allowMaskMismatch;

    public WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)true, (boolean)false);
    }

    public WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)10000L);
    }

    public WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)forceCloseTimeoutMillis, (boolean)false);
    }

    WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
        super((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)forceCloseTimeoutMillis, (boolean)absoluteUpgradeUrl);
        this.allowExtensions = allowExtensions;
        this.performMasking = performMasking;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        String expectedSubprotocol;
        URI wsURL = this.uri();
        byte[] nonce = WebSocketUtil.randomBytes((int)16);
        String key = WebSocketUtil.base64((byte[])nonce);
        String acceptSeed = key + MAGIC_GUID;
        byte[] sha1 = WebSocketUtil.sha1((byte[])acceptSeed.getBytes((Charset)CharsetUtil.US_ASCII));
        this.expectedChallengeResponseString = WebSocketUtil.base64((byte[])sha1);
        if (logger.isDebugEnabled()) {
            logger.debug((String)"WebSocket version 07 client handshake key: {}, expected response: {}", (Object)key, (Object)this.expectedChallengeResponseString);
        }
        DefaultFullHttpRequest request = new DefaultFullHttpRequest((HttpVersion)HttpVersion.HTTP_1_1, (HttpMethod)HttpMethod.GET, (String)this.upgradeUrl((URI)wsURL), (ByteBuf)Unpooled.EMPTY_BUFFER);
        HttpHeaders headers = request.headers();
        if (this.customHeaders != null) {
            headers.add((HttpHeaders)this.customHeaders);
        }
        headers.set((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET).set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE).set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY, (Object)key).set((CharSequence)HttpHeaderNames.HOST, (Object)WebSocketClientHandshaker07.websocketHostValue((URI)wsURL));
        if (!headers.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN)) {
            headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, (Object)WebSocketClientHandshaker07.websocketOriginValue((URI)wsURL));
        }
        if ((expectedSubprotocol = this.expectedSubprotocol()) != null && !expectedSubprotocol.isEmpty()) {
            headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)expectedSubprotocol);
        }
        headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, (Object)"7");
        return request;
    }

    @Override
    protected void verify(FullHttpResponse response) {
        HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
        HttpHeaders headers = response.headers();
        if (!response.status().equals((Object)status)) {
            throw new WebSocketHandshakeException((String)("Invalid handshake response getStatus: " + response.status()));
        }
        String upgrade = headers.get((CharSequence)HttpHeaderNames.UPGRADE);
        if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase((CharSequence)upgrade)) {
            throw new WebSocketHandshakeException((String)("Invalid handshake response upgrade: " + upgrade));
        }
        if (!headers.containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, (boolean)true)) {
            throw new WebSocketHandshakeException((String)("Invalid handshake response connection: " + headers.get((CharSequence)HttpHeaderNames.CONNECTION)));
        }
        String accept = headers.get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
        if (accept == null) throw new WebSocketHandshakeException((String)String.format((String)"Invalid challenge. Actual: %s. Expected: %s", (Object[])new Object[]{accept, this.expectedChallengeResponseString}));
        if (accept.equals((Object)this.expectedChallengeResponseString)) return;
        throw new WebSocketHandshakeException((String)String.format((String)"Invalid challenge. Actual: %s. Expected: %s", (Object[])new Object[]{accept, this.expectedChallengeResponseString}));
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket07FrameDecoder((boolean)false, (boolean)this.allowExtensions, (int)this.maxFramePayloadLength(), (boolean)this.allowMaskMismatch);
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket07FrameEncoder((boolean)this.performMasking);
    }

    @Override
    public WebSocketClientHandshaker07 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
        super.setForceCloseTimeoutMillis((long)forceCloseTimeoutMillis);
        return this;
    }
}

