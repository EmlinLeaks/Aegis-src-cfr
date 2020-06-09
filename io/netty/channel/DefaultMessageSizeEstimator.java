/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.util.internal.ObjectUtil;

public final class DefaultMessageSizeEstimator
implements MessageSizeEstimator {
    public static final MessageSizeEstimator DEFAULT = new DefaultMessageSizeEstimator((int)8);
    private final MessageSizeEstimator.Handle handle;

    public DefaultMessageSizeEstimator(int unknownSize) {
        ObjectUtil.checkPositiveOrZero((int)unknownSize, (String)"unknownSize");
        this.handle = new HandleImpl((int)unknownSize, null);
    }

    @Override
    public MessageSizeEstimator.Handle newHandle() {
        return this.handle;
    }
}

