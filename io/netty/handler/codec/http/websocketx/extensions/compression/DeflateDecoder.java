/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

abstract class DeflateDecoder
extends WebSocketExtensionDecoder {
    static final ByteBuf FRAME_TAIL = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.wrappedBuffer((byte[])new byte[]{0, 0, -1, -1})).asReadOnly();
    static final ByteBuf EMPTY_DEFLATE_BLOCK = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.wrappedBuffer((byte[])new byte[]{0})).asReadOnly();
    private final boolean noContext;
    private final WebSocketExtensionFilter extensionDecoderFilter;
    private EmbeddedChannel decoder;

    DeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter) {
        this.noContext = noContext;
        this.extensionDecoderFilter = ObjectUtil.checkNotNull(extensionDecoderFilter, (String)"extensionDecoderFilter");
    }

    protected WebSocketExtensionFilter extensionDecoderFilter() {
        return this.extensionDecoderFilter;
    }

    protected abstract boolean appendFrameTail(WebSocketFrame var1);

    protected abstract int newRsv(WebSocketFrame var1);

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        WebSocketFrame outMsg;
        ByteBuf decompressedContent = this.decompressContent((ChannelHandlerContext)ctx, (WebSocketFrame)msg);
        if (msg instanceof TextWebSocketFrame) {
            outMsg = new TextWebSocketFrame((boolean)msg.isFinalFragment(), (int)this.newRsv((WebSocketFrame)msg), (ByteBuf)decompressedContent);
        } else if (msg instanceof BinaryWebSocketFrame) {
            outMsg = new BinaryWebSocketFrame((boolean)msg.isFinalFragment(), (int)this.newRsv((WebSocketFrame)msg), (ByteBuf)decompressedContent);
        } else {
            if (!(msg instanceof ContinuationWebSocketFrame)) throw new CodecException((String)("unexpected frame type: " + msg.getClass().getName()));
            outMsg = new ContinuationWebSocketFrame((boolean)msg.isFinalFragment(), (int)this.newRsv((WebSocketFrame)msg), (ByteBuf)decompressedContent);
        }
        out.add((Object)outMsg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved((ChannelHandlerContext)ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.channelInactive((ChannelHandlerContext)ctx);
    }

    private ByteBuf decompressContent(ChannelHandlerContext ctx, WebSocketFrame msg) {
        CompositeByteBuf compositeDecompressedContent;
        block8 : {
            if (this.decoder == null) {
                if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
                    throw new CodecException((String)("unexpected initial frame type: " + msg.getClass().getName()));
                }
                this.decoder = new EmbeddedChannel((ChannelHandler[])new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder((ZlibWrapper)ZlibWrapper.NONE)});
            }
            boolean readable = msg.content().isReadable();
            boolean emptyDeflateBlock = EMPTY_DEFLATE_BLOCK.equals((Object)msg.content());
            this.decoder.writeInbound((Object[])new Object[]{msg.content().retain()});
            if (this.appendFrameTail((WebSocketFrame)msg)) {
                this.decoder.writeInbound((Object[])new Object[]{FRAME_TAIL.duplicate()});
            }
            compositeDecompressedContent = ctx.alloc().compositeBuffer();
            do {
                ByteBuf partUncompressedContent;
                if ((partUncompressedContent = (ByteBuf)this.decoder.readInbound()) == null) {
                    if (!emptyDeflateBlock) {
                        break;
                    }
                    break block8;
                }
                if (!partUncompressedContent.isReadable()) {
                    partUncompressedContent.release();
                    continue;
                }
                compositeDecompressedContent.addComponent((boolean)true, (ByteBuf)partUncompressedContent);
            } while (true);
            if (readable && compositeDecompressedContent.numComponents() <= 0) {
                compositeDecompressedContent.release();
                throw new CodecException((String)"cannot read uncompressed buffer");
            }
        }
        if (!msg.isFinalFragment()) return compositeDecompressedContent;
        if (!this.noContext) return compositeDecompressedContent;
        this.cleanup();
        return compositeDecompressedContent;
    }

    private void cleanup() {
        if (this.decoder == null) return;
        if (this.decoder.finish()) {
            ByteBuf buf;
            while ((buf = (ByteBuf)this.decoder.readOutbound()) != null) {
                buf.release();
            }
        }
        this.decoder = null;
    }
}

