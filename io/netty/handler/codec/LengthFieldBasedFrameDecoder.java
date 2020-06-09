/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;
import java.util.List;

public class LengthFieldBasedFrameDecoder
extends ByteToMessageDecoder {
    private final ByteOrder byteOrder;
    private final int maxFrameLength;
    private final int lengthFieldOffset;
    private final int lengthFieldLength;
    private final int lengthFieldEndOffset;
    private final int lengthAdjustment;
    private final int initialBytesToStrip;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private long tooLongFrameLength;
    private long bytesToDiscard;

    public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        this((int)maxFrameLength, (int)lengthFieldOffset, (int)lengthFieldLength, (int)0, (int)0);
    }

    public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        this((int)maxFrameLength, (int)lengthFieldOffset, (int)lengthFieldLength, (int)lengthAdjustment, (int)initialBytesToStrip, (boolean)true);
    }

    public LengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        this((ByteOrder)ByteOrder.BIG_ENDIAN, (int)maxFrameLength, (int)lengthFieldOffset, (int)lengthFieldLength, (int)lengthAdjustment, (int)initialBytesToStrip, (boolean)failFast);
    }

    public LengthFieldBasedFrameDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        if (byteOrder == null) {
            throw new NullPointerException((String)"byteOrder");
        }
        ObjectUtil.checkPositive((int)maxFrameLength, (String)"maxFrameLength");
        ObjectUtil.checkPositiveOrZero((int)lengthFieldOffset, (String)"lengthFieldOffset");
        ObjectUtil.checkPositiveOrZero((int)initialBytesToStrip, (String)"initialBytesToStrip");
        if (lengthFieldOffset > maxFrameLength - lengthFieldLength) {
            throw new IllegalArgumentException((String)("maxFrameLength (" + maxFrameLength + ") must be equal to or greater than lengthFieldOffset (" + lengthFieldOffset + ") + lengthFieldLength (" + lengthFieldLength + ")."));
        }
        this.byteOrder = byteOrder;
        this.maxFrameLength = maxFrameLength;
        this.lengthFieldOffset = lengthFieldOffset;
        this.lengthFieldLength = lengthFieldLength;
        this.lengthAdjustment = lengthAdjustment;
        this.lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
        this.initialBytesToStrip = initialBytesToStrip;
        this.failFast = failFast;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode((ChannelHandlerContext)ctx, (ByteBuf)in);
        if (decoded == null) return;
        out.add((Object)decoded);
    }

    private void discardingTooLongFrame(ByteBuf in) {
        long bytesToDiscard = this.bytesToDiscard;
        int localBytesToDiscard = (int)Math.min((long)bytesToDiscard, (long)((long)in.readableBytes()));
        in.skipBytes((int)localBytesToDiscard);
        this.bytesToDiscard = bytesToDiscard -= (long)localBytesToDiscard;
        this.failIfNecessary((boolean)false);
    }

    private static void failOnNegativeLengthField(ByteBuf in, long frameLength, int lengthFieldEndOffset) {
        in.skipBytes((int)lengthFieldEndOffset);
        throw new CorruptedFrameException((String)("negative pre-adjustment length field: " + frameLength));
    }

    private static void failOnFrameLengthLessThanLengthFieldEndOffset(ByteBuf in, long frameLength, int lengthFieldEndOffset) {
        in.skipBytes((int)lengthFieldEndOffset);
        throw new CorruptedFrameException((String)("Adjusted frame length (" + frameLength + ") is less than lengthFieldEndOffset: " + lengthFieldEndOffset));
    }

    private void exceededFrameLength(ByteBuf in, long frameLength) {
        long discard = frameLength - (long)in.readableBytes();
        this.tooLongFrameLength = frameLength;
        if (discard < 0L) {
            in.skipBytes((int)((int)frameLength));
        } else {
            this.discardingTooLongFrame = true;
            this.bytesToDiscard = discard;
            in.skipBytes((int)in.readableBytes());
        }
        this.failIfNecessary((boolean)true);
    }

    private static void failOnFrameLengthLessThanInitialBytesToStrip(ByteBuf in, long frameLength, int initialBytesToStrip) {
        in.skipBytes((int)((int)frameLength));
        throw new CorruptedFrameException((String)("Adjusted frame length (" + frameLength + ") is less than initialBytesToStrip: " + initialBytesToStrip));
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (this.discardingTooLongFrame) {
            this.discardingTooLongFrame((ByteBuf)in);
        }
        if (in.readableBytes() < this.lengthFieldEndOffset) {
            return null;
        }
        int actualLengthFieldOffset = in.readerIndex() + this.lengthFieldOffset;
        long frameLength = this.getUnadjustedFrameLength((ByteBuf)in, (int)actualLengthFieldOffset, (int)this.lengthFieldLength, (ByteOrder)this.byteOrder);
        if (frameLength < 0L) {
            LengthFieldBasedFrameDecoder.failOnNegativeLengthField((ByteBuf)in, (long)frameLength, (int)this.lengthFieldEndOffset);
        }
        if ((frameLength += (long)(this.lengthAdjustment + this.lengthFieldEndOffset)) < (long)this.lengthFieldEndOffset) {
            LengthFieldBasedFrameDecoder.failOnFrameLengthLessThanLengthFieldEndOffset((ByteBuf)in, (long)frameLength, (int)this.lengthFieldEndOffset);
        }
        if (frameLength > (long)this.maxFrameLength) {
            this.exceededFrameLength((ByteBuf)in, (long)frameLength);
            return null;
        }
        int frameLengthInt = (int)frameLength;
        if (in.readableBytes() < frameLengthInt) {
            return null;
        }
        if (this.initialBytesToStrip > frameLengthInt) {
            LengthFieldBasedFrameDecoder.failOnFrameLengthLessThanInitialBytesToStrip((ByteBuf)in, (long)frameLength, (int)this.initialBytesToStrip);
        }
        in.skipBytes((int)this.initialBytesToStrip);
        int readerIndex = in.readerIndex();
        int actualFrameLength = frameLengthInt - this.initialBytesToStrip;
        ByteBuf frame = this.extractFrame((ChannelHandlerContext)ctx, (ByteBuf)in, (int)readerIndex, (int)actualFrameLength);
        in.readerIndex((int)(readerIndex + actualFrameLength));
        return frame;
    }

    protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
        buf = buf.order((ByteOrder)order);
        switch (length) {
            case 1: {
                return (long)buf.getUnsignedByte((int)offset);
            }
            case 2: {
                return (long)buf.getUnsignedShort((int)offset);
            }
            case 3: {
                return (long)buf.getUnsignedMedium((int)offset);
            }
            case 4: {
                return buf.getUnsignedInt((int)offset);
            }
            case 8: {
                return buf.getLong((int)offset);
            }
        }
        throw new DecoderException((String)("unsupported lengthFieldLength: " + this.lengthFieldLength + " (expected: 1, 2, 3, 4, or 8)"));
    }

    private void failIfNecessary(boolean firstDetectionOfTooLongFrame) {
        if (this.bytesToDiscard != 0L) {
            if (!this.failFast) return;
            if (!firstDetectionOfTooLongFrame) return;
            this.fail((long)this.tooLongFrameLength);
            return;
        }
        long tooLongFrameLength = this.tooLongFrameLength;
        this.tooLongFrameLength = 0L;
        this.discardingTooLongFrame = false;
        if (this.failFast) {
            if (!firstDetectionOfTooLongFrame) return;
        }
        this.fail((long)tooLongFrameLength);
    }

    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.retainedSlice((int)index, (int)length);
    }

    private void fail(long frameLength) {
        if (frameLength <= 0L) throw new TooLongFrameException((String)("Adjusted frame length exceeds " + this.maxFrameLength + " - discarding"));
        throw new TooLongFrameException((String)("Adjusted frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded"));
    }
}

