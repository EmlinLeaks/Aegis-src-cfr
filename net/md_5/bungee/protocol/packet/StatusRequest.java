/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class StatusRequest
extends DefinedPacket {
    @Override
    public void read(ByteBuf buf) {
    }

    @Override
    public void write(ByteBuf buf) {
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((StatusRequest)this);
    }

    @Override
    public String toString() {
        return "StatusRequest()";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StatusRequest)) {
            return false;
        }
        StatusRequest other = (StatusRequest)o;
        if (other.canEqual((Object)this)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof StatusRequest;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}

