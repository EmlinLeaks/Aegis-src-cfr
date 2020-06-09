/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class Kick
extends DefinedPacket {
    private String message;

    @Override
    public void read(ByteBuf buf) {
        this.message = Kick.readString((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf) {
        Kick.writeString((String)this.message, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Kick)this);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Kick(message=" + this.getMessage() + ")";
    }

    public Kick() {
    }

    public Kick(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Kick)) {
            return false;
        }
        Kick other = (Kick)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null) {
            if (other$message == null) return true;
            return false;
        }
        if (this$message.equals((Object)other$message)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Kick;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $message = this.getMessage();
        return result * 59 + ($message == null ? 43 : $message.hashCode());
    }
}

