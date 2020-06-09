/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class EntityStatus
extends DefinedPacket {
    public static final byte DEBUG_INFO_REDUCED = 22;
    public static final byte DEBUG_INFO_NORMAL = 23;
    private int entityId;
    private byte status;

    @Override
    public void read(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.status = buf.readByte();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt((int)this.entityId);
        buf.writeByte((int)this.status);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((EntityStatus)this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getStatus() {
        return this.status;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EntityStatus(entityId=" + this.getEntityId() + ", status=" + this.getStatus() + ")";
    }

    public EntityStatus() {
    }

    public EntityStatus(int entityId, byte status) {
        this.entityId = entityId;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EntityStatus)) {
            return false;
        }
        EntityStatus other = (EntityStatus)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getEntityId() != other.getEntityId()) {
            return false;
        }
        if (this.getStatus() == other.getStatus()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EntityStatus;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getEntityId();
        return result * 59 + this.getStatus();
    }
}

