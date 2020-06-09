/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.config;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class ListenerInfo {
    private final InetSocketAddress host;
    private final String motd;
    private final int maxPlayers;
    private final int tabListSize;
    private final List<String> serverPriority;
    private final boolean forceDefault;
    private final Map<String, String> forcedHosts;
    private final String tabListType;
    private final boolean setLocalAddress;
    private final boolean pingPassthrough;
    private final int queryPort;
    private final boolean queryEnabled;
    private final boolean proxyProtocol;

    @Deprecated
    public ListenerInfo(InetSocketAddress host, String motd, int maxPlayers, int tabListSize, List<String> serverPriority, boolean forceDefault, Map<String, String> forcedHosts, String tabListType, boolean setLocalAddress, boolean pingPassthrough, int queryPort, boolean queryEnabled) {
        this((InetSocketAddress)host, (String)motd, (int)maxPlayers, (int)tabListSize, serverPriority, (boolean)forceDefault, forcedHosts, (String)tabListType, (boolean)setLocalAddress, (boolean)pingPassthrough, (int)queryPort, (boolean)queryEnabled, (boolean)false);
    }

    @Deprecated
    public String getDefaultServer() {
        return this.serverPriority.get((int)0);
    }

    @Deprecated
    public String getFallbackServer() {
        String string;
        if (this.serverPriority.size() > 1) {
            string = this.serverPriority.get((int)1);
            return string;
        }
        string = this.getDefaultServer();
        return string;
    }

    public InetSocketAddress getHost() {
        return this.host;
    }

    public String getMotd() {
        return this.motd;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getTabListSize() {
        return this.tabListSize;
    }

    public List<String> getServerPriority() {
        return this.serverPriority;
    }

    public boolean isForceDefault() {
        return this.forceDefault;
    }

    public Map<String, String> getForcedHosts() {
        return this.forcedHosts;
    }

    public String getTabListType() {
        return this.tabListType;
    }

    public boolean isSetLocalAddress() {
        return this.setLocalAddress;
    }

    public boolean isPingPassthrough() {
        return this.pingPassthrough;
    }

    public int getQueryPort() {
        return this.queryPort;
    }

    public boolean isQueryEnabled() {
        return this.queryEnabled;
    }

    public boolean isProxyProtocol() {
        return this.proxyProtocol;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ListenerInfo)) {
            return false;
        }
        ListenerInfo other = (ListenerInfo)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        InetSocketAddress this$host = this.getHost();
        InetSocketAddress other$host = other.getHost();
        if (this$host == null ? other$host != null : !((Object)this$host).equals((Object)other$host)) {
            return false;
        }
        String this$motd = this.getMotd();
        String other$motd = other.getMotd();
        if (this$motd == null ? other$motd != null : !this$motd.equals((Object)other$motd)) {
            return false;
        }
        if (this.getMaxPlayers() != other.getMaxPlayers()) {
            return false;
        }
        if (this.getTabListSize() != other.getTabListSize()) {
            return false;
        }
        List<String> this$serverPriority = this.getServerPriority();
        List<String> other$serverPriority = other.getServerPriority();
        if (this$serverPriority == null ? other$serverPriority != null : !((Object)this$serverPriority).equals(other$serverPriority)) {
            return false;
        }
        if (this.isForceDefault() != other.isForceDefault()) {
            return false;
        }
        Map<String, String> this$forcedHosts = this.getForcedHosts();
        Map<String, String> other$forcedHosts = other.getForcedHosts();
        if (this$forcedHosts == null ? other$forcedHosts != null : !((Object)this$forcedHosts).equals(other$forcedHosts)) {
            return false;
        }
        String this$tabListType = this.getTabListType();
        String other$tabListType = other.getTabListType();
        if (this$tabListType == null ? other$tabListType != null : !this$tabListType.equals((Object)other$tabListType)) {
            return false;
        }
        if (this.isSetLocalAddress() != other.isSetLocalAddress()) {
            return false;
        }
        if (this.isPingPassthrough() != other.isPingPassthrough()) {
            return false;
        }
        if (this.getQueryPort() != other.getQueryPort()) {
            return false;
        }
        if (this.isQueryEnabled() != other.isQueryEnabled()) {
            return false;
        }
        if (this.isProxyProtocol() == other.isProxyProtocol()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ListenerInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        InetSocketAddress $host = this.getHost();
        result = result * 59 + ($host == null ? 43 : ((Object)$host).hashCode());
        String $motd = this.getMotd();
        result = result * 59 + ($motd == null ? 43 : $motd.hashCode());
        result = result * 59 + this.getMaxPlayers();
        result = result * 59 + this.getTabListSize();
        List<String> $serverPriority = this.getServerPriority();
        result = result * 59 + ($serverPriority == null ? 43 : ((Object)$serverPriority).hashCode());
        result = result * 59 + (this.isForceDefault() ? 79 : 97);
        Map<String, String> $forcedHosts = this.getForcedHosts();
        result = result * 59 + ($forcedHosts == null ? 43 : ((Object)$forcedHosts).hashCode());
        String $tabListType = this.getTabListType();
        result = result * 59 + ($tabListType == null ? 43 : $tabListType.hashCode());
        result = result * 59 + (this.isSetLocalAddress() ? 79 : 97);
        result = result * 59 + (this.isPingPassthrough() ? 79 : 97);
        result = result * 59 + this.getQueryPort();
        result = result * 59 + (this.isQueryEnabled() ? 79 : 97);
        return result * 59 + (this.isProxyProtocol() ? 79 : 97);
    }

    public String toString() {
        return "ListenerInfo(host=" + this.getHost() + ", motd=" + this.getMotd() + ", maxPlayers=" + this.getMaxPlayers() + ", tabListSize=" + this.getTabListSize() + ", serverPriority=" + this.getServerPriority() + ", forceDefault=" + this.isForceDefault() + ", forcedHosts=" + this.getForcedHosts() + ", tabListType=" + this.getTabListType() + ", setLocalAddress=" + this.isSetLocalAddress() + ", pingPassthrough=" + this.isPingPassthrough() + ", queryPort=" + this.getQueryPort() + ", queryEnabled=" + this.isQueryEnabled() + ", proxyProtocol=" + this.isProxyProtocol() + ")";
    }

    public ListenerInfo(InetSocketAddress host, String motd, int maxPlayers, int tabListSize, List<String> serverPriority, boolean forceDefault, Map<String, String> forcedHosts, String tabListType, boolean setLocalAddress, boolean pingPassthrough, int queryPort, boolean queryEnabled, boolean proxyProtocol) {
        this.host = host;
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.tabListSize = tabListSize;
        this.serverPriority = serverPriority;
        this.forceDefault = forceDefault;
        this.forcedHosts = forcedHosts;
        this.tabListType = tabListType;
        this.setLocalAddress = setLocalAddress;
        this.pingPassthrough = pingPassthrough;
        this.queryPort = queryPort;
        this.queryEnabled = queryEnabled;
        this.proxyProtocol = proxyProtocol;
    }
}

