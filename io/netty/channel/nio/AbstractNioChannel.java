/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ScheduledFuture;

public abstract class AbstractNioChannel
extends AbstractChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
    private final SelectableChannel ch;
    protected final int readInterestOp;
    volatile SelectionKey selectionKey;
    boolean readPending;
    private final Runnable clearReadPendingRunnable = new Runnable((AbstractNioChannel)this){
        final /* synthetic */ AbstractNioChannel this$0;
        {
            this.this$0 = this$0;
        }

        public void run() {
            AbstractNioChannel.access$000((AbstractNioChannel)this.this$0);
        }
    };
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;

    protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super((Channel)parent);
        this.ch = ch;
        this.readInterestOp = readInterestOp;
        try {
            ch.configureBlocking((boolean)false);
            return;
        }
        catch (IOException e) {
            try {
                ch.close();
                throw new ChannelException((String)"Failed to enter non-blocking mode.", (Throwable)e);
            }
            catch (IOException e2) {
                logger.warn((String)"Failed to close a partially initialized socket.", (Throwable)e2);
            }
            throw new ChannelException((String)"Failed to enter non-blocking mode.", (Throwable)e);
        }
    }

    @Override
    public boolean isOpen() {
        return this.ch.isOpen();
    }

    @Override
    public NioUnsafe unsafe() {
        return (NioUnsafe)super.unsafe();
    }

    protected SelectableChannel javaChannel() {
        return this.ch;
    }

    @Override
    public NioEventLoop eventLoop() {
        return (NioEventLoop)super.eventLoop();
    }

    protected SelectionKey selectionKey() {
        if ($assertionsDisabled) return this.selectionKey;
        if (this.selectionKey != null) return this.selectionKey;
        throw new AssertionError();
    }

    @Deprecated
    protected boolean isReadPending() {
        return this.readPending;
    }

    @Deprecated
    protected void setReadPending(boolean readPending) {
        if (!this.isRegistered()) {
            this.readPending = readPending;
            return;
        }
        NioEventLoop eventLoop = this.eventLoop();
        if (eventLoop.inEventLoop()) {
            this.setReadPending0((boolean)readPending);
            return;
        }
        eventLoop.execute((Runnable)new Runnable((AbstractNioChannel)this, (boolean)readPending){
            final /* synthetic */ boolean val$readPending;
            final /* synthetic */ AbstractNioChannel this$0;
            {
                this.this$0 = this$0;
                this.val$readPending = bl;
            }

            public void run() {
                AbstractNioChannel.access$100((AbstractNioChannel)this.this$0, (boolean)this.val$readPending);
            }
        });
    }

    protected final void clearReadPending() {
        if (!this.isRegistered()) {
            this.readPending = false;
            return;
        }
        NioEventLoop eventLoop = this.eventLoop();
        if (eventLoop.inEventLoop()) {
            this.clearReadPending0();
            return;
        }
        eventLoop.execute((Runnable)this.clearReadPendingRunnable);
    }

    private void setReadPending0(boolean readPending) {
        this.readPending = readPending;
        if (readPending) return;
        ((AbstractNioUnsafe)this.unsafe()).removeReadOp();
    }

    private void clearReadPending0() {
        this.readPending = false;
        ((AbstractNioUnsafe)this.unsafe()).removeReadOp();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof NioEventLoop;
    }

    @Override
    protected void doRegister() throws Exception {
        boolean selected = false;
        do {
            try {
                this.selectionKey = this.javaChannel().register((Selector)this.eventLoop().unwrappedSelector(), (int)0, (Object)this);
                return;
            }
            catch (CancelledKeyException e) {
                if (selected) throw e;
                this.eventLoop().selectNow();
                selected = true;
                continue;
            }
            break;
        } while (true);
    }

    @Override
    protected void doDeregister() throws Exception {
        this.eventLoop().cancel((SelectionKey)this.selectionKey());
    }

    @Override
    protected void doBeginRead() throws Exception {
        SelectionKey selectionKey = this.selectionKey;
        if (!selectionKey.isValid()) {
            return;
        }
        this.readPending = true;
        int interestOps = selectionKey.interestOps();
        if ((interestOps & this.readInterestOp) != 0) return;
        selectionKey.interestOps((int)(interestOps | this.readInterestOp));
    }

    protected abstract boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

    protected abstract void doFinishConnect() throws Exception;

    protected final ByteBuf newDirectBuffer(ByteBuf buf) {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.safeRelease((Object)buf);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer((int)readableBytes);
            directBuf.writeBytes((ByteBuf)buf, (int)buf.readerIndex(), (int)readableBytes);
            ReferenceCountUtil.safeRelease((Object)buf);
            return directBuf;
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf == null) return buf;
        directBuf.writeBytes((ByteBuf)buf, (int)buf.readerIndex(), (int)readableBytes);
        ReferenceCountUtil.safeRelease((Object)buf);
        return directBuf;
    }

    protected final ByteBuf newDirectBuffer(ReferenceCounted holder, ByteBuf buf) {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.safeRelease((Object)holder);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer((int)readableBytes);
            directBuf.writeBytes((ByteBuf)buf, (int)buf.readerIndex(), (int)readableBytes);
            ReferenceCountUtil.safeRelease((Object)holder);
            return directBuf;
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf != null) {
            directBuf.writeBytes((ByteBuf)buf, (int)buf.readerIndex(), (int)readableBytes);
            ReferenceCountUtil.safeRelease((Object)holder);
            return directBuf;
        }
        if (holder == buf) return buf;
        buf.retain();
        ReferenceCountUtil.safeRelease((Object)holder);
        return buf;
    }

    @Override
    protected void doClose() throws Exception {
        ScheduledFuture<?> future;
        ChannelPromise promise = this.connectPromise;
        if (promise != null) {
            promise.tryFailure((Throwable)new ClosedChannelException());
            this.connectPromise = null;
        }
        if ((future = this.connectTimeoutFuture) == null) return;
        future.cancel((boolean)false);
        this.connectTimeoutFuture = null;
    }

    static /* synthetic */ void access$000(AbstractNioChannel x0) {
        x0.clearReadPending0();
    }

    static /* synthetic */ void access$100(AbstractNioChannel x0, boolean x1) {
        x0.setReadPending0((boolean)x1);
    }

    static /* synthetic */ ChannelPromise access$200(AbstractNioChannel x0) {
        return x0.connectPromise;
    }

    static /* synthetic */ ChannelPromise access$202(AbstractNioChannel x0, ChannelPromise x1) {
        x0.connectPromise = x1;
        return x0.connectPromise;
    }

    static /* synthetic */ SocketAddress access$302(AbstractNioChannel x0, SocketAddress x1) {
        x0.requestedRemoteAddress = x1;
        return x0.requestedRemoteAddress;
    }

    static /* synthetic */ ScheduledFuture access$402(AbstractNioChannel x0, ScheduledFuture x1) {
        x0.connectTimeoutFuture = x1;
        return x0.connectTimeoutFuture;
    }

    static /* synthetic */ ScheduledFuture access$400(AbstractNioChannel x0) {
        return x0.connectTimeoutFuture;
    }

    static /* synthetic */ SocketAddress access$300(AbstractNioChannel x0) {
        return x0.requestedRemoteAddress;
    }
}

