/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class WebSocketServerHandshaker {
    protected static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker.class);
    private final String uri;
    private final String[] subprotocols;
    private final WebSocketVersion version;
    private final WebSocketDecoderConfig decoderConfig;
    private String selectedSubprotocol;
    public static final String SUB_PROTOCOL_WILDCARD = "*";

    protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, int maxFramePayloadLength) {
        this((WebSocketVersion)version, (String)uri, (String)subprotocols, (WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().maxFramePayloadLength((int)maxFramePayloadLength).build());
    }

    protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, WebSocketDecoderConfig decoderConfig) {
        this.version = version;
        this.uri = uri;
        if (subprotocols != null) {
            String[] subprotocolArray = subprotocols.split((String)",");
            for (int i = 0; i < subprotocolArray.length; ++i) {
                subprotocolArray[i] = subprotocolArray[i].trim();
            }
            this.subprotocols = subprotocolArray;
        } else {
            this.subprotocols = EmptyArrays.EMPTY_STRINGS;
        }
        this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, (String)"decoderConfig");
    }

    public String uri() {
        return this.uri;
    }

    public Set<String> subprotocols() {
        LinkedHashSet<String> ret = new LinkedHashSet<String>();
        Collections.addAll(ret, this.subprotocols);
        return ret;
    }

    public WebSocketVersion version() {
        return this.version;
    }

    public int maxFramePayloadLength() {
        return this.decoderConfig.maxFramePayloadLength();
    }

    public WebSocketDecoderConfig decoderConfig() {
        return this.decoderConfig;
    }

    public ChannelFuture handshake(Channel channel, FullHttpRequest req) {
        return this.handshake((Channel)channel, (FullHttpRequest)req, null, (ChannelPromise)channel.newPromise());
    }

    public final ChannelFuture handshake(Channel channel, FullHttpRequest req, HttpHeaders responseHeaders, ChannelPromise promise) {
        ChannelHandlerContext ctx;
        String encoderName;
        if (logger.isDebugEnabled()) {
            logger.debug((String)"{} WebSocket version {} server handshake", (Object)channel, (Object)((Object)this.version()));
        }
        FullHttpResponse response = this.newHandshakeResponse((FullHttpRequest)req, (HttpHeaders)responseHeaders);
        ChannelPipeline p = channel.pipeline();
        if (p.get(HttpObjectAggregator.class) != null) {
            p.remove(HttpObjectAggregator.class);
        }
        if (p.get(HttpContentCompressor.class) != null) {
            p.remove(HttpContentCompressor.class);
        }
        if ((ctx = p.context(HttpRequestDecoder.class)) == null) {
            ctx = p.context(HttpServerCodec.class);
            if (ctx == null) {
                promise.setFailure((Throwable)new IllegalStateException((String)"No HttpDecoder and no HttpServerCodec in the pipeline"));
                return promise;
            }
            p.addBefore((String)ctx.name(), (String)"wsencoder", (ChannelHandler)this.newWebSocketEncoder());
            p.addBefore((String)ctx.name(), (String)"wsdecoder", (ChannelHandler)this.newWebsocketDecoder());
            encoderName = ctx.name();
        } else {
            p.replace((String)ctx.name(), (String)"wsdecoder", (ChannelHandler)this.newWebsocketDecoder());
            encoderName = p.context(HttpResponseEncoder.class).name();
            p.addBefore((String)encoderName, (String)"wsencoder", (ChannelHandler)this.newWebSocketEncoder());
        }
        channel.writeAndFlush((Object)response).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((WebSocketServerHandshaker)this, (String)encoderName, (ChannelPromise)promise){
            final /* synthetic */ String val$encoderName;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ WebSocketServerHandshaker this$0;
            {
                this.this$0 = this$0;
                this.val$encoderName = string;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                if (future.isSuccess()) {
                    ChannelPipeline p = future.channel().pipeline();
                    p.remove((String)this.val$encoderName);
                    this.val$promise.setSuccess();
                    return;
                }
                this.val$promise.setFailure((Throwable)future.cause());
            }
        });
        return promise;
    }

    public ChannelFuture handshake(Channel channel, HttpRequest req) {
        return this.handshake((Channel)channel, (HttpRequest)req, null, (ChannelPromise)channel.newPromise());
    }

    public final ChannelFuture handshake(Channel channel, HttpRequest req, HttpHeaders responseHeaders, ChannelPromise promise) {
        ChannelPipeline p;
        ChannelHandlerContext ctx;
        if (req instanceof FullHttpRequest) {
            return this.handshake((Channel)channel, (FullHttpRequest)((FullHttpRequest)req), (HttpHeaders)responseHeaders, (ChannelPromise)promise);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((String)"{} WebSocket version {} server handshake", (Object)channel, (Object)((Object)this.version()));
        }
        if ((ctx = (p = channel.pipeline()).context(HttpRequestDecoder.class)) == null && (ctx = p.context(HttpServerCodec.class)) == null) {
            promise.setFailure((Throwable)new IllegalStateException((String)"No HttpDecoder and no HttpServerCodec in the pipeline"));
            return promise;
        }
        String aggregatorName = "httpAggregator";
        p.addAfter((String)ctx.name(), (String)aggregatorName, (ChannelHandler)new HttpObjectAggregator((int)8192));
        p.addAfter((String)aggregatorName, (String)"handshaker", (ChannelHandler)new SimpleChannelInboundHandler<FullHttpRequest>((WebSocketServerHandshaker)this, (Channel)channel, (HttpHeaders)responseHeaders, (ChannelPromise)promise){
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ HttpHeaders val$responseHeaders;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ WebSocketServerHandshaker this$0;
            {
                this.this$0 = this$0;
                this.val$channel = channel;
                this.val$responseHeaders = httpHeaders;
                this.val$promise = channelPromise;
            }

            protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws java.lang.Exception {
                ctx.pipeline().remove((ChannelHandler)this);
                this.this$0.handshake((Channel)this.val$channel, (FullHttpRequest)msg, (HttpHeaders)this.val$responseHeaders, (ChannelPromise)this.val$promise);
            }

            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws java.lang.Exception {
                ctx.pipeline().remove((ChannelHandler)this);
                this.val$promise.tryFailure((Throwable)cause);
                ctx.fireExceptionCaught((Throwable)cause);
            }

            public void channelInactive(ChannelHandlerContext ctx) throws java.lang.Exception {
                if (!this.val$promise.isDone()) {
                    this.val$promise.tryFailure((Throwable)new java.nio.channels.ClosedChannelException());
                }
                ctx.fireChannelInactive();
            }
        });
        try {
            ctx.fireChannelRead((Object)ReferenceCountUtil.retain(req));
            return promise;
        }
        catch (Throwable cause) {
            promise.setFailure((Throwable)cause);
        }
        return promise;
    }

    protected abstract FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2);

    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
        if (channel != null) return this.close((Channel)channel, (CloseWebSocketFrame)frame, (ChannelPromise)channel.newPromise());
        throw new NullPointerException((String)"channel");
    }

    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
        if (channel != null) return channel.writeAndFlush((Object)frame, (ChannelPromise)promise).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        throw new NullPointerException((String)"channel");
    }

    protected String selectSubprotocol(String requestedSubprotocols) {
        String[] requestedSubprotocolArray;
        if (requestedSubprotocols == null) return null;
        if (this.subprotocols.length == 0) {
            return null;
        }
        String[] arrstring = requestedSubprotocolArray = requestedSubprotocols.split((String)",");
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String p = arrstring[n2];
            String requestedSubprotocol = p.trim();
            for (String supportedSubprotocol : this.subprotocols) {
                if (!"*".equals((Object)supportedSubprotocol) && !requestedSubprotocol.equals((Object)supportedSubprotocol)) continue;
                this.selectedSubprotocol = requestedSubprotocol;
                return requestedSubprotocol;
            }
            ++n2;
        }
        return null;
    }

    public String selectedSubprotocol() {
        return this.selectedSubprotocol;
    }

    protected abstract WebSocketFrameDecoder newWebsocketDecoder();

    protected abstract WebSocketFrameEncoder newWebSocketEncoder();
}

