/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

public final class MathUtil {
    private MathUtil() {
    }

    public static int findNextPositivePowerOfTwo(int value) {
        if ($assertionsDisabled) return 1 << 32 - Integer.numberOfLeadingZeros((int)(value - 1));
        if (value <= Integer.MIN_VALUE) throw new AssertionError();
        if (value < 1073741824) return 1 << 32 - Integer.numberOfLeadingZeros((int)(value - 1));
        throw new AssertionError();
    }

    public static int safeFindNextPositivePowerOfTwo(int value) {
        if (value <= 0) {
            return 1;
        }
        if (value >= 1073741824) {
            return 1073741824;
        }
        int n = MathUtil.findNextPositivePowerOfTwo((int)value);
        return n;
    }

    public static boolean isOutOfBounds(int index, int length, int capacity) {
        if ((index | length | index + length | capacity - (index + length)) >= 0) return false;
        return true;
    }

    public static int compare(int x, int y) {
        if (x < y) {
            return -1;
        }
        if (x <= y) return 0;
        return 1;
    }

    public static int compare(long x, long y) {
        if (x < y) {
            return -1;
        }
        if (x <= y) return 0;
        return 1;
    }
}

