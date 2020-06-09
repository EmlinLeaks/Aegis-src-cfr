/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.StringUtil;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioByteChannel
extends AbstractNioChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata((boolean)false, (int)16);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(FileRegion.class) + ')';
    private final Runnable flushTask = new Runnable((AbstractNioByteChannel)this){
        final /* synthetic */ AbstractNioByteChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            ((AbstractNioChannel.AbstractNioUnsafe)this.this$0.unsafe()).flush0();
        }
    };
    private boolean inputClosedSeenErrorOnRead;

    protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
        super((Channel)parent, (SelectableChannel)ch, (int)1);
    }

    protected abstract ChannelFuture shutdownInput();

    protected boolean isInputShutdown0() {
        return false;
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioByteUnsafe((AbstractNioByteChannel)this);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    final boolean shouldBreakReadReady(ChannelConfig config) {
        if (!this.isInputShutdown0()) return false;
        if (this.inputClosedSeenErrorOnRead) return true;
        if (AbstractNioByteChannel.isAllowHalfClosure((ChannelConfig)config)) return false;
        return true;
    }

    private static boolean isAllowHalfClosure(ChannelConfig config) {
        if (!(config instanceof SocketChannelConfig)) return false;
        if (!((SocketChannelConfig)config).isAllowHalfClosure()) return false;
        return true;
    }

    protected final int doWrite0(ChannelOutboundBuffer in) throws Exception {
        Object msg = in.current();
        if (msg != null) return this.doWriteInternal((ChannelOutboundBuffer)in, (Object)in.current());
        return 0;
    }

    private int doWriteInternal(ChannelOutboundBuffer in, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (!buf.isReadable()) {
                in.remove();
                return 0;
            }
            int localFlushedAmount = this.doWriteBytes((ByteBuf)buf);
            if (localFlushedAmount <= 0) return Integer.MAX_VALUE;
            in.progress((long)((long)localFlushedAmount));
            if (buf.isReadable()) return 1;
            in.remove();
            return 1;
        }
        if (!(msg instanceof FileRegion)) throw new Error();
        FileRegion region = (FileRegion)msg;
        if (region.transferred() >= region.count()) {
            in.remove();
            return 0;
        }
        long localFlushedAmount = this.doWriteFileRegion((FileRegion)region);
        if (localFlushedAmount <= 0L) return Integer.MAX_VALUE;
        in.progress((long)localFlushedAmount);
        if (region.transferred() < region.count()) return 1;
        in.remove();
        return 1;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        int writeSpinCount = this.config().getWriteSpinCount();
        do {
            if ((msg = in.current()) != null) continue;
            this.clearOpWrite();
            return;
        } while ((writeSpinCount -= this.doWriteInternal((ChannelOutboundBuffer)in, (Object)msg)) > 0);
        this.incompleteWrite((boolean)(writeSpinCount < 0));
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            if (!buf.isDirect()) return this.newDirectBuffer((ByteBuf)buf);
            return msg;
        }
        if (!(msg instanceof FileRegion)) throw new UnsupportedOperationException((String)("unsupported message type: " + StringUtil.simpleClassName((Object)msg) + EXPECTED_TYPES));
        return msg;
    }

    protected final void incompleteWrite(boolean setOpWrite) {
        if (setOpWrite) {
            this.setOpWrite();
            return;
        }
        this.clearOpWrite();
        this.eventLoop().execute((Runnable)this.flushTask);
    }

    protected abstract long doWriteFileRegion(FileRegion var1) throws Exception;

    protected abstract int doReadBytes(ByteBuf var1) throws Exception;

    protected abstract int doWriteBytes(ByteBuf var1) throws Exception;

    protected final void setOpWrite() {
        SelectionKey key = this.selectionKey();
        if (!key.isValid()) {
            return;
        }
        int interestOps = key.interestOps();
        if ((interestOps & 4) != 0) return;
        key.interestOps((int)(interestOps | 4));
    }

    protected final void clearOpWrite() {
        SelectionKey key = this.selectionKey();
        if (!key.isValid()) {
            return;
        }
        int interestOps = key.interestOps();
        if ((interestOps & 4) == 0) return;
        key.interestOps((int)(interestOps & -5));
    }

    static /* synthetic */ boolean access$000(ChannelConfig x0) {
        return AbstractNioByteChannel.isAllowHalfClosure((ChannelConfig)x0);
    }

    static /* synthetic */ boolean access$102(AbstractNioByteChannel x0, boolean x1) {
        x0.inputClosedSeenErrorOnRead = x1;
        return x0.inputClosedSeenErrorOnRead;
    }
}

