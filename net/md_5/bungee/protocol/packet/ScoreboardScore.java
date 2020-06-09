/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ScoreboardScore
extends DefinedPacket {
    private String itemName;
    private byte action;
    private String scoreName;
    private int value;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.itemName = ScoreboardScore.readString((ByteBuf)buf);
        this.action = buf.readByte();
        this.scoreName = ScoreboardScore.readString((ByteBuf)buf);
        if (this.action == 1) return;
        this.value = ScoreboardScore.readVarInt((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        ScoreboardScore.writeString((String)this.itemName, (ByteBuf)buf);
        buf.writeByte((int)this.action);
        ScoreboardScore.writeString((String)this.scoreName, (ByteBuf)buf);
        if (this.action == 1) return;
        ScoreboardScore.writeVarInt((int)this.value, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((ScoreboardScore)this);
    }

    public String getItemName() {
        return this.itemName;
    }

    public byte getAction() {
        return this.action;
    }

    public String getScoreName() {
        return this.scoreName;
    }

    public int getValue() {
        return this.value;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ScoreboardScore(itemName=" + this.getItemName() + ", action=" + this.getAction() + ", scoreName=" + this.getScoreName() + ", value=" + this.getValue() + ")";
    }

    public ScoreboardScore() {
    }

    public ScoreboardScore(String itemName, byte action, String scoreName, int value) {
        this.itemName = itemName;
        this.action = action;
        this.scoreName = scoreName;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreboardScore)) {
            return false;
        }
        ScoreboardScore other = (ScoreboardScore)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$itemName = this.getItemName();
        String other$itemName = other.getItemName();
        if (this$itemName == null ? other$itemName != null : !this$itemName.equals((Object)other$itemName)) {
            return false;
        }
        if (this.getAction() != other.getAction()) {
            return false;
        }
        String this$scoreName = this.getScoreName();
        String other$scoreName = other.getScoreName();
        if (this$scoreName == null ? other$scoreName != null : !this$scoreName.equals((Object)other$scoreName)) {
            return false;
        }
        if (this.getValue() == other.getValue()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ScoreboardScore;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $itemName = this.getItemName();
        result = result * 59 + ($itemName == null ? 43 : $itemName.hashCode());
        result = result * 59 + this.getAction();
        String $scoreName = this.getScoreName();
        result = result * 59 + ($scoreName == null ? 43 : $scoreName.hashCode());
        return result * 59 + this.getValue();
    }
}

