/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketWrapper {
    public final DefinedPacket packet;
    public final ByteBuf buf;
    private boolean released;

    public void trySingleRelease() {
        if (this.released) return;
        this.buf.release();
        this.released = true;
    }

    public PacketWrapper(DefinedPacket packet, ByteBuf buf) {
        this.packet = packet;
        this.buf = buf;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }
}

