/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PlayerListItem;

public class PlayerListItem
extends DefinedPacket {
    private Action action;
    private Item[] items;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.action = Action.values()[DefinedPacket.readVarInt((ByteBuf)buf)];
        this.items = new Item[DefinedPacket.readVarInt((ByteBuf)buf)];
        int i = 0;
        while (i < this.items.length) {
            Item item = this.items[i] = new Item();
            item.setUuid((UUID)DefinedPacket.readUUID((ByteBuf)buf));
            switch (1.$SwitchMap$net$md_5$bungee$protocol$packet$PlayerListItem$Action[this.action.ordinal()]) {
                case 1: {
                    ((Item)item).username = (String)DefinedPacket.readString((ByteBuf)buf);
                    ((Item)item).properties = (String[][])new String[DefinedPacket.readVarInt((ByteBuf)buf)][];
                    for (int j = 0; j < ((Item)item).properties.length; ++j) {
                        String name = DefinedPacket.readString((ByteBuf)buf);
                        String value = DefinedPacket.readString((ByteBuf)buf);
                        if (buf.readBoolean()) {
                            Item.access$100((Item)item)[j] = new String[]{name, value, DefinedPacket.readString((ByteBuf)buf)};
                            continue;
                        }
                        Item.access$100((Item)item)[j] = new String[]{name, value};
                    }
                    ((Item)item).gamemode = (int)DefinedPacket.readVarInt((ByteBuf)buf);
                    ((Item)item).ping = (int)DefinedPacket.readVarInt((ByteBuf)buf);
                    if (!buf.readBoolean()) break;
                    ((Item)item).displayName = (String)DefinedPacket.readString((ByteBuf)buf);
                    break;
                }
                case 2: {
                    ((Item)item).gamemode = (int)DefinedPacket.readVarInt((ByteBuf)buf);
                    break;
                }
                case 3: {
                    ((Item)item).ping = (int)DefinedPacket.readVarInt((ByteBuf)buf);
                    break;
                }
                case 4: {
                    if (!buf.readBoolean()) break;
                    ((Item)item).displayName = (String)DefinedPacket.readString((ByteBuf)buf);
                }
            }
            ++i;
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        DefinedPacket.writeVarInt((int)this.action.ordinal(), (ByteBuf)buf);
        DefinedPacket.writeVarInt((int)this.items.length, (ByteBuf)buf);
        Item[] arritem = this.items;
        int n = arritem.length;
        int n2 = 0;
        while (n2 < n) {
            Item item = arritem[n2];
            DefinedPacket.writeUUID((UUID)((Item)item).uuid, (ByteBuf)buf);
            switch (1.$SwitchMap$net$md_5$bungee$protocol$packet$PlayerListItem$Action[this.action.ordinal()]) {
                case 1: {
                    DefinedPacket.writeString((String)((Item)item).username, (ByteBuf)buf);
                    DefinedPacket.writeVarInt((int)((Item)item).properties.length, (ByteBuf)buf);
                    for (String[] prop : ((Item)item).properties) {
                        DefinedPacket.writeString((String)prop[0], (ByteBuf)buf);
                        DefinedPacket.writeString((String)prop[1], (ByteBuf)buf);
                        if (prop.length >= 3) {
                            buf.writeBoolean((boolean)true);
                            DefinedPacket.writeString((String)prop[2], (ByteBuf)buf);
                            continue;
                        }
                        buf.writeBoolean((boolean)false);
                    }
                    DefinedPacket.writeVarInt((int)((Item)item).gamemode, (ByteBuf)buf);
                    DefinedPacket.writeVarInt((int)((Item)item).ping, (ByteBuf)buf);
                    buf.writeBoolean((boolean)(((Item)item).displayName != null));
                    if (((Item)item).displayName == null) break;
                    DefinedPacket.writeString((String)((Item)item).displayName, (ByteBuf)buf);
                    break;
                }
                case 2: {
                    DefinedPacket.writeVarInt((int)((Item)item).gamemode, (ByteBuf)buf);
                    break;
                }
                case 3: {
                    DefinedPacket.writeVarInt((int)((Item)item).ping, (ByteBuf)buf);
                    break;
                }
                case 4: {
                    buf.writeBoolean((boolean)(((Item)item).displayName != null));
                    if (((Item)item).displayName == null) break;
                    DefinedPacket.writeString((String)((Item)item).displayName, (ByteBuf)buf);
                }
            }
            ++n2;
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((PlayerListItem)this);
    }

    public Action getAction() {
        return this.action;
    }

    public Item[] getItems() {
        return this.items;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PlayerListItem(action=" + (Object)((Object)this.getAction()) + ", items=" + Arrays.deepToString((Object[])this.getItems()) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerListItem)) {
            return false;
        }
        PlayerListItem other = (PlayerListItem)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Action this$action = this.getAction();
        Action other$action = other.getAction();
        if (this$action == null ? other$action != null : !((Object)((Object)this$action)).equals((Object)((Object)other$action))) {
            return false;
        }
        if (Arrays.deepEquals((Object[])this.getItems(), (Object[])other.getItems())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerListItem;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Action $action = this.getAction();
        result = result * 59 + ($action == null ? 43 : ((Object)((Object)$action)).hashCode());
        return result * 59 + Arrays.deepHashCode((Object[])this.getItems());
    }
}

