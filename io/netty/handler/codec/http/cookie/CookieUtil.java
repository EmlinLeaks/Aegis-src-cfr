/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.InternalThreadLocalMap;
import java.util.BitSet;

final class CookieUtil {
    private static final BitSet VALID_COOKIE_NAME_OCTETS = CookieUtil.validCookieNameOctets();
    private static final BitSet VALID_COOKIE_VALUE_OCTETS = CookieUtil.validCookieValueOctets();
    private static final BitSet VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS = CookieUtil.validCookieAttributeValueOctets();

    private static BitSet validCookieNameOctets() {
        int[] separators;
        BitSet bits = new BitSet();
        for (int i = 32; i < 127; ++i) {
            bits.set((int)i);
        }
        int[] arrn = separators = new int[]{40, 41, 60, 62, 64, 44, 59, 58, 92, 34, 47, 91, 93, 63, 61, 123, 125, 32, 9};
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int separator = arrn[n2];
            bits.set((int)separator, (boolean)false);
            ++n2;
        }
        return bits;
    }

    private static BitSet validCookieValueOctets() {
        int i;
        BitSet bits = new BitSet();
        bits.set((int)33);
        for (i = 35; i <= 43; ++i) {
            bits.set((int)i);
        }
        for (i = 45; i <= 58; ++i) {
            bits.set((int)i);
        }
        for (i = 60; i <= 91; ++i) {
            bits.set((int)i);
        }
        i = 93;
        while (i <= 126) {
            bits.set((int)i);
            ++i;
        }
        return bits;
    }

    private static BitSet validCookieAttributeValueOctets() {
        BitSet bits = new BitSet();
        int i = 32;
        do {
            if (i >= 127) {
                bits.set((int)59, (boolean)false);
                return bits;
            }
            bits.set((int)i);
            ++i;
        } while (true);
    }

    static StringBuilder stringBuilder() {
        return InternalThreadLocalMap.get().stringBuilder();
    }

    static String stripTrailingSeparatorOrNull(StringBuilder buf) {
        if (buf.length() == 0) {
            return null;
        }
        String string = CookieUtil.stripTrailingSeparator((StringBuilder)buf);
        return string;
    }

    static String stripTrailingSeparator(StringBuilder buf) {
        if (buf.length() <= 0) return buf.toString();
        buf.setLength((int)(buf.length() - 2));
        return buf.toString();
    }

    static void add(StringBuilder sb, String name, long val) {
        sb.append((String)name);
        sb.append((char)'=');
        sb.append((long)val);
        sb.append((char)';');
        sb.append((char)' ');
    }

    static void add(StringBuilder sb, String name, String val) {
        sb.append((String)name);
        sb.append((char)'=');
        sb.append((String)val);
        sb.append((char)';');
        sb.append((char)' ');
    }

    static void add(StringBuilder sb, String name) {
        sb.append((String)name);
        sb.append((char)';');
        sb.append((char)' ');
    }

    static void addQuoted(StringBuilder sb, String name, String val) {
        if (val == null) {
            val = "";
        }
        sb.append((String)name);
        sb.append((char)'=');
        sb.append((char)'\"');
        sb.append((String)val);
        sb.append((char)'\"');
        sb.append((char)';');
        sb.append((char)' ');
    }

    static int firstInvalidCookieNameOctet(CharSequence cs) {
        return CookieUtil.firstInvalidOctet((CharSequence)cs, (BitSet)VALID_COOKIE_NAME_OCTETS);
    }

    static int firstInvalidCookieValueOctet(CharSequence cs) {
        return CookieUtil.firstInvalidOctet((CharSequence)cs, (BitSet)VALID_COOKIE_VALUE_OCTETS);
    }

    static int firstInvalidOctet(CharSequence cs, BitSet bits) {
        int i = 0;
        while (i < cs.length()) {
            char c = cs.charAt((int)i);
            if (!bits.get((int)c)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    static CharSequence unwrapValue(CharSequence cs) {
        int len = cs.length();
        if (len <= 0) return cs;
        if (cs.charAt((int)0) != '\"') return cs;
        if (len < 2) return null;
        if (cs.charAt((int)(len - 1)) != '\"') return null;
        if (len == 2) {
            return "";
        }
        CharSequence charSequence = cs.subSequence((int)1, (int)(len - 1));
        return charSequence;
    }

    static String validateAttributeValue(String name, String value) {
        if (value == null) {
            return null;
        }
        if ((value = value.trim()).isEmpty()) {
            return null;
        }
        int i = CookieUtil.firstInvalidOctet((CharSequence)value, (BitSet)VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS);
        if (i == -1) return value;
        throw new IllegalArgumentException((String)(name + " contains the prohibited characters: " + value.charAt((int)i)));
    }

    private CookieUtil() {
    }
}

