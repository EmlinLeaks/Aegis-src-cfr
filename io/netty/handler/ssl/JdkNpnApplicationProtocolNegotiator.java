/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkBaseApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkNpnApplicationProtocolNegotiator;
import java.util.List;

@Deprecated
public final class JdkNpnApplicationProtocolNegotiator
extends JdkBaseApplicationProtocolNegotiator {
    private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory NPN_WRAPPER = new JdkApplicationProtocolNegotiator.SslEngineWrapperFactory(){
        {
            if (io.netty.handler.ssl.JettyNpnSslEngine.isAvailable()) return;
            throw new java.lang.RuntimeException((String)"NPN unsupported. Is your classpath configured correctly? See https://wiki.eclipse.org/Jetty/Feature/NPN");
        }

        public javax.net.ssl.SSLEngine wrapSslEngine(javax.net.ssl.SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
            return new io.netty.handler.ssl.JettyNpnSslEngine((javax.net.ssl.SSLEngine)engine, (JdkApplicationProtocolNegotiator)applicationNegotiator, (boolean)isServer);
        }
    };

    public JdkNpnApplicationProtocolNegotiator(Iterable<String> protocols) {
        this((boolean)false, protocols);
    }

    public JdkNpnApplicationProtocolNegotiator(String ... protocols) {
        this((boolean)false, (String[])protocols);
    }

    public JdkNpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, Iterable<String> protocols) {
        this((boolean)failIfNoCommonProtocols, (boolean)failIfNoCommonProtocols, protocols);
    }

    public JdkNpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, String ... protocols) {
        this((boolean)failIfNoCommonProtocols, (boolean)failIfNoCommonProtocols, (String[])protocols);
    }

    public JdkNpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, Iterable<String> protocols) {
        this((JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)(clientFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY), (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)(serverFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY), protocols);
    }

    public JdkNpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, String ... protocols) {
        this((JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)(clientFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY), (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)(serverFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY), (String[])protocols);
    }

    public JdkNpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, Iterable<String> protocols) {
        super((JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)NPN_WRAPPER, (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)selectorFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)listenerFactory, protocols);
    }

    public JdkNpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, String ... protocols) {
        super((JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)NPN_WRAPPER, (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)selectorFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)listenerFactory, (String[])protocols);
    }
}

