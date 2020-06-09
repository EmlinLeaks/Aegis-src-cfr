/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ViewDistance
extends DefinedPacket {
    private int distance;

    @Override
    public void read(ByteBuf buf) {
        this.distance = DefinedPacket.readVarInt((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf) {
        DefinedPacket.writeVarInt((int)this.distance, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((ViewDistance)this);
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "ViewDistance(distance=" + this.getDistance() + ")";
    }

    public ViewDistance() {
    }

    public ViewDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ViewDistance)) {
            return false;
        }
        ViewDistance other = (ViewDistance)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getDistance() == other.getDistance()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ViewDistance;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        return result * 59 + this.getDistance();
    }
}

