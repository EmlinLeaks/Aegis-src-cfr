/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.DomainNameMapping;
import io.netty.util.DomainNameMappingBuilder;
import io.netty.util.internal.ObjectUtil;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DomainNameMappingBuilder<V> {
    private final V defaultValue;
    private final Map<String, V> map;

    public DomainNameMappingBuilder(V defaultValue) {
        this((int)4, defaultValue);
    }

    public DomainNameMappingBuilder(int initialCapacity, V defaultValue) {
        this.defaultValue = ObjectUtil.checkNotNull(defaultValue, (String)"defaultValue");
        this.map = new LinkedHashMap<String, V>((int)initialCapacity);
    }

    public DomainNameMappingBuilder<V> add(String hostname, V output) {
        this.map.put((String)ObjectUtil.checkNotNull(hostname, (String)"hostname"), ObjectUtil.checkNotNull(output, (String)"output"));
        return this;
    }

    public DomainNameMapping<V> build() {
        return new ImmutableDomainNameMapping<V>(this.defaultValue, this.map, null);
    }
}

