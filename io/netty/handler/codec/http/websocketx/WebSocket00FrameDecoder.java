/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class WebSocket00FrameDecoder
extends ReplayingDecoder<Void>
implements WebSocketFrameDecoder {
    static final int DEFAULT_MAX_FRAME_SIZE = 16384;
    private final long maxFrameSize;
    private boolean receivedClosingHandshake;

    public WebSocket00FrameDecoder() {
        this((int)16384);
    }

    public WebSocket00FrameDecoder(int maxFrameSize) {
        this.maxFrameSize = (long)maxFrameSize;
    }

    public WebSocket00FrameDecoder(WebSocketDecoderConfig decoderConfig) {
        this.maxFrameSize = (long)ObjectUtil.checkNotNull(decoderConfig, (String)"decoderConfig").maxFramePayloadLength();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.receivedClosingHandshake) {
            in.skipBytes((int)this.actualReadableBytes());
            return;
        }
        byte type = in.readByte();
        WebSocketFrame frame = (type & 128) == 128 ? this.decodeBinaryFrame((ChannelHandlerContext)ctx, (byte)type, (ByteBuf)in) : this.decodeTextFrame((ChannelHandlerContext)ctx, (ByteBuf)in);
        if (frame == null) return;
        out.add((Object)frame);
    }

    private WebSocketFrame decodeBinaryFrame(ChannelHandlerContext ctx, byte type, ByteBuf buffer) {
        byte b;
        long frameSize = 0L;
        int lengthFieldSize = 0;
        do {
            b = buffer.readByte();
            frameSize <<= 7;
            if ((frameSize |= (long)(b & 127)) > this.maxFrameSize) {
                throw new TooLongFrameException();
            }
            if (++lengthFieldSize <= 8) continue;
            throw new TooLongFrameException();
        } while ((b & 128) == 128);
        if (type == -1 && frameSize == 0L) {
            this.receivedClosingHandshake = true;
            return new CloseWebSocketFrame((boolean)true, (int)0, (ByteBuf)ctx.alloc().buffer((int)0));
        }
        ByteBuf payload = ByteBufUtil.readBytes((ByteBufAllocator)ctx.alloc(), (ByteBuf)buffer, (int)((int)frameSize));
        return new BinaryWebSocketFrame((ByteBuf)payload);
    }

    private WebSocketFrame decodeTextFrame(ChannelHandlerContext ctx, ByteBuf buffer) {
        int rbytes;
        int ridx = buffer.readerIndex();
        int delimPos = buffer.indexOf((int)ridx, (int)(ridx + (rbytes = this.actualReadableBytes())), (byte)-1);
        if (delimPos == -1) {
            if ((long)rbytes <= this.maxFrameSize) return null;
            throw new TooLongFrameException();
        }
        int frameSize = delimPos - ridx;
        if ((long)frameSize > this.maxFrameSize) {
            throw new TooLongFrameException();
        }
        ByteBuf binaryData = ByteBufUtil.readBytes((ByteBufAllocator)ctx.alloc(), (ByteBuf)buffer, (int)frameSize);
        buffer.skipBytes((int)1);
        int ffDelimPos = binaryData.indexOf((int)binaryData.readerIndex(), (int)binaryData.writerIndex(), (byte)-1);
        if (ffDelimPos < 0) return new TextWebSocketFrame((ByteBuf)binaryData);
        binaryData.release();
        throw new IllegalArgumentException((String)"a text frame should not contain 0xFF.");
    }
}

