/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateServerExtensionHandshaker;

public class WebSocketServerCompressionHandler
extends WebSocketServerExtensionHandler {
    public WebSocketServerCompressionHandler() {
        super((WebSocketServerExtensionHandshaker[])new WebSocketServerExtensionHandshaker[]{new PerMessageDeflateServerExtensionHandshaker(), new DeflateFrameServerExtensionHandshaker()});
    }
}

