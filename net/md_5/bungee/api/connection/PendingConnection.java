/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.connection;

import java.net.InetSocketAddress;
import java.util.UUID;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.Connection;

public interface PendingConnection
extends Connection {
    public String getName();

    public int getVersion();

    public InetSocketAddress getVirtualHost();

    public ListenerInfo getListener();

    @Deprecated
    public String getUUID();

    public UUID getUniqueId();

    public void setUniqueId(UUID var1);

    public boolean isOnlineMode();

    public void setOnlineMode(boolean var1);

    public boolean isLegacy();
}

