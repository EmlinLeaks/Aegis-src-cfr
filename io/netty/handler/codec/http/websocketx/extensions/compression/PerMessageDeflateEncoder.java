/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateEncoder;
import java.util.List;

class PerMessageDeflateEncoder
extends DeflateEncoder {
    private boolean compressing;

    PerMessageDeflateEncoder(int compressionLevel, int windowSize, boolean noContext) {
        super((int)compressionLevel, (int)windowSize, (boolean)noContext, (WebSocketExtensionFilter)WebSocketExtensionFilter.NEVER_SKIP);
    }

    PerMessageDeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
        super((int)compressionLevel, (int)windowSize, (boolean)noContext, (WebSocketExtensionFilter)extensionEncoderFilter);
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (!super.acceptOutboundMessage((Object)msg)) {
            return false;
        }
        WebSocketFrame wsFrame = (WebSocketFrame)msg;
        if (this.extensionEncoderFilter().mustSkip((WebSocketFrame)wsFrame)) {
            if (!this.compressing) return false;
            throw new IllegalStateException((String)"Cannot skip per message deflate encoder, compression in progress");
        }
        if (wsFrame instanceof TextWebSocketFrame || wsFrame instanceof BinaryWebSocketFrame) {
            if ((wsFrame.rsv() & 4) == 0) return true;
        }
        if (!(wsFrame instanceof ContinuationWebSocketFrame)) return false;
        if (!this.compressing) return false;
        return true;
    }

    @Override
    protected int rsv(WebSocketFrame msg) {
        int n;
        if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
            n = msg.rsv();
            return n;
        }
        n = msg.rsv() | 4;
        return n;
    }

    @Override
    protected boolean removeFrameTail(WebSocketFrame msg) {
        return msg.isFinalFragment();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        super.encode((ChannelHandlerContext)ctx, (WebSocketFrame)msg, out);
        if (msg.isFinalFragment()) {
            this.compressing = false;
            return;
        }
        if (!(msg instanceof TextWebSocketFrame)) {
            if (!(msg instanceof BinaryWebSocketFrame)) return;
        }
        this.compressing = true;
    }
}

