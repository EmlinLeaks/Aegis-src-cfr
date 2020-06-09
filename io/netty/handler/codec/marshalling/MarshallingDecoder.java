/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.jboss.marshalling.ByteInput
 *  org.jboss.marshalling.Unmarshaller
 */
package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.marshalling.ChannelBufferByteInput;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

public class MarshallingDecoder
extends LengthFieldBasedFrameDecoder {
    private final UnmarshallerProvider provider;

    public MarshallingDecoder(UnmarshallerProvider provider) {
        this((UnmarshallerProvider)provider, (int)1048576);
    }

    public MarshallingDecoder(UnmarshallerProvider provider, int maxObjectSize) {
        super((int)maxObjectSize, (int)0, (int)4, (int)0, (int)4);
        this.provider = provider;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf)super.decode((ChannelHandlerContext)ctx, (ByteBuf)in);
        if (frame == null) {
            return null;
        }
        Unmarshaller unmarshaller = this.provider.getUnmarshaller((ChannelHandlerContext)ctx);
        ChannelBufferByteInput input = new ChannelBufferByteInput((ByteBuf)frame);
        try {
            unmarshaller.start((ByteInput)input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            Object object = obj;
            return object;
        }
        finally {
            unmarshaller.close();
        }
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice((int)index, (int)length);
    }
}

