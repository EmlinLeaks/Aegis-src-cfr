/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.CompleteChannelFuture;
import io.netty.util.concurrent.EventExecutor;

final class SucceededChannelFuture
extends CompleteChannelFuture {
    SucceededChannelFuture(Channel channel, EventExecutor executor) {
        super((Channel)channel, (EventExecutor)executor);
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}

