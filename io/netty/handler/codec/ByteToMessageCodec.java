/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class ByteToMessageCodec<I>
extends ChannelDuplexHandler {
    private final TypeParameterMatcher outboundMsgMatcher;
    private final MessageToByteEncoder<I> encoder;
    private final ByteToMessageDecoder decoder = new ByteToMessageDecoder((ByteToMessageCodec)this){
        final /* synthetic */ ByteToMessageCodec this$0;
        {
            this.this$0 = this$0;
        }

        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            this.this$0.decode((ChannelHandlerContext)ctx, (ByteBuf)in, out);
        }

        protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            this.this$0.decodeLast((ChannelHandlerContext)ctx, (ByteBuf)in, out);
        }
    };

    protected ByteToMessageCodec() {
        this((boolean)true);
    }

    protected ByteToMessageCodec(Class<? extends I> outboundMessageType) {
        this(outboundMessageType, (boolean)true);
    }

    protected ByteToMessageCodec(boolean preferDirect) {
        this.ensureNotSharable();
        this.outboundMsgMatcher = TypeParameterMatcher.find((Object)this, ByteToMessageCodec.class, (String)"I");
        this.encoder = new Encoder((ByteToMessageCodec)this, (boolean)preferDirect);
    }

    protected ByteToMessageCodec(Class<? extends I> outboundMessageType, boolean preferDirect) {
        this.ensureNotSharable();
        this.outboundMsgMatcher = TypeParameterMatcher.get(outboundMessageType);
        this.encoder = new Encoder((ByteToMessageCodec)this, (boolean)preferDirect);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.outboundMsgMatcher.match((Object)msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.decoder.channelRead((ChannelHandlerContext)ctx, (Object)msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.encoder.write((ChannelHandlerContext)ctx, (Object)msg, (ChannelPromise)promise);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelReadComplete((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.decoder.channelInactive((ChannelHandlerContext)ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        try {
            this.decoder.handlerAdded((ChannelHandlerContext)ctx);
            return;
        }
        finally {
            this.encoder.handlerAdded((ChannelHandlerContext)ctx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            this.decoder.handlerRemoved((ChannelHandlerContext)ctx);
            return;
        }
        finally {
            this.encoder.handlerRemoved((ChannelHandlerContext)ctx);
        }
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, ByteBuf var3) throws Exception;

    protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) return;
        this.decode((ChannelHandlerContext)ctx, (ByteBuf)in, out);
    }
}

