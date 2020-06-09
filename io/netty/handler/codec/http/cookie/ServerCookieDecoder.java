/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public final class ServerCookieDecoder
extends CookieDecoder {
    private static final String RFC2965_VERSION = "$Version";
    private static final String RFC2965_PATH = "$Path";
    private static final String RFC2965_DOMAIN = "$Domain";
    private static final String RFC2965_PORT = "$Port";
    public static final ServerCookieDecoder STRICT = new ServerCookieDecoder((boolean)true);
    public static final ServerCookieDecoder LAX = new ServerCookieDecoder((boolean)false);

    private ServerCookieDecoder(boolean strict) {
        super((boolean)strict);
    }

    public Set<Cookie> decode(String header) {
        int headerLen = ObjectUtil.checkNotNull(header, (String)"header").length();
        if (headerLen == 0) {
            return Collections.emptySet();
        }
        TreeSet<Cookie> cookies = new TreeSet<Cookie>();
        int i = 0;
        boolean rfc2965Style = false;
        if (header.regionMatches((boolean)true, (int)0, (String)RFC2965_VERSION, (int)0, (int)RFC2965_VERSION.length())) {
            i = header.indexOf((int)59) + 1;
            rfc2965Style = true;
        }
        do {
            int valueEnd;
            int nameEnd;
            DefaultCookie cookie;
            int valueBegin;
            int nameBegin;
            block10 : {
                if (i == headerLen) {
                    return cookies;
                }
                char c = header.charAt((int)i);
                if (c == '\t' || c == '\n' || c == '\u000b' || c == '\f' || c == '\r' || c == ' ' || c == ',' || c == ';') {
                    ++i;
                    continue;
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
                    break block10;
                } while (++i != headerLen);
                nameEnd = headerLen;
                valueEnd = -1;
                valueBegin = -1;
            }
            if (rfc2965Style && (header.regionMatches((int)nameBegin, (String)RFC2965_PATH, (int)0, (int)RFC2965_PATH.length()) || header.regionMatches((int)nameBegin, (String)RFC2965_DOMAIN, (int)0, (int)RFC2965_DOMAIN.length()) || header.regionMatches((int)nameBegin, (String)RFC2965_PORT, (int)0, (int)RFC2965_PORT.length())) || (cookie = this.initCookie((String)header, (int)nameBegin, (int)nameEnd, (int)valueBegin, (int)valueEnd)) == null) continue;
            cookies.add((Cookie)cookie);
        } while (true);
    }
}

