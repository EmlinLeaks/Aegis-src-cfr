/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.Mapping;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DomainNameMapping<V>
implements Mapping<String, V> {
    final V defaultValue;
    private final Map<String, V> map;
    private final Map<String, V> unmodifiableMap;

    @Deprecated
    public DomainNameMapping(V defaultValue) {
        this((int)4, defaultValue);
    }

    @Deprecated
    public DomainNameMapping(int initialCapacity, V defaultValue) {
        this(new LinkedHashMap<K, V>((int)initialCapacity), defaultValue);
    }

    DomainNameMapping(Map<String, V> map, V defaultValue) {
        this.defaultValue = ObjectUtil.checkNotNull(defaultValue, (String)"defaultValue");
        this.map = map;
        this.unmodifiableMap = map != null ? Collections.unmodifiableMap(map) : null;
    }

    @Deprecated
    public DomainNameMapping<V> add(String hostname, V output) {
        this.map.put((String)DomainNameMapping.normalizeHostname((String)ObjectUtil.checkNotNull(hostname, (String)"hostname")), ObjectUtil.checkNotNull(output, (String)"output"));
        return this;
    }

    static boolean matches(String template, String hostName) {
        if (!template.startsWith((String)"*.")) return template.equals((Object)hostName);
        if (template.regionMatches((int)2, (String)hostName, (int)0, (int)hostName.length())) return true;
        if (StringUtil.commonSuffixOfLength((String)hostName, (String)template, (int)(template.length() - 1))) return true;
        return false;
    }

    static String normalizeHostname(String hostname) {
        if (!DomainNameMapping.needsNormalization((String)hostname)) return hostname.toLowerCase((Locale)Locale.US);
        hostname = IDN.toASCII((String)hostname, (int)1);
        return hostname.toLowerCase((Locale)Locale.US);
    }

    private static boolean needsNormalization(String hostname) {
        int length = hostname.length();
        int i = 0;
        while (i < length) {
            char c = hostname.charAt((int)i);
            if (c > '') {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public V map(String hostname) {
        Map.Entry<String, V> entry;
        if (hostname == null) return (V)this.defaultValue;
        hostname = DomainNameMapping.normalizeHostname((String)hostname);
        Iterator<Map.Entry<String, V>> iterator = this.map.entrySet().iterator();
        do {
            if (!iterator.hasNext()) return (V)this.defaultValue;
        } while (!DomainNameMapping.matches((String)(entry = iterator.next()).getKey(), (String)hostname));
        return (V)entry.getValue();
    }

    public Map<String, V> asMap() {
        return this.unmodifiableMap;
    }

    public String toString() {
        return StringUtil.simpleClassName((Object)this) + "(default: " + this.defaultValue + ", map: " + this.map + ')';
    }
}

