/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.SelectStrategy;
import io.netty.util.IntSupplier;

final class DefaultSelectStrategy
implements SelectStrategy {
    static final SelectStrategy INSTANCE = new DefaultSelectStrategy();

    private DefaultSelectStrategy() {
    }

    @Override
    public int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception {
        if (!hasTasks) return -1;
        int n = selectSupplier.get();
        return n;
    }
}

