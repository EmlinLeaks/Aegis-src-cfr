/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class KeepAlive
extends DefinedPacket {
    private long randomId;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.randomId = protocolVersion >= 340 ? buf.readLong() : (long)KeepAlive.readVarInt((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= 340) {
            buf.writeLong((long)this.randomId);
            return;
        }
        KeepAlive.writeVarInt((int)((int)this.randomId), (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((KeepAlive)this);
    }

    public long getRandomId() {
        return this.randomId;
    }

    public void setRandomId(long randomId) {
        this.randomId = randomId;
    }

    @Override
    public String toString() {
        return "KeepAlive(randomId=" + this.getRandomId() + ")";
    }

    public KeepAlive() {
    }

    public KeepAlive(long randomId) {
        this.randomId = randomId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeepAlive)) {
            return false;
        }
        KeepAlive other = (KeepAlive)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getRandomId() == other.getRandomId()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof KeepAlive;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $randomId = this.getRandomId();
        return result * 59 + (int)($randomId >>> 32 ^ $randomId);
    }
}

