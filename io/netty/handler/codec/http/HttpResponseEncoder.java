/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;

public class HttpResponseEncoder
extends HttpObjectEncoder<HttpResponse> {
    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (!super.acceptOutboundMessage((Object)msg)) return false;
        if (msg instanceof HttpRequest) return false;
        return true;
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf, HttpResponse response) throws Exception {
        response.protocolVersion().encode((ByteBuf)buf);
        buf.writeByte((int)32);
        response.status().encode((ByteBuf)buf);
        ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
    }

    @Override
    protected void sanitizeHeadersBeforeEncode(HttpResponse msg, boolean isAlwaysEmpty) {
        if (!isAlwaysEmpty) return;
        HttpResponseStatus status = msg.status();
        if (status.codeClass() != HttpStatusClass.INFORMATIONAL && status.code() != HttpResponseStatus.NO_CONTENT.code()) {
            if (status.code() != HttpResponseStatus.RESET_CONTENT.code()) return;
            msg.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
            msg.headers().setInt((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (int)0);
            return;
        }
        msg.headers().remove((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
        msg.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
    }

    @Override
    protected boolean isContentAlwaysEmpty(HttpResponse msg) {
        HttpResponseStatus status = msg.status();
        if (status.codeClass() == HttpStatusClass.INFORMATIONAL) {
            if (status.code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) return true;
            return msg.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION);
        }
        if (status.code() == HttpResponseStatus.NO_CONTENT.code()) return true;
        if (status.code() == HttpResponseStatus.NOT_MODIFIED.code()) return true;
        if (status.code() == HttpResponseStatus.RESET_CONTENT.code()) return true;
        return false;
    }
}

