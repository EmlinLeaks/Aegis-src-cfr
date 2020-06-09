/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.ByteOutput
 *  org.jboss.marshalling.Marshaller
 */
package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.marshalling.ChannelBufferByteOutput;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import org.jboss.marshalling.ByteOutput;
import org.jboss.marshalling.Marshaller;

@ChannelHandler.Sharable
public class MarshallingEncoder
extends MessageToByteEncoder<Object> {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private final MarshallerProvider provider;

    public MarshallingEncoder(MarshallerProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Marshaller marshaller = this.provider.getMarshaller((ChannelHandlerContext)ctx);
        int lengthPos = out.writerIndex();
        out.writeBytes((byte[])LENGTH_PLACEHOLDER);
        ChannelBufferByteOutput output = new ChannelBufferByteOutput((ByteBuf)out);
        marshaller.start((ByteOutput)output);
        marshaller.writeObject((Object)msg);
        marshaller.finish();
        marshaller.close();
        out.setInt((int)lengthPos, (int)(out.writerIndex() - lengthPos - 4));
    }
}

