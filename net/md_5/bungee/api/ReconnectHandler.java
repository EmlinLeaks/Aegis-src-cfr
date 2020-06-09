/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ReconnectHandler {
    public ServerInfo getServer(ProxiedPlayer var1);

    public void setServer(ProxiedPlayer var1);

    public void save();

    public void close();
}

