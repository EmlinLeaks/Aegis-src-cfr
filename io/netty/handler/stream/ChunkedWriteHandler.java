/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedInput;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ChunkedWriteHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
    private final Queue<PendingWrite> queue = new ArrayDeque<PendingWrite>();
    private volatile ChannelHandlerContext ctx;
    private PendingWrite currentWrite;

    public ChunkedWriteHandler() {
    }

    @Deprecated
    public ChunkedWriteHandler(int maxPendingWrites) {
        if (maxPendingWrites > 0) return;
        throw new IllegalArgumentException((String)("maxPendingWrites: " + maxPendingWrites + " (expected: > 0)"));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    public void resumeTransfer() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            return;
        }
        if (ctx.executor().inEventLoop()) {
            this.resumeTransfer0((ChannelHandlerContext)ctx);
            return;
        }
        ctx.executor().execute((Runnable)new Runnable((ChunkedWriteHandler)this, (ChannelHandlerContext)ctx){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChunkedWriteHandler this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
            }

            public void run() {
                ChunkedWriteHandler.access$000((ChunkedWriteHandler)this.this$0, (ChannelHandlerContext)this.val$ctx);
            }
        });
    }

    private void resumeTransfer0(ChannelHandlerContext ctx) {
        try {
            this.doFlush((ChannelHandlerContext)ctx);
            return;
        }
        catch (Exception e) {
            logger.warn((String)"Unexpected exception while sending chunks.", (Throwable)e);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.queue.add((PendingWrite)new PendingWrite((Object)msg, (ChannelPromise)promise));
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        this.doFlush((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.doFlush((ChannelHandlerContext)ctx);
        ctx.fireChannelInactive();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            this.doFlush((ChannelHandlerContext)ctx);
        }
        ctx.fireChannelWritabilityChanged();
    }

    private void discard(Throwable cause) {
        do {
            PendingWrite currentWrite = this.currentWrite;
            if (this.currentWrite == null) {
                currentWrite = this.queue.poll();
            } else {
                this.currentWrite = null;
            }
            if (currentWrite == null) {
                return;
            }
            Object message = currentWrite.msg;
            if (message instanceof ChunkedInput) {
                boolean endOfInput;
                long inputLength;
                ChunkedInput in = (ChunkedInput)message;
                try {
                    endOfInput = in.isEndOfInput();
                    inputLength = in.length();
                    ChunkedWriteHandler.closeInput(in);
                }
                catch (Exception e) {
                    ChunkedWriteHandler.closeInput(in);
                    currentWrite.fail((Throwable)e);
                    if (!logger.isWarnEnabled()) continue;
                    logger.warn((String)(ChunkedInput.class.getSimpleName() + " failed"), (Throwable)e);
                    continue;
                }
                if (!endOfInput) {
                    if (cause == null) {
                        cause = new ClosedChannelException();
                    }
                    currentWrite.fail((Throwable)cause);
                    continue;
                }
                currentWrite.success((long)inputLength);
                continue;
            }
            if (cause == null) {
                cause = new ClosedChannelException();
            }
            currentWrite.fail((Throwable)cause);
        } while (true);
    }

    private void doFlush(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            this.discard(null);
            return;
        }
        boolean requiresFlush = true;
        ByteBufAllocator allocator = ctx.alloc();
        while (channel.isWritable()) {
            if (this.currentWrite == null) {
                this.currentWrite = this.queue.poll();
            }
            if (this.currentWrite == null) break;
            if (this.currentWrite.promise.isDone()) {
                this.currentWrite = null;
                continue;
            }
            PendingWrite currentWrite = this.currentWrite;
            Object pendingMessage = currentWrite.msg;
            if (pendingMessage instanceof ChunkedInput) {
                boolean endOfInput;
                boolean suspend;
                ChunkedInput chunks = (ChunkedInput)pendingMessage;
                ByteBuf message = null;
                try {
                    message = (ByteBuf)chunks.readChunk((ByteBufAllocator)allocator);
                    endOfInput = chunks.isEndOfInput();
                    suspend = message == null ? !endOfInput : false;
                }
                catch (Throwable t) {
                    this.currentWrite = null;
                    if (message != null) {
                        ReferenceCountUtil.release(message);
                    }
                    ChunkedWriteHandler.closeInput(chunks);
                    currentWrite.fail((Throwable)t);
                    break;
                }
                if (suspend) break;
                if (message == null) {
                    message = Unpooled.EMPTY_BUFFER;
                }
                ChannelFuture f = ctx.write((Object)message);
                if (endOfInput) {
                    this.currentWrite = null;
                    f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((ChunkedWriteHandler)this, (ChunkedInput)chunks, (PendingWrite)currentWrite){
                        final /* synthetic */ ChunkedInput val$chunks;
                        final /* synthetic */ PendingWrite val$currentWrite;
                        final /* synthetic */ ChunkedWriteHandler this$0;
                        {
                            this.this$0 = this$0;
                            this.val$chunks = chunkedInput;
                            this.val$currentWrite = pendingWrite;
                        }

                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                ChunkedWriteHandler.access$100((ChunkedInput)this.val$chunks);
                                this.val$currentWrite.fail((Throwable)future.cause());
                                return;
                            }
                            long inputProgress = this.val$chunks.progress();
                            long inputLength = this.val$chunks.length();
                            ChunkedWriteHandler.access$100((ChunkedInput)this.val$chunks);
                            this.val$currentWrite.progress((long)inputProgress, (long)inputLength);
                            this.val$currentWrite.success((long)inputLength);
                        }
                    });
                } else if (channel.isWritable()) {
                    f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((ChunkedWriteHandler)this, (ChunkedInput)chunks, (PendingWrite)currentWrite){
                        final /* synthetic */ ChunkedInput val$chunks;
                        final /* synthetic */ PendingWrite val$currentWrite;
                        final /* synthetic */ ChunkedWriteHandler this$0;
                        {
                            this.this$0 = this$0;
                            this.val$chunks = chunkedInput;
                            this.val$currentWrite = pendingWrite;
                        }

                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                ChunkedWriteHandler.access$100((ChunkedInput)this.val$chunks);
                                this.val$currentWrite.fail((Throwable)future.cause());
                                return;
                            }
                            this.val$currentWrite.progress((long)this.val$chunks.progress(), (long)this.val$chunks.length());
                        }
                    });
                } else {
                    f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((ChunkedWriteHandler)this, (ChunkedInput)chunks, (PendingWrite)currentWrite, (Channel)channel){
                        final /* synthetic */ ChunkedInput val$chunks;
                        final /* synthetic */ PendingWrite val$currentWrite;
                        final /* synthetic */ Channel val$channel;
                        final /* synthetic */ ChunkedWriteHandler this$0;
                        {
                            this.this$0 = this$0;
                            this.val$chunks = chunkedInput;
                            this.val$currentWrite = pendingWrite;
                            this.val$channel = channel;
                        }

                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                ChunkedWriteHandler.access$100((ChunkedInput)this.val$chunks);
                                this.val$currentWrite.fail((Throwable)future.cause());
                                return;
                            }
                            this.val$currentWrite.progress((long)this.val$chunks.progress(), (long)this.val$chunks.length());
                            if (!this.val$channel.isWritable()) return;
                            this.this$0.resumeTransfer();
                        }
                    });
                }
                ctx.flush();
                requiresFlush = false;
            } else {
                this.currentWrite = null;
                ctx.write((Object)pendingMessage, (ChannelPromise)currentWrite.promise);
                requiresFlush = true;
            }
            if (channel.isActive()) continue;
            this.discard((Throwable)new ClosedChannelException());
            break;
        }
        if (!requiresFlush) return;
        ctx.flush();
    }

    private static void closeInput(ChunkedInput<?> chunks) {
        try {
            chunks.close();
            return;
        }
        catch (Throwable t) {
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)"Failed to close a chunked input.", (Throwable)t);
        }
    }

    static /* synthetic */ void access$000(ChunkedWriteHandler x0, ChannelHandlerContext x1) {
        x0.resumeTransfer0((ChannelHandlerContext)x1);
    }

    static /* synthetic */ void access$100(ChunkedInput x0) {
        ChunkedWriteHandler.closeInput(x0);
    }
}

