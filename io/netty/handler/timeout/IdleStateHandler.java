/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.timeout;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IdleStateHandler
extends ChannelDuplexHandler {
    private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos((long)1L);
    private final ChannelFutureListener writeListener = new ChannelFutureListener((IdleStateHandler)this){
        final /* synthetic */ IdleStateHandler this$0;
        {
            this.this$0 = this$0;
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            IdleStateHandler.access$002((IdleStateHandler)this.this$0, (long)this.this$0.ticksInNanos());
            IdleStateHandler.access$102((IdleStateHandler)this.this$0, (boolean)IdleStateHandler.access$202((IdleStateHandler)this.this$0, (boolean)true));
        }
    };
    private final boolean observeOutput;
    private final long readerIdleTimeNanos;
    private final long writerIdleTimeNanos;
    private final long allIdleTimeNanos;
    private ScheduledFuture<?> readerIdleTimeout;
    private long lastReadTime;
    private boolean firstReaderIdleEvent = true;
    private ScheduledFuture<?> writerIdleTimeout;
    private long lastWriteTime;
    private boolean firstWriterIdleEvent = true;
    private ScheduledFuture<?> allIdleTimeout;
    private boolean firstAllIdleEvent = true;
    private byte state;
    private boolean reading;
    private long lastChangeCheckTimeStamp;
    private int lastMessageHashCode;
    private long lastPendingWriteBytes;
    private long lastFlushProgress;

    public IdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        this((long)((long)readerIdleTimeSeconds), (long)((long)writerIdleTimeSeconds), (long)((long)allIdleTimeSeconds), (TimeUnit)TimeUnit.SECONDS);
    }

    public IdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        this((boolean)false, (long)readerIdleTime, (long)writerIdleTime, (long)allIdleTime, (TimeUnit)unit);
    }

    public IdleStateHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException((String)"unit");
        }
        this.observeOutput = observeOutput;
        this.readerIdleTimeNanos = readerIdleTime <= 0L ? 0L : Math.max((long)unit.toNanos((long)readerIdleTime), (long)MIN_TIMEOUT_NANOS);
        this.writerIdleTimeNanos = writerIdleTime <= 0L ? 0L : Math.max((long)unit.toNanos((long)writerIdleTime), (long)MIN_TIMEOUT_NANOS);
        if (allIdleTime <= 0L) {
            this.allIdleTimeNanos = 0L;
            return;
        }
        this.allIdleTimeNanos = Math.max((long)unit.toNanos((long)allIdleTime), (long)MIN_TIMEOUT_NANOS);
    }

    public long getReaderIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis((long)this.readerIdleTimeNanos);
    }

    public long getWriterIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis((long)this.writerIdleTimeNanos);
    }

    public long getAllIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis((long)this.allIdleTimeNanos);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (!ctx.channel().isActive()) return;
        if (!ctx.channel().isRegistered()) return;
        this.initialize((ChannelHandlerContext)ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            this.initialize((ChannelHandlerContext)ctx);
        }
        super.channelRegistered((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.initialize((ChannelHandlerContext)ctx);
        super.channelActive((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
        super.channelInactive((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
            this.reading = true;
            this.firstAllIdleEvent = true;
            this.firstReaderIdleEvent = true;
        }
        ctx.fireChannelRead((Object)msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if ((this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) && this.reading) {
            this.lastReadTime = this.ticksInNanos();
            this.reading = false;
        }
        ctx.fireChannelReadComplete();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.writerIdleTimeNanos <= 0L && this.allIdleTimeNanos <= 0L) {
            ctx.write((Object)msg, (ChannelPromise)promise);
            return;
        }
        ctx.write((Object)msg, (ChannelPromise)promise.unvoid()).addListener((GenericFutureListener<? extends Future<? super Void>>)this.writeListener);
    }

    private void initialize(ChannelHandlerContext ctx) {
        switch (this.state) {
            case 1: 
            case 2: {
                return;
            }
        }
        this.state = 1;
        this.initOutputChanged((ChannelHandlerContext)ctx);
        this.lastReadTime = this.lastWriteTime = this.ticksInNanos();
        if (this.readerIdleTimeNanos > 0L) {
            this.readerIdleTimeout = this.schedule((ChannelHandlerContext)ctx, (Runnable)new ReaderIdleTimeoutTask((IdleStateHandler)this, (ChannelHandlerContext)ctx), (long)this.readerIdleTimeNanos, (TimeUnit)TimeUnit.NANOSECONDS);
        }
        if (this.writerIdleTimeNanos > 0L) {
            this.writerIdleTimeout = this.schedule((ChannelHandlerContext)ctx, (Runnable)new WriterIdleTimeoutTask((IdleStateHandler)this, (ChannelHandlerContext)ctx), (long)this.writerIdleTimeNanos, (TimeUnit)TimeUnit.NANOSECONDS);
        }
        if (this.allIdleTimeNanos <= 0L) return;
        this.allIdleTimeout = this.schedule((ChannelHandlerContext)ctx, (Runnable)new AllIdleTimeoutTask((IdleStateHandler)this, (ChannelHandlerContext)ctx), (long)this.allIdleTimeNanos, (TimeUnit)TimeUnit.NANOSECONDS);
    }

    long ticksInNanos() {
        return System.nanoTime();
    }

    ScheduledFuture<?> schedule(ChannelHandlerContext ctx, Runnable task, long delay, TimeUnit unit) {
        return ctx.executor().schedule((Runnable)task, (long)delay, (TimeUnit)unit);
    }

    private void destroy() {
        this.state = (byte)2;
        if (this.readerIdleTimeout != null) {
            this.readerIdleTimeout.cancel((boolean)false);
            this.readerIdleTimeout = null;
        }
        if (this.writerIdleTimeout != null) {
            this.writerIdleTimeout.cancel((boolean)false);
            this.writerIdleTimeout = null;
        }
        if (this.allIdleTimeout == null) return;
        this.allIdleTimeout.cancel((boolean)false);
        this.allIdleTimeout = null;
    }

    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        ctx.fireUserEventTriggered((Object)evt);
    }

    protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
        switch (2.$SwitchMap$io$netty$handler$timeout$IdleState[state.ordinal()]) {
            case 1: {
                IdleStateEvent idleStateEvent;
                if (first) {
                    idleStateEvent = IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT;
                    return idleStateEvent;
                }
                idleStateEvent = IdleStateEvent.ALL_IDLE_STATE_EVENT;
                return idleStateEvent;
            }
            case 2: {
                IdleStateEvent idleStateEvent;
                if (first) {
                    idleStateEvent = IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT;
                    return idleStateEvent;
                }
                idleStateEvent = IdleStateEvent.READER_IDLE_STATE_EVENT;
                return idleStateEvent;
            }
            case 3: {
                IdleStateEvent idleStateEvent;
                if (first) {
                    idleStateEvent = IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT;
                    return idleStateEvent;
                }
                idleStateEvent = IdleStateEvent.WRITER_IDLE_STATE_EVENT;
                return idleStateEvent;
            }
        }
        throw new IllegalArgumentException((String)("Unhandled: state=" + (Object)((Object)state) + ", first=" + first));
    }

    private void initOutputChanged(ChannelHandlerContext ctx) {
        if (!this.observeOutput) return;
        Channel channel = ctx.channel();
        Channel.Unsafe unsafe = channel.unsafe();
        ChannelOutboundBuffer buf = unsafe.outboundBuffer();
        if (buf == null) return;
        this.lastMessageHashCode = System.identityHashCode((Object)buf.current());
        this.lastPendingWriteBytes = buf.totalPendingWriteBytes();
        this.lastFlushProgress = buf.currentProgress();
    }

    private boolean hasOutputChanged(ChannelHandlerContext ctx, boolean first) {
        ChannelOutboundBuffer buf;
        Channel.Unsafe unsafe;
        long flushProgress;
        Channel channel;
        if (!this.observeOutput) return false;
        if (this.lastChangeCheckTimeStamp != this.lastWriteTime) {
            this.lastChangeCheckTimeStamp = this.lastWriteTime;
            if (!first) {
                return true;
            }
        }
        if ((buf = (unsafe = (channel = ctx.channel()).unsafe()).outboundBuffer()) == null) return false;
        int messageHashCode = System.identityHashCode((Object)buf.current());
        long pendingWriteBytes = buf.totalPendingWriteBytes();
        if (messageHashCode != this.lastMessageHashCode || pendingWriteBytes != this.lastPendingWriteBytes) {
            this.lastMessageHashCode = messageHashCode;
            this.lastPendingWriteBytes = pendingWriteBytes;
            if (!first) {
                return true;
            }
        }
        if ((flushProgress = buf.currentProgress()) == this.lastFlushProgress) return false;
        this.lastFlushProgress = flushProgress;
        if (first) return false;
        return true;
    }

    static /* synthetic */ long access$002(IdleStateHandler x0, long x1) {
        x0.lastWriteTime = x1;
        return x0.lastWriteTime;
    }

    static /* synthetic */ boolean access$102(IdleStateHandler x0, boolean x1) {
        x0.firstWriterIdleEvent = x1;
        return x0.firstWriterIdleEvent;
    }

    static /* synthetic */ boolean access$202(IdleStateHandler x0, boolean x1) {
        x0.firstAllIdleEvent = x1;
        return x0.firstAllIdleEvent;
    }

    static /* synthetic */ long access$300(IdleStateHandler x0) {
        return x0.readerIdleTimeNanos;
    }

    static /* synthetic */ boolean access$400(IdleStateHandler x0) {
        return x0.reading;
    }

    static /* synthetic */ long access$500(IdleStateHandler x0) {
        return x0.lastReadTime;
    }

    static /* synthetic */ ScheduledFuture access$602(IdleStateHandler x0, ScheduledFuture x1) {
        x0.readerIdleTimeout = x1;
        return x0.readerIdleTimeout;
    }

    static /* synthetic */ boolean access$700(IdleStateHandler x0) {
        return x0.firstReaderIdleEvent;
    }

    static /* synthetic */ boolean access$702(IdleStateHandler x0, boolean x1) {
        x0.firstReaderIdleEvent = x1;
        return x0.firstReaderIdleEvent;
    }

    static /* synthetic */ long access$000(IdleStateHandler x0) {
        return x0.lastWriteTime;
    }

    static /* synthetic */ long access$800(IdleStateHandler x0) {
        return x0.writerIdleTimeNanos;
    }

    static /* synthetic */ ScheduledFuture access$902(IdleStateHandler x0, ScheduledFuture x1) {
        x0.writerIdleTimeout = x1;
        return x0.writerIdleTimeout;
    }

    static /* synthetic */ boolean access$100(IdleStateHandler x0) {
        return x0.firstWriterIdleEvent;
    }

    static /* synthetic */ boolean access$1000(IdleStateHandler x0, ChannelHandlerContext x1, boolean x2) {
        return x0.hasOutputChanged((ChannelHandlerContext)x1, (boolean)x2);
    }

    static /* synthetic */ long access$1100(IdleStateHandler x0) {
        return x0.allIdleTimeNanos;
    }

    static /* synthetic */ ScheduledFuture access$1202(IdleStateHandler x0, ScheduledFuture x1) {
        x0.allIdleTimeout = x1;
        return x0.allIdleTimeout;
    }

    static /* synthetic */ boolean access$200(IdleStateHandler x0) {
        return x0.firstAllIdleEvent;
    }
}

