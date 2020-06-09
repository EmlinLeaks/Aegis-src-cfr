/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.protocol.DefinedPacket;

@ChannelHandler.Sharable
public class Varint21LengthFieldPrepender
extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int bodyLen = msg.readableBytes();
        int headerLen = Varint21LengthFieldPrepender.varintSize((int)bodyLen);
        out.ensureWritable((int)(headerLen + bodyLen));
        DefinedPacket.writeVarInt((int)bodyLen, (ByteBuf)out);
        out.writeBytes((ByteBuf)msg);
    }

    private static int varintSize(int paramInt) {
        if ((paramInt & -128) == 0) {
            return 1;
        }
        if ((paramInt & -16384) == 0) {
            return 2;
        }
        if ((paramInt & -2097152) == 0) {
            return 3;
        }
        if ((paramInt & -268435456) != 0) return 5;
        return 4;
    }
}

