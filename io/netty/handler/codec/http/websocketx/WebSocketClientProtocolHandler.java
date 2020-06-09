/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.Utf8FrameValidator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandshakeHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.internal.ObjectUtil;
import java.net.URI;
import java.util.List;

public class WebSocketClientProtocolHandler
extends WebSocketProtocolHandler {
    private static final long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10000L;
    private final WebSocketClientHandshaker handshaker;
    private final boolean handleCloseFrames;
    private final long handshakeTimeoutMillis;

    public WebSocketClientHandshaker handshaker() {
        return this.handshaker;
    }

    public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames, boolean performMasking, boolean allowMaskMismatch) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)handleCloseFrames, (boolean)performMasking, (boolean)allowMaskMismatch, (long)10000L);
    }

    public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames, boolean performMasking, boolean allowMaskMismatch, long handshakeTimeoutMillis) {
        this((WebSocketClientHandshaker)WebSocketClientHandshakerFactory.newHandshaker((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)performMasking, (boolean)allowMaskMismatch), (boolean)handleCloseFrames, (long)handshakeTimeoutMillis);
    }

    public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)handleCloseFrames, (long)10000L);
    }

    public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean handleCloseFrames, long handshakeTimeoutMillis) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)handleCloseFrames, (boolean)true, (boolean)false, (long)handshakeTimeoutMillis);
    }

    public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)10000L);
    }

    public WebSocketClientProtocolHandler(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, long handshakeTimeoutMillis) {
        this((URI)webSocketURL, (WebSocketVersion)version, (String)subprotocol, (boolean)allowExtensions, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (boolean)true, (long)handshakeTimeoutMillis);
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames) {
        this((WebSocketClientHandshaker)handshaker, (boolean)handleCloseFrames, (long)10000L);
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames, long handshakeTimeoutMillis) {
        this((WebSocketClientHandshaker)handshaker, (boolean)handleCloseFrames, (boolean)true, (long)handshakeTimeoutMillis);
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames, boolean dropPongFrames) {
        this((WebSocketClientHandshaker)handshaker, (boolean)handleCloseFrames, (boolean)dropPongFrames, (long)10000L);
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, boolean handleCloseFrames, boolean dropPongFrames, long handshakeTimeoutMillis) {
        super((boolean)dropPongFrames);
        this.handshaker = handshaker;
        this.handleCloseFrames = handleCloseFrames;
        this.handshakeTimeoutMillis = ObjectUtil.checkPositive((long)handshakeTimeoutMillis, (String)"handshakeTimeoutMillis");
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker) {
        this((WebSocketClientHandshaker)handshaker, (long)10000L);
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker handshaker, long handshakeTimeoutMillis) {
        this((WebSocketClientHandshaker)handshaker, (boolean)true, (long)handshakeTimeoutMillis);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (this.handleCloseFrames && frame instanceof CloseWebSocketFrame) {
            ctx.close();
            return;
        }
        super.decode((ChannelHandlerContext)ctx, (WebSocketFrame)frame, out);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketClientProtocolHandshakeHandler.class) == null) {
            ctx.pipeline().addBefore((String)ctx.name(), (String)WebSocketClientProtocolHandshakeHandler.class.getName(), (ChannelHandler)new WebSocketClientProtocolHandshakeHandler((WebSocketClientHandshaker)this.handshaker, (long)this.handshakeTimeoutMillis));
        }
        if (cp.get(Utf8FrameValidator.class) != null) return;
        ctx.pipeline().addBefore((String)ctx.name(), (String)Utf8FrameValidator.class.getName(), (ChannelHandler)new Utf8FrameValidator());
    }
}

