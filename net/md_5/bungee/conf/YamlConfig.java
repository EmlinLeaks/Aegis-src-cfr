/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.md_5.bungee.conf;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.ArrayList;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

public class YamlConfig
implements ConfigurationAdapter {
    private final Yaml yaml;
    private Map<String, Object> config;
    private final File file = new File((String)"config.yml");

    public YamlConfig() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle((DumperOptions.FlowStyle)DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml((DumperOptions)options);
    }

    @Override
    public void load() {
        Map groups;
        try {
            this.file.createNewFile();
            FileInputStream is = new FileInputStream((File)this.file);
            Throwable throwable = null;
            try {
                try {
                    this.config = (Map)this.yaml.load((InputStream)is);
                }
                catch (YAMLException ex) {
                    throw new RuntimeException((String)"Invalid configuration encountered - this is a configuration error and NOT a bug! Please attempt to fix the error or see https://www.spigotmc.org/ for help.", (Throwable)ex);
                }
            }
            catch (Throwable ex) {
                throwable = ex;
                throw ex;
            }
            finally {
                YamlConfig.$closeResource((Throwable)throwable, (AutoCloseable)is);
            }
            this.config = this.config == null ? new CaseInsensitiveMap<Object>() : new CaseInsensitiveMap<Object>(this.config);
        }
        catch (IOException ex) {
            throw new RuntimeException((String)"Could not load configuration!", (Throwable)ex);
        }
        Map permissions = (Map)this.get((String)"permissions", null);
        if (permissions == null) {
            this.set((String)"permissions.default", Arrays.asList("bungeecord.command.server", "bungeecord.command.list"));
            this.set((String)"permissions.admin", Arrays.asList("bungeecord.command.alert", "bungeecord.command.end", "bungeecord.command.ip", "bungeecord.command.reload"));
        }
        if ((groups = (Map)this.get((String)"groups", null)) != null) return;
        this.set((String)"groups.md_5", Collections.singletonList("admin"));
    }

    private <T> T get(String path, T def) {
        return (T)this.get((String)path, def, this.config);
    }

    private <T> T get(String path, T def, Map submap) {
        int index = path.indexOf((int)46);
        if (index == -1) {
            Object val = submap.get((Object)path);
            if (val != null) return (T)val;
            if (def == null) return (T)val;
            val = def;
            submap.put(path, def);
            this.save();
            return (T)val;
        }
        String first = path.substring((int)0, (int)index);
        String second = path.substring((int)(index + 1), (int)path.length());
        LinkedHashMap<K, V> sub = (LinkedHashMap<K, V>)submap.get((Object)first);
        if (sub != null) return (T)this.get((String)second, def, sub);
        sub = new LinkedHashMap<K, V>();
        submap.put(first, sub);
        return (T)this.get((String)second, def, sub);
    }

    private void set(String path, Object val) {
        this.set((String)path, (Object)val, this.config);
    }

    private void set(String path, Object val, Map submap) {
        int index = path.indexOf((int)46);
        if (index == -1) {
            if (val == null) {
                submap.remove((Object)path);
            } else {
                submap.put(path, val);
            }
            this.save();
            return;
        }
        String first = path.substring((int)0, (int)index);
        String second = path.substring((int)(index + 1), (int)path.length());
        LinkedHashMap<K, V> sub = (LinkedHashMap<K, V>)submap.get((Object)first);
        if (sub == null) {
            sub = new LinkedHashMap<K, V>();
            submap.put(first, sub);
        }
        this.set((String)second, (Object)val, sub);
    }

    private void save() {
        try {
            FileWriter wr = new FileWriter((File)this.file);
            Throwable throwable = null;
            try {
                this.yaml.dump(this.config, (Writer)wr);
                return;
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                YamlConfig.$closeResource((Throwable)throwable, (AutoCloseable)wr);
            }
        }
        catch (IOException ex) {
            ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Could not save config", (Throwable)ex);
        }
    }

    @Override
    public int getInt(String path, int def) {
        return this.get((String)path, Integer.valueOf((int)def)).intValue();
    }

    @Override
    public String getString(String path, String def) {
        return this.get((String)path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.get((String)path, Boolean.valueOf((boolean)def)).booleanValue();
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        Map<String, HashMap<K, V>> base = this.get((String)"servers", Collections.singletonMap("lobby", new HashMap<K, V>()));
        HashMap<String, ServerInfo> ret = new HashMap<String, ServerInfo>();
        Iterator<Map.Entry<String, HashMap<K, V>>> iterator = base.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HashMap<K, V>> entry = iterator.next();
            Map val = (Map)entry.getValue();
            String name = entry.getKey();
            String addr = this.get((String)"address", "localhost:25565", (Map)val);
            String motd = ChatColor.translateAlternateColorCodes((char)'&', (String)this.get((String)"motd", "&1Just another BungeeCord - Forced Host", (Map)val));
            boolean restricted = this.get((String)"restricted", Boolean.valueOf((boolean)false), (Map)val).booleanValue();
            InetSocketAddress address = Util.getAddr((String)addr);
            ServerInfo info = ProxyServer.getInstance().constructServerInfo((String)name, (InetSocketAddress)address, (String)motd, (boolean)restricted);
            ret.put((String)name, (ServerInfo)info);
        }
        return ret;
    }

    @SuppressFBWarnings(value={"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
    @Override
    public Collection<ListenerInfo> getListeners() {
        Collection base = (Collection)this.get((String)"listeners", Arrays.asList(new HashMap<K, V>()));
        HashMap<String, String> forcedDef = new HashMap<String, String>();
        forcedDef.put("pvp.md-5.net", "pvp");
        HashSet<ListenerInfo> ret = new HashSet<ListenerInfo>();
        Iterator<E> iterator = base.iterator();
        while (iterator.hasNext()) {
            Map val = (Map)iterator.next();
            String motd = this.get((String)"motd", "&1Another Bungee server", (Map)val);
            motd = ChatColor.translateAlternateColorCodes((char)'&', (String)motd);
            int maxPlayers = this.get((String)"max_players", Integer.valueOf((int)1), (Map)val).intValue();
            boolean forceDefault = this.get((String)"force_default_server", Boolean.valueOf((boolean)false), (Map)val).booleanValue();
            String host = this.get((String)"host", "0.0.0.0:25577", (Map)val);
            int tabListSize = this.get((String)"tab_size", Integer.valueOf((int)60), (Map)val).intValue();
            InetSocketAddress address = Util.getAddr((String)host);
            CaseInsensitiveMap<String> forced = new CaseInsensitiveMap<String>((Map)this.get((String)"forced_hosts", forcedDef, (Map)val));
            String tabListName = this.get((String)"tab_list", "GLOBAL_PING", (Map)val);
            DefaultTabList value = DefaultTabList.valueOf((String)tabListName.toUpperCase((Locale)Locale.ROOT));
            if (value == null) {
                value = DefaultTabList.GLOBAL_PING;
            }
            boolean setLocalAddress = this.get((String)"bind_local_address", Boolean.valueOf((boolean)true), (Map)val).booleanValue();
            boolean pingPassthrough = this.get((String)"ping_passthrough", Boolean.valueOf((boolean)false), (Map)val).booleanValue();
            boolean query = this.get((String)"query_enabled", Boolean.valueOf((boolean)false), (Map)val).booleanValue();
            int queryPort = this.get((String)"query_port", Integer.valueOf((int)25577), (Map)val).intValue();
            boolean proxyProtocol = this.get((String)"proxy_protocol", Boolean.valueOf((boolean)false), (Map)val).booleanValue();
            ArrayList<String> serverPriority = new ArrayList<String>((Collection)this.get((String)"priorities", Collections.EMPTY_LIST, (Map)val));
            String defaultServer = (String)this.get((String)"default_server", null, (Map)val);
            String fallbackServer = (String)this.get((String)"fallback_server", null, (Map)val);
            if (defaultServer != null) {
                serverPriority.add(defaultServer);
                this.set((String)"default_server", null, (Map)val);
            }
            if (fallbackServer != null) {
                serverPriority.add(fallbackServer);
                this.set((String)"fallback_server", null, (Map)val);
            }
            if (serverPriority.isEmpty()) {
                serverPriority.add("lobby");
            }
            this.set((String)"priorities", serverPriority, (Map)val);
            ListenerInfo info = new ListenerInfo((InetSocketAddress)address, (String)motd, (int)maxPlayers, (int)tabListSize, serverPriority, (boolean)forceDefault, forced, (String)value.toString(), (boolean)setLocalAddress, (boolean)pingPassthrough, (int)queryPort, (boolean)query, (boolean)proxyProtocol);
            ret.add((ListenerInfo)info);
        }
        return ret;
    }

    @Override
    public Collection<String> getGroups(String player) {
        Collection groups = (Collection)this.get((String)("groups." + player), null);
        HashSet<String> ret = groups == null ? new HashSet<String>() : new HashSet<E>(groups);
        ret.add("default");
        return ret;
    }

    @Override
    public Collection<?> getList(String path, Collection<?> def) {
        return this.get((String)path, def);
    }

    @Override
    public Collection<String> getPermissions(String group) {
        Collection<E> collection;
        Collection permissions = (Collection)this.get((String)("permissions." + group), null);
        if (permissions == null) {
            collection = Collections.EMPTY_SET;
            return collection;
        }
        collection = permissions;
        return collection;
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

