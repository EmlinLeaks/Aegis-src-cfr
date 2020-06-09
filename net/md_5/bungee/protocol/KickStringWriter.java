/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class KickStringWriter
extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {
        out.writeByte((int)255);
        out.writeShort((int)msg.length());
        char[] arrc = msg.toCharArray();
        int n = arrc.length;
        int n2 = 0;
        while (n2 < n) {
            char c = arrc[n2];
            out.writeChar((int)c);
            ++n2;
        }
    }
}

