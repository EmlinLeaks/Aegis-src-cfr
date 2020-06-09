/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import java.util.List;

public class LineBasedFrameDecoder
extends ByteToMessageDecoder {
    private final int maxLength;
    private final boolean failFast;
    private final boolean stripDelimiter;
    private boolean discarding;
    private int discardedBytes;
    private int offset;

    public LineBasedFrameDecoder(int maxLength) {
        this((int)maxLength, (boolean)true, (boolean)false);
    }

    public LineBasedFrameDecoder(int maxLength, boolean stripDelimiter, boolean failFast) {
        this.maxLength = maxLength;
        this.failFast = failFast;
        this.stripDelimiter = stripDelimiter;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode((ChannelHandlerContext)ctx, (ByteBuf)in);
        if (decoded == null) return;
        out.add((Object)decoded);
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        int eol = this.findEndOfLine((ByteBuf)buffer);
        if (!this.discarding) {
            int delimLength;
            if (eol < 0) {
                int length = buffer.readableBytes();
                if (length <= this.maxLength) return null;
                this.discardedBytes = length;
                buffer.readerIndex((int)buffer.writerIndex());
                this.discarding = true;
                this.offset = 0;
                if (!this.failFast) return null;
                this.fail((ChannelHandlerContext)ctx, (String)("over " + this.discardedBytes));
                return null;
            }
            int length = eol - buffer.readerIndex();
            int n = delimLength = buffer.getByte((int)eol) == 13 ? 2 : 1;
            if (length > this.maxLength) {
                buffer.readerIndex((int)(eol + delimLength));
                this.fail((ChannelHandlerContext)ctx, (int)length);
                return null;
            }
            if (!this.stripDelimiter) return buffer.readRetainedSlice((int)(length + delimLength));
            ByteBuf frame = buffer.readRetainedSlice((int)length);
            buffer.skipBytes((int)delimLength);
            return frame;
        }
        if (eol < 0) {
            this.discardedBytes += buffer.readableBytes();
            buffer.readerIndex((int)buffer.writerIndex());
            this.offset = 0;
            return null;
        }
        int length = this.discardedBytes + eol - buffer.readerIndex();
        int delimLength = buffer.getByte((int)eol) == 13 ? 2 : 1;
        buffer.readerIndex((int)(eol + delimLength));
        this.discardedBytes = 0;
        this.discarding = false;
        if (this.failFast) return null;
        this.fail((ChannelHandlerContext)ctx, (int)length);
        return null;
    }

    private void fail(ChannelHandlerContext ctx, int length) {
        this.fail((ChannelHandlerContext)ctx, (String)String.valueOf((int)length));
    }

    private void fail(ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught((Throwable)new TooLongFrameException((String)("frame length (" + length + ") exceeds the allowed maximum (" + this.maxLength + ')')));
    }

    private int findEndOfLine(ByteBuf buffer) {
        int totalLength = buffer.readableBytes();
        int i = buffer.forEachByte((int)(buffer.readerIndex() + this.offset), (int)(totalLength - this.offset), (ByteProcessor)ByteProcessor.FIND_LF);
        if (i >= 0) {
            this.offset = 0;
            if (i <= 0) return i;
            if (buffer.getByte((int)(i - 1)) != 13) return i;
            return --i;
        }
        this.offset = totalLength;
        return i;
    }
}

