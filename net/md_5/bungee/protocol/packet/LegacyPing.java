/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LegacyPing
extends DefinedPacket {
    private final boolean v1_5;

    @Override
    public void read(ByteBuf buf) {
        throw new UnsupportedOperationException((String)"Not supported yet.");
    }

    @Override
    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException((String)"Not supported yet.");
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((LegacyPing)this);
    }

    public boolean isV1_5() {
        return this.v1_5;
    }

    @Override
    public String toString() {
        return "LegacyPing(v1_5=" + this.isV1_5() + ")";
    }

    public LegacyPing(boolean v1_5) {
        this.v1_5 = v1_5;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LegacyPing)) {
            return false;
        }
        LegacyPing other = (LegacyPing)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isV1_5() == other.isV1_5()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LegacyPing;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        return result * 59 + (this.isV1_5() ? 79 : 97);
    }
}

