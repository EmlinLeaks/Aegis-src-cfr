/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Shorts;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@GwtCompatible(emulated=true)
public final class Shorts {
    public static final int BYTES = 2;
    public static final short MAX_POWER_OF_TWO = 16384;

    private Shorts() {
    }

    public static int hashCode(short value) {
        return value;
    }

    public static short checkedCast(long value) {
        short result = (short)((int)value);
        if ((long)result == value) return result;
        throw new IllegalArgumentException((String)("Out of range: " + value));
    }

    public static short saturatedCast(long value) {
        if (value > 32767L) {
            return 32767;
        }
        if (value >= -32768L) return (short)((int)value);
        return -32768;
    }

    public static int compare(short a, short b) {
        return a - b;
    }

    public static boolean contains(short[] array, short target) {
        short[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            short value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(short[] array, short target) {
        return Shorts.indexOf((short[])array, (short)target, (int)0, (int)array.length);
    }

    private static int indexOf(short[] array, short target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(short[] array, short[] target) {
        Preconditions.checkNotNull(array, (Object)"array");
        Preconditions.checkNotNull(target, (Object)"target");
        if (target.length == 0) {
            return 0;
        }
        int i = 0;
        block0 : while (i < array.length - target.length + 1) {
            int j = 0;
            while (j < target.length) {
                if (array[i + j] != target[j]) {
                    ++i;
                    continue block0;
                }
                ++j;
            }
            return i;
            break;
        }
        return -1;
    }

    public static int lastIndexOf(short[] array, short target) {
        return Shorts.lastIndexOf((short[])array, (short)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(short[] array, short target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static short min(short ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        short min = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] < min) {
                min = array[i];
            }
            ++i;
        }
        return min;
    }

    public static short max(short ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        short max = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] > max) {
                max = array[i];
            }
            ++i;
        }
        return max;
    }

    public static short[] concat(short[] ... arrays) {
        short[] array;
        int length = 0;
        short[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        short[] result = new short[length];
        int pos = 0;
        short[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            short[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    @GwtIncompatible
    public static byte[] toByteArray(short value) {
        return new byte[]{(byte)(value >> 8), (byte)value};
    }

    @GwtIncompatible
    public static short fromByteArray(byte[] bytes) {
        Preconditions.checkArgument((boolean)(bytes.length >= 2), (String)"array too small: %s < %s", (int)bytes.length, (int)2);
        return Shorts.fromBytes((byte)bytes[0], (byte)bytes[1]);
    }

    @GwtIncompatible
    public static short fromBytes(byte b1, byte b2) {
        return (short)(b1 << 8 | b2 & 255);
    }

    @Beta
    public static Converter<String, Short> stringConverter() {
        return ShortConverter.INSTANCE;
    }

    public static short[] ensureCapacity(short[] array, int minLength, int padding) {
        short[] arrs;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrs = Arrays.copyOf((short[])array, (int)(minLength + padding));
            return arrs;
        }
        arrs = array;
        return arrs;
    }

    public static String join(String separator, short ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 6));
        builder.append((int)array[0]);
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((int)array[i]);
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<short[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static short[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof ShortArrayAsList) {
            return ((ShortArrayAsList)collection).toShortArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        short[] array = new short[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).shortValue();
            ++i;
        }
        return array;
    }

    public static List<Short> asList(short ... backingArray) {
        if (backingArray.length != 0) return new ShortArrayAsList((short[])backingArray);
        return Collections.emptyList();
    }

    static /* synthetic */ int access$000(short[] x0, short x1, int x2, int x3) {
        return Shorts.indexOf((short[])x0, (short)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(short[] x0, short x1, int x2, int x3) {
        return Shorts.lastIndexOf((short[])x0, (short)x1, (int)x2, (int)x3);
    }
}

