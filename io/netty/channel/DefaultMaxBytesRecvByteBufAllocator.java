/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.DefaultMaxBytesRecvByteBufAllocator;
import io.netty.channel.MaxBytesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractMap;
import java.util.Map;

public class DefaultMaxBytesRecvByteBufAllocator
implements MaxBytesRecvByteBufAllocator {
    private volatile int maxBytesPerRead;
    private volatile int maxBytesPerIndividualRead;

    public DefaultMaxBytesRecvByteBufAllocator() {
        this((int)65536, (int)65536);
    }

    public DefaultMaxBytesRecvByteBufAllocator(int maxBytesPerRead, int maxBytesPerIndividualRead) {
        DefaultMaxBytesRecvByteBufAllocator.checkMaxBytesPerReadPair((int)maxBytesPerRead, (int)maxBytesPerIndividualRead);
        this.maxBytesPerRead = maxBytesPerRead;
        this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl((DefaultMaxBytesRecvByteBufAllocator)this, null);
    }

    @Override
    public int maxBytesPerRead() {
        return this.maxBytesPerRead;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefaultMaxBytesRecvByteBufAllocator maxBytesPerRead(int maxBytesPerRead) {
        ObjectUtil.checkPositive((int)maxBytesPerRead, (String)"maxBytesPerRead");
        DefaultMaxBytesRecvByteBufAllocator defaultMaxBytesRecvByteBufAllocator = this;
        // MONITORENTER : defaultMaxBytesRecvByteBufAllocator
        int maxBytesPerIndividualRead = this.maxBytesPerIndividualRead();
        if (maxBytesPerRead < maxBytesPerIndividualRead) {
            throw new IllegalArgumentException((String)("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead));
        }
        this.maxBytesPerRead = maxBytesPerRead;
        // MONITOREXIT : defaultMaxBytesRecvByteBufAllocator
        return this;
    }

    @Override
    public int maxBytesPerIndividualRead() {
        return this.maxBytesPerIndividualRead;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefaultMaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int maxBytesPerIndividualRead) {
        ObjectUtil.checkPositive((int)maxBytesPerIndividualRead, (String)"maxBytesPerIndividualRead");
        DefaultMaxBytesRecvByteBufAllocator defaultMaxBytesRecvByteBufAllocator = this;
        // MONITORENTER : defaultMaxBytesRecvByteBufAllocator
        int maxBytesPerRead = this.maxBytesPerRead();
        if (maxBytesPerIndividualRead > maxBytesPerRead) {
            throw new IllegalArgumentException((String)("maxBytesPerIndividualRead cannot be greater than maxBytesPerRead (" + maxBytesPerRead + "): " + maxBytesPerIndividualRead));
        }
        this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
        // MONITOREXIT : defaultMaxBytesRecvByteBufAllocator
        return this;
    }

    @Override
    public synchronized Map.Entry<Integer, Integer> maxBytesPerReadPair() {
        return new AbstractMap.SimpleEntry<Integer, Integer>(Integer.valueOf((int)this.maxBytesPerRead), Integer.valueOf((int)this.maxBytesPerIndividualRead));
    }

    private static void checkMaxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
        ObjectUtil.checkPositive((int)maxBytesPerRead, (String)"maxBytesPerRead");
        ObjectUtil.checkPositive((int)maxBytesPerIndividualRead, (String)"maxBytesPerIndividualRead");
        if (maxBytesPerRead >= maxBytesPerIndividualRead) return;
        throw new IllegalArgumentException((String)("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DefaultMaxBytesRecvByteBufAllocator maxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
        DefaultMaxBytesRecvByteBufAllocator.checkMaxBytesPerReadPair((int)maxBytesPerRead, (int)maxBytesPerIndividualRead);
        DefaultMaxBytesRecvByteBufAllocator defaultMaxBytesRecvByteBufAllocator = this;
        // MONITORENTER : defaultMaxBytesRecvByteBufAllocator
        this.maxBytesPerRead = maxBytesPerRead;
        this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
        // MONITOREXIT : defaultMaxBytesRecvByteBufAllocator
        return this;
    }
}

