/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.Bzip2BitWriter;
import io.netty.handler.codec.compression.Bzip2BlockCompressor;
import io.netty.handler.codec.compression.Bzip2Encoder;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Bzip2Encoder
extends MessageToByteEncoder<ByteBuf> {
    private State currentState = State.INIT;
    private final Bzip2BitWriter writer = new Bzip2BitWriter();
    private final int streamBlockSize;
    private int streamCRC;
    private Bzip2BlockCompressor blockCompressor;
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;

    public Bzip2Encoder() {
        this((int)9);
    }

    public Bzip2Encoder(int blockSizeMultiplier) {
        if (blockSizeMultiplier < 1) throw new IllegalArgumentException((String)("blockSizeMultiplier: " + blockSizeMultiplier + " (expected: 1-9)"));
        if (blockSizeMultiplier > 9) {
            throw new IllegalArgumentException((String)("blockSizeMultiplier: " + blockSizeMultiplier + " (expected: 1-9)"));
        }
        this.streamBlockSize = blockSizeMultiplier * 100000;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        if (this.finished) {
            out.writeBytes((ByteBuf)in);
            return;
        }
        block6 : do {
            switch (4.$SwitchMap$io$netty$handler$codec$compression$Bzip2Encoder$State[this.currentState.ordinal()]) {
                case 1: {
                    out.ensureWritable((int)4);
                    out.writeMedium((int)4348520);
                    out.writeByte((int)(48 + this.streamBlockSize / 100000));
                    this.currentState = State.INIT_BLOCK;
                }
                case 2: {
                    this.blockCompressor = new Bzip2BlockCompressor((Bzip2BitWriter)this.writer, (int)this.streamBlockSize);
                    this.currentState = State.WRITE_DATA;
                }
                case 3: {
                    if (!in.isReadable()) {
                        return;
                    }
                    Bzip2BlockCompressor blockCompressor = this.blockCompressor;
                    int length = Math.min((int)in.readableBytes(), (int)blockCompressor.availableSize());
                    int bytesWritten = blockCompressor.write((ByteBuf)in, (int)in.readerIndex(), (int)length);
                    in.skipBytes((int)bytesWritten);
                    if (!blockCompressor.isFull()) {
                        if (!in.isReadable()) return;
                        continue block6;
                    }
                    this.currentState = State.CLOSE_BLOCK;
                }
                case 4: {
                    this.closeBlock((ByteBuf)out);
                    this.currentState = State.INIT_BLOCK;
                    continue block6;
                }
            }
            break;
        } while (true);
        throw new IllegalStateException();
    }

    private void closeBlock(ByteBuf out) {
        Bzip2BlockCompressor blockCompressor = this.blockCompressor;
        if (blockCompressor.isEmpty()) return;
        blockCompressor.close((ByteBuf)out);
        int blockCRC = blockCompressor.crc();
        this.streamCRC = (this.streamCRC << 1 | this.streamCRC >>> 31) ^ blockCRC;
    }

    public boolean isClosed() {
        return this.finished;
    }

    public ChannelFuture close() {
        return this.close((ChannelPromise)this.ctx().newPromise());
    }

    public ChannelFuture close(ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx();
        EventExecutor executor = ctx.executor();
        if (executor.inEventLoop()) {
            return this.finishEncode((ChannelHandlerContext)ctx, (ChannelPromise)promise);
        }
        executor.execute((Runnable)new Runnable((Bzip2Encoder)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ Bzip2Encoder this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                ChannelFuture f = Bzip2Encoder.access$100((Bzip2Encoder)this.this$0, (ChannelHandlerContext)Bzip2Encoder.access$000((Bzip2Encoder)this.this$0), (ChannelPromise)this.val$promise);
                f.addListener((GenericFutureListener<? extends Future<? super Void>>)new io.netty.channel.ChannelPromiseNotifier((ChannelPromise[])new ChannelPromise[]{this.val$promise}));
            }
        });
        return promise;
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ChannelFuture f = this.finishEncode((ChannelHandlerContext)ctx, (ChannelPromise)ctx.newPromise());
        f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((Bzip2Encoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ Bzip2Encoder this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
                this.val$promise = channelPromise;
            }

            public void operationComplete(ChannelFuture f) throws Exception {
                this.val$ctx.close((ChannelPromise)this.val$promise);
            }
        });
        if (f.isDone()) return;
        ctx.executor().schedule((Runnable)new Runnable((Bzip2Encoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ Bzip2Encoder this$0;
            {
                this.this$0 = this$0;
                this.val$ctx = channelHandlerContext;
                this.val$promise = channelPromise;
            }

            public void run() {
                this.val$ctx.close((ChannelPromise)this.val$promise);
            }
        }, (long)10L, (TimeUnit)TimeUnit.SECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
        if (this.finished) {
            promise.setSuccess();
            return promise;
        }
        this.finished = true;
        ByteBuf footer = ctx.alloc().buffer();
        this.closeBlock((ByteBuf)footer);
        int streamCRC = this.streamCRC;
        Bzip2BitWriter writer = this.writer;
        try {
            writer.writeBits((ByteBuf)footer, (int)24, (long)1536581L);
            writer.writeBits((ByteBuf)footer, (int)24, (long)3690640L);
            writer.writeInt((ByteBuf)footer, (int)streamCRC);
            writer.flush((ByteBuf)footer);
            return ctx.writeAndFlush((Object)footer, (ChannelPromise)promise);
        }
        finally {
            this.blockCompressor = null;
        }
    }

    private ChannelHandlerContext ctx() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx != null) return ctx;
        throw new IllegalStateException((String)"not added to a pipeline");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    static /* synthetic */ ChannelHandlerContext access$000(Bzip2Encoder x0) {
        return x0.ctx();
    }

    static /* synthetic */ ChannelFuture access$100(Bzip2Encoder x0, ChannelHandlerContext x1, ChannelPromise x2) {
        return x0.finishEncode((ChannelHandlerContext)x1, (ChannelPromise)x2);
    }
}

