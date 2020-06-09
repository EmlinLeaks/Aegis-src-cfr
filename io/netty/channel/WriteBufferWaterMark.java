/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.util.internal.ObjectUtil;

public final class WriteBufferWaterMark {
    private static final int DEFAULT_LOW_WATER_MARK = 32768;
    private static final int DEFAULT_HIGH_WATER_MARK = 65536;
    public static final WriteBufferWaterMark DEFAULT = new WriteBufferWaterMark((int)32768, (int)65536, (boolean)false);
    private final int low;
    private final int high;

    public WriteBufferWaterMark(int low, int high) {
        this((int)low, (int)high, (boolean)true);
    }

    WriteBufferWaterMark(int low, int high, boolean validate) {
        if (validate) {
            ObjectUtil.checkPositiveOrZero((int)low, (String)"low");
            if (high < low) {
                throw new IllegalArgumentException((String)("write buffer's high water mark cannot be less than  low water mark (" + low + "): " + high));
            }
        }
        this.low = low;
        this.high = high;
    }

    public int low() {
        return this.low;
    }

    public int high() {
        return this.high;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder((int)55).append((String)"WriteBufferWaterMark(low: ").append((int)this.low).append((String)", high: ").append((int)this.high).append((String)")");
        return builder.toString();
    }
}

