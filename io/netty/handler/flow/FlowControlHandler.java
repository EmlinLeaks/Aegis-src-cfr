/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.flow;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class FlowControlHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(FlowControlHandler.class);
    private final boolean releaseMessages;
    private RecyclableArrayDeque queue;
    private ChannelConfig config;
    private boolean shouldConsume;

    public FlowControlHandler() {
        this((boolean)true);
    }

    public FlowControlHandler(boolean releaseMessages) {
        this.releaseMessages = releaseMessages;
    }

    boolean isQueueEmpty() {
        if (this.queue == null) return true;
        if (this.queue.isEmpty()) return true;
        return false;
    }

    private void destroy() {
        if (this.queue == null) return;
        if (!this.queue.isEmpty()) {
            logger.trace((String)"Non-empty queue: {}", (Object)this.queue);
            if (this.releaseMessages) {
                E msg;
                while ((msg = this.queue.poll()) != null) {
                    ReferenceCountUtil.safeRelease(msg);
                }
            }
        }
        this.queue.recycle();
        this.queue = null;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.config = ctx.channel().config();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
        ctx.fireChannelInactive();
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (this.dequeue((ChannelHandlerContext)ctx, (int)1) != 0) return;
        this.shouldConsume = true;
        ctx.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.queue == null) {
            this.queue = RecyclableArrayDeque.newInstance();
        }
        this.queue.offer(msg);
        int minConsume = this.shouldConsume ? 1 : 0;
        this.shouldConsume = false;
        this.dequeue((ChannelHandlerContext)ctx, (int)minConsume);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (!this.isQueueEmpty()) return;
        ctx.fireChannelReadComplete();
    }

    private int dequeue(ChannelHandlerContext ctx, int minConsume) {
        E msg;
        int consumed;
        for (consumed = 0; this.queue != null && (consumed < minConsume || this.config.isAutoRead()) && (msg = this.queue.poll()) != null; ++consumed) {
            ctx.fireChannelRead(msg);
        }
        if (this.queue == null) return consumed;
        if (!this.queue.isEmpty()) return consumed;
        this.queue.recycle();
        this.queue = null;
        if (consumed <= 0) return consumed;
        ctx.fireChannelReadComplete();
        return consumed;
    }
}

