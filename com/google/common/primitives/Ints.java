/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nullable
 */
package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Ints {
    public static final int BYTES = 4;
    public static final int MAX_POWER_OF_TWO = 1073741824;

    private Ints() {
    }

    public static int hashCode(int value) {
        return value;
    }

    public static int checkedCast(long value) {
        int result = (int)value;
        if ((long)result == value) return result;
        throw new IllegalArgumentException((String)("Out of range: " + value));
    }

    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value >= Integer.MIN_VALUE) return (int)value;
        return Integer.MIN_VALUE;
    }

    public static int compare(int a, int b) {
        if (a < b) {
            return -1;
        }
        if (a <= b) return 0;
        return 1;
    }

    public static boolean contains(int[] array, int target) {
        int[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            int value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(int[] array, int target) {
        return Ints.indexOf((int[])array, (int)target, (int)0, (int)array.length);
    }

    private static int indexOf(int[] array, int target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(int[] array, int[] target) {
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

    public static int lastIndexOf(int[] array, int target) {
        return Ints.lastIndexOf((int[])array, (int)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(int[] array, int target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static int min(int ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        int min = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] < min) {
                min = array[i];
            }
            ++i;
        }
        return min;
    }

    public static int max(int ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        int max = array[0];
        int i = 1;
        while (i < array.length) {
            if (array[i] > max) {
                max = array[i];
            }
            ++i;
        }
        return max;
    }

    public static int[] concat(int[] ... arrays) {
        int[] array;
        int length = 0;
        int[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        int[] result = new int[length];
        int pos = 0;
        int[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            int[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    @GwtIncompatible
    public static byte[] toByteArray(int value) {
        return new byte[]{(byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value};
    }

    @GwtIncompatible
    public static int fromByteArray(byte[] bytes) {
        Preconditions.checkArgument((boolean)(bytes.length >= 4), (String)"array too small: %s < %s", (int)bytes.length, (int)4);
        return Ints.fromBytes((byte)bytes[0], (byte)bytes[1], (byte)bytes[2], (byte)bytes[3]);
    }

    @GwtIncompatible
    public static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 255) << 16 | (b3 & 255) << 8 | b4 & 255;
    }

    @Beta
    public static Converter<String, Integer> stringConverter() {
        return IntConverter.INSTANCE;
    }

    public static int[] ensureCapacity(int[] array, int minLength, int padding) {
        int[] arrn;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrn = Arrays.copyOf((int[])array, (int)(minLength + padding));
            return arrn;
        }
        arrn = array;
        return arrn;
    }

    public static String join(String separator, int ... array) {
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

    public static Comparator<int[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static int[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof IntArrayAsList) {
            return ((IntArrayAsList)collection).toIntArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        int[] array = new int[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).intValue();
            ++i;
        }
        return array;
    }

    public static List<Integer> asList(int ... backingArray) {
        if (backingArray.length != 0) return new IntArrayAsList((int[])backingArray);
        return Collections.emptyList();
    }

    @Nullable
    @CheckForNull
    @Beta
    public static Integer tryParse(String string) {
        return Ints.tryParse((String)string, (int)10);
    }

    @Nullable
    @CheckForNull
    @Beta
    public static Integer tryParse(String string, int radix) {
        Long result = Longs.tryParse((String)string, (int)radix);
        if (result == null) return null;
        if (result.longValue() == (long)result.intValue()) return Integer.valueOf((int)result.intValue());
        return null;
    }

    static /* synthetic */ int access$000(int[] x0, int x1, int x2, int x3) {
        return Ints.indexOf((int[])x0, (int)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(int[] x0, int x1, int x2, int x3) {
        return Ints.lastIndexOf((int[])x0, (int)x1, (int)x2, (int)x3);
    }
}

