/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;

public final class CoalescingBufferQueue
extends AbstractCoalescingBufferQueue {
    private final Channel channel;

    public CoalescingBufferQueue(Channel channel) {
        this((Channel)channel, (int)4);
    }

    public CoalescingBufferQueue(Channel channel, int initSize) {
        this((Channel)channel, (int)initSize, (boolean)false);
    }

    public CoalescingBufferQueue(Channel channel, int initSize, boolean updateWritability) {
        super((Channel)(updateWritability ? channel : null), (int)initSize);
        this.channel = ObjectUtil.checkNotNull(channel, (String)"channel");
    }

    public ByteBuf remove(int bytes, ChannelPromise aggregatePromise) {
        return this.remove((ByteBufAllocator)this.channel.alloc(), (int)bytes, (ChannelPromise)aggregatePromise);
    }

    public void releaseAndFailAll(Throwable cause) {
        this.releaseAndFailAll((ChannelOutboundInvoker)this.channel, (Throwable)cause);
    }

    @Override
    protected ByteBuf compose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
        if (!(cumulation instanceof CompositeByteBuf)) return this.composeIntoComposite((ByteBufAllocator)alloc, (ByteBuf)cumulation, (ByteBuf)next);
        CompositeByteBuf composite = (CompositeByteBuf)cumulation;
        composite.addComponent((boolean)true, (ByteBuf)next);
        return composite;
    }

    @Override
    protected ByteBuf removeEmptyValue() {
        return Unpooled.EMPTY_BUFFER;
    }
}

