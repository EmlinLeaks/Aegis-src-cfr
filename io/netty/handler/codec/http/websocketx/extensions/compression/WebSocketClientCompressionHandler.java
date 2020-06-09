/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateClientExtensionHandshaker;

@ChannelHandler.Sharable
public final class WebSocketClientCompressionHandler
extends WebSocketClientExtensionHandler {
    public static final WebSocketClientCompressionHandler INSTANCE = new WebSocketClientCompressionHandler();

    private WebSocketClientCompressionHandler() {
        super((WebSocketClientExtensionHandshaker[])new WebSocketClientExtensionHandshaker[]{new PerMessageDeflateClientExtensionHandshaker(), new DeflateFrameClientExtensionHandshaker((boolean)false), new DeflateFrameClientExtensionHandshaker((boolean)true)});
    }
}

