/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.math.LinearTransformation;
import com.google.common.math.Stats;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class PairedStats
implements Serializable {
    private final Stats xStats;
    private final Stats yStats;
    private final double sumOfProductsOfDeltas;
    private static final int BYTES = 88;
    private static final long serialVersionUID = 0L;

    PairedStats(Stats xStats, Stats yStats, double sumOfProductsOfDeltas) {
        this.xStats = xStats;
        this.yStats = yStats;
        this.sumOfProductsOfDeltas = sumOfProductsOfDeltas;
    }

    public long count() {
        return this.xStats.count();
    }

    public Stats xStats() {
        return this.xStats;
    }

    public Stats yStats() {
        return this.yStats;
    }

    public double populationCovariance() {
        Preconditions.checkState((boolean)(this.count() != 0L));
        return this.sumOfProductsOfDeltas / (double)this.count();
    }

    public double sampleCovariance() {
        Preconditions.checkState((boolean)(this.count() > 1L));
        return this.sumOfProductsOfDeltas / (double)(this.count() - 1L);
    }

    public double pearsonsCorrelationCoefficient() {
        Preconditions.checkState((boolean)(this.count() > 1L));
        if (Double.isNaN((double)this.sumOfProductsOfDeltas)) {
            return Double.NaN;
        }
        double xSumOfSquaresOfDeltas = this.xStats().sumOfSquaresOfDeltas();
        double ySumOfSquaresOfDeltas = this.yStats().sumOfSquaresOfDeltas();
        Preconditions.checkState((boolean)(xSumOfSquaresOfDeltas > 0.0));
        Preconditions.checkState((boolean)(ySumOfSquaresOfDeltas > 0.0));
        double productOfSumsOfSquaresOfDeltas = PairedStats.ensurePositive((double)(xSumOfSquaresOfDeltas * ySumOfSquaresOfDeltas));
        return PairedStats.ensureInUnitRange((double)(this.sumOfProductsOfDeltas / Math.sqrt((double)productOfSumsOfSquaresOfDeltas)));
    }

    public LinearTransformation leastSquaresFit() {
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

    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PairedStats other = (PairedStats)obj;
        if (!this.xStats.equals((Object)other.xStats)) return false;
        if (!this.yStats.equals((Object)other.yStats)) return false;
        if (Double.doubleToLongBits((double)this.sumOfProductsOfDeltas) != Double.doubleToLongBits((double)other.sumOfProductsOfDeltas)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.xStats, this.yStats, Double.valueOf((double)this.sumOfProductsOfDeltas)});
    }

    public String toString() {
        if (this.count() <= 0L) return MoreObjects.toStringHelper((Object)this).add((String)"xStats", (Object)this.xStats).add((String)"yStats", (Object)this.yStats).toString();
        return MoreObjects.toStringHelper((Object)this).add((String)"xStats", (Object)this.xStats).add((String)"yStats", (Object)this.yStats).add((String)"populationCovariance", (double)this.populationCovariance()).toString();
    }

    double sumOfProductsOfDeltas() {
        return this.sumOfProductsOfDeltas;
    }

    private static double ensurePositive(double value) {
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

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate((int)88).order((ByteOrder)ByteOrder.LITTLE_ENDIAN);
        this.xStats.writeTo((ByteBuffer)buffer);
        this.yStats.writeTo((ByteBuffer)buffer);
        buffer.putDouble((double)this.sumOfProductsOfDeltas);
        return buffer.array();
    }

    public static PairedStats fromByteArray(byte[] byteArray) {
        Preconditions.checkNotNull(byteArray);
        Preconditions.checkArgument((boolean)(byteArray.length == 88), (String)"Expected PairedStats.BYTES = %s, got %s", (int)88, (int)byteArray.length);
        ByteBuffer buffer = ByteBuffer.wrap((byte[])byteArray).order((ByteOrder)ByteOrder.LITTLE_ENDIAN);
        Stats xStats = Stats.readFrom((ByteBuffer)buffer);
        Stats yStats = Stats.readFrom((ByteBuffer)buffer);
        double sumOfProductsOfDeltas = buffer.getDouble();
        return new PairedStats((Stats)xStats, (Stats)yStats, (double)sumOfProductsOfDeltas);
    }
}

