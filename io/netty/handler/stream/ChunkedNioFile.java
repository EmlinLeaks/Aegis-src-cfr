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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

public class ChunkedNioFile
implements ChunkedInput<ByteBuf> {
    private final FileChannel in;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;

    public ChunkedNioFile(File in) throws IOException {
        this((FileChannel)new RandomAccessFile((File)in, (String)"r").getChannel());
    }

    public ChunkedNioFile(File in, int chunkSize) throws IOException {
        this((FileChannel)new RandomAccessFile((File)in, (String)"r").getChannel(), (int)chunkSize);
    }

    public ChunkedNioFile(FileChannel in) throws IOException {
        this((FileChannel)in, (int)8192);
    }

    public ChunkedNioFile(FileChannel in, int chunkSize) throws IOException {
        this((FileChannel)in, (long)0L, (long)in.size(), (int)chunkSize);
    }

    public ChunkedNioFile(FileChannel in, long offset, long length, int chunkSize) throws IOException {
        if (in == null) {
            throw new NullPointerException((String)"in");
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
        if (!in.isOpen()) {
            throw new ClosedChannelException();
        }
        this.in = in;
        this.chunkSize = chunkSize;
        this.offset = this.startOffset = offset;
        this.endOffset = offset + length;
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
        if (!this.in.isOpen()) return true;
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
        long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        int chunkSize = (int)Math.min((long)((long)this.chunkSize), (long)(this.endOffset - offset));
        ByteBuf buffer = allocator.buffer((int)chunkSize);
        boolean release = true;
        try {
            int localReadBytes22;
            int readBytes = 0;
            while ((localReadBytes22 = buffer.writeBytes((FileChannel)this.in, (long)(offset + (long)readBytes), (int)(chunkSize - readBytes))) >= 0 && (readBytes += localReadBytes22) != chunkSize) {
            }
            this.offset += (long)readBytes;
            release = false;
            ByteBuf localReadBytes22 = buffer;
            return localReadBytes22;
        }
        finally {
            if (release) {
                buffer.release();
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

