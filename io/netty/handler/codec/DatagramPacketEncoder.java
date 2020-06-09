/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

public class DatagramPacketEncoder<M>
extends MessageToMessageEncoder<AddressedEnvelope<M, InetSocketAddress>> {
    private final MessageToMessageEncoder<? super M> encoder;

    public DatagramPacketEncoder(MessageToMessageEncoder<? super M> encoder) {
        this.encoder = ObjectUtil.checkNotNull(encoder, (String)"encoder");
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (!super.acceptOutboundMessage((Object)msg)) return false;
        AddressedEnvelope envelope = (AddressedEnvelope)msg;
        if (!this.encoder.acceptOutboundMessage(envelope.content())) return false;
        if (!(envelope.sender() instanceof InetSocketAddress)) {
            if (envelope.sender() != null) return false;
        }
        if (!(envelope.recipient() instanceof InetSocketAddress)) return false;
        return true;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<M, InetSocketAddress> msg, List<Object> out) throws Exception {
        assert (out.isEmpty());
        this.encoder.encode((ChannelHandlerContext)ctx, msg.content(), out);
        if (out.size() != 1) {
            throw new EncoderException((String)(StringUtil.simpleClassName(this.encoder) + " must produce only one message."));
        }
        Object content = out.get((int)0);
        if (!(content instanceof ByteBuf)) throw new EncoderException((String)(StringUtil.simpleClassName(this.encoder) + " must produce only ByteBuf."));
        out.set((int)0, (Object)new DatagramPacket((ByteBuf)((ByteBuf)content), (InetSocketAddress)msg.recipient(), (InetSocketAddress)msg.sender()));
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        this.encoder.bind((ChannelHandlerContext)ctx, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        this.encoder.connect((ChannelHandlerContext)ctx, (SocketAddress)remoteAddress, (SocketAddress)localAddress, (ChannelPromise)promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.encoder.disconnect((ChannelHandlerContext)ctx, (ChannelPromise)promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.encoder.close((ChannelHandlerContext)ctx, (ChannelPromise)promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.encoder.deregister((ChannelHandlerContext)ctx, (ChannelPromise)promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        this.encoder.read((ChannelHandlerContext)ctx);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        this.encoder.flush((ChannelHandlerContext)ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.encoder.handlerAdded((ChannelHandlerContext)ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.encoder.handlerRemoved((ChannelHandlerContext)ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.encoder.exceptionCaught((ChannelHandlerContext)ctx, (Throwable)cause);
    }

    @Override
    public boolean isSharable() {
        return this.encoder.isSharable();
    }
}

