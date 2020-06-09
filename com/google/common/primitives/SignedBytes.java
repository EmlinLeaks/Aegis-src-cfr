/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.SignedBytes;
import java.util.Comparator;

@GwtCompatible
public final class SignedBytes {
    public static final byte MAX_POWER_OF_TWO = 64;

    private SignedBytes() {
    }

    public static byte checkedCast(long value) {
        byte result = (byte)((int)value);
        if ((long)result == value) return result;
        throw new IllegalArgumentException((String)("Out of range: " + value));
    }

    public static byte saturatedCast(long value) {
        if (value > 127L) {
            return 127;
        }
        if (value >= -128L) return (byte)((int)value);
        return -128;
    }

    public static int compare(byte a, byte b) {
        return a - b;
    }

    public static byte min(byte ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        byte min = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] < min) {
                min = array[i];
            }
            ++i;
        }
        return min;
    }

    public static byte max(byte ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        byte max = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] > max) {
                max = array[i];
            }
            ++i;
        }
        return max;
    }

    public static String join(String separator, byte ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 5));
        builder.append((int)array[0]);
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((int)array[i]);
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<byte[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }
}

