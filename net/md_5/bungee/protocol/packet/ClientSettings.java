/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ClientSettings
extends DefinedPacket {
    private String locale;
    private byte viewDistance;
    private int chatFlags;
    private boolean chatColours;
    private byte difficulty;
    private byte skinParts;
    private int mainHand;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.locale = ClientSettings.readString((ByteBuf)buf);
        this.viewDistance = buf.readByte();
        this.chatFlags = protocolVersion >= 107 ? DefinedPacket.readVarInt((ByteBuf)buf) : (int)buf.readUnsignedByte();
        this.chatColours = buf.readBoolean();
        this.skinParts = buf.readByte();
        if (protocolVersion < 107) return;
        this.mainHand = DefinedPacket.readVarInt((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        ClientSettings.writeString((String)this.locale, (ByteBuf)buf);
        buf.writeByte((int)this.viewDistance);
        if (protocolVersion >= 107) {
            DefinedPacket.writeVarInt((int)this.chatFlags, (ByteBuf)buf);
        } else {
            buf.writeByte((int)this.chatFlags);
        }
        buf.writeBoolean((boolean)this.chatColours);
        buf.writeByte((int)this.skinParts);
        if (protocolVersion < 107) return;
        DefinedPacket.writeVarInt((int)this.mainHand, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((ClientSettings)this);
    }

    public String getLocale() {
        return this.locale;
    }

    public byte getViewDistance() {
        return this.viewDistance;
    }

    public int getChatFlags() {
        return this.chatFlags;
    }

    public boolean isChatColours() {
        return this.chatColours;
    }

    public byte getDifficulty() {
        return this.difficulty;
    }

    public byte getSkinParts() {
        return this.skinParts;
    }

    public int getMainHand() {
        return this.mainHand;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setViewDistance(byte viewDistance) {
        this.viewDistance = viewDistance;
    }

    public void setChatFlags(int chatFlags) {
        this.chatFlags = chatFlags;
    }

    public void setChatColours(boolean chatColours) {
        this.chatColours = chatColours;
    }

    public void setDifficulty(byte difficulty) {
        this.difficulty = difficulty;
    }

    public void setSkinParts(byte skinParts) {
        this.skinParts = skinParts;
    }

    public void setMainHand(int mainHand) {
        this.mainHand = mainHand;
    }

    @Override
    public String toString() {
        return "ClientSettings(locale=" + this.getLocale() + ", viewDistance=" + this.getViewDistance() + ", chatFlags=" + this.getChatFlags() + ", chatColours=" + this.isChatColours() + ", difficulty=" + this.getDifficulty() + ", skinParts=" + this.getSkinParts() + ", mainHand=" + this.getMainHand() + ")";
    }

    public ClientSettings() {
    }

    public ClientSettings(String locale, byte viewDistance, int chatFlags, boolean chatColours, byte difficulty, byte skinParts, int mainHand) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatFlags = chatFlags;
        this.chatColours = chatColours;
        this.difficulty = difficulty;
        this.skinParts = skinParts;
        this.mainHand = mainHand;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientSettings)) {
            return false;
        }
        ClientSettings other = (ClientSettings)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$locale = this.getLocale();
        String other$locale = other.getLocale();
        if (this$locale == null ? other$locale != null : !this$locale.equals((Object)other$locale)) {
            return false;
        }
        if (this.getViewDistance() != other.getViewDistance()) {
            return false;
        }
        if (this.getChatFlags() != other.getChatFlags()) {
            return false;
        }
        if (this.isChatColours() != other.isChatColours()) {
            return false;
        }
        if (this.getDifficulty() != other.getDifficulty()) {
            return false;
        }
        if (this.getSkinParts() != other.getSkinParts()) {
            return false;
        }
        if (this.getMainHand() == other.getMainHand()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClientSettings;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $locale = this.getLocale();
        result = result * 59 + ($locale == null ? 43 : $locale.hashCode());
        result = result * 59 + this.getViewDistance();
        result = result * 59 + this.getChatFlags();
        result = result * 59 + (this.isChatColours() ? 79 : 97);
        result = result * 59 + this.getDifficulty();
        result = result * 59 + this.getSkinParts();
        return result * 59 + this.getMainHand();
    }
}

