/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.conf;

import com.google.common.base.Preconditions;
import gnu.trove.map.TMap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import net.md_5.bungee.util.CaseInsensitiveSet;

public class Configuration
implements ProxyConfig {
    private int timeout = 30000;
    private String uuid = UUID.randomUUID().toString();
    private Collection<ListenerInfo> listeners;
    private TMap<String, ServerInfo> servers;
    private boolean onlineMode = true;
    private boolean logCommands;
    private boolean logPings = true;
    private int playerLimit = -1;
    private Collection<String> disabledCommands;
    private int throttle = 4000;
    private int throttleLimit = 3;
    private boolean ipForward;
    private Favicon favicon;
    private int compressionThreshold = 256;
    private boolean preventProxyConnections;
    private boolean forgeSupport;
    private boolean blockIpsWhenInvalidPacket = true;
    private int maxSimilarNicknames = 4;
    private String maxSimilarNicknamesMessage = "&cToo many players are logging, we allow only premium players to join! Try again to join in 30 seconds.";
    private int maxSimilarNicknamesTime = 30;
    private Collection<String> bypassIps = new ArrayList<String>(Collections.singletonList("127.0.0.1"));

    public void load() {
        ConfigurationAdapter adapter = ProxyServer.getInstance().getConfigurationAdapter();
        adapter.load();
        File fav = new File((String)"server-icon.png");
        if (fav.exists()) {
            try {
                this.favicon = Favicon.create((BufferedImage)ImageIO.read((File)fav));
            }
            catch (IOException | IllegalArgumentException ex) {
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Could not load server icon", (Throwable)ex);
            }
        }
        this.listeners = adapter.getListeners();
        this.timeout = adapter.getInt((String)"timeout", (int)this.timeout);
        this.uuid = adapter.getString((String)"stats", (String)this.uuid);
        this.onlineMode = adapter.getBoolean((String)"online_mode", (boolean)this.onlineMode);
        this.logCommands = adapter.getBoolean((String)"log_commands", (boolean)this.logCommands);
        this.logPings = adapter.getBoolean((String)"log_pings", (boolean)this.logPings);
        this.playerLimit = adapter.getInt((String)"player_limit", (int)this.playerLimit);
        this.throttle = adapter.getInt((String)"connection_throttle", (int)this.throttle);
        this.throttleLimit = adapter.getInt((String)"connection_throttle_limit", (int)this.throttleLimit);
        this.ipForward = adapter.getBoolean((String)"ip_forward", (boolean)this.ipForward);
        this.compressionThreshold = adapter.getInt((String)"network_compression_threshold", (int)this.compressionThreshold);
        this.preventProxyConnections = adapter.getBoolean((String)"prevent_proxy_connections", (boolean)this.preventProxyConnections);
        this.forgeSupport = adapter.getBoolean((String)"forge_support", (boolean)this.forgeSupport);
        this.blockIpsWhenInvalidPacket = adapter.getBoolean((String)"aegis.block-ips-when-invalid-packet", (boolean)this.blockIpsWhenInvalidPacket);
        this.maxSimilarNicknames = adapter.getInt((String)"aegis.max-similar-nicknames.max", (int)this.maxSimilarNicknames);
        this.maxSimilarNicknamesMessage = ChatColor.translateAlternateColorCodes((char)'&', (String)adapter.getString((String)"aegis.max-similar-nicknames.message", (String)this.maxSimilarNicknamesMessage));
        this.maxSimilarNicknamesTime = adapter.getInt((String)"aegis.max-similar-nicknames.time", (int)this.maxSimilarNicknamesTime);
        this.bypassIps = adapter.getList((String)"aegis.bypass-ips", this.bypassIps);
        this.disabledCommands = new CaseInsensitiveSet(adapter.getList((String)"disabled_commands", Arrays.asList("disabledcommandhere")));
        Preconditions.checkArgument((boolean)(this.listeners != null && !this.listeners.isEmpty()), (Object)"No listeners defined.");
        Map<String, ServerInfo> newServers = adapter.getServers();
        Preconditions.checkArgument((boolean)(newServers != null && !newServers.isEmpty()), (Object)"No servers defined");
        if (this.servers == null) {
            this.servers = new CaseInsensitiveMap<ServerInfo>(newServers);
        } else {
            for (ServerInfo oldServer : this.servers.values()) {
                Preconditions.checkArgument((boolean)newServers.containsKey((Object)oldServer.getName()), (String)"Server %s removed on reload!", (Object)oldServer.getName());
            }
            for (Map.Entry newServer : newServers.entrySet()) {
                if (this.servers.containsValue(newServer.getValue())) continue;
                this.servers.put((String)((String)newServer.getKey()), (ServerInfo)((ServerInfo)newServer.getValue()));
            }
        }
        Iterator<Object> iterator = this.listeners.iterator();
        block4 : while (iterator.hasNext()) {
            String server;
            ListenerInfo listener = (ListenerInfo)iterator.next();
            for (int i = 0; i < listener.getServerPriority().size(); ++i) {
                server = listener.getServerPriority().get((int)i);
                Preconditions.checkArgument((boolean)this.servers.containsKey((Object)server), (String)"Server %s (priority %s) is not defined", (Object)server, (int)i);
            }
            Iterator<String> i = listener.getForcedHosts().values().iterator();
            do {
                if (!i.hasNext()) continue block4;
                server = i.next();
                if (this.servers.containsKey((Object)server)) continue;
                ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)"Forced host server {0} is not defined", (Object)server);
            } while (true);
            break;
        }
        return;
    }

    @Deprecated
    @Override
    public String getFavicon() {
        return this.getFaviconObject().getEncoded();
    }

    @Override
    public Favicon getFaviconObject() {
        return this.favicon;
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public Collection<ListenerInfo> getListeners() {
        return this.listeners;
    }

    public TMap<String, ServerInfo> getServers() {
        return this.servers;
    }

    @Override
    public boolean isOnlineMode() {
        return this.onlineMode;
    }

    @Override
    public boolean isLogCommands() {
        return this.logCommands;
    }

    public boolean isLogPings() {
        return this.logPings;
    }

    @Override
    public int getPlayerLimit() {
        return this.playerLimit;
    }

    @Override
    public Collection<String> getDisabledCommands() {
        return this.disabledCommands;
    }

    @Override
    public int getThrottle() {
        return this.throttle;
    }

    public int getThrottleLimit() {
        return this.throttleLimit;
    }

    @Override
    public boolean isIpForward() {
        return this.ipForward;
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public boolean isPreventProxyConnections() {
        return this.preventProxyConnections;
    }

    public boolean isForgeSupport() {
        return this.forgeSupport;
    }

    public boolean isBlockIpsWhenInvalidPacket() {
        return this.blockIpsWhenInvalidPacket;
    }

    public int getMaxSimilarNicknames() {
        return this.maxSimilarNicknames;
    }

    public String getMaxSimilarNicknamesMessage() {
        return this.maxSimilarNicknamesMessage;
    }

    public int getMaxSimilarNicknamesTime() {
        return this.maxSimilarNicknamesTime;
    }

    public Collection<String> getBypassIps() {
        return this.bypassIps;
    }
}

