/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedInput;

public class HttpChunkedInput
implements ChunkedInput<HttpContent> {
    private final ChunkedInput<ByteBuf> input;
    private final LastHttpContent lastHttpContent;
    private boolean sentLastChunk;

    public HttpChunkedInput(ChunkedInput<ByteBuf> input) {
        this.input = input;
        this.lastHttpContent = LastHttpContent.EMPTY_LAST_CONTENT;
    }

    public HttpChunkedInput(ChunkedInput<ByteBuf> input, LastHttpContent lastHttpContent) {
        this.input = input;
        this.lastHttpContent = lastHttpContent;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (!this.input.isEndOfInput()) return false;
        return this.sentLastChunk;
    }

    @Override
    public void close() throws Exception {
        this.input.close();
    }

    @Deprecated
    @Override
    public HttpContent readChunk(ChannelHandlerContext ctx) throws Exception {
        return this.readChunk((ByteBufAllocator)ctx.alloc());
    }

    @Override
    public HttpContent readChunk(ByteBufAllocator allocator) throws Exception {
        if (!this.input.isEndOfInput()) {
            ByteBuf buf = this.input.readChunk((ByteBufAllocator)allocator);
            if (buf != null) return new DefaultHttpContent((ByteBuf)buf);
            return null;
        }
        if (this.sentLastChunk) {
            return null;
        }
        this.sentLastChunk = true;
        return this.lastHttpContent;
    }

    @Override
    public long length() {
        return this.input.length();
    }

    @Override
    public long progress() {
        return this.input.progress();
    }
}

