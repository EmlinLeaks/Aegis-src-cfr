/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class ProtobufVarint32LengthFieldPrepender
extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int bodyLen = msg.readableBytes();
        int headerLen = ProtobufVarint32LengthFieldPrepender.computeRawVarint32Size((int)bodyLen);
        out.ensureWritable((int)(headerLen + bodyLen));
        ProtobufVarint32LengthFieldPrepender.writeRawVarint32((ByteBuf)out, (int)bodyLen);
        out.writeBytes((ByteBuf)msg, (int)msg.readerIndex(), (int)bodyLen);
    }

    static void writeRawVarint32(ByteBuf out, int value) {
        do {
            if ((value & -128) == 0) {
                out.writeByte((int)value);
                return;
            }
            out.writeByte((int)(value & 127 | 128));
            value >>>= 7;
        } while (true);
    }

    static int computeRawVarint32Size(int value) {
        if ((value & -128) == 0) {
            return 1;
        }
        if ((value & -16384) == 0) {
            return 2;
        }
        if ((value & -2097152) == 0) {
            return 3;
        }
        if ((value & -268435456) != 0) return 5;
        return 4;
    }
}

