/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Set;

public class SpdyFrameEncoder {
    private final int version;

    public SpdyFrameEncoder(SpdyVersion spdyVersion) {
        if (spdyVersion == null) {
            throw new NullPointerException((String)"spdyVersion");
        }
        this.version = spdyVersion.getVersion();
    }

    private void writeControlFrameHeader(ByteBuf buffer, int type, byte flags, int length) {
        buffer.writeShort((int)(this.version | 32768));
        buffer.writeShort((int)type);
        buffer.writeByte((int)flags);
        buffer.writeMedium((int)length);
    }

    public ByteBuf encodeDataFrame(ByteBufAllocator allocator, int streamId, boolean last, ByteBuf data) {
        int flags = last ? 1 : 0;
        int length = data.readableBytes();
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        frame.writeInt((int)(streamId & Integer.MAX_VALUE));
        frame.writeByte((int)flags);
        frame.writeMedium((int)length);
        frame.writeBytes((ByteBuf)data, (int)data.readerIndex(), (int)length);
        return frame;
    }

    public ByteBuf encodeSynStreamFrame(ByteBufAllocator allocator, int streamId, int associatedToStreamId, byte priority, boolean last, boolean unidirectional, ByteBuf headerBlock) {
        byte flags;
        int headerBlockLength = headerBlock.readableBytes();
        byte by = flags = last ? (byte)1 : 0;
        if (unidirectional) {
            flags = (byte)((byte)(flags | 2));
        }
        int length = 10 + headerBlockLength;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)1, (byte)flags, (int)length);
        frame.writeInt((int)streamId);
        frame.writeInt((int)associatedToStreamId);
        frame.writeShort((int)((priority & 255) << 13));
        frame.writeBytes((ByteBuf)headerBlock, (int)headerBlock.readerIndex(), (int)headerBlockLength);
        return frame;
    }

    public ByteBuf encodeSynReplyFrame(ByteBufAllocator allocator, int streamId, boolean last, ByteBuf headerBlock) {
        int headerBlockLength = headerBlock.readableBytes();
        byte flags = last ? (byte)1 : 0;
        int length = 4 + headerBlockLength;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)2, (byte)flags, (int)length);
        frame.writeInt((int)streamId);
        frame.writeBytes((ByteBuf)headerBlock, (int)headerBlock.readerIndex(), (int)headerBlockLength);
        return frame;
    }

    public ByteBuf encodeRstStreamFrame(ByteBufAllocator allocator, int streamId, int statusCode) {
        byte flags = 0;
        int length = 8;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)3, (byte)flags, (int)length);
        frame.writeInt((int)streamId);
        frame.writeInt((int)statusCode);
        return frame;
    }

    public ByteBuf encodeSettingsFrame(ByteBufAllocator allocator, SpdySettingsFrame spdySettingsFrame) {
        Set<Integer> ids = spdySettingsFrame.ids();
        int numSettings = ids.size();
        byte flags = spdySettingsFrame.clearPreviouslyPersistedSettings() ? (byte)1 : 0;
        int length = 4 + 8 * numSettings;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)4, (byte)flags, (int)length);
        frame.writeInt((int)numSettings);
        Iterator<Integer> iterator = ids.iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            flags = 0;
            if (spdySettingsFrame.isPersistValue((int)id.intValue())) {
                flags = (byte)(flags | 1);
            }
            if (spdySettingsFrame.isPersisted((int)id.intValue())) {
                flags = (byte)(flags | 2);
            }
            frame.writeByte((int)flags);
            frame.writeMedium((int)id.intValue());
            frame.writeInt((int)spdySettingsFrame.getValue((int)id.intValue()));
        }
        return frame;
    }

    public ByteBuf encodePingFrame(ByteBufAllocator allocator, int id) {
        byte flags = 0;
        int length = 4;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)6, (byte)flags, (int)length);
        frame.writeInt((int)id);
        return frame;
    }

    public ByteBuf encodeGoAwayFrame(ByteBufAllocator allocator, int lastGoodStreamId, int statusCode) {
        byte flags = 0;
        int length = 8;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)7, (byte)flags, (int)length);
        frame.writeInt((int)lastGoodStreamId);
        frame.writeInt((int)statusCode);
        return frame;
    }

    public ByteBuf encodeHeadersFrame(ByteBufAllocator allocator, int streamId, boolean last, ByteBuf headerBlock) {
        int headerBlockLength = headerBlock.readableBytes();
        byte flags = last ? (byte)1 : 0;
        int length = 4 + headerBlockLength;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)8, (byte)flags, (int)length);
        frame.writeInt((int)streamId);
        frame.writeBytes((ByteBuf)headerBlock, (int)headerBlock.readerIndex(), (int)headerBlockLength);
        return frame;
    }

    public ByteBuf encodeWindowUpdateFrame(ByteBufAllocator allocator, int streamId, int deltaWindowSize) {
        byte flags = 0;
        int length = 8;
        ByteBuf frame = allocator.ioBuffer((int)(8 + length)).order((ByteOrder)ByteOrder.BIG_ENDIAN);
        this.writeControlFrameHeader((ByteBuf)frame, (int)9, (byte)flags, (int)length);
        frame.writeInt((int)streamId);
        frame.writeInt((int)deltaWindowSize);
        return frame;
    }
}

