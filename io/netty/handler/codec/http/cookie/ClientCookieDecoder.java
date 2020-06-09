/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.ObjectUtil;

public final class ClientCookieDecoder
extends CookieDecoder {
    public static final ClientCookieDecoder STRICT = new ClientCookieDecoder((boolean)true);
    public static final ClientCookieDecoder LAX = new ClientCookieDecoder((boolean)false);

    private ClientCookieDecoder(boolean strict) {
        super((boolean)strict);
    }

    public Cookie decode(String header) {
        int headerLen = ObjectUtil.checkNotNull(header, (String)"header").length();
        if (headerLen == 0) {
            return null;
        }
        CookieBuilder cookieBuilder = null;
        int i = 0;
        do {
            int nameEnd;
            int valueBegin;
            int nameBegin;
            int valueEnd;
            block13 : {
                char c;
                if (i != headerLen && (c = header.charAt((int)i)) != ',') {
                    if (c == '\t' || c == '\n' || c == '\u000b' || c == '\f' || c == '\r' || c == ' ' || c == ';') {
                        ++i;
                        continue;
                    }
                } else {
                    if (cookieBuilder == null) return null;
                    Cookie cookie = cookieBuilder.cookie();
                    return cookie;
                }
                nameBegin = i;
                do {
                    char curChar;
                    if ((curChar = header.charAt((int)i)) == ';') {
                        nameEnd = i;
                        valueEnd = -1;
                        valueBegin = -1;
                    } else {
                        if (curChar != '=') continue;
                        nameEnd = i++;
                        if (i == headerLen) {
                            valueEnd = 0;
                            valueBegin = 0;
                        } else {
                            valueBegin = i;
                            int semiPos = header.indexOf((int)59, (int)i);
                            i = semiPos > 0 ? semiPos : headerLen;
                            valueEnd = i;
                        }
                    }
                    break block13;
                } while (++i != headerLen);
                nameEnd = headerLen;
                valueEnd = -1;
                valueBegin = -1;
            }
            if (valueEnd > 0 && header.charAt((int)(valueEnd - 1)) == ',') {
                --valueEnd;
            }
            if (cookieBuilder == null) {
                DefaultCookie cookie = this.initCookie((String)header, (int)nameBegin, (int)nameEnd, (int)valueBegin, (int)valueEnd);
                if (cookie == null) {
                    return null;
                }
                cookieBuilder = new CookieBuilder((DefaultCookie)cookie, (String)header);
                continue;
            }
            cookieBuilder.appendAttribute((int)nameBegin, (int)nameEnd, (int)valueBegin, (int)valueEnd);
        } while (true);
    }
}

