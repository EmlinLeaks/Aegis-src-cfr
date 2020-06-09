/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInts;
import java.math.BigInteger;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class UnsignedInteger
extends Number
implements Comparable<UnsignedInteger> {
    public static final UnsignedInteger ZERO = UnsignedInteger.fromIntBits((int)0);
    public static final UnsignedInteger ONE = UnsignedInteger.fromIntBits((int)1);
    public static final UnsignedInteger MAX_VALUE = UnsignedInteger.fromIntBits((int)-1);
    private final int value;

    private UnsignedInteger(int value) {
        this.value = value & -1;
    }

    public static UnsignedInteger fromIntBits(int bits) {
        return new UnsignedInteger((int)bits);
    }

    public static UnsignedInteger valueOf(long value) {
        Preconditions.checkArgument((boolean)((value & 0xFFFFFFFFL) == value), (String)"value (%s) is outside the range for an unsigned integer value", (long)value);
        return UnsignedInteger.fromIntBits((int)((int)value));
    }

    public static UnsignedInteger valueOf(BigInteger value) {
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument((boolean)(value.signum() >= 0 && value.bitLength() <= 32), (String)"value (%s) is outside the range for an unsigned integer value", (Object)value);
        return UnsignedInteger.fromIntBits((int)value.intValue());
    }

    public static UnsignedInteger valueOf(String string) {
        return UnsignedInteger.valueOf((String)string, (int)10);
    }

    public static UnsignedInteger valueOf(String string, int radix) {
        return UnsignedInteger.fromIntBits((int)UnsignedInts.parseUnsignedInt((String)string, (int)radix));
    }

    public UnsignedInteger plus(UnsignedInteger val) {
        return UnsignedInteger.fromIntBits((int)(this.value + Preconditions.checkNotNull(val).value));
    }

    public UnsignedInteger minus(UnsignedInteger val) {
        return UnsignedInteger.fromIntBits((int)(this.value - Preconditions.checkNotNull(val).value));
    }

    @GwtIncompatible
    public UnsignedInteger times(UnsignedInteger val) {
        return UnsignedInteger.fromIntBits((int)(this.value * Preconditions.checkNotNull(val).value));
    }

    public UnsignedInteger dividedBy(UnsignedInteger val) {
        return UnsignedInteger.fromIntBits((int)UnsignedInts.divide((int)this.value, (int)Preconditions.checkNotNull(val).value));
    }

    public UnsignedInteger mod(UnsignedInteger val) {
        return UnsignedInteger.fromIntBits((int)UnsignedInts.remainder((int)this.value, (int)Preconditions.checkNotNull(val).value));
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return UnsignedInts.toLong((int)this.value);
    }

    @Override
    public float floatValue() {
        return (float)this.longValue();
    }

    @Override
    public double doubleValue() {
        return (double)this.longValue();
    }

    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf((long)this.longValue());
    }

    @Override
    public int compareTo(UnsignedInteger other) {
        Preconditions.checkNotNull(other);
        return UnsignedInts.compare((int)this.value, (int)other.value);
    }

    public int hashCode() {
        return this.value;
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UnsignedInteger)) return false;
        UnsignedInteger other = (UnsignedInteger)obj;
        if (this.value != other.value) return false;
        return true;
    }

    public String toString() {
        return this.toString((int)10);
    }

    public String toString(int radix) {
        return UnsignedInts.toString((int)this.value, (int)radix);
    }
}

