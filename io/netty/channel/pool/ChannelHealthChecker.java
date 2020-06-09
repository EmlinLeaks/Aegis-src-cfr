/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.pool;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.util.concurrent.Future;

public interface ChannelHealthChecker {
    public static final ChannelHealthChecker ACTIVE = new ChannelHealthChecker(){

        public Future<Boolean> isHealthy(Channel channel) {
            Future<Boolean> future;
            io.netty.channel.EventLoop loop = channel.eventLoop();
            if (channel.isActive()) {
                future = loop.newSucceededFuture(Boolean.TRUE);
                return future;
            }
            future = loop.newSucceededFuture(Boolean.FALSE);
            return future;
        }
    };

    public Future<Boolean> isHealthy(Channel var1);
}

