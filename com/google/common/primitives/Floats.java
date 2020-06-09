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
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Floats {
    public static final int BYTES = 4;

    private Floats() {
    }

    public static int hashCode(float value) {
        return Float.valueOf((float)value).hashCode();
    }

    public static int compare(float a, float b) {
        return Float.compare((float)a, (float)b);
    }

    public static boolean isFinite(float value) {
        boolean bl;
        boolean bl2 = Float.NEGATIVE_INFINITY < value;
        if (value < Float.POSITIVE_INFINITY) {
            bl = true;
            return bl2 & bl;
        }
        bl = false;
        return bl2 & bl;
    }

    public static boolean contains(float[] array, float target) {
        float[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            float value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(float[] array, float target) {
        return Floats.indexOf((float[])array, (float)target, (int)0, (int)array.length);
    }

    private static int indexOf(float[] array, float target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(float[] array, float[] target) {
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

    public static int lastIndexOf(float[] array, float target) {
        return Floats.lastIndexOf((float[])array, (float)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(float[] array, float target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static float min(float ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        float min = array[0];
        int i = 1;
        while (i < array.length) {
            min = Math.min((float)min, (float)array[i]);
            ++i;
        }
        return min;
    }

    public static float max(float ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        float max = array[0];
        int i = 1;
        while (i < array.length) {
            max = Math.max((float)max, (float)array[i]);
            ++i;
        }
        return max;
    }

    public static float[] concat(float[] ... arrays) {
        float[] array;
        int length = 0;
        float[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        float[] result = new float[length];
        int pos = 0;
        float[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            float[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    @Beta
    public static Converter<String, Float> stringConverter() {
        return FloatConverter.INSTANCE;
    }

    public static float[] ensureCapacity(float[] array, int minLength, int padding) {
        float[] arrf;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrf = Arrays.copyOf((float[])array, (int)(minLength + padding));
            return arrf;
        }
        arrf = array;
        return arrf;
    }

    public static String join(String separator, float ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 12));
        builder.append((float)array[0]);
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((float)array[i]);
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<float[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static float[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof FloatArrayAsList) {
            return ((FloatArrayAsList)collection).toFloatArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        float[] array = new float[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).floatValue();
            ++i;
        }
        return array;
    }

    public static List<Float> asList(float ... backingArray) {
        if (backingArray.length != 0) return new FloatArrayAsList((float[])backingArray);
        return Collections.emptyList();
    }

    @Nullable
    @CheckForNull
    @Beta
    @GwtIncompatible
    public static Float tryParse(String string) {
        if (!Doubles.FLOATING_POINT_PATTERN.matcher((CharSequence)string).matches()) return null;
        try {
            return Float.valueOf((float)Float.parseFloat((String)string));
        }
        catch (NumberFormatException e) {
            // empty catch block
        }
        return null;
    }

    static /* synthetic */ int access$000(float[] x0, float x1, int x2, int x3) {
        return Floats.indexOf((float[])x0, (float)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(float[] x0, float x1, int x2, int x3) {
        return Floats.lastIndexOf((float[])x0, (float)x1, (int)x2, (int)x3);
    }
}

