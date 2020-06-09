/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;

public class WebSocket07FrameDecoder
extends WebSocket08FrameDecoder {
    public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
        this((WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().expectMaskedFrames((boolean)expectMaskedFrames).allowExtensions((boolean)allowExtensions).maxFramePayloadLength((int)maxFramePayloadLength).build());
    }

    public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        this((WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().expectMaskedFrames((boolean)expectMaskedFrames).allowExtensions((boolean)allowExtensions).maxFramePayloadLength((int)maxFramePayloadLength).allowMaskMismatch((boolean)allowMaskMismatch).build());
    }

    public WebSocket07FrameDecoder(WebSocketDecoderConfig decoderConfig) {
        super((WebSocketDecoderConfig)decoderConfig);
    }
}

