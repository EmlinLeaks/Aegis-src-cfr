/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.tab;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.tab.TabList;

public class ServerUnique
extends TabList {
    private final Collection<UUID> uuids = new HashSet<UUID>();

    public ServerUnique(ProxiedPlayer player) {
        super((ProxiedPlayer)player);
    }

    @Override
    public void onUpdate(PlayerListItem playerListItem) {
        PlayerListItem.Item[] arritem = playerListItem.getItems();
        int n = arritem.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                this.player.unsafe().sendPacket((DefinedPacket)playerListItem);
                return;
            }
            PlayerListItem.Item item = arritem[n2];
            if (playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER) {
                this.uuids.add((UUID)item.getUuid());
            } else if (playerListItem.getAction() == PlayerListItem.Action.REMOVE_PLAYER) {
                this.uuids.remove((Object)item.getUuid());
            }
            ++n2;
        } while (true);
    }

    @Override
    public void onPingChange(int ping) {
    }

    @Override
    public void onServerChange() {
        PlayerListItem packet = new PlayerListItem();
        packet.setAction((PlayerListItem.Action)PlayerListItem.Action.REMOVE_PLAYER);
        PlayerListItem.Item[] items = new PlayerListItem.Item[this.uuids.size()];
        int i = 0;
        Iterator<UUID> iterator = this.uuids.iterator();
        do {
            if (!iterator.hasNext()) {
                packet.setItems((PlayerListItem.Item[])items);
                this.player.unsafe().sendPacket((DefinedPacket)packet);
                this.uuids.clear();
                return;
            }
            UUID uuid = iterator.next();
            int n = i++;
            PlayerListItem.Item item = new PlayerListItem.Item();
            items[n] = item;
            PlayerListItem.Item item2 = item;
            item2.setUuid((UUID)uuid);
        } while (true);
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onDisconnect() {
    }
}

