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
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder;
import java.util.List;

class PerMessageDeflateDecoder
extends DeflateDecoder {
    private boolean compressing;

    PerMessageDeflateDecoder(boolean noContext) {
        super((boolean)noContext, (WebSocketExtensionFilter)WebSocketExtensionFilter.NEVER_SKIP);
    }

    PerMessageDeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter) {
        super((boolean)noContext, (WebSocketExtensionFilter)extensionDecoderFilter);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        if (!super.acceptInboundMessage((Object)msg)) {
            return false;
        }
        WebSocketFrame wsFrame = (WebSocketFrame)msg;
        if (this.extensionDecoderFilter().mustSkip((WebSocketFrame)wsFrame)) {
            if (!this.compressing) return false;
            throw new IllegalStateException((String)"Cannot skip per message deflate decoder, compression in progress");
        }
        if (wsFrame instanceof TextWebSocketFrame || wsFrame instanceof BinaryWebSocketFrame) {
            if ((wsFrame.rsv() & 4) > 0) return true;
        }
        if (!(wsFrame instanceof ContinuationWebSocketFrame)) return false;
        if (!this.compressing) return false;
        return true;
    }

    @Override
    protected int newRsv(WebSocketFrame msg) {
        int n;
        if ((msg.rsv() & 4) > 0) {
            n = msg.rsv() ^ 4;
            return n;
        }
        n = msg.rsv();
        return n;
    }

    @Override
    protected boolean appendFrameTail(WebSocketFrame msg) {
        return msg.isFinalFragment();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        super.decode((ChannelHandlerContext)ctx, (WebSocketFrame)msg, out);
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

