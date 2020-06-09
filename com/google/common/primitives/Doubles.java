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
public final class Doubles {
    public static final int BYTES = 8;
    @GwtIncompatible
    static final Pattern FLOATING_POINT_PATTERN = Doubles.fpPattern();

    private Doubles() {
    }

    public static int hashCode(double value) {
        return Double.valueOf((double)value).hashCode();
    }

    public static int compare(double a, double b) {
        return Double.compare((double)a, (double)b);
    }

    public static boolean isFinite(double value) {
        boolean bl;
        boolean bl2 = Double.NEGATIVE_INFINITY < value;
        if (value < Double.POSITIVE_INFINITY) {
            bl = true;
            return bl2 & bl;
        }
        bl = false;
        return bl2 & bl;
    }

    public static boolean contains(double[] array, double target) {
        double[] arr$ = array;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            double value = arr$[i$];
            if (value == target) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    public static int indexOf(double[] array, double target) {
        return Doubles.indexOf((double[])array, (double)target, (int)0, (int)array.length);
    }

    private static int indexOf(double[] array, double target, int start, int end) {
        int i = start;
        while (i < end) {
            if (array[i] == target) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static int indexOf(double[] array, double[] target) {
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

    public static int lastIndexOf(double[] array, double target) {
        return Doubles.lastIndexOf((double[])array, (double)target, (int)0, (int)array.length);
    }

    private static int lastIndexOf(double[] array, double target, int start, int end) {
        int i = end - 1;
        while (i >= start) {
            if (array[i] == target) {
                return i;
            }
            --i;
        }
        return -1;
    }

    public static double min(double ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        double min = array[0];
        int i = 1;
        while (i < array.length) {
            min = Math.min((double)min, (double)array[i]);
            ++i;
        }
        return min;
    }

    public static double max(double ... array) {
        Preconditions.checkArgument((boolean)(array.length > 0));
        double max = array[0];
        int i = 1;
        while (i < array.length) {
            max = Math.max((double)max, (double)array[i]);
            ++i;
        }
        return max;
    }

    public static double[] concat(double[] ... arrays) {
        double[] array;
        int length = 0;
        double[][] arr$ = arrays;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; length += array.length, ++i$) {
            array = arr$[i$];
        }
        double[] result = new double[length];
        int pos = 0;
        double[][] arr$2 = arrays;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            double[] array2 = arr$2[i$];
            System.arraycopy((Object)array2, (int)0, (Object)result, (int)pos, (int)array2.length);
            pos += array2.length;
            ++i$;
        }
        return result;
    }

    @Beta
    public static Converter<String, Double> stringConverter() {
        return DoubleConverter.INSTANCE;
    }

    public static double[] ensureCapacity(double[] array, int minLength, int padding) {
        double[] arrd;
        Preconditions.checkArgument((boolean)(minLength >= 0), (String)"Invalid minLength: %s", (int)minLength);
        Preconditions.checkArgument((boolean)(padding >= 0), (String)"Invalid padding: %s", (int)padding);
        if (array.length < minLength) {
            arrd = Arrays.copyOf((double[])array, (int)(minLength + padding));
            return arrd;
        }
        arrd = array;
        return arrd;
    }

    public static String join(String separator, double ... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder((int)(array.length * 12));
        builder.append((double)array[0]);
        int i = 1;
        while (i < array.length) {
            builder.append((String)separator).append((double)array[i]);
            ++i;
        }
        return builder.toString();
    }

    public static Comparator<double[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    public static double[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof DoubleArrayAsList) {
            return ((DoubleArrayAsList)collection).toDoubleArray();
        }
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        double[] array = new double[len];
        int i = 0;
        while (i < len) {
            array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).doubleValue();
            ++i;
        }
        return array;
    }

    public static List<Double> asList(double ... backingArray) {
        if (backingArray.length != 0) return new DoubleArrayAsList((double[])backingArray);
        return Collections.emptyList();
    }

    @GwtIncompatible
    private static Pattern fpPattern() {
        String decimal = "(?:\\d++(?:\\.\\d*+)?|\\.\\d++)";
        String completeDec = decimal + "(?:[eE][+-]?\\d++)?[fFdD]?";
        String hex = "(?:\\p{XDigit}++(?:\\.\\p{XDigit}*+)?|\\.\\p{XDigit}++)";
        String completeHex = "0[xX]" + hex + "[pP][+-]?\\d++[fFdD]?";
        String fpPattern = "[+-]?(?:NaN|Infinity|" + completeDec + "|" + completeHex + ")";
        return Pattern.compile((String)fpPattern);
    }

    @Nullable
    @CheckForNull
    @Beta
    @GwtIncompatible
    public static Double tryParse(String string) {
        if (!FLOATING_POINT_PATTERN.matcher((CharSequence)string).matches()) return null;
        try {
            return Double.valueOf((double)Double.parseDouble((String)string));
        }
        catch (NumberFormatException e) {
            // empty catch block
        }
        return null;
    }

    static /* synthetic */ int access$000(double[] x0, double x1, int x2, int x3) {
        return Doubles.indexOf((double[])x0, (double)x1, (int)x2, (int)x3);
    }

    static /* synthetic */ int access$100(double[] x0, double x1, int x2, int x3) {
        return Doubles.lastIndexOf((double[])x0, (double)x1, (int)x2, (int)x3);
    }
}

