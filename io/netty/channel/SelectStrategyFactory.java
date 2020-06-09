/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.SelectStrategy;

public interface SelectStrategyFactory {
    public SelectStrategy newSelectStrategy();
}

