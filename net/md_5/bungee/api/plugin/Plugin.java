/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.plugin;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginLogger;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;

public class Plugin {
    private PluginDescription description;
    private ProxyServer proxy;
    private File file;
    private Logger logger;
    private ExecutorService service;

    public void onLoad() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public final File getDataFolder() {
        return new File((File)this.getProxy().getPluginsFolder(), (String)this.getDescription().getName());
    }

    public final InputStream getResourceAsStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream((String)name);
    }

    final void init(ProxyServer proxy, PluginDescription description) {
        this.proxy = proxy;
        this.description = description;
        this.file = description.getFile();
        this.logger = new PluginLogger((Plugin)this);
    }

    @Deprecated
    public ExecutorService getExecutorService() {
        if (this.service != null) return this.service;
        String name = this.getDescription() == null ? "unknown" : this.getDescription().getName();
        this.service = Executors.newCachedThreadPool((ThreadFactory)new ThreadFactoryBuilder().setNameFormat((String)(name + " Pool Thread #%1$d")).setThreadFactory((ThreadFactory)new GroupedThreadFactory((Plugin)this, (String)name)).build());
        return this.service;
    }

    public PluginDescription getDescription() {
        return this.description;
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }

    public File getFile() {
        return this.file;
    }

    public Logger getLogger() {
        return this.logger;
    }
}

