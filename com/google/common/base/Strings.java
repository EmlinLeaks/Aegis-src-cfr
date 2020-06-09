/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@GwtCompatible
public final class Strings {
    private Strings() {
    }

    public static String nullToEmpty(@Nullable String string) {
        if (string == null) {
            return "";
        }
        String string2 = string;
        return string2;
    }

    @Nullable
    public static String emptyToNull(@Nullable String string) {
        if (Strings.isNullOrEmpty((String)string)) {
            return null;
        }
        String string2 = string;
        return string2;
    }

    public static boolean isNullOrEmpty(@Nullable String string) {
        return Platform.stringIsNullOrEmpty((String)string);
    }

    public static String padStart(String string, int minLength, char padChar) {
        Preconditions.checkNotNull(string);
        if (string.length() >= minLength) {
            return string;
        }
        StringBuilder sb = new StringBuilder((int)minLength);
        int i = string.length();
        do {
            if (i >= minLength) {
                sb.append((String)string);
                return sb.toString();
            }
            sb.append((char)padChar);
            ++i;
        } while (true);
    }

    public static String padEnd(String string, int minLength, char padChar) {
        Preconditions.checkNotNull(string);
        if (string.length() >= minLength) {
            return string;
        }
        StringBuilder sb = new StringBuilder((int)minLength);
        sb.append((String)string);
        int i = string.length();
        while (i < minLength) {
            sb.append((char)padChar);
            ++i;
        }
        return sb.toString();
    }

    public static String repeat(String string, int count) {
        Preconditions.checkNotNull(string);
        if (count <= 1) {
            Preconditions.checkArgument((boolean)(count >= 0), (String)"invalid count: %s", (int)count);
            if (count == 0) {
                return "";
            }
            String string2 = string;
            return string2;
        }
        int len = string.length();
        long longSize = (long)len * (long)count;
        int size = (int)longSize;
        if ((long)size != longSize) {
            throw new ArrayIndexOutOfBoundsException((String)("Required array size too large: " + longSize));
        }
        char[] array = new char[size];
        string.getChars((int)0, (int)len, (char[])array, (int)0);
        int n = len;
        do {
            if (n >= size - n) {
                System.arraycopy((Object)array, (int)0, (Object)array, (int)n, (int)(size - n));
                return new String((char[])array);
            }
            System.arraycopy((Object)array, (int)0, (Object)array, (int)n, (int)n);
            n <<= 1;
        } while (true);
    }

    public static String commonPrefix(CharSequence a, CharSequence b) {
        int p;
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        int maxPrefixLength = Math.min((int)a.length(), (int)b.length());
        for (p = 0; p < maxPrefixLength && a.charAt((int)p) == b.charAt((int)p); ++p) {
        }
        if (!Strings.validSurrogatePairAt((CharSequence)a, (int)(p - 1))) {
            if (!Strings.validSurrogatePairAt((CharSequence)b, (int)(p - 1))) return a.subSequence((int)0, (int)p).toString();
        }
        --p;
        return a.subSequence((int)0, (int)p).toString();
    }

    public static String commonSuffix(CharSequence a, CharSequence b) {
        int s;
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        int maxSuffixLength = Math.min((int)a.length(), (int)b.length());
        for (s = 0; s < maxSuffixLength && a.charAt((int)(a.length() - s - 1)) == b.charAt((int)(b.length() - s - 1)); ++s) {
        }
        if (!Strings.validSurrogatePairAt((CharSequence)a, (int)(a.length() - s - 1))) {
            if (!Strings.validSurrogatePairAt((CharSequence)b, (int)(b.length() - s - 1))) return a.subSequence((int)(a.length() - s), (int)a.length()).toString();
        }
        --s;
        return a.subSequence((int)(a.length() - s), (int)a.length()).toString();
    }

    @VisibleForTesting
    static boolean validSurrogatePairAt(CharSequence string, int index) {
        if (index < 0) return false;
        if (index > string.length() - 2) return false;
        if (!Character.isHighSurrogate((char)string.charAt((int)index))) return false;
        if (!Character.isLowSurrogate((char)string.charAt((int)(index + 1)))) return false;
        return true;
    }
}

