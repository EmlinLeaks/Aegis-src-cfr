/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.impl;

import java.io.PrintStream;

public class Constants {
    private static final boolean VERBOSE;
    public static final int DEFAULT_CAPACITY = 10;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    public static final byte DEFAULT_BYTE_NO_ENTRY_VALUE;
    public static final short DEFAULT_SHORT_NO_ENTRY_VALUE;
    public static final char DEFAULT_CHAR_NO_ENTRY_VALUE;
    public static final int DEFAULT_INT_NO_ENTRY_VALUE;
    public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
    public static final float DEFAULT_FLOAT_NO_ENTRY_VALUE;
    public static final double DEFAULT_DOUBLE_NO_ENTRY_VALUE;

    static {
        boolean verbose = false;
        try {
            verbose = System.getProperty((String)"gnu.trove.verbose", null) != null;
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        VERBOSE = verbose;
        String property = "0";
        try {
            property = System.getProperty((String)"gnu.trove.no_entry.byte", (String)property);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        int value = "MAX_VALUE".equalsIgnoreCase((String)property) ? 127 : ("MIN_VALUE".equalsIgnoreCase((String)property) ? -128 : (int)Byte.valueOf((String)property).byteValue());
        if (value > 127) {
            value = 127;
        } else if (value < -128) {
            value = -128;
        }
        DEFAULT_BYTE_NO_ENTRY_VALUE = (byte)value;
        if (VERBOSE) {
            System.out.println((String)("DEFAULT_BYTE_NO_ENTRY_VALUE: " + DEFAULT_BYTE_NO_ENTRY_VALUE));
        }
        property = "0";
        try {
            property = System.getProperty((String)"gnu.trove.no_entry.short", (String)property);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        value = "MAX_VALUE".equalsIgnoreCase((String)property) ? 32767 : ("MIN_VALUE".equalsIgnoreCase((String)property) ? -32768 : (int)Short.valueOf((String)property).shortValue());
        if (value > 32767) {
            value = 32767;
        } else if (value < -32768) {
            value = -32768;
        }
        DEFAULT_SHORT_NO_ENTRY_VALUE = (short)value;
        if (VERBOSE) {
            System.out.println((String)("DEFAULT_SHORT_NO_ENTRY_VALUE: " + DEFAULT_SHORT_NO_ENTRY_VALUE));
        }
        property = "\u0000";
        try {
            property = System.getProperty((String)"gnu.trove.no_entry.char", (String)property);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        value = "MAX_VALUE".equalsIgnoreCase((String)property) ? 65535 : ("MIN_VALUE".equalsIgnoreCase((String)property) ? 0 : property.toCharArray()[0]);
        if (value > 65535) {
            value = 65535;
        } else if (value < 0) {
            value = 0;
        }
        DEFAULT_CHAR_NO_ENTRY_VALUE = (char)value;
        if (VERBOSE) {
            System.out.println((String)("DEFAULT_CHAR_NO_ENTRY_VALUE: " + Integer.valueOf((int)value)));
        }
        property = "0";
        try {
            property = System.getProperty((String)"gnu.trove.no_entry.int", (String)property);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        value = "MAX_VALUE".equalsIgnoreCase((String)property) ? Integer.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase((String)property) ? Integer.MIN_VALUE : Integer.valueOf((String)property).intValue());
        DEFAULT_INT_NO_ENTRY_VALUE = value;
        if (VERBOSE) {
            System.out.println((String)("DEFAULT_INT_NO_ENTRY_VALUE: " + DEFAULT_INT_NO_ENTRY_VALUE));
        }
        String property22 = "0";
        try {
            property22 = System.getProperty((String)"gnu.trove.no_entry.long", (String)property22);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        long value2 = "MAX_VALUE".equalsIgnoreCase((String)property22) ? Long.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase((String)property22) ? Long.MIN_VALUE : Long.valueOf((String)property22).longValue());
        DEFAULT_LONG_NO_ENTRY_VALUE = value2;
        if (VERBOSE) {
            System.out.println((String)("DEFAULT_LONG_NO_ENTRY_VALUE: " + DEFAULT_LONG_NO_ENTRY_VALUE));
        }
        property = "0";
        try {
            property = System.getProperty((String)"gnu.trove.no_entry.float", (String)property);
        }
        catch (SecurityException property22) {
            // empty catch block
        }
        float value3 = "MAX_VALUE".equalsIgnoreCase((String)property) ? Float.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase((String)property) ? Float.MIN_VALUE : ("MIN_NORMAL".equalsIgnoreCase((String)property) ? Float.MIN_NORMAL : ("NEGATIVE_INFINITY".equalsIgnoreCase((String)property) ? Float.NEGATIVE_INFINITY : ("POSITIVE_INFINITY".equalsIgnoreCase((String)property) ? Float.POSITIVE_INFINITY : Float.valueOf((String)property).floatValue()))));
        DEFAULT_FLOAT_NO_ENTRY_VALUE = value3;
        if (VERBOSE) {
            System.out.println((String)("DEFAULT_FLOAT_NO_ENTRY_VALUE: " + DEFAULT_FLOAT_NO_ENTRY_VALUE));
        }
        property22 = "0";
        try {
            property22 = System.getProperty((String)"gnu.trove.no_entry.double", (String)property22);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        double value4 = "MAX_VALUE".equalsIgnoreCase((String)property22) ? Double.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase((String)property22) ? Double.MIN_VALUE : ("MIN_NORMAL".equalsIgnoreCase((String)property22) ? Double.MIN_NORMAL : ("NEGATIVE_INFINITY".equalsIgnoreCase((String)property22) ? Double.NEGATIVE_INFINITY : ("POSITIVE_INFINITY".equalsIgnoreCase((String)property22) ? Double.POSITIVE_INFINITY : Double.valueOf((String)property22).doubleValue()))));
        DEFAULT_DOUBLE_NO_ENTRY_VALUE = value4;
        if (!VERBOSE) return;
        System.out.println((String)("DEFAULT_DOUBLE_NO_ENTRY_VALUE: " + DEFAULT_DOUBLE_NO_ENTRY_VALUE));
    }
}

