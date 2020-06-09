/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.CookieUtil;

public abstract class CookieEncoder {
    protected final boolean strict;

    protected CookieEncoder(boolean strict) {
        this.strict = strict;
    }

    protected void validateCookie(String name, String value) {
        if (!this.strict) return;
        int pos = CookieUtil.firstInvalidCookieNameOctet((CharSequence)name);
        if (pos >= 0) {
            throw new IllegalArgumentException((String)("Cookie name contains an invalid char: " + name.charAt((int)pos)));
        }
        CharSequence unwrappedValue = CookieUtil.unwrapValue((CharSequence)value);
        if (unwrappedValue == null) {
            throw new IllegalArgumentException((String)("Cookie value wrapping quotes are not balanced: " + value));
        }
        pos = CookieUtil.firstInvalidCookieValueOctet((CharSequence)unwrappedValue);
        if (pos < 0) return;
        throw new IllegalArgumentException((String)("Cookie value contains an invalid char: " + unwrappedValue.charAt((int)pos)));
    }
}

