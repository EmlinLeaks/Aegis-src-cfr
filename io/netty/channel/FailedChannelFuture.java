/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.CompleteChannelFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.PlatformDependent;

final class FailedChannelFuture
extends CompleteChannelFuture {
    private final Throwable cause;

    FailedChannelFuture(Channel channel, EventExecutor executor, Throwable cause) {
        super((Channel)channel, (EventExecutor)executor);
        if (cause == null) {
            throw new NullPointerException((String)"cause");
        }
        this.cause = cause;
    }

    @Override
    public Throwable cause() {
        return this.cause;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public ChannelFuture sync() {
        PlatformDependent.throwException((Throwable)this.cause);
        return this;
    }

    @Override
    public ChannelFuture syncUninterruptibly() {
        PlatformDependent.throwException((Throwable)this.cause);
        return this;
    }
}

