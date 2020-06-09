/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ByteToMessageDecoder
extends ChannelInboundHandlerAdapter {
    public static final Cumulator MERGE_CUMULATOR = new Cumulator(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            try {
                ByteBuf buffer = cumulation.writerIndex() > cumulation.maxCapacity() - in.readableBytes() || cumulation.refCnt() > 1 || cumulation.isReadOnly() ? ByteToMessageDecoder.expandCumulation((ByteBufAllocator)alloc, (ByteBuf)cumulation, (int)in.readableBytes()) : cumulation;
                buffer.writeBytes((ByteBuf)in);
                ByteBuf byteBuf = buffer;
                return byteBuf;
            }
            finally {
                in.release();
            }
        }
    };
    public static final Cumulator COMPOSITE_CUMULATOR = new Cumulator(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            try {
                ByteBuf composite;
                ByteBuf buffer;
                if (cumulation.refCnt() > 1) {
                    buffer = ByteToMessageDecoder.expandCumulation((ByteBufAllocator)alloc, (ByteBuf)cumulation, (int)in.readableBytes());
                    buffer.writeBytes((ByteBuf)in);
                } else {
                    if (cumulation instanceof io.netty.buffer.CompositeByteBuf) {
                        composite = (io.netty.buffer.CompositeByteBuf)cumulation;
                    } else {
                        composite = alloc.compositeBuffer((int)Integer.MAX_VALUE);
                        composite.addComponent((boolean)true, (ByteBuf)cumulation);
                    }
                    composite.addComponent((boolean)true, (ByteBuf)in);
                    in = null;
                    buffer = composite;
                }
                composite = buffer;
                return composite;
            }
            finally {
                if (in != null) {
                    in.release();
                }
            }
        }
    };
    private static final byte STATE_INIT = 0;
    private static final byte STATE_CALLING_CHILD_DECODE = 1;
    private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
    ByteBuf cumulation;
    private Cumulator cumulator = MERGE_CUMULATOR;
    private boolean singleDecode;
    private boolean first;
    private boolean firedChannelRead;
    private byte decodeState = 0;
    private int discardAfterReads = 16;
    private int numReads;

    protected ByteToMessageDecoder() {
        this.ensureNotSharable();
    }

    public void setSingleDecode(boolean singleDecode) {
        this.singleDecode = singleDecode;
    }

    public boolean isSingleDecode() {
        return this.singleDecode;
    }

    public void setCumulator(Cumulator cumulator) {
        if (cumulator == null) {
            throw new NullPointerException((String)"cumulator");
        }
        this.cumulator = cumulator;
    }

    public void setDiscardAfterReads(int discardAfterReads) {
        ObjectUtil.checkPositive((int)discardAfterReads, (String)"discardAfterReads");
        this.discardAfterReads = discardAfterReads;
    }

    protected int actualReadableBytes() {
        return this.internalBuffer().readableBytes();
    }

    protected ByteBuf internalBuffer() {
        if (this.cumulation == null) return Unpooled.EMPTY_BUFFER;
        return this.cumulation;
    }

    @Override
    public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (this.decodeState == 1) {
            this.decodeState = (byte)2;
            return;
        }
        ByteBuf buf = this.cumulation;
        if (buf != null) {
            this.cumulation = null;
            this.numReads = 0;
            int readable = buf.readableBytes();
            if (readable > 0) {
                ctx.fireChannelRead((Object)buf);
                ctx.fireChannelReadComplete();
            } else {
                buf.release();
            }
        }
        this.handlerRemoved0((ChannelHandlerContext)ctx);
    }

    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            ctx.fireChannelRead((Object)msg);
            return;
        }
        CodecOutputList out = CodecOutputList.newInstance();
        try {
            ByteBuf data = (ByteBuf)msg;
            this.first = this.cumulation == null;
            this.cumulation = this.first ? data : this.cumulator.cumulate((ByteBufAllocator)ctx.alloc(), (ByteBuf)this.cumulation, (ByteBuf)data);
            this.callDecode((ChannelHandlerContext)ctx, (ByteBuf)this.cumulation, (List<Object>)out);
            return;
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DecoderException((Throwable)e);
        }
        finally {
            if (this.cumulation != null && !this.cumulation.isReadable()) {
                this.numReads = 0;
                this.cumulation.release();
                this.cumulation = null;
            } else if (++this.numReads >= this.discardAfterReads) {
                this.numReads = 0;
                this.discardSomeReadBytes();
            }
            int size = out.size();
            this.firedChannelRead |= out.insertSinceRecycled();
            ByteToMessageDecoder.fireChannelRead((ChannelHandlerContext)ctx, (CodecOutputList)out, (int)size);
            out.recycle();
        }
    }

    static void fireChannelRead(ChannelHandlerContext ctx, List<Object> msgs, int numElements) {
        if (msgs instanceof CodecOutputList) {
            ByteToMessageDecoder.fireChannelRead((ChannelHandlerContext)ctx, (CodecOutputList)((CodecOutputList)msgs), (int)numElements);
            return;
        }
        int i = 0;
        while (i < numElements) {
            ctx.fireChannelRead((Object)msgs.get((int)i));
            ++i;
        }
    }

    static void fireChannelRead(ChannelHandlerContext ctx, CodecOutputList msgs, int numElements) {
        int i = 0;
        while (i < numElements) {
            ctx.fireChannelRead((Object)msgs.getUnsafe((int)i));
            ++i;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.numReads = 0;
        this.discardSomeReadBytes();
        if (!this.firedChannelRead && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        this.firedChannelRead = false;
        ctx.fireChannelReadComplete();
    }

    protected final void discardSomeReadBytes() {
        if (this.cumulation == null) return;
        if (this.first) return;
        if (this.cumulation.refCnt() != 1) return;
        this.cumulation.discardSomeReadBytes();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channelInputClosed((ChannelHandlerContext)ctx, (boolean)true);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ChannelInputShutdownEvent) {
            this.channelInputClosed((ChannelHandlerContext)ctx, (boolean)false);
        }
        super.userEventTriggered((ChannelHandlerContext)ctx, (Object)evt);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void channelInputClosed(ChannelHandlerContext ctx, boolean callChannelInactive) throws Exception {
        CodecOutputList out = CodecOutputList.newInstance();
        try {
            this.channelInputClosed((ChannelHandlerContext)ctx, (List<Object>)out);
            return;
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DecoderException((Throwable)e);
        }
        finally {
            try {
                if (this.cumulation != null) {
                    this.cumulation.release();
                    this.cumulation = null;
                }
                int size = out.size();
                ByteToMessageDecoder.fireChannelRead((ChannelHandlerContext)ctx, (CodecOutputList)out, (int)size);
                if (size > 0) {
                    ctx.fireChannelReadComplete();
                }
                if (callChannelInactive) {
                    ctx.fireChannelInactive();
                }
            }
            finally {
                out.recycle();
            }
        }
    }

    void channelInputClosed(ChannelHandlerContext ctx, List<Object> out) throws Exception {
        if (this.cumulation != null) {
            this.callDecode((ChannelHandlerContext)ctx, (ByteBuf)this.cumulation, out);
            this.decodeLast((ChannelHandlerContext)ctx, (ByteBuf)this.cumulation, out);
            return;
        }
        this.decodeLast((ChannelHandlerContext)ctx, (ByteBuf)Unpooled.EMPTY_BUFFER, out);
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            do lbl-1000: // 3 sources:
            {
                block7 : {
                    if (in.isReadable() == false) return;
                    outSize = out.size();
                    if (outSize > 0) {
                        ByteToMessageDecoder.fireChannelRead((ChannelHandlerContext)ctx, out, (int)outSize);
                        out.clear();
                        if (ctx.isRemoved()) {
                            return;
                        }
                        outSize = 0;
                    }
                    oldInputLength = in.readableBytes();
                    this.decodeRemovalReentryProtection((ChannelHandlerContext)ctx, (ByteBuf)in, out);
                    if (ctx.isRemoved()) {
                        return;
                    }
                    if (outSize != out.size()) break block7;
                    if (oldInputLength != in.readableBytes()) ** GOTO lbl-1000
                    return;
                }
                if (oldInputLength != in.readableBytes()) continue;
                throw new DecoderException((String)(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message."));
            } while (!this.isSingleDecode());
            return;
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception cause) {
            throw new DecoderException((Throwable)cause);
        }
    }

    protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void decodeRemovalReentryProtection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        boolean removePending;
        this.decodeState = 1;
        try {
            this.decode((ChannelHandlerContext)ctx, (ByteBuf)in, out);
            removePending = this.decodeState == 2;
        }
        catch (Throwable throwable) {
            boolean removePending2 = this.decodeState == 2;
            this.decodeState = 0;
            if (!removePending2) throw throwable;
            ByteToMessageDecoder.fireChannelRead((ChannelHandlerContext)ctx, out, (int)out.size());
            out.clear();
            this.handlerRemoved((ChannelHandlerContext)ctx);
            throw throwable;
        }
        this.decodeState = 0;
        if (!removePending) return;
        ByteToMessageDecoder.fireChannelRead((ChannelHandlerContext)ctx, out, (int)out.size());
        out.clear();
        this.handlerRemoved((ChannelHandlerContext)ctx);
    }

    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) return;
        this.decodeRemovalReentryProtection((ChannelHandlerContext)ctx, (ByteBuf)in, out);
    }

    static ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf cumulation, int readable) {
        ByteBuf oldCumulation = cumulation;
        cumulation = alloc.buffer((int)(oldCumulation.readableBytes() + readable));
        cumulation.writeBytes((ByteBuf)oldCumulation);
        oldCumulation.release();
        return cumulation;
    }
}

