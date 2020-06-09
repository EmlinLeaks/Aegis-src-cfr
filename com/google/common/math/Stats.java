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
import com.google.common.math.DoubleUtils;
import com.google.common.math.StatsAccumulator;
import com.google.common.primitives.Doubles;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class Stats
implements Serializable {
    private final long count;
    private final double mean;
    private final double sumOfSquaresOfDeltas;
    private final double min;
    private final double max;
    static final int BYTES = 40;
    private static final long serialVersionUID = 0L;

    Stats(long count, double mean, double sumOfSquaresOfDeltas, double min, double max) {
        this.count = count;
        this.mean = mean;
        this.sumOfSquaresOfDeltas = sumOfSquaresOfDeltas;
        this.min = min;
        this.max = max;
    }

    public static Stats of(Iterable<? extends Number> values) {
        StatsAccumulator accumulator = new StatsAccumulator();
        accumulator.addAll(values);
        return accumulator.snapshot();
    }

    public static Stats of(Iterator<? extends Number> values) {
        StatsAccumulator accumulator = new StatsAccumulator();
        accumulator.addAll(values);
        return accumulator.snapshot();
    }

    public static Stats of(double ... values) {
        StatsAccumulator acummulator = new StatsAccumulator();
        acummulator.addAll((double[])values);
        return acummulator.snapshot();
    }

    public static Stats of(int ... values) {
        StatsAccumulator acummulator = new StatsAccumulator();
        acummulator.addAll((int[])values);
        return acummulator.snapshot();
    }

    public static Stats of(long ... values) {
        StatsAccumulator acummulator = new StatsAccumulator();
        acummulator.addAll((long[])values);
        return acummulator.snapshot();
    }

    public long count() {
        return this.count;
    }

    public double mean() {
        Preconditions.checkState((boolean)(this.count != 0L));
        return this.mean;
    }

    public double sum() {
        return this.mean * (double)this.count;
    }

    public double populationVariance() {
        Preconditions.checkState((boolean)(this.count > 0L));
        if (Double.isNaN((double)this.sumOfSquaresOfDeltas)) {
            return Double.NaN;
        }
        if (this.count != 1L) return DoubleUtils.ensureNonNegative((double)this.sumOfSquaresOfDeltas) / (double)this.count();
        return 0.0;
    }

    public double populationStandardDeviation() {
        return Math.sqrt((double)this.populationVariance());
    }

    public double sampleVariance() {
        Preconditions.checkState((boolean)(this.count > 1L));
        if (!Double.isNaN((double)this.sumOfSquaresOfDeltas)) return DoubleUtils.ensureNonNegative((double)this.sumOfSquaresOfDeltas) / (double)(this.count - 1L);
        return Double.NaN;
    }

    public double sampleStandardDeviation() {
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

    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Stats other = (Stats)obj;
        if (this.count != other.count) return false;
        if (Double.doubleToLongBits((double)this.mean) != Double.doubleToLongBits((double)other.mean)) return false;
        if (Double.doubleToLongBits((double)this.sumOfSquaresOfDeltas) != Double.doubleToLongBits((double)other.sumOfSquaresOfDeltas)) return false;
        if (Double.doubleToLongBits((double)this.min) != Double.doubleToLongBits((double)other.min)) return false;
        if (Double.doubleToLongBits((double)this.max) != Double.doubleToLongBits((double)other.max)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{Long.valueOf((long)this.count), Double.valueOf((double)this.mean), Double.valueOf((double)this.sumOfSquaresOfDeltas), Double.valueOf((double)this.min), Double.valueOf((double)this.max)});
    }

    public String toString() {
        if (this.count() <= 0L) return MoreObjects.toStringHelper((Object)this).add((String)"count", (long)this.count).toString();
        return MoreObjects.toStringHelper((Object)this).add((String)"count", (long)this.count).add((String)"mean", (double)this.mean).add((String)"populationStandardDeviation", (double)this.populationStandardDeviation()).add((String)"min", (double)this.min).add((String)"max", (double)this.max).toString();
    }

    double sumOfSquaresOfDeltas() {
        return this.sumOfSquaresOfDeltas;
    }

    public static double meanOf(Iterable<? extends Number> values) {
        return Stats.meanOf(values.iterator());
    }

    public static double meanOf(Iterator<? extends Number> values) {
        Preconditions.checkArgument((boolean)values.hasNext());
        long count = 1L;
        double mean = values.next().doubleValue();
        while (values.hasNext()) {
            double value = values.next().doubleValue();
            ++count;
            if (Doubles.isFinite((double)value) && Doubles.isFinite((double)mean)) {
                mean += (value - mean) / (double)count;
                continue;
            }
            mean = StatsAccumulator.calculateNewMeanNonFinite((double)mean, (double)value);
        }
        return mean;
    }

    public static double meanOf(double ... values) {
        Preconditions.checkArgument((boolean)(values.length > 0));
        double mean = values[0];
        int index = 1;
        while (index < values.length) {
            double value = values[index];
            mean = Doubles.isFinite((double)value) && Doubles.isFinite((double)mean) ? (mean += (value - mean) / (double)(index + 1)) : StatsAccumulator.calculateNewMeanNonFinite((double)mean, (double)value);
            ++index;
        }
        return mean;
    }

    public static double meanOf(int ... values) {
        Preconditions.checkArgument((boolean)(values.length > 0));
        double mean = (double)values[0];
        int index = 1;
        while (index < values.length) {
            double value = (double)values[index];
            mean = Doubles.isFinite((double)value) && Doubles.isFinite((double)mean) ? (mean += (value - mean) / (double)(index + 1)) : StatsAccumulator.calculateNewMeanNonFinite((double)mean, (double)value);
            ++index;
        }
        return mean;
    }

    public static double meanOf(long ... values) {
        Preconditions.checkArgument((boolean)(values.length > 0));
        double mean = (double)values[0];
        int index = 1;
        while (index < values.length) {
            double value = (double)values[index];
            mean = Doubles.isFinite((double)value) && Doubles.isFinite((double)mean) ? (mean += (value - mean) / (double)(index + 1)) : StatsAccumulator.calculateNewMeanNonFinite((double)mean, (double)value);
            ++index;
        }
        return mean;
    }

    public byte[] toByteArray() {
        ByteBuffer buff = ByteBuffer.allocate((int)40).order((ByteOrder)ByteOrder.LITTLE_ENDIAN);
        this.writeTo((ByteBuffer)buff);
        return buff.array();
    }

    void writeTo(ByteBuffer buffer) {
        Preconditions.checkNotNull(buffer);
        Preconditions.checkArgument((boolean)(buffer.remaining() >= 40), (String)"Expected at least Stats.BYTES = %s remaining , got %s", (int)40, (int)buffer.remaining());
        buffer.putLong((long)this.count).putDouble((double)this.mean).putDouble((double)this.sumOfSquaresOfDeltas).putDouble((double)this.min).putDouble((double)this.max);
    }

    public static Stats fromByteArray(byte[] byteArray) {
        Preconditions.checkNotNull(byteArray);
        Preconditions.checkArgument((boolean)(byteArray.length == 40), (String)"Expected Stats.BYTES = %s remaining , got %s", (int)40, (int)byteArray.length);
        return Stats.readFrom((ByteBuffer)ByteBuffer.wrap((byte[])byteArray).order((ByteOrder)ByteOrder.LITTLE_ENDIAN));
    }

    static Stats readFrom(ByteBuffer buffer) {
        Preconditions.checkNotNull(buffer);
        Preconditions.checkArgument((boolean)(buffer.remaining() >= 40), (String)"Expected at least Stats.BYTES = %s remaining , got %s", (int)40, (int)buffer.remaining());
        return new Stats((long)buffer.getLong(), (double)buffer.getDouble(), (double)buffer.getDouble(), (double)buffer.getDouble(), (double)buffer.getDouble());
    }
}

