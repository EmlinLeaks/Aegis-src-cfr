/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostStandardRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import io.netty.util.AsciiString;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;
import java.util.List;

public class HttpPostRequestDecoder
implements InterfaceHttpPostRequestDecoder {
    static final int DEFAULT_DISCARD_THRESHOLD = 10485760;
    private final InterfaceHttpPostRequestDecoder decoder;

    public HttpPostRequestDecoder(HttpRequest request) {
        this((HttpDataFactory)new DefaultHttpDataFactory((long)16384L), (HttpRequest)request, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request) {
        this((HttpDataFactory)factory, (HttpRequest)request, (Charset)HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
        if (factory == null) {
            throw new NullPointerException((String)"factory");
        }
        if (request == null) {
            throw new NullPointerException((String)"request");
        }
        if (charset == null) {
            throw new NullPointerException((String)"charset");
        }
        if (HttpPostRequestDecoder.isMultipart((HttpRequest)request)) {
            this.decoder = new HttpPostMultipartRequestDecoder((HttpDataFactory)factory, (HttpRequest)request, (Charset)charset);
            return;
        }
        this.decoder = new HttpPostStandardRequestDecoder((HttpDataFactory)factory, (HttpRequest)request, (Charset)charset);
    }

    public static boolean isMultipart(HttpRequest request) {
        String mimeType = request.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
        if (mimeType == null) return false;
        if (!mimeType.startsWith((String)HttpHeaderValues.MULTIPART_FORM_DATA.toString())) return false;
        if (HttpPostRequestDecoder.getMultipartDataBoundary((String)mimeType) == null) return false;
        return true;
    }

    protected static String[] getMultipartDataBoundary(String contentType) {
        int crank;
        String charsetHeader;
        int index;
        String multiPartHeader;
        int mrank;
        String bound;
        String charset;
        String[] headerContentType = HttpPostRequestDecoder.splitHeaderContentType((String)contentType);
        if (!headerContentType[0].regionMatches((boolean)true, (int)0, (String)(multiPartHeader = HttpHeaderValues.MULTIPART_FORM_DATA.toString()), (int)0, (int)multiPartHeader.length())) return null;
        String boundaryHeader = HttpHeaderValues.BOUNDARY.toString();
        if (headerContentType[1].regionMatches((boolean)true, (int)0, (String)boundaryHeader, (int)0, (int)boundaryHeader.length())) {
            mrank = 1;
            crank = 2;
        } else {
            if (!headerContentType[2].regionMatches((boolean)true, (int)0, (String)boundaryHeader, (int)0, (int)boundaryHeader.length())) return null;
            mrank = 2;
            crank = 1;
        }
        String boundary = StringUtil.substringAfter((String)headerContentType[mrank], (char)'=');
        if (boundary == null) {
            throw new ErrorDataDecoderException((String)"Needs a boundary value");
        }
        if (boundary.charAt((int)0) == '\"' && (bound = boundary.trim()).charAt((int)(index = bound.length() - 1)) == '\"') {
            boundary = bound.substring((int)1, (int)index);
        }
        if (!headerContentType[crank].regionMatches((boolean)true, (int)0, (String)(charsetHeader = HttpHeaderValues.CHARSET.toString()), (int)0, (int)charsetHeader.length()) || (charset = StringUtil.substringAfter((String)headerContentType[crank], (char)'=')) == null) return new String[]{"--" + boundary};
        return new String[]{"--" + boundary, charset};
    }

    @Override
    public boolean isMultipart() {
        return this.decoder.isMultipart();
    }

    @Override
    public void setDiscardThreshold(int discardThreshold) {
        this.decoder.setDiscardThreshold((int)discardThreshold);
    }

    @Override
    public int getDiscardThreshold() {
        return this.decoder.getDiscardThreshold();
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas() {
        return this.decoder.getBodyHttpDatas();
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas(String name) {
        return this.decoder.getBodyHttpDatas((String)name);
    }

    @Override
    public InterfaceHttpData getBodyHttpData(String name) {
        return this.decoder.getBodyHttpData((String)name);
    }

    @Override
    public InterfaceHttpPostRequestDecoder offer(HttpContent content) {
        return this.decoder.offer((HttpContent)content);
    }

    @Override
    public boolean hasNext() {
        return this.decoder.hasNext();
    }

    @Override
    public InterfaceHttpData next() {
        return this.decoder.next();
    }

    @Override
    public InterfaceHttpData currentPartialHttpData() {
        return this.decoder.currentPartialHttpData();
    }

    @Override
    public void destroy() {
        this.decoder.destroy();
    }

    @Override
    public void cleanFiles() {
        this.decoder.cleanFiles();
    }

    @Override
    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.decoder.removeHttpDataFromClean((InterfaceHttpData)data);
    }

    private static String[] splitHeaderContentType(String sb) {
        int bEnd;
        int aStart = HttpPostBodyUtil.findNonWhitespace((String)sb, (int)0);
        int aEnd = sb.indexOf((int)59);
        if (aEnd == -1) {
            return new String[]{sb, "", ""};
        }
        int bStart = HttpPostBodyUtil.findNonWhitespace((String)sb, (int)(aEnd + 1));
        if (sb.charAt((int)(aEnd - 1)) == ' ') {
            --aEnd;
        }
        if ((bEnd = sb.indexOf((int)59, (int)bStart)) == -1) {
            bEnd = HttpPostBodyUtil.findEndOfString((String)sb);
            return new String[]{sb.substring((int)aStart, (int)aEnd), sb.substring((int)bStart, (int)bEnd), ""};
        }
        int cStart = HttpPostBodyUtil.findNonWhitespace((String)sb, (int)(bEnd + 1));
        if (sb.charAt((int)(bEnd - 1)) == ' ') {
            --bEnd;
        }
        int cEnd = HttpPostBodyUtil.findEndOfString((String)sb);
        return new String[]{sb.substring((int)aStart, (int)aEnd), sb.substring((int)bStart, (int)bEnd), sb.substring((int)cStart, (int)cEnd)};
    }
}

