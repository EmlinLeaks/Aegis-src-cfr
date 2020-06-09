/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import net.md_5.bungee.protocol.DefinedPacket;
import xyz.yooniks.aegis.AddressBlocker;
import xyz.yooniks.aegis.Aegis;

public class Varint21FrameDecoder
extends ByteToMessageDecoder {
    private static final AddressBlocker ADDRESS_BLOCKER = Aegis.getInstance().getAddressBlocker();
    private static boolean DIRECT_WARNING;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        InetAddress address = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress();
        if (ADDRESS_BLOCKER.isBlocked((InetAddress)address)) {
            ctx.close();
            in.clear();
            return;
        }
        try {
            in.markReaderIndex();
            int i = 0;
            do {
                if (i >= 3) {
                    ctx.close();
                    in.clear();
                    ADDRESS_BLOCKER.block((InetAddress)address);
                    System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " length wider than 21-bit"));
                    return;
                }
                if (!in.isReadable()) {
                    in.resetReaderIndex();
                    return;
                }
                byte read = in.readByte();
                if (read >= 0) {
                    in.resetReaderIndex();
                    int length = DefinedPacket.readVarInt((ByteBuf)in);
                    if (length == 0) {
                        return;
                    }
                    if (in.readableBytes() < length) {
                        in.resetReaderIndex();
                        return;
                    }
                    out.add((Object)in.readRetainedSlice((int)length));
                    return;
                }
                ++i;
            } while (true);
        }
        catch (Exception ex) {
            ctx.close();
            in.clear();
            ADDRESS_BLOCKER.block((InetAddress)address);
            System.out.println((String)("{CasualProtector} " + address.getHostAddress() + " var21FrameDecoder exception"));
        }
    }
}

