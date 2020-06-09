/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.config;

import java.net.InetSocketAddress;
import java.util.Collection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ServerInfo {
    public String getName();

    public InetSocketAddress getAddress();

    public Collection<ProxiedPlayer> getPlayers();

    public String getMotd();

    public boolean isRestricted();

    public String getPermission();

    public boolean canAccess(CommandSender var1);

    public void sendData(String var1, byte[] var2);

    public boolean sendData(String var1, byte[] var2, boolean var3);

    public void ping(Callback<ServerPing> var1);
}

