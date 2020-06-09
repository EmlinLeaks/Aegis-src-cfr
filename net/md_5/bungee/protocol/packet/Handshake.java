/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class Handshake
extends DefinedPacket {
    private int protocolVersion;
    private String host;
    private int port;
    private int requestedProtocol;

    @Override
    public void read(ByteBuf buf) {
        this.protocolVersion = Handshake.readVarInt((ByteBuf)buf);
        this.host = Handshake.readString((ByteBuf)buf);
        this.port = buf.readUnsignedShort();
        this.requestedProtocol = Handshake.readVarInt((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf) {
        Handshake.writeVarInt((int)this.protocolVersion, (ByteBuf)buf);
        Handshake.writeString((String)this.host, (ByteBuf)buf);
        buf.writeShort((int)this.port);
        Handshake.writeVarInt((int)this.requestedProtocol, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Handshake)this);
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public int getRequestedProtocol() {
        return this.requestedProtocol;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRequestedProtocol(int requestedProtocol) {
        this.requestedProtocol = requestedProtocol;
    }

    @Override
    public String toString() {
        return "Handshake(protocolVersion=" + this.getProtocolVersion() + ", host=" + this.getHost() + ", port=" + this.getPort() + ", requestedProtocol=" + this.getRequestedProtocol() + ")";
    }

    public Handshake() {
    }

    public Handshake(int protocolVersion, String host, int port, int requestedProtocol) {
        this.protocolVersion = protocolVersion;
        this.host = host;
        this.port = port;
        this.requestedProtocol = requestedProtocol;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Handshake)) {
            return false;
        }
        Handshake other = (Handshake)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getProtocolVersion() != other.getProtocolVersion()) {
            return false;
        }
        String this$host = this.getHost();
        String other$host = other.getHost();
        if (this$host == null ? other$host != null : !this$host.equals((Object)other$host)) {
            return false;
        }
        if (this.getPort() != other.getPort()) {
            return false;
        }
        if (this.getRequestedProtocol() == other.getRequestedProtocol()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Handshake;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getProtocolVersion();
        String $host = this.getHost();
        result = result * 59 + ($host == null ? 43 : $host.hashCode());
        result = result * 59 + this.getPort();
        return result * 59 + this.getRequestedProtocol();
    }
}

