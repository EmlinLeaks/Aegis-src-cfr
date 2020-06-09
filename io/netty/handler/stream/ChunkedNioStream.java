/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChunkedNioStream
implements ChunkedInput<ByteBuf> {
    private final ReadableByteChannel in;
    private final int chunkSize;
    private long offset;
    private final ByteBuffer byteBuffer;

    public ChunkedNioStream(ReadableByteChannel in) {
        this((ReadableByteChannel)in, (int)8192);
    }

    public ChunkedNioStream(ReadableByteChannel in, int chunkSize) {
        if (in == null) {
            throw new NullPointerException((String)"in");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException((String)("chunkSize: " + chunkSize + " (expected: a positive integer)"));
        }
        this.in = in;
        this.offset = 0L;
        this.chunkSize = chunkSize;
        this.byteBuffer = ByteBuffer.allocate((int)chunkSize);
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.byteBuffer.position() > 0) {
            return false;
        }
        if (!this.in.isOpen()) return true;
        int b = this.in.read((ByteBuffer)this.byteBuffer);
        if (b < 0) {
            return true;
        }
        this.offset += (long)b;
        return false;
    }

    @Override
    public void close() throws Exception {
        this.in.close();
    }

    @Deprecated
    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        return this.readChunk((ByteBufAllocator)ctx.alloc());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
        int localReadBytes;
        if (this.isEndOfInput()) {
            return null;
        }
        int readBytes = this.byteBuffer.position();
        while ((localReadBytes = this.in.read((ByteBuffer)this.byteBuffer)) >= 0) {
            this.offset += (long)localReadBytes;
            if ((readBytes += localReadBytes) != this.chunkSize) continue;
        }
        this.byteBuffer.flip();
        boolean release = true;
        ByteBuf buffer = allocator.buffer((int)this.byteBuffer.remaining());
        try {
            buffer.writeBytes((ByteBuffer)this.byteBuffer);
            this.byteBuffer.clear();
            release = false;
            ByteBuf byteBuf = buffer;
            return byteBuf;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }

    @Override
    public long length() {
        return -1L;
    }

    @Override
    public long progress() {
        return this.offset;
    }
}

