/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameEncoder
extends MessageToMessageEncoder<WebSocketFrame>
implements WebSocketFrameEncoder {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private static final int GATHERING_WRITE_THRESHOLD = 1024;
    private final boolean maskPayload;

    public WebSocket08FrameEncoder(boolean maskPayload) {
        this.maskPayload = maskPayload;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        int opcode;
        ByteBuf data = msg.content();
        if (msg instanceof TextWebSocketFrame) {
            opcode = 1;
        } else if (msg instanceof PingWebSocketFrame) {
            opcode = 9;
        } else if (msg instanceof PongWebSocketFrame) {
            opcode = 10;
        } else if (msg instanceof CloseWebSocketFrame) {
            opcode = 8;
        } else if (msg instanceof BinaryWebSocketFrame) {
            opcode = 2;
        } else {
            if (!(msg instanceof ContinuationWebSocketFrame)) throw new UnsupportedOperationException((String)("Cannot encode frame of type: " + msg.getClass().getName()));
            opcode = 0;
        }
        int length = data.readableBytes();
        if (logger.isTraceEnabled()) {
            logger.trace((String)"Encoding WebSocket Frame opCode={} length={}", (Object)Byte.valueOf((byte)opcode), (Object)Integer.valueOf((int)length));
        }
        int b0 = 0;
        if (msg.isFinalFragment()) {
            b0 |= 128;
        }
        b0 |= msg.rsv() % 8 << 4;
        b0 |= opcode % 128;
        if (opcode == 9 && length > 125) {
            throw new TooLongFrameException((String)("invalid payload for PING (payload length must be <= 125, was " + length));
        }
        boolean release = true;
        ReferenceCounted buf = null;
        try {
            int size;
            int maskLength;
            int n = maskLength = this.maskPayload ? 4 : 0;
            if (length <= 125) {
                size = 2 + maskLength;
                if (this.maskPayload || length <= 1024) {
                    size += length;
                }
                buf = ctx.alloc().buffer((int)size);
                ((ByteBuf)buf).writeByte((int)b0);
                byte b = (byte)(this.maskPayload ? (byte)(128 | (byte)length) : (byte)length);
                ((ByteBuf)buf).writeByte((int)b);
            } else if (length <= 65535) {
                size = 4 + maskLength;
                if (this.maskPayload || length <= 1024) {
                    size += length;
                }
                buf = ctx.alloc().buffer((int)size);
                ((ByteBuf)buf).writeByte((int)b0);
                ((ByteBuf)buf).writeByte((int)(this.maskPayload ? 254 : 126));
                ((ByteBuf)buf).writeByte((int)(length >>> 8 & 255));
                ((ByteBuf)buf).writeByte((int)(length & 255));
            } else {
                size = 10 + maskLength;
                if (this.maskPayload || length <= 1024) {
                    size += length;
                }
                buf = ctx.alloc().buffer((int)size);
                ((ByteBuf)buf).writeByte((int)b0);
                ((ByteBuf)buf).writeByte((int)(this.maskPayload ? 255 : 127));
                ((ByteBuf)buf).writeLong((long)((long)length));
            }
            if (this.maskPayload) {
                int random = (int)(Math.random() * 2.147483647E9);
                byte[] mask = ByteBuffer.allocate((int)4).putInt((int)random).array();
                ((ByteBuf)buf).writeBytes((byte[])mask);
                ByteOrder srcOrder = data.order();
                ByteOrder dstOrder = ((ByteBuf)buf).order();
                int counter = 0;
                int i = data.readerIndex();
                int end = data.writerIndex();
                if (srcOrder == dstOrder) {
                    int intMask = (mask[0] & 255) << 24 | (mask[1] & 255) << 16 | (mask[2] & 255) << 8 | mask[3] & 255;
                    if (srcOrder == ByteOrder.LITTLE_ENDIAN) {
                        intMask = Integer.reverseBytes((int)intMask);
                    }
                    while (i + 3 < end) {
                        int intData = data.getInt((int)i);
                        ((ByteBuf)buf).writeInt((int)(intData ^ intMask));
                        i += 4;
                    }
                }
                while (i < end) {
                    byte byteData = data.getByte((int)i);
                    ((ByteBuf)buf).writeByte((int)(byteData ^ mask[counter++ % 4]));
                    ++i;
                }
                out.add((Object)buf);
            } else if (((ByteBuf)buf).writableBytes() >= data.readableBytes()) {
                ((ByteBuf)buf).writeBytes((ByteBuf)data);
                out.add((Object)buf);
            } else {
                out.add((Object)buf);
                out.add((Object)data.retain());
            }
            release = false;
            return;
        }
        finally {
            if (release && buf != null) {
                buf.release();
            }
        }
    }
}

