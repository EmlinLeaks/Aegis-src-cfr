/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;
import java.util.Map;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class AbstractReconnectHandler
implements ReconnectHandler {
    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        ServerInfo server = AbstractReconnectHandler.getForcedHost((PendingConnection)player.getPendingConnection());
        if (server != null) return server;
        server = this.getStoredServer((ProxiedPlayer)player);
        if (server == null) {
            server = ProxyServer.getInstance().getServerInfo((String)player.getPendingConnection().getListener().getDefaultServer());
        }
        Preconditions.checkState((boolean)(server != null), (Object)"Default server not defined");
        return server;
    }

    public static ServerInfo getForcedHost(PendingConnection con) {
        if (con.getVirtualHost() == null) {
            return null;
        }
        String forced = con.getListener().getForcedHosts().get((Object)con.getVirtualHost().getHostString());
        if (forced != null) return ProxyServer.getInstance().getServerInfo((String)forced);
        if (!con.getListener().isForceDefault()) return ProxyServer.getInstance().getServerInfo((String)forced);
        forced = con.getListener().getDefaultServer();
        return ProxyServer.getInstance().getServerInfo((String)forced);
    }

    protected abstract ServerInfo getStoredServer(ProxiedPlayer var1);
}

