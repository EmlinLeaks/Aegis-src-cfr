/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolUtil;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkBaseApplicationProtocolNegotiator;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;

class JdkBaseApplicationProtocolNegotiator
implements JdkApplicationProtocolNegotiator {
    private final List<String> protocols;
    private final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory;
    private final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory;
    private final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory;
    static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory(){

        public io.netty.handler.ssl.JdkApplicationProtocolNegotiator$ProtocolSelector newSelector(javax.net.ssl.SSLEngine engine, java.util.Set<String> supportedProtocols) {
            return new io.netty.handler.ssl.JdkBaseApplicationProtocolNegotiator$FailProtocolSelector((io.netty.handler.ssl.JdkSslEngine)((io.netty.handler.ssl.JdkSslEngine)engine), supportedProtocols);
        }
    };
    static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory NO_FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory(){

        public io.netty.handler.ssl.JdkApplicationProtocolNegotiator$ProtocolSelector newSelector(javax.net.ssl.SSLEngine engine, java.util.Set<String> supportedProtocols) {
            return new io.netty.handler.ssl.JdkBaseApplicationProtocolNegotiator$NoFailProtocolSelector((io.netty.handler.ssl.JdkSslEngine)((io.netty.handler.ssl.JdkSslEngine)engine), supportedProtocols);
        }
    };
    static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory(){

        public io.netty.handler.ssl.JdkApplicationProtocolNegotiator$ProtocolSelectionListener newListener(javax.net.ssl.SSLEngine engine, List<String> supportedProtocols) {
            return new io.netty.handler.ssl.JdkBaseApplicationProtocolNegotiator$FailProtocolSelectionListener((io.netty.handler.ssl.JdkSslEngine)((io.netty.handler.ssl.JdkSslEngine)engine), supportedProtocols);
        }
    };
    static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory NO_FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory(){

        public io.netty.handler.ssl.JdkApplicationProtocolNegotiator$ProtocolSelectionListener newListener(javax.net.ssl.SSLEngine engine, List<String> supportedProtocols) {
            return new io.netty.handler.ssl.JdkBaseApplicationProtocolNegotiator$NoFailProtocolSelectionListener((io.netty.handler.ssl.JdkSslEngine)((io.netty.handler.ssl.JdkSslEngine)engine), supportedProtocols);
        }
    };

    JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, Iterable<String> protocols) {
        this((JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)wrapperFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)selectorFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)listenerFactory, ApplicationProtocolUtil.toList(protocols));
    }

    JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, String ... protocols) {
        this((JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)wrapperFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectorFactory)selectorFactory, (JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory)listenerFactory, ApplicationProtocolUtil.toList((String[])protocols));
    }

    private JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, List<String> protocols) {
        this.wrapperFactory = ObjectUtil.checkNotNull(wrapperFactory, (String)"wrapperFactory");
        this.selectorFactory = ObjectUtil.checkNotNull(selectorFactory, (String)"selectorFactory");
        this.listenerFactory = ObjectUtil.checkNotNull(listenerFactory, (String)"listenerFactory");
        this.protocols = Collections.unmodifiableList(ObjectUtil.checkNotNull(protocols, (String)"protocols"));
    }

    @Override
    public List<String> protocols() {
        return this.protocols;
    }

    @Override
    public JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory() {
        return this.selectorFactory;
    }

    @Override
    public JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory() {
        return this.listenerFactory;
    }

    @Override
    public JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory() {
        return this.wrapperFactory;
    }
}

