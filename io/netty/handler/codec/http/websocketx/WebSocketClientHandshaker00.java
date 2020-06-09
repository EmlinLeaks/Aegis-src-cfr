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
import io.netty.handler.codec.http.websocketx.WebSocket00FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import java.net.URI;
import java.nio.ByteBuffer;

public class WebSocketClientHandshaker00
extends WebSocketClientHandshaker {
    private static final AsciiString WEBSOCKET = AsciiString.cached((String)"WebSocket");
    private ByteBuf expectedChallengeResponseBytes;

    public WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)10000L);
    }

    public WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)forceCloseTimeoutMillis, (boolean)false);
    }

    WebSocketClientHandshaker00(URI webSocketURL, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
        super((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)forceCloseTimeoutMillis, (boolean)absoluteUpgradeUrl);
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        String expectedSubprotocol;
        int spaces1 = WebSocketUtil.randomNumber((int)1, (int)12);
        int spaces2 = WebSocketUtil.randomNumber((int)1, (int)12);
        int max1 = Integer.MAX_VALUE / spaces1;
        int max2 = Integer.MAX_VALUE / spaces2;
        int number1 = WebSocketUtil.randomNumber((int)0, (int)max1);
        int number2 = WebSocketUtil.randomNumber((int)0, (int)max2);
        int product1 = number1 * spaces1;
        int product2 = number2 * spaces2;
        String key1 = Integer.toString((int)product1);
        String key2 = Integer.toString((int)product2);
        key1 = WebSocketClientHandshaker00.insertRandomCharacters((String)key1);
        key2 = WebSocketClientHandshaker00.insertRandomCharacters((String)key2);
        key1 = WebSocketClientHandshaker00.insertSpaces((String)key1, (int)spaces1);
        key2 = WebSocketClientHandshaker00.insertSpaces((String)key2, (int)spaces2);
        byte[] key3 = WebSocketUtil.randomBytes((int)8);
        ByteBuffer buffer = ByteBuffer.allocate((int)4);
        buffer.putInt((int)number1);
        byte[] number1Array = buffer.array();
        buffer = ByteBuffer.allocate((int)4);
        buffer.putInt((int)number2);
        byte[] number2Array = buffer.array();
        byte[] challenge = new byte[16];
        System.arraycopy((Object)number1Array, (int)0, (Object)challenge, (int)0, (int)4);
        System.arraycopy((Object)number2Array, (int)0, (Object)challenge, (int)4, (int)4);
        System.arraycopy((Object)key3, (int)0, (Object)challenge, (int)8, (int)8);
        this.expectedChallengeResponseBytes = Unpooled.wrappedBuffer((byte[])WebSocketUtil.md5((byte[])challenge));
        URI wsURL = this.uri();
        DefaultFullHttpRequest request = new DefaultFullHttpRequest((HttpVersion)HttpVersion.HTTP_1_1, (HttpMethod)HttpMethod.GET, (String)this.upgradeUrl((URI)wsURL), (ByteBuf)Unpooled.wrappedBuffer((byte[])key3));
        HttpHeaders headers = request.headers();
        if (this.customHeaders != null) {
            headers.add((HttpHeaders)this.customHeaders);
        }
        headers.set((CharSequence)HttpHeaderNames.UPGRADE, (Object)WEBSOCKET).set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE).set((CharSequence)HttpHeaderNames.HOST, (Object)WebSocketClientHandshaker00.websocketHostValue((URI)wsURL)).set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1, (Object)key1).set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2, (Object)key2);
        if (!headers.contains((CharSequence)HttpHeaderNames.ORIGIN)) {
            headers.set((CharSequence)HttpHeaderNames.ORIGIN, (Object)WebSocketClientHandshaker00.websocketOriginValue((URI)wsURL));
        }
        if ((expectedSubprotocol = this.expectedSubprotocol()) != null && !expectedSubprotocol.isEmpty()) {
            headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)expectedSubprotocol);
        }
        headers.set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)Integer.valueOf((int)key3.length));
        return request;
    }

    @Override
    protected void verify(FullHttpResponse response) {
        if (!response.status().equals((Object)HttpResponseStatus.SWITCHING_PROTOCOLS)) {
            throw new WebSocketHandshakeException((String)("Invalid handshake response getStatus: " + response.status()));
        }
        HttpHeaders headers = response.headers();
        String upgrade = headers.get((CharSequence)HttpHeaderNames.UPGRADE);
        if (!WEBSOCKET.contentEqualsIgnoreCase((CharSequence)upgrade)) {
            throw new WebSocketHandshakeException((String)("Invalid handshake response upgrade: " + upgrade));
        }
        if (!headers.containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, (boolean)true)) {
            throw new WebSocketHandshakeException((String)("Invalid handshake response connection: " + headers.get((CharSequence)HttpHeaderNames.CONNECTION)));
        }
        ByteBuf challenge = response.content();
        if (challenge.equals((Object)this.expectedChallengeResponseBytes)) return;
        throw new WebSocketHandshakeException((String)"Invalid challenge");
    }

    private static String insertRandomCharacters(String key) {
        int count = WebSocketUtil.randomNumber((int)1, (int)12);
        char[] randomChars = new char[count];
        int randCount = 0;
        while (randCount < count) {
            int rand = (int)(Math.random() * 126.0 + 33.0);
            if ((33 >= rand || rand >= 47) && (58 >= rand || rand >= 126)) continue;
            randomChars[randCount] = (char)rand;
            ++randCount;
        }
        int i = 0;
        while (i < count) {
            int split = WebSocketUtil.randomNumber((int)0, (int)key.length());
            String part1 = key.substring((int)0, (int)split);
            String part2 = key.substring((int)split);
            key = part1 + randomChars[i] + part2;
            ++i;
        }
        return key;
    }

    private static String insertSpaces(String key, int spaces) {
        int i = 0;
        while (i < spaces) {
            int split = WebSocketUtil.randomNumber((int)1, (int)(key.length() - 1));
            String part1 = key.substring((int)0, (int)split);
            String part2 = key.substring((int)split);
            key = part1 + ' ' + part2;
            ++i;
        }
        return key;
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder((int)this.maxFramePayloadLength());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }

    @Override
    public WebSocketClientHandshaker00 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
        super.setForceCloseTimeoutMillis((long)forceCloseTimeoutMillis);
        return this;
    }
}

