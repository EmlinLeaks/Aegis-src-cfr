/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.DoubleUtils;
import com.google.common.math.Stats;
import com.google.common.primitives.Doubles;
import java.util.Iterator;

@Beta
@GwtIncompatible
public final class StatsAccumulator {
    private long count = 0L;
    private double mean = 0.0;
    private double sumOfSquaresOfDeltas = 0.0;
    private double min = Double.NaN;
    private double max = Double.NaN;

    public void add(double value) {
        if (this.count == 0L) {
            this.count = 1L;
            this.mean = value;
            this.min = value;
            this.max = value;
            if (Doubles.isFinite((double)value)) return;
            this.sumOfSquaresOfDeltas = Double.NaN;
            return;
        }
        ++this.count;
        if (Doubles.isFinite((double)value) && Doubles.isFinite((double)this.mean)) {
            double delta = value - this.mean;
            this.mean += delta / (double)this.count;
            this.sumOfSquaresOfDeltas += delta * (value - this.mean);
        } else {
            this.mean = StatsAccumulator.calculateNewMeanNonFinite((double)this.mean, (double)value);
            this.sumOfSquaresOfDeltas = Double.NaN;
        }
        this.min = Math.min((double)this.min, (double)value);
        this.max = Math.max((double)this.max, (double)value);
    }

    public void addAll(Iterable<? extends Number> values) {
        Iterator<? extends Number> i$ = values.iterator();
        while (i$.hasNext()) {
            Number value = i$.next();
            this.add((double)value.doubleValue());
        }
    }

    public void addAll(Iterator<? extends Number> values) {
        while (values.hasNext()) {
            this.add((double)values.next().doubleValue());
        }
    }

    public void addAll(double ... values) {
        double[] arr$ = values;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            double value = arr$[i$];
            this.add((double)value);
            ++i$;
        }
    }

    public void addAll(int ... values) {
        int[] arr$ = values;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            int value = arr$[i$];
            this.add((double)((double)value));
            ++i$;
        }
    }

    public void addAll(long ... values) {
        long[] arr$ = values;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            long value = arr$[i$];
            this.add((double)((double)value));
            ++i$;
        }
    }

    public void addAll(Stats values) {
        if (values.count() == 0L) {
            return;
        }
        if (this.count == 0L) {
            this.count = values.count();
            this.mean = values.mean();
            this.sumOfSquaresOfDeltas = values.sumOfSquaresOfDeltas();
            this.min = values.min();
            this.max = values.max();
            return;
        }
        this.count += values.count();
        if (Doubles.isFinite((double)this.mean) && Doubles.isFinite((double)values.mean())) {
            double delta = values.mean() - this.mean;
            this.mean += delta * (double)values.count() / (double)this.count;
            this.sumOfSquaresOfDeltas += values.sumOfSquaresOfDeltas() + delta * (values.mean() - this.mean) * (double)values.count();
        } else {
            this.mean = StatsAccumulator.calculateNewMeanNonFinite((double)this.mean, (double)values.mean());
            this.sumOfSquaresOfDeltas = Double.NaN;
        }
        this.min = Math.min((double)this.min, (double)values.min());
        this.max = Math.max((double)this.max, (double)values.max());
    }

    public Stats snapshot() {
        return new Stats((long)this.count, (double)this.mean, (double)this.sumOfSquaresOfDeltas, (double)this.min, (double)this.max);
    }

    public long count() {
        return this.count;
    }

    public double mean() {
        Preconditions.checkState((boolean)(this.count != 0L));
        return this.mean;
    }

    public final double sum() {
        return this.mean * (double)this.count;
    }

    public final double populationVariance() {
        Preconditions.checkState((boolean)(this.count != 0L));
        if (Double.isNaN((double)this.sumOfSquaresOfDeltas)) {
            return Double.NaN;
        }
        if (this.count != 1L) return DoubleUtils.ensureNonNegative((double)this.sumOfSquaresOfDeltas) / (double)this.count;
        return 0.0;
    }

    public final double populationStandardDeviation() {
        return Math.sqrt((double)this.populationVariance());
    }

    public final double sampleVariance() {
        Preconditions.checkState((boolean)(this.count > 1L));
        if (!Double.isNaN((double)this.sumOfSquaresOfDeltas)) return DoubleUtils.ensureNonNegative((double)this.sumOfSquaresOfDeltas) / (double)(this.count - 1L);
        return Double.NaN;
    }

    public final double sampleStandardDeviation() {
        return Math.sqrt((double)this.sampleVariance());
    }

    public double min() {
        Preconditions.checkState((boolean)(this.count != 0L));
        return this.min;
    }

    public double max() {
        Preconditions.checkState((boolean)(this.count != 0L));
        return this.max;
    }

    double sumOfSquaresOfDeltas() {
        return this.sumOfSquaresOfDeltas;
    }

    static double calculateNewMeanNonFinite(double previousMean, double value) {
        if (Doubles.isFinite((double)previousMean)) {
            return value;
        }
        if (Doubles.isFinite((double)value)) return previousMean;
        if (previousMean != value) return Double.NaN;
        return previousMean;
    }
}

