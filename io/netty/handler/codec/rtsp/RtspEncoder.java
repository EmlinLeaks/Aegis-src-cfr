/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;

public class RtspEncoder
extends HttpObjectEncoder<HttpMessage> {
    private static final int CRLF_SHORT = 3338;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (!super.acceptOutboundMessage((Object)msg)) return false;
        if (msg instanceof HttpRequest) return true;
        if (!(msg instanceof HttpResponse)) return false;
        return true;
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf, HttpMessage message) throws Exception {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)message;
            ByteBufUtil.copy((AsciiString)request.method().asciiName(), (ByteBuf)buf);
            buf.writeByte((int)32);
            buf.writeCharSequence((CharSequence)request.uri(), (Charset)CharsetUtil.UTF_8);
            buf.writeByte((int)32);
            buf.writeCharSequence((CharSequence)request.protocolVersion().toString(), (Charset)CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
            return;
        }
        if (!(message instanceof HttpResponse)) throw new UnsupportedMessageTypeException((String)("Unsupported type " + StringUtil.simpleClassName((Object)message)));
        HttpResponse response = (HttpResponse)message;
        buf.writeCharSequence((CharSequence)response.protocolVersion().toString(), (Charset)CharsetUtil.US_ASCII);
        buf.writeByte((int)32);
        ByteBufUtil.copy((AsciiString)response.status().codeAsText(), (ByteBuf)buf);
        buf.writeByte((int)32);
        buf.writeCharSequence((CharSequence)response.status().reasonPhrase(), (Charset)CharsetUtil.US_ASCII);
        ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
    }
}

