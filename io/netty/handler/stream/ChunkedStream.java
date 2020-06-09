/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class ChunkedStream
implements ChunkedInput<ByteBuf> {
    static final int DEFAULT_CHUNK_SIZE = 8192;
    private final PushbackInputStream in;
    private final int chunkSize;
    private long offset;
    private boolean closed;

    public ChunkedStream(InputStream in) {
        this((InputStream)in, (int)8192);
    }

    public ChunkedStream(InputStream in, int chunkSize) {
        if (in == null) {
            throw new NullPointerException((String)"in");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException((String)("chunkSize: " + chunkSize + " (expected: a positive integer)"));
        }
        this.in = in instanceof PushbackInputStream ? (PushbackInputStream)in : new PushbackInputStream((InputStream)in);
        this.chunkSize = chunkSize;
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.closed) {
            return true;
        }
        int b = this.in.read();
        if (b < 0) {
            return true;
        }
        this.in.unread((int)b);
        return false;
    }

    @Override
    public void close() throws Exception {
        this.closed = true;
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
        if (this.isEndOfInput()) {
            return null;
        }
        int availableBytes = this.in.available();
        int chunkSize = availableBytes <= 0 ? this.chunkSize : Math.min((int)this.chunkSize, (int)this.in.available());
        boolean release = true;
        ByteBuf buffer = allocator.buffer((int)chunkSize);
        try {
            this.offset += (long)buffer.writeBytes((InputStream)this.in, (int)chunkSize);
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

