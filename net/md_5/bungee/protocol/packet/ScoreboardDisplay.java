/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ScoreboardDisplay
extends DefinedPacket {
    private byte position;
    private String name;

    @Override
    public void read(ByteBuf buf) {
        this.position = buf.readByte();
        this.name = ScoreboardDisplay.readString((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte((int)this.position);
        ScoreboardDisplay.writeString((String)this.name, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((ScoreboardDisplay)this);
    }

    public byte getPosition() {
        return this.position;
    }

    public String getName() {
        return this.name;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ScoreboardDisplay(position=" + this.getPosition() + ", name=" + this.getName() + ")";
    }

    public ScoreboardDisplay() {
    }

    public ScoreboardDisplay(byte position, String name) {
        this.position = position;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreboardDisplay)) {
            return false;
        }
        ScoreboardDisplay other = (ScoreboardDisplay)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getPosition() != other.getPosition()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null) {
            if (other$name == null) return true;
            return false;
        }
        if (this$name.equals((Object)other$name)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ScoreboardDisplay;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPosition();
        String $name = this.getName();
        return result * 59 + ($name == null ? 43 : $name.hashCode());
    }
}

