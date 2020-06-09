/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class PingPacket
extends DefinedPacket {
    private long time;

    @Override
    public void read(ByteBuf buf) {
        this.time = buf.readLong();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong((long)this.time);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((PingPacket)this);
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "PingPacket(time=" + this.getTime() + ")";
    }

    public PingPacket() {
    }

    public PingPacket(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PingPacket)) {
            return false;
        }
        PingPacket other = (PingPacket)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getTime() == other.getTime()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PingPacket;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $time = this.getTime();
        return result * 59 + (int)($time >>> 32 ^ $time);
    }
}

