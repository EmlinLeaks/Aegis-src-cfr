/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.math.BigDecimal;

public final class LazilyParsedNumber
extends Number {
    private final String value;

    public LazilyParsedNumber(String value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        try {
            return Integer.parseInt((String)this.value);
        }
        catch (NumberFormatException e) {
            try {
                return (int)Long.parseLong((String)this.value);
            }
            catch (NumberFormatException nfe) {
                return new BigDecimal((String)this.value).intValue();
            }
        }
    }

    @Override
    public long longValue() {
        try {
            return Long.parseLong((String)this.value);
        }
        catch (NumberFormatException e) {
            return new BigDecimal((String)this.value).longValue();
        }
    }

    @Override
    public float floatValue() {
        return Float.parseFloat((String)this.value);
    }

    @Override
    public double doubleValue() {
        return Double.parseDouble((String)this.value);
    }

    public String toString() {
        return this.value;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new BigDecimal((String)this.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LazilyParsedNumber)) return false;
        LazilyParsedNumber other = (LazilyParsedNumber)obj;
        if (this.value == other.value) return true;
        if (this.value.equals((Object)other.value)) return true;
        return false;
    }
}

