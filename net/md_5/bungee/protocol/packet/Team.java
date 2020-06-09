/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Team
extends DefinedPacket {
    private String name;
    private byte mode;
    private String displayName;
    private String prefix;
    private String suffix;
    private String nameTagVisibility;
    private String collisionRule;
    private int color;
    private byte friendlyFire;
    private String[] players;

    public Team(String name) {
        this.name = name;
        this.mode = 1;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.name = Team.readString((ByteBuf)buf);
        this.mode = buf.readByte();
        if (this.mode == 0 || this.mode == 2) {
            this.displayName = Team.readString((ByteBuf)buf);
            if (protocolVersion < 393) {
                this.prefix = Team.readString((ByteBuf)buf);
                this.suffix = Team.readString((ByteBuf)buf);
            }
            this.friendlyFire = buf.readByte();
            this.nameTagVisibility = Team.readString((ByteBuf)buf);
            if (protocolVersion >= 107) {
                this.collisionRule = Team.readString((ByteBuf)buf);
            }
            int n = this.color = protocolVersion >= 393 ? Team.readVarInt((ByteBuf)buf) : (int)buf.readByte();
            if (protocolVersion >= 393) {
                this.prefix = Team.readString((ByteBuf)buf);
                this.suffix = Team.readString((ByteBuf)buf);
            }
        }
        if (this.mode != 0 && this.mode != 3) {
            if (this.mode != 4) return;
        }
        int len = Team.readVarInt((ByteBuf)buf);
        this.players = new String[len];
        int i = 0;
        while (i < len) {
            this.players[i] = Team.readString((ByteBuf)buf);
            ++i;
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        Team.writeString((String)this.name, (ByteBuf)buf);
        buf.writeByte((int)this.mode);
        if (this.mode == 0 || this.mode == 2) {
            Team.writeString((String)this.displayName, (ByteBuf)buf);
            if (protocolVersion < 393) {
                Team.writeString((String)this.prefix, (ByteBuf)buf);
                Team.writeString((String)this.suffix, (ByteBuf)buf);
            }
            buf.writeByte((int)this.friendlyFire);
            Team.writeString((String)this.nameTagVisibility, (ByteBuf)buf);
            if (protocolVersion >= 107) {
                Team.writeString((String)this.collisionRule, (ByteBuf)buf);
            }
            if (protocolVersion >= 393) {
                Team.writeVarInt((int)this.color, (ByteBuf)buf);
                Team.writeString((String)this.prefix, (ByteBuf)buf);
                Team.writeString((String)this.suffix, (ByteBuf)buf);
            } else {
                buf.writeByte((int)this.color);
            }
        }
        if (this.mode != 0 && this.mode != 3) {
            if (this.mode != 4) return;
        }
        Team.writeVarInt((int)this.players.length, (ByteBuf)buf);
        String[] arrstring = this.players;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String player = arrstring[n2];
            Team.writeString((String)player, (ByteBuf)buf);
            ++n2;
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Team)this);
    }

    public String getName() {
        return this.name;
    }

    public byte getMode() {
        return this.mode;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public String getCollisionRule() {
        return this.collisionRule;
    }

    public int getColor() {
        return this.color;
    }

    public byte getFriendlyFire() {
        return this.friendlyFire;
    }

    public String[] getPlayers() {
        return this.players;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setNameTagVisibility(String nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
    }

    public void setCollisionRule(String collisionRule) {
        this.collisionRule = collisionRule;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setFriendlyFire(byte friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "Team(name=" + this.getName() + ", mode=" + this.getMode() + ", displayName=" + this.getDisplayName() + ", prefix=" + this.getPrefix() + ", suffix=" + this.getSuffix() + ", nameTagVisibility=" + this.getNameTagVisibility() + ", collisionRule=" + this.getCollisionRule() + ", color=" + this.getColor() + ", friendlyFire=" + this.getFriendlyFire() + ", players=" + Arrays.deepToString((Object[])this.getPlayers()) + ")";
    }

    public Team() {
    }

    public Team(String name, byte mode, String displayName, String prefix, String suffix, String nameTagVisibility, String collisionRule, int color, byte friendlyFire, String[] players) {
        this.name = name;
        this.mode = mode;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nameTagVisibility = nameTagVisibility;
        this.collisionRule = collisionRule;
        this.color = color;
        this.friendlyFire = friendlyFire;
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        Team other = (Team)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        if (this.getMode() != other.getMode()) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals((Object)other$displayName)) {
            return false;
        }
        String this$prefix = this.getPrefix();
        String other$prefix = other.getPrefix();
        if (this$prefix == null ? other$prefix != null : !this$prefix.equals((Object)other$prefix)) {
            return false;
        }
        String this$suffix = this.getSuffix();
        String other$suffix = other.getSuffix();
        if (this$suffix == null ? other$suffix != null : !this$suffix.equals((Object)other$suffix)) {
            return false;
        }
        String this$nameTagVisibility = this.getNameTagVisibility();
        String other$nameTagVisibility = other.getNameTagVisibility();
        if (this$nameTagVisibility == null ? other$nameTagVisibility != null : !this$nameTagVisibility.equals((Object)other$nameTagVisibility)) {
            return false;
        }
        String this$collisionRule = this.getCollisionRule();
        String other$collisionRule = other.getCollisionRule();
        if (this$collisionRule == null ? other$collisionRule != null : !this$collisionRule.equals((Object)other$collisionRule)) {
            return false;
        }
        if (this.getColor() != other.getColor()) {
            return false;
        }
        if (this.getFriendlyFire() != other.getFriendlyFire()) {
            return false;
        }
        if (Arrays.deepEquals((Object[])this.getPlayers(), (Object[])other.getPlayers())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Team;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        result = result * 59 + this.getMode();
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        String $prefix = this.getPrefix();
        result = result * 59 + ($prefix == null ? 43 : $prefix.hashCode());
        String $suffix = this.getSuffix();
        result = result * 59 + ($suffix == null ? 43 : $suffix.hashCode());
        String $nameTagVisibility = this.getNameTagVisibility();
        result = result * 59 + ($nameTagVisibility == null ? 43 : $nameTagVisibility.hashCode());
        String $collisionRule = this.getCollisionRule();
        result = result * 59 + ($collisionRule == null ? 43 : $collisionRule.hashCode());
        result = result * 59 + this.getColor();
        result = result * 59 + this.getFriendlyFire();
        return result * 59 + Arrays.deepHashCode((Object[])this.getPlayers());
    }
}

