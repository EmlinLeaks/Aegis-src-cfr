/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class DelimiterBasedFrameDecoder
extends ByteToMessageDecoder {
    private final ByteBuf[] delimiters;
    private final int maxFrameLength;
    private final boolean stripDelimiter;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private int tooLongFrameLength;
    private final LineBasedFrameDecoder lineBasedDecoder;

    public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf delimiter) {
        this((int)maxFrameLength, (boolean)true, (ByteBuf)delimiter);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf delimiter) {
        this((int)maxFrameLength, (boolean)stripDelimiter, (boolean)true, (ByteBuf)delimiter);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf delimiter) {
        this((int)maxFrameLength, (boolean)stripDelimiter, (boolean)failFast, (ByteBuf[])new ByteBuf[]{delimiter.slice((int)delimiter.readerIndex(), (int)delimiter.readableBytes())});
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf ... delimiters) {
        this((int)maxFrameLength, (boolean)true, (ByteBuf[])delimiters);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf ... delimiters) {
        this((int)maxFrameLength, (boolean)stripDelimiter, (boolean)true, (ByteBuf[])delimiters);
    }

    public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf ... delimiters) {
        DelimiterBasedFrameDecoder.validateMaxFrameLength((int)maxFrameLength);
        if (delimiters == null) {
            throw new NullPointerException((String)"delimiters");
        }
        if (delimiters.length == 0) {
            throw new IllegalArgumentException((String)"empty delimiters");
        }
        if (DelimiterBasedFrameDecoder.isLineBased((ByteBuf[])delimiters) && !this.isSubclass()) {
            this.lineBasedDecoder = new LineBasedFrameDecoder((int)maxFrameLength, (boolean)stripDelimiter, (boolean)failFast);
            this.delimiters = null;
        } else {
            this.delimiters = new ByteBuf[delimiters.length];
            for (int i = 0; i < delimiters.length; ++i) {
                ByteBuf d = delimiters[i];
                DelimiterBasedFrameDecoder.validateDelimiter((ByteBuf)d);
                this.delimiters[i] = d.slice((int)d.readerIndex(), (int)d.readableBytes());
            }
            this.lineBasedDecoder = null;
        }
        this.maxFrameLength = maxFrameLength;
        this.stripDelimiter = stripDelimiter;
        this.failFast = failFast;
    }

    private static boolean isLineBased(ByteBuf[] delimiters) {
        if (delimiters.length != 2) {
            return false;
        }
        ByteBuf a = delimiters[0];
        ByteBuf b = delimiters[1];
        if (a.capacity() < b.capacity()) {
            a = delimiters[1];
            b = delimiters[0];
        }
        if (a.capacity() != 2) return false;
        if (b.capacity() != 1) return false;
        if (a.getByte((int)0) != 13) return false;
        if (a.getByte((int)1) != 10) return false;
        if (b.getByte((int)0) != 10) return false;
        return true;
    }

    private boolean isSubclass() {
        if (this.getClass() == DelimiterBasedFrameDecoder.class) return false;
        return true;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode((ChannelHandlerContext)ctx, (ByteBuf)in);
        if (decoded == null) return;
        out.add((Object)decoded);
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if (this.lineBasedDecoder != null) {
            return this.lineBasedDecoder.decode((ChannelHandlerContext)ctx, (ByteBuf)buffer);
        }
        int minFrameLength = Integer.MAX_VALUE;
        ByteBuf minDelim = null;
        for (ByteBuf delim : this.delimiters) {
            int frameLength = DelimiterBasedFrameDecoder.indexOf((ByteBuf)buffer, (ByteBuf)delim);
            if (frameLength < 0 || frameLength >= minFrameLength) continue;
            minFrameLength = frameLength;
            minDelim = delim;
        }
        if (minDelim != null) {
            int minDelimLength = minDelim.capacity();
            if (this.discardingTooLongFrame) {
                this.discardingTooLongFrame = false;
                buffer.skipBytes((int)(minFrameLength + minDelimLength));
                int tooLongFrameLength = this.tooLongFrameLength;
                this.tooLongFrameLength = 0;
                if (this.failFast) return null;
                this.fail((long)((long)tooLongFrameLength));
                return null;
            }
            if (minFrameLength > this.maxFrameLength) {
                buffer.skipBytes((int)(minFrameLength + minDelimLength));
                this.fail((long)((long)minFrameLength));
                return null;
            }
            if (!this.stripDelimiter) return buffer.readRetainedSlice((int)(minFrameLength + minDelimLength));
            ByteBuf frame = buffer.readRetainedSlice((int)minFrameLength);
            buffer.skipBytes((int)minDelimLength);
            return frame;
        }
        if (!this.discardingTooLongFrame) {
            if (buffer.readableBytes() <= this.maxFrameLength) return null;
            this.tooLongFrameLength = buffer.readableBytes();
            buffer.skipBytes((int)buffer.readableBytes());
            this.discardingTooLongFrame = true;
            if (!this.failFast) return null;
            this.fail((long)((long)this.tooLongFrameLength));
            return null;
        }
        this.tooLongFrameLength += buffer.readableBytes();
        buffer.skipBytes((int)buffer.readableBytes());
        return null;
    }

    private void fail(long frameLength) {
        if (frameLength <= 0L) throw new TooLongFrameException((String)("frame length exceeds " + this.maxFrameLength + " - discarding"));
        throw new TooLongFrameException((String)("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded"));
    }

    private static int indexOf(ByteBuf haystack, ByteBuf needle) {
        int i = haystack.readerIndex();
        while (i < haystack.writerIndex()) {
            int needleIndex;
            int haystackIndex = i;
            for (needleIndex = 0; needleIndex < needle.capacity() && haystack.getByte((int)haystackIndex) == needle.getByte((int)needleIndex); ++needleIndex) {
                if (++haystackIndex != haystack.writerIndex() || needleIndex == needle.capacity() - 1) continue;
                return -1;
            }
            if (needleIndex == needle.capacity()) {
                return i - haystack.readerIndex();
            }
            ++i;
        }
        return -1;
    }

    private static void validateDelimiter(ByteBuf delimiter) {
        if (delimiter == null) {
            throw new NullPointerException((String)"delimiter");
        }
        if (delimiter.isReadable()) return;
        throw new IllegalArgumentException((String)"empty delimiter");
    }

    private static void validateMaxFrameLength(int maxFrameLength) {
        ObjectUtil.checkPositive((int)maxFrameLength, (String)"maxFrameLength");
    }
}

