/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateServerExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class PerMessageDeflateServerExtensionHandshaker
implements WebSocketServerExtensionHandshaker {
    public static final int MIN_WINDOW_SIZE = 8;
    public static final int MAX_WINDOW_SIZE = 15;
    static final String PERMESSAGE_DEFLATE_EXTENSION = "permessage-deflate";
    static final String CLIENT_MAX_WINDOW = "client_max_window_bits";
    static final String SERVER_MAX_WINDOW = "server_max_window_bits";
    static final String CLIENT_NO_CONTEXT = "client_no_context_takeover";
    static final String SERVER_NO_CONTEXT = "server_no_context_takeover";
    private final int compressionLevel;
    private final boolean allowServerWindowSize;
    private final int preferredClientWindowSize;
    private final boolean allowServerNoContext;
    private final boolean preferredClientNoContext;
    private final WebSocketExtensionFilterProvider extensionFilterProvider;

    public PerMessageDeflateServerExtensionHandshaker() {
        this((int)6, (boolean)ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), (int)15, (boolean)false, (boolean)false);
    }

    public PerMessageDeflateServerExtensionHandshaker(int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize, boolean allowServerNoContext, boolean preferredClientNoContext) {
        this((int)compressionLevel, (boolean)allowServerWindowSize, (int)preferredClientWindowSize, (boolean)allowServerNoContext, (boolean)preferredClientNoContext, (WebSocketExtensionFilterProvider)WebSocketExtensionFilterProvider.DEFAULT);
    }

    public PerMessageDeflateServerExtensionHandshaker(int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize, boolean allowServerNoContext, boolean preferredClientNoContext, WebSocketExtensionFilterProvider extensionFilterProvider) {
        if (preferredClientWindowSize > 15) throw new IllegalArgumentException((String)("preferredServerWindowSize: " + preferredClientWindowSize + " (expected: 8-15)"));
        if (preferredClientWindowSize < 8) {
            throw new IllegalArgumentException((String)("preferredServerWindowSize: " + preferredClientWindowSize + " (expected: 8-15)"));
        }
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        this.compressionLevel = compressionLevel;
        this.allowServerWindowSize = allowServerWindowSize;
        this.preferredClientWindowSize = preferredClientWindowSize;
        this.allowServerNoContext = allowServerNoContext;
        this.preferredClientNoContext = preferredClientNoContext;
        this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, (String)"extensionFilterProvider");
    }

    @Override
    public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
        if (!PERMESSAGE_DEFLATE_EXTENSION.equals((Object)extensionData.name())) {
            return null;
        }
        boolean deflateEnabled = true;
        int clientWindowSize = 15;
        int serverWindowSize = 15;
        boolean serverNoContext = false;
        boolean clientNoContext = false;
        Iterator<Map.Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();
        while (deflateEnabled && parametersIterator.hasNext()) {
            Map.Entry<String, String> parameter = parametersIterator.next();
            if (CLIENT_MAX_WINDOW.equalsIgnoreCase((String)parameter.getKey())) {
                clientWindowSize = this.preferredClientWindowSize;
                continue;
            }
            if (SERVER_MAX_WINDOW.equalsIgnoreCase((String)parameter.getKey())) {
                if (this.allowServerWindowSize) {
                    serverWindowSize = Integer.parseInt((String)parameter.getValue());
                    if (serverWindowSize <= 15 && serverWindowSize >= 8) continue;
                    deflateEnabled = false;
                    continue;
                }
                deflateEnabled = false;
                continue;
            }
            if (CLIENT_NO_CONTEXT.equalsIgnoreCase((String)parameter.getKey())) {
                clientNoContext = this.preferredClientNoContext;
                continue;
            }
            if (SERVER_NO_CONTEXT.equalsIgnoreCase((String)parameter.getKey())) {
                if (this.allowServerNoContext) {
                    serverNoContext = true;
                    continue;
                }
                deflateEnabled = false;
                continue;
            }
            deflateEnabled = false;
        }
        if (!deflateEnabled) return null;
        return new PermessageDeflateExtension((int)this.compressionLevel, (boolean)serverNoContext, (int)serverWindowSize, (boolean)clientNoContext, (int)clientWindowSize, (WebSocketExtensionFilterProvider)this.extensionFilterProvider);
    }
}

