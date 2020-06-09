/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.pool;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;

public abstract class AbstractChannelPoolHandler
implements ChannelPoolHandler {
    @Override
    public void channelAcquired(Channel ch) throws Exception {
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
    }
}

