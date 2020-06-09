/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

public class ValueConversionException
extends RuntimeException {
    private static final long serialVersionUID = -1L;

    public ValueConversionException(String message) {
        this((String)message, null);
    }

    public ValueConversionException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

