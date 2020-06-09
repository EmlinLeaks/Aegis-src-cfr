/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
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
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

abstract class DeflateEncoder
extends WebSocketExtensionEncoder {
    private final int compressionLevel;
    private final int windowSize;
    private final boolean noContext;
    private final WebSocketExtensionFilter extensionEncoderFilter;
    private EmbeddedChannel encoder;

    DeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
        this.compressionLevel = compressionLevel;
        this.windowSize = windowSize;
        this.noContext = noContext;
        this.extensionEncoderFilter = ObjectUtil.checkNotNull(extensionEncoderFilter, (String)"extensionEncoderFilter");
    }

    protected WebSocketExtensionFilter extensionEncoderFilter() {
        return this.extensionEncoderFilter;
    }

    protected abstract int rsv(WebSocketFrame var1);

    protected abstract boolean removeFrameTail(WebSocketFrame var1);

    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf compressedContent;
        WebSocketFrame outMsg;
        if (msg.content().isReadable()) {
            compressedContent = this.compressContent((ChannelHandlerContext)ctx, (WebSocketFrame)msg);
        } else {
            if (!msg.isFinalFragment()) throw new CodecException((String)"cannot compress content buffer");
            compressedContent = PerMessageDeflateDecoder.EMPTY_DEFLATE_BLOCK.duplicate();
        }
        if (msg instanceof TextWebSocketFrame) {
            outMsg = new TextWebSocketFrame((boolean)msg.isFinalFragment(), (int)this.rsv((WebSocketFrame)msg), (ByteBuf)compressedContent);
        } else if (msg instanceof BinaryWebSocketFrame) {
            outMsg = new BinaryWebSocketFrame((boolean)msg.isFinalFragment(), (int)this.rsv((WebSocketFrame)msg), (ByteBuf)compressedContent);
        } else {
            if (!(msg instanceof ContinuationWebSocketFrame)) throw new CodecException((String)("unexpected frame type: " + msg.getClass().getName()));
            outMsg = new ContinuationWebSocketFrame((boolean)msg.isFinalFragment(), (int)this.rsv((WebSocketFrame)msg), (ByteBuf)compressedContent);
        }
        out.add((Object)outMsg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved((ChannelHandlerContext)ctx);
    }

    private ByteBuf compressContent(ChannelHandlerContext ctx, WebSocketFrame msg) {
        CompositeByteBuf fullCompressedContent;
        block6 : {
            if (this.encoder == null) {
                this.encoder = new EmbeddedChannel((ChannelHandler[])new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder((ZlibWrapper)ZlibWrapper.NONE, (int)this.compressionLevel, (int)this.windowSize, (int)8)});
            }
            this.encoder.writeOutbound((Object[])new Object[]{msg.content().retain()});
            fullCompressedContent = ctx.alloc().compositeBuffer();
            do {
                ByteBuf partCompressedContent;
                if ((partCompressedContent = (ByteBuf)this.encoder.readOutbound()) == null) {
                    if (fullCompressedContent.numComponents() <= 0) {
                        break;
                    }
                    break block6;
                }
                if (!partCompressedContent.isReadable()) {
                    partCompressedContent.release();
                    continue;
                }
                fullCompressedContent.addComponent((boolean)true, (ByteBuf)partCompressedContent);
            } while (true);
            fullCompressedContent.release();
            throw new CodecException((String)"cannot read compressed buffer");
        }
        if (msg.isFinalFragment() && this.noContext) {
            this.cleanup();
        }
        if (!this.removeFrameTail((WebSocketFrame)msg)) return fullCompressedContent;
        int realLength = fullCompressedContent.readableBytes() - PerMessageDeflateDecoder.FRAME_TAIL.readableBytes();
        return fullCompressedContent.slice((int)0, (int)realLength);
    }

    private void cleanup() {
        if (this.encoder == null) return;
        if (this.encoder.finish()) {
            ByteBuf buf;
            while ((buf = (ByteBuf)this.encoder.readOutbound()) != null) {
                buf.release();
            }
        }
        this.encoder = null;
    }
}

