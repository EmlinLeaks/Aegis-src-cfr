/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class FixedLengthFrameDecoder
extends ByteToMessageDecoder {
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        ObjectUtil.checkPositive((int)frameLength, (String)"frameLength");
        this.frameLength = frameLength;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode((ChannelHandlerContext)ctx, (ByteBuf)in);
        if (decoded == null) return;
        out.add((Object)decoded);
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in.readableBytes() >= this.frameLength) return in.readRetainedSlice((int)this.frameLength);
        return null;
    }
}

