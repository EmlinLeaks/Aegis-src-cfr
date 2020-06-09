/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive
extends JsonElement {
    private static final Class<?>[] PRIMITIVE_TYPES = new Class[]{Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
    private Object value;

    public JsonPrimitive(Boolean bool) {
        this.setValue((Object)bool);
    }

    public JsonPrimitive(Number number) {
        this.setValue((Object)number);
    }

    public JsonPrimitive(String string) {
        this.setValue((Object)string);
    }

    public JsonPrimitive(Character c) {
        this.setValue((Object)c);
    }

    JsonPrimitive(Object primitive) {
        this.setValue((Object)primitive);
    }

    @Override
    JsonPrimitive deepCopy() {
        return this;
    }

    void setValue(Object primitive) {
        if (primitive instanceof Character) {
            char c = ((Character)primitive).charValue();
            this.value = String.valueOf((char)c);
            return;
        }
        $Gson$Preconditions.checkArgument((boolean)(primitive instanceof Number || JsonPrimitive.isPrimitiveOrString((Object)primitive)));
        this.value = primitive;
    }

    public boolean isBoolean() {
        return this.value instanceof Boolean;
    }

    @Override
    Boolean getAsBooleanWrapper() {
        return (Boolean)this.value;
    }

    @Override
    public boolean getAsBoolean() {
        if (!this.isBoolean()) return Boolean.parseBoolean((String)this.getAsString());
        return this.getAsBooleanWrapper().booleanValue();
    }

    public boolean isNumber() {
        return this.value instanceof Number;
    }

    @Override
    public Number getAsNumber() {
        Number number;
        if (this.value instanceof String) {
            number = new LazilyParsedNumber((String)((String)this.value));
            return number;
        }
        number = (Number)this.value;
        return number;
    }

    public boolean isString() {
        return this.value instanceof String;
    }

    @Override
    public String getAsString() {
        if (this.isNumber()) {
            return this.getAsNumber().toString();
        }
        if (!this.isBoolean()) return (String)this.value;
        return this.getAsBooleanWrapper().toString();
    }

    @Override
    public double getAsDouble() {
        double d;
        if (this.isNumber()) {
            d = this.getAsNumber().doubleValue();
            return d;
        }
        d = Double.parseDouble((String)this.getAsString());
        return d;
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        BigDecimal bigDecimal;
        if (this.value instanceof BigDecimal) {
            bigDecimal = (BigDecimal)this.value;
            return bigDecimal;
        }
        bigDecimal = new BigDecimal((String)this.value.toString());
        return bigDecimal;
    }

    @Override
    public BigInteger getAsBigInteger() {
        BigInteger bigInteger;
        if (this.value instanceof BigInteger) {
            bigInteger = (BigInteger)this.value;
            return bigInteger;
        }
        bigInteger = new BigInteger((String)this.value.toString());
        return bigInteger;
    }

    @Override
    public float getAsFloat() {
        float f;
        if (this.isNumber()) {
            f = this.getAsNumber().floatValue();
            return f;
        }
        f = Float.parseFloat((String)this.getAsString());
        return f;
    }

    @Override
    public long getAsLong() {
        long l;
        if (this.isNumber()) {
            l = this.getAsNumber().longValue();
            return l;
        }
        l = Long.parseLong((String)this.getAsString());
        return l;
    }

    @Override
    public short getAsShort() {
        short s;
        if (this.isNumber()) {
            s = this.getAsNumber().shortValue();
            return s;
        }
        s = Short.parseShort((String)this.getAsString());
        return s;
    }

    @Override
    public int getAsInt() {
        int n;
        if (this.isNumber()) {
            n = this.getAsNumber().intValue();
            return n;
        }
        n = Integer.parseInt((String)this.getAsString());
        return n;
    }

    @Override
    public byte getAsByte() {
        byte by;
        if (this.isNumber()) {
            by = this.getAsNumber().byteValue();
            return by;
        }
        by = Byte.parseByte((String)this.getAsString());
        return by;
    }

    @Override
    public char getAsCharacter() {
        return this.getAsString().charAt((int)0);
    }

    private static boolean isPrimitiveOrString(Object target) {
        if (target instanceof String) {
            return true;
        }
        Class<?> classOfPrimitive = target.getClass();
        Class<?>[] arrclass = PRIMITIVE_TYPES;
        int n = arrclass.length;
        int n2 = 0;
        while (n2 < n) {
            Class<?> standardPrimitive = arrclass[n2];
            if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        if (JsonPrimitive.isIntegral((JsonPrimitive)this)) {
            long value = this.getAsNumber().longValue();
            return (int)(value ^ value >>> 32);
        }
        if (!(this.value instanceof Number)) return this.value.hashCode();
        long value = Double.doubleToLongBits((double)this.getAsNumber().doubleValue());
        return (int)(value ^ value >>> 32);
    }

    public boolean equals(Object obj) {
        double b;
        if (this == obj) {
            return true;
        }
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        JsonPrimitive other = (JsonPrimitive)obj;
        if (this.value == null) {
            if (other.value != null) return false;
            return true;
        }
        if (JsonPrimitive.isIntegral((JsonPrimitive)this) && JsonPrimitive.isIntegral((JsonPrimitive)other)) {
            if (this.getAsNumber().longValue() != other.getAsNumber().longValue()) return false;
            return true;
        }
        if (!(this.value instanceof Number)) return this.value.equals((Object)other.value);
        if (!(other.value instanceof Number)) return this.value.equals((Object)other.value);
        double a = this.getAsNumber().doubleValue();
        if (a == (b = other.getAsNumber().doubleValue())) return true;
        if (!Double.isNaN((double)a)) return false;
        if (!Double.isNaN((double)b)) return false;
        return true;
    }

    private static boolean isIntegral(JsonPrimitive primitive) {
        if (!(primitive.value instanceof Number)) return false;
        Number number = (Number)primitive.value;
        if (number instanceof BigInteger) return true;
        if (number instanceof Long) return true;
        if (number instanceof Integer) return true;
        if (number instanceof Short) return true;
        if (number instanceof Byte) return true;
        return false;
    }
}

