/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;

public final class DeflateFrameServerExtensionHandshaker
implements WebSocketServerExtensionHandshaker {
    static final String X_WEBKIT_DEFLATE_FRAME_EXTENSION = "x-webkit-deflate-frame";
    static final String DEFLATE_FRAME_EXTENSION = "deflate-frame";
    private final int compressionLevel;
    private final WebSocketExtensionFilterProvider extensionFilterProvider;

    public DeflateFrameServerExtensionHandshaker() {
        this((int)6);
    }

    public DeflateFrameServerExtensionHandshaker(int compressionLevel) {
        this((int)compressionLevel, (WebSocketExtensionFilterProvider)WebSocketExtensionFilterProvider.DEFAULT);
    }

    public DeflateFrameServerExtensionHandshaker(int compressionLevel, WebSocketExtensionFilterProvider extensionFilterProvider) {
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        this.compressionLevel = compressionLevel;
        this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, (String)"extensionFilterProvider");
    }

    @Override
    public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
        if (!X_WEBKIT_DEFLATE_FRAME_EXTENSION.equals((Object)extensionData.name()) && !DEFLATE_FRAME_EXTENSION.equals((Object)extensionData.name())) {
            return null;
        }
        if (!extensionData.parameters().isEmpty()) return null;
        return new DeflateFrameServerExtension((int)this.compressionLevel, (String)extensionData.name(), (WebSocketExtensionFilterProvider)this.extensionFilterProvider);
    }
}

