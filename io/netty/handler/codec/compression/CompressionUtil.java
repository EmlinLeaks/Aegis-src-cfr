/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.handler.codec.compression.DecompressionException;
import java.nio.ByteBuffer;

final class CompressionUtil {
    private CompressionUtil() {
    }

    static void checkChecksum(ByteBufChecksum checksum, ByteBuf uncompressed, int currentChecksum) {
        checksum.reset();
        checksum.update((ByteBuf)uncompressed, (int)uncompressed.readerIndex(), (int)uncompressed.readableBytes());
        int checksumResult = (int)checksum.getValue();
        if (checksumResult == currentChecksum) return;
        throw new DecompressionException((String)String.format((String)"stream corrupted: mismatching checksum: %d (expected: %d)", (Object[])new Object[]{Integer.valueOf((int)checksumResult), Integer.valueOf((int)currentChecksum)}));
    }

    static ByteBuffer safeNioBuffer(ByteBuf buffer) {
        ByteBuffer byteBuffer;
        if (buffer.nioBufferCount() == 1) {
            byteBuffer = buffer.internalNioBuffer((int)buffer.readerIndex(), (int)buffer.readableBytes());
            return byteBuffer;
        }
        byteBuffer = buffer.nioBuffer();
        return byteBuffer;
    }

    static ByteBuffer safeNioBuffer(ByteBuf buffer, int index, int length) {
        ByteBuffer byteBuffer;
        if (buffer.nioBufferCount() == 1) {
            byteBuffer = buffer.internalNioBuffer((int)index, (int)length);
            return byteBuffer;
        }
        byteBuffer = buffer.nioBuffer((int)index, (int)length);
        return byteBuffer;
    }
}

