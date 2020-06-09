/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelException;

public class ChannelPipelineException
extends ChannelException {
    private static final long serialVersionUID = 3379174210419885980L;

    public ChannelPipelineException() {
    }

    public ChannelPipelineException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public ChannelPipelineException(String message) {
        super((String)message);
    }

    public ChannelPipelineException(Throwable cause) {
        super((Throwable)cause);
    }
}

