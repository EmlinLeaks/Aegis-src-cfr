/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.math.Quantiles;

@Beta
@GwtIncompatible
public final class Quantiles {
    public static ScaleAndIndex median() {
        return Quantiles.scale((int)2).index((int)1);
    }

    public static Scale quartiles() {
        return Quantiles.scale((int)4);
    }

    public static Scale percentiles() {
        return Quantiles.scale((int)100);
    }

    public static Scale scale(int scale) {
        return new Scale((int)scale, null);
    }

    private static boolean containsNaN(double ... dataset) {
        double[] arr$ = dataset;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            double value = arr$[i$];
            if (Double.isNaN((double)value)) {
                return true;
            }
            ++i$;
        }
        return false;
    }

    private static double interpolate(double lower, double upper, double remainder, double scale) {
        if (lower == Double.NEGATIVE_INFINITY) {
            if (upper != Double.POSITIVE_INFINITY) return Double.NEGATIVE_INFINITY;
            return Double.NaN;
        }
        if (upper != Double.POSITIVE_INFINITY) return lower + (upper - lower) * remainder / scale;
        return Double.POSITIVE_INFINITY;
    }

    private static void checkIndex(int index, int scale) {
        if (index < 0) throw new IllegalArgumentException((String)("Quantile indexes must be between 0 and the scale, which is " + scale));
        if (index <= scale) return;
        throw new IllegalArgumentException((String)("Quantile indexes must be between 0 and the scale, which is " + scale));
    }

    private static double[] longsToDoubles(long[] longs) {
        int len = longs.length;
        double[] doubles = new double[len];
        int i = 0;
        while (i < len) {
            doubles[i] = (double)longs[i];
            ++i;
        }
        return doubles;
    }

    private static double[] intsToDoubles(int[] ints) {
        int len = ints.length;
        double[] doubles = new double[len];
        int i = 0;
        while (i < len) {
            doubles[i] = (double)ints[i];
            ++i;
        }
        return doubles;
    }

    private static void selectInPlace(int required, double[] array, int from, int to) {
        if (required == from) {
            int min = from;
            int index = from + 1;
            do {
                if (index > to) {
                    if (min == from) return;
                    Quantiles.swap((double[])array, (int)min, (int)from);
                    return;
                }
                if (array[min] > array[index]) {
                    min = index;
                }
                ++index;
            } while (true);
        }
        while (to > from) {
            int partitionPoint = Quantiles.partition((double[])array, (int)from, (int)to);
            if (partitionPoint >= required) {
                to = partitionPoint - 1;
            }
            if (partitionPoint > required) continue;
            from = partitionPoint + 1;
        }
    }

    private static int partition(double[] array, int from, int to) {
        Quantiles.movePivotToStartOfSlice((double[])array, (int)from, (int)to);
        double pivot = array[from];
        int partitionPoint = to;
        int i = to;
        do {
            if (i <= from) {
                Quantiles.swap((double[])array, (int)from, (int)partitionPoint);
                return partitionPoint;
            }
            if (array[i] > pivot) {
                Quantiles.swap((double[])array, (int)partitionPoint, (int)i);
                --partitionPoint;
            }
            --i;
        } while (true);
    }

    private static void movePivotToStartOfSlice(double[] array, int from, int to) {
        boolean toLessThanFrom;
        int mid = from + to >>> 1;
        boolean toLessThanMid = array[to] < array[mid];
        boolean midLessThanFrom = array[mid] < array[from];
        boolean bl = toLessThanFrom = array[to] < array[from];
        if (toLessThanMid == midLessThanFrom) {
            Quantiles.swap((double[])array, (int)mid, (int)from);
            return;
        }
        if (toLessThanMid == toLessThanFrom) return;
        Quantiles.swap((double[])array, (int)from, (int)to);
    }

    private static void selectAllInPlace(int[] allRequired, int requiredFrom, int requiredTo, double[] array, int from, int to) {
        int requiredBelow;
        int requiredAbove;
        int requiredChosen = Quantiles.chooseNextSelection((int[])allRequired, (int)requiredFrom, (int)requiredTo, (int)from, (int)to);
        int required = allRequired[requiredChosen];
        Quantiles.selectInPlace((int)required, (double[])array, (int)from, (int)to);
        for (requiredBelow = requiredChosen - 1; requiredBelow >= requiredFrom && allRequired[requiredBelow] == required; --requiredBelow) {
        }
        if (requiredBelow >= requiredFrom) {
            Quantiles.selectAllInPlace((int[])allRequired, (int)requiredFrom, (int)requiredBelow, (double[])array, (int)from, (int)(required - 1));
        }
        for (requiredAbove = requiredChosen + 1; requiredAbove <= requiredTo && allRequired[requiredAbove] == required; ++requiredAbove) {
        }
        if (requiredAbove > requiredTo) return;
        Quantiles.selectAllInPlace((int[])allRequired, (int)requiredAbove, (int)requiredTo, (double[])array, (int)(required + 1), (int)to);
    }

    private static int chooseNextSelection(int[] allRequired, int requiredFrom, int requiredTo, int from, int to) {
        if (requiredFrom == requiredTo) {
            return requiredFrom;
        }
        int centerFloor = from + to >>> 1;
        int low = requiredFrom;
        int high = requiredTo;
        do {
            if (high <= low + 1) {
                if (from + to - allRequired[low] - allRequired[high] <= 0) return low;
                return high;
            }
            int mid = low + high >>> 1;
            if (allRequired[mid] > centerFloor) {
                high = mid;
                continue;
            }
            if (allRequired[mid] >= centerFloor) return mid;
            low = mid;
        } while (true);
    }

    private static void swap(double[] array, int i, int j) {
        double temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static /* synthetic */ void access$300(int x0, int x1) {
        Quantiles.checkIndex((int)x0, (int)x1);
    }

    static /* synthetic */ double[] access$400(long[] x0) {
        return Quantiles.longsToDoubles((long[])x0);
    }

    static /* synthetic */ double[] access$500(int[] x0) {
        return Quantiles.intsToDoubles((int[])x0);
    }

    static /* synthetic */ boolean access$600(double[] x0) {
        return Quantiles.containsNaN((double[])x0);
    }

    static /* synthetic */ void access$700(int x0, double[] x1, int x2, int x3) {
        Quantiles.selectInPlace((int)x0, (double[])x1, (int)x2, (int)x3);
    }

    static /* synthetic */ double access$800(double x0, double x1, double x2, double x3) {
        return Quantiles.interpolate((double)x0, (double)x1, (double)x2, (double)x3);
    }

    static /* synthetic */ void access$900(int[] x0, int x1, int x2, double[] x3, int x4, int x5) {
        Quantiles.selectAllInPlace((int[])x0, (int)x1, (int)x2, (double[])x3, (int)x4, (int)x5);
    }
}

