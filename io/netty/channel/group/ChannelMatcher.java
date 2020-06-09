/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.group;

import io.netty.channel.Channel;

public interface ChannelMatcher {
    public boolean matches(Channel var1);
}

