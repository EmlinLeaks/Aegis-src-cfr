/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.bootstrap;

import io.netty.channel.Channel;

@Deprecated
public interface ChannelFactory<T extends Channel> {
    public T newChannel();
}

