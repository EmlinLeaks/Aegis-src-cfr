/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.handler.ssl.JettyAlpnSslEngine;
import io.netty.util.internal.PlatformDependent;
import javax.net.ssl.SSLEngine;

abstract class JettyAlpnSslEngine
extends JdkSslEngine {
    private static final boolean available = JettyAlpnSslEngine.initAvailable();

    static boolean isAvailable() {
        return available;
    }

    private static boolean initAvailable() {
        if (PlatformDependent.javaVersion() > 8) return false;
        try {
            Class.forName((String)"sun.security.ssl.ALPNExtension", (boolean)true, null);
            return true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    static JettyAlpnSslEngine newClientEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ClientEngine((SSLEngine)engine, (JdkApplicationProtocolNegotiator)applicationNegotiator);
    }

    static JettyAlpnSslEngine newServerEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ServerEngine((SSLEngine)engine, (JdkApplicationProtocolNegotiator)applicationNegotiator);
    }

    private JettyAlpnSslEngine(SSLEngine engine) {
        super((SSLEngine)engine);
    }
}

