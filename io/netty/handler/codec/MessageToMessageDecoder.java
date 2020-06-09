/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I>
extends ChannelInboundHandlerAdapter {
    private final TypeParameterMatcher matcher;

    protected MessageToMessageDecoder() {
        this.matcher = TypeParameterMatcher.find((Object)this, MessageToMessageDecoder.class, (String)"I");
    }

    protected MessageToMessageDecoder(Class<? extends I> inboundMessageType) {
        this.matcher = TypeParameterMatcher.get(inboundMessageType);
    }

    public boolean acceptInboundMessage(Object msg) throws Exception {
        return this.matcher.match((Object)msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CodecOutputList out = CodecOutputList.newInstance();
        try {
            if (this.acceptInboundMessage((Object)msg)) {
                Object cast = msg;
                try {
                    this.decode((ChannelHandlerContext)ctx, cast, (List<Object>)out);
                    return;
                }
                finally {
                    ReferenceCountUtil.release((Object)cast);
                }
            }
            out.add((Object)msg);
            return;
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DecoderException((Throwable)e);
        }
        finally {
            int size = out.size();
            int i = 0;
            do {
                if (i >= size) {
                    out.recycle();
                }
                ctx.fireChannelRead((Object)out.getUnsafe((int)i));
                ++i;
            } while (true);
        }
    }

    protected abstract void decode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

