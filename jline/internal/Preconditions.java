/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Preconditions {
    public static <T> T checkNotNull(T reference) {
        if (reference != null) return (T)reference;
        throw new NullPointerException();
    }
}

