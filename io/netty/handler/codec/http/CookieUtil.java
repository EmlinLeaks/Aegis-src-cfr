/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import java.util.BitSet;

@Deprecated
final class CookieUtil {
    private static final BitSet VALID_COOKIE_VALUE_OCTETS = CookieUtil.validCookieValueOctets();
    private static final BitSet VALID_COOKIE_NAME_OCTETS = CookieUtil.validCookieNameOctets((BitSet)VALID_COOKIE_VALUE_OCTETS);

    private static BitSet validCookieValueOctets() {
        BitSet bits = new BitSet((int)8);
        int i = 35;
        do {
            if (i >= 127) {
                bits.set((int)34, (boolean)false);
                bits.set((int)44, (boolean)false);
                bits.set((int)59, (boolean)false);
                bits.set((int)92, (boolean)false);
                return bits;
            }
            bits.set((int)i);
            ++i;
        } while (true);
    }

    private static BitSet validCookieNameOctets(BitSet validCookieValueOctets) {
        BitSet bits = new BitSet((int)8);
        bits.or((BitSet)validCookieValueOctets);
        bits.set((int)40, (boolean)false);
        bits.set((int)41, (boolean)false);
        bits.set((int)60, (boolean)false);
        bits.set((int)62, (boolean)false);
        bits.set((int)64, (boolean)false);
        bits.set((int)58, (boolean)false);
        bits.set((int)47, (boolean)false);
        bits.set((int)91, (boolean)false);
        bits.set((int)93, (boolean)false);
        bits.set((int)63, (boolean)false);
        bits.set((int)61, (boolean)false);
        bits.set((int)123, (boolean)false);
        bits.set((int)125, (boolean)false);
        bits.set((int)32, (boolean)false);
        bits.set((int)9, (boolean)false);
        return bits;
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

    private CookieUtil() {
    }
}

