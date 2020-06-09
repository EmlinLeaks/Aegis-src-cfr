/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.LinearTransformation;
import com.google.common.math.PairedStats;
import com.google.common.math.Stats;
import com.google.common.math.StatsAccumulator;
import com.google.common.primitives.Doubles;

@Beta
@GwtIncompatible
public final class PairedStatsAccumulator {
    private final StatsAccumulator xStats = new StatsAccumulator();
    private final StatsAccumulator yStats = new StatsAccumulator();
    private double sumOfProductsOfDeltas = 0.0;

    public void add(double x, double y) {
        this.xStats.add((double)x);
        if (Doubles.isFinite((double)x) && Doubles.isFinite((double)y)) {
            if (this.xStats.count() > 1L) {
                this.sumOfProductsOfDeltas += (x - this.xStats.mean()) * (y - this.yStats.mean());
            }
        } else {
            this.sumOfProductsOfDeltas = Double.NaN;
        }
        this.yStats.add((double)y);
    }

    public void addAll(PairedStats values) {
        if (values.count() == 0L) {
            return;
        }
        this.xStats.addAll((Stats)values.xStats());
        this.sumOfProductsOfDeltas = this.yStats.count() == 0L ? values.sumOfProductsOfDeltas() : (this.sumOfProductsOfDeltas += values.sumOfProductsOfDeltas() + (values.xStats().mean() - this.xStats.mean()) * (values.yStats().mean() - this.yStats.mean()) * (double)values.count());
        this.yStats.addAll((Stats)values.yStats());
    }

    public PairedStats snapshot() {
        return new PairedStats((Stats)this.xStats.snapshot(), (Stats)this.yStats.snapshot(), (double)this.sumOfProductsOfDeltas);
    }

    public long count() {
        return this.xStats.count();
    }

    public Stats xStats() {
        return this.xStats.snapshot();
    }

    public Stats yStats() {
        return this.yStats.snapshot();
    }

    public double populationCovariance() {
        Preconditions.checkState((boolean)(this.count() != 0L));
        return this.sumOfProductsOfDeltas / (double)this.count();
    }

    public final double sampleCovariance() {
        Preconditions.checkState((boolean)(this.count() > 1L));
        return this.sumOfProductsOfDeltas / (double)(this.count() - 1L);
    }

    public final double pearsonsCorrelationCoefficient() {
        Preconditions.checkState((boolean)(this.count() > 1L));
        if (Double.isNaN((double)this.sumOfProductsOfDeltas)) {
            return Double.NaN;
        }
        double xSumOfSquaresOfDeltas = this.xStats.sumOfSquaresOfDeltas();
        double ySumOfSquaresOfDeltas = this.yStats.sumOfSquaresOfDeltas();
        Preconditions.checkState((boolean)(xSumOfSquaresOfDeltas > 0.0));
        Preconditions.checkState((boolean)(ySumOfSquaresOfDeltas > 0.0));
        double productOfSumsOfSquaresOfDeltas = this.ensurePositive((double)(xSumOfSquaresOfDeltas * ySumOfSquaresOfDeltas));
        return PairedStatsAccumulator.ensureInUnitRange((double)(this.sumOfProductsOfDeltas / Math.sqrt((double)productOfSumsOfSquaresOfDeltas)));
    }

    public final LinearTransformation leastSquaresFit() {
        Preconditions.checkState((boolean)(this.count() > 1L));
        if (Double.isNaN((double)this.sumOfProductsOfDeltas)) {
            return LinearTransformation.forNaN();
        }
        double xSumOfSquaresOfDeltas = this.xStats.sumOfSquaresOfDeltas();
        if (xSumOfSquaresOfDeltas > 0.0) {
            if (!(this.yStats.sumOfSquaresOfDeltas() > 0.0)) return LinearTransformation.horizontal((double)this.yStats.mean());
            return LinearTransformation.mapping((double)this.xStats.mean(), (double)this.yStats.mean()).withSlope((double)(this.sumOfProductsOfDeltas / xSumOfSquaresOfDeltas));
        }
        Preconditions.checkState((boolean)(this.yStats.sumOfSquaresOfDeltas() > 0.0));
        return LinearTransformation.vertical((double)this.xStats.mean());
    }

    private double ensurePositive(double value) {
        if (!(value > 0.0)) return Double.MIN_VALUE;
        return value;
    }

    private static double ensureInUnitRange(double value) {
        if (value >= 1.0) {
            return 1.0;
        }
        if (!(value <= -1.0)) return value;
        return -1.0;
    }
}

