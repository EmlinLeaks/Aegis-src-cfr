/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl;

public final class HashFunctions {
    public static int hash(double value) {
        assert (!Double.isNaN((double)value)) : "Values of NaN are not supported.";
        long bits = Double.doubleToLongBits((double)value);
        return (int)(bits ^ bits >>> 32);
    }

    public static int hash(float value) {
        if ($assertionsDisabled) return Float.floatToIntBits((float)(value * 6.6360896E8f));
        if (!Float.isNaN((float)value)) return Float.floatToIntBits((float)(value * 6.6360896E8f));
        throw new AssertionError((Object)"Values of NaN are not supported.");
    }

    public static int hash(int value) {
        return value;
    }

    public static int hash(long value) {
        return (int)(value ^ value >>> 32);
    }

    public static int hash(Object object) {
        if (object == null) {
            return 0;
        }
        int n = object.hashCode();
        return n;
    }
}

