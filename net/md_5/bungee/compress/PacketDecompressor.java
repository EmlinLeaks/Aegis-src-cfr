/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.compress;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketDecompressor
extends MessageToMessageDecoder<ByteBuf> {
    private final BungeeZlib zlib = CompressFactory.zlib.newInstance();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.zlib.init((boolean)false, (int)0);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.zlib.free();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = DefinedPacket.readVarInt((ByteBuf)in);
        if (size == 0) {
            out.add((Object)in.slice().retain());
            in.skipBytes((int)in.readableBytes());
            return;
        }
        ByteBuf decompressed = ctx.alloc().directBuffer();
        try {
            this.zlib.process((ByteBuf)in, (ByteBuf)decompressed);
            Preconditions.checkState((boolean)(decompressed.readableBytes() == size), (Object)"Decompressed packet size mismatch");
            out.add((Object)decompressed);
            decompressed = null;
            return;
        }
        finally {
            if (decompressed != null) {
                decompressed.release();
            }
        }
    }
}

