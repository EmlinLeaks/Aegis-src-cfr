/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.compress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketCompressor
extends MessageToByteEncoder<ByteBuf> {
    private final BungeeZlib zlib = CompressFactory.zlib.newInstance();
    private int threshold = 256;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.zlib.init((boolean)true, (int)-1);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.zlib.free();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int origSize = msg.readableBytes();
        if (origSize < this.threshold) {
            DefinedPacket.writeVarInt((int)0, (ByteBuf)out);
            out.writeBytes((ByteBuf)msg);
            return;
        }
        DefinedPacket.writeVarInt((int)origSize, (ByteBuf)out);
        this.zlib.process((ByteBuf)msg, (ByteBuf)out);
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}

