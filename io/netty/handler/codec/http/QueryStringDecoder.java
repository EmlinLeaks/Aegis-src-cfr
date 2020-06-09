/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {
    private static final int DEFAULT_MAX_PARAMS = 1024;
    private final Charset charset;
    private final String uri;
    private final int maxParams;
    private final boolean semicolonIsNormalChar;
    private int pathEndIdx;
    private String path;
    private Map<String, List<String>> params;

    public QueryStringDecoder(String uri) {
        this((String)uri, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(String uri, boolean hasPath) {
        this((String)uri, (Charset)HttpConstants.DEFAULT_CHARSET, (boolean)hasPath);
    }

    public QueryStringDecoder(String uri, Charset charset) {
        this((String)uri, (Charset)charset, (boolean)true);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this((String)uri, (Charset)charset, (boolean)hasPath, (int)1024);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        this((String)uri, (Charset)charset, (boolean)hasPath, (int)maxParams, (boolean)false);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams, boolean semicolonIsNormalChar) {
        this.uri = ObjectUtil.checkNotNull(uri, (String)"uri");
        this.charset = ObjectUtil.checkNotNull(charset, (String)"charset");
        this.maxParams = ObjectUtil.checkPositive((int)maxParams, (String)"maxParams");
        this.semicolonIsNormalChar = semicolonIsNormalChar;
        this.pathEndIdx = hasPath ? -1 : 0;
    }

    public QueryStringDecoder(URI uri) {
        this((URI)uri, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(URI uri, Charset charset) {
        this((URI)uri, (Charset)charset, (int)1024);
    }

    public QueryStringDecoder(URI uri, Charset charset, int maxParams) {
        this((URI)uri, (Charset)charset, (int)maxParams, (boolean)false);
    }

    public QueryStringDecoder(URI uri, Charset charset, int maxParams, boolean semicolonIsNormalChar) {
        String rawQuery;
        String rawPath = uri.getRawPath();
        if (rawPath == null) {
            rawPath = "";
        }
        this.uri = (rawQuery = uri.getRawQuery()) == null ? rawPath : rawPath + '?' + rawQuery;
        this.charset = ObjectUtil.checkNotNull(charset, (String)"charset");
        this.maxParams = ObjectUtil.checkPositive((int)maxParams, (String)"maxParams");
        this.semicolonIsNormalChar = semicolonIsNormalChar;
        this.pathEndIdx = rawPath.length();
    }

    public String toString() {
        return this.uri();
    }

    public String uri() {
        return this.uri;
    }

    public String path() {
        if (this.path != null) return this.path;
        this.path = QueryStringDecoder.decodeComponent((String)this.uri, (int)0, (int)this.pathEndIdx(), (Charset)this.charset, (boolean)true);
        return this.path;
    }

    public Map<String, List<String>> parameters() {
        if (this.params != null) return this.params;
        this.params = QueryStringDecoder.decodeParams((String)this.uri, (int)this.pathEndIdx(), (Charset)this.charset, (int)this.maxParams, (boolean)this.semicolonIsNormalChar);
        return this.params;
    }

    public String rawPath() {
        return this.uri.substring((int)0, (int)this.pathEndIdx());
    }

    public String rawQuery() {
        int start = this.pathEndIdx() + 1;
        if (start >= this.uri.length()) return "";
        String string = this.uri.substring((int)start);
        return string;
    }

    private int pathEndIdx() {
        if (this.pathEndIdx != -1) return this.pathEndIdx;
        this.pathEndIdx = QueryStringDecoder.findPathEndIndex((String)this.uri);
        return this.pathEndIdx;
    }

    private static Map<String, List<String>> decodeParams(String s, int from, Charset charset, int paramsLimit, boolean semicolonIsNormalChar) {
        int i;
        int len = s.length();
        if (from >= len) {
            return Collections.emptyMap();
        }
        if (s.charAt((int)from) == '?') {
            ++from;
        }
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        int nameStart = from;
        int valueStart = -1;
        block6 : for (i = from; i < len; ++i) {
            switch (s.charAt((int)i)) {
                case '=': {
                    if (nameStart == i) {
                        nameStart = i + 1;
                        break;
                    }
                    if (valueStart >= nameStart) break;
                    valueStart = i + 1;
                    break;
                }
                case ';': {
                    if (semicolonIsNormalChar) break;
                }
                case '&': {
                    if (QueryStringDecoder.addParam((String)s, (int)nameStart, (int)valueStart, (int)i, params, (Charset)charset) && --paramsLimit == 0) {
                        return params;
                    }
                    nameStart = i + 1;
                    break;
                }
                case '#': {
                    break block6;
                }
            }
        }
        QueryStringDecoder.addParam((String)s, (int)nameStart, (int)valueStart, (int)i, params, (Charset)charset);
        return params;
    }

    private static boolean addParam(String s, int nameStart, int valueStart, int valueEnd, Map<String, List<String>> params, Charset charset) {
        if (nameStart >= valueEnd) {
            return false;
        }
        if (valueStart <= nameStart) {
            valueStart = valueEnd + 1;
        }
        String name = QueryStringDecoder.decodeComponent((String)s, (int)nameStart, (int)(valueStart - 1), (Charset)charset, (boolean)false);
        String value = QueryStringDecoder.decodeComponent((String)s, (int)valueStart, (int)valueEnd, (Charset)charset, (boolean)false);
        List<String> values = params.get((Object)name);
        if (values == null) {
            values = new ArrayList<String>((int)1);
            params.put((String)name, values);
        }
        values.add((String)value);
        return true;
    }

    public static String decodeComponent(String s) {
        return QueryStringDecoder.decodeComponent((String)s, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public static String decodeComponent(String s, Charset charset) {
        if (s != null) return QueryStringDecoder.decodeComponent((String)s, (int)0, (int)s.length(), (Charset)charset, (boolean)false);
        return "";
    }

    private static String decodeComponent(String s, int from, int toExcluded, Charset charset, boolean isPath) {
        int len = toExcluded - from;
        if (len <= 0) {
            return "";
        }
        int firstEscaped = -1;
        for (int i = from; i < toExcluded; ++i) {
            char c = s.charAt((int)i);
            if (c != '%' && (c != '+' || isPath)) continue;
            firstEscaped = i;
            break;
        }
        if (firstEscaped == -1) {
            return s.substring((int)from, (int)toExcluded);
        }
        CharsetDecoder decoder = CharsetUtil.decoder((Charset)charset);
        int decodedCapacity = (toExcluded - firstEscaped) / 3;
        ByteBuffer byteBuf = ByteBuffer.allocate((int)decodedCapacity);
        CharBuffer charBuf = CharBuffer.allocate((int)decodedCapacity);
        StringBuilder strBuf = new StringBuilder((int)len);
        strBuf.append((CharSequence)s, (int)from, (int)firstEscaped);
        int i = firstEscaped;
        while (i < toExcluded) {
            char c = s.charAt((int)i);
            if (c != '%') {
                strBuf.append((char)(c != '+' || isPath ? c : (char)' '));
            } else {
                byteBuf.clear();
                do {
                    if (i + 3 > toExcluded) {
                        throw new IllegalArgumentException((String)("unterminated escape sequence at index " + i + " of: " + s));
                    }
                    byteBuf.put((byte)StringUtil.decodeHexByte((CharSequence)s, (int)(i + 1)));
                } while ((i += 3) < toExcluded && s.charAt((int)i) == '%');
                --i;
                byteBuf.flip();
                charBuf.clear();
                CoderResult result = decoder.reset().decode((ByteBuffer)byteBuf, (CharBuffer)charBuf, (boolean)true);
                try {
                    if (!result.isUnderflow()) {
                        result.throwException();
                    }
                    if (!(result = decoder.flush((CharBuffer)charBuf)).isUnderflow()) {
                        result.throwException();
                    }
                }
                catch (CharacterCodingException ex) {
                    throw new IllegalStateException((Throwable)ex);
                }
                strBuf.append((Object)charBuf.flip());
            }
            ++i;
        }
        return strBuf.toString();
    }

    private static int findPathEndIndex(String uri) {
        int len = uri.length();
        int i = 0;
        while (i < len) {
            char c = uri.charAt((int)i);
            if (c == '?') return i;
            if (c == '#') {
                return i;
            }
            ++i;
        }
        return len;
    }
}

