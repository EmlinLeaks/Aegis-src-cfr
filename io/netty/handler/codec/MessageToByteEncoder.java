/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class MessageToByteEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    private final boolean preferDirect;

    protected MessageToByteEncoder() {
        this((boolean)true);
    }

    protected MessageToByteEncoder(Class<? extends I> outboundMessageType) {
        this(outboundMessageType, (boolean)true);
    }

    protected MessageToByteEncoder(boolean preferDirect) {
        this.matcher = TypeParameterMatcher.find((Object)this, MessageToByteEncoder.class, (String)"I");
        this.preferDirect = preferDirect;
    }

    protected MessageToByteEncoder(Class<? extends I> outboundMessageType, boolean preferDirect) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
        this.preferDirect = preferDirect;
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match((Object)msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ReferenceCounted buf = null;
        try {
            if (this.acceptOutboundMessage((Object)msg)) {
                Object cast = msg;
                buf = this.allocateBuffer((ChannelHandlerContext)ctx, cast, (boolean)this.preferDirect);
                try {
                    this.encode((ChannelHandlerContext)ctx, cast, (ByteBuf)buf);
                }
                finally {
                    ReferenceCountUtil.release((Object)cast);
                }
                if (((ByteBuf)buf).isReadable()) {
                    ctx.write((Object)buf, (ChannelPromise)promise);
                } else {
                    buf.release();
                    ctx.write((Object)Unpooled.EMPTY_BUFFER, (ChannelPromise)promise);
                }
                buf = null;
                return;
            }
            ctx.write((Object)msg, (ChannelPromise)promise);
            return;
        }
        catch (EncoderException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new EncoderException((Throwable)e);
        }
        finally {
            if (buf != null) {
                buf.release();
            }
        }
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, I msg, boolean preferDirect) throws Exception {
        if (!preferDirect) return ctx.alloc().heapBuffer();
        return ctx.alloc().ioBuffer();
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, ByteBuf var3) throws Exception;

    protected boolean isPreferDirect() {
        return this.preferDirect;
    }
}

