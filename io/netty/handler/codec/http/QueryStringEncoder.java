/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public class QueryStringEncoder {
    private final Charset charset;
    private final StringBuilder uriBuilder;
    private boolean hasParams;
    private static final byte WRITE_UTF_UNKNOWN = 63;
    private static final char[] CHAR_MAP = "0123456789ABCDEF".toCharArray();

    public QueryStringEncoder(String uri) {
        this((String)uri, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringEncoder(String uri, Charset charset) {
        ObjectUtil.checkNotNull(charset, (String)"charset");
        this.uriBuilder = new StringBuilder((String)uri);
        this.charset = CharsetUtil.UTF_8.equals((Object)charset) ? null : charset;
    }

    public void addParam(String name, String value) {
        ObjectUtil.checkNotNull(name, (String)"name");
        if (this.hasParams) {
            this.uriBuilder.append((char)'&');
        } else {
            this.uriBuilder.append((char)'?');
            this.hasParams = true;
        }
        this.encodeComponent((CharSequence)name);
        if (value == null) return;
        this.uriBuilder.append((char)'=');
        this.encodeComponent((CharSequence)value);
    }

    private void encodeComponent(CharSequence s) {
        if (this.charset == null) {
            this.encodeUtf8Component((CharSequence)s);
            return;
        }
        this.encodeNonUtf8Component((CharSequence)s);
    }

    public URI toUri() throws URISyntaxException {
        return new URI((String)this.toString());
    }

    public String toString() {
        return this.uriBuilder.toString();
    }

    /*
     * Unable to fully structure code
     */
    private void encodeNonUtf8Component(CharSequence s) {
        buf = null;
        i = 0;
        len = s.length();
        block0 : do {
            if (i >= len) return;
            c = s.charAt((int)i);
            if (QueryStringEncoder.dontNeedEncoding((char)c)) {
                this.uriBuilder.append((char)c);
                ++i;
                continue;
            }
            index = 0;
            if (buf == null) {
                buf = new char[s.length() - i];
            }
            do {
                buf[index] = c;
                ++index;
            } while (++i < s.length() && !QueryStringEncoder.dontNeedEncoding((char)(c = s.charAt((int)i))));
            var8_8 = bytes = new String((char[])buf, (int)0, (int)index).getBytes((Charset)this.charset);
            var9_9 = var8_8.length;
            var10_10 = 0;
            do {
                if (var10_10 < var9_9) ** break;
                continue block0;
                b = var8_8[var10_10];
                this.appendEncoded((int)b);
                ++var10_10;
            } while (true);
            break;
        } while (true);
    }

    private void encodeUtf8Component(CharSequence s) {
        int i = 0;
        int len = s.length();
        while (i < len) {
            char c = s.charAt((int)i);
            if (c < '?') {
                if (QueryStringEncoder.dontNeedEncoding((char)c)) {
                    this.uriBuilder.append((char)c);
                } else {
                    this.appendEncoded((int)c);
                }
            } else if (c < '\u0800') {
                this.appendEncoded((int)(192 | c >> 6));
                this.appendEncoded((int)(128 | c & 63));
            } else if (StringUtil.isSurrogate((char)c)) {
                if (!Character.isHighSurrogate((char)c)) {
                    this.appendEncoded((int)63);
                } else {
                    if (++i == s.length()) {
                        this.appendEncoded((int)63);
                        return;
                    }
                    this.writeUtf8Surrogate((char)c, (char)s.charAt((int)i));
                }
            } else {
                this.appendEncoded((int)(224 | c >> 12));
                this.appendEncoded((int)(128 | c >> 6 & 63));
                this.appendEncoded((int)(128 | c & 63));
            }
            ++i;
        }
    }

    private void writeUtf8Surrogate(char c, char c2) {
        if (Character.isLowSurrogate((char)c2)) {
            int codePoint = Character.toCodePoint((char)c, (char)c2);
            this.appendEncoded((int)(240 | codePoint >> 18));
            this.appendEncoded((int)(128 | codePoint >> 12 & 63));
            this.appendEncoded((int)(128 | codePoint >> 6 & 63));
            this.appendEncoded((int)(128 | codePoint & 63));
            return;
        }
        this.appendEncoded((int)63);
        this.appendEncoded((int)(Character.isHighSurrogate((char)c2) ? 63 : (int)c2));
    }

    private void appendEncoded(int b) {
        this.uriBuilder.append((char)'%').append((char)QueryStringEncoder.forDigit((int)(b >> 4))).append((char)QueryStringEncoder.forDigit((int)b));
    }

    private static char forDigit(int digit) {
        return CHAR_MAP[digit & 15];
    }

    private static boolean dontNeedEncoding(char ch) {
        if (ch >= 'a') {
            if (ch <= 'z') return true;
        }
        if (ch >= 'A') {
            if (ch <= 'Z') return true;
        }
        if (ch >= '0') {
            if (ch <= '9') return true;
        }
        if (ch == '-') return true;
        if (ch == '_') return true;
        if (ch == '.') return true;
        if (ch == '*') return true;
        return false;
    }
}

