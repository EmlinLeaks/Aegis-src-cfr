/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;
import java.util.List;

@ChannelHandler.Sharable
public class LengthFieldPrepender
extends MessageToMessageEncoder<ByteBuf> {
    private final ByteOrder byteOrder;
    private final int lengthFieldLength;
    private final boolean lengthIncludesLengthFieldLength;
    private final int lengthAdjustment;

    public LengthFieldPrepender(int lengthFieldLength) {
        this((int)lengthFieldLength, (boolean)false);
    }

    public LengthFieldPrepender(int lengthFieldLength, boolean lengthIncludesLengthFieldLength) {
        this((int)lengthFieldLength, (int)0, (boolean)lengthIncludesLengthFieldLength);
    }

    public LengthFieldPrepender(int lengthFieldLength, int lengthAdjustment) {
        this((int)lengthFieldLength, (int)lengthAdjustment, (boolean)false);
    }

    public LengthFieldPrepender(int lengthFieldLength, int lengthAdjustment, boolean lengthIncludesLengthFieldLength) {
        this((ByteOrder)ByteOrder.BIG_ENDIAN, (int)lengthFieldLength, (int)lengthAdjustment, (boolean)lengthIncludesLengthFieldLength);
    }

    public LengthFieldPrepender(ByteOrder byteOrder, int lengthFieldLength, int lengthAdjustment, boolean lengthIncludesLengthFieldLength) {
        if (lengthFieldLength != 1 && lengthFieldLength != 2 && lengthFieldLength != 3 && lengthFieldLength != 4 && lengthFieldLength != 8) {
            throw new IllegalArgumentException((String)("lengthFieldLength must be either 1, 2, 3, 4, or 8: " + lengthFieldLength));
        }
        ObjectUtil.checkNotNull(byteOrder, (String)"byteOrder");
        this.byteOrder = byteOrder;
        this.lengthFieldLength = lengthFieldLength;
        this.lengthIncludesLengthFieldLength = lengthIncludesLengthFieldLength;
        this.lengthAdjustment = lengthAdjustment;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        length = msg.readableBytes() + this.lengthAdjustment;
        if (this.lengthIncludesLengthFieldLength) {
            length += this.lengthFieldLength;
        }
        ObjectUtil.checkPositiveOrZero((int)length, (String)"length");
        switch (this.lengthFieldLength) {
            case 1: {
                if (length >= 256) {
                    throw new IllegalArgumentException((String)("length does not fit into a byte: " + length));
                }
                out.add((Object)ctx.alloc().buffer((int)1).order((ByteOrder)this.byteOrder).writeByte((int)((byte)length)));
                ** break;
            }
            case 2: {
                if (length >= 65536) {
                    throw new IllegalArgumentException((String)("length does not fit into a short integer: " + length));
                }
                out.add((Object)ctx.alloc().buffer((int)2).order((ByteOrder)this.byteOrder).writeShort((int)((short)length)));
                ** break;
            }
            case 3: {
                if (length >= 16777216) {
                    throw new IllegalArgumentException((String)("length does not fit into a medium integer: " + length));
                }
                out.add((Object)ctx.alloc().buffer((int)3).order((ByteOrder)this.byteOrder).writeMedium((int)length));
                ** break;
            }
            case 4: {
                out.add((Object)ctx.alloc().buffer((int)4).order((ByteOrder)this.byteOrder).writeInt((int)length));
                ** break;
            }
            case 8: {
                out.add((Object)ctx.alloc().buffer((int)8).order((ByteOrder)this.byteOrder).writeLong((long)((long)length)));
                ** break;
            }
        }
        throw new Error((String)"should not reach here");
lbl34: // 5 sources:
        out.add((Object)msg.retain());
    }
}

