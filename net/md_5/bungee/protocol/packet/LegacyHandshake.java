/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LegacyHandshake
extends DefinedPacket {
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
        handler.handle((LegacyHandshake)this);
    }

    @Override
    public String toString() {
        return "LegacyHandshake()";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LegacyHandshake)) {
            return false;
        }
        LegacyHandshake other = (LegacyHandshake)o;
        if (other.canEqual((Object)this)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LegacyHandshake;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}

