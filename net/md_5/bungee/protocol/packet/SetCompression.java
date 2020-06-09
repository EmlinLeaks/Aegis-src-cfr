/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class SetCompression
extends DefinedPacket {
    private int threshold;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.threshold = DefinedPacket.readVarInt((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        DefinedPacket.writeVarInt((int)this.threshold, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((SetCompression)this);
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "SetCompression(threshold=" + this.getThreshold() + ")";
    }

    public SetCompression() {
    }

    public SetCompression(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SetCompression)) {
            return false;
        }
        SetCompression other = (SetCompression)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getThreshold() == other.getThreshold()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof SetCompression;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        return result * 59 + this.getThreshold();
    }
}

