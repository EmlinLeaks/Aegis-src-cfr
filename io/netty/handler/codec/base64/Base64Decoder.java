/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import java.util.List;

@ChannelHandler.Sharable
public class Base64Decoder
extends MessageToMessageDecoder<ByteBuf> {
    private final Base64Dialect dialect;

    public Base64Decoder() {
        this((Base64Dialect)Base64Dialect.STANDARD);
    }

    public Base64Decoder(Base64Dialect dialect) {
        if (dialect == null) {
            throw new NullPointerException((String)"dialect");
        }
        this.dialect = dialect;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add((Object)Base64.decode((ByteBuf)msg, (int)msg.readerIndex(), (int)msg.readableBytes(), (Base64Dialect)this.dialect));
    }
}

