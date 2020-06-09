/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

final class HttpHeadersEncoder {
    private static final int COLON_AND_SPACE_SHORT = 14880;

    private HttpHeadersEncoder() {
    }

    static void encoderHeader(CharSequence name, CharSequence value, ByteBuf buf) {
        int nameLen = name.length();
        int valueLen = value.length();
        int entryLen = nameLen + valueLen + 4;
        buf.ensureWritable((int)entryLen);
        int offset = buf.writerIndex();
        HttpHeadersEncoder.writeAscii((ByteBuf)buf, (int)offset, (CharSequence)name);
        ByteBufUtil.setShortBE((ByteBuf)buf, (int)(offset += nameLen), (int)14880);
        HttpHeadersEncoder.writeAscii((ByteBuf)buf, (int)(offset += 2), (CharSequence)value);
        ByteBufUtil.setShortBE((ByteBuf)buf, (int)(offset += valueLen), (int)3338);
        buf.writerIndex((int)(offset += 2));
    }

    private static void writeAscii(ByteBuf buf, int offset, CharSequence value) {
        if (value instanceof AsciiString) {
            ByteBufUtil.copy((AsciiString)((AsciiString)value), (int)0, (ByteBuf)buf, (int)offset, (int)value.length());
            return;
        }
        buf.setCharSequence((int)offset, (CharSequence)value, (Charset)CharsetUtil.US_ASCII);
    }
}

