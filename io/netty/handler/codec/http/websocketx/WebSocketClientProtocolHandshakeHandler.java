/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandshakeHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketClientProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private static final long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10000L;
    private final WebSocketClientHandshaker handshaker;
    private final long handshakeTimeoutMillis;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;

    WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker) {
        this((WebSocketClientHandshaker)handshaker, (long)10000L);
    }

    WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker, long handshakeTimeoutMillis) {
        this.handshaker = handshaker;
        this.handshakeTimeoutMillis = ObjectUtil.checkPositive((long)handshakeTimeoutMillis, (String)"handshakeTimeoutMillis");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive((ChannelHandlerContext)ctx);
        this.handshaker.handshake((Channel)ctx.channel()).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((WebSocketClientProtocolHandshakeHandler)this, (ChannelHandlerContext)ctx){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ WebSocketClientProtocolHandshakeHandler this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
            }

            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    WebSocketClientProtocolHandshakeHandler.access$000((WebSocketClientProtocolHandshakeHandler)this.this$0).tryFailure((java.lang.Throwable)future.cause());
                    this.val$ctx.fireExceptionCaught((java.lang.Throwable)future.cause());
                    return;
                }
                this.val$ctx.fireUserEventTriggered((Object)((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED));
            }
        });
        this.applyHandshakeTimeout();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            ctx.fireChannelRead((Object)msg);
            return;
        }
        FullHttpResponse response = (FullHttpResponse)msg;
        try {
            if (this.handshaker.isHandshakeComplete()) throw new IllegalStateException((String)"WebSocketClientHandshaker should have been non finished yet");
            this.handshaker.finishHandshake((Channel)ctx.channel(), (FullHttpResponse)response);
            this.handshakePromise.trySuccess();
            ctx.fireUserEventTriggered((Object)((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE));
            ctx.pipeline().remove((ChannelHandler)this);
            return;
        }
        finally {
            response.release();
        }
    }

    private void applyHandshakeTimeout() {
        ChannelPromise localHandshakePromise = this.handshakePromise;
        if (this.handshakeTimeoutMillis <= 0L) return;
        if (localHandshakePromise.isDone()) {
            return;
        }
        ScheduledFuture<?> timeoutFuture = this.ctx.executor().schedule((Runnable)new Runnable((WebSocketClientProtocolHandshakeHandler)this, (ChannelPromise)localHandshakePromise){
            final /* synthetic */ ChannelPromise val$localHandshakePromise;
            final /* synthetic */ WebSocketClientProtocolHandshakeHandler this$0;
            {
                this.this$0 = this$0;
                this.val$localHandshakePromise = channelPromise;
            }

            public void run() {
                if (this.val$localHandshakePromise.isDone()) {
                    return;
                }
                if (!this.val$localHandshakePromise.tryFailure((java.lang.Throwable)new io.netty.handler.codec.http.websocketx.WebSocketHandshakeException((String)"handshake timed out"))) return;
                WebSocketClientProtocolHandshakeHandler.access$100((WebSocketClientProtocolHandshakeHandler)this.this$0).flush().fireUserEventTriggered((Object)((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_TIMEOUT)).close();
            }
        }, (long)this.handshakeTimeoutMillis, (TimeUnit)TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)new FutureListener<Void>((WebSocketClientProtocolHandshakeHandler)this, timeoutFuture){
            final /* synthetic */ Future val$timeoutFuture;
            final /* synthetic */ WebSocketClientProtocolHandshakeHandler this$0;
            {
                this.this$0 = this$0;
                this.val$timeoutFuture = future;
            }

            public void operationComplete(Future<Void> f) throws Exception {
                this.val$timeoutFuture.cancel((boolean)false);
            }
        });
    }

    ChannelFuture getHandshakeFuture() {
        return this.handshakePromise;
    }

    static /* synthetic */ ChannelPromise access$000(WebSocketClientProtocolHandshakeHandler x0) {
        return x0.handshakePromise;
    }

    static /* synthetic */ ChannelHandlerContext access$100(WebSocketClientProtocolHandshakeHandler x0) {
        return x0.ctx;
    }
}

