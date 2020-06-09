/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.plugin;

import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class PluginLogger
extends Logger {
    private final String pluginName;

    protected PluginLogger(Plugin plugin) {
        super((String)plugin.getClass().getCanonicalName(), null);
        this.pluginName = "[" + plugin.getDescription().getName() + "] ";
        this.setParent((Logger)plugin.getProxy().getLogger());
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage((String)(this.pluginName + logRecord.getMessage()));
        super.log((LogRecord)logRecord);
    }
}

