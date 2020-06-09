/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ComparisonChain;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ComparisonChain {
    private static final ComparisonChain ACTIVE = new ComparisonChain(){

        public ComparisonChain compare(Comparable left, Comparable right) {
            return this.classify((int)left.compareTo(right));
        }

        public <T> ComparisonChain compare(@Nullable T left, @Nullable T right, Comparator<T> comparator) {
            return this.classify((int)comparator.compare(left, right));
        }

        public ComparisonChain compare(int left, int right) {
            return this.classify((int)com.google.common.primitives.Ints.compare((int)left, (int)right));
        }

        public ComparisonChain compare(long left, long right) {
            return this.classify((int)com.google.common.primitives.Longs.compare((long)left, (long)right));
        }

        public ComparisonChain compare(float left, float right) {
            return this.classify((int)java.lang.Float.compare((float)left, (float)right));
        }

        public ComparisonChain compare(double left, double right) {
            return this.classify((int)java.lang.Double.compare((double)left, (double)right));
        }

        public ComparisonChain compareTrueFirst(boolean left, boolean right) {
            return this.classify((int)com.google.common.primitives.Booleans.compare((boolean)right, (boolean)left));
        }

        public ComparisonChain compareFalseFirst(boolean left, boolean right) {
            return this.classify((int)com.google.common.primitives.Booleans.compare((boolean)left, (boolean)right));
        }

        ComparisonChain classify(int result) {
            ComparisonChain comparisonChain;
            if (result < 0) {
                comparisonChain = ComparisonChain.access$100();
                return comparisonChain;
            }
            if (result > 0) {
                comparisonChain = ComparisonChain.access$200();
                return comparisonChain;
            }
            comparisonChain = ComparisonChain.access$300();
            return comparisonChain;
        }

        public int result() {
            return 0;
        }
    };
    private static final ComparisonChain LESS = new InactiveComparisonChain((int)-1);
    private static final ComparisonChain GREATER = new InactiveComparisonChain((int)1);

    private ComparisonChain() {
    }

    public static ComparisonChain start() {
        return ACTIVE;
    }

    public abstract ComparisonChain compare(Comparable<?> var1, Comparable<?> var2);

    public abstract <T> ComparisonChain compare(@Nullable T var1, @Nullable T var2, Comparator<T> var3);

    public abstract ComparisonChain compare(int var1, int var2);

    public abstract ComparisonChain compare(long var1, long var3);

    public abstract ComparisonChain compare(float var1, float var2);

    public abstract ComparisonChain compare(double var1, double var3);

    @Deprecated
    public final ComparisonChain compare(Boolean left, Boolean right) {
        return this.compareFalseFirst((boolean)left.booleanValue(), (boolean)right.booleanValue());
    }

    public abstract ComparisonChain compareTrueFirst(boolean var1, boolean var2);

    public abstract ComparisonChain compareFalseFirst(boolean var1, boolean var2);

    public abstract int result();

    static /* synthetic */ ComparisonChain access$100() {
        return LESS;
    }

    static /* synthetic */ ComparisonChain access$200() {
        return GREATER;
    }

    static /* synthetic */ ComparisonChain access$300() {
        return ACTIVE;
    }
}

