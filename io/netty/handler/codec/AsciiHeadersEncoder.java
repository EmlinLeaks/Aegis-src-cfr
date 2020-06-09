/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.AsciiHeadersEncoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.Map;

public final class AsciiHeadersEncoder {
    private final ByteBuf buf;
    private final SeparatorType separatorType;
    private final NewlineType newlineType;

    public AsciiHeadersEncoder(ByteBuf buf) {
        this((ByteBuf)buf, (SeparatorType)SeparatorType.COLON_SPACE, (NewlineType)NewlineType.CRLF);
    }

    public AsciiHeadersEncoder(ByteBuf buf, SeparatorType separatorType, NewlineType newlineType) {
        if (buf == null) {
            throw new NullPointerException((String)"buf");
        }
        if (separatorType == null) {
            throw new NullPointerException((String)"separatorType");
        }
        if (newlineType == null) {
            throw new NullPointerException((String)"newlineType");
        }
        this.buf = buf;
        this.separatorType = separatorType;
        this.newlineType = newlineType;
    }

    /*
     * Unable to fully structure code
     */
    public void encode(Map.Entry<CharSequence, CharSequence> entry) {
        name = entry.getKey();
        value = entry.getValue();
        buf = this.buf;
        nameLen = name.length();
        valueLen = value.length();
        entryLen = nameLen + valueLen + 4;
        offset = buf.writerIndex();
        buf.ensureWritable((int)entryLen);
        AsciiHeadersEncoder.writeAscii((ByteBuf)buf, (int)offset, (CharSequence)name);
        offset += nameLen;
        switch (1.$SwitchMap$io$netty$handler$codec$AsciiHeadersEncoder$SeparatorType[this.separatorType.ordinal()]) {
            case 1: {
                buf.setByte((int)offset++, (int)58);
                ** break;
            }
            case 2: {
                buf.setByte((int)offset++, (int)58);
                buf.setByte((int)offset++, (int)32);
                ** break;
            }
        }
        throw new Error();
lbl24: // 2 sources:
        AsciiHeadersEncoder.writeAscii((ByteBuf)buf, (int)offset, (CharSequence)value);
        offset += valueLen;
        switch (1.$SwitchMap$io$netty$handler$codec$AsciiHeadersEncoder$NewlineType[this.newlineType.ordinal()]) {
            case 1: {
                buf.setByte((int)offset++, (int)10);
                ** break;
            }
            case 2: {
                buf.setByte((int)offset++, (int)13);
                buf.setByte((int)offset++, (int)10);
                ** break;
            }
        }
        throw new Error();
lbl38: // 2 sources:
        buf.writerIndex((int)offset);
    }

    private static void writeAscii(ByteBuf buf, int offset, CharSequence value) {
        if (value instanceof AsciiString) {
            ByteBufUtil.copy((AsciiString)((AsciiString)value), (int)0, (ByteBuf)buf, (int)offset, (int)value.length());
            return;
        }
        buf.setCharSequence((int)offset, (CharSequence)value, (Charset)CharsetUtil.US_ASCII);
    }
}

