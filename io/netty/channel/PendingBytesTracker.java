/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.PendingBytesTracker;
import io.netty.util.internal.ObjectUtil;

abstract class PendingBytesTracker
implements MessageSizeEstimator.Handle {
    private final MessageSizeEstimator.Handle estimatorHandle;

    private PendingBytesTracker(MessageSizeEstimator.Handle estimatorHandle) {
        this.estimatorHandle = ObjectUtil.checkNotNull(estimatorHandle, (String)"estimatorHandle");
    }

    @Override
    public final int size(Object msg) {
        return this.estimatorHandle.size((Object)msg);
    }

    public abstract void incrementPendingOutboundBytes(long var1);

    public abstract void decrementPendingOutboundBytes(long var1);

    static PendingBytesTracker newTracker(Channel channel) {
        PendingBytesTracker pendingBytesTracker;
        if (channel.pipeline() instanceof DefaultChannelPipeline) {
            return new DefaultChannelPipelinePendingBytesTracker((DefaultChannelPipeline)((DefaultChannelPipeline)channel.pipeline()));
        }
        ChannelOutboundBuffer buffer = channel.unsafe().outboundBuffer();
        MessageSizeEstimator.Handle handle = channel.config().getMessageSizeEstimator().newHandle();
        if (buffer == null) {
            pendingBytesTracker = new NoopPendingBytesTracker((MessageSizeEstimator.Handle)handle);
            return pendingBytesTracker;
        }
        pendingBytesTracker = new ChannelOutboundBufferPendingBytesTracker((ChannelOutboundBuffer)buffer, (MessageSizeEstimator.Handle)handle);
        return pendingBytesTracker;
    }
}

