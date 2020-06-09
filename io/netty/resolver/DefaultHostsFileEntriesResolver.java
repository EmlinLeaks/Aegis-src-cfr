/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.DefaultHostsFileEntriesResolver;
import io.netty.resolver.HostsFileEntries;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.HostsFileParser;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

public final class DefaultHostsFileEntriesResolver
implements HostsFileEntriesResolver {
    private final Map<String, Inet4Address> inet4Entries;
    private final Map<String, Inet6Address> inet6Entries;

    public DefaultHostsFileEntriesResolver() {
        this((HostsFileEntries)DefaultHostsFileEntriesResolver.parseEntries());
    }

    DefaultHostsFileEntriesResolver(HostsFileEntries entries) {
        this.inet4Entries = entries.inet4Entries();
        this.inet6Entries = entries.inet6Entries();
    }

    @Override
    public InetAddress address(String inetHost, ResolvedAddressTypes resolvedAddressTypes) {
        String normalized = this.normalize((String)inetHost);
        switch (1.$SwitchMap$io$netty$resolver$ResolvedAddressTypes[resolvedAddressTypes.ordinal()]) {
            case 1: {
                return (InetAddress)this.inet4Entries.get((Object)normalized);
            }
            case 2: {
                return (InetAddress)this.inet6Entries.get((Object)normalized);
            }
            case 3: {
                InetAddress inetAddress;
                Inet4Address inet4Address = this.inet4Entries.get((Object)normalized);
                if (inet4Address != null) {
                    inetAddress = inet4Address;
                    return inetAddress;
                }
                inetAddress = (InetAddress)this.inet6Entries.get((Object)normalized);
                return inetAddress;
            }
            case 4: {
                InetAddress inetAddress;
                Inet6Address inet6Address = this.inet6Entries.get((Object)normalized);
                if (inet6Address != null) {
                    inetAddress = inet6Address;
                    return inetAddress;
                }
                inetAddress = (InetAddress)this.inet4Entries.get((Object)normalized);
                return inetAddress;
            }
        }
        throw new IllegalArgumentException((String)("Unknown ResolvedAddressTypes " + (Object)((Object)resolvedAddressTypes)));
    }

    String normalize(String inetHost) {
        return inetHost.toLowerCase((Locale)Locale.ENGLISH);
    }

    private static HostsFileEntries parseEntries() {
        if (!PlatformDependent.isWindows()) return HostsFileParser.parseSilently();
        return HostsFileParser.parseSilently((Charset[])new Charset[]{Charset.defaultCharset(), CharsetUtil.UTF_16, CharsetUtil.UTF_8});
    }
}

