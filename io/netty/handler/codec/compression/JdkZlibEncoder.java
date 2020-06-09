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
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class JdkZlibEncoder
extends ZlibEncoder {
    private final ZlibWrapper wrapper;
    private final Deflater deflater;
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;
    private final CRC32 crc = new CRC32();
    private static final byte[] gzipHeader = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0};
    private boolean writeHeader = true;

    public JdkZlibEncoder() {
        this((int)6);
    }

    public JdkZlibEncoder(int compressionLevel) {
        this((ZlibWrapper)ZlibWrapper.ZLIB, (int)compressionLevel);
    }

    public JdkZlibEncoder(ZlibWrapper wrapper) {
        this((ZlibWrapper)wrapper, (int)6);
    }

    public JdkZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        if (wrapper == null) {
            throw new NullPointerException((String)"wrapper");
        }
        if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException((String)("wrapper '" + (Object)((Object)ZlibWrapper.ZLIB_OR_NONE) + "' is not allowed for compression."));
        }
        this.wrapper = wrapper;
        this.deflater = new Deflater((int)compressionLevel, (boolean)(wrapper != ZlibWrapper.ZLIB));
    }

    public JdkZlibEncoder(byte[] dictionary) {
        this((int)6, (byte[])dictionary);
    }

    public JdkZlibEncoder(int compressionLevel, byte[] dictionary) {
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        if (dictionary == null) {
            throw new NullPointerException((String)"dictionary");
        }
        this.wrapper = ZlibWrapper.ZLIB;
        this.deflater = new Deflater((int)compressionLevel);
        this.deflater.setDictionary((byte[])dictionary);
    }

    @Override
    public ChannelFuture close() {
        return this.close((ChannelPromise)this.ctx().newPromise());
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx();
        EventExecutor executor = ctx.executor();
        if (executor.inEventLoop()) {
            return this.finishEncode((ChannelHandlerContext)ctx, (ChannelPromise)promise);
        }
        ChannelPromise p = ctx.newPromise();
        executor.execute((Runnable)new Runnable((JdkZlibEncoder)this, (ChannelPromise)p, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$p;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ JdkZlibEncoder this$0;
            {
                this.this$0 = this$0;
                this.val$p = channelPromise;
                this.val$promise = channelPromise2;
            }

            public void run() {
                ChannelFuture f = JdkZlibEncoder.access$100((JdkZlibEncoder)this.this$0, (ChannelHandlerContext)JdkZlibEncoder.access$000((JdkZlibEncoder)this.this$0), (ChannelPromise)this.val$p);
                f.addListener((GenericFutureListener<? extends Future<? super Void>>)new io.netty.channel.ChannelPromiseNotifier((ChannelPromise[])new ChannelPromise[]{this.val$promise}));
            }
        });
        return p;
    }

    private ChannelHandlerContext ctx() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx != null) return ctx;
        throw new IllegalStateException((String)"not added to a pipeline");
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf uncompressed, ByteBuf out) throws Exception {
        byte[] inAry;
        int offset;
        if (this.finished) {
            out.writeBytes((ByteBuf)uncompressed);
            return;
        }
        int len = uncompressed.readableBytes();
        if (len == 0) {
            return;
        }
        if (uncompressed.hasArray()) {
            inAry = uncompressed.array();
            offset = uncompressed.arrayOffset() + uncompressed.readerIndex();
            uncompressed.skipBytes((int)len);
        } else {
            inAry = new byte[len];
            uncompressed.readBytes((byte[])inAry);
            offset = 0;
        }
        if (this.writeHeader) {
            this.writeHeader = false;
            if (this.wrapper == ZlibWrapper.GZIP) {
                out.writeBytes((byte[])gzipHeader);
            }
        }
        if (this.wrapper == ZlibWrapper.GZIP) {
            this.crc.update((byte[])inAry, (int)offset, (int)len);
        }
        this.deflater.setInput((byte[])inAry, (int)offset, (int)len);
        do {
            this.deflate((ByteBuf)out);
            if (this.deflater.needsInput()) {
                return;
            }
            if (out.isWritable()) continue;
            out.ensureWritable((int)out.writerIndex());
        } while (true);
    }

    @Override
    protected final ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
        int sizeEstimate = (int)Math.ceil((double)((double)msg.readableBytes() * 1.001)) + 12;
        if (!this.writeHeader) return ctx.alloc().heapBuffer((int)sizeEstimate);
        switch (4.$SwitchMap$io$netty$handler$codec$compression$ZlibWrapper[this.wrapper.ordinal()]) {
            case 1: {
                return ctx.alloc().heapBuffer((int)(sizeEstimate += gzipHeader.length));
            }
            case 2: {
                sizeEstimate += 2;
                break;
            }
        }
        return ctx.alloc().heapBuffer((int)sizeEstimate);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ChannelFuture f = this.finishEncode((ChannelHandlerContext)ctx, (ChannelPromise)ctx.newPromise());
        f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((JdkZlibEncoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ JdkZlibEncoder this$0;
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
        ctx.executor().schedule((Runnable)new Runnable((JdkZlibEncoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ JdkZlibEncoder this$0;
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

    private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
        if (this.finished) {
            promise.setSuccess();
            return promise;
        }
        this.finished = true;
        ByteBuf footer = ctx.alloc().heapBuffer();
        if (this.writeHeader && this.wrapper == ZlibWrapper.GZIP) {
            this.writeHeader = false;
            footer.writeBytes((byte[])gzipHeader);
        }
        this.deflater.finish();
        while (!this.deflater.finished()) {
            this.deflate((ByteBuf)footer);
            if (footer.isWritable()) continue;
            ctx.write((Object)footer);
            footer = ctx.alloc().heapBuffer();
        }
        if (this.wrapper == ZlibWrapper.GZIP) {
            int crcValue = (int)this.crc.getValue();
            int uncBytes = this.deflater.getTotalIn();
            footer.writeByte((int)crcValue);
            footer.writeByte((int)(crcValue >>> 8));
            footer.writeByte((int)(crcValue >>> 16));
            footer.writeByte((int)(crcValue >>> 24));
            footer.writeByte((int)uncBytes);
            footer.writeByte((int)(uncBytes >>> 8));
            footer.writeByte((int)(uncBytes >>> 16));
            footer.writeByte((int)(uncBytes >>> 24));
        }
        this.deflater.end();
        return ctx.writeAndFlush((Object)footer, (ChannelPromise)promise);
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    private void deflate(ByteBuf out) {
        int numBytes;
        if (PlatformDependent.javaVersion() < 7) {
            this.deflateJdk6((ByteBuf)out);
        }
        do {
            int writerIndex = out.writerIndex();
            numBytes = this.deflater.deflate((byte[])out.array(), (int)(out.arrayOffset() + writerIndex), (int)out.writableBytes(), (int)2);
            out.writerIndex((int)(writerIndex + numBytes));
        } while (numBytes > 0);
    }

    private void deflateJdk6(ByteBuf out) {
        int numBytes;
        do {
            int writerIndex = out.writerIndex();
            numBytes = this.deflater.deflate((byte[])out.array(), (int)(out.arrayOffset() + writerIndex), (int)out.writableBytes());
            out.writerIndex((int)(writerIndex + numBytes));
        } while (numBytes > 0);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    static /* synthetic */ ChannelHandlerContext access$000(JdkZlibEncoder x0) {
        return x0.ctx();
    }

    static /* synthetic */ ChannelFuture access$100(JdkZlibEncoder x0, ChannelHandlerContext x1, ChannelPromise x2) {
        return x0.finishEncode((ChannelHandlerContext)x1, (ChannelPromise)x2);
    }
}

