/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api;

import java.util.Collection;
import java.util.Map;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

@Deprecated
public interface ProxyConfig {
    public int getTimeout();

    public String getUuid();

    public Collection<ListenerInfo> getListeners();

    public Map<String, ServerInfo> getServers();

    public boolean isOnlineMode();

    public boolean isLogCommands();

    public int getPlayerLimit();

    public Collection<String> getDisabledCommands();

    @Deprecated
    public int getThrottle();

    @Deprecated
    public boolean isIpForward();

    @Deprecated
    public String getFavicon();

    public Favicon getFaviconObject();
}

