/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.config;

import java.util.Collection;
import java.util.Map;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

public interface ConfigurationAdapter {
    public void load();

    public int getInt(String var1, int var2);

    public String getString(String var1, String var2);

    public boolean getBoolean(String var1, boolean var2);

    public Collection<?> getList(String var1, Collection<?> var2);

    public Map<String, ServerInfo> getServers();

    public Collection<ListenerInfo> getListeners();

    public Collection<String> getGroups(String var1);

    public Collection<String> getPermissions(String var1);
}

