/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.Conscrypt;
import io.netty.handler.ssl.Java9SslUtils;
import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkBaseApplicationProtocolNegotiator;
import io.netty.handler.ssl.JettyAlpnSslEngine;
import io.netty.util.internal.PlatformDependent;
import java.util.List;

@Deprecated
public final class JdkAlpnApplicationProtocolNegotiator
extends JdkBaseApplicationProtocolNegotiator {
    private static final boolean AVAILABLE = Conscrypt.isAvailable() || JdkAlpnApplicationProtocolNegotiator.jdkAlpnSupported() || JettyAlpnSslEngine.isAvailable();
    private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory ALPN_WRAPPER = AVAILABLE ? new AlpnWrapper(null) : new FailureWrapper(null);

    public JdkAlpnApplicationProtocolNegotiator(Iterable<String> protocols) {
        this((boolean)false, protocols);
    }

    public JdkAlpnApplicationProtocolNegotiator(String ... protocols) {
        this((boolean)false, (String[])protocols);
    }

    public JdkAlpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, Iterable<String> protocols) {
        this((boolean)failIfNoCommonProtocols, (boolean)failIfNoCommonProtocols, protocols);
    }

    public JdkAlpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, String ... protocols) {
        this((boolean)failIfNoCommonProtocols, (boolean)failIfNoCommonProtocols, (String[])protocols);
    }

    public JdkAlpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, Iterable<String> protocols) {
        this((JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)(serverFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY), (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)(clientFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY), protocols);
    }

    public JdkAlpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, String ... protocols) {
        this((JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)(serverFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY), (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)(clientFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY), (String[])protocols);
    }

    public JdkAlpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, Iterable<String> protocols) {
        super((JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)ALPN_WRAPPER, (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)selectorFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)listenerFactory, protocols);
    }

    public JdkAlpnApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, String ... protocols) {
        super((JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)ALPN_WRAPPER, (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)selectorFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)listenerFactory, (String[])protocols);
    }

    static boolean jdkAlpnSupported() {
        if (PlatformDependent.javaVersion() < 9) return false;
        if (!Java9SslUtils.supportsAlpn()) return false;
        return true;
    }

    static boolean isAlpnSupported() {
        return AVAILABLE;
    }
}

