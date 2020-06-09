/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Beta
@Immutable
@GwtCompatible
public final class HostAndPort
implements Serializable {
    private static final int NO_PORT = -1;
    private final String host;
    private final int port;
    private final boolean hasBracketlessColons;
    private static final long serialVersionUID = 0L;

    private HostAndPort(String host, int port, boolean hasBracketlessColons) {
        this.host = host;
        this.port = port;
        this.hasBracketlessColons = hasBracketlessColons;
    }

    public String getHost() {
        return this.host;
    }

    @Deprecated
    public String getHostText() {
        return this.host;
    }

    public boolean hasPort() {
        if (this.port < 0) return false;
        return true;
    }

    public int getPort() {
        Preconditions.checkState((boolean)this.hasPort());
        return this.port;
    }

    public int getPortOrDefault(int defaultPort) {
        int n;
        if (this.hasPort()) {
            n = this.port;
            return n;
        }
        n = defaultPort;
        return n;
    }

    public static HostAndPort fromParts(String host, int port) {
        Preconditions.checkArgument((boolean)HostAndPort.isValidPort((int)port), (String)"Port out of range: %s", (int)port);
        HostAndPort parsedHost = HostAndPort.fromString((String)host);
        Preconditions.checkArgument((boolean)(!parsedHost.hasPort()), (String)"Host has a port: %s", (Object)host);
        return new HostAndPort((String)parsedHost.host, (int)port, (boolean)parsedHost.hasBracketlessColons);
    }

    public static HostAndPort fromHost(String host) {
        HostAndPort parsedHost = HostAndPort.fromString((String)host);
        Preconditions.checkArgument((boolean)(!parsedHost.hasPort()), (String)"Host has a port: %s", (Object)host);
        return parsedHost;
    }

    public static HostAndPort fromString(String hostPortString) {
        String host;
        Preconditions.checkNotNull(hostPortString);
        String portString = null;
        boolean hasBracketlessColons = false;
        if (hostPortString.startsWith((String)"[")) {
            String[] hostAndPort = HostAndPort.getHostAndPortFromBracketedHost((String)hostPortString);
            host = hostAndPort[0];
            portString = hostAndPort[1];
        } else {
            int colonPos = hostPortString.indexOf((int)58);
            if (colonPos >= 0 && hostPortString.indexOf((int)58, (int)(colonPos + 1)) == -1) {
                host = hostPortString.substring((int)0, (int)colonPos);
                portString = hostPortString.substring((int)(colonPos + 1));
            } else {
                host = hostPortString;
                hasBracketlessColons = colonPos >= 0;
            }
        }
        int port = -1;
        if (Strings.isNullOrEmpty((String)portString)) return new HostAndPort((String)host, (int)port, (boolean)hasBracketlessColons);
        Preconditions.checkArgument((boolean)(!portString.startsWith((String)"+")), (String)"Unparseable port number: %s", (Object)hostPortString);
        try {
            port = Integer.parseInt((String)portString);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException((String)("Unparseable port number: " + hostPortString));
        }
        Preconditions.checkArgument((boolean)HostAndPort.isValidPort((int)port), (String)"Port number out of range: %s", (Object)hostPortString);
        return new HostAndPort((String)host, (int)port, (boolean)hasBracketlessColons);
    }

    private static String[] getHostAndPortFromBracketedHost(String hostPortString) {
        int colonIndex = 0;
        int closeBracketIndex = 0;
        Preconditions.checkArgument((boolean)(hostPortString.charAt((int)0) == '['), (String)"Bracketed host-port string must start with a bracket: %s", (Object)hostPortString);
        colonIndex = hostPortString.indexOf((int)58);
        closeBracketIndex = hostPortString.lastIndexOf((int)93);
        Preconditions.checkArgument((boolean)(colonIndex > -1 && closeBracketIndex > colonIndex), (String)"Invalid bracketed host/port: %s", (Object)hostPortString);
        String host = hostPortString.substring((int)1, (int)closeBracketIndex);
        if (closeBracketIndex + 1 == hostPortString.length()) {
            return new String[]{host, ""};
        }
        Preconditions.checkArgument((boolean)(hostPortString.charAt((int)(closeBracketIndex + 1)) == ':'), (String)"Only a colon may follow a close bracket: %s", (Object)hostPortString);
        int i = closeBracketIndex + 2;
        while (i < hostPortString.length()) {
            Preconditions.checkArgument((boolean)Character.isDigit((char)hostPortString.charAt((int)i)), (String)"Port must be numeric: %s", (Object)hostPortString);
            ++i;
        }
        return new String[]{host, hostPortString.substring((int)(closeBracketIndex + 2))};
    }

    public HostAndPort withDefaultPort(int defaultPort) {
        Preconditions.checkArgument((boolean)HostAndPort.isValidPort((int)defaultPort));
        if (this.hasPort()) return this;
        if (this.port != defaultPort) return new HostAndPort((String)this.host, (int)defaultPort, (boolean)this.hasBracketlessColons);
        return this;
    }

    public HostAndPort requireBracketsForIPv6() {
        Preconditions.checkArgument((boolean)(!this.hasBracketlessColons), (String)"Possible bracketless IPv6 literal: %s", (Object)this.host);
        return this;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HostAndPort)) return false;
        HostAndPort that = (HostAndPort)other;
        if (!Objects.equal((Object)this.host, (Object)that.host)) return false;
        if (this.port != that.port) return false;
        if (this.hasBracketlessColons != that.hasBracketlessColons) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.host, Integer.valueOf((int)this.port), Boolean.valueOf((boolean)this.hasBracketlessColons)});
    }

    public String toString() {
        StringBuilder builder = new StringBuilder((int)(this.host.length() + 8));
        if (this.host.indexOf((int)58) >= 0) {
            builder.append((char)'[').append((String)this.host).append((char)']');
        } else {
            builder.append((String)this.host);
        }
        if (!this.hasPort()) return builder.toString();
        builder.append((char)':').append((int)this.port);
        return builder.toString();
    }

    private static boolean isValidPort(int port) {
        if (port < 0) return false;
        if (port > 65535) return false;
        return true;
    }
}

