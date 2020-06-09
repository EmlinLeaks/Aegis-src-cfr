/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioChannel;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.util.internal.StringUtil;
import java.io.IOException;

public abstract class AbstractOioByteChannel
extends AbstractOioChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)false);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';

    protected AbstractOioByteChannel(Channel parent) {
        super((Channel)parent);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    protected abstract boolean isInputShutdown();

    protected abstract ChannelFuture shutdownInput();

    private void closeOnRead(ChannelPipeline pipeline) {
        if (!this.isOpen()) return;
        if (Boolean.TRUE.equals((Object)this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
            this.shutdownInput();
            pipeline.fireUserEventTriggered((Object)ChannelInputShutdownEvent.INSTANCE);
        } else {
            this.unsafe().close((ChannelPromise)this.unsafe().voidPromise());
        }
        pipeline.fireUserEventTriggered((Object)ChannelInputShutdownReadComplete.INSTANCE);
    }

    private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, RecvByteBufAllocator.Handle allocHandle) {
        if (byteBuf != null) {
            if (byteBuf.isReadable()) {
                this.readPending = false;
                pipeline.fireChannelRead((Object)byteBuf);
            } else {
                byteBuf.release();
            }
        }
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
        pipeline.fireExceptionCaught((Throwable)cause);
        if (!close) {
            if (!(cause instanceof IOException)) return;
        }
        this.closeOnRead((ChannelPipeline)pipeline);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doRead() {
        ChannelConfig config = this.config();
        if (this.isInputShutdown()) return;
        if (!this.readPending) {
            return;
        }
        this.readPending = false;
        ChannelPipeline pipeline = this.pipeline();
        ByteBufAllocator allocator = config.getAllocator();
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        allocHandle.reset((ChannelConfig)config);
        ByteBuf byteBuf = null;
        boolean close = false;
        boolean readData = false;
        try {
            byteBuf = allocHandle.allocate((ByteBufAllocator)allocator);
            do {
                int maxCapacity;
                allocHandle.lastBytesRead((int)this.doReadBytes((ByteBuf)byteBuf));
                if (allocHandle.lastBytesRead() <= 0) {
                    if (byteBuf.isReadable()) break;
                    byteBuf.release();
                    byteBuf = null;
                    boolean bl = close = allocHandle.lastBytesRead() < 0;
                    if (!close) break;
                    this.readPending = false;
                    break;
                }
                readData = true;
                int available = this.available();
                if (available <= 0) break;
                if (byteBuf.isWritable()) continue;
                int capacity = byteBuf.capacity();
                if (capacity == (maxCapacity = byteBuf.maxCapacity())) {
                    allocHandle.incMessagesRead((int)1);
                    this.readPending = false;
                    pipeline.fireChannelRead((Object)byteBuf);
                    byteBuf = allocHandle.allocate((ByteBufAllocator)allocator);
                    continue;
                }
                int writerIndex = byteBuf.writerIndex();
                if (writerIndex + available > maxCapacity) {
                    byteBuf.capacity((int)maxCapacity);
                    continue;
                }
                byteBuf.ensureWritable((int)available);
            } while (allocHandle.continueReading());
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    this.readPending = false;
                    pipeline.fireChannelRead((Object)byteBuf);
                } else {
                    byteBuf.release();
                }
                byteBuf = null;
            }
            if (readData) {
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
            }
            if (!close) return;
            this.closeOnRead((ChannelPipeline)pipeline);
            return;
        }
        catch (Throwable t) {
            this.handleReadException((ChannelPipeline)pipeline, (ByteBuf)byteBuf, (Throwable)t, (boolean)close, (RecvByteBufAllocator.Handle)allocHandle);
            return;
        }
        finally {
            if (this.readPending || config.isAutoRead() || !readData && this.isActive()) {
                this.read();
            }
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        while ((msg = in.current()) != null) {
            if (msg instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf)msg;
                int readableBytes = buf.readableBytes();
                while (readableBytes > 0) {
                    this.doWriteBytes((ByteBuf)buf);
                    int newReadableBytes = buf.readableBytes();
                    in.progress((long)((long)(readableBytes - newReadableBytes)));
                    readableBytes = newReadableBytes;
                }
                in.remove();
                continue;
            }
            if (msg instanceof FileRegion) {
                FileRegion region = (FileRegion)msg;
                long transferred = region.transferred();
                this.doWriteFileRegion((FileRegion)region);
                in.progress((long)(region.transferred() - transferred));
                in.remove();
                continue;
            }
            in.remove((Throwable)new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg))));
        }
        return;
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) throws Exception {
        if (msg instanceof ByteBuf) return msg;
        if (!(msg instanceof FileRegion)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
        return msg;
    }

    protected abstract int available();

    protected abstract int doReadBytes(ByteBuf var1) throws Exception;

    protected abstract void doWriteBytes(ByteBuf var1) throws Exception;

    protected abstract void doWriteFileRegion(FileRegion var1) throws Exception;
}

