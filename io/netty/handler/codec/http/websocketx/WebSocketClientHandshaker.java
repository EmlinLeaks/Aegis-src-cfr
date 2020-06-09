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
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketScheme;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.URI;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class WebSocketClientHandshaker {
    private static final String HTTP_SCHEME_PREFIX = HttpScheme.HTTP + "://";
    private static final String HTTPS_SCHEME_PREFIX = HttpScheme.HTTPS + "://";
    protected static final int DEFAULT_FORCE_CLOSE_TIMEOUT_MILLIS = 10000;
    private final URI uri;
    private final WebSocketVersion version;
    private volatile boolean handshakeComplete;
    private volatile long forceCloseTimeoutMillis = 10000L;
    private volatile int forceCloseInit;
    private static final AtomicIntegerFieldUpdater<WebSocketClientHandshaker> FORCE_CLOSE_INIT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(WebSocketClientHandshaker.class, (String)"forceCloseInit");
    private volatile boolean forceCloseComplete;
    private final String expectedSubprotocol;
    private volatile String actualSubprotocol;
    protected final HttpHeaders customHeaders;
    private final int maxFramePayloadLength;
    private final boolean absoluteUpgradeUrl;

    protected WebSocketClientHandshaker(URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength) {
        this((URI)uri, (WebSocketVersion)version, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)10000L);
    }

    protected WebSocketClientHandshaker(URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis) {
        this((URI)uri, (WebSocketVersion)version, (String)subprotocol, (HttpHeaders)customHeaders, (int)maxFramePayloadLength, (long)forceCloseTimeoutMillis, (boolean)false);
    }

    protected WebSocketClientHandshaker(URI uri, WebSocketVersion version, String subprotocol, HttpHeaders customHeaders, int maxFramePayloadLength, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
        this.uri = uri;
        this.version = version;
        this.expectedSubprotocol = subprotocol;
        this.customHeaders = customHeaders;
        this.maxFramePayloadLength = maxFramePayloadLength;
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
        this.absoluteUpgradeUrl = absoluteUpgradeUrl;
    }

    public URI uri() {
        return this.uri;
    }

    public WebSocketVersion version() {
        return this.version;
    }

    public int maxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }

    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    private void setHandshakeComplete() {
        this.handshakeComplete = true;
    }

    public String expectedSubprotocol() {
        return this.expectedSubprotocol;
    }

    public String actualSubprotocol() {
        return this.actualSubprotocol;
    }

    private void setActualSubprotocol(String actualSubprotocol) {
        this.actualSubprotocol = actualSubprotocol;
    }

    public long forceCloseTimeoutMillis() {
        return this.forceCloseTimeoutMillis;
    }

    protected boolean isForceCloseComplete() {
        return this.forceCloseComplete;
    }

    public WebSocketClientHandshaker setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
        return this;
    }

    public ChannelFuture handshake(Channel channel) {
        if (channel != null) return this.handshake((Channel)channel, (ChannelPromise)channel.newPromise());
        throw new NullPointerException((String)"channel");
    }

    public final ChannelFuture handshake(Channel channel, ChannelPromise promise) {
        HttpClientCodec codec;
        ChannelPipeline pipeline = channel.pipeline();
        HttpResponseDecoder decoder = pipeline.get(HttpResponseDecoder.class);
        if (decoder == null && (codec = pipeline.get(HttpClientCodec.class)) == null) {
            promise.setFailure((Throwable)new IllegalStateException((String)"ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
            return promise;
        }
        FullHttpRequest request = this.newHandshakeRequest();
        channel.writeAndFlush((Object)request).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((WebSocketClientHandshaker)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ WebSocketClientHandshaker this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    this.val$promise.setFailure((Throwable)future.cause());
                    return;
                }
                ChannelPipeline p = future.channel().pipeline();
                ChannelHandlerContext ctx = p.context(HttpRequestEncoder.class);
                if (ctx == null) {
                    ctx = p.context(HttpClientCodec.class);
                }
                if (ctx == null) {
                    this.val$promise.setFailure((Throwable)new IllegalStateException((String)"ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec"));
                    return;
                }
                p.addAfter((String)ctx.name(), (String)"ws-encoder", (ChannelHandler)this.this$0.newWebSocketEncoder());
                this.val$promise.setSuccess();
            }
        });
        return promise;
    }

    protected abstract FullHttpRequest newHandshakeRequest();

    public final void finishHandshake(Channel channel, FullHttpResponse response) {
        ChannelHandlerContext ctx;
        HttpObjectAggregator aggregator;
        this.verify((FullHttpResponse)response);
        String receivedProtocol = response.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
        receivedProtocol = receivedProtocol != null ? receivedProtocol.trim() : null;
        String expectedProtocol = this.expectedSubprotocol != null ? this.expectedSubprotocol : "";
        boolean protocolValid = false;
        if (expectedProtocol.isEmpty() && receivedProtocol == null) {
            protocolValid = true;
            this.setActualSubprotocol((String)this.expectedSubprotocol);
        } else if (!expectedProtocol.isEmpty() && receivedProtocol != null && !receivedProtocol.isEmpty()) {
            for (String protocol : expectedProtocol.split((String)",")) {
                if (!protocol.trim().equals((Object)receivedProtocol)) continue;
                protocolValid = true;
                this.setActualSubprotocol((String)receivedProtocol);
                break;
            }
        }
        if (!protocolValid) {
            throw new WebSocketHandshakeException((String)String.format((String)"Invalid subprotocol. Actual: %s. Expected one of: %s", (Object[])new Object[]{receivedProtocol, this.expectedSubprotocol}));
        }
        this.setHandshakeComplete();
        ChannelPipeline p = channel.pipeline();
        HttpContentDecompressor decompressor = p.get(HttpContentDecompressor.class);
        if (decompressor != null) {
            p.remove((ChannelHandler)decompressor);
        }
        if ((aggregator = p.get(HttpObjectAggregator.class)) != null) {
            p.remove((ChannelHandler)aggregator);
        }
        if ((ctx = p.context(HttpResponseDecoder.class)) == null) {
            ctx = p.context(HttpClientCodec.class);
            if (ctx == null) {
                throw new IllegalStateException((String)"ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec");
            }
            HttpClientCodec codec = (HttpClientCodec)ctx.handler();
            codec.removeOutboundHandler();
            p.addAfter((String)ctx.name(), (String)"ws-decoder", (ChannelHandler)this.newWebsocketDecoder());
            channel.eventLoop().execute((Runnable)new Runnable((WebSocketClientHandshaker)this, (ChannelPipeline)p, (HttpClientCodec)codec){
                final /* synthetic */ ChannelPipeline val$p;
                final /* synthetic */ HttpClientCodec val$codec;
                final /* synthetic */ WebSocketClientHandshaker this$0;
                {
                    this.this$0 = this$0;
                    this.val$p = channelPipeline;
                    this.val$codec = httpClientCodec;
                }

                public void run() {
                    this.val$p.remove((ChannelHandler)this.val$codec);
                }
            });
            return;
        }
        if (p.get(HttpRequestEncoder.class) != null) {
            p.remove(HttpRequestEncoder.class);
        }
        ChannelHandlerContext context = ctx;
        p.addAfter((String)context.name(), (String)"ws-decoder", (ChannelHandler)this.newWebsocketDecoder());
        channel.eventLoop().execute((Runnable)new Runnable((WebSocketClientHandshaker)this, (ChannelPipeline)p, (ChannelHandlerContext)context){
            final /* synthetic */ ChannelPipeline val$p;
            final /* synthetic */ ChannelHandlerContext val$context;
            final /* synthetic */ WebSocketClientHandshaker this$0;
            {
                this.this$0 = this$0;
                this.val$p = channelPipeline;
                this.val$context = channelHandlerContext;
            }

            public void run() {
                this.val$p.remove((ChannelHandler)this.val$context.handler());
            }
        });
    }

    public final ChannelFuture processHandshake(Channel channel, HttpResponse response) {
        return this.processHandshake((Channel)channel, (HttpResponse)response, (ChannelPromise)channel.newPromise());
    }

    public final ChannelFuture processHandshake(Channel channel, HttpResponse response, ChannelPromise promise) {
        if (response instanceof FullHttpResponse) {
            try {
                this.finishHandshake((Channel)channel, (FullHttpResponse)((FullHttpResponse)response));
                promise.setSuccess();
                return promise;
            }
            catch (Throwable cause) {
                promise.setFailure((Throwable)cause);
                return promise;
            }
        }
        ChannelPipeline p = channel.pipeline();
        ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
        if (ctx == null && (ctx = p.context(HttpClientCodec.class)) == null) {
            return promise.setFailure((Throwable)new IllegalStateException((String)"ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
        }
        String aggregatorName = "httpAggregator";
        p.addAfter((String)ctx.name(), (String)aggregatorName, (ChannelHandler)new HttpObjectAggregator((int)8192));
        p.addAfter((String)aggregatorName, (String)"handshaker", (ChannelHandler)new SimpleChannelInboundHandler<FullHttpResponse>((WebSocketClientHandshaker)this, (Channel)channel, (ChannelPromise)promise){
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ WebSocketClientHandshaker this$0;
            {
                this.this$0 = this$0;
                this.val$channel = channel;
                this.val$promise = channelPromise;
            }

            protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws java.lang.Exception {
                ctx.pipeline().remove((ChannelHandler)this);
                try {
                    this.this$0.finishHandshake((Channel)this.val$channel, (FullHttpResponse)msg);
                    this.val$promise.setSuccess();
                    return;
                }
                catch (Throwable cause) {
                    this.val$promise.setFailure((Throwable)cause);
                }
            }

            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws java.lang.Exception {
                ctx.pipeline().remove((ChannelHandler)this);
                this.val$promise.setFailure((Throwable)cause);
            }

            public void channelInactive(ChannelHandlerContext ctx) throws java.lang.Exception {
                if (!this.val$promise.isDone()) {
                    this.val$promise.tryFailure((Throwable)new java.nio.channels.ClosedChannelException());
                }
                ctx.fireChannelInactive();
            }
        });
        try {
            ctx.fireChannelRead((Object)ReferenceCountUtil.retain(response));
            return promise;
        }
        catch (Throwable cause) {
            promise.setFailure((Throwable)cause);
        }
        return promise;
    }

    protected abstract void verify(FullHttpResponse var1);

    protected abstract WebSocketFrameDecoder newWebsocketDecoder();

    protected abstract WebSocketFrameEncoder newWebSocketEncoder();

    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
        if (channel != null) return this.close((Channel)channel, (CloseWebSocketFrame)frame, (ChannelPromise)channel.newPromise());
        throw new NullPointerException((String)"channel");
    }

    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
        if (channel == null) {
            throw new NullPointerException((String)"channel");
        }
        channel.writeAndFlush((Object)frame, (ChannelPromise)promise);
        this.applyForceCloseTimeout((Channel)channel, (ChannelFuture)promise);
        return promise;
    }

    private void applyForceCloseTimeout(Channel channel, ChannelFuture flushFuture) {
        long forceCloseTimeoutMillis = this.forceCloseTimeoutMillis;
        WebSocketClientHandshaker handshaker = this;
        if (forceCloseTimeoutMillis <= 0L) return;
        if (!channel.isActive()) return;
        if (this.forceCloseInit != 0) {
            return;
        }
        flushFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((WebSocketClientHandshaker)this, (Channel)channel, (WebSocketClientHandshaker)handshaker, (long)forceCloseTimeoutMillis){
            final /* synthetic */ Channel val$channel;
            final /* synthetic */ WebSocketClientHandshaker val$handshaker;
            final /* synthetic */ long val$forceCloseTimeoutMillis;
            final /* synthetic */ WebSocketClientHandshaker this$0;
            {
                this.this$0 = this$0;
                this.val$channel = channel;
                this.val$handshaker = webSocketClientHandshaker;
                this.val$forceCloseTimeoutMillis = l;
            }

            public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                if (!future.isSuccess()) return;
                if (!this.val$channel.isActive()) return;
                if (!WebSocketClientHandshaker.access$000().compareAndSet(this.val$handshaker, (int)0, (int)1)) return;
                io.netty.util.concurrent.ScheduledFuture<?> forceCloseFuture = this.val$channel.eventLoop().schedule((Runnable)new Runnable(this){
                    final /* synthetic */ 5 this$1;
                    {
                        this.this$1 = this$1;
                    }

                    public void run() {
                        if (!this.this$1.val$channel.isActive()) return;
                        this.this$1.val$channel.close();
                        WebSocketClientHandshaker.access$102((WebSocketClientHandshaker)this.this$1.this$0, (boolean)true);
                    }
                }, (long)this.val$forceCloseTimeoutMillis, (java.util.concurrent.TimeUnit)java.util.concurrent.TimeUnit.MILLISECONDS);
                this.val$channel.closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener(this, forceCloseFuture){
                    final /* synthetic */ java.util.concurrent.Future val$forceCloseFuture;
                    final /* synthetic */ 5 this$1;
                    {
                        this.this$1 = this$1;
                        this.val$forceCloseFuture = future;
                    }

                    public void operationComplete(ChannelFuture future) throws java.lang.Exception {
                        this.val$forceCloseFuture.cancel((boolean)false);
                    }
                });
            }
        });
    }

    protected String upgradeUrl(URI wsURL) {
        if (this.absoluteUpgradeUrl) {
            return wsURL.toString();
        }
        String path = wsURL.getRawPath();
        String query = wsURL.getRawQuery();
        if (query != null && !query.isEmpty()) {
            path = path + '?' + query;
        }
        if (path == null) return "/";
        if (path.isEmpty()) return "/";
        String string = path;
        return string;
    }

    static CharSequence websocketHostValue(URI wsURL) {
        String string;
        int port = wsURL.getPort();
        if (port == -1) {
            return wsURL.getHost();
        }
        String host = wsURL.getHost();
        String scheme = wsURL.getScheme();
        if (port == HttpScheme.HTTP.port()) {
            String string2;
            if (!HttpScheme.HTTP.name().contentEquals((CharSequence)scheme) && !WebSocketScheme.WS.name().contentEquals((CharSequence)scheme)) {
                string2 = NetUtil.toSocketAddressString((String)host, (int)port);
                return string2;
            }
            string2 = host;
            return string2;
        }
        if (port != HttpScheme.HTTPS.port()) return NetUtil.toSocketAddressString((String)host, (int)port);
        if (!HttpScheme.HTTPS.name().contentEquals((CharSequence)scheme) && !WebSocketScheme.WSS.name().contentEquals((CharSequence)scheme)) {
            string = NetUtil.toSocketAddressString((String)host, (int)port);
            return string;
        }
        string = host;
        return string;
    }

    static CharSequence websocketOriginValue(URI wsURL) {
        String schemePrefix;
        int defaultPort;
        String scheme = wsURL.getScheme();
        int port = wsURL.getPort();
        if (WebSocketScheme.WSS.name().contentEquals((CharSequence)scheme) || HttpScheme.HTTPS.name().contentEquals((CharSequence)scheme) || scheme == null && port == WebSocketScheme.WSS.port()) {
            schemePrefix = HTTPS_SCHEME_PREFIX;
            defaultPort = WebSocketScheme.WSS.port();
        } else {
            schemePrefix = HTTP_SCHEME_PREFIX;
            defaultPort = WebSocketScheme.WS.port();
        }
        String host = wsURL.getHost().toLowerCase((Locale)Locale.US);
        if (port == defaultPort) return schemePrefix + host;
        if (port == -1) return schemePrefix + host;
        return schemePrefix + NetUtil.toSocketAddressString((String)host, (int)port);
    }

    static /* synthetic */ AtomicIntegerFieldUpdater access$000() {
        return FORCE_CLOSE_INIT_UPDATER;
    }

    static /* synthetic */ boolean access$102(WebSocketClientHandshaker x0, boolean x1) {
        x0.forceCloseComplete = x1;
        return x0.forceCloseComplete;
    }
}

