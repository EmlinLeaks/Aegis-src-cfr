/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class ProtobufVarint32FrameDecoder
extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int preIndex = in.readerIndex();
        int length = ProtobufVarint32FrameDecoder.readRawVarint32((ByteBuf)in);
        if (preIndex == in.readerIndex()) {
            return;
        }
        if (length < 0) {
            throw new CorruptedFrameException((String)("negative length: " + length));
        }
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        out.add((Object)in.readRetainedSlice((int)length));
    }

    private static int readRawVarint32(ByteBuf buffer) {
        if (!buffer.isReadable()) {
            return 0;
        }
        buffer.markReaderIndex();
        byte tmp = buffer.readByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 127;
        if (!buffer.isReadable()) {
            buffer.resetReaderIndex();
            return 0;
        }
        tmp = buffer.readByte();
        if (tmp >= 0) {
            return result |= tmp << 7;
        }
        result |= (tmp & 127) << 7;
        if (!buffer.isReadable()) {
            buffer.resetReaderIndex();
            return 0;
        }
        tmp = buffer.readByte();
        if (tmp >= 0) {
            return result |= tmp << 14;
        }
        result |= (tmp & 127) << 14;
        if (!buffer.isReadable()) {
            buffer.resetReaderIndex();
            return 0;
        }
        tmp = buffer.readByte();
        if (tmp >= 0) {
            return result |= tmp << 21;
        }
        result |= (tmp & 127) << 21;
        if (!buffer.isReadable()) {
            buffer.resetReaderIndex();
            return 0;
        }
        tmp = buffer.readByte();
        result |= tmp << 28;
        if (tmp >= 0) return result;
        throw new CorruptedFrameException((String)"malformed varint.");
    }
}

