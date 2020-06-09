/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN>
extends ChannelDuplexHandler {
    private final MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<Object>((MessageToMessageCodec)this){
        final /* synthetic */ MessageToMessageCodec this$0;
        {
            this.this$0 = this$0;
        }

        public boolean acceptOutboundMessage(Object msg) throws Exception {
            return this.this$0.acceptOutboundMessage((Object)msg);
        }

        protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            this.this$0.encode((ChannelHandlerContext)ctx, msg, out);
        }
    };
    private final MessageToMessageDecoder<Object> decoder = new MessageToMessageDecoder<Object>((MessageToMessageCodec)this){
        final /* synthetic */ MessageToMessageCodec this$0;
        {
            this.this$0 = this$0;
        }

        public boolean acceptInboundMessage(Object msg) throws Exception {
            return this.this$0.acceptInboundMessage((Object)msg);
        }

        protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            this.this$0.decode((ChannelHandlerContext)ctx, msg, out);
        }
    };
    private final TypeParameterMatcher inboundMsgMatcher;
    private final TypeParameterMatcher outboundMsgMatcher;

    protected MessageToMessageCodec() {
        this.inboundMsgMatcher = TypeParameterMatcher.find((Object)this, MessageToMessageCodec.class, (String)"INBOUND_IN");
        this.outboundMsgMatcher = TypeParameterMatcher.find((Object)this, MessageToMessageCodec.class, (String)"OUTBOUND_IN");
    }

    protected MessageToMessageCodec(Class<? extends INBOUND_IN> inboundMessageType, Class<? extends OUTBOUND_IN> outboundMessageType) {
        this.inboundMsgMatcher = TypeParameterMatcher.get(inboundMessageType);
        this.outboundMsgMatcher = TypeParameterMatcher.get(outboundMessageType);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.decoder.channelRead((ChannelHandlerContext)ctx, (Object)msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.encoder.write((ChannelHandlerContext)ctx, (Object)msg, (ChannelPromise)promise);
    }

    public boolean acceptInboundMessage(Object msg) throws Exception {
        return this.inboundMsgMatcher.match((Object)msg);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.outboundMsgMatcher.match((Object)msg);
    }

    protected abstract void encode(ChannelHandlerContext var1, OUTBOUND_IN var2, List<Object> var3) throws Exception;

    protected abstract void decode(ChannelHandlerContext var1, INBOUND_IN var2, List<Object> var3) throws Exception;
}

