/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.MessageAggregationException;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public abstract class MessageAggregator<I, S, C extends ByteBufHolder, O extends ByteBufHolder>
extends MessageToMessageDecoder<I> {
    private static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private final int maxContentLength;
    private O currentMessage;
    private boolean handlingOversizedMessage;
    private int maxCumulationBufferComponents = 1024;
    private ChannelHandlerContext ctx;
    private ChannelFutureListener continueResponseWriteListener;
    private boolean aggregating;

    protected MessageAggregator(int maxContentLength) {
        MessageAggregator.validateMaxContentLength((int)maxContentLength);
        this.maxContentLength = maxContentLength;
    }

    protected MessageAggregator(int maxContentLength, Class<? extends I> inboundMessageType) {
        super(inboundMessageType);
        MessageAggregator.validateMaxContentLength((int)maxContentLength);
        this.maxContentLength = maxContentLength;
    }

    private static void validateMaxContentLength(int maxContentLength) {
        ObjectUtil.checkPositiveOrZero((int)maxContentLength, (String)"maxContentLength");
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (!super.acceptInboundMessage((Object)msg)) {
            return false;
        }
        Object in = msg;
        if (this.isAggregated(in)) {
            return false;
        }
        if (this.isStartMessage(in)) {
            this.aggregating = true;
            return true;
        }
        if (!this.aggregating) return false;
        if (!this.isContentMessage(in)) return false;
        return true;
    }

    protected abstract boolean isStartMessage(I var1) throws Exception;

    protected abstract boolean isContentMessage(I var1) throws Exception;

    protected abstract boolean isLastContentMessage(C var1) throws Exception;

    protected abstract boolean isAggregated(I var1) throws Exception;

    public final int maxContentLength() {
        return this.maxContentLength;
    }

    public final int maxCumulationBufferComponents() {
        return this.maxCumulationBufferComponents;
    }

    public final void setMaxCumulationBufferComponents(int maxCumulationBufferComponents) {
        if (maxCumulationBufferComponents < 2) {
            throw new IllegalArgumentException((String)("maxCumulationBufferComponents: " + maxCumulationBufferComponents + " (expected: >= 2)"));
        }
        if (this.ctx != null) throw new IllegalStateException((String)"decoder properties cannot be changed once the decoder is added to a pipeline.");
        this.maxCumulationBufferComponents = maxCumulationBufferComponents;
    }

    @Deprecated
    public final boolean isHandlingOversizedMessage() {
        return this.handlingOversizedMessage;
    }

    protected final ChannelHandlerContext ctx() {
        if (this.ctx != null) return this.ctx;
        throw new IllegalStateException((String)"not added to a pipeline yet");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception {
        boolean last;
        assert (this.aggregating);
        if (this.isStartMessage(msg)) {
            this.handlingOversizedMessage = false;
            if (this.currentMessage != null) {
                this.currentMessage.release();
                this.currentMessage = null;
                throw new MessageAggregationException();
            }
            I m = msg;
            Object continueResponse = this.newContinueResponse(m, (int)this.maxContentLength, (ChannelPipeline)ctx.pipeline());
            if (continueResponse != null) {
                ChannelFutureListener listener = this.continueResponseWriteListener;
                if (listener == null) {
                    this.continueResponseWriteListener = listener = new ChannelFutureListener((MessageAggregator)this, (ChannelHandlerContext)ctx){
                        final /* synthetic */ ChannelHandlerContext val$ctx;
                        final /* synthetic */ MessageAggregator this$0;
                        {
                            this.this$0 = this$0;
                            this.val$ctx = channelHandlerContext;
                        }

                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) return;
                            this.val$ctx.fireExceptionCaught((Throwable)future.cause());
                        }
                    };
                }
                boolean closeAfterWrite = this.closeAfterContinueResponse((Object)continueResponse);
                this.handlingOversizedMessage = this.ignoreContentAfterContinueResponse((Object)continueResponse);
                ChannelFuture future = ctx.writeAndFlush((Object)continueResponse).addListener((GenericFutureListener<? extends Future<? super Void>>)listener);
                if (closeAfterWrite) {
                    future.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
                    return;
                }
                if (this.handlingOversizedMessage) {
                    return;
                }
            } else if (this.isContentLengthInvalid(m, (int)this.maxContentLength)) {
                this.invokeHandleOversizedMessage((ChannelHandlerContext)ctx, m);
                return;
            }
            if (m instanceof DecoderResultProvider && !((DecoderResultProvider)m).decoderResult().isSuccess()) {
                O aggregated = m instanceof ByteBufHolder ? this.beginAggregation(m, (ByteBuf)((ByteBufHolder)m).content().retain()) : this.beginAggregation(m, (ByteBuf)Unpooled.EMPTY_BUFFER);
                this.finishAggregation0(aggregated);
                out.add(aggregated);
                return;
            }
            CompositeByteBuf content = ctx.alloc().compositeBuffer((int)this.maxCumulationBufferComponents);
            if (m instanceof ByteBufHolder) {
                MessageAggregator.appendPartialContent((CompositeByteBuf)content, (ByteBuf)((ByteBufHolder)m).content());
            }
            this.currentMessage = this.beginAggregation(m, (ByteBuf)content);
            return;
        }
        if (!this.isContentMessage(msg)) throw new MessageAggregationException();
        if (this.currentMessage == null) {
            return;
        }
        CompositeByteBuf content = (CompositeByteBuf)this.currentMessage.content();
        ByteBufHolder m = (ByteBufHolder)msg;
        if (content.readableBytes() > this.maxContentLength - m.content().readableBytes()) {
            O s = this.currentMessage;
            this.invokeHandleOversizedMessage((ChannelHandlerContext)ctx, s);
            return;
        }
        MessageAggregator.appendPartialContent((CompositeByteBuf)content, (ByteBuf)m.content());
        this.aggregate(this.currentMessage, m);
        if (m instanceof DecoderResultProvider) {
            DecoderResult decoderResult = ((DecoderResultProvider)((Object)m)).decoderResult();
            if (!decoderResult.isSuccess()) {
                if (this.currentMessage instanceof DecoderResultProvider) {
                    ((DecoderResultProvider)this.currentMessage).setDecoderResult((DecoderResult)DecoderResult.failure((Throwable)decoderResult.cause()));
                }
                last = true;
            } else {
                last = this.isLastContentMessage(m);
            }
        } else {
            last = this.isLastContentMessage(m);
        }
        if (!last) return;
        this.finishAggregation0(this.currentMessage);
        out.add(this.currentMessage);
        this.currentMessage = null;
    }

    private static void appendPartialContent(CompositeByteBuf content, ByteBuf partialContent) {
        if (!partialContent.isReadable()) return;
        content.addComponent((boolean)true, (ByteBuf)partialContent.retain());
    }

    protected abstract boolean isContentLengthInvalid(S var1, int var2) throws Exception;

    protected abstract Object newContinueResponse(S var1, int var2, ChannelPipeline var3) throws Exception;

    protected abstract boolean closeAfterContinueResponse(Object var1) throws Exception;

    protected abstract boolean ignoreContentAfterContinueResponse(Object var1) throws Exception;

    protected abstract O beginAggregation(S var1, ByteBuf var2) throws Exception;

    protected void aggregate(O aggregated, C content) throws Exception {
    }

    private void finishAggregation0(O aggregated) throws Exception {
        this.aggregating = false;
        this.finishAggregation(aggregated);
    }

    protected void finishAggregation(O aggregated) throws Exception {
    }

    private void invokeHandleOversizedMessage(ChannelHandlerContext ctx, S oversized) throws Exception {
        this.handlingOversizedMessage = true;
        this.currentMessage = null;
        try {
            this.handleOversizedMessage((ChannelHandlerContext)ctx, oversized);
            return;
        }
        finally {
            ReferenceCountUtil.release(oversized);
        }
    }

    protected void handleOversizedMessage(ChannelHandlerContext ctx, S oversized) throws Exception {
        ctx.fireExceptionCaught((Throwable)new TooLongFrameException((String)("content length exceeded " + this.maxContentLength() + " bytes.")));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.currentMessage != null && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelInactive((ChannelHandlerContext)ctx);
            return;
        }
        finally {
            this.releaseCurrentMessage();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            super.handlerRemoved((ChannelHandlerContext)ctx);
            return;
        }
        finally {
            this.releaseCurrentMessage();
        }
    }

    private void releaseCurrentMessage() {
        if (this.currentMessage == null) return;
        this.currentMessage.release();
        this.currentMessage = null;
        this.handlingOversizedMessage = false;
        this.aggregating = false;
    }
}

