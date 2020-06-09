/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.DomainNameMapping;
import io.netty.util.DomainNameMappingBuilder;

@Deprecated
public final class DomainMappingBuilder<V> {
    private final DomainNameMappingBuilder<V> builder;

    public DomainMappingBuilder(V defaultValue) {
        this.builder = new DomainNameMappingBuilder<V>(defaultValue);
    }

    public DomainMappingBuilder(int initialCapacity, V defaultValue) {
        this.builder = new DomainNameMappingBuilder<V>((int)initialCapacity, defaultValue);
    }

    public DomainMappingBuilder<V> add(String hostname, V output) {
        this.builder.add((String)hostname, output);
        return this;
    }

    public DomainNameMapping<V> build() {
        return this.builder.build();
    }
}

