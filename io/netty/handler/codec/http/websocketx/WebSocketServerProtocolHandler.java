/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.Utf8FrameValidator;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandshakeHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class WebSocketServerProtocolHandler
extends WebSocketProtocolHandler {
    private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, (String)"HANDSHAKER");
    private static final long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10000L;
    private final String websocketPath;
    private final String subprotocols;
    private final boolean checkStartsWith;
    private final long handshakeTimeoutMillis;
    private final WebSocketDecoderConfig decoderConfig;

    public WebSocketServerProtocolHandler(String websocketPath) {
        this((String)websocketPath, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, long handshakeTimeoutMillis) {
        this((String)websocketPath, (boolean)false, (long)handshakeTimeoutMillis);
    }

    public WebSocketServerProtocolHandler(String websocketPath, boolean checkStartsWith) {
        this((String)websocketPath, (boolean)checkStartsWith, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, boolean checkStartsWith, long handshakeTimeoutMillis) {
        this((String)websocketPath, null, (boolean)false, (int)65536, (boolean)false, (boolean)checkStartsWith, (long)handshakeTimeoutMillis);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols) {
        this((String)websocketPath, (String)subprotocols, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, long handshakeTimeoutMillis) {
        this((String)websocketPath, (String)subprotocols, (boolean)false, (long)handshakeTimeoutMillis);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, long handshakeTimeoutMillis) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)65536, (long)handshakeTimeoutMillis);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)maxFrameSize, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, long handshakeTimeoutMillis) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)maxFrameSize, (boolean)false, (long)handshakeTimeoutMillis);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)maxFrameSize, (boolean)allowMaskMismatch, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, long handshakeTimeoutMillis) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)maxFrameSize, (boolean)allowMaskMismatch, (boolean)false, (long)handshakeTimeoutMillis);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)maxFrameSize, (boolean)allowMaskMismatch, (boolean)checkStartsWith, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith, long handshakeTimeoutMillis) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)maxFrameSize, (boolean)allowMaskMismatch, (boolean)checkStartsWith, (boolean)true, (long)handshakeTimeoutMillis);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith, boolean dropPongFrames) {
        this((String)websocketPath, (String)subprotocols, (boolean)allowExtensions, (int)maxFrameSize, (boolean)allowMaskMismatch, (boolean)checkStartsWith, (boolean)dropPongFrames, (long)10000L);
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith, boolean dropPongFrames, long handshakeTimeoutMillis) {
        this((String)websocketPath, (String)subprotocols, (boolean)checkStartsWith, (boolean)dropPongFrames, (long)handshakeTimeoutMillis, (WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().maxFramePayloadLength((int)maxFrameSize).allowMaskMismatch((boolean)allowMaskMismatch).allowExtensions((boolean)allowExtensions).build());
    }

    public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean checkStartsWith, boolean dropPongFrames, long handshakeTimeoutMillis, WebSocketDecoderConfig decoderConfig) {
        super((boolean)dropPongFrames);
        this.websocketPath = websocketPath;
        this.subprotocols = subprotocols;
        this.checkStartsWith = checkStartsWith;
        this.handshakeTimeoutMillis = ObjectUtil.checkPositive((long)handshakeTimeoutMillis, (String)"handshakeTimeoutMillis");
        this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, (String)"decoderConfig");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
            cp.addBefore((String)ctx.name(), (String)WebSocketServerProtocolHandshakeHandler.class.getName(), (ChannelHandler)new WebSocketServerProtocolHandshakeHandler((String)this.websocketPath, (String)this.subprotocols, (boolean)this.checkStartsWith, (long)this.handshakeTimeoutMillis, (WebSocketDecoderConfig)this.decoderConfig));
        }
        if (!this.decoderConfig.withUTF8Validator()) return;
        if (cp.get(Utf8FrameValidator.class) != null) return;
        cp.addBefore((String)ctx.name(), (String)Utf8FrameValidator.class.getName(), (ChannelHandler)new Utf8FrameValidator());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (!(frame instanceof CloseWebSocketFrame)) {
            super.decode((ChannelHandlerContext)ctx, (WebSocketFrame)frame, out);
            return;
        }
        WebSocketServerHandshaker handshaker = WebSocketServerProtocolHandler.getHandshaker((Channel)ctx.channel());
        if (handshaker != null) {
            frame.retain();
            handshaker.close((Channel)ctx.channel(), (CloseWebSocketFrame)((CloseWebSocketFrame)frame));
            return;
        }
        ctx.writeAndFlush((Object)Unpooled.EMPTY_BUFFER).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof WebSocketHandshakeException) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.BAD_REQUEST, (ByteBuf)Unpooled.wrappedBuffer((byte[])cause.getMessage().getBytes()));
            ctx.channel().writeAndFlush((Object)response).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
            return;
        }
        ctx.fireExceptionCaught((Throwable)cause);
        ctx.close();
    }

    static WebSocketServerHandshaker getHandshaker(Channel channel) {
        return channel.attr(HANDSHAKER_ATTR_KEY).get();
    }

    static void setHandshaker(Channel channel, WebSocketServerHandshaker handshaker) {
        channel.attr(HANDSHAKER_ATTR_KEY).set((WebSocketServerHandshaker)handshaker);
    }

    static ChannelHandler forbiddenHttpRequestResponder() {
        return new ChannelInboundHandlerAdapter(){

            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof io.netty.handler.codec.http.FullHttpRequest) {
                    ((io.netty.handler.codec.http.FullHttpRequest)msg).release();
                    DefaultFullHttpResponse response = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.FORBIDDEN, (ByteBuf)ctx.alloc().buffer((int)0));
                    ctx.channel().writeAndFlush((Object)response);
                    return;
                }
                ctx.fireChannelRead((Object)msg);
            }
        };
    }
}

