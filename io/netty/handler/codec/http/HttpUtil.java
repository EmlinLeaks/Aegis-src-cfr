/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class HttpUtil {
    private static final AsciiString CHARSET_EQUALS = AsciiString.of((CharSequence)(HttpHeaderValues.CHARSET + "="));
    private static final AsciiString SEMICOLON = AsciiString.cached((String)";");

    private HttpUtil() {
    }

    public static boolean isOriginForm(URI uri) {
        if (uri.getScheme() != null) return false;
        if (uri.getSchemeSpecificPart() != null) return false;
        if (uri.getHost() != null) return false;
        if (uri.getAuthority() != null) return false;
        return true;
    }

    public static boolean isAsteriskForm(URI uri) {
        if (!"*".equals((Object)uri.getPath())) return false;
        if (uri.getScheme() != null) return false;
        if (uri.getSchemeSpecificPart() != null) return false;
        if (uri.getHost() != null) return false;
        if (uri.getAuthority() != null) return false;
        if (uri.getQuery() != null) return false;
        if (uri.getFragment() != null) return false;
        return true;
    }

    public static boolean isKeepAlive(HttpMessage message) {
        if (message.headers().containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.CLOSE, (boolean)true)) return false;
        if (message.protocolVersion().isKeepAliveDefault()) return true;
        if (!message.headers().containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.KEEP_ALIVE, (boolean)true)) return false;
        return true;
    }

    public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
        HttpUtil.setKeepAlive((HttpHeaders)message.headers(), (HttpVersion)message.protocolVersion(), (boolean)keepAlive);
    }

    public static void setKeepAlive(HttpHeaders h, HttpVersion httpVersion, boolean keepAlive) {
        if (httpVersion.isKeepAliveDefault()) {
            if (keepAlive) {
                h.remove((CharSequence)HttpHeaderNames.CONNECTION);
                return;
            }
            h.set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.CLOSE);
            return;
        }
        if (keepAlive) {
            h.set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.KEEP_ALIVE);
            return;
        }
        h.remove((CharSequence)HttpHeaderNames.CONNECTION);
    }

    public static long getContentLength(HttpMessage message) {
        String value = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
        if (value != null) {
            return Long.parseLong((String)value);
        }
        long webSocketContentLength = (long)HttpUtil.getWebSocketContentLength((HttpMessage)message);
        if (webSocketContentLength < 0L) throw new NumberFormatException((String)("header not found: " + HttpHeaderNames.CONTENT_LENGTH));
        return webSocketContentLength;
    }

    public static long getContentLength(HttpMessage message, long defaultValue) {
        String value = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
        if (value != null) {
            return Long.parseLong((String)value);
        }
        long webSocketContentLength = (long)HttpUtil.getWebSocketContentLength((HttpMessage)message);
        if (webSocketContentLength < 0L) return defaultValue;
        return webSocketContentLength;
    }

    public static int getContentLength(HttpMessage message, int defaultValue) {
        return (int)Math.min((long)Integer.MAX_VALUE, (long)HttpUtil.getContentLength((HttpMessage)message, (long)((long)defaultValue)));
    }

    private static int getWebSocketContentLength(HttpMessage message) {
        HttpHeaders h = message.headers();
        if (message instanceof HttpRequest) {
            HttpRequest req = (HttpRequest)message;
            if (!HttpMethod.GET.equals((Object)req.method())) return -1;
            if (!h.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1)) return -1;
            if (!h.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2)) return -1;
            return 8;
        }
        if (!(message instanceof HttpResponse)) return -1;
        HttpResponse res = (HttpResponse)message;
        if (res.status().code() != 101) return -1;
        if (!h.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN)) return -1;
        if (!h.contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_LOCATION)) return -1;
        return 16;
    }

    public static void setContentLength(HttpMessage message, long length) {
        message.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)Long.valueOf((long)length));
    }

    public static boolean isContentLengthSet(HttpMessage m) {
        return m.headers().contains((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
    }

    public static boolean is100ContinueExpected(HttpMessage message) {
        if (!HttpUtil.isExpectHeaderValid((HttpMessage)message)) return false;
        if (!message.headers().contains((CharSequence)HttpHeaderNames.EXPECT, (CharSequence)HttpHeaderValues.CONTINUE, (boolean)true)) return false;
        return true;
    }

    static boolean isUnsupportedExpectation(HttpMessage message) {
        if (!HttpUtil.isExpectHeaderValid((HttpMessage)message)) {
            return false;
        }
        String expectValue = message.headers().get((CharSequence)HttpHeaderNames.EXPECT);
        if (expectValue == null) return false;
        if (HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase((String)expectValue)) return false;
        return true;
    }

    private static boolean isExpectHeaderValid(HttpMessage message) {
        if (!(message instanceof HttpRequest)) return false;
        if (message.protocolVersion().compareTo((HttpVersion)HttpVersion.HTTP_1_1) < 0) return false;
        return true;
    }

    public static void set100ContinueExpected(HttpMessage message, boolean expected) {
        if (expected) {
            message.headers().set((CharSequence)HttpHeaderNames.EXPECT, (Object)HttpHeaderValues.CONTINUE);
            return;
        }
        message.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
    }

    public static boolean isTransferEncodingChunked(HttpMessage message) {
        return message.headers().contains((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (CharSequence)HttpHeaderValues.CHUNKED, (boolean)true);
    }

    public static void setTransferEncodingChunked(HttpMessage m, boolean chunked) {
        if (chunked) {
            m.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
            m.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
            return;
        }
        List<String> encodings = m.headers().getAll((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
        if (encodings.isEmpty()) {
            return;
        }
        ArrayList<String> values = new ArrayList<String>(encodings);
        Iterator<E> valuesIt = values.iterator();
        while (valuesIt.hasNext()) {
            CharSequence value = (CharSequence)valuesIt.next();
            if (!HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase((CharSequence)value)) continue;
            valuesIt.remove();
        }
        if (values.isEmpty()) {
            m.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
            return;
        }
        m.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, values);
    }

    public static Charset getCharset(HttpMessage message) {
        return HttpUtil.getCharset((HttpMessage)message, (Charset)CharsetUtil.ISO_8859_1);
    }

    public static Charset getCharset(CharSequence contentTypeValue) {
        if (contentTypeValue == null) return CharsetUtil.ISO_8859_1;
        return HttpUtil.getCharset((CharSequence)contentTypeValue, (Charset)CharsetUtil.ISO_8859_1);
    }

    public static Charset getCharset(HttpMessage message, Charset defaultCharset) {
        String contentTypeValue = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
        if (contentTypeValue == null) return defaultCharset;
        return HttpUtil.getCharset((CharSequence)contentTypeValue, (Charset)defaultCharset);
    }

    public static Charset getCharset(CharSequence contentTypeValue, Charset defaultCharset) {
        if (contentTypeValue == null) return defaultCharset;
        CharSequence charsetCharSequence = HttpUtil.getCharsetAsSequence((CharSequence)contentTypeValue);
        if (charsetCharSequence == null) return defaultCharset;
        try {
            return Charset.forName((String)charsetCharSequence.toString());
        }
        catch (IllegalCharsetNameException illegalCharsetNameException) {
            return defaultCharset;
        }
        catch (UnsupportedCharsetException unsupportedCharsetException) {
            // empty catch block
        }
        return defaultCharset;
    }

    @Deprecated
    public static CharSequence getCharsetAsString(HttpMessage message) {
        return HttpUtil.getCharsetAsSequence((HttpMessage)message);
    }

    public static CharSequence getCharsetAsSequence(HttpMessage message) {
        String contentTypeValue = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
        if (contentTypeValue == null) return null;
        return HttpUtil.getCharsetAsSequence((CharSequence)contentTypeValue);
    }

    public static CharSequence getCharsetAsSequence(CharSequence contentTypeValue) {
        if (contentTypeValue == null) {
            throw new NullPointerException((String)"contentTypeValue");
        }
        int indexOfCharset = AsciiString.indexOfIgnoreCaseAscii((CharSequence)contentTypeValue, (CharSequence)CHARSET_EQUALS, (int)0);
        if (indexOfCharset == -1) {
            return null;
        }
        int indexOfEncoding = indexOfCharset + CHARSET_EQUALS.length();
        if (indexOfEncoding >= contentTypeValue.length()) return null;
        CharSequence charsetCandidate = contentTypeValue.subSequence((int)indexOfEncoding, (int)contentTypeValue.length());
        int indexOfSemicolon = AsciiString.indexOfIgnoreCaseAscii((CharSequence)charsetCandidate, (CharSequence)SEMICOLON, (int)0);
        if (indexOfSemicolon != -1) return charsetCandidate.subSequence((int)0, (int)indexOfSemicolon);
        return charsetCandidate;
    }

    public static CharSequence getMimeType(HttpMessage message) {
        String contentTypeValue = message.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
        if (contentTypeValue == null) return null;
        return HttpUtil.getMimeType((CharSequence)contentTypeValue);
    }

    public static CharSequence getMimeType(CharSequence contentTypeValue) {
        if (contentTypeValue == null) {
            throw new NullPointerException((String)"contentTypeValue");
        }
        int indexOfSemicolon = AsciiString.indexOfIgnoreCaseAscii((CharSequence)contentTypeValue, (CharSequence)SEMICOLON, (int)0);
        if (indexOfSemicolon != -1) {
            return contentTypeValue.subSequence((int)0, (int)indexOfSemicolon);
        }
        if (contentTypeValue.length() <= 0) return null;
        CharSequence charSequence = contentTypeValue;
        return charSequence;
    }

    public static String formatHostnameForHttp(InetSocketAddress addr) {
        String hostString = NetUtil.getHostname((InetSocketAddress)addr);
        if (!NetUtil.isValidIpV6Address((String)hostString)) return hostString;
        if (addr.isUnresolved()) return '[' + hostString + ']';
        hostString = NetUtil.toAddressString((InetAddress)addr.getAddress());
        return '[' + hostString + ']';
    }
}

