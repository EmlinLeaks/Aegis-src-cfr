/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.ObjectUtil;

public class FixedRecvByteBufAllocator
extends DefaultMaxMessagesRecvByteBufAllocator {
    private final int bufferSize;

    public FixedRecvByteBufAllocator(int bufferSize) {
        ObjectUtil.checkPositive((int)bufferSize, (String)"bufferSize");
        this.bufferSize = bufferSize;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl((FixedRecvByteBufAllocator)this, (int)this.bufferSize);
    }

    @Override
    public FixedRecvByteBufAllocator respectMaybeMoreData(boolean respectMaybeMoreData) {
        super.respectMaybeMoreData((boolean)respectMaybeMoreData);
        return this;
    }
}

