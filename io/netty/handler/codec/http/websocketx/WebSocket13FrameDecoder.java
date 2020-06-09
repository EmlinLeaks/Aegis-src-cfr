/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;

public class WebSocket13FrameDecoder
extends WebSocket08FrameDecoder {
    public WebSocket13FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
        this((boolean)expectMaskedFrames, (boolean)allowExtensions, (int)maxFramePayloadLength, (boolean)false);
    }

    public WebSocket13FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        this((WebSocketDecoderConfig)WebSocketDecoderConfig.newBuilder().expectMaskedFrames((boolean)expectMaskedFrames).allowExtensions((boolean)allowExtensions).maxFramePayloadLength((int)maxFramePayloadLength).allowMaskMismatch((boolean)allowMaskMismatch).build());
    }

    public WebSocket13FrameDecoder(WebSocketDecoderConfig decoderConfig) {
        super((WebSocketDecoderConfig)decoderConfig);
    }
}

