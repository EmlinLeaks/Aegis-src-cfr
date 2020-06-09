/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.jpountz.lz4.LZ4Compressor
 *  net.jpountz.lz4.LZ4Exception
 *  net.jpountz.lz4.LZ4Factory
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.compression.Lz4FrameEncoder;
import io.netty.handler.codec.compression.Lz4XXHash32;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.zip.Checksum;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;

public class Lz4FrameEncoder
extends MessageToByteEncoder<ByteBuf> {
    static final int DEFAULT_MAX_ENCODE_SIZE = Integer.MAX_VALUE;
    private final int blockSize;
    private final LZ4Compressor compressor;
    private final ByteBufChecksum checksum;
    private final int compressionLevel;
    private ByteBuf buffer;
    private final int maxEncodeSize;
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;

    public Lz4FrameEncoder() {
        this((boolean)false);
    }

    public Lz4FrameEncoder(boolean highCompressor) {
        this((LZ4Factory)LZ4Factory.fastestInstance(), (boolean)highCompressor, (int)65536, (Checksum)new Lz4XXHash32((int)-1756908916));
    }

    public Lz4FrameEncoder(LZ4Factory factory, boolean highCompressor, int blockSize, Checksum checksum) {
        this((LZ4Factory)factory, (boolean)highCompressor, (int)blockSize, (Checksum)checksum, (int)Integer.MAX_VALUE);
    }

    public Lz4FrameEncoder(LZ4Factory factory, boolean highCompressor, int blockSize, Checksum checksum, int maxEncodeSize) {
        if (factory == null) {
            throw new NullPointerException((String)"factory");
        }
        if (checksum == null) {
            throw new NullPointerException((String)"checksum");
        }
        this.compressor = highCompressor ? factory.highCompressor() : factory.fastCompressor();
        this.checksum = ByteBufChecksum.wrapChecksum((Checksum)checksum);
        this.compressionLevel = Lz4FrameEncoder.compressionLevel((int)blockSize);
        this.blockSize = blockSize;
        this.maxEncodeSize = ObjectUtil.checkPositive((int)maxEncodeSize, (String)"maxEncodeSize");
        this.finished = false;
    }

    private static int compressionLevel(int blockSize) {
        if (blockSize < 64 || blockSize > 33554432) throw new IllegalArgumentException((String)String.format((String)"blockSize: %d (expected: %d-%d)", (Object[])new Object[]{Integer.valueOf((int)blockSize), Integer.valueOf((int)64), Integer.valueOf((int)33554432)}));
        int compressionLevel = 32 - Integer.numberOfLeadingZeros((int)(blockSize - 1));
        return Math.max((int)0, (int)(compressionLevel - 10));
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) {
        return this.allocateBuffer((ChannelHandlerContext)ctx, (ByteBuf)msg, (boolean)preferDirect, (boolean)true);
    }

    private ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect, boolean allowEmptyReturn) {
        int targetBufSize = 0;
        int remaining = msg.readableBytes() + this.buffer.readableBytes();
        if (remaining < 0) {
            throw new EncoderException((String)"too much data to allocate a buffer for compression");
        }
        while (remaining > 0) {
            int curSize = Math.min((int)this.blockSize, (int)remaining);
            remaining -= curSize;
            targetBufSize += this.compressor.maxCompressedLength((int)curSize) + 21;
        }
        if (targetBufSize > this.maxEncodeSize || 0 > targetBufSize) {
            throw new EncoderException((String)String.format((String)"requested encode buffer size (%d bytes) exceeds the maximum allowable size (%d bytes)", (Object[])new Object[]{Integer.valueOf((int)targetBufSize), Integer.valueOf((int)this.maxEncodeSize)}));
        }
        if (allowEmptyReturn && targetBufSize < this.blockSize) {
            return Unpooled.EMPTY_BUFFER;
        }
        if (!preferDirect) return ctx.alloc().heapBuffer((int)targetBufSize, (int)targetBufSize);
        return ctx.alloc().ioBuffer((int)targetBufSize, (int)targetBufSize);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int length;
        if (this.finished) {
            if (!out.isWritable((int)in.readableBytes())) {
                throw new IllegalStateException((String)"encode finished and not enough space to write remaining data");
            }
            out.writeBytes((ByteBuf)in);
            return;
        }
        ByteBuf buffer = this.buffer;
        while ((length = in.readableBytes()) > 0) {
            int nextChunkSize = Math.min((int)length, (int)buffer.writableBytes());
            in.readBytes((ByteBuf)buffer, (int)nextChunkSize);
            if (buffer.isWritable()) continue;
            this.flushBufferedData((ByteBuf)out);
        }
    }

    private void flushBufferedData(ByteBuf out) {
        int blockType;
        int compressedLength;
        int flushableBytes = this.buffer.readableBytes();
        if (flushableBytes == 0) {
            return;
        }
        this.checksum.reset();
        this.checksum.update((ByteBuf)this.buffer, (int)this.buffer.readerIndex(), (int)flushableBytes);
        int check = (int)this.checksum.getValue();
        int bufSize = this.compressor.maxCompressedLength((int)flushableBytes) + 21;
        out.ensureWritable((int)bufSize);
        int idx = out.writerIndex();
        try {
            ByteBuffer outNioBuffer = out.internalNioBuffer((int)(idx + 21), (int)(out.writableBytes() - 21));
            int pos = outNioBuffer.position();
            this.compressor.compress((ByteBuffer)this.buffer.internalNioBuffer((int)this.buffer.readerIndex(), (int)flushableBytes), (ByteBuffer)outNioBuffer);
            compressedLength = outNioBuffer.position() - pos;
        }
        catch (LZ4Exception e) {
            throw new CompressionException((Throwable)e);
        }
        if (compressedLength >= flushableBytes) {
            blockType = 16;
            compressedLength = flushableBytes;
            out.setBytes((int)(idx + 21), (ByteBuf)this.buffer, (int)0, (int)flushableBytes);
        } else {
            blockType = 32;
        }
        out.setLong((int)idx, (long)5501767354678207339L);
        out.setByte((int)(idx + 8), (int)((byte)(blockType | this.compressionLevel)));
        out.setIntLE((int)(idx + 9), (int)compressedLength);
        out.setIntLE((int)(idx + 13), (int)flushableBytes);
        out.setIntLE((int)(idx + 17), (int)check);
        out.writerIndex((int)(idx + 21 + compressedLength));
        this.buffer.clear();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.buffer != null && this.buffer.isReadable()) {
            ByteBuf buf = this.allocateBuffer((ChannelHandlerContext)ctx, (ByteBuf)Unpooled.EMPTY_BUFFER, (boolean)this.isPreferDirect(), (boolean)false);
            this.flushBufferedData((ByteBuf)buf);
            ctx.write((Object)buf);
        }
        ctx.flush();
    }

    private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
        if (this.finished) {
            promise.setSuccess();
            return promise;
        }
        this.finished = true;
        ByteBuf footer = ctx.alloc().heapBuffer((int)(this.compressor.maxCompressedLength((int)this.buffer.readableBytes()) + 21));
        this.flushBufferedData((ByteBuf)footer);
        int idx = footer.writerIndex();
        footer.setLong((int)idx, (long)5501767354678207339L);
        footer.setByte((int)(idx + 8), (int)((byte)(16 | this.compressionLevel)));
        footer.setInt((int)(idx + 9), (int)0);
        footer.setInt((int)(idx + 13), (int)0);
        footer.setInt((int)(idx + 17), (int)0);
        footer.writerIndex((int)(idx + 21));
        return ctx.writeAndFlush((Object)footer, (ChannelPromise)promise);
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
        executor.execute((Runnable)new Runnable((Lz4FrameEncoder)this, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ Lz4FrameEncoder this$0;
            {
                this.this$0 = this$0;
                this.val$promise = channelPromise;
            }

            public void run() {
                ChannelFuture f = Lz4FrameEncoder.access$100((Lz4FrameEncoder)this.this$0, (ChannelHandlerContext)Lz4FrameEncoder.access$000((Lz4FrameEncoder)this.this$0), (ChannelPromise)this.val$promise);
                f.addListener((GenericFutureListener<? extends Future<? super Void>>)new io.netty.channel.ChannelPromiseNotifier((ChannelPromise[])new ChannelPromise[]{this.val$promise}));
            }
        });
        return promise;
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ChannelFuture f = this.finishEncode((ChannelHandlerContext)ctx, (ChannelPromise)ctx.newPromise());
        f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((Lz4FrameEncoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ Lz4FrameEncoder this$0;
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
        ctx.executor().schedule((Runnable)new Runnable((Lz4FrameEncoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ Lz4FrameEncoder this$0;
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

    private ChannelHandlerContext ctx() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx != null) return ctx;
        throw new IllegalStateException((String)"not added to a pipeline");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.buffer = Unpooled.wrappedBuffer((byte[])new byte[this.blockSize]);
        this.buffer.clear();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved((ChannelHandlerContext)ctx);
        if (this.buffer == null) return;
        this.buffer.release();
        this.buffer = null;
    }

    final ByteBuf getBackingBuffer() {
        return this.buffer;
    }

    static /* synthetic */ ChannelHandlerContext access$000(Lz4FrameEncoder x0) {
        return x0.ctx();
    }

    static /* synthetic */ ChannelFuture access$100(Lz4FrameEncoder x0, ChannelHandlerContext x1, ChannelPromise x2) {
        return x0.finishEncode((ChannelHandlerContext)x1, (ChannelPromise)x2);
    }
}

