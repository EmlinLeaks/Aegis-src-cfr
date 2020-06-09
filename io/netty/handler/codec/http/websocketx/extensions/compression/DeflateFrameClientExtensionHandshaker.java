/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameClientExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Map;

public final class DeflateFrameClientExtensionHandshaker
implements WebSocketClientExtensionHandshaker {
    private final int compressionLevel;
    private final boolean useWebkitExtensionName;
    private final WebSocketExtensionFilterProvider extensionFilterProvider;

    public DeflateFrameClientExtensionHandshaker(boolean useWebkitExtensionName) {
        this((int)6, (boolean)useWebkitExtensionName);
    }

    public DeflateFrameClientExtensionHandshaker(int compressionLevel, boolean useWebkitExtensionName) {
        this((int)compressionLevel, (boolean)useWebkitExtensionName, (WebSocketExtensionFilterProvider)WebSocketExtensionFilterProvider.DEFAULT);
    }

    public DeflateFrameClientExtensionHandshaker(int compressionLevel, boolean useWebkitExtensionName, WebSocketExtensionFilterProvider extensionFilterProvider) {
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        this.compressionLevel = compressionLevel;
        this.useWebkitExtensionName = useWebkitExtensionName;
        this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, (String)"extensionFilterProvider");
    }

    @Override
    public WebSocketExtensionData newRequestData() {
        String string;
        if (this.useWebkitExtensionName) {
            string = "x-webkit-deflate-frame";
            return new WebSocketExtensionData((String)string, Collections.<K, V>emptyMap());
        }
        string = "deflate-frame";
        return new WebSocketExtensionData((String)string, Collections.<String, String>emptyMap());
    }

    @Override
    public WebSocketClientExtension handshakeExtension(WebSocketExtensionData extensionData) {
        if (!"x-webkit-deflate-frame".equals((Object)extensionData.name()) && !"deflate-frame".equals((Object)extensionData.name())) {
            return null;
        }
        if (!extensionData.parameters().isEmpty()) return null;
        return new DeflateFrameClientExtension((int)this.compressionLevel, (WebSocketExtensionFilterProvider)this.extensionFilterProvider);
    }
}

