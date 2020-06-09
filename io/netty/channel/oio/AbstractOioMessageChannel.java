/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class AbstractOioMessageChannel
extends AbstractOioChannel {
    private final List<Object> readBuf = new ArrayList<Object>();

    protected AbstractOioMessageChannel(Channel parent) {
        super((Channel)parent);
    }

    @Override
    protected void doRead() {
        if (!this.readPending) {
            return;
        }
        this.readPending = false;
        ChannelConfig config = this.config();
        ChannelPipeline pipeline = this.pipeline();
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        allocHandle.reset((ChannelConfig)config);
        boolean closed = false;
        Throwable exception = null;
        try {
            int localRead;
            while ((localRead = this.doReadMessages(this.readBuf)) != 0) {
                if (localRead < 0) {
                    closed = true;
                } else {
                    allocHandle.incMessagesRead((int)localRead);
                    if (allocHandle.continueReading()) continue;
                }
                break;
            }
        }
        catch (Throwable t) {
            exception = t;
        }
        boolean readData = false;
        int size = this.readBuf.size();
        if (size > 0) {
            readData = true;
            for (int i = 0; i < size; ++i) {
                this.readPending = false;
                pipeline.fireChannelRead((Object)this.readBuf.get((int)i));
            }
            this.readBuf.clear();
            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();
        }
        if (exception != null) {
            if (exception instanceof IOException) {
                closed = true;
            }
            pipeline.fireExceptionCaught((Throwable)exception);
        }
        if (closed) {
            if (!this.isOpen()) return;
            this.unsafe().close((ChannelPromise)this.unsafe().voidPromise());
            return;
        }
        if (!this.readPending && !config.isAutoRead()) {
            if (readData) return;
            if (!this.isActive()) return;
        }
        this.read();
    }

    protected abstract int doReadMessages(List<Object> var1) throws Exception;
}

