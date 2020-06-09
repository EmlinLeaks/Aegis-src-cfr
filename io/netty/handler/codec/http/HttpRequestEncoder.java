/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

public class HttpRequestEncoder
extends HttpObjectEncoder<HttpRequest> {
    private static final char SLASH = '/';
    private static final char QUESTION_MARK = '?';
    private static final int SLASH_AND_SPACE_SHORT = 12064;
    private static final int SPACE_SLASH_AND_SPACE_MEDIUM = 2109216;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (!super.acceptOutboundMessage((Object)msg)) return false;
        if (msg instanceof HttpResponse) return false;
        return true;
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf, HttpRequest request) throws Exception {
        ByteBufUtil.copy((AsciiString)request.method().asciiName(), (ByteBuf)buf);
        String uri = request.uri();
        if (uri.isEmpty()) {
            ByteBufUtil.writeMediumBE((ByteBuf)buf, (int)2109216);
        } else {
            CharSequence uriCharSequence = uri;
            boolean needSlash = false;
            int start = uri.indexOf((String)"://");
            if (start != -1 && uri.charAt((int)0) != '/') {
                int index = uri.indexOf((int)63, (int)(start += 3));
                if (index == -1) {
                    if (uri.lastIndexOf((int)47) < start) {
                        needSlash = true;
                    }
                } else if (uri.lastIndexOf((int)47, (int)index) < start) {
                    uriCharSequence = new StringBuilder((String)uri).insert((int)index, (char)'/');
                }
            }
            buf.writeByte((int)32).writeCharSequence((CharSequence)uriCharSequence, (Charset)CharsetUtil.UTF_8);
            if (needSlash) {
                ByteBufUtil.writeShortBE((ByteBuf)buf, (int)12064);
            } else {
                buf.writeByte((int)32);
            }
        }
        request.protocolVersion().encode((ByteBuf)buf);
        ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
    }
}

