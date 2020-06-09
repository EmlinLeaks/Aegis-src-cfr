/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.tab;

import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.packet.PlayerListItem;

public abstract class TabList {
    protected final ProxiedPlayer player;

    public abstract void onUpdate(PlayerListItem var1);

    public abstract void onPingChange(int var1);

    public abstract void onServerChange();

    public abstract void onConnect();

    public abstract void onDisconnect();

    public static PlayerListItem rewrite(PlayerListItem playerListItem) {
        PlayerListItem.Item[] arritem = playerListItem.getItems();
        int n = arritem.length;
        int n2 = 0;
        while (n2 < n) {
            UserConnection player;
            PlayerListItem.Item item = arritem[n2];
            if (item.getUuid() != null && (player = BungeeCord.getInstance().getPlayerByOfflineUUID((UUID)item.getUuid())) != null) {
                item.setUuid((UUID)player.getUniqueId());
                LoginResult loginResult = player.getPendingConnection().getLoginProfile();
                if (loginResult != null && loginResult.getProperties() != null) {
                    String[][] props = new String[loginResult.getProperties().length][];
                    for (int i = 0; i < props.length; ++i) {
                        props[i] = new String[]{loginResult.getProperties()[i].getName(), loginResult.getProperties()[i].getValue(), loginResult.getProperties()[i].getSignature()};
                    }
                    item.setProperties((String[][])props);
                } else {
                    item.setProperties((String[][])new String[0][0]);
                }
                if (playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER || playerListItem.getAction() == PlayerListItem.Action.UPDATE_GAMEMODE) {
                    player.setGamemode((int)item.getGamemode());
                }
                if (playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER || playerListItem.getAction() == PlayerListItem.Action.UPDATE_LATENCY) {
                    player.setPing((int)item.getPing());
                }
            }
            ++n2;
        }
        return playerListItem;
    }

    public TabList(ProxiedPlayer player) {
        this.player = player;
    }
}

