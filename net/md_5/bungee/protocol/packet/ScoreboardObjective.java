/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;

public class ScoreboardObjective
extends DefinedPacket {
    private String name;
    private String value;
    private HealthDisplay type;
    private byte action;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.name = ScoreboardObjective.readString((ByteBuf)buf);
        this.action = buf.readByte();
        if (this.action != 0) {
            if (this.action != 2) return;
        }
        this.value = ScoreboardObjective.readString((ByteBuf)buf);
        if (protocolVersion >= 393) {
            this.type = HealthDisplay.values()[ScoreboardObjective.readVarInt((ByteBuf)buf)];
            return;
        }
        this.type = HealthDisplay.fromString((String)ScoreboardObjective.readString((ByteBuf)buf));
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        ScoreboardObjective.writeString((String)this.name, (ByteBuf)buf);
        buf.writeByte((int)this.action);
        if (this.action != 0) {
            if (this.action != 2) return;
        }
        ScoreboardObjective.writeString((String)this.value, (ByteBuf)buf);
        if (protocolVersion >= 393) {
            ScoreboardObjective.writeVarInt((int)this.type.ordinal(), (ByteBuf)buf);
            return;
        }
        ScoreboardObjective.writeString((String)this.type.toString(), (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((ScoreboardObjective)this);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public HealthDisplay getType() {
        return this.type;
    }

    public byte getAction() {
        return this.action;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(HealthDisplay type) {
        this.type = type;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "ScoreboardObjective(name=" + this.getName() + ", value=" + this.getValue() + ", type=" + (Object)((Object)this.getType()) + ", action=" + this.getAction() + ")";
    }

    public ScoreboardObjective() {
    }

    public ScoreboardObjective(String name, String value, HealthDisplay type, byte action) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreboardObjective)) {
            return false;
        }
        ScoreboardObjective other = (ScoreboardObjective)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals((Object)other$value)) {
            return false;
        }
        HealthDisplay this$type = this.getType();
        HealthDisplay other$type = other.getType();
        if (this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)((Object)other$type))) {
            return false;
        }
        if (this.getAction() == other.getAction()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ScoreboardObjective;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        HealthDisplay $type = this.getType();
        result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
        return result * 59 + this.getAction();
    }
}

