/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateClientExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class PerMessageDeflateClientExtensionHandshaker
implements WebSocketClientExtensionHandshaker {
    private final int compressionLevel;
    private final boolean allowClientWindowSize;
    private final int requestedServerWindowSize;
    private final boolean allowClientNoContext;
    private final boolean requestedServerNoContext;
    private final WebSocketExtensionFilterProvider extensionFilterProvider;

    public PerMessageDeflateClientExtensionHandshaker() {
        this((int)6, (boolean)ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), (int)15, (boolean)false, (boolean)false);
    }

    public PerMessageDeflateClientExtensionHandshaker(int compressionLevel, boolean allowClientWindowSize, int requestedServerWindowSize, boolean allowClientNoContext, boolean requestedServerNoContext) {
        this((int)compressionLevel, (boolean)allowClientWindowSize, (int)requestedServerWindowSize, (boolean)allowClientNoContext, (boolean)requestedServerNoContext, (WebSocketExtensionFilterProvider)WebSocketExtensionFilterProvider.DEFAULT);
    }

    public PerMessageDeflateClientExtensionHandshaker(int compressionLevel, boolean allowClientWindowSize, int requestedServerWindowSize, boolean allowClientNoContext, boolean requestedServerNoContext, WebSocketExtensionFilterProvider extensionFilterProvider) {
        if (requestedServerWindowSize > 15) throw new IllegalArgumentException((String)("requestedServerWindowSize: " + requestedServerWindowSize + " (expected: 8-15)"));
        if (requestedServerWindowSize < 8) {
            throw new IllegalArgumentException((String)("requestedServerWindowSize: " + requestedServerWindowSize + " (expected: 8-15)"));
        }
        if (compressionLevel < 0) throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        if (compressionLevel > 9) {
            throw new IllegalArgumentException((String)("compressionLevel: " + compressionLevel + " (expected: 0-9)"));
        }
        this.compressionLevel = compressionLevel;
        this.allowClientWindowSize = allowClientWindowSize;
        this.requestedServerWindowSize = requestedServerWindowSize;
        this.allowClientNoContext = allowClientNoContext;
        this.requestedServerNoContext = requestedServerNoContext;
        this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, (String)"extensionFilterProvider");
    }

    @Override
    public WebSocketExtensionData newRequestData() {
        HashMap<String, String> parameters = new HashMap<String, String>((int)4);
        if (this.requestedServerWindowSize != 15) {
            parameters.put("server_no_context_takeover", null);
        }
        if (this.allowClientNoContext) {
            parameters.put("client_no_context_takeover", null);
        }
        if (this.requestedServerWindowSize != 15) {
            parameters.put("server_max_window_bits", Integer.toString((int)this.requestedServerWindowSize));
        }
        if (!this.allowClientWindowSize) return new WebSocketExtensionData((String)"permessage-deflate", parameters);
        parameters.put("client_max_window_bits", null);
        return new WebSocketExtensionData((String)"permessage-deflate", parameters);
    }

    @Override
    public WebSocketClientExtension handshakeExtension(WebSocketExtensionData extensionData) {
        if (!"permessage-deflate".equals((Object)extensionData.name())) {
            return null;
        }
        boolean succeed = true;
        int clientWindowSize = 15;
        int serverWindowSize = 15;
        boolean serverNoContext = false;
        boolean clientNoContext = false;
        Iterator<Map.Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();
        while (succeed && parametersIterator.hasNext()) {
            Map.Entry<String, String> parameter = parametersIterator.next();
            if ("client_max_window_bits".equalsIgnoreCase((String)parameter.getKey())) {
                if (this.allowClientWindowSize) {
                    clientWindowSize = Integer.parseInt((String)parameter.getValue());
                    continue;
                }
                succeed = false;
                continue;
            }
            if ("server_max_window_bits".equalsIgnoreCase((String)parameter.getKey())) {
                serverWindowSize = Integer.parseInt((String)parameter.getValue());
                if (clientWindowSize <= 15 && clientWindowSize >= 8) continue;
                succeed = false;
                continue;
            }
            if ("client_no_context_takeover".equalsIgnoreCase((String)parameter.getKey())) {
                if (this.allowClientNoContext) {
                    clientNoContext = true;
                    continue;
                }
                succeed = false;
                continue;
            }
            if ("server_no_context_takeover".equalsIgnoreCase((String)parameter.getKey())) {
                if (this.requestedServerNoContext) {
                    serverNoContext = true;
                    continue;
                }
                succeed = false;
                continue;
            }
            succeed = false;
        }
        if (this.requestedServerNoContext && !serverNoContext || this.requestedServerWindowSize != serverWindowSize) {
            succeed = false;
        }
        if (!succeed) return null;
        return new PermessageDeflateExtension((PerMessageDeflateClientExtensionHandshaker)this, (boolean)serverNoContext, (int)serverWindowSize, (boolean)clientNoContext, (int)clientWindowSize, (WebSocketExtensionFilterProvider)this.extensionFilterProvider);
    }

    static /* synthetic */ int access$000(PerMessageDeflateClientExtensionHandshaker x0) {
        return x0.compressionLevel;
    }
}

