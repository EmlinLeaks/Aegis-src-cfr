/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateEncoder;

class PerFrameDeflateEncoder
extends DeflateEncoder {
    PerFrameDeflateEncoder(int compressionLevel, int windowSize, boolean noContext) {
        super((int)compressionLevel, (int)windowSize, (boolean)noContext, (WebSocketExtensionFilter)WebSocketExtensionFilter.NEVER_SKIP);
    }

    PerFrameDeflateEncoder(int compressionLevel, int windowSize, boolean noContext, WebSocketExtensionFilter extensionEncoderFilter) {
        super((int)compressionLevel, (int)windowSize, (boolean)noContext, (WebSocketExtensionFilter)extensionEncoderFilter);
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        if (!super.acceptOutboundMessage((Object)msg)) {
            return false;
        }
        WebSocketFrame wsFrame = (WebSocketFrame)msg;
        if (this.extensionEncoderFilter().mustSkip((WebSocketFrame)wsFrame)) {
            return false;
        }
        if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
            if (!(msg instanceof ContinuationWebSocketFrame)) return false;
        }
        if (wsFrame.content().readableBytes() <= 0) return false;
        if ((wsFrame.rsv() & 4) != 0) return false;
        return true;
    }

    @Override
    protected int rsv(WebSocketFrame msg) {
        return msg.rsv() | 4;
    }

    @Override
    protected boolean removeFrameTail(WebSocketFrame msg) {
        return true;
    }
}

