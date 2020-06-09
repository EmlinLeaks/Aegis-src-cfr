/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map;

final class HttpMessageUtil {
    static StringBuilder appendRequest(StringBuilder buf, HttpRequest req) {
        HttpMessageUtil.appendCommon((StringBuilder)buf, (HttpMessage)req);
        HttpMessageUtil.appendInitialLine((StringBuilder)buf, (HttpRequest)req);
        HttpMessageUtil.appendHeaders((StringBuilder)buf, (HttpHeaders)req.headers());
        HttpMessageUtil.removeLastNewLine((StringBuilder)buf);
        return buf;
    }

    static StringBuilder appendResponse(StringBuilder buf, HttpResponse res) {
        HttpMessageUtil.appendCommon((StringBuilder)buf, (HttpMessage)res);
        HttpMessageUtil.appendInitialLine((StringBuilder)buf, (HttpResponse)res);
        HttpMessageUtil.appendHeaders((StringBuilder)buf, (HttpHeaders)res.headers());
        HttpMessageUtil.removeLastNewLine((StringBuilder)buf);
        return buf;
    }

    private static void appendCommon(StringBuilder buf, HttpMessage msg) {
        buf.append((String)StringUtil.simpleClassName((Object)msg));
        buf.append((String)"(decodeResult: ");
        buf.append((Object)msg.decoderResult());
        buf.append((String)", version: ");
        buf.append((Object)msg.protocolVersion());
        buf.append((char)')');
        buf.append((String)StringUtil.NEWLINE);
    }

    static StringBuilder appendFullRequest(StringBuilder buf, FullHttpRequest req) {
        HttpMessageUtil.appendFullCommon((StringBuilder)buf, (FullHttpMessage)req);
        HttpMessageUtil.appendInitialLine((StringBuilder)buf, (HttpRequest)req);
        HttpMessageUtil.appendHeaders((StringBuilder)buf, (HttpHeaders)req.headers());
        HttpMessageUtil.appendHeaders((StringBuilder)buf, (HttpHeaders)req.trailingHeaders());
        HttpMessageUtil.removeLastNewLine((StringBuilder)buf);
        return buf;
    }

    static StringBuilder appendFullResponse(StringBuilder buf, FullHttpResponse res) {
        HttpMessageUtil.appendFullCommon((StringBuilder)buf, (FullHttpMessage)res);
        HttpMessageUtil.appendInitialLine((StringBuilder)buf, (HttpResponse)res);
        HttpMessageUtil.appendHeaders((StringBuilder)buf, (HttpHeaders)res.headers());
        HttpMessageUtil.appendHeaders((StringBuilder)buf, (HttpHeaders)res.trailingHeaders());
        HttpMessageUtil.removeLastNewLine((StringBuilder)buf);
        return buf;
    }

    private static void appendFullCommon(StringBuilder buf, FullHttpMessage msg) {
        buf.append((String)StringUtil.simpleClassName((Object)msg));
        buf.append((String)"(decodeResult: ");
        buf.append((Object)msg.decoderResult());
        buf.append((String)", version: ");
        buf.append((Object)msg.protocolVersion());
        buf.append((String)", content: ");
        buf.append((Object)msg.content());
        buf.append((char)')');
        buf.append((String)StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf, HttpRequest req) {
        buf.append((Object)req.method());
        buf.append((char)' ');
        buf.append((String)req.uri());
        buf.append((char)' ');
        buf.append((Object)req.protocolVersion());
        buf.append((String)StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf, HttpResponse res) {
        buf.append((Object)res.protocolVersion());
        buf.append((char)' ');
        buf.append((Object)res.status());
        buf.append((String)StringUtil.NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf, HttpHeaders headers) {
        Iterator<Map.Entry<String, String>> iterator = headers.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> e = iterator.next();
            buf.append((String)e.getKey());
            buf.append((String)": ");
            buf.append((String)e.getValue());
            buf.append((String)StringUtil.NEWLINE);
        }
    }

    private static void removeLastNewLine(StringBuilder buf) {
        buf.setLength((int)(buf.length() - StringUtil.NEWLINE.length()));
    }

    private HttpMessageUtil() {
    }
}

