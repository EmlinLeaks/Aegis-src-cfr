/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;

public class AdaptiveRecvByteBufAllocator
extends DefaultMaxMessagesRecvByteBufAllocator {
    static final int DEFAULT_MINIMUM = 64;
    static final int DEFAULT_INITIAL = 1024;
    static final int DEFAULT_MAXIMUM = 65536;
    private static final int INDEX_INCREMENT = 4;
    private static final int INDEX_DECREMENT = 1;
    private static final int[] SIZE_TABLE;
    @Deprecated
    public static final AdaptiveRecvByteBufAllocator DEFAULT;
    private final int minIndex;
    private final int maxIndex;
    private final int initial;

    private static int getSizeTableIndex(int size) {
        int low = 0;
        int high = SIZE_TABLE.length - 1;
        while (high >= low) {
            if (high == low) {
                return high;
            }
            int mid = low + high >>> 1;
            int a = SIZE_TABLE[mid];
            int b = SIZE_TABLE[mid + 1];
            if (size > b) {
                low = mid + 1;
                continue;
            }
            if (size >= a) {
                if (size != a) return mid + 1;
                return mid;
            }
            high = mid - 1;
        }
        return low;
    }

    public AdaptiveRecvByteBufAllocator() {
        this((int)64, (int)1024, (int)65536);
    }

    public AdaptiveRecvByteBufAllocator(int minimum, int initial, int maximum) {
        ObjectUtil.checkPositive((int)minimum, (String)"minimum");
        if (initial < minimum) {
            throw new IllegalArgumentException((String)("initial: " + initial));
        }
        if (maximum < initial) {
            throw new IllegalArgumentException((String)("maximum: " + maximum));
        }
        int minIndex = AdaptiveRecvByteBufAllocator.getSizeTableIndex((int)minimum);
        this.minIndex = SIZE_TABLE[minIndex] < minimum ? minIndex + 1 : minIndex;
        int maxIndex = AdaptiveRecvByteBufAllocator.getSizeTableIndex((int)maximum);
        this.maxIndex = SIZE_TABLE[maxIndex] > maximum ? maxIndex - 1 : maxIndex;
        this.initial = initial;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl((AdaptiveRecvByteBufAllocator)this, (int)this.minIndex, (int)this.maxIndex, (int)this.initial);
    }

    @Override
    public AdaptiveRecvByteBufAllocator respectMaybeMoreData(boolean respectMaybeMoreData) {
        super.respectMaybeMoreData((boolean)respectMaybeMoreData);
        return this;
    }

    static /* synthetic */ int access$000(int x0) {
        return AdaptiveRecvByteBufAllocator.getSizeTableIndex((int)x0);
    }

    static /* synthetic */ int[] access$100() {
        return SIZE_TABLE;
    }

    static {
        int i;
        ArrayList<Integer> sizeTable = new ArrayList<Integer>();
        for (i = 16; i < 512; i += 16) {
            sizeTable.add(Integer.valueOf((int)i));
        }
        for (i = 512; i > 0; i <<= 1) {
            sizeTable.add(Integer.valueOf((int)i));
        }
        SIZE_TABLE = new int[sizeTable.size()];
        i = 0;
        do {
            if (i >= SIZE_TABLE.length) {
                DEFAULT = new AdaptiveRecvByteBufAllocator();
                return;
            }
            AdaptiveRecvByteBufAllocator.SIZE_TABLE[i] = ((Integer)sizeTable.get((int)i)).intValue();
            ++i;
        } while (true);
    }
}

