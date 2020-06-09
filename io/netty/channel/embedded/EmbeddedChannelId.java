/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.embedded;

import io.netty.channel.ChannelId;

final class EmbeddedChannelId
implements ChannelId {
    private static final long serialVersionUID = -251711922203466130L;
    static final ChannelId INSTANCE = new EmbeddedChannelId();

    private EmbeddedChannelId() {
    }

    @Override
    public String asShortText() {
        return this.toString();
    }

    @Override
    public String asLongText() {
        return this.toString();
    }

    @Override
    public int compareTo(ChannelId o) {
        if (!(o instanceof EmbeddedChannelId)) return this.asLongText().compareTo((String)o.asLongText());
        return 0;
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object obj) {
        return obj instanceof EmbeddedChannelId;
    }

    public String toString() {
        return "embedded";
    }
}

