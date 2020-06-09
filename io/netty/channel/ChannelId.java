/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import java.io.Serializable;

public interface ChannelId
extends Serializable,
Comparable<ChannelId> {
    public String asShortText();

    public String asLongText();
}

