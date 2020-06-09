/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.nano.MessageNano
 */
package io.netty.handler.codec.protobuf;

import com.google.protobuf.nano.MessageNano;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.lang.reflect.Constructor;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufDecoderNano
extends MessageToMessageDecoder<ByteBuf> {
    private final Class<? extends MessageNano> clazz;

    public ProtobufDecoderNano(Class<? extends MessageNano> clazz) {
        this.clazz = ObjectUtil.checkNotNull(clazz, (String)"You must provide a Class");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] array;
        int offset;
        int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = ByteBufUtil.getBytes((ByteBuf)msg, (int)msg.readerIndex(), (int)length, (boolean)false);
            offset = 0;
        }
        MessageNano prototype = this.clazz.getConstructor(new Class[0]).newInstance((Object[])new Object[0]);
        out.add((Object)MessageNano.mergeFrom((MessageNano)prototype, (byte[])array, (int)offset, (int)length));
    }
}

