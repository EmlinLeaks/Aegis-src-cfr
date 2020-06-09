/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker00;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker07;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker08;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import java.net.URI;

public final class WebSocketClientHandshakerFactory {
    private WebSocketClientHandshakerFactory() {
    }

    public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders) {
        return WebSocketClientHandshakerFactory.newHandshaker((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)65536);
    }

    public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
        return WebSocketClientHandshakerFactory.newHandshaker((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)true, (boolean)false);
    }

    public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
        return WebSocketClientHandshakerFactory.newHandshaker((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)-1L);
    }

    public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
        if (version == WebSocketVersion.V13) {
            return new WebSocketClientHandshaker13((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V13, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)forceCloseTimeoutMillis);
        }
        if (version == WebSocketVersion.V08) {
            return new WebSocketClientHandshaker08((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V08, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)forceCloseTimeoutMillis);
        }
        if (version == WebSocketVersion.V07) {
            return new WebSocketClientHandshaker07((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V07, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)forceCloseTimeoutMillis);
        }
        if (version != WebSocketVersion.V00) throw new WebSocketHandshakeException((String)("Protocol version " + (Object)((Object)version) + " not supported."));
        return new WebSocketClientHandshaker00((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V00, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)forceCloseTimeoutMillis);
    }

    public static WebSocketClientHandshaker newHandshaker(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
        if (version == WebSocketVersion.V13) {
            return new WebSocketClientHandshaker13((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V13, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)forceCloseTimeoutMillis, (boolean)absoluteUpgradeUrl);
        }
        if (version == WebSocketVersion.V08) {
            return new WebSocketClientHandshaker08((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V08, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)forceCloseTimeoutMillis, (boolean)absoluteUpgradeUrl);
        }
        if (version == WebSocketVersion.V07) {
            return new WebSocketClientHandshaker07((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V07, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch, (long)forceCloseTimeoutMillis, (boolean)absoluteUpgradeUrl);
        }
        if (version != WebSocketVersion.V00) throw new WebSocketHandshakeException((String)("Protocol version " + (Object)((Object)version) + " not supported."));
        return new WebSocketClientHandshaker00((URI)webSocketURL, (WebSocketVersion)WebSocketVersion.V00, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)forceCloseTimeoutMillis, (boolean)absoluteUpgradeUrl);
    }
}

