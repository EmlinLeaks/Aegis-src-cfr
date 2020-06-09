/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.DoubleUtils;
import com.google.common.math.LinearTransformation;

@Beta
@GwtIncompatible
public abstract class LinearTransformation {
    public static LinearTransformationBuilder mapping(double x1, double y1) {
        Preconditions.checkArgument((boolean)(DoubleUtils.isFinite((double)x1) && DoubleUtils.isFinite((double)y1)));
        return new LinearTransformationBuilder((double)x1, (double)y1, null);
    }

    public static LinearTransformation vertical(double x) {
        Preconditions.checkArgument((boolean)DoubleUtils.isFinite((double)x));
        return new VerticalLinearTransformation((double)x);
    }

    public static LinearTransformation horizontal(double y) {
        Preconditions.checkArgument((boolean)DoubleUtils.isFinite((double)y));
        double slope = 0.0;
        return new RegularLinearTransformation((double)slope, (double)y);
    }

    public static LinearTransformation forNaN() {
        return NaNLinearTransformation.INSTANCE;
    }

    public abstract boolean isVertical();

    public abstract boolean isHorizontal();

    public abstract double slope();

    public abstract double transform(double var1);

    public abstract LinearTransformation inverse();
}

