/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ClientStatus
extends DefinedPacket {
    private byte payload;

    @Override
    public void read(ByteBuf buf) {
        this.payload = buf.readByte();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte((int)this.payload);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((ClientStatus)this);
    }

    public byte getPayload() {
        return this.payload;
    }

    public void setPayload(byte payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ClientStatus(payload=" + this.getPayload() + ")";
    }

    public ClientStatus() {
    }

    public ClientStatus(byte payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientStatus)) {
            return false;
        }
        ClientStatus other = (ClientStatus)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getPayload() == other.getPayload()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClientStatus;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        return result * 59 + this.getPayload();
    }
}

