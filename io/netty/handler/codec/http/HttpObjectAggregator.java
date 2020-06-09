/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpExpectationFailedEvent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class HttpObjectAggregator
extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpObjectAggregator.class);
    private static final FullHttpResponse CONTINUE = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.CONTINUE, (ByteBuf)Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.EXPECTATION_FAILED, (ByteBuf)Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, (ByteBuf)Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse TOO_LARGE = new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_1, (HttpResponseStatus)HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, (ByteBuf)Unpooled.EMPTY_BUFFER);
    private final boolean closeOnExpectationFailed;

    public HttpObjectAggregator(int maxContentLength) {
        this((int)maxContentLength, (boolean)false);
    }

    public HttpObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
        super((int)maxContentLength);
        this.closeOnExpectationFailed = closeOnExpectationFailed;
    }

    @Override
    protected boolean isStartMessage(HttpObject msg) throws Exception {
        return msg instanceof HttpMessage;
    }

    @Override
    protected boolean isContentMessage(HttpObject msg) throws Exception {
        return msg instanceof HttpContent;
    }

    @Override
    protected boolean isLastContentMessage(HttpContent msg) throws Exception {
        return msg instanceof LastHttpContent;
    }

    @Override
    protected boolean isAggregated(HttpObject msg) throws Exception {
        return msg instanceof FullHttpMessage;
    }

    @Override
    protected boolean isContentLengthInvalid(HttpMessage start, int maxContentLength) {
        try {
            if (HttpUtil.getContentLength((HttpMessage)start, (long)-1L) <= (long)maxContentLength) return false;
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static Object continueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
        if (HttpUtil.isUnsupportedExpectation((HttpMessage)start)) {
            pipeline.fireUserEventTriggered((Object)HttpExpectationFailedEvent.INSTANCE);
            return EXPECTATION_FAILED.retainedDuplicate();
        }
        if (!HttpUtil.is100ContinueExpected((HttpMessage)start)) return null;
        if (HttpUtil.getContentLength((HttpMessage)start, (long)-1L) <= (long)maxContentLength) {
            return CONTINUE.retainedDuplicate();
        }
        pipeline.fireUserEventTriggered((Object)HttpExpectationFailedEvent.INSTANCE);
        return TOO_LARGE.retainedDuplicate();
    }

    @Override
    protected Object newContinueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
        Object response = HttpObjectAggregator.continueResponse((HttpMessage)start, (int)maxContentLength, (ChannelPipeline)pipeline);
        if (response == null) return response;
        start.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
        return response;
    }

    @Override
    protected boolean closeAfterContinueResponse(Object msg) {
        if (!this.closeOnExpectationFailed) return false;
        if (!this.ignoreContentAfterContinueResponse((Object)msg)) return false;
        return true;
    }

    @Override
    protected boolean ignoreContentAfterContinueResponse(Object msg) {
        if (!(msg instanceof HttpResponse)) return false;
        HttpResponse httpResponse = (HttpResponse)msg;
        return httpResponse.status().codeClass().equals((Object)((Object)HttpStatusClass.CLIENT_ERROR));
    }

    @Override
    protected FullHttpMessage beginAggregation(HttpMessage start, ByteBuf content) throws Exception {
        assert (!(start instanceof FullHttpMessage));
        HttpUtil.setTransferEncodingChunked((HttpMessage)start, (boolean)false);
        if (start instanceof HttpRequest) {
            return new AggregatedFullHttpRequest((HttpRequest)((HttpRequest)start), (ByteBuf)content, null);
        }
        if (!(start instanceof HttpResponse)) throw new Error();
        return new AggregatedFullHttpResponse((HttpResponse)((HttpResponse)start), (ByteBuf)content, null);
    }

    @Override
    protected void aggregate(FullHttpMessage aggregated, HttpContent content) throws Exception {
        if (!(content instanceof LastHttpContent)) return;
        ((AggregatedFullHttpMessage)aggregated).setTrailingHeaders((HttpHeaders)((LastHttpContent)content).trailingHeaders());
    }

    @Override
    protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
        if (HttpUtil.isContentLengthSet((HttpMessage)aggregated)) return;
        aggregated.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)String.valueOf((int)aggregated.content().readableBytes()));
    }

    @Override
    protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
        if (!(oversized instanceof HttpRequest)) {
            if (!(oversized instanceof HttpResponse)) throw new IllegalStateException();
            ctx.close();
            throw new TooLongFrameException((String)("Response entity too large: " + oversized));
        }
        if (oversized instanceof FullHttpMessage || !HttpUtil.is100ContinueExpected((HttpMessage)oversized) && !HttpUtil.isKeepAlive((HttpMessage)oversized)) {
            ChannelFuture future = ctx.writeAndFlush((Object)TOO_LARGE_CLOSE.retainedDuplicate());
            future.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((HttpObjectAggregator)this, (ChannelHandlerContext)ctx){
                final /* synthetic */ ChannelHandlerContext val$ctx;
                final /* synthetic */ HttpObjectAggregator this$0;
                {
                    this.this$0 = this$0;
                    this.val$ctx = channelHandlerContext;
                }

                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        HttpObjectAggregator.access$000().debug((String)"Failed to send a 413 Request Entity Too Large.", (java.lang.Throwable)future.cause());
                    }
                    this.val$ctx.close();
                }
            });
            return;
        }
        ctx.writeAndFlush((Object)TOO_LARGE.retainedDuplicate()).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((HttpObjectAggregator)this, (ChannelHandlerContext)ctx){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ HttpObjectAggregator this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
            }

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) return;
                HttpObjectAggregator.access$000().debug((String)"Failed to send a 413 Request Entity Too Large.", (java.lang.Throwable)future.cause());
                this.val$ctx.close();
            }
        });
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }

    static {
        EXPECTATION_FAILED.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)Integer.valueOf((int)0));
        TOO_LARGE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)Integer.valueOf((int)0));
        TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)Integer.valueOf((int)0));
        TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.CLOSE);
    }
}

