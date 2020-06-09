/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandshakeHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketServerProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private final String websocketPath;
    private final String subprotocols;
    private final boolean checkStartsWith;
    private final long handshakeTimeoutMillis;
    private final WebSocketDecoderConfig decoderConfig;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;

    WebSocketServerProtocolHandshakeHandler(String websocketPath, String subprotocols, boolean checkStartsWith, long handshakeTimeoutMillis, WebSocketDecoderConfig decoderConfig) {
        this.websocketPath = websocketPath;
        this.subprotocols = subprotocols;
        this.checkStartsWith = checkStartsWith;
        this.handshakeTimeoutMillis = ObjectUtil.checkPositive((long)handshakeTimeoutMillis, (String)"handshakeTimeoutMillis");
        this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, (String)"decoderConfig");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest)msg;
        if (this.isNotWebSocketPath((FullHttpRequest)req)) {
            ctx.fireChannelRead((Object)msg);
            return;
        }
        try {
            if (!HttpMethod.GET.equals((Object)req.method())) {
                WebSocketServerProtocolHandshakeHandler.sendHttpResponse((ChannelHandlerContext)ctx, (HttpRequest)req, (HttpResponse)new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.FORBIDDEN, (ByteBuf)ctx.alloc().buffer((int)0)));
                return;
            }
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory((String)WebSocketServerProtocolHandshakeHandler.getWebSocketLocation((ChannelPipeline)ctx.pipeline(), (HttpRequest)req, (String)this.websocketPath), (String)this.subprotocols, (WebSocketDecoderConfig)this.decoderConfig);
            WebSocketServerHandshaker handshaker = wsFactory.newHandshaker((HttpRequest)req);
            ChannelPromise localHandshakePromise = this.handshakePromise;
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse((Channel)ctx.channel());
                return;
            }
            WebSocketServerProtocolHandler.setHandshaker((Channel)ctx.channel(), (WebSocketServerHandshaker)handshaker);
            ctx.pipeline().replace((ChannelHandler)this, (String)"WS403Responder", (ChannelHandler)WebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
            ChannelFuture handshakeFuture = handshaker.handshake((Channel)ctx.channel(), (FullHttpRequest)req);
            handshakeFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((WebSocketServerProtocolHandshakeHandler)this, (ChannelPromise)localHandshakePromise, (ChannelHandlerContext)ctx, (FullHttpRequest)req, (WebSocketServerHandshaker)handshaker){
                final /* synthetic */ ChannelPromise val$localHandshakePromise;
                final /* synthetic */ ChannelHandlerContext val$ctx;
                final /* synthetic */ FullHttpRequest val$req;
                final /* synthetic */ WebSocketServerHandshaker val$handshaker;
                final /* synthetic */ WebSocketServerProtocolHandshakeHandler this$0;
                {
                    this.this$0 = this$0;
                    this.val$localHandshakePromise = channelPromise;
                    this.val$ctx = channelHandlerContext;
                    this.val$req = fullHttpRequest;
                    this.val$handshaker = webSocketServerHandshaker;
                }

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        this.val$localHandshakePromise.tryFailure((java.lang.Throwable)future.cause());
                        this.val$ctx.fireExceptionCaught((java.lang.Throwable)future.cause());
                        return;
                    }
                    this.val$localHandshakePromise.trySuccess();
                    this.val$ctx.fireUserEventTriggered((Object)((Object)io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler$ServerHandshakeStateEvent.HANDSHAKE_COMPLETE));
                    this.val$ctx.fireUserEventTriggered((Object)new io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler$HandshakeComplete((String)this.val$req.uri(), (HttpHeaders)this.val$req.headers(), (String)this.val$handshaker.selectedSubprotocol()));
                }
            });
            this.applyHandshakeTimeout();
            return;
        }
        finally {
            req.release();
        }
    }

    private boolean isNotWebSocketPath(FullHttpRequest req) {
        if (this.checkStartsWith) {
            if (req.uri().startsWith((String)this.websocketPath)) return false;
            return true;
        }
        if (req.uri().equals((Object)this.websocketPath)) return false;
        return true;
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush((Object)res);
        if (HttpUtil.isKeepAlive((HttpMessage)req)) {
            if (res.status().code() == 200) return;
        }
        f.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }
        String host = req.headers().get((CharSequence)HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }

    private void applyHandshakeTimeout() {
        ChannelPromise localHandshakePromise = this.handshakePromise;
        long handshakeTimeoutMillis = this.handshakeTimeoutMillis;
        if (handshakeTimeoutMillis <= 0L) return;
        if (localHandshakePromise.isDone()) {
            return;
        }
        ScheduledFuture<?> timeoutFuture = this.ctx.executor().schedule((Runnable)new Runnable((WebSocketServerProtocolHandshakeHandler)this, (ChannelPromise)localHandshakePromise){
            final /* synthetic */ ChannelPromise val$localHandshakePromise;
            final /* synthetic */ WebSocketServerProtocolHandshakeHandler this$0;
            {
                this.this$0 = this$0;
                this.val$localHandshakePromise = channelPromise;
            }

            public void run() {
                if (this.val$localHandshakePromise.isDone()) return;
                if (!this.val$localHandshakePromise.tryFailure((java.lang.Throwable)new io.netty.handler.codec.http.websocketx.WebSocketHandshakeException((String)"handshake timed out"))) return;
                WebSocketServerProtocolHandshakeHandler.access$000((WebSocketServerProtocolHandshakeHandler)this.this$0).flush().fireUserEventTriggered((Object)((Object)io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler$ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT)).close();
            }
        }, (long)handshakeTimeoutMillis, (TimeUnit)TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)new FutureListener<Void>((WebSocketServerProtocolHandshakeHandler)this, timeoutFuture){
            final /* synthetic */ Future val$timeoutFuture;
            final /* synthetic */ WebSocketServerProtocolHandshakeHandler this$0;
            {
                this.this$0 = this$0;
                this.val$timeoutFuture = future;
            }

            public void operationComplete(Future<Void> f) throws Exception {
                this.val$timeoutFuture.cancel((boolean)false);
            }
        });
    }

    static /* synthetic */ ChannelHandlerContext access$000(WebSocketServerProtocolHandshakeHandler x0) {
        return x0.ctx;
    }
}

