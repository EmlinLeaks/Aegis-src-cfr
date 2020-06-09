/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;

public class LegacyDecoder
extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!in.isReadable()) {
            return;
        }
        in.markReaderIndex();
        short packetID = in.readUnsignedByte();
        if (packetID == 254) {
            out.add((Object)new PacketWrapper((DefinedPacket)new LegacyPing((boolean)(in.isReadable() && in.readUnsignedByte() == 1)), (ByteBuf)Unpooled.EMPTY_BUFFER));
            return;
        }
        if (packetID == 2 && in.isReadable()) {
            in.skipBytes((int)in.readableBytes());
            out.add((Object)new PacketWrapper((DefinedPacket)new LegacyHandshake(), (ByteBuf)Unpooled.EMPTY_BUFFER));
            return;
        }
        in.resetReaderIndex();
        ctx.pipeline().remove((ChannelHandler)this);
    }
}

