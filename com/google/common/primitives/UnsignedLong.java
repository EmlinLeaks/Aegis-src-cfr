/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.Nullable
 */
package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedLongs;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.math.BigInteger;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
public final class UnsignedLong
extends Number
implements Comparable<UnsignedLong>,
Serializable {
    private static final long UNSIGNED_MASK = Long.MAX_VALUE;
    public static final UnsignedLong ZERO = new UnsignedLong((long)0L);
    public static final UnsignedLong ONE = new UnsignedLong((long)1L);
    public static final UnsignedLong MAX_VALUE = new UnsignedLong((long)-1L);
    private final long value;

    private UnsignedLong(long value) {
        this.value = value;
    }

    public static UnsignedLong fromLongBits(long bits) {
        return new UnsignedLong((long)bits);
    }

    @CanIgnoreReturnValue
    public static UnsignedLong valueOf(long value) {
        Preconditions.checkArgument((boolean)(value >= 0L), (String)"value (%s) is outside the range for an unsigned long value", (long)value);
        return UnsignedLong.fromLongBits((long)value);
    }

    @CanIgnoreReturnValue
    public static UnsignedLong valueOf(BigInteger value) {
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument((boolean)(value.signum() >= 0 && value.bitLength() <= 64), (String)"value (%s) is outside the range for an unsigned long value", (Object)value);
        return UnsignedLong.fromLongBits((long)value.longValue());
    }

    @CanIgnoreReturnValue
    public static UnsignedLong valueOf(String string) {
        return UnsignedLong.valueOf((String)string, (int)10);
    }

    @CanIgnoreReturnValue
    public static UnsignedLong valueOf(String string, int radix) {
        return UnsignedLong.fromLongBits((long)UnsignedLongs.parseUnsignedLong((String)string, (int)radix));
    }

    public UnsignedLong plus(UnsignedLong val) {
        return UnsignedLong.fromLongBits((long)(this.value + Preconditions.checkNotNull(val).value));
    }

    public UnsignedLong minus(UnsignedLong val) {
        return UnsignedLong.fromLongBits((long)(this.value - Preconditions.checkNotNull(val).value));
    }

    public UnsignedLong times(UnsignedLong val) {
        return UnsignedLong.fromLongBits((long)(this.value * Preconditions.checkNotNull(val).value));
    }

    public UnsignedLong dividedBy(UnsignedLong val) {
        return UnsignedLong.fromLongBits((long)UnsignedLongs.divide((long)this.value, (long)Preconditions.checkNotNull(val).value));
    }

    public UnsignedLong mod(UnsignedLong val) {
        return UnsignedLong.fromLongBits((long)UnsignedLongs.remainder((long)this.value, (long)Preconditions.checkNotNull(val).value));
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        float fValue = (float)(this.value & Long.MAX_VALUE);
        if (this.value >= 0L) return fValue;
        fValue += 9.223372E18f;
        return fValue;
    }

    @Override
    public double doubleValue() {
        double dValue = (double)(this.value & Long.MAX_VALUE);
        if (this.value >= 0L) return dValue;
        dValue += 9.223372036854776E18;
        return dValue;
    }

    public BigInteger bigIntegerValue() {
        BigInteger bigInt = BigInteger.valueOf((long)(this.value & Long.MAX_VALUE));
        if (this.value >= 0L) return bigInt;
        return bigInt.setBit((int)63);
    }

    @Override
    public int compareTo(UnsignedLong o) {
        Preconditions.checkNotNull(o);
        return UnsignedLongs.compare((long)this.value, (long)o.value);
    }

    public int hashCode() {
        return Longs.hashCode((long)this.value);
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UnsignedLong)) return false;
        UnsignedLong other = (UnsignedLong)obj;
        if (this.value != other.value) return false;
        return true;
    }

    public String toString() {
        return UnsignedLongs.toString((long)this.value);
    }

    public String toString(int radix) {
        return UnsignedLongs.toString((long)this.value, (int)radix);
    }
}

