/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.CompactObjectInputStream;
import java.io.InputStream;

public class ObjectDecoder
extends LengthFieldBasedFrameDecoder {
    private final ClassResolver classResolver;

    public ObjectDecoder(ClassResolver classResolver) {
        this((int)1048576, (ClassResolver)classResolver);
    }

    public ObjectDecoder(int maxObjectSize, ClassResolver classResolver) {
        super((int)maxObjectSize, (int)0, (int)4, (int)0, (int)4);
        this.classResolver = classResolver;
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
        CompactObjectInputStream ois = new CompactObjectInputStream((InputStream)new ByteBufInputStream((ByteBuf)frame, (boolean)true), (ClassResolver)this.classResolver);
        try {
            Object object = ois.readObject();
            return object;
        }
        finally {
            ois.close();
        }
    }
}

