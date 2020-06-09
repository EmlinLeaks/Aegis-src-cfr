/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

public class MinecraftEncoder
extends MessageToByteEncoder<DefinedPacket> {
    private Protocol protocol;
    private boolean server;
    private int protocolVersion;

    @Override
    protected void encode(ChannelHandlerContext ctx, DefinedPacket msg, ByteBuf out) throws Exception {
        Protocol.DirectionData prot = this.server ? this.protocol.TO_CLIENT : this.protocol.TO_SERVER;
        DefinedPacket.writeVarInt((int)prot.getId(msg.getClass(), (int)this.protocolVersion), (ByteBuf)out);
        msg.write((ByteBuf)out, (ProtocolConstants.Direction)prot.getDirection(), (int)this.protocolVersion);
    }

    public MinecraftEncoder(Protocol protocol, boolean server, int protocolVersion) {
        this.protocol = protocol;
        this.server = server;
        this.protocolVersion = protocolVersion;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}

