/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class ChunkedFile
implements ChunkedInput<ByteBuf> {
    private final RandomAccessFile file;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;

    public ChunkedFile(File file) throws IOException {
        this((File)file, (int)8192);
    }

    public ChunkedFile(File file, int chunkSize) throws IOException {
        this((RandomAccessFile)new RandomAccessFile((File)file, (String)"r"), (int)chunkSize);
    }

    public ChunkedFile(RandomAccessFile file) throws IOException {
        this((RandomAccessFile)file, (int)8192);
    }

    public ChunkedFile(RandomAccessFile file, int chunkSize) throws IOException {
        this((RandomAccessFile)file, (long)0L, (long)file.length(), (int)chunkSize);
    }

    public ChunkedFile(RandomAccessFile file, long offset, long length, int chunkSize) throws IOException {
        if (file == null) {
            throw new NullPointerException((String)"file");
        }
        if (offset < 0L) {
            throw new IllegalArgumentException((String)("offset: " + offset + " (expected: 0 or greater)"));
        }
        if (length < 0L) {
            throw new IllegalArgumentException((String)("length: " + length + " (expected: 0 or greater)"));
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException((String)("chunkSize: " + chunkSize + " (expected: a positive integer)"));
        }
        this.file = file;
        this.offset = this.startOffset = offset;
        this.endOffset = offset + length;
        this.chunkSize = chunkSize;
        file.seek((long)offset);
    }

    public long startOffset() {
        return this.startOffset;
    }

    public long endOffset() {
        return this.endOffset;
    }

    public long currentOffset() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.offset >= this.endOffset) return true;
        if (!this.file.getChannel().isOpen()) return true;
        return false;
    }

    @Override
    public void close() throws Exception {
        this.file.close();
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
        long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        int chunkSize = (int)Math.min((long)((long)this.chunkSize), (long)(this.endOffset - offset));
        ByteBuf buf = allocator.heapBuffer((int)chunkSize);
        boolean release = true;
        try {
            this.file.readFully((byte[])buf.array(), (int)buf.arrayOffset(), (int)chunkSize);
            buf.writerIndex((int)chunkSize);
            this.offset = offset + (long)chunkSize;
            release = false;
            ByteBuf byteBuf = buf;
            return byteBuf;
        }
        finally {
            if (release) {
                buf.release();
            }
        }
    }

    @Override
    public long length() {
        return this.endOffset - this.startOffset;
    }

    @Override
    public long progress() {
        return this.offset - this.startOffset;
    }
}

