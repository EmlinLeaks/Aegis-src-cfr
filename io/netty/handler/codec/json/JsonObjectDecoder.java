/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.json;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class JsonObjectDecoder
extends ByteToMessageDecoder {
    private static final int ST_CORRUPTED = -1;
    private static final int ST_INIT = 0;
    private static final int ST_DECODING_NORMAL = 1;
    private static final int ST_DECODING_ARRAY_STREAM = 2;
    private int openBraces;
    private int idx;
    private int lastReaderIndex;
    private int state;
    private boolean insideString;
    private final int maxObjectLength;
    private final boolean streamArrayElements;

    public JsonObjectDecoder() {
        this((int)1048576);
    }

    public JsonObjectDecoder(int maxObjectLength) {
        this((int)maxObjectLength, (boolean)false);
    }

    public JsonObjectDecoder(boolean streamArrayElements) {
        this((int)1048576, (boolean)streamArrayElements);
    }

    public JsonObjectDecoder(int maxObjectLength, boolean streamArrayElements) {
        if (maxObjectLength < 1) {
            throw new IllegalArgumentException((String)"maxObjectLength must be a positive int");
        }
        this.maxObjectLength = maxObjectLength;
        this.streamArrayElements = streamArrayElements;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int idx;
        int wrtIdx;
        if (this.state == -1) {
            in.skipBytes((int)in.readableBytes());
            return;
        }
        if (this.idx > in.readerIndex() && this.lastReaderIndex != in.readerIndex()) {
            this.idx = in.readerIndex() + (this.idx - this.lastReaderIndex);
        }
        if ((wrtIdx = in.writerIndex()) > this.maxObjectLength) {
            in.skipBytes((int)in.readableBytes());
            this.reset();
            throw new TooLongFrameException((String)("object length exceeds " + this.maxObjectLength + ": " + wrtIdx + " bytes discarded"));
        }
        for (idx = this.idx; idx < wrtIdx; ++idx) {
            byte c = in.getByte((int)idx);
            if (this.state == 1) {
                this.decodeByte((byte)c, (ByteBuf)in, (int)idx);
                if (this.openBraces != 0) continue;
                ByteBuf json = this.extractObject((ChannelHandlerContext)ctx, (ByteBuf)in, (int)in.readerIndex(), (int)(idx + 1 - in.readerIndex()));
                if (json != null) {
                    out.add((Object)json);
                }
                in.readerIndex((int)(idx + 1));
                this.reset();
                continue;
            }
            if (this.state == 2) {
                int idxNoSpaces;
                this.decodeByte((byte)c, (ByteBuf)in, (int)idx);
                if (this.insideString || (this.openBraces != 1 || c != 44) && (this.openBraces != 0 || c != 93)) continue;
                int i = in.readerIndex();
                while (Character.isWhitespace((int)in.getByte((int)i))) {
                    in.skipBytes((int)1);
                    ++i;
                }
                for (idxNoSpaces = idx - 1; idxNoSpaces >= in.readerIndex() && Character.isWhitespace((int)in.getByte((int)idxNoSpaces)); --idxNoSpaces) {
                }
                ByteBuf json = this.extractObject((ChannelHandlerContext)ctx, (ByteBuf)in, (int)in.readerIndex(), (int)(idxNoSpaces + 1 - in.readerIndex()));
                if (json != null) {
                    out.add((Object)json);
                }
                in.readerIndex((int)(idx + 1));
                if (c != 93) continue;
                this.reset();
                continue;
            }
            if (c == 123 || c == 91) {
                this.initDecoding((byte)c);
                if (this.state != 2) continue;
                in.skipBytes((int)1);
                continue;
            }
            if (!Character.isWhitespace((int)c)) {
                this.state = -1;
                throw new CorruptedFrameException((String)("invalid JSON received at byte position " + idx + ": " + ByteBufUtil.hexDump((ByteBuf)in)));
            }
            in.skipBytes((int)1);
        }
        this.idx = in.readableBytes() == 0 ? 0 : idx;
        this.lastReaderIndex = in.readerIndex();
    }

    protected ByteBuf extractObject(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.retainedSlice((int)index, (int)length);
    }

    private void decodeByte(byte c, ByteBuf in, int idx) {
        if (!(c != 123 && c != 91 || this.insideString)) {
            ++this.openBraces;
            return;
        }
        if (!(c != 125 && c != 93 || this.insideString)) {
            --this.openBraces;
            return;
        }
        if (c != 34) return;
        if (!this.insideString) {
            this.insideString = true;
            return;
        }
        int backslashCount = 0;
        --idx;
        while (idx >= 0 && in.getByte((int)idx) == 92) {
            ++backslashCount;
            --idx;
        }
        if (backslashCount % 2 != 0) return;
        this.insideString = false;
    }

    private void initDecoding(byte openingBrace) {
        this.openBraces = 1;
        if (openingBrace == 91 && this.streamArrayElements) {
            this.state = 2;
            return;
        }
        this.state = 1;
    }

    private void reset() {
        this.insideString = false;
        this.state = 0;
        this.openBraces = 0;
    }
}

