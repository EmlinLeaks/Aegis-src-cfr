/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CorruptedWebSocketFrameException;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.Utf8Validator;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameDecoder
extends ByteToMessageDecoder
implements WebSocketFrameDecoder {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private final WebSocketDecoderConfig config;
    private int fragmentedFramesCount;
    private boolean frameFinalFlag;
    private boolean frameMasked;
    private int frameRsv;
    private int frameOpcode;
    private long framePayloadLength;
    private byte[] maskingKey;
    private int framePayloadLen1;
    private boolean receivedClosingHandshake;
    private State state = State.READING_FIRST;

    public WebSocket08FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
        this((boolean)expectMaskedFrames, (boolean)allowExtensions, (int)maxFramePayloadLength, (boolean)false);
    }

    public WebSocket08FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        this((WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().expectMaskedFrames((boolean)expectMaskedFrames).allowExtensions((boolean)allowExtensions).maxFramePayloadLength((int)maxFramePayloadLength).allowMaskMismatch((boolean)allowMaskMismatch).build());
    }

    public WebSocket08FrameDecoder(WebSocketDecoderConfig decoderConfig) {
        this.config = ObjectUtil.checkNotNull(decoderConfig, (String)"decoderConfig");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.receivedClosingHandshake) {
            in.skipBytes((int)this.actualReadableBytes());
            return;
        }
        switch (1.$SwitchMap$io$netty$handler$codec$http$websocketx$WebSocket08FrameDecoder$State[this.state.ordinal()]) {
            case 1: {
                if (!in.isReadable()) {
                    return;
                }
                this.framePayloadLength = 0L;
                byte b = in.readByte();
                this.frameFinalFlag = (b & 128) != 0;
                this.frameRsv = (b & 112) >> 4;
                this.frameOpcode = b & 15;
                if (logger.isTraceEnabled()) {
                    logger.trace((String)"Decoding WebSocket Frame opCode={}", (Object)Integer.valueOf((int)this.frameOpcode));
                }
                this.state = State.READING_SECOND;
            }
            case 2: {
                if (!in.isReadable()) {
                    return;
                }
                byte b = in.readByte();
                this.frameMasked = (b & 128) != 0;
                this.framePayloadLen1 = b & 127;
                if (this.frameRsv != 0 && !this.config.allowExtensions()) {
                    this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)("RSV != 0 and no extension negotiated, RSV:" + this.frameRsv));
                    return;
                }
                if (!this.config.allowMaskMismatch() && this.config.expectMaskedFrames() != this.frameMasked) {
                    this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"received a frame that is not masked as expected");
                    return;
                }
                if (this.frameOpcode > 7) {
                    if (!this.frameFinalFlag) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"fragmented control frame");
                        return;
                    }
                    if (this.framePayloadLen1 > 125) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"control frame with payload length > 125 octets");
                        return;
                    }
                    if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)("control frame using reserved opcode " + this.frameOpcode));
                        return;
                    }
                    if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"received close control frame with payload len 1");
                        return;
                    }
                } else {
                    if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)("data frame using reserved opcode " + this.frameOpcode));
                        return;
                    }
                    if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"received continuation data frame outside fragmented message");
                        return;
                    }
                    if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0 && this.frameOpcode != 9) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"received non-continuation data frame while inside fragmented message");
                        return;
                    }
                }
                this.state = State.READING_SIZE;
            }
            case 3: {
                if (this.framePayloadLen1 == 126) {
                    if (in.readableBytes() < 2) {
                        return;
                    }
                    this.framePayloadLength = (long)in.readUnsignedShort();
                    if (this.framePayloadLength < 126L) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"invalid data frame length (not using minimal length encoding)");
                        return;
                    }
                } else if (this.framePayloadLen1 == 127) {
                    if (in.readableBytes() < 8) {
                        return;
                    }
                    this.framePayloadLength = in.readLong();
                    if (this.framePayloadLength < 65536L) {
                        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (String)"invalid data frame length (not using minimal length encoding)");
                        return;
                    }
                } else {
                    this.framePayloadLength = (long)this.framePayloadLen1;
                }
                if (this.framePayloadLength > (long)this.config.maxFramePayloadLength()) {
                    this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (WebSocketCloseStatus)WebSocketCloseStatus.MESSAGE_TOO_BIG, (String)("Max frame length of " + this.config.maxFramePayloadLength() + " has been exceeded."));
                    return;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace((String)"Decoding WebSocket Frame length={}", (Object)Long.valueOf((long)this.framePayloadLength));
                }
                this.state = State.MASKING_KEY;
            }
            case 4: {
                if (this.frameMasked) {
                    if (in.readableBytes() < 4) {
                        return;
                    }
                    if (this.maskingKey == null) {
                        this.maskingKey = new byte[4];
                    }
                    in.readBytes((byte[])this.maskingKey);
                }
                this.state = State.PAYLOAD;
            }
            case 5: {
                if ((long)in.readableBytes() < this.framePayloadLength) {
                    return;
                }
                ReferenceCounted payloadBuffer = null;
                try {
                    payloadBuffer = ByteBufUtil.readBytes((ByteBufAllocator)ctx.alloc(), (ByteBuf)in, (int)WebSocket08FrameDecoder.toFrameLength((long)this.framePayloadLength));
                    this.state = State.READING_FIRST;
                    if (this.frameMasked) {
                        this.unmask((ByteBuf)payloadBuffer);
                    }
                    if (this.frameOpcode == 9) {
                        out.add((Object)new PingWebSocketFrame((boolean)this.frameFinalFlag, (int)this.frameRsv, (ByteBuf)payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode == 10) {
                        out.add((Object)new PongWebSocketFrame((boolean)this.frameFinalFlag, (int)this.frameRsv, (ByteBuf)payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode == 8) {
                        this.receivedClosingHandshake = true;
                        this.checkCloseFrameBody((ChannelHandlerContext)ctx, (ByteBuf)payloadBuffer);
                        out.add((Object)new CloseWebSocketFrame((boolean)this.frameFinalFlag, (int)this.frameRsv, (ByteBuf)payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameFinalFlag) {
                        if (this.frameOpcode != 9) {
                            this.fragmentedFramesCount = 0;
                        }
                    } else {
                        ++this.fragmentedFramesCount;
                    }
                    if (this.frameOpcode == 1) {
                        out.add((Object)new TextWebSocketFrame((boolean)this.frameFinalFlag, (int)this.frameRsv, (ByteBuf)payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode == 2) {
                        out.add((Object)new BinaryWebSocketFrame((boolean)this.frameFinalFlag, (int)this.frameRsv, (ByteBuf)payloadBuffer));
                        payloadBuffer = null;
                        return;
                    }
                    if (this.frameOpcode != 0) throw new UnsupportedOperationException((String)("Cannot decode web socket frame with opcode: " + this.frameOpcode));
                    out.add((Object)new ContinuationWebSocketFrame((boolean)this.frameFinalFlag, (int)this.frameRsv, (ByteBuf)payloadBuffer));
                    payloadBuffer = null;
                    return;
                }
                finally {
                    if (payloadBuffer != null) {
                        payloadBuffer.release();
                    }
                }
            }
            case 6: {
                if (!in.isReadable()) return;
                in.readByte();
                return;
            }
        }
        throw new Error((String)"Shouldn't reach here.");
    }

    private void unmask(ByteBuf frame) {
        int i = frame.readerIndex();
        int end = frame.writerIndex();
        ByteOrder order = frame.order();
        int intMask = (this.maskingKey[0] & 255) << 24 | (this.maskingKey[1] & 255) << 16 | (this.maskingKey[2] & 255) << 8 | this.maskingKey[3] & 255;
        if (order == ByteOrder.LITTLE_ENDIAN) {
            intMask = Integer.reverseBytes((int)intMask);
        }
        do {
            if (i + 3 >= end) {
                while (i < end) {
                    frame.setByte((int)i, (int)(frame.getByte((int)i) ^ this.maskingKey[i % 4]));
                    ++i;
                }
                return;
            }
            int unmasked = frame.getInt((int)i) ^ intMask;
            frame.setInt((int)i, (int)unmasked);
            i += 4;
        } while (true);
    }

    private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, String reason) {
        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (WebSocketCloseStatus)WebSocketCloseStatus.PROTOCOL_ERROR, (String)reason);
    }

    private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, WebSocketCloseStatus status, String reason) {
        this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)in, (CorruptedWebSocketFrameException)new CorruptedWebSocketFrameException((WebSocketCloseStatus)status, (String)reason));
    }

    private void protocolViolation(ChannelHandlerContext ctx, ByteBuf in, CorruptedWebSocketFrameException ex) {
        ReferenceCounted closeMessage;
        this.state = State.CORRUPT;
        int readableBytes = in.readableBytes();
        if (readableBytes > 0) {
            in.skipBytes((int)readableBytes);
        }
        if (!ctx.channel().isActive()) throw ex;
        if (!this.config.closeOnProtocolViolation()) throw ex;
        if (this.receivedClosingHandshake) {
            closeMessage = Unpooled.EMPTY_BUFFER;
        } else {
            WebSocketCloseStatus closeStatus = ex.closeStatus();
            String reasonText = ex.getMessage();
            if (reasonText == null) {
                reasonText = closeStatus.reasonText();
            }
            closeMessage = new CloseWebSocketFrame((WebSocketCloseStatus)closeStatus, (String)reasonText);
        }
        ctx.writeAndFlush((Object)closeMessage).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        throw ex;
    }

    private static int toFrameLength(long l) {
        if (l <= Integer.MAX_VALUE) return (int)l;
        throw new TooLongFrameException((String)("Length:" + l));
    }

    protected void checkCloseFrameBody(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (buffer == null) return;
        if (!buffer.isReadable()) {
            return;
        }
        if (buffer.readableBytes() == 1) {
            this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)buffer, (WebSocketCloseStatus)WebSocketCloseStatus.INVALID_PAYLOAD_DATA, (String)"Invalid close frame body");
        }
        int idx = buffer.readerIndex();
        buffer.readerIndex((int)0);
        short statusCode = buffer.readShort();
        if (!WebSocketCloseStatus.isValidStatusCode((int)statusCode)) {
            this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)buffer, (String)("Invalid close frame getStatus code: " + statusCode));
        }
        if (buffer.isReadable()) {
            try {
                new Utf8Validator().check((ByteBuf)buffer);
            }
            catch (CorruptedWebSocketFrameException ex) {
                this.protocolViolation((ChannelHandlerContext)ctx, (ByteBuf)buffer, (CorruptedWebSocketFrameException)ex);
            }
        }
        buffer.readerIndex((int)idx);
    }
}

