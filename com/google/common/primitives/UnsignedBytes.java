/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Comparator;

@GwtIncompatible
public final class UnsignedBytes {
    public static final byte MAX_POWER_OF_TWO = -128;
    public static final byte MAX_VALUE = -1;
    private static final int UNSIGNED_MASK = 255;

    private UnsignedBytes() {
    }

    public static int toInt(byte value) {
        return value & 255;
    }

    @CanIgnoreReturnValue
    public static byte checkedCast(long value) {
        if (value >> 8 == 0L) return (byte)((int)value);
        throw new IllegalArgumentException((String)("Out of range: " + value));
    }

    public static byte saturatedCast(long value) {
        if (value > (long)UnsignedBytes.toInt((byte)-1)) {
            return -1;
        }
        if (value >= 0L) return (byte)((int)value);
        return 0;
    }

    public static int compare(byte a, byte b) {
        return UnsignedBytes.toInt((byte)a) - UnsignedBytes.toInt((byte)b);
    }

    public static byte min(byte ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        int min = UnsignedBytes.toInt((byte)array[0]);
        int i = 1;
        while (i < array.length) {
            int next = UnsignedBytes.toInt((byte)array[i]);
            if (next < min) {
                min = next;
            }
            ++i;
        }
        return (byte)min;
    }

    public static byte max(byte ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        int max = UnsignedBytes.toInt((byte)array[0]);
        int i = 1;
        while (i < array.length) {
            int next = UnsignedBytes.toInt((byte)array[i]);
            if (next > max) {
                max = next;
            }
            ++i;
        }
        return (byte)max;
    }

    @Beta
    public static String toString(byte x) {
        return UnsignedBytes.toString((byte)x, (int)10);
    }

    @Beta
    public static String toString(byte x, int radix) {
        Preconditions.checkArgument((boolean)(radix >= 2 && radix <= 36), (String)"radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", (int)radix);
        return Integer.toString((int)UnsignedBytes.toInt((byte)x), (int)radix);
    }

    @Beta
    @CanIgnoreReturnValue
    public static byte parseUnsignedByte(String string) {
        return UnsignedBytes.parseUnsignedByte((String)string, (int)10);
    }

    @Beta
    @CanIgnoreReturnValue
    public static byte parseUnsignedByte(String string, int radix) {
        int parse = Integer.parseInt((String)Preconditions.checkNotNull(string), (int)radix);
        if (parse >> 8 != 0) throw new NumberFormatException((String)("out of range: " + parse));
        return (byte)parse;
    }

    public static String join(String separator, byte ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * (3 + separator.length())));
        builder.append((int)UnsignedBytes.toInt((byte)array[0]));
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((String)UnsignedBytes.toString((byte)array[i]));
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<byte[]> lexicographicalComparator() {
        return LexicographicalComparatorHolder.BEST_COMPARATOR;
    }

    @VisibleForTesting
    static Comparator<byte[]> lexicographicalComparatorJavaImpl() {
        return LexicographicalComparatorHolder.PureJavaComparator.INSTANCE;
    }
}

