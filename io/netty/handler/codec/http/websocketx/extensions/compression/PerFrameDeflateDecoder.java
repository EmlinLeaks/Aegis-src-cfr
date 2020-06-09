/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder;

class PerFrameDeflateDecoder
extends DeflateDecoder {
    PerFrameDeflateDecoder(boolean noContext) {
        super((boolean)noContext, (WebSocketExtensionFilter)WebSocketExtensionFilter.NEVER_SKIP);
    }

    PerFrameDeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter) {
        super((boolean)noContext, (WebSocketExtensionFilter)extensionDecoderFilter);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (!super.acceptInboundMessage((Object)msg)) {
            return false;
        }
        WebSocketFrame wsFrame = (WebSocketFrame)msg;
        if (this.extensionDecoderFilter().mustSkip((WebSocketFrame)wsFrame)) {
            return false;
        }
        if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
            if (!(msg instanceof ContinuationWebSocketFrame)) return false;
        }
        if ((wsFrame.rsv() & 4) <= 0) return false;
        return true;
    }

    @Override
    protected int newRsv(WebSocketFrame msg) {
        return msg.rsv() ^ 4;
    }

    @Override
    protected boolean appendFrameTail(WebSocketFrame msg) {
        return true;
    }
}

