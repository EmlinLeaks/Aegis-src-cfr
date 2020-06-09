/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.jcraft.jzlib.Deflater
 *  com.jcraft.jzlib.JZlib
 *  com.jcraft.jzlib.JZlib$WrapperType
 */
package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.compression.JZlibEncoder;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibUtil;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.EmptyArrays;
import java.util.concurrent.TimeUnit;

public class JZlibEncoder
extends ZlibEncoder {
    private final int wrapperOverhead;
    private final Deflater z = new Deflater();
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;

    public JZlibEncoder() {
        this((int)6);
    }

    public JZlibEncoder(int compressionLevel) {
        this((ZlibWrapper)ZlibWrapper.ZLIB, (int)compressionLevel);
    }

    public JZlibEncoder(ZlibWrapper wrapper) {
        this((ZlibWrapper)wrapper, (int)6);
    }

    public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        this((ZlibWrapper)wrapper, (int)compressionLevel, (int)15, (int)8);
    }

    public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        if (windowBits < 9) throw new IllegalArgumentException((String)("windowBits: " + windowBits + " (expected: 9-15)"));
        if (windowBits > 15) {
            throw new IllegalArgumentException((String)("windowBits: " + windowBits + " (expected: 9-15)"));
        }
        if (memLevel < 1) throw new IllegalArgumentException((String)("memLevel: " + memLevel + " (expected: 1-9)"));
        if (memLevel > 9) {
            throw new IllegalArgumentException((String)("memLevel: " + memLevel + " (expected: 1-9)"));
        }
        if (wrapper == null) {
            throw new NullPointerException((String)"wrapper");
        }
        if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException((String)("wrapper '" + (Object)((Object)ZlibWrapper.ZLIB_OR_NONE) + "' is not allowed for compression."));
        }
        int resultCode = this.z.init((int)compressionLevel, (int)windowBits, (int)memLevel, (JZlib.WrapperType)ZlibUtil.convertWrapperType((ZlibWrapper)wrapper));
        if (resultCode != 0) {
            ZlibUtil.fail((Deflater)this.z, (String)"initialization failure", (int)resultCode);
        }
        this.wrapperOverhead = ZlibUtil.wrapperOverhead((ZlibWrapper)wrapper);
    }

    public JZlibEncoder(byte[] dictionary) {
        this((int)6, (byte[])dictionary);
    }

    public JZlibEncoder(int compressionLevel, byte[] dictionary) {
        this((int)compressionLevel, (int)15, (int)8, (byte[])dictionary);
    }

    public JZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        if (windowBits < 9) throw new IllegalArgumentException((String)("windowBits: " + windowBits + " (expected: 9-15)"));
        if (windowBits > 15) {
            throw new IllegalArgumentException((String)("windowBits: " + windowBits + " (expected: 9-15)"));
        }
        if (memLevel < 1) throw new IllegalArgumentException((String)("memLevel: " + memLevel + " (expected: 1-9)"));
        if (memLevel > 9) {
            throw new IllegalArgumentException((String)("memLevel: " + memLevel + " (expected: 1-9)"));
        }
        if (dictionary == null) {
            throw new NullPointerException((String)"dictionary");
        }
        int resultCode = this.z.deflateInit((int)compressionLevel, (int)windowBits, (int)memLevel, (JZlib.WrapperType)JZlib.W_ZLIB);
        if (resultCode != 0) {
            ZlibUtil.fail((Deflater)this.z, (String)"initialization failure", (int)resultCode);
        } else {
            resultCode = this.z.deflateSetDictionary((byte[])dictionary, (int)dictionary.length);
            if (resultCode != 0) {
                ZlibUtil.fail((Deflater)this.z, (String)"failed to set the dictionary", (int)resultCode);
            }
        }
        this.wrapperOverhead = ZlibUtil.wrapperOverhead((ZlibWrapper)ZlibWrapper.ZLIB);
    }

    @Override
    public ChannelFuture close() {
        return this.close((ChannelPromise)this.ctx().channel().newPromise());
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx();
        EventExecutor executor = ctx.executor();
        if (executor.inEventLoop()) {
            return this.finishEncode((ChannelHandlerContext)ctx, (ChannelPromise)promise);
        }
        ChannelPromise p = ctx.newPromise();
        executor.execute((Runnable)new Runnable((JZlibEncoder)this, (ChannelPromise)p, (ChannelPromise)promise){
            final /* synthetic */ ChannelPromise val$p;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ JZlibEncoder this$0;
            {
                this.this$0 = this$0;
                this.val$p = channelPromise;
                this.val$promise = channelPromise2;
            }

            public void run() {
                ChannelFuture f = JZlibEncoder.access$100((JZlibEncoder)this.this$0, (ChannelHandlerContext)JZlibEncoder.access$000((JZlibEncoder)this.this$0), (ChannelPromise)this.val$p);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        if (this.finished) {
            out.writeBytes((ByteBuf)in);
            return;
        }
        int inputLength = in.readableBytes();
        if (inputLength == 0) {
            return;
        }
        try {
            int resultCode;
            int outputLength;
            boolean inHasArray = in.hasArray();
            this.z.avail_in = inputLength;
            if (inHasArray) {
                this.z.next_in = in.array();
                this.z.next_in_index = in.arrayOffset() + in.readerIndex();
            } else {
                byte[] array = new byte[inputLength];
                in.getBytes((int)in.readerIndex(), (byte[])array);
                this.z.next_in = array;
                this.z.next_in_index = 0;
            }
            int oldNextInIndex = this.z.next_in_index;
            int maxOutputLength = (int)Math.ceil((double)((double)inputLength * 1.001)) + 12 + this.wrapperOverhead;
            out.ensureWritable((int)maxOutputLength);
            this.z.avail_out = maxOutputLength;
            this.z.next_out = out.array();
            int oldNextOutIndex = this.z.next_out_index = out.arrayOffset() + out.writerIndex();
            try {
                resultCode = this.z.deflate((int)2);
            }
            finally {
                in.skipBytes((int)(this.z.next_in_index - oldNextInIndex));
            }
            if (resultCode != 0) {
                ZlibUtil.fail((Deflater)this.z, (String)"compression failure", (int)resultCode);
            }
            if ((outputLength = this.z.next_out_index - oldNextOutIndex) <= 0) return;
            out.writerIndex((int)(out.writerIndex() + outputLength));
            return;
        }
        finally {
            this.z.next_in = null;
            this.z.next_out = null;
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
        ChannelFuture f = this.finishEncode((ChannelHandlerContext)ctx, (ChannelPromise)ctx.newPromise());
        f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener((JZlibEncoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ JZlibEncoder this$0;
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
        ctx.executor().schedule((Runnable)new Runnable((JZlibEncoder)this, (ChannelHandlerContext)ctx, (ChannelPromise)promise){
            final /* synthetic */ ChannelHandlerContext val$ctx;
            final /* synthetic */ ChannelPromise val$promise;
            final /* synthetic */ JZlibEncoder this$0;
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
        try {
            ByteBuf footer;
            this.z.next_in = EmptyArrays.EMPTY_BYTES;
            this.z.next_in_index = 0;
            this.z.avail_in = 0;
            byte[] out = new byte[32];
            this.z.next_out = out;
            this.z.next_out_index = 0;
            this.z.avail_out = out.length;
            int resultCode = this.z.deflate((int)4);
            if (resultCode != 0 && resultCode != 1) {
                promise.setFailure((Throwable)ZlibUtil.deflaterException((Deflater)this.z, (String)"compression failure", (int)resultCode));
                ChannelPromise channelPromise = promise;
                return channelPromise;
            }
            if (this.z.next_out_index != 0) {
                footer = Unpooled.wrappedBuffer((byte[])out, (int)0, (int)this.z.next_out_index);
                return ctx.writeAndFlush((Object)footer, (ChannelPromise)promise);
            }
            footer = Unpooled.EMPTY_BUFFER;
            return ctx.writeAndFlush((Object)footer, (ChannelPromise)promise);
        }
        finally {
            this.z.deflateEnd();
            this.z.next_in = null;
            this.z.next_out = null;
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    static /* synthetic */ ChannelHandlerContext access$000(JZlibEncoder x0) {
        return x0.ctx();
    }

    static /* synthetic */ ChannelFuture access$100(JZlibEncoder x0, ChannelHandlerContext x1, ChannelPromise x2) {
        return x0.finishEncode((ChannelHandlerContext)x1, (ChannelPromise)x2);
    }
}

