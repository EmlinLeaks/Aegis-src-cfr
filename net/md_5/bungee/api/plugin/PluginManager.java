/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginClassloader;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventBus;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class PluginManager {
    private final ProxyServer proxy;
    private final Yaml yaml;
    private final EventBus eventBus;
    private final Map<String, Plugin> plugins = new LinkedHashMap<String, Plugin>();
    private final Map<String, Command> commandMap = new HashMap<String, Command>();
    private Map<String, PluginDescription> toLoad = new HashMap<String, PluginDescription>();
    private final Multimap<Plugin, Command> commandsByPlugin = ArrayListMultimap.create();
    private final Multimap<Plugin, Listener> listenersByPlugin = ArrayListMultimap.create();

    public PluginManager(ProxyServer proxy) {
        this.proxy = proxy;
        Constructor yamlConstructor = new Constructor();
        PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
        propertyUtils.setSkipMissingProperties((boolean)true);
        yamlConstructor.setPropertyUtils((PropertyUtils)propertyUtils);
        this.yaml = new Yaml((BaseConstructor)yamlConstructor);
        this.eventBus = new EventBus((Logger)proxy.getLogger());
    }

    public void registerCommand(Plugin plugin, Command command) {
        this.commandMap.put((String)command.getName().toLowerCase((Locale)Locale.ROOT), (Command)command);
        String[] arrstring = command.getAliases();
        int n = arrstring.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                this.commandsByPlugin.put((Plugin)plugin, (Command)command);
                return;
            }
            String alias = arrstring[n2];
            this.commandMap.put((String)alias.toLowerCase((Locale)Locale.ROOT), (Command)command);
            ++n2;
        } while (true);
    }

    public void unregisterCommand(Command command) {
        while (this.commandMap.values().remove((Object)command)) {
        }
        this.commandsByPlugin.values().remove((Object)command);
    }

    public void unregisterCommands(Plugin plugin) {
        Iterator<Command> it = this.commandsByPlugin.get((Plugin)plugin).iterator();
        while (it.hasNext()) {
            Command command = it.next();
            while (this.commandMap.values().remove((Object)command)) {
            }
            it.remove();
        }
    }

    private Command getCommandIfEnabled(String commandName, CommandSender sender) {
        String commandLower = commandName.toLowerCase((Locale)Locale.ROOT);
        if (!(sender instanceof ProxiedPlayer)) return this.commandMap.get((Object)commandLower);
        if (!this.proxy.getDisabledCommands().contains((Object)commandLower)) return this.commandMap.get((Object)commandLower);
        return null;
    }

    public boolean isExecutableCommand(String commandName, CommandSender sender) {
        if (this.getCommandIfEnabled((String)commandName, (CommandSender)sender) == null) return false;
        return true;
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) {
        return this.dispatchCommand((CommandSender)sender, (String)commandLine, null);
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine, List<String> tabResults) {
        String[] split = commandLine.split((String)" ", (int)-1);
        if (split.length == 0) return false;
        if (split[0].isEmpty()) {
            return false;
        }
        Command command = this.getCommandIfEnabled((String)split[0], (CommandSender)sender);
        if (command == null) {
            return false;
        }
        if (!command.hasPermission((CommandSender)sender)) {
            if (tabResults != null) return true;
            sender.sendMessage((String)this.proxy.getTranslation((String)"no_permission", (Object[])new Object[0]));
            return true;
        }
        String[] args = Arrays.copyOfRange(split, (int)1, (int)split.length);
        try {
            if (tabResults == null) {
                if (this.proxy.getConfig().isLogCommands()) {
                    this.proxy.getLogger().log((Level)Level.INFO, (String)"{0} executed command: /{1}", (Object[])new Object[]{sender.getName(), commandLine});
                }
                command.execute((CommandSender)sender, (String[])args);
                return true;
            }
            if (!commandLine.contains((CharSequence)" ")) return true;
            if (!(command instanceof TabExecutor)) return true;
            Iterator<String> iterator = ((TabExecutor)((Object)command)).onTabComplete((CommandSender)sender, (String[])args).iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                tabResults.add((String)s);
            }
            return true;
        }
        catch (Exception ex) {
            sender.sendMessage((String)((Object)((Object)ChatColor.RED) + "An internal error occurred whilst executing this command, please check the console log for details."));
            ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Error in dispatching command", (Throwable)ex);
        }
        return true;
    }

    public Collection<Plugin> getPlugins() {
        return this.plugins.values();
    }

    public Plugin getPlugin(String name) {
        return this.plugins.get((Object)name);
    }

    public void loadPlugins() {
        HashMap<PluginDescription, Boolean> pluginStatuses = new HashMap<PluginDescription, Boolean>();
        Iterator<Map.Entry<String, PluginDescription>> iterator = this.toLoad.entrySet().iterator();
        do {
            if (!iterator.hasNext()) {
                this.toLoad.clear();
                this.toLoad = null;
                return;
            }
            Map.Entry<String, PluginDescription> entry = iterator.next();
            PluginDescription plugin = entry.getValue();
            if (this.enablePlugin(pluginStatuses, new Stack<PluginDescription>(), (PluginDescription)plugin)) continue;
            ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Failed to enable {0}", (Object)entry.getKey());
        } while (true);
    }

    public void enablePlugins() {
        Iterator<Plugin> iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            try {
                plugin.onEnable();
                ProxyServer.getInstance().getLogger().log((Level)Level.INFO, (String)"Enabled plugin {0} version {1} by {2}", (Object[])new Object[]{plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthor()});
            }
            catch (Throwable t) {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)("Exception encountered when loading plugin: " + plugin.getDescription().getName()), (Throwable)t);
            }
        }
    }

    private boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, Stack<PluginDescription> dependStack, PluginDescription plugin) {
        if (pluginStatuses.containsKey((Object)plugin)) {
            return pluginStatuses.get((Object)plugin).booleanValue();
        }
        HashSet<String> dependencies = new HashSet<String>();
        dependencies.addAll(plugin.getDepends());
        dependencies.addAll(plugin.getSoftDepends());
        boolean status = true;
        for (String dependName : dependencies) {
            Boolean dependStatus;
            PluginDescription depend = this.toLoad.get((Object)dependName);
            Boolean bl = dependStatus = depend != null ? pluginStatuses.get((Object)depend) : Boolean.FALSE;
            if (dependStatus == null) {
                if (dependStack.contains((Object)depend)) {
                    StringBuilder dependencyGraph = new StringBuilder();
                    for (PluginDescription element : dependStack) {
                        dependencyGraph.append((String)element.getName()).append((String)" -> ");
                    }
                    dependencyGraph.append((String)plugin.getName()).append((String)" -> ").append((String)dependName);
                    ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Circular dependency detected: {0}", (Object)dependencyGraph);
                    status = false;
                } else {
                    dependStack.push((PluginDescription)plugin);
                    dependStatus = Boolean.valueOf((boolean)this.enablePlugin(pluginStatuses, dependStack, (PluginDescription)depend));
                    dependStack.pop();
                }
            }
            if (dependStatus == Boolean.FALSE && plugin.getDepends().contains((Object)dependName)) {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"{0} (required by {1}) is unavailable", (Object[])new Object[]{String.valueOf((Object)dependName), plugin.getName()});
                status = false;
            }
            if (status) continue;
        }
        if (status) {
            try {
                PluginClassloader loader = new PluginClassloader((URL[])new URL[]{plugin.getFile().toURI().toURL()});
                Class<?> main = loader.loadClass((String)plugin.getMain());
                Plugin clazz = (Plugin)main.getDeclaredConstructor(new Class[0]).newInstance((Object[])new Object[0]);
                clazz.init((ProxyServer)this.proxy, (PluginDescription)plugin);
                this.plugins.put((String)plugin.getName(), (Plugin)clazz);
                clazz.onLoad();
                ProxyServer.getInstance().getLogger().log((Level)Level.INFO, (String)"Loaded plugin {0} version {1} by {2}", (Object[])new Object[]{plugin.getName(), plugin.getVersion(), plugin.getAuthor()});
            }
            catch (Throwable t) {
                this.proxy.getLogger().log((Level)Level.WARNING, (String)("Error enabling plugin " + plugin.getName()), (Throwable)t);
            }
        }
        pluginStatuses.put((PluginDescription)plugin, (Boolean)Boolean.valueOf((boolean)status));
        return status;
    }

    public void detectPlugins(File folder) {
        Preconditions.checkNotNull(folder, (Object)"folder");
        Preconditions.checkArgument((boolean)folder.isDirectory(), (Object)"Must load from a directory");
        File[] arrfile = folder.listFiles();
        int n = arrfile.length;
        int n2 = 0;
        while (n2 < n) {
            File file = arrfile[n2];
            if (file.isFile() && file.getName().endsWith((String)".jar")) {
                try {
                    JarFile jar = new JarFile((File)file);
                    Throwable throwable = null;
                    try {
                        JarEntry pdf = jar.getJarEntry((String)"bungee.yml");
                        if (pdf == null) {
                            pdf = jar.getJarEntry((String)"plugin.yml");
                        }
                        Preconditions.checkNotNull(pdf, (Object)"Plugin must have a plugin.yml or bungee.yml");
                        InputStream in = jar.getInputStream((ZipEntry)pdf);
                        Throwable throwable2 = null;
                        try {
                            PluginDescription desc = this.yaml.loadAs((InputStream)in, PluginDescription.class);
                            Preconditions.checkNotNull(desc.getName(), (String)"Plugin from %s has no name", (Object)file);
                            Preconditions.checkNotNull(desc.getMain(), (String)"Plugin from %s has no main", (Object)file);
                            desc.setFile((File)file);
                            this.toLoad.put((String)desc.getName(), (PluginDescription)desc);
                        }
                        catch (Throwable desc) {
                            throwable2 = desc;
                            throw desc;
                        }
                        finally {
                            if (in != null) {
                                PluginManager.$closeResource((Throwable)throwable2, (AutoCloseable)in);
                            }
                        }
                    }
                    catch (Throwable pdf) {
                        throwable = pdf;
                        throw pdf;
                    }
                    finally {
                        PluginManager.$closeResource((Throwable)throwable, (AutoCloseable)jar);
                    }
                }
                catch (Exception ex) {
                    ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)("Could not load plugin from file " + file), (Throwable)ex);
                }
            }
            ++n2;
        }
    }

    public <T extends Event> T callEvent(T event) {
        Preconditions.checkNotNull(event, (Object)"event");
        long start = System.nanoTime();
        this.eventBus.post(event);
        ((Event)event).postCall();
        long elapsed = System.nanoTime() - start;
        if (elapsed <= 250000000L) return (T)event;
        ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Event {0} took {1}ns to process!", (Object[])new Object[]{event, Long.valueOf((long)elapsed)});
        return (T)event;
    }

    public void registerListener(Plugin plugin, Listener listener) {
        Method[] arrmethod = listener.getClass().getDeclaredMethods();
        int n = arrmethod.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                this.eventBus.register((Object)listener);
                this.listenersByPlugin.put((Plugin)plugin, (Listener)listener);
                return;
            }
            Method method = arrmethod[n2];
            Preconditions.checkArgument((boolean)(!method.isAnnotationPresent(Subscribe.class)), (String)"Listener %s has registered using deprecated subscribe annotation! Please update to @EventHandler.", (Object)listener);
            ++n2;
        } while (true);
    }

    public void unregisterListener(Listener listener) {
        this.eventBus.unregister((Object)listener);
        this.listenersByPlugin.values().remove((Object)listener);
    }

    public void unregisterListeners(Plugin plugin) {
        Iterator<Listener> it = this.listenersByPlugin.get((Plugin)plugin).iterator();
        while (it.hasNext()) {
            this.eventBus.unregister((Object)it.next());
            it.remove();
        }
    }

    public Collection<Map.Entry<String, Command>> getCommands() {
        return Collections.unmodifiableCollection(this.commandMap.entrySet());
    }

    public PluginManager(ProxyServer proxy, Yaml yaml, EventBus eventBus) {
        this.proxy = proxy;
        this.yaml = yaml;
        this.eventBus = eventBus;
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
            return;
        }
        catch (Throwable throwable) {
            x0.addSuppressed((Throwable)throwable);
            return;
        }
    }
}

