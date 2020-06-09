/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class BossBar
extends DefinedPacket {
    private UUID uuid;
    private int action;
    private String title;
    private float health;
    private int color;
    private int division;
    private byte flags;

    public BossBar(UUID uuid, int action) {
        this.uuid = uuid;
        this.action = action;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.uuid = BossBar.readUUID((ByteBuf)buf);
        this.action = BossBar.readVarInt((ByteBuf)buf);
        switch (this.action) {
            case 0: {
                this.title = BossBar.readString((ByteBuf)buf);
                this.health = buf.readFloat();
                this.color = BossBar.readVarInt((ByteBuf)buf);
                this.division = BossBar.readVarInt((ByteBuf)buf);
                this.flags = buf.readByte();
                return;
            }
            case 2: {
                this.health = buf.readFloat();
                return;
            }
            case 3: {
                this.title = BossBar.readString((ByteBuf)buf);
                return;
            }
            case 4: {
                this.color = BossBar.readVarInt((ByteBuf)buf);
                this.division = BossBar.readVarInt((ByteBuf)buf);
                return;
            }
            case 5: {
                this.flags = buf.readByte();
            }
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        BossBar.writeUUID((UUID)this.uuid, (ByteBuf)buf);
        BossBar.writeVarInt((int)this.action, (ByteBuf)buf);
        switch (this.action) {
            case 0: {
                BossBar.writeString((String)this.title, (ByteBuf)buf);
                buf.writeFloat((float)this.health);
                BossBar.writeVarInt((int)this.color, (ByteBuf)buf);
                BossBar.writeVarInt((int)this.division, (ByteBuf)buf);
                buf.writeByte((int)this.flags);
                return;
            }
            case 2: {
                buf.writeFloat((float)this.health);
                return;
            }
            case 3: {
                BossBar.writeString((String)this.title, (ByteBuf)buf);
                return;
            }
            case 4: {
                BossBar.writeVarInt((int)this.color, (ByteBuf)buf);
                BossBar.writeVarInt((int)this.division, (ByteBuf)buf);
                return;
            }
            case 5: {
                buf.writeByte((int)this.flags);
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((BossBar)this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getAction() {
        return this.action;
    }

    public String getTitle() {
        return this.title;
    }

    public float getHealth() {
        return this.health;
    }

    public int getColor() {
        return this.color;
    }

    public int getDivision() {
        return this.division;
    }

    public byte getFlags() {
        return this.flags;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return "BossBar(uuid=" + this.getUuid() + ", action=" + this.getAction() + ", title=" + this.getTitle() + ", health=" + this.getHealth() + ", color=" + this.getColor() + ", division=" + this.getDivision() + ", flags=" + this.getFlags() + ")";
    }

    public BossBar() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BossBar)) {
            return false;
        }
        BossBar other = (BossBar)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        UUID this$uuid = this.getUuid();
        UUID other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !((Object)this$uuid).equals((Object)other$uuid)) {
            return false;
        }
        if (this.getAction() != other.getAction()) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals((Object)other$title)) {
            return false;
        }
        if (Float.compare((float)this.getHealth(), (float)other.getHealth()) != 0) {
            return false;
        }
        if (this.getColor() != other.getColor()) {
            return false;
        }
        if (this.getDivision() != other.getDivision()) {
            return false;
        }
        if (this.getFlags() == other.getFlags()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BossBar;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UUID $uuid = this.getUuid();
        result = result * 59 + ($uuid == null ? 43 : ((Object)$uuid).hashCode());
        result = result * 59 + this.getAction();
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        result = result * 59 + Float.floatToIntBits((float)this.getHealth());
        result = result * 59 + this.getColor();
        result = result * 59 + this.getDivision();
        return result * 59 + this.getFlags();
    }
}

