/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.CharBuffer;

public abstract class CookieDecoder {
    private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
    private final boolean strict;

    protected CookieDecoder(boolean strict) {
        this.strict = strict;
    }

    protected DefaultCookie initCookie(String header, int nameBegin, int nameEnd, int valueBegin, int valueEnd) {
        boolean wrap;
        int invalidOctetPos;
        if (nameBegin == -1 || nameBegin == nameEnd) {
            this.logger.debug((String)"Skipping cookie with null name");
            return null;
        }
        if (valueBegin == -1) {
            this.logger.debug((String)"Skipping cookie with null value");
            return null;
        }
        CharBuffer wrappedValue = CharBuffer.wrap((CharSequence)header, (int)valueBegin, (int)valueEnd);
        CharSequence unwrappedValue = CookieUtil.unwrapValue((CharSequence)wrappedValue);
        if (unwrappedValue == null) {
            this.logger.debug((String)"Skipping cookie because starting quotes are not properly balanced in '{}'", (Object)wrappedValue);
            return null;
        }
        String name = header.substring((int)nameBegin, (int)nameEnd);
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet((CharSequence)name)) >= 0) {
            if (!this.logger.isDebugEnabled()) return null;
            this.logger.debug((String)"Skipping cookie because name '{}' contains invalid char '{}'", (Object)name, (Object)Character.valueOf((char)name.charAt((int)invalidOctetPos)));
            return null;
        }
        boolean bl = wrap = unwrappedValue.length() != valueEnd - valueBegin;
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet((CharSequence)unwrappedValue)) >= 0) {
            if (!this.logger.isDebugEnabled()) return null;
            this.logger.debug((String)"Skipping cookie because value '{}' contains invalid char '{}'", (Object)unwrappedValue, (Object)Character.valueOf((char)unwrappedValue.charAt((int)invalidOctetPos)));
            return null;
        }
        DefaultCookie cookie = new DefaultCookie((String)name, (String)unwrappedValue.toString());
        cookie.setWrap((boolean)wrap);
        return cookie;
    }
}

