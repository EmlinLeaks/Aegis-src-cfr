/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.string;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class LineEncoder
extends MessageToMessageEncoder<CharSequence> {
    private final Charset charset;
    private final byte[] lineSeparator;

    public LineEncoder() {
        this((LineSeparator)LineSeparator.DEFAULT, (Charset)CharsetUtil.UTF_8);
    }

    public LineEncoder(LineSeparator lineSeparator) {
        this((LineSeparator)lineSeparator, (Charset)CharsetUtil.UTF_8);
    }

    public LineEncoder(Charset charset) {
        this((LineSeparator)LineSeparator.DEFAULT, (Charset)charset);
    }

    public LineEncoder(LineSeparator lineSeparator, Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, (String)"charset");
        this.lineSeparator = ObjectUtil.checkNotNull(lineSeparator, (String)"lineSeparator").value().getBytes((Charset)charset);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        ByteBuf buffer = ByteBufUtil.encodeString((ByteBufAllocator)ctx.alloc(), (CharBuffer)CharBuffer.wrap((CharSequence)msg), (Charset)this.charset, (int)this.lineSeparator.length);
        buffer.writeBytes((byte[])this.lineSeparator);
        out.add((Object)buffer);
    }
}

