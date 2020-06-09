/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class ParseRequest {
    final String rawValue;
    final int radix;

    private ParseRequest(String rawValue, int radix) {
        this.rawValue = rawValue;
        this.radix = radix;
    }

    static ParseRequest fromString(String stringValue) {
        int radix;
        String rawValue;
        if (stringValue.length() == 0) {
            throw new NumberFormatException((String)"empty string");
        }
        char firstChar = stringValue.charAt((int)0);
        if (stringValue.startsWith((String)"0x") || stringValue.startsWith((String)"0X")) {
            rawValue = stringValue.substring((int)2);
            radix = 16;
            return new ParseRequest((String)rawValue, (int)radix);
        }
        if (firstChar == '#') {
            rawValue = stringValue.substring((int)1);
            radix = 16;
            return new ParseRequest((String)rawValue, (int)radix);
        }
        if (firstChar == '0' && stringValue.length() > 1) {
            rawValue = stringValue.substring((int)1);
            radix = 8;
            return new ParseRequest((String)rawValue, (int)radix);
        }
        rawValue = stringValue;
        radix = 10;
        return new ParseRequest((String)rawValue, (int)radix);
    }
}

