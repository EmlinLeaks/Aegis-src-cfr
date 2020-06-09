/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.util;

public final class Pow2 {
    public static final int MAX_POW2 = 1073741824;

    public static int roundToPowerOfTwo(int value) {
        if (value > 1073741824) {
            throw new IllegalArgumentException((String)("There is no larger power of 2 int for value:" + value + " since it exceeds 2^31."));
        }
        if (value >= 0) return 1 << 32 - Integer.numberOfLeadingZeros((int)(value - 1));
        throw new IllegalArgumentException((String)("Given value:" + value + ". Expecting value >= 0."));
    }

    public static boolean isPowerOfTwo(int value) {
        if ((value & value - 1) != 0) return false;
        return true;
    }

    public static long align(long value, int alignment) {
        if (Pow2.isPowerOfTwo((int)alignment)) return value + (long)(alignment - 1) & (long)(~(alignment - 1));
        throw new IllegalArgumentException((String)("alignment must be a power of 2:" + alignment));
    }
}

