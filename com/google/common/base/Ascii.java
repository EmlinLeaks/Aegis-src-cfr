/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@GwtCompatible
public final class Ascii {
    public static final byte NUL = 0;
    public static final byte SOH = 1;
    public static final byte STX = 2;
    public static final byte ETX = 3;
    public static final byte EOT = 4;
    public static final byte ENQ = 5;
    public static final byte ACK = 6;
    public static final byte BEL = 7;
    public static final byte BS = 8;
    public static final byte HT = 9;
    public static final byte LF = 10;
    public static final byte NL = 10;
    public static final byte VT = 11;
    public static final byte FF = 12;
    public static final byte CR = 13;
    public static final byte SO = 14;
    public static final byte SI = 15;
    public static final byte DLE = 16;
    public static final byte DC1 = 17;
    public static final byte XON = 17;
    public static final byte DC2 = 18;
    public static final byte DC3 = 19;
    public static final byte XOFF = 19;
    public static final byte DC4 = 20;
    public static final byte NAK = 21;
    public static final byte SYN = 22;
    public static final byte ETB = 23;
    public static final byte CAN = 24;
    public static final byte EM = 25;
    public static final byte SUB = 26;
    public static final byte ESC = 27;
    public static final byte FS = 28;
    public static final byte GS = 29;
    public static final byte RS = 30;
    public static final byte US = 31;
    public static final byte SP = 32;
    public static final byte SPACE = 32;
    public static final byte DEL = 127;
    public static final char MIN = '\u0000';
    public static final char MAX = '';

    private Ascii() {
    }

    public static String toLowerCase(String string) {
        int length = string.length();
        int i = 0;
        while (i < length) {
            if (Ascii.isUpperCase((char)string.charAt((int)i))) {
                char[] chars = string.toCharArray();
                while (i < length) {
                    char c = chars[i];
                    if (Ascii.isUpperCase((char)c)) {
                        chars[i] = (char)(c ^ 32);
                    }
                    ++i;
                }
                return String.valueOf((char[])chars);
            }
            ++i;
        }
        return string;
    }

    public static String toLowerCase(CharSequence chars) {
        if (chars instanceof String) {
            return Ascii.toLowerCase((String)((String)chars));
        }
        char[] newChars = new char[chars.length()];
        int i = 0;
        while (i < newChars.length) {
            newChars[i] = Ascii.toLowerCase((char)chars.charAt((int)i));
            ++i;
        }
        return String.valueOf((char[])newChars);
    }

    public static char toLowerCase(char c) {
        char c2;
        if (Ascii.isUpperCase((char)c)) {
            c2 = (char)(c ^ 32);
            return c2;
        }
        c2 = c;
        return c2;
    }

    public static String toUpperCase(String string) {
        int length = string.length();
        int i = 0;
        while (i < length) {
            if (Ascii.isLowerCase((char)string.charAt((int)i))) {
                char[] chars = string.toCharArray();
                while (i < length) {
                    char c = chars[i];
                    if (Ascii.isLowerCase((char)c)) {
                        chars[i] = (char)(c & 95);
                    }
                    ++i;
                }
                return String.valueOf((char[])chars);
            }
            ++i;
        }
        return string;
    }

    public static String toUpperCase(CharSequence chars) {
        if (chars instanceof String) {
            return Ascii.toUpperCase((String)((String)chars));
        }
        char[] newChars = new char[chars.length()];
        int i = 0;
        while (i < newChars.length) {
            newChars[i] = Ascii.toUpperCase((char)chars.charAt((int)i));
            ++i;
        }
        return String.valueOf((char[])newChars);
    }

    public static char toUpperCase(char c) {
        char c2;
        if (Ascii.isLowerCase((char)c)) {
            c2 = (char)(c & 95);
            return c2;
        }
        c2 = c;
        return c2;
    }

    public static boolean isLowerCase(char c) {
        if (c < 'a') return false;
        if (c > 'z') return false;
        return true;
    }

    public static boolean isUpperCase(char c) {
        if (c < 'A') return false;
        if (c > 'Z') return false;
        return true;
    }

    public static String truncate(CharSequence seq, int maxLength, String truncationIndicator) {
        Preconditions.checkNotNull(seq);
        int truncationLength = maxLength - truncationIndicator.length();
        Preconditions.checkArgument((boolean)(truncationLength >= 0), (String)"maxLength (%s) must be >= length of the truncation indicator (%s)", (int)maxLength, (int)truncationIndicator.length());
        if (seq.length() > maxLength) return new StringBuilder((int)maxLength).append((CharSequence)seq, (int)0, (int)truncationLength).append((String)truncationIndicator).toString();
        String string = seq.toString();
        if (string.length() <= maxLength) {
            return string;
        }
        seq = string;
        return new StringBuilder((int)maxLength).append((CharSequence)seq, (int)0, (int)truncationLength).append((String)truncationIndicator).toString();
    }

    public static boolean equalsIgnoreCase(CharSequence s1, CharSequence s2) {
        int length = s1.length();
        if (s1 == s2) {
            return true;
        }
        if (length != s2.length()) {
            return false;
        }
        int i = 0;
        while (i < length) {
            char c2;
            char c1 = s1.charAt((int)i);
            if (c1 != (c2 = s2.charAt((int)i))) {
                int alphaIndex = Ascii.getAlphaIndex((char)c1);
                if (alphaIndex >= 26) return false;
                if (alphaIndex != Ascii.getAlphaIndex((char)c2)) return false;
            }
            ++i;
        }
        return true;
    }

    private static int getAlphaIndex(char c) {
        return (char)((c | 32) - 97);
    }
}

