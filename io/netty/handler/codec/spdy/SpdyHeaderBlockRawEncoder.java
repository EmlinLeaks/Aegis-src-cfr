/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.spdy.SpdyHeaderBlockEncoder;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SpdyHeaderBlockRawEncoder
extends SpdyHeaderBlockEncoder {
    private final int version;

    public SpdyHeaderBlockRawEncoder(SpdyVersion version) {
        if (version == null) {
            throw new NullPointerException((String)"version");
        }
        this.version = version.getVersion();
    }

    private static void setLengthField(ByteBuf buffer, int writerIndex, int length) {
        buffer.setInt((int)writerIndex, (int)length);
    }

    private static void writeLengthField(ByteBuf buffer, int length) {
        buffer.writeInt((int)length);
    }

    @Override
    public ByteBuf encode(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
        Set<K> names = frame.headers().names();
        int numHeaders = names.size();
        if (numHeaders == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        if (numHeaders > 65535) {
            throw new IllegalArgumentException((String)"header block contains too many headers");
        }
        ByteBuf headerBlock = alloc.heapBuffer();
        SpdyHeaderBlockRawEncoder.writeLengthField((ByteBuf)headerBlock, (int)numHeaders);
        Iterator<K> iterator = names.iterator();
        while (iterator.hasNext()) {
            CharSequence name = (CharSequence)iterator.next();
            SpdyHeaderBlockRawEncoder.writeLengthField((ByteBuf)headerBlock, (int)name.length());
            ByteBufUtil.writeAscii((ByteBuf)headerBlock, (CharSequence)name);
            int savedIndex = headerBlock.writerIndex();
            int valueLength = 0;
            SpdyHeaderBlockRawEncoder.writeLengthField((ByteBuf)headerBlock, (int)valueLength);
            for (CharSequence value : frame.headers().getAll(name)) {
                int length = value.length();
                if (length <= 0) continue;
                ByteBufUtil.writeAscii((ByteBuf)headerBlock, (CharSequence)value);
                headerBlock.writeByte((int)0);
                valueLength += length + 1;
            }
            if (valueLength != 0) {
                --valueLength;
            }
            if (valueLength > 65535) {
                throw new IllegalArgumentException((String)("header exceeds allowable length: " + name));
            }
            if (valueLength <= 0) continue;
            SpdyHeaderBlockRawEncoder.setLengthField((ByteBuf)headerBlock, (int)savedIndex, (int)valueLength);
            headerBlock.writerIndex((int)(headerBlock.writerIndex() - 1));
        }
        return headerBlock;
    }

    @Override
    void end() {
    }
}

